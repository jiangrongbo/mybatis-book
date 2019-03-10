package com.blog4java.jdbc;

import com.blog4java.common.IOUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 获取数据库自增主键
 */
public class Example07 {
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
        System.out.println("-----------------------");
    }

    @Test
    public void testJdbc() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            // 获取Connection对象
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
                    "sa", "");
            Statement stmt = conn.createStatement();
            String sql = "insert into user(create_time, name, password, phone, nick_name) " +
                    "values('2010-10-24 10:20:30','User1','test','18700001111','User1');";
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet genKeys = stmt.getGeneratedKeys();
            if(genKeys.next()) {
                System.out.println("自增长主键：" + genKeys.getInt(1));
            }
            IOUtils.closeQuietly(stmt);
            IOUtils.closeQuietly(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
