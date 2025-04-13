package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.User;
import com.ruralexpress.entity.UserVerification;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.mapper.UserVerificationMapper;
import com.ruralexpress.service.SmsService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    @Autowired
    private SmsService smsService;
    
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
    
    /**
     * 管理员功能：获取用户列表，带分页
     */
    @Override
    public Map<String, Object> getUsersWithPagination(Integer page, Integer pageSize, String keyword, Integer userType, Integer status) {
        try {
            log.info("开始获取用户列表: page={}, pageSize={}, keyword={}, userType={}, status={}", page, pageSize, keyword, userType, status);
            
            // 构建查询条件
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            
            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w
                    .like(User::getPhone, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getRealName, keyword)
                );
            }
            
            // 用户类型筛选
            if (userType != null) {
                wrapper.eq(User::getUserType, userType);
            }
            
            // 状态筛选
            if (status != null) {
                wrapper.eq(User::getStatus, status);
            }
            
            // 按创建时间倒序排序
            wrapper.orderByDesc(User::getCreatedAt);
            
            // 分页查询
            Page<User> pageParam = new Page<>(page, pageSize);
            IPage<User> userPage = userMapper.selectPage(pageParam, wrapper);
            
            // 处理结果
            List<User> users = userPage.getRecords();
            
            // 清空敏感信息
            for (User user : users) {
                user.setPassword(null);
            }
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", users);
            result.put("total", userPage.getTotal());
            result.put("pages", userPage.getPages());
            result.put("current", userPage.getCurrent());
            
            log.info("获取用户列表成功: 共{}条记录", userPage.getTotal());
            return result;
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage(), e);
            throw new BusinessException("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 管理员功能：检查手机号是否已存在
     */
    @Override
    public boolean isPhoneExists(String phone) {
        try {
            User user = findByPhone(phone);
            return user != null;
        } catch (Exception e) {
            log.error("检查手机号是否存在时出错: {}", e.getMessage(), e);
            throw new BusinessException("检查手机号是否存在时出错: " + e.getMessage());
        }
    }
    
    /**
     * 管理员功能：创建用户
     */
    @Override
    @Transactional
    public User createUser(User user) {
        try {
            log.info("开始创建用户: {}", user.getPhone());
            
            // 检查手机号是否已存在
            if (isPhoneExists(user.getPhone())) {
                log.warn("创建用户失败：手机号{}已存在", user.getPhone());
                throw new BusinessException("该手机号已被注册");
            }
            
            // 设置默认值
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // 设置默认密码（例如手机号后6位）
                String defaultPassword = user.getPhone().substring(Math.max(0, user.getPhone().length() - 6));
                user.setPassword(passwordEncoder.encode(defaultPassword));
                log.debug("为用户{}设置默认密码", user.getPhone());
            }
            
            // 如果昵称为空，设置默认昵称
            if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
                String phone = user.getPhone();
                String defaultNickname = "用户" + phone.substring(Math.max(0, phone.length() - 4));
                user.setNickname(defaultNickname);
            }
            
            // 如果用户类型为空，设置为普通用户
            if (user.getUserType() == null) {
                user.setUserType(0);
            }
            
            // 如果状态为空，设置为正常状态
            if (user.getStatus() == null) {
                user.setStatus(0);
            }
            
            // 如果性别为空，设置为未知
            if (user.getGender() == null) {
                user.setGender(0);
            }
            
            // 如果实名认证状态为空，设置为未认证
            if (user.getVerified() == null) {
                user.setVerified(0);
            }
            
            // 设置创建和更新时间
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            // 保存用户
            userMapper.insert(user);
            log.info("用户创建成功: {}", user.getPhone());
            
            // 清空密码后返回
            user.setPassword(null);
            return user;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建用户过程中发生错误: {}", e.getMessage(), e);
            throw new BusinessException("创建用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 管理员功能：更新用户
     */
    @Override
    @Transactional
    public User updateUser(User user) {
        try {
            log.info("开始更新用户: id={}, phone={}", user.getId(), user.getPhone());
            
            // 查询原用户
            User existingUser = findById(user.getId());
            if (existingUser == null) {
                log.warn("更新用户失败: id={}的用户不存在", user.getId());
                throw new BusinessException("用户不存在");
            }
            
            // 如果更新手机号，检查手机号是否被其他用户使用
            if (user.getPhone() != null && !user.getPhone().equals(existingUser.getPhone())) {
                User phoneUser = findByPhone(user.getPhone());
                if (phoneUser != null && !phoneUser.getId().equals(user.getId())) {
                    log.warn("更新用户失败: 手机号{}已被其他用户使用", user.getPhone());
                    throw new BusinessException("该手机号已被其他用户使用");
                }
            }
            
            // 处理密码更新
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // 不更新密码，使用原密码
                user.setPassword(existingUser.getPassword());
            }
            
            // 设置更新时间
            user.setUpdatedAt(LocalDateTime.now());
            
            // 更新用户
            userMapper.updateById(user);
            log.info("用户更新成功: id={}", user.getId());
            
            // 清空密码后返回
            User updatedUser = findById(user.getId());
            updatedUser.setPassword(null);
            return updatedUser;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户过程中发生错误: {}", e.getMessage(), e);
            throw new BusinessException("更新用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 管理员功能：删除用户
     */
    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        try {
            log.info("开始删除用户: id={}", id);
            
            // 查询用户
            User user = findById(id);
            if (user == null) {
                log.warn("删除用户失败: id={}的用户不存在", id);
                throw new BusinessException("用户不存在");
            }
            
            // 删除用户
            int result = userMapper.deleteById(id);
            
            // 删除用户相关的认证信息
            LambdaQueryWrapper<UserVerification> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserVerification::getUserId, id);
            userVerificationMapper.delete(wrapper);
            
            log.info("用户删除成功: id={}", id);
            return result > 0;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户过程中发生错误: {}", e.getMessage(), e);
            throw new BusinessException("删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 按关键词搜索用户
     */
    @Override
    public Map<String, Object> searchUsers(String keyword, Integer limit) {
        try {
            log.info("开始搜索用户: keyword={}, limit={}", keyword, limit);
            
            if (!StringUtils.hasText(keyword)) {
                log.warn("搜索用户失败: 关键词为空");
                throw new BusinessException("搜索关键词不能为空");
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认限制10条
            }
            
            // 构建查询条件
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w
                .like(User::getPhone, keyword)
                .or()
                .like(User::getNickname, keyword)
                .or()
                .like(User::getRealName, keyword)
            );
            wrapper.eq(User::getStatus, 0); // 只搜索正常状态的用户
            wrapper.orderByDesc(User::getCreatedAt);
            wrapper.last("LIMIT " + limit); // 限制结果数量
            
            // 执行查询
            List<User> users = userMapper.selectList(wrapper);
            
            // 处理结果
            List<Map<String, Object>> userList = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("phone", user.getPhone());
                userMap.put("nickname", user.getNickname());
                userMap.put("avatar", user.getAvatar());
                userMap.put("realName", user.getRealName());
                userMap.put("userType", user.getUserType());
                userList.add(userMap);
            }
            
            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", userList);
            result.put("total", userList.size());
            
            log.info("搜索用户成功: 共{}条结果", userList.size());
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("搜索用户过程中发生错误: {}", e.getMessage(), e);
            throw new BusinessException("搜索用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            log.info("开始处理用户{}的密码修改", userId);
            
            // 查询用户
            User user = this.findById(userId);
            if (user == null) {
                log.warn("修改密码失败: 用户{}不存在", userId);
                throw new BusinessException("用户不存在");
            }
            
            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                log.warn("修改密码失败: 用户{}的旧密码错误", userId);
                throw new BusinessException("旧密码错误");
            }
            
            // 如果新密码与旧密码相同，直接返回成功
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                log.info("用户{}的新密码与旧密码相同，无需修改", userId);
                return true;
            }
            
            // 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            
            // 更新密码
            user.setPassword(encodedPassword);
            user.setUpdatedAt(LocalDateTime.now());
            
            int result = userMapper.updateById(user);
            
            if (result > 0) {
                log.info("用户{}密码修改成功", userId);
                return true;
            } else {
                log.warn("用户{}密码修改失败: 数据库更新异常", userId);
                throw new BusinessException("密码修改失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改密码过程中发生错误: {}", e.getMessage(), e);
            throw new BusinessException("修改密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 更换手机号
     */
    @Override
    @Transactional
    public boolean changePhone(Long userId, String newPhone, String verifyCode) {
        try {
            log.info("开始处理用户{}的手机号更换", userId);
            
            // 查询用户
            User user = this.findById(userId);
            if (user == null) {
                log.warn("更换手机号失败: 用户{}不存在", userId);
                throw new BusinessException("用户不存在");
            }
            
            // 检查新手机号是否已被使用
            User existingUser = findByPhone(newPhone);
            if (existingUser != null) {
                log.warn("更换手机号失败: 手机号{}已被其他用户使用", newPhone);
                throw new BusinessException("该手机号已被其他用户使用");
            }
            
            // 验证验证码
            boolean validCode = smsService.verifyCode(newPhone, verifyCode, "changePhone");
            if (!validCode) {
                log.warn("更换手机号失败: 用户{}的验证码错误", userId);
                throw new BusinessException("验证码错误或已过期");
            }
            
            // 保存旧手机号(可用于记录历史)
            String oldPhone = user.getPhone();
            
            // 更新手机号
            user.setPhone(newPhone);
            user.setUpdatedAt(LocalDateTime.now());
            
            int result = userMapper.updateById(user);
            
            if (result > 0) {
                log.info("用户{}手机号更换成功, 旧号码: {}, 新号码: {}", userId, oldPhone, newPhone);
                return true;
            } else {
                log.warn("用户{}手机号更换失败: 数据库更新异常", userId);
                throw new BusinessException("手机号更换失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更换手机号过程中发生错误: {}", e.getMessage(), e);
            throw new BusinessException("更换手机号失败: " + e.getMessage());
        }
    }
} 