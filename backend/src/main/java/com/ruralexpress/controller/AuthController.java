package com.ruralexpress.controller;

import com.ruralexpress.dto.LoginDto;
import com.ruralexpress.entity.User;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.service.UserService;
import com.ruralexpress.utils.ApiResult;
import com.ruralexpress.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    /**
     * 用户登录
     * @param loginDto 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@Validated @RequestBody LoginDto loginDto) {
        log.info("用户登录请求: {}", loginDto.getPhone());
        
        try {
            // 用户登录
            User user = userService.login(loginDto.getPhone(), loginDto.getPassword());
            
            try {
                // 生成JWT令牌
                String token = jwtTokenUtil.generateToken(user.getId(), loginDto.getRememberMe());
                
                // 构造返回结果
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("user", user);
                
                log.info("用户登录成功: {}", loginDto.getPhone());
                return ApiResult.success(result);
            } catch (Exception e) {
                log.error("生成JWT令牌失败: {}", e.getMessage(), e);
                return ApiResult.serverError("登录验证失败: " + e.getMessage());
            }
        } catch (BusinessException e) {
            // 业务异常，返回具体错误信息
            log.warn("用户登录失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            // 未预期的异常，返回详细错误信息
            log.error("用户登录过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("登录失败: " + e.getMessage());
        }
    }
} 