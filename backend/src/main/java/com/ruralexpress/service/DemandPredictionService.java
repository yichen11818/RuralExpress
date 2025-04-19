package com.ruralexpress.service;

import com.ruralexpress.dto.DemandPredictionDTO;
import com.ruralexpress.dto.HistoricalDemandDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 需求预测服务接口
 * 负责区域用工需求预测
 */
public interface DemandPredictionService {
    
    /**
     * 获取指定区域未来一段时间的需求预测
     * @param area 区域名称
     * @param startDate 开始日期
     * @param days 预测天数
     * @return 预测结果列表
     */
    List<DemandPredictionDTO> predictDemandForArea(String area, LocalDate startDate, int days);
    
    /**
     * 获取多个区域未来一段时间的需求预测
     * @param areas 区域列表
     * @param startDate 开始日期
     * @param days 预测天数
     * @return 区域到预测结果的映射
     */
    Map<String, List<DemandPredictionDTO>> predictDemandForAreas(List<String> areas, LocalDate startDate, int days);
    
    /**
     * 获取特定区域的历史需求数据
     * @param area 区域名称
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 历史需求数据列表
     */
    List<HistoricalDemandDTO> getHistoricalDemand(String area, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取特定区域的预测需求与实际需求对比图表数据
     * @param area 区域名称
     * @param days 过去的天数
     * @return 预测结果列表（包含历史预测和实际值）
     */
    List<DemandPredictionDTO> getPredictionAccuracy(String area, int days);
    
    /**
     * 更新预测模型
     * @param area 区域
     * @return 更新是否成功
     */
    boolean updatePredictionModel(String area);
} 