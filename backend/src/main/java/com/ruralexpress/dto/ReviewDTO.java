package com.ruralexpress.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评价数据传输对象
 */
@Data
public class ReviewDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 快递员ID
     */
    @NotNull(message = "快递员ID不能为空")
    private Long courierId;
    
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 评分(1-5)
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1分")
    @Max(value = 5, message = "评分最高为5分")
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价标签
     */
    private List<String> tags;
    
    /**
     * 评价图片URL列表
     */
    private List<String> images;
    
    /**
     * 是否匿名(0-否,1-是)
     */
    private Integer anonymous = 0;
} 