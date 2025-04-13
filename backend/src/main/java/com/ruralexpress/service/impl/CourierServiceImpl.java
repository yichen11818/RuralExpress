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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruralexpress.dto.CourierDTO;

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

    private static final Logger logger = LoggerFactory.getLogger(CourierServiceImpl.class);

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
        IPage<Courier> courierPage = courierMapper.findByDistance(page, longitude, latitude, distance);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Courier courier : courierPage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", courier.getId());
            
            // 查询快递员关联的用户信息
            User user = userMapper.selectById(courier.getUserId());
            
            if (user != null) {
                map.put("name", user.getNickname() != null ? user.getNickname() : user.getPhone());
                map.put("phone", user.getPhone());
                map.put("avatar", user.getAvatar());
            }
            
            map.put("serviceArea", courier.getServiceArea());
            map.put("rating", courier.getRating());
            map.put("completedOrders", courier.getCompletedOrders());
            
            // 获取距离（查询时已计算好）
            // 注：CourierMapper.findByDistance 查询会计算距离并放入实体的某个未使用字段中
            // 如果没有适当的字段，则使用模拟距离数据
            try {
                // 尝试获取responseTime字段用于距离
                map.put("distance", courier.getResponseTime() / 60.0);
            } catch (Exception e) {
                // 如果没有适当的字段，使用模拟数据
                map.put("distance", 2.0 + Math.random() * 3.0);
            }
            
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
        try {
            // 按照评分和完成订单数量查询推荐的快递员
            LambdaQueryWrapper<Courier> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Courier::getAuditStatus, 1) // 审核通过的
                      .eq(Courier::getServiceStatus, 1) // 服务中的
                      .orderByDesc(Courier::getRating)  // 按评分降序
                      .orderByDesc(Courier::getCompletedOrders) // 按完成订单数降序
                      .last("LIMIT " + limit);
                      
            List<Courier> couriers = courierMapper.selectList(queryWrapper);
            
            // 由于数据库中没有latitude和longitude字段，查询后手动为快递员设置模拟位置数据
            if (couriers != null && !couriers.isEmpty()) {
                // 成都市中心坐标
                double baseLat = 30.5702;
                double baseLng = 104.0665;
                
                for (int i = 0; i < couriers.size(); i++) {
                    Courier courier = couriers.get(i);
                    // 随机生成在成都市中心附近的坐标
                    double latOffset = (Math.random() - 0.5) * 0.1; // 约±5公里范围
                    double lngOffset = (Math.random() - 0.5) * 0.1;
                    
                    courier.setLatitude(baseLat + latOffset);
                    courier.setLongitude(baseLng + lngOffset);
                }
            } else {
                // 如果数据库没有数据，返回空列表
                return new ArrayList<>();
            }
            
            return couriers;
        } catch (Exception e) {
            logger.error("获取推荐快递员失败", e);
            // 出现异常时返回空列表
            return new ArrayList<>();
        }
    }

    /**
     * 获取所有激活状态的快递员
     * @return 快递员列表
     */
    private List<Courier> getAllActiveCouriers() {
        try {
            // 尝试从数据库查询
            LambdaQueryWrapper<Courier> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Courier::getAuditStatus, 1) // 审核通过的
                      .eq(Courier::getServiceStatus, 1); // 服务中的
                      
            List<Courier> couriers = courierMapper.selectList(queryWrapper);
            
            // 为查询结果添加位置信息
            if (couriers != null && !couriers.isEmpty()) {
                // 成都市中心坐标
                double baseLat = 30.5702;
                double baseLng = 104.0665;
                
                for (int i = 0; i < couriers.size(); i++) {
                    Courier courier = couriers.get(i);
                    // 这里可以调用地图API获取真实坐标，暂时使用随机生成的坐标
                    if (courier.getLatitude() == null || courier.getLongitude() == null) {
                        // 随机生成在成都市中心附近的坐标
                        double latOffset = (Math.random() - 0.5) * 0.1; // 约±5公里范围
                        double lngOffset = (Math.random() - 0.5) * 0.1;
                        
                        courier.setLatitude(baseLat + latOffset);
                        courier.setLongitude(baseLng + lngOffset);
                    }
                }
                
                return couriers;
            }
            
            // 如果数据库查询结果为空，返回空列表
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("从数据库获取快递员失败", e);
            // 如果数据库查询失败，返回空列表
            return new ArrayList<>();
        }
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

    @Override
    public List<Map<String, Object>> searchCouriers(String keyword, Integer limit) {
        List<Courier> couriers = courierMapper.searchCouriers(keyword, limit);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Courier courier : couriers) {
            Map<String, Object> map = new HashMap<>();
            
            // 获取用户信息
            User user = userMapper.selectById(courier.getUserId());
            
            map.put("id", courier.getId());
            map.put("userId", courier.getUserId());
            map.put("name", user != null ? (user.getRealName() != null ? user.getRealName() : user.getNickname()) : "未知快递员");
            map.put("phone", user != null ? user.getPhone() : "");
            map.put("avatar", user != null ? user.getAvatar() : "");
            map.put("serviceArea", courier.getServiceArea());
            map.put("serviceStatus", courier.getServiceStatus());
            map.put("serviceStatusText", courier.getServiceStatus() == 1 ? "接单中" : "休息中");
            map.put("rating", courier.getRating());
            map.put("deliveryCount", courier.getCompletedOrders());
            map.put("company", "乡递通快递"); // 可根据实际情况从关联表查询
            
            resultList.add(map);
        }
        
        return resultList;
    }

    /**
     * 管理员功能：获取快递员列表
     */
    @Override
    public Map<String, Object> getCouriersWithPagination(Integer page, Integer pageSize, String keyword, Integer status, Double rating) {
        logger.info("分页查询快递员列表: page={}, pageSize={}, keyword={}, status={}, rating={}", 
                page, pageSize, keyword, status, rating);
                
        // 构建查询条件
        LambdaQueryWrapper<Courier> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果有关键字，查询相关字段
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 查询用户表中匹配的用户ID
            List<Long> userIds = userMapper.findUserIdsByKeyword(keyword);
            
            // 如果找到匹配的用户，查询这些用户ID的快递员
            if (!userIds.isEmpty()) {
                queryWrapper.in(Courier::getUserId, userIds);
            } else {
                // 没找到匹配的用户，返回空结果
                Map<String, Object> emptyResult = new HashMap<>();
                emptyResult.put("total", 0);
                emptyResult.put("list", new ArrayList<>());
                return emptyResult;
            }
        }
        
        // 按状态筛选
        if (status != null) {
            queryWrapper.eq(Courier::getStatus, status);
        }
        
        // 按评分筛选
        if (rating != null && rating > 0) {
            queryWrapper.ge(Courier::getRating, new BigDecimal(rating.toString()));
        }
        
        // 创建分页对象
        Page<Courier> courierPage = new Page<>(page, pageSize);
        
        // 执行查询
        IPage<Courier> courierIPage = courierMapper.selectPage(courierPage, queryWrapper);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Courier courier : courierIPage.getRecords()) {
            // 查询对应的用户信息
            User user = userMapper.selectById(courier.getUserId());
            if (user == null) continue;
            
            Map<String, Object> courierMap = new HashMap<>();
            courierMap.put("id", courier.getId());
            courierMap.put("phone", user.getPhone());
            courierMap.put("name", user.getRealName() != null ? user.getRealName() : user.getNickname());
            courierMap.put("avatar", user.getAvatar());
            courierMap.put("idCard", courier.getIdCardFront()); // 使用idCardFront字段代替idCard
            courierMap.put("workNo", "WK" + courier.getId()); // 使用ID生成工号
            courierMap.put("serviceArea", courier.getServiceArea());
            courierMap.put("status", courier.getStatus());
            courierMap.put("rating", courier.getRating());
            courierMap.put("deliveryCount", courier.getCompletedOrders());
            courierMap.put("registerTime", courier.getCreatedAt());
            
            resultList.add(courierMap);
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", courierIPage.getTotal());
        result.put("list", resultList);
        
        return result;
    }
    
    /**
     * 管理员功能：根据ID查询快递员
     */
    @Override
    public Courier findById(Long id) {
        logger.info("根据ID查询快递员: id={}", id);
        
        if (id == null) {
            logger.warn("快递员ID为空");
            return null;
        }
        
        try {
            return courierMapper.selectById(id);
        } catch (Exception e) {
            logger.error("查询快递员失败: id={}", id, e);
            return null;
        }
    }
    
    /**
     * 管理员功能：创建快递员(使用DTO)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Courier createCourierWithDTO(CourierDTO courierDTO) {
        logger.info("使用DTO创建快递员: {}", courierDTO);
        
        try {
            // 检查手机号是否已注册
            User existUser = findUserByPhone(courierDTO.getPhone());
            Long userId;
            
            if (existUser == null) {
                // 创建新用户
                User user = new User();
                user.setPhone(courierDTO.getPhone());
                user.setPassword(courierDTO.getPassword());  // 应该是加密的密码
                user.setNickname(courierDTO.getName());
                user.setRealName(courierDTO.getName());
                user.setUserType(1);  // 设置为快递员
                user.setStatus(0);    // 正常状态
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                
                userMapper.insert(user);
                userId = user.getId();
            } else {
                // 使用现有用户
                userId = existUser.getId();
                
                // 更新用户类型为快递员
                existUser.setUserType(1);
                userMapper.updateById(existUser);
            }
            
            // 创建快递员实体
            Courier courier = new Courier();
            courier.setUserId(userId);
            courier.setStatus(courierDTO.getStatus() != null ? courierDTO.getStatus() : 0);
            courier.setServiceArea(courierDTO.getServiceArea());
            courier.setRating(new BigDecimal("5.0"));  // 默认评分为5
            courier.setBalance(new BigDecimal("0"));   // 默认余额为0
            courier.setCompletedOrders(0);  // 默认完成订单数为0
            courier.setServiceStatus(0);    // 默认休息中
            courier.setAuditStatus(1);      // 默认审核通过
            courier.setCreatedAt(LocalDateTime.now());
            courier.setUpdatedAt(LocalDateTime.now());
            
            // 设置身份证照片URL
            courier.setIdCardFront(courierDTO.getIdCardFront());
            courier.setIdCardBack(courierDTO.getIdCardBack());
            
            // 保存快递员
            courierMapper.insert(courier);
            
            return courier;
        } catch (Exception e) {
            logger.error("创建快递员失败", e);
            throw new RuntimeException("创建快递员失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 管理员功能：更新快递员(使用DTO)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Courier updateCourierWithDTO(CourierDTO courierDTO) {
        logger.info("使用DTO更新快递员: {}", courierDTO);
        
        try {
            // 检查快递员是否存在
            Courier existCourier = courierMapper.selectById(courierDTO.getId());
            if (existCourier == null) {
                throw new IllegalArgumentException("快递员不存在: id=" + courierDTO.getId());
            }
            
            // 更新快递员信息
            existCourier.setServiceArea(courierDTO.getServiceArea());
            existCourier.setStatus(courierDTO.getStatus());
            existCourier.setUpdatedAt(LocalDateTime.now());
            
            courierMapper.updateById(existCourier);
            
            // 如果需要更新用户名称，同时更新用户表
            if (courierDTO.getName() != null) {
                User user = userMapper.selectById(existCourier.getUserId());
                if (user != null) {
                    user.setRealName(courierDTO.getName());
                    userMapper.updateById(user);
                }
            }
            
            return existCourier;
        } catch (Exception e) {
            logger.error("更新快递员失败", e);
            throw new RuntimeException("更新快递员失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据手机号查找用户
     */
    private User findUserByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return null;
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 管理员功能：删除快递员
     * @param id 快递员ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCourier(Long id) {
        logger.info("删除快递员: id={}", id);
        
        // 检查快递员是否存在
        Courier courier = courierMapper.selectById(id);
        if (courier == null) {
            logger.warn("快递员不存在: id={}", id);
            return false;
        }
        
        try {
            // 检查是否有关联的订单
            int orderCount = courierMapper.countCourierOrders(id);
            if (orderCount > 0) {
                logger.warn("快递员已有关联订单，无法删除: id={}, orderCount={}", id, orderCount);
                throw new IllegalArgumentException("该快递员已有关联订单，无法删除");
            }
            
            // 删除快递员标签
            LambdaQueryWrapper<CourierTag> tagQueryWrapper = new LambdaQueryWrapper<>();
            tagQueryWrapper.eq(CourierTag::getCourierId, id);
            courierTagMapper.delete(tagQueryWrapper);
            
            // 获取用户ID
            Long userId = courier.getUserId();
            
            // 删除快递员
            int result = courierMapper.deleteById(id);
            
            // 更新用户类型
            if (userId != null) {
                User user = userMapper.selectById(userId);
                if (user != null) {
                    user.setUserType(0); // 设置为普通用户
                    userMapper.updateById(user);
                }
            }
            
            return result > 0;
        } catch (Exception e) {
            logger.error("删除快递员失败: id={}", id, e);
            throw new RuntimeException("删除快递员失败: " + e.getMessage(), e);
        }
    }

    /**
     * 管理员功能：创建快递员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Courier createCourier(Courier courier) {
        logger.info("创建快递员: {}", courier);
        
        try {
            // 检查用户是否存在
            User user = userMapper.selectById(courier.getUserId());
            if (user == null) {
                throw new IllegalArgumentException("用户不存在: userId=" + courier.getUserId());
            }
            
            // 设置基本信息
            if (courier.getStatus() == null) {
                courier.setStatus(0);  // 默认为正常状态
            }
            
            if (courier.getRating() == null) {
                courier.setRating(new BigDecimal("5.0"));  // 默认评分5.0
            }
            
            if (courier.getCompletedOrders() == null) {
                courier.setCompletedOrders(0);  // 默认完成订单数为0
            }
            
            courier.setCreatedAt(LocalDateTime.now());
            courier.setUpdatedAt(LocalDateTime.now());
            
            // 保存快递员信息
            courierMapper.insert(courier);
            
            // 更新用户类型为快递员
            if (user.getUserType() == null || user.getUserType() != 1) {
                user.setUserType(1);  // 设置为快递员
                userMapper.updateById(user);
            }
            
            return courier;
        } catch (Exception e) {
            logger.error("创建快递员失败", e);
            throw new RuntimeException("创建快递员失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 管理员功能：更新快递员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Courier updateCourier(Courier courier) {
        logger.info("更新快递员: {}", courier);
        
        try {
            // 检查快递员是否存在
            Courier existCourier = courierMapper.selectById(courier.getId());
            if (existCourier == null) {
                throw new IllegalArgumentException("快递员不存在: id=" + courier.getId());
            }
            
            // 保留创建时间
            courier.setCreatedAt(existCourier.getCreatedAt());
            courier.setUpdatedAt(LocalDateTime.now());
            
            // 更新快递员信息
            courierMapper.updateById(courier);
            
            return courier;
        } catch (Exception e) {
            logger.error("更新快递员失败", e);
            throw new RuntimeException("更新快递员失败: " + e.getMessage(), e);
        }
    }
} 