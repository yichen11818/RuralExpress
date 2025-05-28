package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息数据访问层
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadMessageCount(@Param("userId") Long userId);
    
    /**
     * 获取用户消息列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 消息列表
     */
    IPage<Message> getUserMessages(Page<Message> page, @Param("userId") Long userId);
    
    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markMessageAsRead(@Param("messageId") Long messageId, @Param("userId") Long userId);
    
    /**
     * 获取管理员未处理的快递员申请消息
     * @param page 分页参数
     * @return 申请消息列表
     */
    IPage<Message> getPendingCourierApplications(Page<Message> page);
} 