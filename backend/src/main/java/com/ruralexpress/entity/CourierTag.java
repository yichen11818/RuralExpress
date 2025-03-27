package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 快递员标签实体类
 */
@Data
@TableName("t_courier_tag")
public class CourierTag {
    
    /**
     * 标签ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 快递员ID
     */
    private Long courierId;
    
    /**
     * 标签内容
     */
    private String tagName;
    
    /**
     * 标签类型(0-系统标签,1-用户评价标签)
     */
    private Integer tagType;
    
    /**
     * 标签计数
     */
    private Integer count;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 