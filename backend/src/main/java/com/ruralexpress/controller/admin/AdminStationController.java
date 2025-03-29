package com.ruralexpress.controller.admin;

import com.ruralexpress.entity.Station;
import com.ruralexpress.service.StationService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员服务点控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/stations")
public class AdminStationController {

    @Autowired
    private StationService stationService;

    /**
     * 获取服务点列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param status 状态
     * @param area 区域（省市区）
     * @return 服务点列表
     */
    @GetMapping
    public ApiResult<Map<String, Object>> getStations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String area) {
        
        try {
            Map<String, Object> result = stationService.getStationsWithPagination(page, pageSize, keyword, status, area);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取服务点列表失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取服务点列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取服务点详情
     * @param id 服务点ID
     * @return 服务点详情
     */
    @GetMapping("/{id}")
    public ApiResult<Map<String, Object>> getStationDetail(@PathVariable Long id) {
        try {
            Map<String, Object> station = stationService.getStationDetail(id);
            if (station == null) {
                return ApiResult.notFound("服务点不存在");
            }
            
            return ApiResult.success(station);
        } catch (Exception e) {
            log.error("获取服务点详情失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取服务点详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建服务点
     * @param stationData 服务点信息
     * @return 创建结果
     */
    @PostMapping
    public ApiResult<Station> createStation(@RequestBody Map<String, Object> stationData) {
        try {
            Station createdStation = stationService.createStation(stationData);
            return ApiResult.success(createdStation);
        } catch (Exception e) {
            log.error("创建服务点失败: {}", e.getMessage(), e);
            return ApiResult.serverError("创建服务点失败: " + e.getMessage());
        }
    }

    /**
     * 更新服务点信息
     * @param id 服务点ID
     * @param stationData 服务点信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Station> updateStation(@PathVariable Long id, @RequestBody Map<String, Object> stationData) {
        try {
            // 设置ID，防止篡改
            stationData.put("id", id);
            
            Station updatedStation = stationService.updateStation(stationData);
            if (updatedStation == null) {
                return ApiResult.notFound("服务点不存在");
            }
            
            return ApiResult.success(updatedStation);
        } catch (Exception e) {
            log.error("更新服务点失败: {}", e.getMessage(), e);
            return ApiResult.serverError("更新服务点失败: " + e.getMessage());
        }
    }

    /**
     * 更新服务点状态
     * @param id 服务点ID
     * @param status 状态值
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResult<Station> updateStationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        
        try {
            // 获取状态值
            Integer status = statusMap.get("status");
            if (status == null) {
                return ApiResult.error(400, "缺少status参数");
            }
            
            Station updatedStation = stationService.updateStationStatus(id, status);
            if (updatedStation == null) {
                return ApiResult.notFound("服务点不存在");
            }
            
            return ApiResult.success(updatedStation);
        } catch (Exception e) {
            log.error("更新服务点状态失败: {}", e.getMessage(), e);
            return ApiResult.serverError("更新服务点状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除服务点
     * @param id 服务点ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteStation(@PathVariable Long id) {
        try {
            boolean success = stationService.deleteStation(id);
            if (!success) {
                return ApiResult.notFound("服务点不存在");
            }
            
            return ApiResult.success(null);
        } catch (Exception e) {
            log.error("删除服务点失败: {}", e.getMessage(), e);
            return ApiResult.serverError("删除服务点失败: " + e.getMessage());
        }
    }
} 