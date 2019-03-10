package com.blog4java.example.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterParam {
    @NotNull(message = "用户名不能为空")
    private String name;
    @NotNull(message = "密码不能为空")
    private String password;
    @NotNull(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "性别不能为空")
    @Pattern(regexp = "(male|female)" ,message = "性别输入不合法")
    private String gender;
    private String nickName;
}
