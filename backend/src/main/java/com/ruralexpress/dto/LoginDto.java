package com.ruralexpress.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 登录数据传输对象
 */
@Data
public class LoginDto {
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 是否记住我
     */
    private Boolean rememberMe = false;
} 