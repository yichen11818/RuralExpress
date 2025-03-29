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
    
    /**
     * 获取用户的物流追踪列表
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 物流追踪列表
     */
    @GetMapping("/tracking/list")
    public ResponseEntity<Map<String, Object>> getTrackingList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从请求中获取用户ID，这里假设是从认证信息中获取
            Long userId = 1L; // TODO: 获取当前登录用户ID
            
            // 调用物流服务获取物流追踪列表
            Map<String, Object> data = logisticsService.getTrackingList(userId, page, pageSize);
            
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "获取物流追踪列表成功");
            result.put("data", data);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "获取物流追踪列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
} 