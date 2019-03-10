package com.blog4java.mybatis.example;

import com.alibaba.fastjson.JSON;
import com.blog4java.common.DbUtils;
import com.blog4java.mybatis.example.entity.UserEntity;
import com.blog4java.mybatis.example.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicSqlExample {

    private UserMapper userMapper;

    private SqlSession sqlSession;
    @Before
    public void initData() throws IOException {
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
    public void testDynamicSql()  {
        UserEntity entity = new UserEntity();
        entity.setPhone("18700001111");
        List<UserEntity> userList =  userMapper.getUserByEntity(entity);
        System.out.println(JSON.toJSONString(userList));
    }

    @Test
    public void testGetUserByPhoneAndName() {
        UserEntity userEntity = userMapper.getUserByPhoneAndName("18700001111", "User4");
        System.out.println(userEntity);
    }

    @Test
    public void testGetUserByNames() {
        List<UserEntity> users = userMapper.getUserByNames(Arrays.asList("User4","User5"));
        System.out.println(users);

    }

    @Test
    public void testSqlNode() {
        // 构建SqlNode
        SqlNode sn1 = new StaticTextSqlNode("select * from user where 1=1");
        SqlNode sn2 = new IfSqlNode(new StaticTextSqlNode(" AND id = #{id}"),"id != null");
        SqlNode sn3 = new IfSqlNode(new StaticTextSqlNode(" AND name = #{name}"),"name != null");
        SqlNode sn4 = new IfSqlNode(new StaticTextSqlNode(" AND phone = #{phone}"),"phone != null");
        SqlNode mixedSqlNode = new MixedSqlNode(Arrays.asList(sn1, sn2, sn3, sn4));
        // 创建参数对象
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id","1");
        // 创建动态SQL解析上下文
        DynamicContext context = new DynamicContext(sqlSession.getConfiguration(),paramMap);
        // 调用SqlNode的apply()方法解析动态SQL
        mixedSqlNode.apply(context);
        // 调用DynamicContext对象的getSql（）方法获取动态SQL解析后的SQL语句
        System.out.println(context.getSql());
    }


    @Test
    public void testGetUserByName() {
        String userName = "'Test4'";
        UserEntity userEntity = userMapper.getUserByName(userName);
        System.out.println(userEntity);
    }





}
