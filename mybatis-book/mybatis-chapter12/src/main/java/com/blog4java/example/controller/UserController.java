package com.blog4java.example.controller;

import com.blog4java.example.entity.User;
import com.blog4java.example.param.UserRegisterParam;
import com.blog4java.example.service.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("register")
    public String userRegister(@Validated UserRegisterParam param)
            throws Exception {
        User user = new User();
        BeanUtils.copyProperties(user,param);
        if(userService.userRegister(user)) {
            return "注册成功";
        }
        return "注册失败";
    }

    @RequestMapping("getAllUser")
    public List<User> getAllUserInfo() {
        return userService.getAllUserInfo();
    }
}
