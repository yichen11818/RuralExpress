package com.ruralexpress.service;

import com.ruralexpress.entity.User;

import java.util.Map;

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
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 更换手机号
     * @param userId 用户ID
     * @param newPhone 新手机号
     * @param verifyCode 验证码
     * @return 是否更换成功
     */
    boolean changePhone(Long userId, String newPhone, String verifyCode);
    
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
    
    /**
     * 管理员功能：获取用户列表，带分页
     * @param page 当前页
     * @param pageSize 每页大小
     * @param keyword 搜索关键词（可以是手机号、姓名等）
     * @param userType 用户类型（0普通用户、1快递员、2管理员）
     * @param status 用户状态（0正常、1禁用）
     * @return 包含用户列表和统计数据的结果
     */
    Map<String, Object> getUsersWithPagination(Integer page, Integer pageSize, String keyword, Integer userType, Integer status);
    
    /**
     * 管理员功能：判断手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);
    
    /**
     * 管理员功能：创建用户
     * @param user 用户信息
     * @return 创建后的用户
     */
    User createUser(User user);
    
    /**
     * 管理员功能：更新用户
     * @param user 用户信息
     * @return 更新后的用户
     */
    User updateUser(User user);
    
    /**
     * 管理员功能：删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long id);
    
    /**
     * 按关键词搜索用户
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 用户列表
     */
    Map<String, Object> searchUsers(String keyword, Integer limit);
} 