package com.ruralexpress.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求数据传输对象
 */
@Data
public class PaymentRequestDto {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 支付金额
     */
    private BigDecimal amount;
} 