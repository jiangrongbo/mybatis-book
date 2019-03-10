package com.blog4java.mybatis.example.mapper;

import com.blog4java.mybatis.example.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {

    List<Order> listOrdersByUserId(@Param("userId") Long userId);

    Order getOrderByNo(@Param("orderNo") String orderNo);

    Order getOrderByNoWithJoin(@Param("orderNo") String orderNo);

}