package com.ruralexpress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ruralexpress.service.LogisticsService;
import com.ruralexpress.utils.JwtTokenUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 物流信息控制器
 */
@RestController
@RequestMapping("/order")
public class LogisticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(LogisticsController.class);

    @Autowired
    private LogisticsService logisticsService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        
        logger.info("接收到物流追踪列表请求: page={}, pageSize={}", page, pageSize);
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从认证用户信息获取用户ID
            Long userId = getUserIdFromRequest(request);
            logger.info("从JWT令牌获取的用户ID: {}", userId);
            
            if (userId == null) {
                userId = 1L; // 如果无法获取用户ID，默认使用1
                logger.info("无法获取用户ID，使用默认ID: {}", userId);
            }
            
            // 调用物流服务获取物流追踪列表
            logger.info("调用服务获取物流追踪列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
            Map<String, Object> data = logisticsService.getTrackingList(userId, page, pageSize);
            logger.info("服务返回数据: {}", data);
            
            result.put("code", 200);
            result.put("success", true);
            result.put("message", "获取物流追踪列表成功");
            result.put("data", data);
            
            logger.info("响应结果准备完成");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("获取物流追踪列表失败", e);
            result.put("code", 500);
            result.put("success", false);
            result.put("message", "获取物流追踪列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            // 从请求头获取JWT令牌
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtTokenUtil.getUserIdFromToken(token);
            }
            return null;
        } catch (Exception e) {
            logger.error("从请求获取用户ID失败", e);
            return null;
        }
    }
} 