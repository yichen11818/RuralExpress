package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.dto.OrderFilterDto;
import com.ruralexpress.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 根据关键词搜索订单
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 订单列表
     */
    @Select("SELECT * FROM t_order WHERE order_no LIKE CONCAT('%', #{keyword}, '%') " +
           "OR sender_name LIKE CONCAT('%', #{keyword}, '%') " +
           "OR sender_phone LIKE CONCAT('%', #{keyword}, '%') " +
           "OR sender_address LIKE CONCAT('%', #{keyword}, '%') " +
           "OR receiver_name LIKE CONCAT('%', #{keyword}, '%') " +
           "OR receiver_phone LIKE CONCAT('%', #{keyword}, '%') " +
           "OR receiver_address LIKE CONCAT('%', #{keyword}, '%') " +
           "ORDER BY created_at DESC LIMIT #{limit}")
    List<Order> searchOrders(@Param("keyword") String keyword, @Param("limit") Integer limit);
    
    /**
     * 管理员查询订单列表
     * @param filterDto 过滤条件
     * @return 订单列表
     */
    List<Order> findOrdersForAdmin(@Param("filter") OrderFilterDto filterDto);
    
    /**
     * 管理员查询订单总数
     * @param filterDto 过滤条件
     * @return 订单总数
     */
    int countOrdersForAdmin(@Param("filter") OrderFilterDto filterDto);
    
    /**
     * 根据ID查询订单详情（包含关联信息）
     * @param id 订单ID
     * @return 订单详情
     */
    Order findOrderDetailById(@Param("id") Long id);
    
    /**
     * 统计今日订单数
     * @return 今日订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE DATE(created_at) = CURDATE()")
    int countTodayOrders();
    
    /**
     * 统计今日完成订单数
     * @return 今日完成订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE DATE(created_at) = CURDATE() AND status = 6")
    int countTodayCompletedOrders();
    
    /**
     * 统计本月订单数
     * @return 本月订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())")
    int countMonthOrders();
    
    /**
     * 统计本月完成订单数
     * @return 本月完成订单数
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE()) AND status = 6")
    int countMonthCompletedOrders();
    
    /**
     * 统计各状态订单数
     * @return 各状态订单数
     */
    @Select("SELECT status, COUNT(*) as count FROM t_order GROUP BY status")
    List<Map<String, Object>> countOrdersByStatus();
    
    /**
     * 统计各类型订单数
     * @return 各类型订单数
     */
    @Select("SELECT package_type, COUNT(*) as count FROM t_order GROUP BY package_type")
    List<Map<String, Object>> countOrdersByType();
    
    /**
     * 查询符合条件的订单用于导出
     * @param filterDto 过滤条件
     * @return 订单列表
     */
    List<Order> findOrdersForExport(@Param("filter") OrderFilterDto filterDto);
} 