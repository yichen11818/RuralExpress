package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruralexpress.entity.SystemSetting;
import com.ruralexpress.mapper.SystemSettingMapper;
import com.ruralexpress.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemSettingsServiceImpl implements SystemSettingsService {

    private final SystemSettingMapper systemSettingMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> getAllSettings() {
        log.info("获取所有系统设置");
        
        // 查询所有系统设置
        List<SystemSetting> settings = systemSettingMapper.selectList(null);
        
        // 转换为Map
        Map<String, Object> result = new HashMap<>();
        for (SystemSetting setting : settings) {
            result.put(setting.getSettingKey(), deserializeValue(setting.getSettingValue()));
        }
        
        return result;
    }

    @Override
    public Object getSettingByKey(String key) {
        log.info("获取系统设置: key={}", key);
        
        // 查询系统设置
        LambdaQueryWrapper<SystemSetting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemSetting::getSettingKey, key);
        
        SystemSetting setting = systemSettingMapper.selectOne(queryWrapper);
        if (setting == null) {
            log.warn("系统设置不存在: key={}", key);
            return null;
        }
        
        return deserializeValue(setting.getSettingValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSettings(Map<String, Object> settings) {
        log.info("保存系统设置: {}", settings);
        
        if (settings == null || settings.isEmpty()) {
            log.warn("系统设置为空，不保存");
            return;
        }
        
        // 保存每个设置项
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            saveSetting(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSetting(String key, Object value) {
        log.info("保存系统设置: key={}, value={}", key, value);
        
        if (key == null || key.trim().isEmpty()) {
            log.warn("系统设置键为空，不保存");
            return;
        }
        
        // 序列化值
        String serializedValue = serializeValue(value);
        
        // 查询是否存在
        LambdaQueryWrapper<SystemSetting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemSetting::getSettingKey, key);
        
        SystemSetting existingSetting = systemSettingMapper.selectOne(queryWrapper);
        LocalDateTime now = LocalDateTime.now();
        
        if (existingSetting == null) {
            // 不存在，创建新的设置
            log.info("创建新的系统设置: key={}", key);
            
            SystemSetting setting = new SystemSetting();
            setting.setSettingKey(key);
            setting.setSettingValue(serializedValue);
            setting.setDescription(key);
            setting.setCreateTime(now);
            setting.setUpdateTime(now);
            
            systemSettingMapper.insert(setting);
        } else {
            // 存在，更新设置
            log.info("更新系统设置: key={}", key);
            
            existingSetting.setSettingValue(serializedValue);
            existingSetting.setUpdateTime(now);
            
            systemSettingMapper.updateById(existingSetting);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSetting(String key) {
        log.info("删除系统设置: key={}", key);
        
        // 查询是否存在
        LambdaQueryWrapper<SystemSetting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemSetting::getSettingKey, key);
        
        SystemSetting existingSetting = systemSettingMapper.selectOne(queryWrapper);
        if (existingSetting == null) {
            log.warn("系统设置不存在: key={}", key);
            return false;
        }
        
        // 删除设置
        systemSettingMapper.deleteById(existingSetting.getId());
        
        return true;
    }
    
    /**
     * 序列化值为JSON字符串
     * @param value 值
     * @return JSON字符串
     */
    private String serializeValue(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("序列化值失败: {}", value, e);
            throw new RuntimeException("序列化值失败", e);
        }
    }
    
    /**
     * 反序列化JSON字符串为对象
     * @param json JSON字符串
     * @return 对象
     */
    private Object deserializeValue(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            log.error("反序列化值失败: {}", json, e);
            // 如果反序列化失败，则返回原始字符串
            return json;
        }
    }
} 