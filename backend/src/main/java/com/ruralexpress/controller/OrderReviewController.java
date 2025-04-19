package com.ruralexpress.controller;

import com.ruralexpress.dto.ReviewDTO;
import com.ruralexpress.entity.Review;
import com.ruralexpress.service.ReviewService;
import com.ruralexpress.utils.ApiResult;
import com.ruralexpress.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单评价控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 提交订单评价
     * @param reviewDTO 评价信息
     * @return 评价结果
     */
    @PostMapping("/review")
    public ApiResult<?> submitReview(@RequestBody ReviewDTO reviewDTO) {
        log.info("提交订单评价请求: {}", reviewDTO);
        
        try {
            // 设置用户ID
            reviewDTO.setUserId(SecurityUtils.getCurrentUserId());
            
            Review review = reviewService.submitReview(reviewDTO);
            return ApiResult.success(review);
        } catch (IllegalArgumentException e) {
            log.warn("提交评价失败(参数异常): {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("提交评价失败: {}", e.getMessage(), e);
            return ApiResult.serverError("提交评价失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单评价
     * @param orderId 订单ID
     * @return 评价详情
     */
    @GetMapping("/{orderId}/review")
    public ApiResult<?> getOrderReview(@PathVariable Long orderId) {
        log.info("获取订单评价请求: orderId={}", orderId);
        
        try {
            Review review = reviewService.getReviewByOrderId(orderId);
            if (review == null) {
                return ApiResult.success(null);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", review.getId());
            result.put("rating", review.getRating());
            result.put("content", review.getContent());
            result.put("reply", review.getReply());
            result.put("anonymous", review.getAnonymous());
            result.put("createdAt", review.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            // 获取评价图片
            result.put("images", reviewService.getReviewImages(review.getId()));
            
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取订单评价失败: {}", e.getMessage(), e);
            return ApiResult.serverError("获取订单评价失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传评价图片
     * @param file 图片文件
     * @return 上传结果
     */
    @PostMapping("/review/upload")
    public ApiResult<?> uploadReviewImage(@RequestParam("file") MultipartFile file) {
        log.info("上传评价图片请求: filename={}, size={}KB", 
                file.getOriginalFilename(), file.getSize() / 1024);
        
        try {
            String imageUrl = reviewService.uploadReviewImage(file);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("上传评价图片失败: {}", e.getMessage(), e);
            return ApiResult.serverError("上传评价图片失败: " + e.getMessage());
        }
    }
} 