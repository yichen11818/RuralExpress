package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.SystemSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统设置 Mapper 接口
 */
@Mapper
public interface SystemSettingsMapper extends BaseMapper<SystemSettings> {
    
    /**
     * 根据设置键获取设置值
     * @param key 设置键
     * @return 设置值
     */
    @Select("SELECT setting_value FROM t_system_settings WHERE setting_key = #{key}")
    String getSettingValueByKey(@Param("key") String key);
} 