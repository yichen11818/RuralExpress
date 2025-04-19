package com.ruralexpress.controller;

import com.ruralexpress.entity.Review;
import com.ruralexpress.dto.ReviewDTO;
import com.ruralexpress.service.ReviewService;
import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评价控制器
 */
@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取快递员评价列表
     * @param courierId 快递员ID
     * @param page 页码
     * @param size 每页数量
     * @param rating 评价类型：good-好评, neutral-中评, bad-差评
     * @return 评价列表
     */
    @GetMapping("/courier/{courierId}")
    public ApiResult<Map<String, Object>> getCourierReviews(
            @PathVariable Long courierId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String rating) {
        log.info("获取快递员评价列表请求: courierId={}, page={}, size={}, rating={}", courierId, page, size, rating);
        
        try {
            Map<String, Object> reviews = reviewService.getCourierReviews(courierId, page, size, rating);
            return ApiResult.success(reviews);
        } catch (Exception e) {
            log.error("获取快递员评价列表失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取评价列表失败: " + e.getMessage());
        }
    }

    /**
     * 提交评价
     * @param reviewDTO 评价信息
     * @return 评价结果
     */
    @PostMapping
    public ApiResult<Review> submitReview(@RequestBody ReviewDTO reviewDTO) {
        log.info("提交评价请求: {}", reviewDTO);
        
        try {
            Review review = reviewService.submitReview(reviewDTO);
            return ApiResult.success(review);
        } catch (Exception e) {
            log.error("提交评价失败: {}", e.getMessage(), e);
            return ApiResult.serverError("提交评价失败: " + e.getMessage());
        }
    }

    /**
     * 快递员回复评价
     * @param reviewId 评价ID
     * @param replyMap 回复内容
     * @return 回复结果
     */
    @PostMapping("/{reviewId}/reply")
    public ApiResult<Review> replyToReview(
            @PathVariable Long reviewId,
            @RequestBody Map<String, String> replyMap) {
        String reply = replyMap.get("reply");
        log.info("快递员回复评价请求: reviewId={}, reply={}", reviewId, reply);
        
        if (reply == null || reply.trim().isEmpty()) {
            return ApiResult.error(400, "回复内容不能为空");
        }
        
        try {
            Review review = reviewService.replyToReview(reviewId, reply);
            return ApiResult.success(review);
        } catch (Exception e) {
            log.error("回复评价失败: {}", e.getMessage(), e);
            return ApiResult.serverError("回复评价失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户评价列表
     * @param page 页码
     * @param size 每页数量
     * @return 评价列表
     */
    @GetMapping("/user")
    public ApiResult<Map<String, Object>> getUserReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取用户评价列表请求: page={}, size={}", page, size);
        
        try {
            Map<String, Object> reviews = reviewService.getUserReviews(page, size);
            return ApiResult.success(reviews);
        } catch (Exception e) {
            log.error("获取用户评价列表失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取评价列表失败: " + e.getMessage());
        }
    }
} 