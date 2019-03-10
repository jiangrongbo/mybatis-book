package com.blog4java.mybatis;

import com.alibaba.fastjson.JSON;
import com.blog4java.common.IOUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SqlRunnerExample {

    Connection connection = null;

    @Before
    public void initTable() throws SQLException, IOException {
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
                "sa", "");
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setLogWriter(null);
        scriptRunner.runScript(Resources.getResourceAsReader("create-table.sql"));
        scriptRunner.runScript(Resources.getResourceAsReader("init-data.sql"));
    }

    @Test
    public void testSelectOne() throws SQLException {
        SqlRunner sqlRunner = new SqlRunner(connection);
        String qryUserSql = new SQL() {{
            SELECT("*");
            FROM("user");
            WHERE("id = ?");
        }}.toString();
        Map<String, Object> resultMap = sqlRunner.selectOne(qryUserSql, Integer.valueOf(1));
        System.out.println(JSON.toJSONString(resultMap));
    }

    @Test
    public void testDelete() throws SQLException {
        SqlRunner sqlRunner = new SqlRunner(connection);
        String deleteUserSql = new SQL(){{
            DELETE_FROM("user");
            WHERE("id = ?");
        }}.toString();
        sqlRunner.delete(deleteUserSql, Integer.valueOf(1));
    }

    @Test
    public void testUpdate() throws SQLException {
        SqlRunner sqlRunner = new SqlRunner(connection);
        String updateUserSql = new SQL(){{
            UPDATE("user");
            SET("nick_name = ?");
            WHERE("id = ?");
        }}.toString();
        sqlRunner.update(updateUserSql, "Jane", Integer.valueOf(1));
    }

    @Test
    public void testInsert() throws SQLException {
        SqlRunner sqlRunner = new SqlRunner(connection);
        String insertUserSql = new SQL(){{
            INSERT_INTO("user");
            INTO_COLUMNS("create_time,name,password,phone,nick_name");
            INTO_VALUES("?,?,?,?,?");
        }}.toString();
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        sqlRunner.insert(insertUserSql,createTime,"Jane","test","18700000000","J");
    }

    @After
    public void closeConnection() {
        IOUtils.closeQuietly(connection);
    }


}
