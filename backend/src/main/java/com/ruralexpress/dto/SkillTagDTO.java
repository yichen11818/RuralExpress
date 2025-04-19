package com.ruralexpress.dto;

import lombok.Data;

/**
 * 技能标签数据传输对象
 */
@Data
public class SkillTagDTO {
    
    /**
     * 标签ID
     */
    private Long id;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 标签频率/数量
     */
    private Integer count;
    
    /**
     * 标签权重(可用于前端展示等)
     */
    private Double weight;
    
    /**
     * 标签类型
     */
    private Integer tagType;
} 