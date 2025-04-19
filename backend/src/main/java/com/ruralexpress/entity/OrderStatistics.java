package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单统计实体类
 * 用于存储每日订单统计数据，支持需求预测分析
 */
@Data
@TableName("t_order_statistics")
public class OrderStatistics {
    
    /**
     * 统计记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 统计日期
     */
    private LocalDate statisticsDate;
    
    /**
     * 区域
     */
    private String area;
    
    /**
     * 该区域当天订单总数
     */
    private Integer orderCount;
    
    /**
     * 该区域活跃快递员数量
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
     * 记录创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 记录更新时间
     */
    private LocalDateTime updatedAt;
} 