package com.blog4java.example.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String createTime;
    private String password;
    private String phone;
    private String gender;
    private String nickName;
}
