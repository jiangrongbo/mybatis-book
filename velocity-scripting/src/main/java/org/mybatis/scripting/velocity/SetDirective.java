/**
 *    Copyright 2012-2017 the original author or authors.
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
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.Node;

public class SetDirective extends TrimDirective {

  @Override
  public String getName() {
    return "mset";
  }

  @Override
  protected Params getParams(InternalContextAdapter context, Node node) throws IOException {
    final Params params = new Params();
    params.setPrefix("SET");
    params.setSuffixOverrides(",");
    if (node.jjtGetNumChildren() == 1) {
      final Node child = node.jjtGetChild(0);
      if (child instanceof ASTBlock) {
        StringWriter blockContent = new StringWriter();
        child.render(context, blockContent);
        params.setBody(blockContent.toString().trim());
        return params;
      }
    }
    return null;
  }

}
