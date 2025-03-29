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
} 