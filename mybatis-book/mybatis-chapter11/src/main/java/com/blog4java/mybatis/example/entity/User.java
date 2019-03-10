package com.blog4java.mybatis.example.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class User {
    private Long id;
    private String name;
    private Date createTime;
    private String password;
    private String phone;
    private String gender;
    private String nickName;
    private List<Order> orders;
}
