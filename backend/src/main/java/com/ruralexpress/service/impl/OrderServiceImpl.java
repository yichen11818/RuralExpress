package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruralexpress.dto.OrderCreateDto;
import com.ruralexpress.dto.OrderFilterDto;
import com.ruralexpress.dto.OrderUpdateDto;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.Order;
import com.ruralexpress.entity.User;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.mapper.CourierMapper;
import com.ruralexpress.mapper.OrderMapper;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourierMapper courierMapper;
    
    @Override
    @Transactional
    public Order createOrder(OrderCreateDto createDto) {
        // 验证用户是否存在
        User user = userMapper.selectById(createDto.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 创建订单
        Order order = new Order();
        BeanUtils.copyProperties(createDto, order);
        
        // 生成订单编号
        order.setOrderNo(generateOrderNo());
        
        // 设置订单状态为待接单
        order.setStatus(0);
        
        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        
        // 保存订单
        orderMapper.insert(order);
        
        log.info("订单创建成功: ID={}, 订单号={}", order.getId(), order.getOrderNo());
        
        return order;
    }
    
    @Override
    public Order findById(Long id) {
        return orderMapper.selectById(id);
    }
    
    @Override
    public Map<String, Object> findUserOrders(OrderFilterDto filterDto) {
        List<Order> orders = orderMapper.findUserOrders(filterDto);
        int total = orderMapper.countUserOrders(filterDto);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", orders);
        result.put("total", total);
        return result;
    }
    
    @Override
    public Map<String, Object> findCourierOrders(OrderFilterDto filterDto) {
        List<Order> orders = orderMapper.findCourierOrders(filterDto);
        int total = orderMapper.countCourierOrders(filterDto);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", orders);
        result.put("total", total);
        return result;
    }
    
    @Override
    public Map<String, Object> findPendingOrders(OrderFilterDto filterDto) {
        List<Order> orders = orderMapper.findPendingOrders(filterDto);
        int total = orderMapper.countPendingOrders(filterDto);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", orders);
        result.put("total", total);
        return result;
    }
    
    @Override
    @Transactional
    public Order acceptOrder(Long id, Long courierId) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证订单状态是否为待接单
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态错误，无法接单");
        }
        
        // 验证快递员是否存在
        Courier courier = courierMapper.selectById(courierId);
        if (courier == null) {
            throw new BusinessException("快递员不存在");
        }
        
        // 设置快递员ID
        order.setCourierId(courierId);
        
        // 更新订单状态为已接单
        order.setStatus(1);
        
        // 更新时间
        order.setUpdatedAt(LocalDateTime.now());
        
        // 更新订单
        orderMapper.updateById(order);
        
        log.info("订单接单成功: ID={}, 订单号={}, 快递员ID={}", order.getId(), order.getOrderNo(), courierId);
        
        return order;
    }
    
    @Override
    @Transactional
    public Order updateOrderStatus(Long id, Integer status) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证状态合法性
        if (status < 0 || status > 7) {
            throw new BusinessException("订单状态不合法");
        }
        
        // 验证状态流转的合法性
        if (!isValidStatusTransition(order.getStatus(), status)) {
            throw new BusinessException("订单状态流转不合法");
        }
        
        // 更新状态
        order.setStatus(status);
        
        // 如果状态是取件中，设置取件时间
        if (status == 2) {
            // 取件中，不设置实际取件时间
        } else if (status == 3) {
            // 已取件，设置实际取件时间
            order.setActualPickupTime(LocalDateTime.now());
        } else if (status == 5) {
            // 已送达，设置实际送达时间
            order.setActualDeliveryTime(LocalDateTime.now());
        }
        
        // 更新时间
        order.setUpdatedAt(LocalDateTime.now());
        
        // 更新订单
        orderMapper.updateById(order);
        
        log.info("订单状态更新成功: ID={}, 订单号={}, 状态={}", order.getId(), order.getOrderNo(), status);
        
        return order;
    }
    
    @Override
    @Transactional
    public Order cancelOrder(Long id, String reason) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证订单状态是否可取消
        if (order.getStatus() > 3) {
            throw new BusinessException("订单已在配送中或已完成，无法取消");
        }
        
        // 设置取消原因
        order.setCancelReason(reason);
        
        // 更新订单状态为已取消
        order.setStatus(7);
        
        // 更新时间
        order.setUpdatedAt(LocalDateTime.now());
        
        // 更新订单
        orderMapper.updateById(order);
        
        log.info("订单取消成功: ID={}, 订单号={}, 原因={}", order.getId(), order.getOrderNo(), reason);
        
        return order;
    }
    
    @Override
    @Transactional
    public Order updateOrder(Long id, OrderUpdateDto updateDto) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证订单状态是否可更新
        if (order.getStatus() > 1) {
            throw new BusinessException("订单已在处理中，无法更新信息");
        }
        
        // 更新订单信息
        if (updateDto.getSenderName() != null) {
            order.setSenderName(updateDto.getSenderName());
        }
        if (updateDto.getSenderPhone() != null) {
            order.setSenderPhone(updateDto.getSenderPhone());
        }
        if (updateDto.getSenderAddress() != null) {
            order.setSenderAddress(updateDto.getSenderAddress());
        }
        if (updateDto.getReceiverName() != null) {
            order.setReceiverName(updateDto.getReceiverName());
        }
        if (updateDto.getReceiverPhone() != null) {
            order.setReceiverPhone(updateDto.getReceiverPhone());
        }
        if (updateDto.getReceiverAddress() != null) {
            order.setReceiverAddress(updateDto.getReceiverAddress());
        }
        if (updateDto.getExpectedPickupTime() != null) {
            order.setExpectedPickupTime(updateDto.getExpectedPickupTime());
        }
        if (updateDto.getExpectedDeliveryTime() != null) {
            order.setExpectedDeliveryTime(updateDto.getExpectedDeliveryTime());
        }
        if (updateDto.getRemark() != null) {
            order.setRemark(updateDto.getRemark());
        }
        if (updateDto.getSenderLongitude() != null) {
            order.setSenderLongitude(updateDto.getSenderLongitude());
        }
        if (updateDto.getSenderLatitude() != null) {
            order.setSenderLatitude(updateDto.getSenderLatitude());
        }
        if (updateDto.getReceiverLongitude() != null) {
            order.setReceiverLongitude(updateDto.getReceiverLongitude());
        }
        if (updateDto.getReceiverLatitude() != null) {
            order.setReceiverLatitude(updateDto.getReceiverLatitude());
        }
        
        // 更新时间
        order.setUpdatedAt(LocalDateTime.now());
        
        // 更新订单
        orderMapper.updateById(order);
        
        log.info("订单信息更新成功: ID={}, 订单号={}", order.getId(), order.getOrderNo());
        
        return order;
    }
    
    /**
     * 生成订单编号
     * 格式：年月日时分秒 + 4位随机数
     * @return 订单编号
     */
    private String generateOrderNo() {
        // 获取当前时间
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // 生成4位随机数
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        String randomStr = String.format("%04d", randomNum);
        
        return timeStr + randomStr;
    }
    
    /**
     * 验证订单状态流转的合法性
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     * @return 是否合法
     */
    private boolean isValidStatusTransition(Integer currentStatus, Integer newStatus) {
        // 已取消状态不能再变更
        if (currentStatus == 7) {
            return false;
        }
        
        // 已完成状态不能再变更
        if (currentStatus == 6) {
            return false;
        }
        
        // 状态只能向前流转，不能回退
        // 特例：任何状态都可以直接取消
        if (newStatus == 7) {
            return true;
        }
        
        // 正常状态流转检查: 只能向前进一步
        return newStatus == currentStatus + 1;
    }
    
    /**
     * 根据关键词搜索订单
     */
    @Override
    public List<Map<String, Object>> searchOrders(String keyword, Integer limit) {
        List<Order> orders = orderMapper.searchOrders(keyword, limit);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("senderName", order.getSenderName());
            map.put("senderPhone", order.getSenderPhone());
            map.put("senderAddress", order.getSenderAddress());
            map.put("receiverName", order.getReceiverName());
            map.put("receiverPhone", order.getReceiverPhone());
            map.put("receiverAddress", order.getReceiverAddress());
            map.put("packageType", order.getPackageType());
            map.put("packageTypeText", getPackageTypeText(order.getPackageType()));
            map.put("weight", order.getWeight());
            map.put("price", order.getPrice());
            map.put("status", order.getStatus());
            map.put("statusText", getStatusText(order.getStatus()));
            map.put("createTime", order.getCreatedAt());
            
            // 如果需要查询关联的用户或快递员信息，可以在这里添加
            
            resultList.add(map);
        }
        
        return resultList;
    }
    
    /**
     * 获取包裹类型文本
     */
    private String getPackageTypeText(Integer packageType) {
        if (packageType == null) return "未知";
        
        switch (packageType) {
            case 0: return "小件";
            case 1: return "中件";
            case 2: return "大件";
            default: return "未知";
        }
    }
    
    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        
        switch (status) {
            case 0: return "待接单";
            case 1: return "已接单";
            case 2: return "取件中";
            case 3: return "已取件";
            case 4: return "配送中";
            case 5: return "已送达";
            case 6: return "已完成";
            case 7: return "已取消";
            default: return "未知";
        }
    }
    
    /**
     * 查询管理员订单列表
     */
    @Override
    public Map<String, Object> findOrdersForAdmin(OrderFilterDto filterDto) {
        List<Order> orders = orderMapper.findOrdersForAdmin(filterDto);
        int total = orderMapper.countOrdersForAdmin(filterDto);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", orders);
        result.put("total", total);
        return result;
    }
    
    /**
     * 管理员查询订单详情
     */
    @Override
    public Order findOrderDetailForAdmin(Long id) {
        Order order = orderMapper.findOrderDetailById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        return order;
    }
    
    /**
     * 管理员更新订单状态
     */
    @Override
    @Transactional
    public Order updateOrderStatusByAdmin(Long id, Integer status) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证状态合法性
        if (status < 0 || status > 7) {
            throw new BusinessException("订单状态不合法");
        }
        
        // 管理员可以任意修改状态，不检查状态流转
        
        // 更新状态
        order.setStatus(status);
        
        // 根据状态设置相应的时间
        if (status == 3) {
            // 已取件，设置实际取件时间
            order.setActualPickupTime(LocalDateTime.now());
        } else if (status == 5) {
            // 已送达，设置实际送达时间
            order.setActualDeliveryTime(LocalDateTime.now());
        }
        
        // 更新时间
        order.setUpdatedAt(LocalDateTime.now());
        
        // 更新订单
        orderMapper.updateById(order);
        
        log.info("管理员订单状态更新成功: ID={}, 订单号={}, 状态={}", order.getId(), order.getOrderNo(), status);
        
        return order;
    }
    
    /**
     * 获取订单统计信息
     */
    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 今日订单数
        int todayOrders = orderMapper.countTodayOrders();
        result.put("todayOrders", todayOrders);
        
        // 今日完成订单数
        int todayCompletedOrders = orderMapper.countTodayCompletedOrders();
        result.put("todayCompletedOrders", todayCompletedOrders);
        
        // 本月订单数
        int monthOrders = orderMapper.countMonthOrders();
        result.put("monthOrders", monthOrders);
        
        // 本月完成订单数
        int monthCompletedOrders = orderMapper.countMonthCompletedOrders();
        result.put("monthCompletedOrders", monthCompletedOrders);
        
        // 各状态订单数
        List<Map<String, Object>> statusCounts = orderMapper.countOrdersByStatus();
        result.put("statusCounts", statusCounts);
        
        // 各类型订单数
        List<Map<String, Object>> typeCounts = orderMapper.countOrdersByType();
        result.put("typeCounts", typeCounts);
        
        return result;
    }
    
    /**
     * 导出订单数据
     */
    @Override
    public String exportOrders(String startDate, String endDate, Integer status) {
        // 根据条件查询订单
        OrderFilterDto filterDto = new OrderFilterDto();
        if (startDate != null && !startDate.isEmpty()) {
            filterDto.setStartDate(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            filterDto.setEndDate(endDate);
        }
        if (status != null) {
            filterDto.setStatus(status);
        }
        
        List<Order> orders = orderMapper.findOrdersForExport(filterDto);
        
        // 在实际应用中，这里应该创建Excel文件并导出
        // 这里简化处理，返回一个模拟的文件URL
        String fileName = "orders_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        String fileUrl = "/exports/" + fileName;
        
        log.info("订单数据导出成功: 文件={}, 记录数={}", fileName, orders.size());
        
        return fileUrl;
    }
    
    /**
     * 批量删除订单
     */
    @Override
    @Transactional
    public void batchDeleteOrders(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException("未选择需要删除的订单");
        }
        
        for (Long id : ids) {
            // 验证订单是否存在
            Order order = orderMapper.selectById(id);
            if (order != null) {
                // 在实际应用中，可能需要检查订单状态，只允许删除特定状态的订单
                orderMapper.deleteById(id);
                log.info("订单删除成功: ID={}, 订单号={}", order.getId(), order.getOrderNo());
            }
        }
    }
} 