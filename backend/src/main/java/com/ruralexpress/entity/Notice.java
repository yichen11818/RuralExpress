package com.ruralexpress.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
public class Notice {
    /**
     * 公告ID
     */
    private Long id;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 公告来源
     */
    private String source;
    
    /**
     * 公告分类
     */
    private String category;
    
    /**
     * 是否置顶
     */
    private Boolean isTop;
    
    /**
     * 发布状态（0-草稿，1-已发布）
     */
    private Integer status;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建者ID
     */
    private Long createBy;
    
    /**
     * 更新者ID
     */
    private Long updateBy;
} 