package com.blog4java.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class MetaObjectExample {

    @Data
    @AllArgsConstructor
    private static class User {
        List<Order> orders;
        String name;
        Integer age;
    }

    @Data
    @AllArgsConstructor
    private static class Order {
        String orderNo;
        String goodsName;
    }

    @Test
    public void testMetaObject() {
        List<Order> orders = new ArrayList() {
            {
                add(new Order("order20171024010246", "《Mybatis源码深度解析》图书"));
                add(new Order("order20171024010248", "《AngularJS入门与进阶》图书"));
            }
        };
        User user = new User(orders, "江荣波", 3);
        MetaObject metaObject = SystemMetaObject.forObject(user);
        // 获取第一笔订单的商品名称
        System.out.println(metaObject.getValue("orders[0].goodsName"));
        // 获取第二笔订单的商品名称
        System.out.println(metaObject.getValue("orders[1].goodsName"));
        // 为属性设置值
        metaObject.setValue("orders[1].orderNo","order20181113010139");
        // 判断User对象是否有orderNo属性
        System.out.println("是否有orderNo属性且orderNo属性有对应的Getter方法：" + metaObject.hasGetter("orderNo"));
        // 判断User对象是否有name属性
        System.out.println("是否有name属性且orderNo属性有对应的name方法：" + metaObject.hasGetter("name"));

    }
}
