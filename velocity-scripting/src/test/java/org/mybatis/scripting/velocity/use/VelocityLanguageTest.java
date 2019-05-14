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
package org.mybatis.scripting.velocity.use;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Just a test case. Not a real Velocity implementation.
 */
public class VelocityLanguageTest {

  protected static SqlSessionFactory sqlSessionFactory;

  public enum IDS {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE
  }

  @BeforeAll
  public static void setUp() throws Exception {
    Connection conn = null;

    try {
      Class.forName("org.hsqldb.jdbcDriver");
      conn = DriverManager.getConnection("jdbc:hsqldb:mem:bname", "sa", "");

      Reader reader = Resources.getResourceAsReader("org/mybatis/scripting/velocity/use/CreateDB.sql");

      ScriptRunner runner = new ScriptRunner(conn);
      runner.setLogWriter(null);
      runner.setErrorLogWriter(null);
      runner.runScript(reader);
      conn.commit();
      reader.close();

      reader = Resources.getResourceAsReader("org/mybatis/scripting/velocity/use/MapperConfig.xml");
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      reader.close();
    } finally {
      if (conn != null) {
        conn.close();
      }
    }
  }

  @Test
  public void testDynamicSelectWithPropertyParams() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      Parameter p = new Parameter(true, "Fli%");
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNames", p);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertEquals("Flintstone", n.getLastName());
      }

      p = new Parameter(false, "Fli%");
      answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNames", p);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertTrue(n.getLastName() == null);
      }

      p = new Parameter(false, "Rub%");
      answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNames", p);
      assertEquals(2, answer.size());
      for (Name n : answer) {
        assertTrue(n.getLastName() == null);
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithExpressionParams() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      Parameter p = new Parameter(true, "Fli");
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithExpressions", p);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertEquals("Flintstone", n.getLastName());
      }

      p = new Parameter(false, "Fli");
      answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithExpressions", p);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertTrue(n.getLastName() == null);
      }

      p = new Parameter(false, "Rub");
      answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithExpressions", p);
      assertEquals(2, answer.size());
      for (Name n : answer) {
        assertTrue(n.getLastName() == null);
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testSelectNamesWithFormattedParam() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      Parameter p = new Parameter(true, "Fli");
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithFormattedParam", p);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertEquals("Flintstone", n.getLastName());
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testEnumBinding() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectEnumBinding");
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertEquals("Flintstone", n.getLastName());
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testSelectNamesWithFormattedParamSafe() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      Parameter p = new Parameter(true, "Fli");
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithFormattedParamSafe",
          p);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertEquals("Flintstone", n.getLastName());
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithIteration() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      int[] ids = { 2, 4, 5 };
      Map<String, int[]> param = new HashMap<>();
      param.put("ids", ids);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithIteration", param);
      assertEquals(3, answer.size());
      for (int i = 0; i < ids.length; i++) {
        assertEquals(ids[i], answer.get(i).getId());
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testEmptyWhere() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      int[] ids = {};
      Map<String, int[]> param = new HashMap<>();
      param.put("ids", ids);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithIteration", param);
      assertEquals(5, answer.size());

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testTrim() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      int[] ids = {};
      Map<String, int[]> param = new HashMap<>();
      param.put("ids", ids);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectWithTrim", param);
      assertEquals(5, answer.size());

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithIterationOverMap() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      Map<Integer, String> ids = new HashMap<>();
      ids.put(2, "Wilma");
      ids.put(4, "Barney");
      ids.put(5, "Betty");
      Map<String, Map<Integer, String>> param = new HashMap<>();
      param.put("ids", ids);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithIterationOverMap",
          param);
      assertEquals(3, answer.size());
      for (Name n : answer) {
        assertEquals(ids.get(n.getId()).toString(), n.getFirstName());
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithIterationComplex() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      Name[] names = { new Name(2), new Name(4), new Name(5) };
      Map<String, Name[]> param = new HashMap<>();
      param.put("names", names);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithIterationComplex",
          param);
      assertEquals(3, answer.size());
      for (int i = 0; i < names.length; i++) {
        assertEquals(names[i].getId(), answer.get(i).getId());
      }

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithIterationBoundary() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      List<Name> names = new ArrayList<>();
      for (int i = 0; i < 1001; i++) {
        names.add(new Name(i));
      }

      Map<String, List<Name>> param = new HashMap<>();
      param.put("names", names);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithIterationComplex",
          param);
      assertEquals(5, answer.size());
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testSelectKey() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {

      Name fred = new Name();
      fred.setFirstName("Fred");
      fred.setLastName("Flinstone");
      sqlSession.insert("org.mybatis.scripting.velocity.use.insertName", fred);
      assertTrue(fred.getId() != 0);

    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testSelectWithCustomUserDirective() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      Map<String, List<Name>> param = new HashMap<>();
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectWithCustomUserDirective",
          param);
      assertEquals(5, answer.size());
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithInDirectiveForOneThousandPlusOne() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      List<Name> names = new ArrayList<>();
      for (int i = 0; i < 1001; i++) {
        names.add(new Name(i + 1));
      }

      Map<String, List<Name>> param = new HashMap<>();
      param.put("names", names);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithInDirective", param);
      assertEquals(5, answer.size());
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithInDirectiveForOneThousand() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      List<Name> names = new ArrayList<>();
      for (int i = 0; i < 1000; i++) {
        names.add(new Name(i + 1));
      }

      Map<String, List<Name>> param = new HashMap<>();
      param.put("names", names);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithInDirective", param);
      assertEquals(5, answer.size());
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithInDirectiveForOneThousandMinusOne() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      List<Name> names = new ArrayList<>();
      for (int i = 0; i < 999; i++) {
        names.add(new Name(i + 1));
      }

      Map<String, List<Name>> param = new HashMap<>();
      param.put("names", names);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithInDirective", param);
      assertEquals(5, answer.size());
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void testDynamicSelectWithInDirectiveForOneItem() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      List<Name> names = new ArrayList<>();
      for (int i = 0; i < 1; i++) {
        names.add(new Name(i + 1));
      }

      Map<String, List<Name>> param = new HashMap<>();
      param.put("names", names);
      List<Name> answer = sqlSession.selectList("org.mybatis.scripting.velocity.use.selectNamesWithInDirective", param);
      assertEquals(1, answer.size());
    } finally {
      sqlSession.close();
    }
  }

}
