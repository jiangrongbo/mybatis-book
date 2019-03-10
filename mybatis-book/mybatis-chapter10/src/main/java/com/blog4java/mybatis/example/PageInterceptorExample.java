package com.blog4java.mybatis.example;

import com.alibaba.fastjson.JSON;
import com.blog4java.common.DbUtils;
import com.blog4java.mybatis.example.entity.UserEntity;
import com.blog4java.mybatis.example.mapper.UserMapper;
import com.blog4java.mybatis.example.query.UserQuery;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PageInterceptorExample {

    private UserMapper userMapper;

    private SqlSession sqlSession;

    @Before
    public void init() throws IOException {
        DbUtils.initData();
        // 获取配置文件输入流
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        // 通过SqlSessionFactoryBuilder的build()方法创建SqlSessionFactory实例
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 调用openSession()方法创建SqlSession实例
        sqlSession = sqlSessionFactory.openSession();
        // 获取UserMapper代理对象
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    @Test
    public void testPageInterceptor() {
        UserQuery query = new UserQuery();
        query.setPageSize(5);
        query.setFull(true);
        List<UserEntity> users = userMapper.getUserPageable(query);
        System.out.println("总数据量：" + query.getTotalCount() + ",总页数："
                + query.getTotalPage()+ "，当前查询数据：" + JSON.toJSONString(users));
    }



}
