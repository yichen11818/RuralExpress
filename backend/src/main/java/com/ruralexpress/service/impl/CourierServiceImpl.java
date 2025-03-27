package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.CourierTag;
import com.ruralexpress.entity.User;
import com.ruralexpress.mapper.CourierMapper;
import com.ruralexpress.mapper.CourierTagMapper;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 快递员服务实现类
 */
@Service
public class CourierServiceImpl implements CourierService {

    @Autowired
    private CourierMapper courierMapper;
    
    @Autowired
    private CourierTagMapper courierTagMapper;
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 申请成为快递员
     */
    @Override
    @Transactional
    public Long applyCourier(Long userId, Courier courier, String idCardFront, String idCardBack) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 检查用户是否已经是快递员
        LambdaQueryWrapper<Courier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Courier::getUserId, userId);
        Courier existCourier = courierMapper.selectOne(queryWrapper);
        if (existCourier != null) {
            throw new IllegalArgumentException("您已经申请过快递员");
        }
        
        // 设置快递员信息
        courier.setUserId(userId);
        courier.setIdCardFront(idCardFront);
        courier.setIdCardBack(idCardBack);
        courier.setAuditStatus(0); // 待审核
        courier.setServiceStatus(0); // 休息中
        courier.setRating(new BigDecimal("5.0")); // 初始评分为5分
        courier.setBalance(new BigDecimal("0")); // 初始余额为0
        courier.setCompletedOrders(0); // 初始完成订单数为0
        courier.setCreatedAt(LocalDateTime.now());
        courier.setUpdatedAt(LocalDateTime.now());
        
        // 保存快递员信息
        courierMapper.insert(courier);
        
        // 更新用户类型为快递员
        user.setUserType(1); // 快递员
        userMapper.updateById(user);
        
        return courier.getId();
    }

    /**
     * 获取快递员详情
     */
    @Override
    public Map<String, Object> getCourierDetail(Long courierId) {
        // 查询快递员信息
        Courier courier = courierMapper.selectById(courierId);
        if (courier == null) {
            throw new IllegalArgumentException("快递员不存在");
        }
        
        // 查询用户信息
        User user = userMapper.selectById(courier.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 查询快递员标签
        List<CourierTag> tags = courierTagMapper.findByCourierId(courierId);
        
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", courier.getId());
        result.put("userId", courier.getUserId());
        result.put("name", user.getRealName());
        result.put("avatar", user.getAvatar());
        result.put("phone", user.getPhone());
        result.put("serviceArea", courier.getServiceArea());
        result.put("serviceStatus", courier.getServiceStatus());
        result.put("rating", courier.getRating());
        result.put("completedOrders", courier.getCompletedOrders());
        result.put("workStartTime", courier.getWorkStartTime());
        result.put("workEndTime", courier.getWorkEndTime());
        result.put("vehicle", courier.getVehicle());
        result.put("introduction", courier.getIntroduction());
        result.put("responseTime", courier.getResponseTime());
        
        // 处理标签数据
        List<String> tagList = tags.stream()
                .map(CourierTag::getTagName)
                .collect(Collectors.toList());
        result.put("tags", tagList);
        
        return result;
    }

    /**
     * 按区域查询快递员
     */
    @Override
    public IPage<Map<String, Object>> findCouriersByArea(Page<Courier> page, String province, String city, String district, Integer sortType) {
        // 查询快递员列表
        IPage<Courier> courierPage = courierMapper.findByArea(page, province, city, district);
        
        // 处理排序
        List<Courier> couriers = courierPage.getRecords();
        if (sortType != null) {
            if (sortType == 1) {
                // 按评分排序
                couriers.sort((c1, c2) -> c2.getRating().compareTo(c1.getRating()));
            } else if (sortType == 2) {
                // 按订单数排序
                couriers.sort((c1, c2) -> c2.getCompletedOrders().compareTo(c1.getCompletedOrders()));
            }
        }
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Courier courier : couriers) {
            User user = userMapper.selectById(courier.getUserId());
            if (user == null) continue;
            
            Map<String, Object> map = new HashMap<>();
            map.put("id", courier.getId());
            map.put("name", user.getRealName());
            map.put("avatar", user.getAvatar());
            map.put("rating", courier.getRating());
            map.put("completedOrders", courier.getCompletedOrders());
            map.put("serviceTime", getServiceTime(courier.getCreatedAt()));
            map.put("serviceArea", courier.getServiceArea());
            
            // 查询标签
            List<CourierTag> tags = courierTagMapper.findByCourierId(courier.getId());
            List<String> tagList = tags.stream()
                    .limit(3) // 最多返回3个标签
                    .map(CourierTag::getTagName)
                    .collect(Collectors.toList());
            map.put("tags", tagList);
            
            // 计算距离（这里是模拟数据，实际应根据经纬度计算）
            map.put("distance", 2.0 + Math.random() * 3.0);
            
            resultList.add(map);
        }
        
        // 创建新的分页对象返回
        IPage<Map<String, Object>> resultPage = new Page<>(page.getCurrent(), page.getSize(), courierPage.getTotal());
        resultPage.setRecords(resultList);
        
        return resultPage;
    }

    /**
     * 按距离查询快递员
     */
    @Override
    public IPage<Map<String, Object>> findCouriersByDistance(Page<Courier> page, BigDecimal longitude, BigDecimal latitude, Integer distance) {
        // 查询快递员列表
        IPage<Courier> courierPage = courierMapper.findNearby(page, longitude, latitude, distance);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Courier courier : courierPage.getRecords()) {
            User user = userMapper.selectById(courier.getUserId());
            if (user == null) continue;
            
            Map<String, Object> map = new HashMap<>();
            map.put("id", courier.getId());
            map.put("name", user.getRealName());
            map.put("avatar", user.getAvatar());
            map.put("rating", courier.getRating());
            map.put("completedOrders", courier.getCompletedOrders());
            map.put("serviceTime", getServiceTime(courier.getCreatedAt()));
            map.put("serviceArea", courier.getServiceArea());
            
            // 查询标签
            List<CourierTag> tags = courierTagMapper.findByCourierId(courier.getId());
            List<String> tagList = tags.stream()
                    .limit(3) // 最多返回3个标签
                    .map(CourierTag::getTagName)
                    .collect(Collectors.toList());
            map.put("tags", tagList);
            
            // 获取距离（查询时已计算好）
            map.put("distance", courier.getResponseTime() / 100.0); // 这里使用responseTime暂存距离数据
            
            resultList.add(map);
        }
        
        // 创建新的分页对象返回
        IPage<Map<String, Object>> resultPage = new Page<>(page.getCurrent(), page.getSize(), courierPage.getTotal());
        resultPage.setRecords(resultList);
        
        return resultPage;
    }

    /**
     * 获取快递员标签
     */
    @Override
    public List<CourierTag> getCourierTags(Long courierId) {
        return courierTagMapper.findByCourierId(courierId);
    }

    /**
     * 添加快递员标签
     */
    @Override
    @Transactional
    public boolean addCourierTag(Long courierId, String tagName, Integer tagType) {
        // 检查快递员是否存在
        Courier courier = courierMapper.selectById(courierId);
        if (courier == null) {
            throw new IllegalArgumentException("快递员不存在");
        }
        
        // 查询是否已存在该标签
        LambdaQueryWrapper<CourierTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourierTag::getCourierId, courierId)
                   .eq(CourierTag::getTagName, tagName);
        CourierTag existTag = courierTagMapper.selectOne(queryWrapper);
        
        if (existTag != null) {
            // 标签已存在，增加计数
            courierTagMapper.incrementTagCount(courierId, tagName);
        } else {
            // 标签不存在，新增标签
            CourierTag tag = new CourierTag();
            tag.setCourierId(courierId);
            tag.setTagName(tagName);
            tag.setTagType(tagType);
            tag.setCount(1);
            tag.setCreatedAt(LocalDateTime.now());
            tag.setUpdatedAt(LocalDateTime.now());
            courierTagMapper.insert(tag);
        }
        
        return true;
    }

    /**
     * 审核快递员申请
     */
    @Override
    @Transactional
    public boolean auditCourier(Long courierId, Integer auditStatus, String auditRemark) {
        // 检查快递员是否存在
        Courier courier = courierMapper.selectById(courierId);
        if (courier == null) {
            throw new IllegalArgumentException("快递员不存在");
        }
        
        // 更新审核状态
        courier.setAuditStatus(auditStatus);
        courier.setUpdatedAt(LocalDateTime.now());
        
        // 如果审核通过，设置服务状态为接单中
        if (auditStatus == 1) {
            courier.setServiceStatus(1);
        }
        
        return courierMapper.updateById(courier) > 0;
    }

    /**
     * 修改服务状态
     */
    @Override
    public boolean updateServiceStatus(Long courierId, Integer serviceStatus) {
        return courierMapper.updateServiceStatus(courierId, serviceStatus) > 0;
    }

    /**
     * 更新快递员评分
     */
    @Override
    public boolean updateRating(Long courierId, BigDecimal rating) {
        return courierMapper.updateRating(courierId, rating) > 0;
    }

    /**
     * 增加完成订单数
     */
    @Override
    public boolean incrementCompletedOrders(Long courierId) {
        return courierMapper.incrementCompletedOrders(courierId) > 0;
    }

    /**
     * 获取我的快递员信息
     */
    @Override
    public Map<String, Object> getMyCourierInfo(Long userId) {
        // 查询快递员信息
        LambdaQueryWrapper<Courier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Courier::getUserId, userId);
        Courier courier = courierMapper.selectOne(queryWrapper);
        
        if (courier == null) {
            throw new IllegalArgumentException("您还不是快递员");
        }
        
        return getCourierDetail(courier.getId());
    }

    /**
     * 更新快递员信息
     */
    @Override
    public boolean updateCourierInfo(Long courierId, Courier courier) {
        // 检查快递员是否存在
        Courier existCourier = courierMapper.selectById(courierId);
        if (existCourier == null) {
            throw new IllegalArgumentException("快递员不存在");
        }
        
        // 设置不可修改的字段
        courier.setId(courierId);
        courier.setUserId(existCourier.getUserId());
        courier.setAuditStatus(existCourier.getAuditStatus());
        courier.setRating(existCourier.getRating());
        courier.setBalance(existCourier.getBalance());
        courier.setCompletedOrders(existCourier.getCompletedOrders());
        courier.setIdCardFront(existCourier.getIdCardFront());
        courier.setIdCardBack(existCourier.getIdCardBack());
        courier.setCreatedAt(existCourier.getCreatedAt());
        courier.setUpdatedAt(LocalDateTime.now());
        
        return courierMapper.updateById(courier) > 0;
    }
    
    /**
     * 计算服务时间（月）
     */
    private int getServiceTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        return (now.getYear() - createdAt.getYear()) * 12 + (now.getMonthValue() - createdAt.getMonthValue());
    }

    /**
     * 获取推荐的快递员列表
     */
    @Override
    public List<Courier> getRecommendedCouriers(int limit) {
        // 按照评分和完成订单数量查询推荐的快递员
        LambdaQueryWrapper<Courier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Courier::getAuditStatus, 1) // 审核通过的
                  .eq(Courier::getServiceStatus, 1) // 服务中的
                  .orderByDesc(Courier::getRating)  // 按评分降序
                  .orderByDesc(Courier::getCompletedOrders) // 按完成订单数降序
                  .last("LIMIT " + limit);
                  
        return courierMapper.selectList(queryWrapper);
    }

    /**
     * 根据位置信息获取附近的快递员
     * @param latitude 纬度
     * @param longitude 经度
     * @param limit 限制数量
     * @return 快递员列表
     */
    @Override
    public List<Courier> getNearestCouriers(double latitude, double longitude, int limit) {
        logger.info("获取附近快递员: 纬度={}, 经度={}, 限制={}", latitude, longitude, limit);
        
        try {
            // 获取所有激活状态的快递员
            List<Courier> allCouriers = getAllActiveCouriers();
            
            // 计算距离并排序
            List<Courier> sortedCouriers = allCouriers.stream()
                .filter(courier -> courier.getLatitude() != null && courier.getLongitude() != null)
                .sorted((c1, c2) -> {
                    // 计算距离
                    double distance1 = calculateDistance(latitude, longitude, 
                                        c1.getLatitude(), c1.getLongitude());
                    double distance2 = calculateDistance(latitude, longitude, 
                                        c2.getLatitude(), c2.getLongitude());
                    return Double.compare(distance1, distance2);
                })
                .limit(limit)
                .collect(Collectors.toList());
            
            // 设置距离信息
            for (Courier courier : sortedCouriers) {
                double distance = calculateDistance(latitude, longitude, 
                                    courier.getLatitude(), courier.getLongitude());
                courier.setDistance(Math.round(distance * 10) / 10.0); // 保留一位小数
            }
            
            return sortedCouriers;
        } catch (Exception e) {
            logger.error("获取附近快递员失败", e);
            // 如果发生异常，返回推荐快递员
            return getRecommendedCouriers(limit);
        }
    }
    
    /**
     * 获取所有激活状态的快递员
     * @return 快递员列表
     */
    private List<Courier> getAllActiveCouriers() {
        // 这里简化处理，直接返回所有审核通过的快递员
        // 实际项目中应该从数据库查询，并增加状态过滤
        List<Courier> couriers = new ArrayList<>();
        
        // TODO: 替换为实际数据库查询
        // 模拟数据
        Courier c1 = new Courier();
        c1.setId(1L);
        c1.setName("张师傅");
        c1.setRating(4.8);
        c1.setCompletedOrders(128);
        c1.setLatitude(30.5866);
        c1.setLongitude(104.0655);
        c1.setAvatarUrl("/static/images/courier-1.png");
        
        Courier c2 = new Courier();
        c2.setId(2L);
        c2.setName("李师傅");
        c2.setRating(4.9);
        c2.setCompletedOrders(156);
        c2.setLatitude(30.5830);
        c2.setLongitude(104.0682);
        c2.setAvatarUrl("/static/images/courier-2.png");
        
        Courier c3 = new Courier();
        c3.setId(3L);
        c3.setName("王师傅");
        c3.setRating(4.7);
        c3.setCompletedOrders(98);
        c3.setLatitude(30.5920);
        c3.setLongitude(104.0612);
        c3.setAvatarUrl("/static/images/courier-3.png");
        
        Courier c4 = new Courier();
        c4.setId(4L);
        c4.setName("刘师傅");
        c4.setRating(4.6);
        c4.setCompletedOrders(76);
        c4.setLatitude(30.5890);
        c4.setLongitude(104.0730);
        c4.setAvatarUrl("/static/images/courier-4.png");
        
        Courier c5 = new Courier();
        c5.setId(5L);
        c5.setName("赵师傅");
        c5.setRating(4.5);
        c5.setCompletedOrders(64);
        c5.setLatitude(30.5950);
        c5.setLongitude(104.0580);
        c5.setAvatarUrl("/static/images/courier-5.png");
        
        couriers.add(c1);
        couriers.add(c2);
        couriers.add(c3);
        couriers.add(c4);
        couriers.add(c5);
        
        return couriers;
    }
    
    /**
     * 使用Haversine公式计算两点间的距离（单位：公里）
     * @param lat1 纬度1
     * @param lon1 经度1
     * @param lat2 纬度2
     * @param lon2 经度2
     * @return 距离，单位为公里
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 地球平均半径（单位：千米）
        final int R = 6371;
        
        // 将经纬度转换为弧度
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        // Haversine公式
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // 计算距离
        return R * c;
    }
} 