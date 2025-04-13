package com.ruralexpress.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruralexpress.dto.PaymentRequestDto;
import com.ruralexpress.entity.Payment;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.service.PaymentService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * 创建支付订单
     * @param requestDto 支付请求信息
     * @return 支付参数
     */
    @PostMapping("/pay")
    public ApiResult<Map<String, Object>> pay(@RequestBody PaymentRequestDto requestDto) {
        log.info("支付请求: {}", requestDto);
        
        try {
            Map<String, Object> paymentParams = paymentService.createPayment(requestDto);
            return ApiResult.success(paymentParams);
        } catch (BusinessException e) {
            log.warn("支付失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("支付过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("支付失败: " + e.getMessage());
        }
    }
    
    /**
     * 支付回调
     * @param paymentNo 支付流水号
     * @param status 支付状态(1-成功,0-失败)
     * @return 处理结果
     */
    @PostMapping("/callback")
    public ApiResult<Payment> callback(
            @RequestParam String paymentNo,
            @RequestParam Integer status) {
        log.info("支付回调: paymentNo={}, status={}", paymentNo, status);
        
        try {
            boolean success = status != null && status == 1;
            Payment payment = paymentService.handlePaymentCallback(paymentNo, success);
            return ApiResult.success(payment);
        } catch (Exception e) {
            log.error("处理支付回调异常: {}", e.getMessage(), e);
            return ApiResult.serverError("处理支付回调失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询支付状态
     * @param orderId 订单ID
     * @return 支付状态
     */
    @GetMapping("/status/{orderId}")
    public ApiResult<Integer> getPaymentStatus(@PathVariable Long orderId) {
        log.info("查询支付状态: orderId={}", orderId);
        
        try {
            Integer status = paymentService.getPaymentStatus(orderId);
            return ApiResult.success(status);
        } catch (Exception e) {
            log.error("查询支付状态异常: {}", e.getMessage(), e);
            return ApiResult.serverError("查询支付状态失败: " + e.getMessage());
        }
    }

    /**
     * 取消支付
     * @param orderId 订单ID
     * @return 取消结果
     */
    @PutMapping("/cancel/{orderId}")
    public ApiResult<Boolean> cancelPayment(@PathVariable Long orderId) {
        log.info("取消支付请求: orderId={}", orderId);
        
        try {
            // 根据订单ID查询未支付的支付记录
            LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Payment::getOrderId, orderId)
                    .eq(Payment::getStatus, 0) // 未支付状态
                    .orderByDesc(Payment::getCreatedAt)
                    .last("LIMIT 1");
            
            Payment payment = paymentService.getOne(queryWrapper);
            if (payment == null) {
                return ApiResult.error(404, "未找到待支付的记录");
            }
            
            // 更新支付状态为已取消(2)
            payment.setStatus(2);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentService.updateById(payment);
            
            return ApiResult.success(true);
        } catch (Exception e) {
            log.error("取消支付异常: {}", e.getMessage(), e);
            return ApiResult.serverError("取消支付失败: " + e.getMessage());
        }
    }

    /**
     * 简单测试端点
     * @return 字符串
     */
    @GetMapping("/test")
    public ApiResult<String> test() {
        log.info("测试支付API");
        return ApiResult.success("支付API测试成功");
    }

    /**
     * 模拟支付成功（开发环境使用）
     * @param orderId 订单ID
     * @return 处理结果
     */
    @PostMapping("/mock/success/{orderId}")
    public ApiResult<Payment> mockPaymentSuccess(@PathVariable Long orderId) {
        log.info("模拟支付成功请求: orderId={}", orderId);
        
        try {
            // 查询订单对应的未支付记录
            LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Payment::getOrderId, orderId)
                    .eq(Payment::getStatus, 0) // 未支付状态
                    .orderByDesc(Payment::getCreatedAt)
                    .last("LIMIT 1");
            
            Payment payment = paymentService.getOne(queryWrapper);
            if (payment == null) {
                return ApiResult.error(404, "未找到待支付的记录");
            }
            
            // 更新支付记录为已支付
            payment = paymentService.handlePaymentCallback(payment.getPaymentNo(), true);
            return ApiResult.success(payment);
        } catch (Exception e) {
            log.error("模拟支付失败: {}", e.getMessage(), e);
            return ApiResult.serverError("模拟支付失败: " + e.getMessage());
        }
    }
} 