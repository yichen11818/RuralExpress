package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.DemandPrediction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 需求预测Mapper接口
 */
@Mapper
public interface DemandPredictionMapper extends BaseMapper<DemandPrediction> {
    
    /**
     * 查询指定区域和日期范围内的需求预测数据
     */
    @Select("SELECT * FROM t_demand_prediction WHERE area = #{area} AND prediction_date BETWEEN #{startDate} AND #{endDate} ORDER BY prediction_date")
    List<DemandPrediction> getPredictionsByAreaAndDateRange(
            @Param("area") String area,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 查询指定区域和日期的最新预测数据
     */
    @Select("SELECT * FROM t_demand_prediction WHERE area = #{area} AND prediction_date = #{predictionDate} ORDER BY created_at DESC LIMIT 1")
    DemandPrediction getLatestPredictionByAreaAndDate(
            @Param("area") String area,
            @Param("predictionDate") LocalDate predictionDate);
} 