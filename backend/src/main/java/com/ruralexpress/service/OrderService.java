package com.ruralexpress.service;

import com.ruralexpress.dto.OrderCreateDto;
import com.ruralexpress.dto.OrderFilterDto;
import com.ruralexpress.dto.OrderUpdateDto;
import com.ruralexpress.entity.Order;

import java.util.List;
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
    
    /**
     * 根据关键词搜索订单
     * @param keyword 搜索关键词
     * @param limit 限制返回数量
     * @return 订单列表
     */
    List<Map<String, Object>> searchOrders(String keyword, Integer limit);
    
    /**
     * 查询管理员订单列表
     * @param filterDto 过滤条件
     * @return 订单列表和总数
     */
    Map<String, Object> findOrdersForAdmin(OrderFilterDto filterDto);
    
    /**
     * 管理员查询订单详情
     * @param id 订单ID
     * @return 订单详情（包含更多信息）
     */
    Order findOrderDetailForAdmin(Long id);
    
    /**
     * 管理员更新订单状态
     * @param id 订单ID
     * @param status 新状态
     * @return 更新后的订单
     */
    Order updateOrderStatusByAdmin(Long id, Integer status);
    
    /**
     * 获取订单统计信息
     * @return 统计信息
     */
    Map<String, Object> getOrderStatistics();
    
    /**
     * 导出订单数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 订单状态
     * @return 导出文件URL
     */
    String exportOrders(String startDate, String endDate, Integer status);
    
    /**
     * 批量删除订单
     * @param ids 订单ID数组
     */
    void batchDeleteOrders(Long[] ids);
} 