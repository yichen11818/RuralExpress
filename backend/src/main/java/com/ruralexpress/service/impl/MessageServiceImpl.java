package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Message;
import com.ruralexpress.mapper.MessageMapper;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Long sendMessage(Message message) {
        message.setStatus(0); // 设置为未读
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        return message.getId();
    }

    @Override
    public int getUnreadMessageCount(Long userId) {
        return messageMapper.getUnreadMessageCount(userId);
    }

    @Override
    @Transactional
    public boolean markMessageAsRead(Long messageId, Long userId) {
        return messageMapper.markMessageAsRead(messageId, userId);
    }

    @Override
    public IPage<Message> getUserMessages(Page<Message> page, Long userId) {
        return messageMapper.getUserMessages(page, userId);
    }

    @Override
    public IPage<Message> getPendingCourierApplications(Page<Message> page) {
        return messageMapper.getPendingCourierApplications(page);
    }

    @Override
    @Transactional
    public boolean sendCourierApplicationMessage(Long courierId, Long userId, String userName) {
        // 查找所有管理员用户
        List<Long> adminIds = userMapper.findAdminUserIds();
        if (adminIds.isEmpty()) {
            return false;
        }
        
        // 给每个管理员发送消息
        for (Long adminId : adminIds) {
            Message message = new Message();
            message.setTitle("新快递员申请");
            message.setContent("用户 " + userName + " 申请成为快递员，请及时审核");
            message.setType(1); // 快递员申请
            message.setStatus(0); // 未读
            message.setTargetId(courierId); // 关联快递员ID
            message.setReceiverId(adminId); // 管理员ID
            message.setSenderId(userId); // 申请用户ID
            message.setCreatedAt(LocalDateTime.now());
            message.setUpdatedAt(LocalDateTime.now());
            
            messageMapper.insert(message);
        }
        
        return true;
    }
} 