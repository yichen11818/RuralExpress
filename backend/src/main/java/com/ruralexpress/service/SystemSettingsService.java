package com.ruralexpress.service;

import java.util.Map;

/**
 * 系统设置服务接口
 */
public interface SystemSettingsService {
    
    /**
     * 获取所有系统设置
     * @return 系统设置Map
     */
    Map<String, Object> getAllSettings();
    
    /**
     * 根据键获取系统设置值
     * @param key 设置键
     * @return 设置值
     */
    Object getSettingByKey(String key);
    
    /**
     * 保存多个系统设置
     * @param settings 设置Map
     */
    void saveSettings(Map<String, Object> settings);
    
    /**
     * 保存单个系统设置
     * @param key 设置键
     * @param value 设置值
     */
    void saveSetting(String key, Object value);
    
    /**
     * 删除系统设置
     * @param key 设置键
     * @return 是否成功
     */
    boolean deleteSetting(String key);
} 