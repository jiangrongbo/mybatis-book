package com.blog4java.jdbc;

import com.blog4java.common.IOUtils;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 使用Statement对象批量执行SQL语句
 */
public class Example05 {
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
    }

    @Test
    public void testJdbcBatch() {
        try {
            // 创建DataSource实例
            DataSourceFactory dsf = new UnpooledDataSourceFactory();
            Properties properties = new Properties();
            InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("database.properties");
            properties.load(configStream);
            dsf.setProperties(properties);
            DataSource dataSource = dsf.getDataSource();
            // 获取Connection对象
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.addBatch("insert into  " +
                    "user(create_time, name, password, phone, nick_name) " +
                    "values('2010-10-24 10:20:30', 'User1', 'test', '18700001111', 'User1');");
            statement.addBatch("insert into " +
                    "user (create_time, name, password, phone, nick_name) " +
                    "values('2010-10-24 10:20:30', 'User2', 'test', '18700002222', 'User2');");
            statement.executeBatch();
            statement.execute("select * from user");

            ResultSet result = statement.getResultSet();
            dumpRS(result);
            // 关闭连接
            IOUtils.closeQuietly(statement);
            IOUtils.closeQuietly(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpRS(ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columCount = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columCount; i++) {
                String columName = metaData.getColumnName(i);
                String columVal = resultSet.getString(columName);
                System.out.println(columName + ":" + columVal);
            }
            System.out.println("-------------------------------------");
        }
    }
}
