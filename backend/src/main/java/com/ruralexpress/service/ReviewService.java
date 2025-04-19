package com.ruralexpress.service;

import com.ruralexpress.entity.Review;
import com.ruralexpress.dto.ReviewDTO;

import java.util.Map;

/**
 * 评价服务接口
 */
public interface ReviewService {
    
    /**
     * 获取快递员评价列表
     * @param courierId 快递员ID
     * @param page 页码
     * @param size 每页数量
     * @param rating 评价类型：good-好评, neutral-中评, bad-差评
     * @return 评价列表
     */
    Map<String, Object> getCourierReviews(Long courierId, Integer page, Integer size, String rating);
    
    /**
     * 提交评价
     * @param reviewDTO 评价信息
     * @return 评价实体
     */
    Review submitReview(ReviewDTO reviewDTO);
    
    /**
     * 快递员回复评价
     * @param reviewId 评价ID
     * @param reply 回复内容
     * @return 评价实体
     */
    Review replyToReview(Long reviewId, String reply);
    
    /**
     * 获取用户评价列表
     * @param page 页码
     * @param size 每页数量
     * @return 评价列表
     */
    Map<String, Object> getUserReviews(Integer page, Integer size);
    
    /**
     * 根据订单ID获取评价
     * @param orderId 订单ID
     * @return 评价实体
     */
    Review getReviewByOrderId(Long orderId);
    
    /**
     * 统计快递员评分
     * @param courierId 快递员ID
     * @return 统计结果
     */
    Map<String, Object> calculateCourierRating(Long courierId);
} 