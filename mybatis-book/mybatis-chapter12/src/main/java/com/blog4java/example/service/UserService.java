package com.blog4java.example.service;

import com.blog4java.example.entity.User;

import java.util.List;

public interface UserService {
    boolean userRegister(User user);

    List<User> getAllUserInfo();
}
