package com.ruralexpress.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Message;

/**
 * 系统消息服务接口
 */
public interface MessageService {
    
    /**
     * 发送消息
     * @param message 消息内容
     * @return 消息ID
     */
    Long sendMessage(Message message);
    
    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadMessageCount(Long userId);
    
    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markMessageAsRead(Long messageId, Long userId);
    
    /**
     * 获取用户消息列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 消息列表
     */
    IPage<Message> getUserMessages(Page<Message> page, Long userId);
    
    /**
     * 获取管理员未处理的快递员申请消息
     * @param page 分页参数
     * @return 申请消息列表
     */
    IPage<Message> getPendingCourierApplications(Page<Message> page);
    
    /**
     * 发送快递员申请消息给管理员
     * @param courierId 快递员ID
     * @param userId 申请用户ID
     * @param userName 申请用户名称
     * @return 是否成功
     */
    boolean sendCourierApplicationMessage(Long courierId, Long userId, String userName);
} 