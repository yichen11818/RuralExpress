package com.ruralexpress.service.impl;

import com.ruralexpress.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现类
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // 验证码过期时间(分钟)
    private static final int EXPIRE_MINUTES = 5;
    
    // 验证码Redis前缀
    private static final String VERIFY_CODE_PREFIX = "verify_code:";
    
    /**
     * 生成验证码
     * @return 6位数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        // 生成6位数字验证码
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * 构建验证码Redis键
     * @param phone 手机号
     * @param type 验证码类型
     * @return Redis键
     */
    private String buildRedisKey(String phone, String type) {
        return VERIFY_CODE_PREFIX + type + ":" + phone;
    }
    
    @Override
    public boolean sendVerifyCode(String phone, String type) {
        try {
            // 生成验证码
            String code = generateCode();
            
            // 保存验证码到Redis
            String redisKey = buildRedisKey(phone, type);
            redisTemplate.opsForValue().set(redisKey, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            // TODO: 实际发送短信
            // 这里应该调用实际的短信发送服务
            log.info("发送验证码 {} 到 {}, 类型: {}", code, phone, type);
            
            return true;
        } catch (Exception e) {
            log.error("发送验证码失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String phone, String code, String type) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        
        try {
            // 从Redis获取验证码
            String redisKey = buildRedisKey(phone, type);
            String savedCode = redisTemplate.opsForValue().get(redisKey);
            
            // 验证码不存在或已过期
            if (savedCode == null) {
                return false;
            }
            
            // 验证码比对
            boolean result = code.equals(savedCode);
            
            // 验证成功后，删除验证码
            if (result) {
                redisTemplate.delete(redisKey);
            }
            
            return result;
        } catch (Exception e) {
            log.error("验证码校验失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 