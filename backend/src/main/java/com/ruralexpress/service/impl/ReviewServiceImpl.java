package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.Order;
import com.ruralexpress.entity.Review;
import com.ruralexpress.entity.User;
import com.ruralexpress.dto.ReviewDTO;
import com.ruralexpress.mapper.CourierMapper;
import com.ruralexpress.mapper.OrderMapper;
import com.ruralexpress.mapper.ReviewMapper;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.service.CourierService;
import com.ruralexpress.service.ReviewService;
import com.ruralexpress.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评价服务实现类
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourierMapper courierMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private CourierService courierService;

    /**
     * 获取快递员评价列表
     */
    @Override
    public Map<String, Object> getCourierReviews(Long courierId, Integer page, Integer size, String rating) {
        // 查询快递员信息
        Courier courier = courierMapper.selectById(courierId);
        if (courier == null) {
            throw new IllegalArgumentException("快递员不存在");
        }
        
        // 构建查询条件
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getCourierId, courierId);
        
        // 根据评价类型过滤
        if (rating != null) {
            if ("good".equals(rating)) {
                queryWrapper.ge(Review::getRating, 4); // 4-5分为好评
            } else if ("neutral".equals(rating)) {
                queryWrapper.eq(Review::getRating, 3); // 3分为中评
            } else if ("bad".equals(rating)) {
                queryWrapper.le(Review::getRating, 2); // 1-2分为差评
            }
        }
        
        // 按时间倒序排序
        queryWrapper.orderByDesc(Review::getCreatedAt);
        
        // 分页查询
        Page<Review> pageParam = new Page<>(page, size);
        Page<Review> reviewPage = reviewMapper.selectPage(pageParam, queryWrapper);
        
        // 处理返回结果
        List<Map<String, Object>> reviewList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Review review : reviewPage.getRecords()) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("id", review.getId());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("content", review.getContent());
            reviewMap.put("reply", review.getReply());
            reviewMap.put("time", review.getCreatedAt().format(formatter));
            
            // 查询用户信息
            User user = userMapper.selectById(review.getUserId());
            if (user != null) {
                if (review.getAnonymous() == 1) {
                    // 匿名评价
                    reviewMap.put("userName", "匿名用户");
                    reviewMap.put("userAvatar", "/static/images/default-avatar.png");
                } else {
                    // 实名评价
                    reviewMap.put("userName", user.getNickname() != null ? user.getNickname() : 
                                 "用户" + user.getPhone().substring(0, 3) + "****" + user.getPhone().substring(7));
                    reviewMap.put("userAvatar", user.getAvatar() != null ? user.getAvatar() : "/static/images/default-avatar.png");
                }
            } else {
                reviewMap.put("userName", "未知用户");
                reviewMap.put("userAvatar", "/static/images/default-avatar.png");
            }
            
            // 查询订单信息
            Order order = orderMapper.selectById(review.getOrderId());
            if (order != null) {
                String packageTypeStr = "";
                switch (order.getPackageType()) {
                    case 0:
                        packageTypeStr = "小件快递";
                        break;
                    case 1:
                        packageTypeStr = "中件快递";
                        break;
                    case 2:
                        packageTypeStr = "大件快递";
                        break;
                    default:
                        packageTypeStr = "快递";
                }
                
                // 计算配送距离
                String deliveryDistance = "未知距离";
                if (order.getSenderLatitude() != null && order.getSenderLongitude() != null 
                        && order.getReceiverLatitude() != null && order.getReceiverLongitude() != null) {
                    double distance = calculateDistance(
                            order.getSenderLatitude(), order.getSenderLongitude(),
                            order.getReceiverLatitude(), order.getReceiverLongitude()
                    );
                    // 四舍五入到一位小数
                    distance = Math.round(distance * 10) / 10.0;
                    deliveryDistance = distance + "公里";
                }
                
                // 提取地址信息
                String addressInfo = "";
                if (order.getSenderAddress() != null && order.getReceiverAddress() != null) {
                    // 提取地址的简要信息，例如城市或区域
                    String senderCity = extractCity(order.getSenderAddress());
                    String receiverCity = extractCity(order.getReceiverAddress());
                    addressInfo = senderCity + "→" + receiverCity;
                }
                
                String deliveryTime = "未知时间";
                if (order.getActualDeliveryTime() != null) {
                    deliveryTime = order.getActualDeliveryTime().format(formatter) + " 送达";
                }
                
                reviewMap.put("orderInfo", packageTypeStr + " " + deliveryDistance + " " + addressInfo + " " + deliveryTime);
            }
            
            reviewList.add(reviewMap);
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", reviewPage.getTotal());
        result.put("list", reviewList);
        
        return result;
    }

    /**
     * 提交评价
     */
    @Override
    @Transactional
    public Review submitReview(ReviewDTO reviewDTO) {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 检查订单是否已评价
        Review existingReview = getReviewByOrderId(reviewDTO.getOrderId());
        if (existingReview != null) {
            throw new IllegalArgumentException("该订单已评价，不能重复评价");
        }
        
        // 检查订单是否存在
        Order order = orderMapper.selectById(reviewDTO.getOrderId());
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 检查订单状态
        if (order.getStatus() != 6) { // 假设状态6为已完成
            throw new IllegalArgumentException("只能评价已完成的订单");
        }
        
        // 检查订单所属用户
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("只能评价自己的订单");
        }
        
        // 创建评价实体
        Review review = new Review();
        review.setUserId(userId);
        review.setCourierId(reviewDTO.getCourierId());
        review.setOrderId(reviewDTO.getOrderId());
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        review.setAnonymous(reviewDTO.getAnonymous());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        // 保存评价
        reviewMapper.insert(review);
        
        // 更新快递员评分
        updateCourierRating(reviewDTO.getCourierId());
        
        return review;
    }

    /**
     * 快递员回复评价
     */
    @Override
    @Transactional
    public Review replyToReview(Long reviewId, String reply) {
        // 查询评价是否存在
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("评价不存在");
        }
        
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 查询当前用户关联的快递员信息
        Courier courier = courierMapper.selectOne(
            new LambdaQueryWrapper<Courier>()
                .eq(Courier::getUserId, userId)
        );
        
        // 验证权限
        if (courier == null || !courier.getId().equals(review.getCourierId())) {
            throw new IllegalArgumentException("只能回复自己收到的评价");
        }
        
        // 更新评价回复
        review.setReply(reply);
        review.setUpdatedAt(LocalDateTime.now());
        reviewMapper.updateById(review);
        
        return review;
    }

    /**
     * 获取用户评价列表
     */
    @Override
    public Map<String, Object> getUserReviews(Integer page, Integer size) {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 构建查询条件
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getUserId, userId);
        queryWrapper.orderByDesc(Review::getCreatedAt);
        
        // 分页查询
        Page<Review> pageParam = new Page<>(page, size);
        Page<Review> reviewPage = reviewMapper.selectPage(pageParam, queryWrapper);
        
        // 处理返回结果
        List<Map<String, Object>> reviewList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Review review : reviewPage.getRecords()) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("id", review.getId());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("content", review.getContent());
            reviewMap.put("reply", review.getReply());
            reviewMap.put("time", review.getCreatedAt().format(formatter));
            
            // 查询快递员信息
            Courier courier = courierMapper.selectById(review.getCourierId());
            if (courier != null) {
                User courierUser = userMapper.selectById(courier.getUserId());
                if (courierUser != null) {
                    reviewMap.put("courierName", courierUser.getRealName() != null ? courierUser.getRealName() : courierUser.getNickname());
                    reviewMap.put("courierAvatar", courierUser.getAvatar() != null ? courierUser.getAvatar() : "/static/images/default-avatar.png");
                }
            }
            
            // 查询订单信息
            Order order = orderMapper.selectById(review.getOrderId());
            if (order != null) {
                reviewMap.put("orderNo", order.getOrderNo());
                reviewMap.put("orderInfo", "订单金额: " + order.getPrice() + "元, 送达时间: " + 
                             (order.getActualDeliveryTime() != null ? order.getActualDeliveryTime().format(formatter) : "未知"));
            }
            
            reviewList.add(reviewMap);
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", reviewPage.getTotal());
        result.put("list", reviewList);
        
        return result;
    }

    /**
     * 根据订单ID获取评价
     */
    @Override
    public Review getReviewByOrderId(Long orderId) {
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getOrderId, orderId);
        return reviewMapper.selectOne(queryWrapper);
    }

    /**
     * 统计快递员评分
     */
    @Override
    public Map<String, Object> calculateCourierRating(Long courierId) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询快递员所有评价
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getCourierId, courierId);
        List<Review> reviews = reviewMapper.selectList(queryWrapper);
        
        if (reviews.isEmpty()) {
            result.put("averageRating", 5.0); // 默认5分
            result.put("totalReviews", 0);
            result.put("goodReviews", 0);
            result.put("neutralReviews", 0);
            result.put("badReviews", 0);
            return result;
        }
        
        // 统计评分
        int totalRating = 0;
        int goodCount = 0;
        int neutralCount = 0;
        int badCount = 0;
        
        for (Review review : reviews) {
            totalRating += review.getRating();
            
            if (review.getRating() >= 4) {
                goodCount++;
            } else if (review.getRating() == 3) {
                neutralCount++;
            } else {
                badCount++;
            }
        }
        
        // 计算平均分
        double averageRating = (double) totalRating / reviews.size();
        BigDecimal roundedRating = new BigDecimal(averageRating).setScale(1, RoundingMode.HALF_UP);
        
        // 更新快递员评分
        Courier courier = courierMapper.selectById(courierId);
        if (courier != null) {
            courier.setRating(roundedRating);
            courierMapper.updateById(courier);
        }
        
        // 构造返回结果
        result.put("averageRating", roundedRating);
        result.put("totalReviews", reviews.size());
        result.put("goodReviews", goodCount);
        result.put("neutralReviews", neutralCount);
        result.put("badReviews", badCount);
        
        return result;
    }
    
    /**
     * 更新快递员评分
     */
    private void updateCourierRating(Long courierId) {
        Map<String, Object> ratingStats = calculateCourierRating(courierId);
        BigDecimal rating = (BigDecimal) ratingStats.get("averageRating");
        courierService.updateRating(courierId, rating);
    }
    
    /**
     * 从地址中提取城市信息
     * @param address 完整地址
     * @return 城市名称
     */
    private String extractCity(String address) {
        if (address == null || address.isEmpty()) {
            return "未知";
        }
        
        // 尝试提取省市区格式的城市部分
        // 假设地址格式大致为：xx省xx市xx区xx街道
        String[] parts = address.split("省|市|区|县");
        if (parts.length > 1) {
            // 提取省后面、市前面的部分作为城市
            return parts[1].trim();
        }
        
        // 如果地址格式不符合预期，则简单返回前10个字符作为地点标识
        if (address.length() > 10) {
            return address.substring(0, 10) + "...";
        }
        
        return address;
    }
} 