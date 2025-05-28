package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@TableName("t_message")
public class Message {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型(0-系统消息,1-快递员申请,2-订单通知)
     */
    private Integer type;
    
    /**
     * 状态(0-未读,1-已读)
     */
    private Integer status;
    
    /**
     * 关联ID
     */
    private Long targetId;
    
    /**
     * 接收者用户ID
     */
    private Long receiverId;
    
    /**
     * 发送者用户ID
     */
    private Long senderId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 