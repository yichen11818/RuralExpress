package com.ruralexpress.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.common.ApiResult;
import com.ruralexpress.entity.Message;
import com.ruralexpress.service.MessageService;
import com.ruralexpress.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;
    
    /**
     * 获取用户未读消息数量
     */
    @GetMapping("/unread/count")
    public ApiResult<Integer> getUnreadMessageCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = messageService.getUnreadMessageCount(userId);
        return ApiResult.success(count);
    }
    
    /**
     * 获取用户消息列表
     */
    @GetMapping("/list")
    public ApiResult<IPage<Message>> getUserMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<Message> pageParam = new Page<>(page, pageSize);
        IPage<Message> messageList = messageService.getUserMessages(pageParam, userId);
        return ApiResult.success(messageList);
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    public ApiResult<Boolean> markMessageAsRead(@PathVariable Long messageId) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean result = messageService.markMessageAsRead(messageId, userId);
        return ApiResult.success(result);
    }
    
    /**
     * 获取管理员未处理的快递员申请消息
     */
    @GetMapping("/courier-applications")
    public ApiResult<IPage<Message>> getPendingCourierApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 验证是否是管理员
        if (!SecurityUtils.isAdmin()) {
            return ApiResult.fail("无权限访问");
        }
        
        Page<Message> pageParam = new Page<>(page, pageSize);
        IPage<Message> applications = messageService.getPendingCourierApplications(pageParam);
        return ApiResult.success(applications);
    }
} 