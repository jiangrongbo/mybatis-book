package com.blog4java.plugin.pager;

import com.blog4java.common.IOUtils;
import com.blog4java.plugin.utils.ReflectionUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Intercepts({
        @Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class})
})
public class PageInterceptor implements Interceptor {

    private String databaseType;

    public Object intercept(Invocation invocation) throws Throwable {
        // 获取拦截的目标对象
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate = (StatementHandler) ReflectionUtils.getFieldValue(handler, "delegate");
        BoundSql boundSql = delegate.getBoundSql();
        // 获取参数对象，当参数对象为Page的子类时执行分页操作
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof Page<?>) {
            Page<?> page = (Page<?>) parameterObject;
            MappedStatement mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate, "mappedStatement");
            Connection connection = (Connection) invocation.getArgs()[0];
            String sql = boundSql.getSql();
            if (page.isFull()) {
                // 获取记录总数
                this.setTotalCount(page, mappedStatement, connection);
            }
            page.setTimestamp(System.currentTimeMillis());
            // 获取分页SQL
            String pageSql = this.getPageSql(page, sql);
            // 将原始SQL语句替换成分页语句
            ReflectionUtils.setFieldValue(boundSql, "sql", pageSql);
        }
        return invocation.proceed();
    }

    /**
     * 拦截器对应的封装原始对象的方法
     */
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置注册拦截器时设定的属性
     */
    public void setProperties(Properties properties) {
        this.databaseType = properties.getProperty("databaseType");
    }

    /**
     * 根据page对象获取对应的分页查询Sql语句，
     * 这里只做了三种数据库类型，Mysql、Oracle、HSQLDB
     * 其它的数据库都没有进行分页
     *
     * @param page 分页对象
     * @param sql  原始sql语句
     * @return
     */
    private String getPageSql(Page<?> page, String sql) {
        StringBuffer sqlBuffer = new StringBuffer(sql);
        if ("mysql".equalsIgnoreCase(databaseType)) {
            return getMysqlPageSql(page, sqlBuffer);
        } else if ("oracle".equalsIgnoreCase(databaseType)) {
            return getOraclePageSql(page, sqlBuffer);
        } else if ("hsqldb".equalsIgnoreCase(databaseType)) {
            return getHSQLDBPageSql(page, sqlBuffer);
        }
        return sqlBuffer.toString();
    }

    /**
     * 获取Mysql数据库的分页查询语句
     *
     * @param page      分页对象
     * @param sqlBuffer 包含原sql语句的StringBuffer对象
     * @return Mysql数据库分页语句
     */
    private String getMysqlPageSql(Page<?> page, StringBuffer sqlBuffer) {
        int offset = (page.getPageNo() - 1) * page.getPageSize();
        sqlBuffer.append(" limit ").append(offset).append(",").append(page.getPageSize());
        return sqlBuffer.toString();
    }

    /**
     * 获取Oracle数据库的分页查询语句
     *
     * @param page      分页对象
     * @param sqlBuffer 包含原sql语句的StringBuffer对象
     * @return Oracle数据库的分页查询语句
     */
    private String getOraclePageSql(Page<?> page, StringBuffer sqlBuffer) {
        int offset = (page.getPageNo() - 1) * page.getPageSize() + 1;
        sqlBuffer.insert(0, "select u.*, rownum r from (").append(") u where rownum < ")
                .append(offset + page.getPageSize());
        sqlBuffer.insert(0, "select * from (").append(") where r >= ").append(offset);
        return sqlBuffer.toString();
    }


    /**
     * 获取HSQLDB数据库的分页查询语句
     *
     * @param page      分页对象
     * @param sqlBuffer 包含原sql语句的StringBuffer对象
     * @return Oracle数据库的分页查询语句
     */
    private String getHSQLDBPageSql(Page<?> page, StringBuffer sqlBuffer) {
        int offset = (page.getPageNo() - 1) * page.getPageSize() + 1;
        String sql = "select limit " + offset + " " + page.getPageSize() + " * from (" + sqlBuffer.toString() + " )";
        return sql;
    }

    /**
     * 给当前的参数对象page设置总记录数
     *
     * @param page            Mapper映射语句对应的参数对象
     * @param mappedStatement Mapper映射语句
     * @param connection      当前的数据库连接
     */
    private void setTotalCount(Page<?> page, MappedStatement mappedStatement, Connection connection) {
        BoundSql boundSql = mappedStatement.getBoundSql(page);
        String sql = boundSql.getSql();
        // 获取总记录数
        String countSql = this.getCountSql(sql);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, page);
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, page, countBoundSql);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(countSql);
            parameterHandler.setParameters(pstmt);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int totalCount = rs.getInt(1);
                page.setTotalCount(totalCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(rs);
            IOUtils.closeQuietly(pstmt);
        }
    }

    /**
     * 根据原Sql语句获取对应的查询总记录数的Sql语句
     *
     * @param sql
     * @return
     */
    private String getCountSql(String sql) {
        return "select count(1) " + sql.substring(sql.toLowerCase().indexOf("from"));
    }


}
