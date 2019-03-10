package com.blog4java.mybatis.example.mapper;

import com.blog4java.mybatis.example.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User getUserById(@Param("userId") Long userId);

    User getUserByIdFull(@Param("userId") Long userId);

    User getUserByIdForUserDetailMap(@Param("userId") Long userId);

    User getUserByIdForDiscriminator(@Param("userId") Long userId);

    User getUserByIdForJoin(@Param("userId") Long userId);

    List<User> getUsersByPhone(@Param("phone") String phone);

}