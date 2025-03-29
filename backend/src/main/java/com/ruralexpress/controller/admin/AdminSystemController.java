package com.ruralexpress.controller.admin;

import com.ruralexpress.service.SystemSettingsService;
import com.ruralexpress.utils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统设置控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/system")
public class AdminSystemController {

    private final SystemSettingsService systemSettingsService;

    /**
     * 获取所有系统设置
     * @return 系统设置
     */
    @GetMapping("/settings")
    public ApiResult<Map<String, Object>> getAllSettings() {
        log.info("获取所有系统设置");
        
        try {
            Map<String, Object> settings = systemSettingsService.getAllSettings();
            log.info("获取所有系统设置成功: {}", settings);
            return ApiResult.success(settings);
        } catch (Exception e) {
            log.error("获取所有系统设置失败", e);
            return ApiResult.serverError("获取系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 保存系统设置
     * @param settings 设置项
     * @return 保存结果
     */
    @PostMapping("/settings")
    public ApiResult<Boolean> saveSettings(@RequestBody Map<String, Object> settings) {
        log.info("保存系统设置: {}", settings);
        
        try {
            systemSettingsService.saveSettings(settings);
            log.info("保存系统设置成功");
            return ApiResult.success(true);
        } catch (Exception e) {
            log.error("保存系统设置失败", e);
            return ApiResult.serverError("保存系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定键的系统设置
     * @param key 设置键
     * @return 设置值
     */
    @GetMapping("/settings/{key}")
    public ApiResult<Object> getSettingsByKey(@PathVariable String key) {
        log.info("获取系统设置: key={}", key);
        
        try {
            Object value = systemSettingsService.getSettingByKey(key);
            log.info("获取系统设置成功: key={}, value={}", key, value);
            return ApiResult.success(value);
        } catch (Exception e) {
            log.error("获取系统设置失败: key={}", key, e);
            return ApiResult.serverError("获取系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 保存指定键的系统设置
     * @param key 设置键
     * @param value 设置值
     * @return 保存结果
     */
    @PostMapping("/settings/{key}")
    public ApiResult<Boolean> saveSettingByKey(
            @PathVariable String key,
            @RequestBody Map<String, Object> value) {
        
        log.info("保存系统设置: key={}, value={}", key, value);
        
        try {
            systemSettingsService.saveSetting(key, value.get("value"));
            log.info("保存系统设置成功: key={}", key);
            return ApiResult.success(true);
        } catch (Exception e) {
            log.error("保存系统设置失败: key={}", key, e);
            return ApiResult.serverError("保存系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 删除指定键的系统设置
     * @param key 设置键
     * @return 删除结果
     */
    @DeleteMapping("/settings/{key}")
    public ApiResult<Boolean> deleteSettingByKey(@PathVariable String key) {
        log.info("删除系统设置: key={}", key);
        
        try {
            boolean result = systemSettingsService.deleteSetting(key);
            if (result) {
                log.info("删除系统设置成功: key={}", key);
                return ApiResult.success(true);
            } else {
                log.warn("系统设置不存在: key={}", key);
                return ApiResult.error(404, "系统设置不存在");
            }
        } catch (Exception e) {
            log.error("删除系统设置失败: key={}", key, e);
            return ApiResult.serverError("删除系统设置失败: " + e.getMessage());
        }
    }
} 