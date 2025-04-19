package com.ruralexpress.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.Order;
import com.ruralexpress.entity.Review;
import com.ruralexpress.entity.ReviewImage;
import com.ruralexpress.entity.User;
import com.ruralexpress.dto.ReviewDTO;
import com.ruralexpress.mapper.CourierMapper;
import com.ruralexpress.mapper.OrderMapper;
import com.ruralexpress.mapper.ReviewImageMapper;
import com.ruralexpress.mapper.ReviewMapper;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.service.CourierService;
import com.ruralexpress.service.ReviewService;
import com.ruralexpress.utils.FileUploadUtil;
import com.ruralexpress.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private ReviewImageMapper reviewImageMapper;
    
    @Autowired
    private CourierService courierService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    @Value("${upload.review-image.path}")
    private String reviewImagePath;

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
            
            // 添加标签数据
            if (review.getTags() != null && !review.getTags().isEmpty()) {
                reviewMap.put("tags", JSON.parseArray(review.getTags(), String.class));
            } else {
                reviewMap.put("tags", new ArrayList<>());
            }
            
            // 获取评价图片
            List<String> images = getReviewImages(review.getId());
            reviewMap.put("images", images);
            
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
        
        // 临时解决方案：如果获取不到用户ID，使用订单的用户ID
        if (userId == null) {
            // 检查订单是否存在
            Order order = orderMapper.selectById(reviewDTO.getOrderId());
            if (order == null) {
                throw new IllegalArgumentException("订单不存在");
            }
            userId = order.getUserId();
            System.out.println("使用订单用户ID替代: " + userId);
        }
        
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
        if (order.getStatus() != 5 && order.getStatus() != 6) { // 状态5为已送达，状态6为已完成
            throw new IllegalArgumentException("只能评价已送达或已完成的订单");
        }
        
        // 从订单获取快递员ID（如果评价DTO中没有提供）
        if (reviewDTO.getCourierId() == null || reviewDTO.getCourierId() <= 0) {
            if (order.getCourierId() != null) {
                reviewDTO.setCourierId(order.getCourierId());
                System.out.println("从订单获取快递员ID: " + order.getCourierId());
            } else {
                throw new IllegalArgumentException("找不到快递员信息，无法提交评价");
            }
        }
        
        System.out.println("评价使用的快递员ID: " + reviewDTO.getCourierId());
        
        // 创建评价实体
        Review review = new Review();
        review.setUserId(userId);
        review.setCourierId(reviewDTO.getCourierId());
        review.setOrderId(reviewDTO.getOrderId());
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        review.setAnonymous(reviewDTO.getAnonymous());
        
        // 处理标签
        if (reviewDTO.getTags() != null && !reviewDTO.getTags().isEmpty()) {
            review.setTags(JSON.toJSONString(reviewDTO.getTags()));
        }
        
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        // 保存评价
        reviewMapper.insert(review);
        
        // 保存评价图片
        if (reviewDTO.getImages() != null && !reviewDTO.getImages().isEmpty()) {
            saveReviewImages(review.getId(), reviewDTO.getImages());
        }
        
        // 更新订单状态为已完成
        if (order.getStatus() == 5) { // 如果是已送达状态，更新为已完成
            order.setStatus(6);
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
        }
        
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
            reviewMap.put("orderId", review.getOrderId());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("content", review.getContent());
            reviewMap.put("reply", review.getReply());
            reviewMap.put("createdAt", review.getCreatedAt().format(formatter));
            
            // 添加标签数据
            if (review.getTags() != null && !review.getTags().isEmpty()) {
                reviewMap.put("tags", JSON.parseArray(review.getTags(), String.class));
            } else {
                reviewMap.put("tags", new ArrayList<>());
            }
            
            // 获取评价图片
            List<String> images = getReviewImages(review.getId());
            reviewMap.put("images", images);
            
            // 查询快递员信息
            Courier courier = courierMapper.selectById(review.getCourierId());
            if (courier != null) {
                User courierUser = userMapper.selectById(courier.getUserId());
                if (courierUser != null) {
                    reviewMap.put("courierName", courierUser.getNickname() != null ? courierUser.getNickname() : courierUser.getUsername());
                    reviewMap.put("courierAvatar", courierUser.getAvatar() != null ? courierUser.getAvatar() : "/static/images/default-avatar.png");
                } else {
                    reviewMap.put("courierName", "未知快递员");
                    reviewMap.put("courierAvatar", "/static/images/default-avatar.png");
                }
            } else {
                reviewMap.put("courierName", "未知快递员");
                reviewMap.put("courierAvatar", "/static/images/default-avatar.png");
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
        // 查询快递员信息
        Courier courier = courierMapper.selectById(courierId);
        if (courier == null) {
            throw new IllegalArgumentException("快递员不存在");
        }
        
        // 查询快递员的所有评价
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getCourierId, courierId);
        List<Review> reviews = reviewMapper.selectList(queryWrapper);
        
        // 统计评价数量
        int totalCount = reviews.size();
        int goodCount = 0;  // 好评数量
        int neutralCount = 0;  // 中评数量
        int badCount = 0;  // 差评数量
        double totalRating = 0;  // 总评分
        
        for (Review review : reviews) {
            int rating = review.getRating();
            totalRating += rating;
            
            if (rating >= 4) {
                goodCount++;
            } else if (rating == 3) {
                neutralCount++;
            } else {
                badCount++;
            }
        }
        
        // 计算平均评分
        double averageRating = 0;
        if (totalCount > 0) {
            averageRating = totalRating / totalCount;
            // 四舍五入到一位小数
            averageRating = Math.round(averageRating * 10) / 10.0;
        }
        
        // 计算好评率
        double goodRate = 0;
        if (totalCount > 0) {
            goodRate = (double) goodCount / totalCount * 100;
            // 四舍五入到一位小数
            goodRate = Math.round(goodRate * 10) / 10.0;
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("goodCount", goodCount);
        result.put("neutralCount", neutralCount);
        result.put("badCount", badCount);
        result.put("averageRating", averageRating);
        result.put("goodRate", goodRate);
        
        return result;
    }
    
    /**
     * 上传评价图片
     */
    @Override
    public String uploadReviewImage(MultipartFile file) {
        try {
            // 上传图片
            String fileName = fileUploadUtil.uploadFile(file, reviewImagePath);
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("上传评价图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存评价图片记录
     */
    @Override
    @Transactional
    public List<String> saveReviewImages(Long reviewId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 保存图片记录
        List<ReviewImage> images = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            ReviewImage image = new ReviewImage();
            image.setReviewId(reviewId);
            image.setImageUrl(imageUrls.get(i));
            image.setSort(i);
            image.setCreatedAt(LocalDateTime.now());
            reviewImageMapper.insert(image);
            images.add(image);
        }
        
        // 返回图片URL列表
        return images.stream().map(ReviewImage::getImageUrl).collect(Collectors.toList());
    }
    
    /**
     * 获取评价的图片列表
     */
    @Override
    public List<String> getReviewImages(Long reviewId) {
        LambdaQueryWrapper<ReviewImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReviewImage::getReviewId, reviewId);
        queryWrapper.orderByAsc(ReviewImage::getSort);
        
        List<ReviewImage> images = reviewImageMapper.selectList(queryWrapper);
        return images.stream().map(ReviewImage::getImageUrl).collect(Collectors.toList());
    }

    /**
     * 更新快递员评分
     */
    private void updateCourierRating(Long courierId) {
        Map<String, Object> ratingInfo = calculateCourierRating(courierId);
        Double averageRating = (Double) ratingInfo.get("averageRating");
        
        // 更新快递员评分
        Courier courier = courierMapper.selectById(courierId);
        courier.setRating(new BigDecimal(averageRating).setScale(1, RoundingMode.HALF_UP));
        courier.setRatingCount((Integer) ratingInfo.get("totalCount"));
        courierMapper.updateById(courier);
    }
    
    /**
     * 从地址中提取城市信息
     */
    private String extractCity(String address) {
        if (address == null || address.isEmpty()) {
            return "";
        }
        
        // 简单根据常见的城市分隔符来提取城市信息
        String[] cityDelimiters = {"省", "市", "区", "县"};
        
        String city = "";
        int start = 0;
        
        for (String delimiter : cityDelimiters) {
            int index = address.indexOf(delimiter, start);
            if (index > 0) {
                city = address.substring(start, index + 1);
                start = index + 1;
            }
        }
        
        // 如果没有提取到城市信息，则取地址的前5个字符
        if (city.isEmpty() && address.length() > 5) {
            city = address.substring(0, 5);
        } else if (city.isEmpty()) {
            city = address;
        }
        
        return city;
    }
    
    /**
     * 计算两个坐标点之间的距离
     * @param lat1 纬度1
     * @param lon1 经度1
     * @param lat2 纬度2
     * @param lon2 经度2
     * @return 距离（公里）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径（千米）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
} 