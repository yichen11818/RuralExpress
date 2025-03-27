package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.dto.OrderFilterDto;
import com.ruralexpress.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 查询用户订单列表
     * @param filterDto 过滤条件
     * @return 订单列表
     */
    List<Order> findUserOrders(@Param("filter") OrderFilterDto filterDto);
    
    /**
     * 查询用户订单总数
     * @param filterDto 过滤条件
     * @return 订单总数
     */
    int countUserOrders(@Param("filter") OrderFilterDto filterDto);
    
    /**
     * 查询快递员订单列表
     * @param filterDto 过滤条件
     * @return 订单列表
     */
    List<Order> findCourierOrders(@Param("filter") OrderFilterDto filterDto);
    
    /**
     * 查询快递员订单总数
     * @param filterDto 过滤条件
     * @return 订单总数
     */
    int countCourierOrders(@Param("filter") OrderFilterDto filterDto);
    
    /**
     * 查询待接单列表
     * @param filterDto 过滤条件
     * @return 订单列表
     */
    List<Order> findPendingOrders(@Param("filter") OrderFilterDto filterDto);
    
    /**
     * 查询待接单总数
     * @param filterDto 过滤条件
     * @return 订单总数
     */
    int countPendingOrders(@Param("filter") OrderFilterDto filterDto);
} 