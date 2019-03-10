package com.blog4java.example.configure;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.blog4java.example.mapper"},
        sqlSessionTemplateRef="sqlSessionTemplate")
public class DataSourceConfiguration {
    @Bean(name = "dataSource")
    @Primary
    public DataSource setDataSource() {
        // 创建数据源Bean，并执行数据库脚本
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("create-table-c12.sql")
                .addScript("init-data-c12.sql")
                .build();
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory setSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        // 通过Mybatis Spring模块提供的SqlSessionFactoryBean
        // 创建Mybatis的SqlSessionFactory对象
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:com/blog4java/example/mapper/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate setSqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        // 创建Mybatis Spring模块中的SqlSessionTemplate对象
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager setTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Primary
    public TransactionTemplate transactionTemplate(@Qualifier("transactionManager")DataSourceTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
