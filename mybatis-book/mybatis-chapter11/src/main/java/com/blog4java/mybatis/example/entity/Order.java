package com.blog4java.mybatis.example.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class Order {
    private Long id;
    private Date createTime;
    private BigDecimal amount;
    private Long userId;
    private String orderNo;
    private String address;
    private User user;
}
