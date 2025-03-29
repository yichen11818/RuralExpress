package com.ruralexpress.controller.admin;

import com.ruralexpress.entity.Courier;
import com.ruralexpress.dto.CourierDTO;
import com.ruralexpress.service.CourierService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员快递员控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/couriers")
public class AdminCourierController {

    @Autowired
    private CourierService courierService;

    /**
     * 获取快递员列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param status 状态
     * @param rating 评分
     * @return 快递员列表
     */
    @GetMapping
    public ApiResult<Map<String, Object>> getCouriers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Double rating) {
        
        try {
            Map<String, Object> result = courierService.getCouriersWithPagination(page, pageSize, keyword, status, rating);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取快递员列表失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取快递员列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取快递员详情
     * @param id 快递员ID
     * @return 快递员详情
     */
    @GetMapping("/{id}")
    public ApiResult<Courier> getCourierDetail(@PathVariable Long id) {
        try {
            Courier courier = courierService.findById(id);
            if (courier == null) {
                return ApiResult.notFound("快递员不存在");
            }
            
            return ApiResult.success(courier);
        } catch (Exception e) {
            log.error("获取快递员详情失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取快递员详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建快递员
     * @param courierDTO 快递员信息
     * @return 创建结果
     */
    @PostMapping
    public ApiResult<Courier> createCourier(@RequestBody CourierDTO courierDTO) {
        try {
            Courier createdCourier = courierService.createCourierWithDTO(courierDTO);
            return ApiResult.success(createdCourier);
        } catch (Exception e) {
            log.error("创建快递员失败: {}", e.getMessage(), e);
            return ApiResult.serverError("创建快递员失败: " + e.getMessage());
        }
    }

    /**
     * 更新快递员信息
     * @param id 快递员ID
     * @param courierDTO 快递员信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Courier> updateCourier(@PathVariable Long id, @RequestBody CourierDTO courierDTO) {
        try {
            // 检查快递员是否存在
            Courier existingCourier = courierService.findById(id);
            if (existingCourier == null) {
                return ApiResult.notFound("快递员不存在");
            }
            
            // 设置ID，防止篡改
            courierDTO.setId(id);
            
            Courier updatedCourier = courierService.updateCourierWithDTO(courierDTO);
            return ApiResult.success(updatedCourier);
        } catch (Exception e) {
            log.error("更新快递员失败: {}", e.getMessage(), e);
            return ApiResult.serverError("更新快递员失败: " + e.getMessage());
        }
    }

    /**
     * 更新快递员状态
     * @param id 快递员ID
     * @param status 状态值
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResult<Courier> updateCourierStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        
        try {
            // 获取状态值
            Integer status = statusMap.get("status");
            if (status == null) {
                return ApiResult.error(400, "缺少status参数");
            }
            
            // 检查快递员是否存在
            Courier existingCourier = courierService.findById(id);
            if (existingCourier == null) {
                return ApiResult.notFound("快递员不存在");
            }
            
            // 更新快递员状态
            existingCourier.setStatus(status);
            Courier updatedCourier = courierService.updateCourier(existingCourier);
            return ApiResult.success(updatedCourier);
        } catch (Exception e) {
            log.error("更新快递员状态失败: {}", e.getMessage(), e);
            return ApiResult.serverError("更新快递员状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除快递员
     * @param id 快递员ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteCourier(@PathVariable Long id) {
        try {
            // 检查快递员是否存在
            Courier existingCourier = courierService.findById(id);
            if (existingCourier == null) {
                return ApiResult.notFound("快递员不存在");
            }
            
            // 删除快递员
            courierService.deleteCourier(id);
            return ApiResult.success(null);
        } catch (Exception e) {
            log.error("删除快递员失败: {}", e.getMessage(), e);
            return ApiResult.serverError("删除快递员失败: " + e.getMessage());
        }
    }

    /**
     * 测试端点
     */
    @GetMapping("/test")
    public String test() {
        log.info("快递员管理测试端点被访问");
        return "快递员管理测试端点正常工作";
    }
} 