package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.SystemSetting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统设置Mapper接口
 */
@Mapper
public interface SystemSettingMapper extends BaseMapper<SystemSetting> {
}