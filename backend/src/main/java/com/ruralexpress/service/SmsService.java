package com.ruralexpress.service;

/**
 * 短信服务接口
 */
public interface SmsService {
    
    /**
     * 发送验证码
     * @param phone 手机号
     * @param type 验证码类型(register-注册,login-登录,resetPwd-重置密码,changePhone-更换手机号)
     * @return 是否发送成功
     */
    boolean sendVerifyCode(String phone, String type);
    
    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @param type 验证码类型
     * @return 是否验证成功
     */
    boolean verifyCode(String phone, String code, String type);
} 