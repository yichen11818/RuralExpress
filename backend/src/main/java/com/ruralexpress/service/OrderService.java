package com.ruralexpress.service;

import com.ruralexpress.dto.OrderCreateDto;
import com.ruralexpress.dto.OrderFilterDto;
import com.ruralexpress.dto.OrderUpdateDto;
import com.ruralexpress.entity.Order;

import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param createDto 创建订单信息
     * @return 创建的订单
     */
    Order createOrder(OrderCreateDto createDto);
    
    /**
     * 根据ID查找订单
     * @param id 订单ID
     * @return 订单信息
     */
    Order findById(Long id);
    
    /**
     * 查询用户订单
     * @param filterDto 过滤条件
     * @return 订单列表和总数
     */
    Map<String, Object> findUserOrders(OrderFilterDto filterDto);
    
    /**
     * 查询快递员订单
     * @param filterDto 过滤条件
     * @return 订单列表和总数
     */
    Map<String, Object> findCourierOrders(OrderFilterDto filterDto);
    
    /**
     * 查询待接单列表
     * @param filterDto 过滤条件
     * @return 订单列表和总数
     */
    Map<String, Object> findPendingOrders(OrderFilterDto filterDto);
    
    /**
     * 快递员接单
     * @param id 订单ID
     * @param courierId 快递员ID
     * @return 更新后的订单
     */
    Order acceptOrder(Long id, Long courierId);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 更新后的订单
     */
    Order updateOrderStatus(Long id, Integer status);
    
    /**
     * 取消订单
     * @param id 订单ID
     * @param reason 取消原因
     * @return 更新后的订单
     */
    Order cancelOrder(Long id, String reason);
    
    /**
     * 更新订单信息
     * @param id 订单ID
     * @param updateDto 更新信息
     * @return 更新后的订单
     */
    Order updateOrder(Long id, OrderUpdateDto updateDto);
} 