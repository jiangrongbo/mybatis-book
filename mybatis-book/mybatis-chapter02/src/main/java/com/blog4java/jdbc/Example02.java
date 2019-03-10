package com.blog4java.jdbc;

import com.blog4java.common.DbUtils;
import com.blog4java.common.IOUtils;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Example02 {
    @Test
    public void testJdbc() {
        // 初始化数据
        DbUtils.initData();
        try {
            // 创建DataSource实例
            DataSource dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver",
                    "jdbc:hsqldb:mem:mybatis", "sa", "");
            // 获取Connection对象
            Connection connection = dataSource.getConnection();
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
                System.out.println("---------------------------------------");
            }
            // 关闭连接
            IOUtils.closeQuietly(statement);
            IOUtils.closeQuietly(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
