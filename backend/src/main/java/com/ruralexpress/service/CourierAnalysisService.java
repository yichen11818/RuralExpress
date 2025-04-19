package com.ruralexpress.service;

import com.ruralexpress.dto.CourierClusterDTO;
import com.ruralexpress.dto.SkillTagDTO;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.CourierTag;

import java.util.List;
import java.util.Map;

/**
 * 快递员分析服务接口
 * 负责快递员技能标签分析和聚类
 */
public interface CourierAnalysisService {
    
    /**
     * 获取所有技能标签及其频率
     * @return 技能标签列表
     */
    List<SkillTagDTO> getAllSkillTags();
    
    /**
     * 获取特定区域的技能标签及其频率
     * @param area 区域名称
     * @return 技能标签列表
     */
    List<SkillTagDTO> getSkillTagsByArea(String area);
    
    /**
     * 对快递员进行技能标签聚类分析
     * @return 聚类结果
     */
    List<CourierClusterDTO> clusterCouriersBySkills();
    
    /**
     * 基于标签相似度推荐相似快递员
     * @param courierId 快递员ID
     * @param limit 推荐数量限制
     * @return 相似快递员列表
     */
    List<Courier> getSimilarCouriers(Long courierId, Integer limit);
    
    /**
     * 获取技能标签的区域分布
     * @param tagName 标签名称
     * @return 区域及数量映射
     */
    Map<String, Integer> getTagDistributionByArea(String tagName);
} 