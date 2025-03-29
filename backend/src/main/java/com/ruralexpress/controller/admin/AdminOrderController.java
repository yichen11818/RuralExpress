package com.ruralexpress.controller.admin;

import com.ruralexpress.entity.Order;
import com.ruralexpress.dto.OrderFilterDto;
import com.ruralexpress.service.OrderService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param status 状态
     * @param type 订单类型
     * @return 订单列表
     */
    @GetMapping
    public ApiResult<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type) {
        
        log.info("管理员获取订单列表: page={}, pageSize={}, keyword={}, status={}, type={}", 
                page, pageSize, keyword, status, type);
        
        try {
            OrderFilterDto filterDto = new OrderFilterDto();
            filterDto.setPage(page);
            filterDto.setSize(pageSize);
            filterDto.setKeyword(keyword);
            filterDto.setStatus(status);
            filterDto.setType(type);
            
            Map<String, Object> result = orderService.findOrdersForAdmin(filterDto);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("管理员获取订单列表失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ApiResult<Order> getOrderDetail(@PathVariable Long id) {
        log.info("管理员获取订单详情: id={}", id);
        
        try {
            Order order = orderService.findOrderDetailForAdmin(id);
            if (order == null) {
                return ApiResult.notFound("订单不存在");
            }
            
            return ApiResult.success(order);
        } catch (Exception e) {
            log.error("管理员获取订单详情失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param statusMap 状态信息
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResult<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        
        Integer status = statusMap.get("status");
        log.info("管理员更新订单状态: id={}, status={}", id, status);
        
        if (status == null) {
            return ApiResult.error(400, "缺少status参数");
        }
        
        try {
            Order order = orderService.updateOrderStatusByAdmin(id, status);
            return ApiResult.success(order);
        } catch (Exception e) {
            log.error("管理员更新订单状态失败: {}", e.getMessage(), e);
            return ApiResult.serverError("更新订单状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单统计信息
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ApiResult<Map<String, Object>> getStatistics() {
        log.info("管理员获取订单统计信息");
        
        try {
            Map<String, Object> statistics = orderService.getOrderStatistics();
            return ApiResult.success(statistics);
        } catch (Exception e) {
            log.error("管理员获取订单统计信息失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取订单统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 导出订单数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 状态
     * @return 导出结果
     */
    @GetMapping("/export")
    public ApiResult<String> exportOrders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer status) {
        
        log.info("管理员导出订单数据: startDate={}, endDate={}, status={}", 
                startDate, endDate, status);
        
        try {
            // 导出订单数据逻辑
            String exportUrl = orderService.exportOrders(startDate, endDate, status);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", exportUrl);
            
            return ApiResult.success(exportUrl);
        } catch (Exception e) {
            log.error("管理员导出订单数据失败: {}", e.getMessage(), e);
            return ApiResult.serverError("导出订单数据失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除订单
     * @param ids 订单ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ApiResult<Void> batchDeleteOrders(@RequestBody Map<String, Long[]> idsMap) {
        Long[] ids = idsMap.get("ids");
        
        if (ids == null || ids.length == 0) {
            return ApiResult.error(400, "请选择要删除的订单");
        }
        
        log.info("管理员批量删除订单: ids={}", (Object)ids);
        
        try {
            orderService.batchDeleteOrders(ids);
            return ApiResult.success(null);
        } catch (Exception e) {
            log.error("管理员批量删除订单失败: {}", e.getMessage(), e);
            return ApiResult.serverError("批量删除订单失败: " + e.getMessage());
        }
    }

    /**
     * 测试端点
     */
    @GetMapping("/test")
    public String test() {
        log.info("订单管理测试端点被访问");
        return "订单管理测试端点正常工作";
    }
} 