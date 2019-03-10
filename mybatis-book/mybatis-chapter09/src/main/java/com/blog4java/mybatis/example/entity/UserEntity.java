package com.blog4java.mybatis.example.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserEntity {
    private Long id;
    private String name;
    private Date createTime;
    private String password;
    private String phone;
    private String nickName;
}
