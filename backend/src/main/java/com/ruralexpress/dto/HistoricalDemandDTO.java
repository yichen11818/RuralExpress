package com.ruralexpress.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 历史需求数据传输对象
 */
@Data
public class HistoricalDemandDTO {
    
    /**
     * 日期
     */
    private LocalDate date;
    
    /**
     * 区域
     */
    private String area;
    
    /**
     * 订单数量
     */
    private Integer orderCount;
    
    /**
     * 活跃快递员数量
     */
    private Integer activeCouriers;
    
    /**
     * 平均订单处理时间（分钟）
     */
    private Double avgProcessTime;
    
    /**
     * 是否工作日
     */
    private Boolean isWorkday;
    
    /**
     * 是否节假日
     */
    private Boolean isHoliday;
    
    /**
     * 天气状况编码
     */
    private Integer weatherCode;
    
    /**
     * 需求趋势（同比去年增长率）
     */
    private Double yearOverYearTrend;
} 