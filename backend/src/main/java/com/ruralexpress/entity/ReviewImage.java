package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价图片实体类
 */
@Data
@TableName("t_review_image")
public class ReviewImage {
    
    /**
     * 图片ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 评价ID
     */
    private Long reviewId;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 排序号
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 