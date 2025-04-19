package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评价 Mapper 接口
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    
    /**
     * 查询快递员的评价总数
     * @param courierId 快递员ID
     * @return 评价总数
     */
    @Select("SELECT COUNT(*) FROM t_review WHERE courier_id = #{courierId}")
    int countByCourierId(@Param("courierId") Long courierId);
    
    /**
     * 查询快递员的平均评分
     * @param courierId 快递员ID
     * @return 平均评分
     */
    @Select("SELECT AVG(rating) FROM t_review WHERE courier_id = #{courierId}")
    Double getAverageRatingByCourierId(@Param("courierId") Long courierId);
    
    /**
     * 查询用户的评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    @Select("SELECT * FROM t_review WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Review> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询订单评价
     * @param orderId 订单ID
     * @return 评价
     */
    @Select("SELECT * FROM t_review WHERE order_id = #{orderId} LIMIT 1")
    Review findByOrderId(@Param("orderId") Long orderId);
} 