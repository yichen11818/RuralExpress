package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
} 