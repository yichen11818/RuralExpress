package com.ruralexpress.dto;

import lombok.Data;

/**
 * 订单过滤条件数据传输对象
 */
@Data
public class OrderFilterDto {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 快递员ID
     */
    private Long courierId;
    
    /**
     * 订单状态
     */
    private Integer status;
    
    /**
     * 包裹类型
     */
    private Integer packageType;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 距离(公里)
     */
    private Integer distance;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 起始日期
     */
    private String startDate;
    
    /**
     * 结束日期
     */
    private String endDate;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 排序字段
     */
    private String sortField;
    
    /**
     * 排序方向(asc/desc)
     */
    private String sortDirection;
    
    /**
     * 获取起始索引
     */
    public int getOffset() {
        return (page - 1) * size;
    }
} 