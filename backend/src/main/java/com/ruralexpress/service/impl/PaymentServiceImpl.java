package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruralexpress.dto.PaymentRequestDto;
import com.ruralexpress.entity.Order;
import com.ruralexpress.entity.Payment;
import com.ruralexpress.entity.User;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.mapper.OrderMapper;
import com.ruralexpress.mapper.PaymentMapper;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public Map<String, Object> createPayment(PaymentRequestDto requestDto) {
        log.info("创建支付记录: {}", requestDto);
        
        // 查询订单信息
        Order order = orderMapper.selectById(requestDto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 查询用户信息
        User user = userMapper.selectById(order.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查订单是否已支付
        LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payment::getOrderId, order.getId())
                 .eq(Payment::getStatus, 1); // 已支付状态
        
        Long count = paymentMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("订单已支付，请勿重复支付");
        }
        
        // 生成支付记录
        Payment payment = new Payment();
        payment.setUserId(order.getUserId());
        payment.setOrderId(order.getId());
        payment.setPaymentNo(generatePaymentNo());
        payment.setChannel(requestDto.getPaymentMethod());
        payment.setAmount(requestDto.getAmount());
        payment.setStatus(0); // 未支付
        
        LocalDateTime now = LocalDateTime.now();
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        
        paymentMapper.insert(payment);
        
        log.info("支付记录创建成功: ID={}, 支付流水号={}", payment.getId(), payment.getPaymentNo());
        
        // 构建支付参数（实际项目中需要调用支付接口获取参数）
        Map<String, Object> paymentParams = new HashMap<>();
        
        // 根据不同支付方式生成不同支付参数
        switch (payment.getChannel()) {
            case "wxpay":
                // 此处应当调用微信支付接口获取支付参数
                paymentParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                paymentParams.put("nonceStr", generateNonceStr());
                paymentParams.put("package", "prepay_id=" + generatePrepayId());
                paymentParams.put("signType", "MD5");
                paymentParams.put("paySign", "模拟签名");
                break;
                
            case "alipay":
                // 此处应当调用支付宝接口获取支付参数
                paymentParams.put("tradeNO", payment.getPaymentNo());
                paymentParams.put("orderStr", "模拟支付宝支付信息");
                break;
                
            case "balance":
                // 余额支付逻辑
                // 实际项目中应检查用户余额是否足够，并扣减余额
                paymentParams.put("success", true);
                break;
                
            default:
                throw new BusinessException("不支持的支付方式: " + payment.getChannel());
        }
        
        return paymentParams;
    }
    
    @Override
    @Transactional
    public Payment handlePaymentCallback(String paymentNo, boolean success) {
        // 根据支付流水号查询支付记录
        LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payment::getPaymentNo, paymentNo);
        
        Payment payment = paymentMapper.selectOne(queryWrapper);
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }
        
        // 如果已经是终态，则不再处理
        if (payment.getStatus() == 1 || payment.getStatus() == 2) {
            return payment;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 更新支付状态
        if (success) {
            payment.setStatus(1); // 已支付
            payment.setPaymentTime(now);
            
            // 更新订单状态（实际业务中可根据需求处理）
            Order order = orderMapper.selectById(payment.getOrderId());
            if (order != null && order.getStatus() == 0) {
                order.setStatus(1); // 更新订单状态为已支付/已接单
                order.setUpdatedAt(now);
                orderMapper.updateById(order);
            }
        } else {
            payment.setStatus(2); // 支付失败/已退款
        }
        
        payment.setUpdatedAt(now);
        paymentMapper.updateById(payment);
        
        return payment;
    }
    
    @Override
    public Integer getPaymentStatus(Long orderId) {
        // 查询订单的支付状态
        LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payment::getOrderId, orderId)
                 .orderByDesc(Payment::getCreatedAt)
                 .last("LIMIT 1");
        
        Payment payment = paymentMapper.selectOne(queryWrapper);
        return payment != null ? payment.getStatus() : null;
    }
    
    /**
     * 生成支付流水号
     */
    private String generatePaymentNo() {
        // 格式: PAY + 年月日时分秒 + 4位随机数
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return "PAY" + dateStr + randomStr;
    }
    
    /**
     * 生成随机字符串
     */
    private String generateNonceStr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * 生成预支付ID
     */
    private String generatePrepayId() {
        // 模拟生成预支付ID
        return "wx" + System.currentTimeMillis() + "simulate";
    }
} 