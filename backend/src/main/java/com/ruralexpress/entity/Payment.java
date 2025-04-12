package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体类
 */
@Data
@TableName("t_payment")
public class Payment {
    
    /**
     * 支付ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 支付流水号
     */
    private String paymentNo;
    
    /**
     * 支付渠道(wxpay-微信支付,alipay-支付宝,balance-余额支付)
     */
    private String channel;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
    
    /**
     * 状态(0-未支付,1-已支付,2-已退款)
     */
    private Integer status;
    
    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 