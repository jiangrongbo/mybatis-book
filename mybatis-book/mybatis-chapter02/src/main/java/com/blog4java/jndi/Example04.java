package com.blog4java.jndi;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class Example04 {
    @Before
    public void before() throws IOException {
        DataSourceFactory dsf = new UnpooledDataSourceFactory();
        Properties properties = new Properties();
        InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("database.properties");
        properties.load(configStream);
        dsf.setProperties(properties);
        DataSource dataSource = dsf.getDataSource();
        try {
            Properties jndiProps = new Properties();
            jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
            jndiProps.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
            Context ctx = new InitialContext(jndiProps);
            ctx.bind("java:TestDC", dataSource);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJndi() {
        try {
            Properties jndiProps = new Properties();
            jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
            jndiProps.put(Context.URL_PKG_PREFIXES, "org.apache.naming");
            Context ctx = new InitialContext(jndiProps);
            DataSource dataSource = (DataSource) ctx.lookup("java:TestDC");
            Connection conn = dataSource.getConnection();
            Assert.assertNotNull(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
