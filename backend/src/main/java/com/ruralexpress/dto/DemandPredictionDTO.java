package com.ruralexpress.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用工需求预测数据传输对象
 */
@Data
public class DemandPredictionDTO {
    
    /**
     * 预测日期
     */
    private LocalDate date;
    
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
     * 当前可用快递员数量
     */
    private Integer availableCouriers;
    
    /**
     * 预测值的上界
     */
    private Double upperBound;
    
    /**
     * 预测值的下界
     */
    private Double lowerBound;
    
    /**
     * 预测准确度分数(0-100)
     */
    private Double accuracyScore;
} 