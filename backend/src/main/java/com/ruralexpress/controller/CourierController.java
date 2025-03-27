package com.ruralexpress.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.dto.CourierApplyDto;
import com.ruralexpress.dto.CourierUpdateDto;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.exception.BusinessException;
import com.ruralexpress.service.CourierService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 快递员控制器
 */
@Slf4j
@RestController
@RequestMapping("/courier")
public class CourierController {
    
    @Autowired
    private CourierService courierService;
    
    /**
     * 申请成为快递员
     * @param applyDto 申请信息
     * @return 申请结果
     */
    @PostMapping("/apply")
    public ApiResult<Map<String, Object>> apply(@Validated @RequestBody CourierApplyDto applyDto) {
        log.info("快递员申请请求: {}", applyDto.getUserId());
        
        try {
            // 将DTO中的信息转换为applyCourier方法需要的参数
            Courier courier = new Courier();
            courier.setServiceArea(applyDto.getServiceArea());
            courier.setWorkStartTime(applyDto.getWorkStartTime());
            courier.setWorkEndTime(applyDto.getWorkEndTime());
            courier.setVehicle(applyDto.getVehicle());
            courier.setIntroduction(applyDto.getIntroduction());
            
            Long courierId = courierService.applyCourier(
                applyDto.getUserId(), 
                courier, 
                applyDto.getIdCardFront(), 
                applyDto.getIdCardBack()
            );
            
            // 获取创建的快递员信息
            Map<String, Object> courierDetail = courierService.getCourierDetail(courierId);
            
            return ApiResult.success(courierDetail);
        } catch (BusinessException e) {
            log.warn("快递员申请失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("快递员申请过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取快递员信息
     * @param id 快递员ID
     * @return 快递员信息
     */
    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> getCourier(@PathVariable Long id) {
        log.info("获取快递员信息请求: {}", id);
        
        try {
            Map<String, Object> courierDetail = courierService.getCourierDetail(id);
            if (courierDetail == null) {
                return ApiResult.error(404, "快递员不存在");
            }
            return ApiResult.success(courierDetail);
        } catch (Exception e) {
            log.error("获取快递员信息过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("获取信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户关联的快递员信息
     * @param userId 用户ID
     * @return 快递员信息
     */
    @GetMapping("/user/{userId}")
    public ApiResult<Map<String, Object>> getCourierByUserId(@PathVariable Long userId) {
        log.info("根据用户ID获取快递员信息请求: {}", userId);
        
        try {
            Map<String, Object> courierInfo = courierService.getMyCourierInfo(userId);
            if (courierInfo == null) {
                return ApiResult.error(404, "未找到关联的快递员信息");
            }
            return ApiResult.success(courierInfo);
        } catch (Exception e) {
            log.error("根据用户ID获取快递员信息过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("获取信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新快递员信息
     * @param id 快递员ID
     * @param updateDto 更新信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Map<String, Object>> updateCourier(@PathVariable Long id, @Validated @RequestBody CourierUpdateDto updateDto) {
        log.info("更新快递员信息请求: {}", id);
        
        try {
            // 获取现有快递员信息
            Map<String, Object> courierDetail = courierService.getCourierDetail(id);
            if (courierDetail == null) {
                return ApiResult.error(404, "快递员不存在");
            }
            
            // 创建更新对象
            Courier courier = new Courier();
            courier.setId(id);
            // 设置需要更新的字段...
            // 这里需要根据CourierUpdateDto的具体字段来设置
            
            // 更新快递员信息
            boolean success = courierService.updateCourierInfo(id, courier);
            if (!success) {
                return ApiResult.error(500, "更新失败");
            }
            
            // 获取更新后的快递员信息
            Map<String, Object> updatedCourier = courierService.getCourierDetail(id);
            return ApiResult.success(updatedCourier);
        } catch (BusinessException e) {
            log.warn("更新快递员信息失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新快递员信息过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新快递员服务状态
     * @param id 快递员ID
     * @param status 服务状态(0-休息,1-接单中)
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResult<Map<String, Object>> updateServiceStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新快递员服务状态请求: {}, 状态: {}", id, status);
        
        try {
            // 更新服务状态
            boolean success = courierService.updateServiceStatus(id, status);
            if (!success) {
                return ApiResult.error(500, "更新失败");
            }
            
            // 获取更新后的快递员信息
            Map<String, Object> courierDetail = courierService.getCourierDetail(id);
            return ApiResult.success(courierDetail);
        } catch (BusinessException e) {
            log.warn("更新快递员服务状态失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("更新快递员服务状态过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询附近的快递员
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离(公里)
     * @return 快递员列表
     */
    @GetMapping("/nearby")
    public ApiResult<List<Map<String, Object>>> getNearby(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5") Integer distance) {
        log.info("查询附近快递员请求: 经度={}, 纬度={}, 距离={}km", longitude, latitude, distance);
        
        try {
            // 转换参数类型
            BigDecimal lng = new BigDecimal(longitude.toString());
            BigDecimal lat = new BigDecimal(latitude.toString());
            
            // 使用分页查询，获取第一页数据
            Page<Courier> page = new Page<>(1, 20);
            IPage<Map<String, Object>> couriers = courierService.findCouriersByDistance(page, lng, lat, distance);
            
            return ApiResult.success(couriers.getRecords());
        } catch (Exception e) {
            log.error("查询附近快递员过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 审核快递员申请
     * @param id 快递员ID
     * @param status 审核状态(1-通过,2-拒绝)
     * @param reason 拒绝原因(仅在拒绝时需要)
     * @return 审核结果
     */
    @PutMapping("/{id}/audit")
    public ApiResult<Map<String, Object>> auditCourier(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String reason) {
        log.info("审核快递员申请请求: {}, 状态: {}", id, status);
        
        try {
            // 审核快递员申请
            boolean success = courierService.auditCourier(id, status, reason);
            if (!success) {
                return ApiResult.error(500, "审核失败");
            }
            
            // 获取更新后的快递员信息
            Map<String, Object> courierDetail = courierService.getCourierDetail(id);
            return ApiResult.success(courierDetail);
        } catch (BusinessException e) {
            log.warn("审核快递员申请失败(业务异常): {}", e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("审核快递员申请过程中发生未知异常: {}", e.getMessage(), e);
            return ApiResult.serverError("审核失败: " + e.getMessage());
        }
    }
} 