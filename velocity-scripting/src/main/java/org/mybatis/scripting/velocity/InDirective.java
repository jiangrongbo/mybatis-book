/**
 *    Copyright 2012-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.scripting.velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserTreeConstants;
import org.apache.velocity.util.introspection.Info;

/**
 * #in($collection $item COLUMN).
 *
 */
public class InDirective extends RepeatDirective {

  /**
   * Immutable fields
   */
  private String var;

  private String open = "(";

  private String close = ")";

  private String separator = ", ";

  private String column = "";

  @Override
  public String getName() {
    return "in";
  }

  @Override
  public void init(RuntimeServices rs, InternalContextAdapter context, Node node) {
    super.init(rs, context, node);
    final int n = node.jjtGetNumChildren() - 1;
    for (int i = 1; i < n; i++) {
      Node child = node.jjtGetChild(i);
      if (i == 1) {
        if (child.getType() == ParserTreeConstants.JJTREFERENCE) {
          this.var = ((ASTReference) child).getRootString();
        } else {
          throw new TemplateInitException("Syntax error", getTemplateName(), getLine(), getColumn());
        }
      } else if (child.getType() == ParserTreeConstants.JJTSTRINGLITERAL) {
        String value = (String) ((ASTStringLiteral) child).value(context);
        if (i == 2) {
          this.column = value;
        }
      } else {
        throw new TemplateInitException("Syntax error", getTemplateName(), getLine(), getColumn());
      }
    }
    this.uberInfo = new Info(this.getTemplateName(), getLine(), getColumn());
  }

  @Override
  public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException {
    Object listObject = node.jjtGetChild(0).value(context);
    if (listObject == null) {
      return false;
    }

    Iterator<?> iterator = null;

    try {
      iterator = this.rsvc.getUberspect().getIterator(listObject, this.uberInfo);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception ee) {
      String msg = "Error getting iterator for #in at " + this.uberInfo;
      this.rsvc.getLog().error(msg, ee);
      throw new VelocityException(msg, ee);
    }

    if (iterator == null) {
      throw new VelocityException("Invalid collection");
    }

    int counter = 0;
    Object o = context.get(this.var);

    ParameterMappingCollector collector = (ParameterMappingCollector) context
        .get(SQLScriptSource.MAPPING_COLLECTOR_KEY);
    String savedItemKey = collector.getItemKey();
    collector.setItemKey(this.var);
    RepeatScope foreach = new RepeatScope(this, context.get(getName()), this.var);
    context.put(getName(), foreach);

    NullHolderContext nullHolderContext = null;
    Object value = null;
    StringWriter buffer = new StringWriter();

    while (iterator.hasNext()) {

      if (counter % MAX_IN_CLAUSE_SIZE == 0) {
        buffer.append(this.open); // Group begins
        buffer.append(this.column);
        buffer.append(" IN ");
        buffer.append(this.open); // In starts
      }

      value = iterator.next();
      put(context, this.var, value);
      foreach.index++;
      foreach.hasNext = iterator.hasNext();

      try {
        if (value == null) {
          if (nullHolderContext == null) {
            nullHolderContext = new NullHolderContext(this.var, context);
          }
          node.jjtGetChild(node.jjtGetNumChildren() - 1).render(nullHolderContext, buffer);
        } else {
          node.jjtGetChild(node.jjtGetNumChildren() - 1).render(context, buffer);
        }
      } catch (StopCommand stop) {
        if (stop.isFor(this)) {
          break;
        }
        clean(context, o, collector, savedItemKey);
        // close does not perform any action and this is here 
        // to avoid eclipse reporting possible leak.
        buffer.close();
        throw stop;
      }
      counter++;

      if ((counter > 0 && counter % MAX_IN_CLAUSE_SIZE == 0) || !iterator.hasNext()) {
        buffer.append(this.close); // In ends
        buffer.append(this.close); // Group ends
        if (iterator.hasNext()) {
          buffer.append(" OR ");
        }
      } else if (iterator.hasNext()) {
        buffer.append(this.separator);
      }

    }
    String content = buffer.toString().trim();
    if (!"".equals(content)) {
      writer.append(this.open);
      writer.append(content);
      writer.append(this.close);
    } else {
      writer.append(this.open);
      writer.append(this.open);
      writer.append(this.column);
      writer.append(" NOT IN ");
      writer.append(this.open);
      writer.append(" NULL ");
      writer.append(this.close);
      writer.append(this.close);
      writer.append(this.close);
    }
    clean(context, o, collector, savedItemKey);
    return true;
  }

  @Override
  public int getType() {
    return BLOCK;
  }

}
