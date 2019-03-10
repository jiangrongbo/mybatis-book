package com.blog4java.jdbc;

import com.blog4java.common.DbUtils;
import com.blog4java.common.IOUtils;
import org.junit.Test;

import java.sql.*;

public class Example01 {
    @Test
    public void testJdbc() {
        // 初始化数据
        DbUtils.initData();
        try {
            // 加载驱动
            Class.forName("org.hsqldb.jdbcDriver");
            // 获取Connection对象
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
                    "sa", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from user");
            // 遍历ResultSet
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columCount = metaData.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columCount; i++) {
                    String columName = metaData.getColumnName(i);
                    String columVal = resultSet.getString(columName);
                    System.out.println(columName + ":" + columVal);
                }
                System.out.println("--------------------------------------");
            }
            // 关闭连接
            IOUtils.closeQuietly(statement);
            IOUtils.closeQuietly(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
