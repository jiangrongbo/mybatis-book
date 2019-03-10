package com.blog4java.mybatis.xpath;

import com.alibaba.fastjson.JSON;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.io.Resources;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XPathExample {

    @Test
    public void testXPathParser() {
        try {
            // 创建DocumentBuilderFactory实例
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 创建DocumentBuilder实例
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputSource = Resources.getResourceAsStream("users.xml");
            Document doc = builder.parse(inputSource);
            // 获取XPath实例
            XPath xpath = XPathFactory.newInstance().newXPath();
            // 执行XPath表达式，获取节点信息
            NodeList nodeList = (NodeList)xpath.evaluate("/users/*", doc, XPathConstants.NODESET);
            List<UserEntity> userList = new ArrayList<>();
            for(int i=1; i < nodeList.getLength() + 1; i++) {
                String path = "/users/user["+i+"]";
                String id = (String)xpath.evaluate(path + "/@id", doc, XPathConstants.STRING);
                String name = (String)xpath.evaluate(path + "/name", doc, XPathConstants.STRING);
                String createTime = (String)xpath.evaluate(path + "/createTime", doc, XPathConstants.STRING);
                String passward = (String)xpath.evaluate(path + "/passward", doc, XPathConstants.STRING);
                String phone = (String)xpath.evaluate(path + "/phone", doc, XPathConstants.STRING);
                String nickName = (String)xpath.evaluate(path + "/nickName", doc, XPathConstants.STRING);
                // 调用buildUserEntity()方法，构建UserEntity对象
                UserEntity userEntity = buildUserEntity(id,name, createTime, passward, phone, nickName);
                userList.add(userEntity);
            }
            System.out.println(JSON.toJSONString(userList));
        } catch (Exception e) {
            throw new BuilderException("Error creating document instance.  Cause: " + e, e);
        }
    }

    private UserEntity buildUserEntity(String id,String name,
                                       String createTime, String passward,
                                       String phone, String nickName)
            throws IllegalAccessException, InvocationTargetException {
        UserEntity userEntity = new UserEntity();
        DateConverter dateConverter = new DateConverter(null);
        dateConverter.setPattern("yyyy-MM-dd HH:mm:ss");
        ConvertUtils.register(dateConverter,Date.class);
        BeanUtils.setProperty(userEntity,"id",id);
        BeanUtils.setProperty(userEntity,"name",name);
        BeanUtils.setProperty(userEntity,"createTime",createTime);
        BeanUtils.setProperty(userEntity,"passward",passward);
        BeanUtils.setProperty(userEntity,"phone",phone);
        BeanUtils.setProperty(userEntity,"nickName",nickName);
        return userEntity;
    }
}
