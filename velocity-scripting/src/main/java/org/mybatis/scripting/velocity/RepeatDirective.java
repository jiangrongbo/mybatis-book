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
import org.apache.velocity.context.ChainedInternalContextAdapter;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.Scope;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserTreeConstants;
import org.apache.velocity.util.introspection.Info;

/**
 * #repeat($collection $item SEP OPEN CLOSE).
 */
public class RepeatDirective extends Directive {

  protected static final int MAX_IN_CLAUSE_SIZE = 1000;

  /** Immutable fields */
  private String var;
  private String open = "";
  private String close = "";
  private String separator = "";
  protected Info uberInfo;

  @Override
  public String getName() {
    return "repeat";
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
        switch (i) {
          case 2:
            this.separator = value;
            break;
          case 3:
            this.open = value;
            break;
          case 4:
            this.close = value;
            break;
          default:
            break;
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

    Iterator<?> i = null;

    try {
      i = this.rsvc.getUberspect().getIterator(listObject, this.uberInfo);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception ee) {
      String msg = "Error getting iterator for #repeat at " + this.uberInfo;
      this.rsvc.getLog().error(msg, ee);
      throw new VelocityException(msg, ee);
    }

    if (i == null) {
      throw new VelocityException("Invalid collection");
    }

    int counter = 0;
    boolean maxNbrLoopsExceeded = false;
    Object o = context.get(this.var);

    ParameterMappingCollector collector = (ParameterMappingCollector) context
        .get(SQLScriptSource.MAPPING_COLLECTOR_KEY);
    String savedItemKey = collector.getItemKey();
    collector.setItemKey(this.var);
    RepeatScope foreach = new RepeatScope(this, context.get(getName()), this.var);
    context.put(getName(), foreach);

    NullHolderContext nullHolderContext = null;
    StringWriter buffer = new StringWriter();
    while (!maxNbrLoopsExceeded && i.hasNext()) {
      Object value = i.next();
      put(context, this.var, value);
      foreach.index++;
      foreach.hasNext = i.hasNext();

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
      maxNbrLoopsExceeded = counter >= MAX_IN_CLAUSE_SIZE;

      if (i.hasNext() && !maxNbrLoopsExceeded) {
        buffer.append(this.separator);
      }

    }
    String content = buffer.toString().trim();
    if (!"".equals(content)) {
      writer.append(this.open);
      writer.append(content);
      writer.append(this.close);
    }
    clean(context, o, collector, savedItemKey);
    return true;

  }

  protected void clean(InternalContextAdapter context, Object o, ParameterMappingCollector collector,
      String savedItemKey) {
    if (o != null) {
      context.put(this.var, o);
    } else {
      context.remove(this.var);
    }
    collector.setItemKey(savedItemKey);
    postRender(context);
  }

  protected void put(InternalContextAdapter context, String key, Object value) {
    context.put(key, value);
  }

  @Override
  public int getType() {
    return BLOCK;
  }

  public static class RepeatScope extends Scope {

    protected int index = -1;
    protected boolean hasNext = false;
    protected final String var;

    public RepeatScope(Object newOwner, Object replaces, String newVar) {
      super(newOwner, replaces);
      this.var = newVar;
    }

    public int getIndex() {
      return this.index;
    }

    public int getCount() {
      return this.index + 1;
    }

    public boolean hasNext() {
      return getHasNext();
    }

    public boolean getHasNext() {
      return this.hasNext;
    }

    public boolean isFirst() {
      return this.index < 1;
    }

    public boolean getFirst() {
      return isFirst();
    }

    public boolean isLast() {
      return !this.hasNext;
    }

    public boolean getLast() {
      return isLast();
    }

    public String getVar() {
      return this.var;
    }

  }

  protected static class NullHolderContext extends ChainedInternalContextAdapter {

    private String loopVariableKey = "";
    private boolean active = true;

    protected NullHolderContext(String key, InternalContextAdapter context) {
      super(context);
      if (key != null) {
        this.loopVariableKey = key;
      }
    }

    @Override
    public Object get(String key) {
      return (this.active && this.loopVariableKey.equals(key)) ? null : super.get(key);
    }

    @Override
    public Object put(String key, Object value) {
      if (this.loopVariableKey.equals(key) && (value == null)) {
        this.active = true;
      }

      return super.put(key, value);
    }

    @Override
    public Object remove(String key) {
      if (this.loopVariableKey.equals(key)) {
        this.active = false;
      }
      return super.remove(key);
    }
  }

  @Override
  public boolean isScopeProvided() {
    return true;
  }

}
