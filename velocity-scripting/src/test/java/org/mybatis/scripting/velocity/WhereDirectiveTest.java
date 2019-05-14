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

import java.io.StringWriter;
import java.util.Properties;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class WhereDirectiveTest {

  static VelocityContext c;
  static VelocityEngine velocity;

  @BeforeAll
  public static void setUpClass() throws Exception {
    Properties p = new Properties();
    p.setProperty("userdirective", WhereDirective.class.getName());
    velocity = new VelocityEngine();
    velocity.setProperty("runtime.log", "target/velocity.log");
    velocity.init(p);
    c = new VelocityContext();
    StringWriter w = new StringWriter();
    velocity.evaluate(c, w, "WARM", "1+1");
  }

  @Test
  public void simpleTest1() throws Exception {
    StringWriter w = new StringWriter();
    velocity.evaluate(c, w, "TEST", "#where()XXXX#end");
    String result = w.toString();
    assertEquals("WHERE XXXX ", result);
  }

  @Test
  public void simpleTest2() throws Exception {
    StringWriter w = new StringWriter();
    velocity.evaluate(c, w, "TEST", "#where() a and b #end");
    String result = w.toString();
    assertEquals("WHERE a and b ", result);
  }

  @Test
  public void simpleTest3() throws Exception {
    StringWriter w = new StringWriter();
    velocity.evaluate(c, w, "TEST", "#where() and a and b#end");
    String result = w.toString();
    assertEquals("WHERE a and b ", result);
  }

  @Test
  public void simpleTest4() throws Exception {
    StringWriter w = new StringWriter();
    velocity.evaluate(c, w, "TEST", "#where() or a and b#end");
    String result = w.toString();
    assertEquals("WHERE a and b ", result);
  }

}
