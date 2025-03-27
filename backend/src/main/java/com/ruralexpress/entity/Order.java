package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("t_order")
public class Order {
    
    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 快递员ID
     */
    private Long courierId;
    
    /**
     * 寄件人姓名
     */
    private String senderName;
    
    /**
     * 寄件人手机号
     */
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
    private String receiverPhone;
    
    /**
     * 收件地址
     */
    private String receiverAddress;
    
    /**
     * 包裹类型(0-小件,1-中件,2-大件)
     */
    private Integer packageType;
    
    /**
     * 重量(kg)
     */
    private BigDecimal weight;
    
    /**
     * 配送费
     */
    private BigDecimal price;
    
    /**
     * 订单状态(0-待接单,1-已接单,2-取件中,3-已取件,4-配送中,5-已送达,6-已完成,7-已取消)
     */
    private Integer status;
    
    /**
     * 期望取件时间
     */
    private LocalDateTime expectedPickupTime;
    
    /**
     * 实际取件时间
     */
    private LocalDateTime actualPickupTime;
    
    /**
     * 期望送达时间
     */
    private LocalDateTime expectedDeliveryTime;
    
    /**
     * 实际送达时间
     */
    private LocalDateTime actualDeliveryTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 