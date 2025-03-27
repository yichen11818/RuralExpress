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
} 