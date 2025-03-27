package com.ruralexpress.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单创建数据传输对象
 */
@Data
public class OrderCreateDto {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 寄件人姓名
     */
    @NotBlank(message = "寄件人姓名不能为空")
    private String senderName;
    
    /**
     * 寄件人手机号
     */
    @NotBlank(message = "寄件人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "寄件人手机号格式不正确")
    private String senderPhone;
    
    /**
     * 寄件地址
     */
    @NotBlank(message = "寄件地址不能为空")
    private String senderAddress;
    
    /**
     * 收件人姓名
     */
    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;
    
    /**
     * 收件人手机号
     */
    @NotBlank(message = "收件人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "收件人手机号格式不正确")
    private String receiverPhone;
    
    /**
     * 收件地址
     */
    @NotBlank(message = "收件地址不能为空")
    private String receiverAddress;
    
    /**
     * 包裹类型(0-小件,1-中件,2-大件)
     */
    @NotNull(message = "包裹类型不能为空")
    private Integer packageType;
    
    /**
     * 重量(kg)
     */
    private BigDecimal weight;
    
    /**
     * 配送费
     */
    @NotNull(message = "配送费不能为空")
    private BigDecimal price;
    
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