package com.ruralexpress.controller.admin;

import com.ruralexpress.entity.User;
import com.ruralexpress.service.UserService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param userType 用户类型
     * @param status 用户状态
     * @return 用户列表
     */
    @GetMapping
    public ApiResult<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) Integer status) {
        
        try {
            Map<String, Object> result = userService.getUsersWithPagination(page, pageSize, keyword, userType, status);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public ApiResult<User> getUserDetail(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ApiResult.notFound("用户不存在");
            }
            
            // 清空敏感信息
            user.setPassword(null);
            return ApiResult.success(user);
        } catch (Exception e) {
            log.error("获取用户详情失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建用户
     * @param userData 用户信息
     * @return 创建结果
     */
    @PostMapping
    public ApiResult<User> createUser(@RequestBody Map<String, Object> userData) {
        try {
            log.info("接收到创建用户请求: {}", userData);
            
            // 从Map中获取字段值
            String phone = (String) userData.get("phone");
            String password = (String) userData.get("password");
            String nickname = (String) userData.get("nickname");
            String realName = (String) userData.get("realName");
            
            // 检查必要参数
            if (phone == null || phone.trim().isEmpty()) {
                return ApiResult.error(400, "手机号不能为空");
            }
            
            // 检查手机号格式
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return ApiResult.error(400, "手机号格式不正确");
            }
            
            // 检查手机号是否已存在
            if (userService.isPhoneExists(phone)) {
                return ApiResult.error(400, "手机号已被注册");
            }
            
            // 创建新用户
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setPassword(password);
            newUser.setNickname(nickname);
            newUser.setRealName(realName);
            
            // 用户类型
            Object userTypeObj = userData.get("userType");
            if (userTypeObj != null) {
                if (userTypeObj instanceof Integer) {
                    newUser.setUserType((Integer) userTypeObj);
                } else if (userTypeObj instanceof Number) {
                    newUser.setUserType(((Number) userTypeObj).intValue());
                } else {
                    newUser.setUserType(0); // 默认普通用户
                }
            } else {
                newUser.setUserType(0); // 默认普通用户
            }
            
            // 实名认证状态
            Object verifiedObj = userData.get("verified");
            if (verifiedObj != null) {
                if (verifiedObj instanceof Boolean) {
                    newUser.setVerified(((Boolean) verifiedObj) ? 1 : 0);
                } else if (verifiedObj instanceof Integer) {
                    newUser.setVerified((Integer) verifiedObj);
                } else if (verifiedObj instanceof Number) {
                    newUser.setVerified(((Number) verifiedObj).intValue());
                } else {
                    newUser.setVerified(0); // 默认未认证
                }
            } else {
                newUser.setVerified(0); // 默认未认证
            }
            
            // 用户状态
            Object statusObj = userData.get("status");
            if (statusObj != null) {
                if (statusObj instanceof Integer) {
                    newUser.setStatus((Integer) statusObj);
                } else if (statusObj instanceof Number) {
                    newUser.setStatus(((Number) statusObj).intValue());
                } else {
                    newUser.setStatus(0); // 默认正常状态
                }
            } else {
                newUser.setStatus(0); // 默认正常状态
            }
            
            User createdUser = userService.createUser(newUser);
            log.info("用户创建成功: {}", createdUser.getId());
            
            createdUser.setPassword(null); // 清空敏感信息
            return ApiResult.success(createdUser);
        } catch (Exception e) {
            log.error("创建用户失败: {}", e.getMessage(), e);
            return ApiResult.serverError("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // 检查用户是否存在
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                return ApiResult.notFound("用户不存在");
            }
            
            // 设置ID，防止篡改
            user.setId(id);
            
            User updatedUser = userService.updateUser(user);
            updatedUser.setPassword(null); // 清空敏感信息
            return ApiResult.success(updatedUser);
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage(), e);
            return ApiResult.serverError("更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态值
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResult<User> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        
        try {
            // 获取状态值
            Integer status = statusMap.get("status");
            if (status == null) {
                return ApiResult.error(400, "缺少status参数");
            }
            
            // 检查用户是否存在
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                return ApiResult.notFound("用户不存在");
            }
            
            // 更新用户状态
            existingUser.setStatus(status);
            User updatedUser = userService.updateUser(existingUser);
            updatedUser.setPassword(null); // 清空敏感信息
            return ApiResult.success(updatedUser);
        } catch (Exception e) {
            log.error("更新用户状态失败: {}", e.getMessage(), e);
            return ApiResult.serverError("更新用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteUser(@PathVariable Long id) {
        try {
            // 检查用户是否存在
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                return ApiResult.notFound("用户不存在");
            }
            
            // 删除用户
            userService.deleteUser(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage(), e);
            return ApiResult.serverError("删除用户失败: " + e.getMessage());
        }
    }
} 