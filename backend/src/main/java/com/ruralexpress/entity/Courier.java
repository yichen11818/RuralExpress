package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 快递员实体类
 */
@Data
@TableName("t_courier")
public class Courier {
    
    /**
     * 快递员ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 服务区域
     */
    private String serviceArea;
    
    /**
     * 服务状态(0-休息,1-接单中)
     */
    private Integer serviceStatus;
    
    /**
     * 审核状态(0-待审核,1-已通过,2-未通过)
     */
    private Integer auditStatus;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 账户余额
     */
    private BigDecimal balance;
    
    /**
     * 已完成订单数
     */
    private Integer completedOrders;
    
    /**
     * 身份证正面照片URL
     */
    private String idCardFront;
    
    /**
     * 身份证背面照片URL
     */
    private String idCardBack;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 工作开始时间
     */
    private String workStartTime;
    
    /**
     * 工作结束时间
     */
    private String workEndTime;
    
    /**
     * 交通工具
     */
    private String vehicle;
    
    /**
     * 简介
     */
    private String introduction;
    
    /**
     * 平均响应时间（分钟）
     */
    private Integer responseTime;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 距离（单位：公里），计算所得，非数据库字段
     */
    @TableField(exist = false)
    private Double distance;
    
    // Getter and Setter for latitude
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    // Getter and Setter for longitude
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    // Getter and Setter for distance
    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
} 