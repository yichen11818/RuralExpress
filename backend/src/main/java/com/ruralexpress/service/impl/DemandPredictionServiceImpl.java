package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruralexpress.dto.DemandPredictionDTO;
import com.ruralexpress.dto.HistoricalDemandDTO;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.DemandPrediction;
import com.ruralexpress.entity.OrderStatistics;
import com.ruralexpress.mapper.CourierMapper;
import com.ruralexpress.mapper.DemandPredictionMapper;
import com.ruralexpress.mapper.OrderStatisticsMapper;
import com.ruralexpress.service.DemandPredictionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 需求预测服务实现类
 */
@Slf4j
@Service
public class DemandPredictionServiceImpl implements DemandPredictionService {

    @Autowired
    private OrderStatisticsMapper orderStatisticsMapper;

    @Autowired
    private DemandPredictionMapper demandPredictionMapper;

    @Autowired
    private CourierMapper courierMapper;

    @Override
    public List<DemandPredictionDTO> predictDemandForArea(String area, LocalDate startDate, int days) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        
        if (days <= 0) {
            days = 7; // 默认预测未来7天
        }
        
        // 查询已有的预测结果
        LocalDate endDate = startDate.plusDays(days - 1);
        List<DemandPrediction> existingPredictions = demandPredictionMapper.getPredictionsByAreaAndDateRange(area, startDate, endDate);
        
        // 如果预测结果完整，直接返回
        if (existingPredictions.size() == days) {
            return existingPredictions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        
        // 预测结果不完整，使用Prophet模型进行预测
        // 获取历史数据
        LocalDate historyStartDate = startDate.minusYears(2); // 获取过去2年的数据
        List<OrderStatistics> historicalData = orderStatisticsMapper.getStatisticsByAreaAndDateRange(
                area, historyStartDate, startDate.minusDays(1));
        
        if (historicalData.isEmpty()) {
            log.warn("区域[{}]没有足够的历史数据进行预测", area);
            return new ArrayList<>();
        }
        
        // 进行预测
        List<DemandPredictionDTO> predictions = performPrediction(area, historicalData, startDate, days);
        
        // 保存预测结果到数据库
        for (DemandPredictionDTO prediction : predictions) {
            DemandPrediction demandPrediction = new DemandPrediction();
            demandPrediction.setArea(area);
            demandPrediction.setPredictionDate(prediction.getDate());
            demandPrediction.setPredictedOrders(prediction.getPredictedOrders());
            demandPrediction.setPredictedCouriers(prediction.getPredictedCouriers());
            demandPrediction.setUpperBound(prediction.getUpperBound());
            demandPrediction.setLowerBound(prediction.getLowerBound());
            demandPrediction.setAccuracyScore(prediction.getAccuracyScore());
            demandPrediction.setModelVersion("prophet-1.0");
            demandPrediction.setCreatedAt(LocalDateTime.now());
            demandPrediction.setUpdatedAt(LocalDateTime.now());
            
            demandPredictionMapper.insert(demandPrediction);
        }
        
        return predictions;
    }

    @Override
    public Map<String, List<DemandPredictionDTO>> predictDemandForAreas(List<String> areas, LocalDate startDate, int days) {
        if (areas == null || areas.isEmpty()) {
            // 如果未指定区域，获取所有区域
            areas = orderStatisticsMapper.getAllAreas();
        }
        
        Map<String, List<DemandPredictionDTO>> result = new HashMap<>();
        
        for (String area : areas) {
            List<DemandPredictionDTO> predictions = predictDemandForArea(area, startDate, days);
            result.put(area, predictions);
        }
        
        return result;
    }

