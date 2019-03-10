package com.blog4java.mybatis.example.mapper;

import com.blog4java.mybatis.example.entity.UserEntity;
import com.blog4java.mybatis.example.query.UserQuery;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {

    @Select("select * from user")
    List<UserEntity> getUserPageable(UserQuery query);

}