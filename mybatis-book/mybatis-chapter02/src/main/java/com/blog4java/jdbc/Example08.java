package com.blog4java.jdbc;

import com.blog4java.common.IOUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

/**
 * 获取数据库基本信息
 */
public class Example08 {

    @Test
    public void testDbMetaData() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            // 获取Connection对象
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
                    "sa", "");
            DatabaseMetaData dmd = conn.getMetaData();
            System.out.println("数据库URL:" + dmd.getURL());
            System.out.println("数据库用户名:" + dmd.getUserName());
            System.out.println("数据库产品名:" + dmd.getDatabaseProductName());
            System.out.println("数据库产品版本:" + dmd.getDatabaseProductVersion());
            System.out.println("驱动主版本:" + dmd.getDriverMajorVersion());
            System.out.println("驱动副版本:" + dmd.getDriverMinorVersion());
            System.out.println("数据库供应商用于schema的首选术语:" + dmd.getSchemaTerm());
            System.out.println("数据库供应商用于catalog的首选术语:" + dmd.getCatalogTerm());
            System.out.println("数据库供应商用于procedure的首选术语:" + dmd.getProcedureTerm());
            System.out.println("null值是否高排序:" + dmd.nullsAreSortedHigh());
            System.out.println("null值是否低排序:" + dmd.nullsAreSortedLow());
            System.out.println("数据库是否将表存储在本地文件中:" + dmd.usesLocalFiles());
            System.out.println("数据库是否为每个表使用一个文件:" + dmd.usesLocalFilePerTable());
            System.out.println("数据库SQL关键字:" + dmd.getSQLKeywords());
            IOUtils.closeQuietly(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
