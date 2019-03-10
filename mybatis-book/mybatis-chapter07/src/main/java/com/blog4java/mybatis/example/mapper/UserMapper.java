package com.blog4java.mybatis.example.mapper;

import com.blog4java.mybatis.example.entity.UserEntity;

import java.util.List;

public interface UserMapper {

    List<UserEntity> listAllUser();

}