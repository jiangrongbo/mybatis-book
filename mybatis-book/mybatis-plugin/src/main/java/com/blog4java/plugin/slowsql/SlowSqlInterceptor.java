package com.blog4java.plugin.slowsql;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SlowSqlInterceptor implements Interceptor {

    private Integer limitSecond;

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        long beginTimeMillis = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        try {
            return invocation.proceed();
        } finally {
            long endTimeMillis = System.currentTimeMillis();
            long costTimeMillis = endTimeMillis - beginTimeMillis;
            if (costTimeMillis > limitSecond * 1000) {
                BoundSql boundSql = statementHandler.getBoundSql();
                // 调用getFormatedSql（）方法对参数占位符进行替换
                String sql = getFormatedSql(boundSql);
                System.out.println("SQL语句【" + sql + "】，执行耗时：" + costTimeMillis + "ms");
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String limitSecond = (String) properties.get("limitSecond");
        this.limitSecond = Integer.parseInt(limitSecond);
    }

    private String getFormatedSql(BoundSql boundSql) {
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        sql = beautifySql(sql);
        if (parameterObject == null || parameterMappings == null || parameterMappings.isEmpty()) {
            return sql;
        }
        String sqlWithoutReplacePlaceholder = sql;
        try {
            if (parameterMappings != null) {
                Class<?> parameterObjectClass = parameterObject.getClass();
                if (isStrictMap(parameterObjectClass)) {
                    DefaultSqlSession.StrictMap<Collection<?>> strictMap = (DefaultSqlSession.StrictMap<Collection<?>>) parameterObject;
                    if (isList(strictMap.get("list").getClass())) {
                        sql = handleListParameter(sql, strictMap.get("list"));
                    }
                } else if (isMap(parameterObjectClass)) {
                    Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
                    sql = handleMapParameter(sql, paramMap, parameterMappings);
                } else {
                    sql = handleCommonParameter(sql, parameterMappings, parameterObjectClass, parameterObject);
                }
            }
        } catch (Exception e) {
            return sqlWithoutReplacePlaceholder;
        }
        return sql;

    }

    private String beautifySql(String sql) {
        sql = sql.replace("\n", "")
                .replace("\t", "")
                .replace("  ", " ")
                .replace("( ", "(")
                .replace(" )", ")")
                .replace(" ,", ",");
        return sql;

    }

    private String handleListParameter(String sql, Collection<?> col) {
        if (col != null && col.size() != 0) {
            for (Object obj : col) {
                String value = null;
                Class<?> objClass = obj.getClass();
                if (isPrimitiveOrPrimitiveWrapper(objClass)) {
                    value = obj.toString();
                } else if (objClass.isAssignableFrom(String.class)) {
                    value = "\"" + obj.toString() + "\"";
                }
                sql = sql.replaceFirst("\\?", value);
            }
        }
        return sql;

    }

    private String handleMapParameter(String sql, Map<?, ?> paramMap, List<ParameterMapping> parameterMappingList) {
        for (ParameterMapping parameterMapping : parameterMappingList) {
            Object propertyName = parameterMapping.getProperty();
            Object propertyValue = paramMap.get(propertyName);
            if (propertyValue != null) {
                if (propertyValue.getClass().isAssignableFrom(String.class)) {
                    propertyValue = "\"" + propertyValue + "\"";
                }
                sql = sql.replaceFirst("\\?", propertyValue.toString());
            }
        }
        return sql;

    }


    private String handleCommonParameter(String sql, List<ParameterMapping> parameterMappingList,
                                         Class<?> parameterObjectClass,
                                         Object parameterObject) throws Exception {
        for (ParameterMapping parameterMapping : parameterMappingList) {
            String propertyValue = null;
            if (isPrimitiveOrPrimitiveWrapper(parameterObjectClass)) {
                propertyValue = parameterObject.toString();
            } else {
                String propertyName = parameterMapping.getProperty();
                Field field = parameterObjectClass.getDeclaredField(propertyName);
                field.setAccessible(true);
                propertyValue = String.valueOf(field.get(parameterObject));
                if (parameterMapping.getJavaType().isAssignableFrom(String.class)) {
                    propertyValue = "\"" + propertyValue + "\"";
                }
            }
            sql = sql.replaceFirst("\\?", propertyValue);
        }
        return sql;

    }

    private boolean isPrimitiveOrPrimitiveWrapper(Class<?> parameterObjectClass) {
        return parameterObjectClass.isPrimitive() ||
                (parameterObjectClass.isAssignableFrom(Byte.class)
                        || parameterObjectClass.isAssignableFrom(Short.class)
                        || parameterObjectClass.isAssignableFrom(Integer.class)
                        || parameterObjectClass.isAssignableFrom(Long.class)
                        || parameterObjectClass.isAssignableFrom(Double.class)
                        || parameterObjectClass.isAssignableFrom(Float.class)
                        || parameterObjectClass.isAssignableFrom(Character.class)
                        || parameterObjectClass.isAssignableFrom(Boolean.class));
    }

    private boolean isStrictMap(Class<?> parameterObjectClass) {
        return parameterObjectClass.isAssignableFrom(DefaultSqlSession.StrictMap.class);

    }


    private boolean isList(Class<?> clazz) {
        Class<?>[] interfaceClasses = clazz.getInterfaces();
        for (Class<?> interfaceClass : interfaceClasses) {
            if (interfaceClass.isAssignableFrom(List.class)) {
                return true;
            }
        }
        return false;

    }


    private boolean isMap(Class<?> parameterObjectClass) {
        Class<?>[] interfaceClasses = parameterObjectClass.getInterfaces();
        for (Class<?> interfaceClass : interfaceClasses) {
            if (interfaceClass.isAssignableFrom(Map.class)) {
                return true;
            }
        }
        return false;

    }


}
