package com.ruralexpress.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户数据传输对象
 */
@Data
public class UserDto {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 密码
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 性别(0-未知,1-男,2-女)
     */
    private Integer gender;
    
    /**
     * 生日
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "生日格式不正确，应为yyyy-MM-dd")
    private String birthday;
    
    /**
     * 个性签名
     */
    @Size(max = 100, message = "个性签名不能超过100个字符")
    private String bio;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 身份证号
     */
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号格式不正确")
    private String idCard;
    
    /**
     * 是否实名认证(0-否,1-是)
     */
    private Integer verified;
    
    /**
     * 用户类型(0-普通用户,1-快递员,2-管理员)
     */
    private Integer userType;
    
    /**
     * 身份证正面照片URL
     */
    private String frontImage;
    
    /**
     * 身份证反面照片URL
     */
    private String backImage;
    
    /**
     * 手持身份证照片URL
     */
    private String holdingImage;
} 