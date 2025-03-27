package com.ruralexpress.controller;

import com.ruralexpress.dto.OrderCreateDto;
import com.ruralexpress.dto.OrderFilterDto;
import com.ruralexpress.dto.OrderUpdateDto;
import com.ruralexpress.entity.Order;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.service.OrderService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     * @param createDto 创建订单信息
     * @return 创建结果
     */
    @PostMapping
    public ApiResult<Order> createOrder(@Validated @RequestBody OrderCreateDto createDto) {
        log.info("创建订单请求: 用户ID={}", createDto.getUserId());
        
        try {
            Order order = orderService.createOrder(createDto);
            return ApiResult.success(order);
        } catch (BusinessException e) {
            log.warn("创建订单失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("创建订单过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("创建订单失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ApiResult<Order> getOrder(@PathVariable Long id) {
        log.info("获取订单详情请求: ID={}", id);
        
        try {
            Order order = orderService.findById(id);
            if (order == null) {
                return ApiResult.error(404, "订单不存在");
            }
            return ApiResult.success(order);
        } catch (Exception e) {
            log.error("获取订单详情过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("获取订单详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的订单列表
     * @param userId 用户ID
     * @param status 订单状态(可选)
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    @GetMapping("/user/{userId}")
    public ApiResult<Map<String, Object>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取用户订单列表请求: 用户ID={}, 状态={}, 页码={}, 大小={}", userId, status, page, size);
        
        try {
            OrderFilterDto filterDto = new OrderFilterDto();
            filterDto.setUserId(userId);
            filterDto.setStatus(status);
            filterDto.setPage(page);
            filterDto.setSize(size);
            
            Map<String, Object> result = orderService.findUserOrders(filterDto);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取用户订单列表过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("获取订单列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取快递员的订单列表
     * @param courierId 快递员ID
     * @param status 订单状态(可选)
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    @GetMapping("/courier/{courierId}")
    public ApiResult<Map<String, Object>> getCourierOrders(
            @PathVariable Long courierId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取快递员订单列表请求: 快递员ID={}, 状态={}, 页码={}, 大小={}", courierId, status, page, size);
        
        try {
            OrderFilterDto filterDto = new OrderFilterDto();
            filterDto.setCourierId(courierId);
            filterDto.setStatus(status);
            filterDto.setPage(page);
            filterDto.setSize(size);
            
            Map<String, Object> result = orderService.findCourierOrders(filterDto);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取快递员订单列表过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("获取订单列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取待接单列表
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离(公里)
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    @GetMapping("/pending")
    public ApiResult<Map<String, Object>> getPendingOrders(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5") Integer distance,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取待接单列表请求: 经度={}, 纬度={}, 距离={}km, 页码={}, 大小={}", 
                longitude, latitude, distance, page, size);
        
        try {
            OrderFilterDto filterDto = new OrderFilterDto();
            filterDto.setLongitude(longitude);
            filterDto.setLatitude(latitude);
            filterDto.setDistance(distance);
            filterDto.setStatus(0); // 待接单状态
            filterDto.setPage(page);
            filterDto.setSize(size);
            
            Map<String, Object> result = orderService.findPendingOrders(filterDto);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取待接单列表过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("获取待接单列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 快递员接单
     * @param id 订单ID
     * @param courierId 快递员ID
     * @return 接单结果
     */
    @PutMapping("/{id}/accept")
    public ApiResult<Order> acceptOrder(@PathVariable Long id, @RequestParam Long courierId) {
        log.info("快递员接单请求: 订单ID={}, 快递员ID={}", id, courierId);
        
        try {
            Order order = orderService.acceptOrder(id, courierId);
            return ApiResult.success(order);
        } catch (BusinessException e) {
            log.warn("快递员接单失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("快递员接单过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("接单失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResult<Order> updateOrderStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新订单状态请求: 订单ID={}, 状态={}", id, status);
        
        try {
            Order order = orderService.updateOrderStatus(id, status);
            return ApiResult.success(order);
        } catch (BusinessException e) {
            log.warn("更新订单状态失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新订单状态过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("更新状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消订单
     * @param id 订单ID
     * @param reason 取消原因
     * @return 取消结果
     */
    @PutMapping("/{id}/cancel")
    public ApiResult<Order> cancelOrder(@PathVariable Long id, @RequestParam String reason) {
        log.info("取消订单请求: 订单ID={}, 原因={}", id, reason);
        
        try {
            Order order = orderService.cancelOrder(id, reason);
            return ApiResult.success(order);
        } catch (BusinessException e) {
            log.warn("取消订单失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("取消订单过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("取消订单失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新订单信息
     * @param id 订单ID
     * @param updateDto 更新信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Order> updateOrder(@PathVariable Long id, @Validated @RequestBody OrderUpdateDto updateDto) {
        log.info("更新订单信息请求: 订单ID={}", id);
        
        try {
            Order order = orderService.updateOrder(id, updateDto);
            return ApiResult.success(order);
        } catch (BusinessException e) {
            log.warn("更新订单信息失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新订单信息过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("更新订单信息失败: " + e.getMessage());
        }
    }
} 