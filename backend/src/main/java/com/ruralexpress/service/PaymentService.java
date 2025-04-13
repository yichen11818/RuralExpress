package com.ruralexpress.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruralexpress.dto.PaymentRequestDto;
import com.ruralexpress.entity.Payment;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService extends IService<Payment> {
    
    /**
     * 创建支付订单
     * @param requestDto 支付请求信息
     * @return 支付参数
     */
    Map<String, Object> createPayment(PaymentRequestDto requestDto);
    
    /**
     * 处理支付回调
     * @param paymentNo 支付流水号
     * @param success 是否成功
     * @return 更新后的支付记录
     */
    Payment handlePaymentCallback(String paymentNo, boolean success);
    
    /**
     * 查询支付状态
     * @param orderId 订单ID
     * @return 支付状态
     */
    Integer getPaymentStatus(Long orderId);
} 