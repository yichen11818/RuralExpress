package com.ruralexpress.controller;

import com.ruralexpress.dto.UserDto;
import com.ruralexpress.entity.User;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.service.UserService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * @param userDto 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResult<User> register(@Validated @RequestBody UserDto userDto) {
        log.info("接收到用户注册请求: {}", userDto.getPhone());
        try {
            // 数据转换
            User user = new User();
            BeanUtils.copyProperties(userDto, user);
            
            // 调用服务进行注册
            User registeredUser = userService.register(user);
            log.info("用户注册成功: {}", userDto.getPhone());
            return ApiResult.success(registeredUser);
        } catch (BusinessException e) {
            // 业务异常，返回具体错误信息
            log.warn("用户注册业务异常: {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            // 未预期的异常，返回详细错误信息
            log.error("用户注册过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ApiResult<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ApiResult.notFound("用户不存在");
        }
        
        // 清空密码
        user.setPassword(null);
        return ApiResult.success(user);
    }
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDto 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<User> updateUser(@PathVariable Long id, @Validated @RequestBody UserDto userDto) {
        // 检查用户是否存在
        User existUser = userService.findById(id);
        if (existUser == null) {
            return ApiResult.notFound("用户不存在");
        }
        
        // 设置用户ID，防止篡改
        userDto.setId(id);
        
        // 复制属性
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        
        // 更新用户
        User updatedUser = userService.update(user);
        return ApiResult.success(updatedUser);
    }
    
    /**
     * 实名认证
     * @param id 用户ID
     * @param userDto 认证信息
     * @return 认证结果
     */
    @PostMapping("/{id}/verify")
    public ApiResult<String> verify(@PathVariable Long id, @Validated @RequestBody UserDto userDto) {
        // 检查用户是否存在
        User existUser = userService.findById(id);
        if (existUser == null) {
            return ApiResult.notFound("用户不存在");
        }
        
        // 根据是否提供照片URL选择不同的认证方法
        boolean result;
        
        if (userDto.getFrontImage() != null && userDto.getBackImage() != null && userDto.getHoldingImage() != null) {
            // 包含照片的认证
            log.info("接收到带照片的实名认证请求: {}", id);
            result = userService.verify(
                id,
                userDto.getRealName(),
                userDto.getIdCard(),
                userDto.getFrontImage(),
                userDto.getBackImage(),
                userDto.getHoldingImage()
            );
        } else {
            // 基本认证
            log.info("接收到基本实名认证请求: {}", id);
            result = userService.verify(id, userDto.getRealName(), userDto.getIdCard());
        }
        
        if (result) {
            return ApiResult.success("实名认证成功");
        } else {
            return ApiResult.error(400, "实名认证失败");
        }
    }
} 