    @Override
    public List<HistoricalDemandDTO> getHistoricalDemand(String area, LocalDate startDate, LocalDate endDate) {
        List<OrderStatistics> statisticsList = orderStatisticsMapper.getStatisticsByAreaAndDateRange(area, startDate, endDate);
        
        return statisticsList.stream()
                .map(this::convertToHistoricalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DemandPredictionDTO> getPredictionAccuracy(String area, int days) {
        LocalDate endDate = LocalDate.now().minusDays(1); // 昨天
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 获取历史预测数据
        List<DemandPrediction> predictions = demandPredictionMapper.getPredictionsByAreaAndDateRange(area, startDate, endDate);
        
        // 获取实际数据
        List<OrderStatistics> actualData = orderStatisticsMapper.getStatisticsByAreaAndDateRange(area, startDate, endDate);
        
        // 将实际数据转为Map，方便查找
        Map<LocalDate, OrderStatistics> actualDataMap = actualData.stream()
                .collect(Collectors.toMap(OrderStatistics::getStatisticsDate, stats -> stats));
        
        List<DemandPredictionDTO> result = new ArrayList<>();
        
        for (DemandPrediction prediction : predictions) {
            DemandPredictionDTO dto = convertToDTO(prediction);
            
            // 添加实际数据
            OrderStatistics actual = actualDataMap.get(prediction.getPredictionDate());
            if (actual != null) {
                // 计算预测准确度
                double accuracy = 100 - Math.min(100, Math.abs(prediction.getPredictedOrders() - actual.getOrderCount()) / 
                        Math.max(1, actual.getOrderCount()) * 100);
                dto.setAccuracyScore(accuracy);
            }
            
            result.add(dto);
        }
        
        return result;
    }

    @Override
    public boolean updatePredictionModel(String area) {
        try {
            // 这里可以添加更新模型的逻辑
            // 例如重新训练模型、更新参数等
            
            // 删除未来的预测数据，以便重新预测
            LambdaQueryWrapper<DemandPrediction> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DemandPrediction::getArea, area)
                   .ge(DemandPrediction::getPredictionDate, LocalDate.now());
            
            demandPredictionMapper.delete(wrapper);
            
            // 重新预测未来7天的数据
            predictDemandForArea(area, LocalDate.now(), 7);
            
            return true;
        } catch (Exception e) {
            log.error("更新区域[{}]的预测模型失败", area, e);
            return false;
        }
    }
    
    /**
     * 执行Prophet模型预测
     * 注：由于项目中不能直接使用Python的Prophet库，这里使用简化的时间序列预测模型
     */
    private List<DemandPredictionDTO> performPrediction(String area, List<OrderStatistics> historicalData, LocalDate startDate, int days) {
        List<DemandPredictionDTO> results = new ArrayList<>();
        
        // 将历史数据按日期排序
        historicalData.sort(Comparator.comparing(OrderStatistics::getStatisticsDate));
        
        // 使用简化的时间序列预测模型 (基于历史同期数据 + 趋势 + 季节性)
        
        // 1. 基础预测：使用最近的趋势
        SimpleRegression regression = new SimpleRegression();
        
        // 使用最近30天的数据计算趋势
        List<OrderStatistics> recentData = historicalData.stream()
                .filter(s -> s.getStatisticsDate().isAfter(startDate.minusDays(30)))
                .collect(Collectors.toList());
        
        if (recentData.size() < 7) {
            // 如果最近数据不足，使用所有历史数据
            recentData = historicalData;
        }
        
        // 计算趋势
        for (int i = 0; i < recentData.size(); i++) {
            regression.addData(i, recentData.get(i).getOrderCount());
        }
        
        // 获取区域当前可用骑手数量
        int availableCouriers = getAvailableCouriers(area);
        
        // 2. 对每一天进行预测
        for (int i = 0; i < days; i++) {
            LocalDate predictionDate = startDate.plusDays(i);
            DemandPredictionDTO prediction = new DemandPredictionDTO();
            prediction.setDate(predictionDate);
            prediction.setArea(area);
            
            // 基本趋势预测值
            double trendPrediction = regression.predict(recentData.size() + i);
            
            // 考虑周期性：根据星期几进行调整
            double weekdayFactor = getWeekdayFactor(predictionDate, historicalData);
            
            // 考虑季节性：根据月份进行调整
            double seasonalFactor = getSeasonalFactor(predictionDate, historicalData);
            
            // 最终预测值 = 趋势 * 周期因子 * 季节因子
            double finalPrediction = Math.max(0, trendPrediction * weekdayFactor * seasonalFactor);
            
            // 设置预测订单数量
            prediction.setPredictedOrders(finalPrediction);
            
            // 预测所需骑手数量 (简单计算：平均每个骑手每天可以处理10个订单)
            int predictedCouriers = (int) Math.ceil(finalPrediction / 10.0);
            prediction.setPredictedCouriers(predictedCouriers);
            prediction.setAvailableCouriers(availableCouriers);
            
            // 设置预测区间（简单计算：±15%）
            prediction.setUpperBound(finalPrediction * 1.15);
            prediction.setLowerBound(Math.max(0, finalPrediction * 0.85));
            
            // 设置预测准确度分数（模拟值）
            prediction.setAccuracyScore(85.0); // 假设准确度为85%
            
            results.add(prediction);
        }
        
        return results;
    }
    
    /**
     * 获取周期因子 (根据星期几的历史数据)
     */
    private double getWeekdayFactor(LocalDate date, List<OrderStatistics> historicalData) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        // 获取历史上同一星期几的数据
        List<OrderStatistics> sameDayOfWeekData = historicalData.stream()
                .filter(s -> s.getStatisticsDate().getDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());
        
        if (sameDayOfWeekData.isEmpty()) {
            return 1.0; // 没有历史数据，返回默认值
        }
        
        // 计算同一星期几的平均订单数
        double avgOrdersOnSameDay = sameDayOfWeekData.stream()
                .mapToInt(OrderStatistics::getOrderCount)
                .average()
                .orElse(0);
        
        // 计算所有历史数据的平均订单数
        double avgOrdersOverall = historicalData.stream()
                .mapToInt(OrderStatistics::getOrderCount)
                .average()
                .orElse(1); // 避免除零
        
        // 周期因子 = 同一星期几的平均值 / 整体平均值
        return avgOrdersOnSameDay / Math.max(1, avgOrdersOverall);
    }
    
