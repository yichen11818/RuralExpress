package com.ruralexpress.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 订单更新数据传输对象
 */
@Data
public class OrderUpdateDto {
    
    /**
     * 寄件人姓名
     */
    private String senderName;
    
    /**
     * 寄件人手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "寄件人手机号格式不正确")
    private String senderPhone;
    
    /**
     * 寄件地址
     */
    private String senderAddress;
    
    /**
     * 收件人姓名
     */
    private String receiverName;
    
    /**
     * 收件人手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "收件人手机号格式不正确")
    private String receiverPhone;
    
    /**
     * 收件地址
     */
    private String receiverAddress;
    
    /**
     * 期望取件时间
     */
    private LocalDateTime expectedPickupTime;
    
    /**
     * 期望送达时间
     */
    private LocalDateTime expectedDeliveryTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 寄件地址经度
     */
    private Double senderLongitude;
    
    /**
     * 寄件地址纬度
     */
    private Double senderLatitude;
    
    /**
     * 收件地址经度
     */
    private Double receiverLongitude;
    
    /**
     * 收件地址纬度
     */
    private Double receiverLatitude;
} 