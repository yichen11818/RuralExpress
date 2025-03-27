package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruralexpress.entity.User;
import com.ruralexpress.entity.UserVerification;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.mapper.UserVerificationMapper;
import com.ruralexpress.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserVerificationMapper userVerificationMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Override
    @Transactional
    public User register(User user) {
        try {
            // 详细记录注册过程
            log.info("开始注册用户: {}", user.getPhone());
            
            // 检查手机号是否已存在
            User existUser = this.findByPhone(user.getPhone());
            if (existUser != null) {
                log.warn("注册失败：手机号{}已被注册", user.getPhone());
                throw new BusinessException("该手机号已被注册，请直接登录或使用其他手机号");
            }
            
            // 设置默认值
            try {
                log.debug("对密码进行加密处理");
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } catch (Exception e) {
                log.error("密码加密失败: {}", e.getMessage(), e);
                throw new BusinessException("密码处理失败，请稍后重试");
            }
            
            // 如果昵称为空，设置默认昵称
            if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
                // 使用手机号后四位作为默认昵称
                String phone = user.getPhone();
                String defaultNickname = "用户" + phone.substring(Math.max(0, phone.length() - 4));
                user.setNickname(defaultNickname);
                log.debug("设置默认昵称: {}", defaultNickname);
            }
            user.setUserType(0); // 默认普通用户
            user.setVerified(0); // 默认未实名认证
            user.setStatus(0);   // 默认正常状态
            user.setGender(0);   // 默认未知
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            // 保存用户
            log.debug("开始保存用户数据到数据库");
            try {
                userMapper.insert(user);
                log.info("用户注册成功: {}", user.getPhone());
            } catch (DuplicateKeyException e) {
                log.error("用户注册失败，手机号已存在: {}", e.getMessage());
                throw new BusinessException("该手机号已被注册，请直接登录或使用其他手机号");
            } catch (DataIntegrityViolationException e) {
                log.error("数据完整性错误: {}", e.getMessage(), e);
                throw new BusinessException("注册信息不完整或格式错误，请检查后重试");
            } catch (Exception e) {
                log.error("用户注册过程中数据库操作失败: {}", e.getMessage(), e);
                throw new BusinessException("注册失败，请稍后重试: " + e.getMessage());
            }
            
            // 清空密码后返回
            user.setPassword(null);
            return user;
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // 捕获并记录其他未预期的异常
            log.error("用户注册过程中发生未知异常: {}", e.getMessage(), e);
            throw new BusinessException("注册过程中发生错误: " + e.getMessage());
        }
    }
    
    @Override
    public User login(String phone, String password) {
        try {
            log.info("开始处理用户登录请求: {}", phone);
            
            // 查询用户
            User user = this.findByPhone(phone);
            if (user == null) {
                log.warn("登录失败: 手机号{}对应的用户不存在", phone);
                throw new BusinessException("用户不存在，请先注册");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("登录失败: 手机号{}的用户密码错误", phone);
                throw new BusinessException("密码错误，请重新输入");
            }
            
            // 检查用户状态
            if (user.getStatus() != 0) {
                log.warn("登录失败: 手机号{}的用户账号已被禁用", phone);
                throw new BusinessException("账号已被禁用，请联系客服");
            }
            
            log.info("用户{}登录成功", phone);
            
            // 清空密码后返回
            user.setPassword(null);
            return user;
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // 捕获并记录其他未预期的异常
            log.error("用户登录过程中发生未知异常: {}", e.getMessage(), e);
            throw new BusinessException("登录过程中发生错误: " + e.getMessage());
        }
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }
    
    @Override
    @Transactional
    public User update(User user) {
        // 设置更新时间
        user.setUpdatedAt(LocalDateTime.now());
        
        // 更新用户信息
        userMapper.updateById(user);
        
        // 返回更新后的用户信息
        return this.findById(user.getId());
    }
    
    @Override
    @Transactional
    public boolean verify(Long id, String realName, String idCard) {
        try {
            log.info("开始处理用户{}的实名认证", id);
            
            // 查询用户
            User user = this.findById(id);
            if (user == null) {
                log.warn("实名认证失败: 用户{}不存在", id);
                throw new BusinessException("用户不存在");
            }
            
            // 调用实名认证接口验证身份证信息
            boolean verificationResult = verifyIdCard(realName, idCard);
            
            // 更新认证信息
            user.setRealName(realName);
            user.setIdCard(idCard); // 实际应用中应该加密保存
            
            // 根据验证结果设置认证状态
            if (verificationResult) {
                user.setVerified(1); // 已认证
                log.info("用户{}实名认证成功", id);
            } else {
                user.setVerified(2); // 认证失败
                log.warn("用户{}实名认证失败: 身份信息不匹配", id);
                throw new BusinessException("身份信息验证失败，请确认姓名和身份证号是否正确");
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            
            // 保存更新
            userMapper.updateById(user);
            
            return verificationResult;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("实名认证过程中发生未知异常: {}", e.getMessage(), e);
            throw new BusinessException("实名认证失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean verify(Long id, String realName, String idCard, String frontImage, String backImage, String holdingImage) {
        try {
            log.info("开始处理用户{}的实名认证(含照片)", id);
            
            // 查询用户
            User user = this.findById(id);
            if (user == null) {
                log.warn("实名认证失败: 用户{}不存在", id);
                throw new BusinessException("用户不存在");
            }
            
            // 调用实名认证接口验证身份证信息
            boolean verificationResult = verifyIdCard(realName, idCard);
            
            // 更新认证信息
            user.setRealName(realName);
            user.setIdCard(idCard); // 实际应用中应该加密保存
            
            // 保存认证信息和照片URL
            UserVerification verification = new UserVerification();
            verification.setUserId(id);
            verification.setRealName(realName);
            verification.setIdCard(idCard);
            verification.setFrontImage(frontImage);
            verification.setBackImage(backImage);
            verification.setHoldingImage(holdingImage);
            verification.setVerifyStatus(verificationResult ? 1 : 2); // 1-通过, 2-未通过
            verification.setVerifyTime(LocalDateTime.now());
            verification.setCreatedAt(LocalDateTime.now());
            
            userVerificationMapper.insert(verification);
            
            // 根据验证结果设置认证状态
            if (verificationResult) {
                user.setVerified(1); // 已认证
                log.info("用户{}实名认证成功", id);
            } else {
                user.setVerified(2); // 认证失败
                log.warn("用户{}实名认证失败: 身份信息不匹配", id);
                throw new BusinessException("身份信息验证失败，请确认姓名和身份证号是否正确");
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            
            // 保存更新
            userMapper.updateById(user);
            
            return verificationResult;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("实名认证过程中发生未知异常: {}", e.getMessage(), e);
            throw new BusinessException("实名认证失败: " + e.getMessage());
        }
    }
    
    /**
     * 调用实名认证API验证身份证信息
     * @param realName 真实姓名
     * @param idCard 身份证号
     * @return 验证结果
     */
    private boolean verifyIdCard(String realName, String idCard) {
        log.info("调用实名认证API验证身份信息");
        
        try {
            // 这里是一个简化的模拟实现，实际应该调用第三方API
            // 例如阿里云实名认证API: https://help.aliyun.com/document_detail/213346.html
            
            // 简单验证格式
            if (realName == null || realName.trim().isEmpty()) {
                log.warn("实名认证失败: 姓名为空");
                return false;
            }
            
            if (idCard == null || !idCard.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)")) {
                log.warn("实名认证失败: 身份证号格式不正确");
                return false;
            }
            
            // 模拟调用第三方API
            /*
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "APPCODE " + appCode);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("name", realName);
            map.add("idcard", idCard);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            String apiUrl = "https://idcard.market.alicloudapi.com/idcard";
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            
            if (response != null && "01".equals(response.get("status"))) {
                // 认证通过
                return true;
            } else {
                // 认证失败
                log.warn("实名认证API返回失败: {}", response);
                return false;
            }
            */
            
            // 简单模拟，这里直接返回true表示认证通过
            // 实际业务中应该根据第三方API的返回结果判断
            log.info("模拟实名认证成功");
            return true;
            
        } catch (Exception e) {
            log.error("调用实名认证API过程中发生异常: {}", e.getMessage(), e);
            throw new BusinessException("身份验证服务异常，请稍后重试");
        }
    }
} 