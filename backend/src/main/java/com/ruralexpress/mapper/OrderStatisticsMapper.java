package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.OrderStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 订单统计Mapper接口
 */
@Mapper
public interface OrderStatisticsMapper extends BaseMapper<OrderStatistics> {
    
    /**
     * 查询指定区域和日期范围内的订单统计数据
     */
    @Select("SELECT * FROM t_order_statistics WHERE area = #{area} AND statistics_date BETWEEN #{startDate} AND #{endDate} ORDER BY statistics_date")
    List<OrderStatistics> getStatisticsByAreaAndDateRange(
            @Param("area") String area,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 查询指定区域的历史上同一日期（月和日相同）的数据
     */
    @Select("SELECT * FROM t_order_statistics WHERE area = #{area} AND MONTH(statistics_date) = MONTH(#{date}) AND DAY(statistics_date) = DAY(#{date}) ORDER BY statistics_date")
    List<OrderStatistics> getHistoricalSameDayStatistics(
            @Param("area") String area,
            @Param("date") LocalDate date);
    
    /**
     * 获取所有区域列表
     */
    @Select("SELECT DISTINCT area FROM t_order_statistics")
    List<String> getAllAreas();
} 