    /**
     * 获取季节性因子 (根据月份的历史数据)
     */
    private double getSeasonalFactor(LocalDate date, List<OrderStatistics> historicalData) {
        int month = date.getMonthValue();
        
        // 获取历史上同一个月的数据
        List<OrderStatistics> sameMonthData = historicalData.stream()
                .filter(s -> s.getStatisticsDate().getMonthValue() == month)
                .collect(Collectors.toList());
        
        if (sameMonthData.isEmpty()) {
            return 1.0; // 没有历史数据，返回默认值
        }
        
        // 计算同一个月的平均订单数
        double avgOrdersInSameMonth = sameMonthData.stream()
                .mapToInt(OrderStatistics::getOrderCount)
                .average()
                .orElse(0);
        
        // 计算所有历史数据的平均订单数
        double avgOrdersOverall = historicalData.stream()
                .mapToInt(OrderStatistics::getOrderCount)
                .average()
                .orElse(1); // 避免除零
        
        // 季节性因子 = 同一个月的平均值 / 整体平均值
        return avgOrdersInSameMonth / Math.max(1, avgOrdersOverall);
    }
    
    /**
     * 获取区域可用骑手数量
     */
    private int getAvailableCouriers(String area) {
        LambdaQueryWrapper<Courier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Courier::getStatus, 1) // 启用状态
               .eq(Courier::getServiceArea, area);
        
        return courierMapper.selectCount(wrapper);
    }
    
    /**
     * 将预测实体转换为DTO
     */
    private DemandPredictionDTO convertToDTO(DemandPrediction prediction) {
        DemandPredictionDTO dto = new DemandPredictionDTO();
        dto.setDate(prediction.getPredictionDate());
        dto.setArea(prediction.getArea());
        dto.setPredictedOrders(prediction.getPredictedOrders());
        dto.setPredictedCouriers(prediction.getPredictedCouriers());
        dto.setUpperBound(prediction.getUpperBound());
        dto.setLowerBound(prediction.getLowerBound());
        dto.setAccuracyScore(prediction.getAccuracyScore());
        
        // 获取当前可用骑手数量
        dto.setAvailableCouriers(getAvailableCouriers(prediction.getArea()));
        
        return dto;
    }
    
    /**
     * 将OrderStatistics转换为HistoricalDemandDTO
     */
    private HistoricalDemandDTO convertToHistoricalDTO(OrderStatistics statistics) {
        HistoricalDemandDTO dto = new HistoricalDemandDTO();
        dto.setDate(statistics.getStatisticsDate());
        dto.setArea(statistics.getArea());
        dto.setOrderCount(statistics.getOrderCount());
        dto.setActiveCouriers(statistics.getActiveCouriers());
        dto.setAvgProcessTime(statistics.getAvgProcessTime());
        dto.setIsWorkday(statistics.getIsWorkday());
        dto.setIsHoliday(statistics.getIsHoliday());
        dto.setWeatherCode(statistics.getWeatherCode());
        
        // 计算同比增长率（如果有去年同一天的数据）
        LocalDate lastYear = statistics.getStatisticsDate().minusYears(1);
        List<OrderStatistics> lastYearData = orderStatisticsMapper.getHistoricalSameDayStatistics(
                statistics.getArea(), lastYear);
        
        if (!lastYearData.isEmpty()) {
            int lastYearOrders = lastYearData.get(0).getOrderCount();
            if (lastYearOrders > 0) {
                double growth = (statistics.getOrderCount() - lastYearOrders) * 100.0 / lastYearOrders;
                dto.setYearOverYearTrend(growth);
            }
        }
        
        return dto;
    }
} 