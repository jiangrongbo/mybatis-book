package com.blog4java.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

public class ScriptRunnerExample {

    @Test
    public void testScriptRunner() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
                    "sa", "");
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(Resources.getResourceAsReader("create-table.sql"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
