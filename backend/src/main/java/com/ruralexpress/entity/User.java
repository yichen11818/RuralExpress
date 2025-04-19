package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("t_user")
public class User {
    
    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 密码(加密存储)
     */
    private String password;
    
    /**
     * 昵称
     */
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
     * 个性签名
     */
    private String bio;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 身份证号(加密存储)
     */
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
     * 状态(0-正常,1-禁用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 获取用户名，返回手机号作为用户名
     * @return 用户名
     */
    public String getUsername() {
        return this.phone;
    }
} 