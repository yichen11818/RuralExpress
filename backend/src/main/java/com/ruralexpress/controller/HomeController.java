package com.ruralexpress.controller;

import com.ruralexpress.entity.Banner;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.Notice;
import com.ruralexpress.service.BannerService;
import com.ruralexpress.service.CourierService;
import com.ruralexpress.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 首页相关控制器
 */
@RestController
@RequestMapping("/home")
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private BannerService bannerService;
    
    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private CourierService courierService;
    
    /**
     * 获取首页数据
     * @return 首页数据
     * @author yichen11818
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取轮播图
            List<Banner> banners = bannerService.getAllActiveBanners();
            result.put("banners", banners);
            
            // 获取公告并格式化数据
            List<Notice> notices = noticeService.getLatestNotices(5);
            // 处理公告数据，确保title字段有值
            notices = notices.stream().map(notice -> {
                // 如果title为空，使用content的前20个字符作为标题
                if (notice.getTitle() == null || notice.getTitle().isEmpty()) {
                    String content = notice.getContent();
                    if (content != null) {
                        notice.setTitle(content.length() > 20 ? 
                            content.substring(0, 20) + "..." : content);
                    } else {
                        notice.setTitle("系统公告");
                    }
                }
                
                // 确保source有值
                if (notice.getSource() == null || notice.getSource().isEmpty()) {
                    notice.setSource("乡递通");
                }
                
                return notice;
            }).collect(Collectors.toList());
            
            result.put("notices", notices);
            
            // 获取附近快递员(临时返回推荐快递员)
            List<Courier> couriers = courierService.getRecommendedCouriers(5);
            result.put("nearestCouriers", couriers);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 记录详细错误信息并返回空数据
            logger.error("获取首页数据失败: {}", e.getMessage(), e);
            logger.error("异常类型: {}", e.getClass().getName());
            
            // 仍然尝试返回已获取的部分数据，而不是空数据
            if (!result.containsKey("banners")) {
                result.put("banners", new ArrayList<>());
            }
            if (!result.containsKey("notices")) {
                result.put("notices", new ArrayList<>());
            }
            if (!result.containsKey("nearestCouriers")) {
                result.put("nearestCouriers", new ArrayList<>());
            }
            return ResponseEntity.ok(result);
        }
    }
    
    /**
     * 获取所有轮播图
     * @return 轮播图列表
     */
    @GetMapping("/banners")
    public ResponseEntity<List<Banner>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllActiveBanners());
    }
    
    /**
     * 获取所有公告
     * @return 公告列表
     */
    @GetMapping("/notices")
    public ResponseEntity<List<Notice>> getAllNotices() {
        try {
            List<Notice> notices = noticeService.getLatestNotices(10);
            // 处理公告数据，确保title字段有值
            notices = notices.stream().map(notice -> {
                // 如果title为空，使用content的前20个字符作为标题
                if (notice.getTitle() == null || notice.getTitle().isEmpty()) {
                    String content = notice.getContent();
                    if (content != null) {
                        notice.setTitle(content.length() > 20 ? 
                            content.substring(0, 20) + "..." : content);
                    } else {
                        notice.setTitle("系统公告");
                    }
                }
                
                // 确保source有值
                if (notice.getSource() == null || notice.getSource().isEmpty()) {
                    notice.setSource("乡递通");
                }
                
                return notice;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            logger.error("获取公告列表失败", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    /**
     * 获取附近快递员
     * @param latitude 纬度
     * @param longitude 经度
     * @param limit 限制数量
     * @return 快递员列表
     */
    @GetMapping("/couriers/nearest")
    public ResponseEntity<List<Courier>> getNearestCouriers(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            if (latitude != null && longitude != null) {
                // 使用位置信息查询附近快递员
                logger.info("使用位置信息查询附近快递员: 纬度={}, 经度={}, 限制数量={}", latitude, longitude, limit);
                return ResponseEntity.ok(courierService.getNearestCouriers(latitude, longitude, limit));
            } else {
                // 降级处理，返回推荐快递员
                logger.info("位置信息不可用，返回推荐快递员");
                return ResponseEntity.ok(courierService.getRecommendedCouriers(limit));
            }
        } catch (Exception e) {
            // 如果出现异常，返回空列表并记录错误
            logger.error("获取附近快递员失败", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
} 