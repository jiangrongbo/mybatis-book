package com.blog4java.jdbc;

import com.blog4java.common.DbUtils;
import com.blog4java.common.IOUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

/**
 * 获取数据库基本信息
 */
public class Example09 {

    @Before
    public void initData() throws  Exception {
        // 初始化数据
        Class.forName("org.hsqldb.jdbcDriver");
        // 获取Connection对象
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
                "sa", "");
        // 使用Mybatis的ScriptRunner工具类执行数据库脚本
        ScriptRunner scriptRunner = new ScriptRunner(conn);
        // 不输出sql日志
        scriptRunner.setLogWriter(null);
        scriptRunner.runScript(Resources.getResourceAsReader("create-table.sql"));
        System.out.println("------------------------");
    }

    @Test
    public void testSavePoint() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            // 获取Connection对象
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
                    "sa", "");
            String sql1 = "insert into user(create_time, name, password, phone, nick_name) " +
                    "values('2010-10-24 10:20:30','User1','test','18700001111','User1')";
            String sql2 = "insert into user(create_time, name, password, phone, nick_name) " +
                    "values('2010-10-24 10:20:30','User2','test','18700001111','User2')";
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql1);
            // 创建保存点
            Savepoint savepoint = conn.setSavepoint("SP1");
            stmt.executeUpdate(sql2);
            // 回滚到保存点
            conn.rollback(savepoint);
            conn.commit();
            ResultSet rs  = conn.createStatement().executeQuery("select * from user ");
            DbUtils.dumpRS(rs);
            IOUtils.closeQuietly(stmt);
            IOUtils.closeQuietly(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
