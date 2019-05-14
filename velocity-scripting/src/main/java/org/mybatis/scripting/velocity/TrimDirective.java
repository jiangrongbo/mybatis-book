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
import java.util.Locale;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.Node;

public class TrimDirective extends Directive {

  @Override
  public String getName() {
    return "trim";
  }

  @Override
  public final int getType() {
    return BLOCK;
  }

  @Override
  public final boolean render(InternalContextAdapter ica, Writer writer, Node node) throws IOException {
    Params p = getParams(ica, node);
    if (p == null) {
      return false;
    }
    return render(p, writer);
  }

  public boolean render(final Params params, final Writer writer) throws IOException {
    int leftIndex = 0;
    int rightIndex = params.maxBody;
    if (rightIndex == 0) {
      return false;
    }
    if (!params.prefixOverrides.isEmpty()) {
      final String LEFT = params.body
          .substring(0, params.maxPrefixLength < params.maxBody ? params.maxPrefixLength : params.maxBody)
          .toUpperCase(Locale.ENGLISH);
      FastLinkedList<String>.Node n = params.prefixOverrides.start();
      while (n != null) {
        if (LEFT.startsWith(n.data)) {
          leftIndex = n.data.length();
          break;
        }
        n = n.next;
      }
    }
    if (!params.suffixOverrides.isEmpty()) {
      final String RIGHT = params.body
          .substring(rightIndex > params.maxSuffixLength ? rightIndex - params.maxSuffixLength : 0)
          .toUpperCase(Locale.ENGLISH);
      FastLinkedList<String>.Node n = params.suffixOverrides.start();
      while (n != null) {
        if (RIGHT.endsWith(n.data)) {
          rightIndex = rightIndex - n.data.length();
          break;
        }
        n = n.next;
      }
    }
    if (rightIndex > leftIndex) {
      String content = params.body.substring(leftIndex, rightIndex).trim();
      if (!"".equals(content)) {
        writer.append(params.prefix).append(' ');
        writer.append(params.body, leftIndex, rightIndex).append(' ');
        writer.append(params.suffix);
      }
    }
    return true;
  }

  protected static final class Params {

    String prefix = "";

    String suffix = "";

    FastLinkedList<String> prefixOverrides = new FastLinkedList<>();

    FastLinkedList<String> suffixOverrides = new FastLinkedList<>();

    String body = "";

    int maxPrefixLength = 0;

    int maxSuffixLength = 0;

    int maxBody = 0;

    public String getBody() {
      return this.body;
    }

    public void setBody(String value) {
      if (value == null) {
        this.body = "";
      } else {
        this.body = value.trim();
      }
      this.maxBody = this.body.length();
    }

    public String getPrefix() {
      return this.prefix;
    }

    public void setPrefix(String value) {
      this.prefix = value;
    }

    public FastLinkedList<String> getPrefixOverrides() {
      return this.prefixOverrides;
    }

    public void setPrefixOverrides(String list) {
      this.maxPrefixLength = fromStringList(list, '|', this.prefixOverrides);
    }

    public FastLinkedList<String> getSuffixOverrides() {
      return this.suffixOverrides;
    }

    public void setSuffixOverrides(String list) {
      this.maxSuffixLength = fromStringList(list, '|', this.suffixOverrides);
    }

    public String getSuffix() {
      return this.suffix;
    }

    public void setSuffix(String value) {
      this.suffix = value;
    }

  }

  protected Params getParams(final InternalContextAdapter context, final Node node) throws IOException {
    final Params params = new Params();
    final int nodes = node.jjtGetNumChildren();
    for (int i = 0; i < nodes; i++) {
      Node child = node.jjtGetChild(i);
      if (child != null) {
        if (!(child instanceof ASTBlock)) {
          if (i == 0) {
            params.setPrefix(String.valueOf(child.value(context)));
          } else if (i == 1) {
            params.setPrefixOverrides(String.valueOf(child.value(context)).toUpperCase(Locale.ENGLISH));
          } else if (i == 2) {
            params.setSuffix(String.valueOf(child.value(context)));
          } else if (i == 3) {
            params.setSuffixOverrides(String.valueOf(child.value(context)).toUpperCase(Locale.ENGLISH));
          } else {
            break;
          }
        } else {
          StringWriter blockContent = new StringWriter();
          child.render(context, blockContent);
          params.setBody(blockContent.toString().trim());
          break;
        }
      }
    }
    return params;
  }

  static int fromStringList(final String list, final char sep, final FastLinkedList<String> fll) {
    int max = 0;
    if (list != null) {
      final int n = list.length();
      int i = 0;
      while (i < n) {
        int r = list.indexOf(sep, i);
        if (i < r) {
          fll.add(list.substring(i, r));
          int len = r - i;
          if (len > max) {
            max = len;
          }
          i = r + 1;
        } else {
          break;
        }
      }
      if (i < n) {
        fll.add(list.substring(i));
        int len = n - i;
        if (len > max) {
          max = len;
        }
      }
    }
    return max;
  }

}
