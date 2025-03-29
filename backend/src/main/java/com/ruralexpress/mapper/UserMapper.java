package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问层接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据关键字查询用户ID列表
     * 关键字匹配用户手机号、昵称或真实姓名
     * 
     * @param keyword 搜索关键字
     * @return 匹配的用户ID列表
     */
    @Select("SELECT id FROM t_user WHERE phone LIKE CONCAT('%', #{keyword}, '%') " +
            "OR nickname LIKE CONCAT('%', #{keyword}, '%') " +
            "OR real_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Long> findUserIdsByKeyword(@Param("keyword") String keyword);
} 