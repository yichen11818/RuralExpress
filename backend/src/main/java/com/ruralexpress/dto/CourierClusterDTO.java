package com.ruralexpress.dto;

import com.ruralexpress.entity.Courier;
import lombok.Data;

import java.util.List;

/**
 * 快递员聚类结果数据传输对象
 */
@Data
public class CourierClusterDTO {
    
    /**
     * 聚类ID
     */
    private Integer clusterId;
    
    /**
     * 聚类名称（描述性名称）
     */
    private String clusterName;
    
    /**
     * 聚类描述
     */
    private String description;
    
    /**
     * 主要技能标签列表
     */
    private List<SkillTagDTO> dominantTags;
    
    /**
     * 该聚类中的快递员列表
     */
    private List<Courier> couriers;
    
    /**
     * 该聚类的大小（快递员数量）
     */
    private Integer size;
} 