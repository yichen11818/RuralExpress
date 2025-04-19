package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruralexpress.dto.CourierClusterDTO;
import com.ruralexpress.dto.SkillTagDTO;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.CourierTag;
import com.ruralexpress.mapper.CourierMapper;
import com.ruralexpress.mapper.CourierTagMapper;
import com.ruralexpress.service.CourierAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 快递员分析服务实现类
 */
@Slf4j
@Service
public class CourierAnalysisServiceImpl implements CourierAnalysisService {

    @Autowired
    private CourierTagMapper courierTagMapper;

    @Autowired
    private CourierMapper courierMapper;

    @Override
    public List<SkillTagDTO> getAllSkillTags() {
        List<CourierTag> tagList = courierTagMapper.selectList(null);
        return convertToSkillTagDTO(tagList);
    }

    @Override
    public List<SkillTagDTO> getSkillTagsByArea(String area) {
        // 1. 获取服务区域内的骑手ID列表
        List<Courier> courierList = courierMapper.selectList(
                new LambdaQueryWrapper<Courier>()
                        .like(Courier::getServiceArea, area)
                        .eq(Courier::getStatus, 1) // 启用状态
        );
        
        List<Long> courierIds = courierList.stream()
                .map(Courier::getId)
                .collect(Collectors.toList());
        
        if (courierIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 2. 获取这些骑手的标签
        List<CourierTag> tagList = courierTagMapper.selectList(
                new LambdaQueryWrapper<CourierTag>()
                        .in(CourierTag::getCourierId, courierIds)
        );
        
        return convertToSkillTagDTO(tagList);
    }

    @Override
    public List<CourierClusterDTO> clusterCouriersBySkills() {
        // 获取所有启用状态的骑手
        List<Courier> couriers = courierMapper.selectList(
                new LambdaQueryWrapper<Courier>()
                        .eq(Courier::getStatus, 1)
        );
        
        if (couriers.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有骑手ID
        List<Long> courierIds = couriers.stream()
                .map(Courier::getId)
                .collect(Collectors.toList());
        
        // 获取所有标签
        List<CourierTag> allTags = courierTagMapper.selectList(
                new LambdaQueryWrapper<CourierTag>()
                        .in(CourierTag::getCourierId, courierIds)
        );
        
        // 标签名称列表 (用于构建特征向量)
        Set<String> tagNames = allTags.stream()
                .map(CourierTag::getTagName)
                .collect(Collectors.toSet());
        
        // 为每个骑手构建标签特征向量
        Map<Long, Map<String, Integer>> courierFeatures = new HashMap<>();
        for (Courier courier : couriers) {
            Map<String, Integer> features = new HashMap<>();
            // 初始化所有标签为0
            tagNames.forEach(tag -> features.put(tag, 0));
            courierFeatures.put(courier.getId(), features);
        }
        
        // 填充特征向量
        for (CourierTag tag : allTags) {
            Map<String, Integer> features = courierFeatures.get(tag.getCourierId());
            if (features != null) {
                features.put(tag.getTagName(), tag.getCount());
            }
        }
        
        // 使用K-means聚类算法进行聚类 (简化版，实际可能需要引入专业机器学习库)
        // 这里使用简化版的聚类，将骑手按主要标签分组
        Map<String, List<Courier>> clusterMap = new HashMap<>();
        
        for (Courier courier : couriers) {
            Map<String, Integer> features = courierFeatures.get(courier.getId());
            if (features.isEmpty()) continue;
            
            // 找出骑手的主导标签 (数量最多的标签)
            String dominantTag = features.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("未分类");
            
            clusterMap.computeIfAbsent(dominantTag, k -> new ArrayList<>()).add(courier);
        }
        
        // 构建聚类结果
        List<CourierClusterDTO> result = new ArrayList<>();
        int clusterId = 0;
        
        for (Map.Entry<String, List<Courier>> entry : clusterMap.entrySet()) {
            CourierClusterDTO cluster = new CourierClusterDTO();
            cluster.setClusterId(++clusterId);
            cluster.setClusterName(entry.getKey() + "专家群体");
            cluster.setDescription("以" + entry.getKey() + "技能为主的快递员群体");
            cluster.setCouriers(entry.getValue());
            cluster.setSize(entry.getValue().size());
            
            // 获取该群体的主要标签
            Set<Long> clusterCourierIds = entry.getValue().stream()
                    .map(Courier::getId)
                    .collect(Collectors.toSet());
            
            List<CourierTag> clusterTags = allTags.stream()
                    .filter(tag -> clusterCourierIds.contains(tag.getCourierId()))
                    .collect(Collectors.toList());
            
            // 统计标签频率
            Map<String, Integer> tagFrequency = new HashMap<>();
            for (CourierTag tag : clusterTags) {
                tagFrequency.put(tag.getTagName(), 
                        tagFrequency.getOrDefault(tag.getTagName(), 0) + tag.getCount());
            }
            
            // 转换为DTO并按频率排序
            List<SkillTagDTO> dominantTags = tagFrequency.entrySet().stream()
                    .map(e -> {
                        SkillTagDTO tagDTO = new SkillTagDTO();
                        tagDTO.setTagName(e.getKey());
                        tagDTO.setCount(e.getValue());
                        tagDTO.setWeight((double) e.getValue() / clusterCourierIds.size());
                        return tagDTO;
                    })
                    .sorted(Comparator.comparing(SkillTagDTO::getCount).reversed())
                    .limit(5) // 取前5个主要标签
                    .collect(Collectors.toList());
            
            cluster.setDominantTags(dominantTags);
            result.add(cluster);
        }
        
        // 按群体大小排序
        result.sort(Comparator.comparing(CourierClusterDTO::getSize).reversed());
        return result;
    }

    @Override
    public List<Courier> getSimilarCouriers(Long courierId, Integer limit) {
        // 获取目标骑手的标签
        List<CourierTag> targetTags = courierTagMapper.selectList(
                new LambdaQueryWrapper<CourierTag>()
                        .eq(CourierTag::getCourierId, courierId)
        );
        
        if (targetTags.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取所有启用状态的骑手(除了目标骑手)
        List<Courier> allCouriers = courierMapper.selectList(
                new LambdaQueryWrapper<Courier>()
                        .eq(Courier::getStatus, 1)
                        .ne(Courier::getId, courierId)
        );
        
        if (allCouriers.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 目标骑手的标签集合
        Set<String> targetTagSet = targetTags.stream()
                .map(CourierTag::getTagName)
                .collect(Collectors.toSet());
        
        // 计算每个骑手与目标骑手的相似度
        Map<Courier, Double> similarityMap = new HashMap<>();
        
        for (Courier courier : allCouriers) {
            List<CourierTag> courierTags = courierTagMapper.selectList(
                    new LambdaQueryWrapper<CourierTag>()
                            .eq(CourierTag::getCourierId, courier.getId())
            );
            
            Set<String> courierTagSet = courierTags.stream()
                    .map(CourierTag::getTagName)
                    .collect(Collectors.toSet());
            
            // 计算Jaccard相似度: 交集大小 / 并集大小
            Set<String> intersection = new HashSet<>(targetTagSet);
            intersection.retainAll(courierTagSet);
            
            Set<String> union = new HashSet<>(targetTagSet);
            union.addAll(courierTagSet);
            
            double similarity = union.isEmpty() ? 0 : (double) intersection.size() / union.size();
            similarityMap.put(courier, similarity);
        }
        
        // 按相似度降序排序并限制结果数量
        return similarityMap.entrySet().stream()
                .sorted(Map.Entry.<Courier, Double>comparingByValue().reversed())
                .limit(limit != null ? limit : 10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getTagDistributionByArea(String tagName) {
        // 获取拥有指定标签的骑手
        List<CourierTag> tags = courierTagMapper.selectList(
                new LambdaQueryWrapper<CourierTag>()
                        .eq(CourierTag::getTagName, tagName)
        );
        
        if (tags.isEmpty()) {
            return new HashMap<>();
        }
        
        List<Long> courierIds = tags.stream()
                .map(CourierTag::getCourierId)
                .collect(Collectors.toList());
        
        // 获取这些骑手的信息
        List<Courier> couriers = courierMapper.selectList(
                new LambdaQueryWrapper<Courier>()
                        .in(Courier::getId, courierIds)
                        .eq(Courier::getStatus, 1)
        );
        
        // 统计每个区域的标签数量
        Map<String, Integer> areaDistribution = new HashMap<>();
        
        for (Courier courier : couriers) {
            String area = courier.getServiceArea();
            if (area != null && !area.isEmpty()) {
                areaDistribution.put(area, areaDistribution.getOrDefault(area, 0) + 1);
            }
        }
        
        return areaDistribution;
    }
    
    /**
     * 转换CourierTag列表为SkillTagDTO列表
     */
    private List<SkillTagDTO> convertToSkillTagDTO(List<CourierTag> tagList) {
        // 按标签名称分组并计算总频率
        Map<String, Integer> tagFrequency = new HashMap<>();
        Map<String, Integer> tagTypeMap = new HashMap<>();
        
        for (CourierTag tag : tagList) {
            tagFrequency.put(tag.getTagName(), 
                    tagFrequency.getOrDefault(tag.getTagName(), 0) + tag.getCount());
            tagTypeMap.put(tag.getTagName(), tag.getTagType());
        }
        
        // 转换为DTO
        List<SkillTagDTO> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            SkillTagDTO dto = new SkillTagDTO();
            dto.setTagName(entry.getKey());
            dto.setCount(entry.getValue());
            dto.setTagType(tagTypeMap.get(entry.getKey()));
            
            // 计算权重 (0-1之间的值)
            double maxCount = tagFrequency.values().stream()
                    .max(Integer::compareTo)
                    .orElse(1);
            dto.setWeight(entry.getValue() / maxCount);
            
            result.add(dto);
        }
        
        // 按频率排序
        result.sort(Comparator.comparing(SkillTagDTO::getCount).reversed());
        return result;
    }
} 