package com.ruralexpress.service;

import java.util.Map;

/**
 * 物流信息服务接口
 */
public interface LogisticsService {
    
    /**
     * 获取物流信息
     * 
     * @param trackingNo 运单号
     * @return 物流信息
     */
    Map<String, Object> getLogisticsInfo(String trackingNo);
    
    /**
     * 通过订单ID获取物流信息
     * 
     * @param orderId 订单ID
     * @return 物流信息
     */
    Map<String, Object> getLogisticsInfoByOrderId(Long orderId);
    
    /**
     * 获取用户的物流追踪列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 物流追踪列表
     */
    Map<String, Object> getTrackingList(Long userId, Integer page, Integer pageSize);
} 