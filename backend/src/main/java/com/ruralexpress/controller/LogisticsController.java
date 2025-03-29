package com.ruralexpress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ruralexpress.service.LogisticsService;
import java.util.*;

/**
 * 物流信息控制器
 */
@RestController
@RequestMapping("/order")
public class LogisticsController {

    @Autowired
    private LogisticsService logisticsService;

    /**
     * 获取物流信息
     *
     * @param trackingNo 运单号
     * @return 响应结果
     */
    @GetMapping("/logistics")
    public ResponseEntity<Map<String, Object>> getLogisticsInfo(@RequestParam String trackingNo) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用物流服务获取物流信息
            Map<String, Object> data = logisticsService.getLogisticsInfo(trackingNo);
            
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "获取物流信息成功");
            result.put("data", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "获取物流信息失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
} 