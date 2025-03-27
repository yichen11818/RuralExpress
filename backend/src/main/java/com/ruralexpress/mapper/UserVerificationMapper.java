package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.UserVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户实名认证Mapper接口
 */
@Mapper
public interface UserVerificationMapper extends BaseMapper<UserVerification> {

    /**
     * 查询用户最新的实名认证记录
     * @param userId 用户ID
     * @return 认证记录
     */
    @Select("SELECT * FROM t_user_verification WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 1")
    UserVerification findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户的所有实名认证记录
     * @param userId 用户ID
     * @return 认证记录列表
     */
    @Select("SELECT * FROM t_user_verification WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserVerification> findAllByUserId(@Param("userId") Long userId);
} 