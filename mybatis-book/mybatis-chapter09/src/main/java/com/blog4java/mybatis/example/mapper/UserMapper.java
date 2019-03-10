package com.blog4java.mybatis.example.mapper;

import com.blog4java.mybatis.example.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {

    List<UserEntity> getUserByEntity(UserEntity user);

    List<UserEntity> getUserInfo(UserEntity user);

    List<UserEntity> getUserByPhones(@Param("phones") List<String> phones);

    @Select("<script>" +
            "select * from user\n" +
            "<where>\n" +
            "    <if test=\"name != null\">\n" +
            "        AND name = #{name}\n" +
            "    </if>\n" +
            "    <if test=\"phone != null\">\n" +
            "        AND phone = #{phone}\n" +
            "    </if>\n" +
            "</where>" +
            "</script>")
    UserEntity getUserByPhoneAndName(@Param("phone") String phone, @Param("name") String name);


    List<UserEntity> getUserByNames(@Param("names") List<String> names);

    UserEntity getUserByName(@Param("userName") String userName);
}