package com.ruralexpress.service;

import com.ruralexpress.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功的用户
     */
    User register(User user);
    
    /**
     * 用户登录
     * @param phone 手机号
     * @param password 密码
     * @return 登录成功的用户信息，不包含密码
     */
    User login(String phone, String password);
    
    /**
     * 根据用户ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Long id);
    
    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);
    
    /**
     * 修改用户信息
     * @param user 用户信息
     * @return 修改后的用户信息
     */
    User update(User user);
    
    /**
     * 实名认证
     * @param id 用户ID
     * @param realName 真实姓名
     * @param idCard 身份证号
     * @return 认证结果
     */
    boolean verify(Long id, String realName, String idCard);
    
    /**
     * 实名认证（含证件照片）
     * @param id 用户ID
     * @param realName 真实姓名
     * @param idCard 身份证号
     * @param frontImage 身份证正面照片URL
     * @param backImage 身份证反面照片URL
     * @param holdingImage 手持身份证照片URL
     * @return 认证结果
     */
    boolean verify(Long id, String realName, String idCard, String frontImage, String backImage, String holdingImage);
} 