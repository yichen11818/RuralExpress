package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 需求预测实体类
 * 用于存储各区域未来日期的需求预测结果
 */
@Data
@TableName("t_demand_prediction")
public class DemandPrediction {
    
    /**
     * 预测记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 预测日期
     */
    private LocalDate predictionDate;
    
    /**
     * 区域
     */
    private String area;
    
    /**
     * 预测订单数量
     */
    private Double predictedOrders;
    
    /**
     * 预测所需快递员数量
     */
    private Integer predictedCouriers;
    
    /**
     * 预测值的上界
     */
    private Double upperBound;
    
    /**
     * 预测值的下界
     */
    private Double lowerBound;
    
    /**
     * 预测准确度评分
     */
    private Double accuracyScore;
    
    /**
     * 预测模型版本
     */
    private String modelVersion;
    
    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 记录更新时间
     */
    private LocalDateTime updatedAt;
} 