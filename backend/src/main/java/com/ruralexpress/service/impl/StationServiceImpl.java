package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.ExpressCompany;
import com.ruralexpress.entity.Station;
import com.ruralexpress.mapper.ExpressCompanyMapper;
import com.ruralexpress.mapper.StationMapper;
import com.ruralexpress.service.StationService;
import lombok.extern.slf4j.Slf4j;
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
 * 服务点服务实现类
 */
@Slf4j
@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationMapper stationMapper;
    
    @Autowired
    private ExpressCompanyMapper expressCompanyMapper;

    /**
     * 创建服务点
     */
    @Override
    @Transactional
    public Long createStation(Station station, String managerName, String managerPhone, List<String> photos, List<Long> companyIds) {
        // 设置初始值
        station.setRating(new BigDecimal("5.0"));
        station.setReviewCount(0);
        station.setStatus(1); // 正常状态
        station.setManagerName(managerName);
        station.setManagerPhone(managerPhone);
        station.setCreatedAt(LocalDateTime.now());
        station.setUpdatedAt(LocalDateTime.now());
        
        // 保存服务点信息
        stationMapper.insert(station);
        Long stationId = station.getId();
        
        // 保存照片
        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                stationMapper.insertPhoto(stationId, photos.get(i), i + 1);
            }
        }
        
        // 关联快递公司
        if (companyIds != null && !companyIds.isEmpty()) {
            for (Long companyId : companyIds) {
                stationMapper.insertStationCompany(stationId, companyId);
            }
        }
        
        return stationId;
    }

    /**
     * 获取服务点详情
     */
    @Override
    public Map<String, Object> getStationDetail(Long stationId) {
        // 查询服务点信息
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        // 查询照片
        List<String> photos = stationMapper.findStationPhotos(stationId);
        
        // 查询支持的快递公司
        List<Map<String, Object>> companies = stationMapper.findSupportedCompanies(stationId);
        
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", station.getId());
        result.put("name", station.getName());
        result.put("logo", station.getLogo());
        result.put("phone", station.getPhone());
        result.put("province", station.getProvince());
        result.put("city", station.getCity());
        result.put("district", station.getDistrict());
        result.put("address", station.getAddress());
        result.put("longitude", station.getLongitude());
        result.put("latitude", station.getLatitude());
        result.put("businessHours", station.getBusinessHours());
        result.put("rating", station.getRating());
        result.put("reviewCount", station.getReviewCount());
        result.put("status", station.getStatus());
        result.put("managerName", station.getManagerName());
        result.put("managerPhone", station.getManagerPhone());
        result.put("photos", photos);
        result.put("companies", companies);
        
        return result;
    }

    /**
     * 按区域查询服务点
     */
    @Override
    public IPage<Map<String, Object>> findStationsByArea(Page<Station> page, String province, String city, String district, Integer sortType) {
        // 查询服务点列表
        IPage<Station> stationPage = stationMapper.findByArea(page, province, city, district);
        
        // 处理排序（数据库层已处理，此处无需额外排序）
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Station station : stationPage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", station.getId());
            map.put("name", station.getName());
            map.put("logo", station.getLogo());
            map.put("address", getFullAddress(station));
            map.put("businessHours", station.getBusinessHours());
            map.put("rating", station.getRating());
            map.put("reviewCount", station.getReviewCount());
            
            // 查询照片（只返回第一张作为封面）
            List<String> photos = stationMapper.findStationPhotos(station.getId());
            if (!photos.isEmpty()) {
                map.put("coverPhoto", photos.get(0));
            } else {
                map.put("coverPhoto", "");
            }
            
            // 查询支持的快递公司（只返回公司名称列表）
            List<Map<String, Object>> companies = stationMapper.findSupportedCompanies(station.getId());
            List<String> companyNames = companies.stream()
                    .map(company -> (String) company.get("name"))
                    .collect(Collectors.toList());
            map.put("supportCompanies", companyNames);
            
            // 计算距离（这里是模拟数据，实际应根据经纬度计算）
            map.put("distance", 1.0 + Math.random() * 2.0);
            
            resultList.add(map);
        }
        
        // 创建新的分页对象返回
        IPage<Map<String, Object>> resultPage = new Page<>(page.getCurrent(), page.getSize(), stationPage.getTotal());
        resultPage.setRecords(resultList);
        
        return resultPage;
    }

    /**
     * 按距离查询服务点
     */
    @Override
    public IPage<Map<String, Object>> findStationsByDistance(Page<Station> page, BigDecimal longitude, BigDecimal latitude, Integer distance) {
        // 查询服务点列表
        IPage<Station> stationPage = stationMapper.findNearby(page, longitude, latitude, distance);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Station station : stationPage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", station.getId());
            map.put("name", station.getName());
            map.put("logo", station.getLogo());
            map.put("address", getFullAddress(station));
            map.put("businessHours", station.getBusinessHours());
            map.put("rating", station.getRating());
            map.put("reviewCount", station.getReviewCount());
            
            // 查询照片（只返回第一张作为封面）
            List<String> photos = stationMapper.findStationPhotos(station.getId());
            if (!photos.isEmpty()) {
                map.put("coverPhoto", photos.get(0));
            } else {
                map.put("coverPhoto", "");
            }
            
            // 查询支持的快递公司（只返回公司名称列表）
            List<Map<String, Object>> companies = stationMapper.findSupportedCompanies(station.getId());
            List<String> companyNames = companies.stream()
                    .map(company -> (String) company.get("name"))
                    .collect(Collectors.toList());
            map.put("supportCompanies", companyNames);
            
            // 获取距离（查询时已计算好）
            map.put("distance", station.getRating().doubleValue() - 4.0); // 这里使用rating暂存距离数据
            
            resultList.add(map);
        }
        
        // 创建新的分页对象返回
        IPage<Map<String, Object>> resultPage = new Page<>(page.getCurrent(), page.getSize(), stationPage.getTotal());
        resultPage.setRecords(resultList);
        
        return resultPage;
    }

    /**
     * 获取服务点支持的快递公司
     */
    @Override
    public List<Map<String, Object>> getStationCompanies(Long stationId) {
        return stationMapper.findSupportedCompanies(stationId);
    }

    /**
     * 获取服务点照片
     */
    @Override
    public List<String> getStationPhotos(Long stationId) {
        return stationMapper.findStationPhotos(stationId);
    }

    /**
     * 更新服务点评分
     */
    @Override
    public boolean updateRating(Long stationId, BigDecimal rating) {
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        // 更新评分
        station.setRating(rating);
        return stationMapper.updateById(station) > 0;
    }

    /**
     * 更新服务点状态
     */
    @Override
    public boolean updateStatus(Long stationId, Integer status) {
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        // 更新状态
        station.setStatus(status);
        return stationMapper.updateById(station) > 0;
    }

    /**
     * 获取附近的服务点
     */
    @Override
    public List<Map<String, Object>> getNearbyStations(Long stationId, Integer limit) {
        // 查询当前服务点
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        // 查询附近服务点
        List<Station> nearbyStations = stationMapper.findNearbyStations(
                station.getLongitude(), station.getLatitude(), 5000, limit, stationId);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Station nearby : nearbyStations) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", nearby.getId());
            map.put("name", nearby.getName());
            map.put("logo", nearby.getLogo());
            map.put("address", getFullAddress(nearby));
            map.put("rating", nearby.getRating());
            
            // 计算距离（这里是模拟数据，实际应根据经纬度计算）
            double distance = calculateDistance(
                    station.getLatitude().doubleValue(), station.getLongitude().doubleValue(),
                    nearby.getLatitude().doubleValue(), nearby.getLongitude().doubleValue());
            map.put("distance", distance);
            
            resultList.add(map);
        }
        
        return resultList;
    }

    /**
     * 更新服务点信息
     */
    @Override
    public boolean updateStationInfo(Long stationId, Station station) {
        // 检查服务点是否存在
        Station existStation = stationMapper.selectById(stationId);
        if (existStation == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        // 设置不可修改的字段
        station.setId(stationId);
        station.setRating(existStation.getRating());
        station.setReviewCount(existStation.getReviewCount());
        station.setCreatedAt(existStation.getCreatedAt());
        station.setUpdatedAt(LocalDateTime.now());
        
        return stationMapper.updateById(station) > 0;
    }

    /**
     * 添加服务点照片
     */
    @Override
    public boolean addStationPhoto(Long stationId, String photoUrl, Integer sort) {
        // 检查服务点是否存在
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        return stationMapper.insertPhoto(stationId, photoUrl, sort) > 0;
    }

    /**
     * 删除服务点照片
     */
    @Override
    public boolean deleteStationPhoto(Long photoId) {
        return stationMapper.deletePhoto(photoId) > 0;
    }

    /**
     * 添加支持的快递公司
     */
    @Override
    public boolean addStationCompany(Long stationId, Long companyId) {
        // 检查服务点是否存在
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        // 检查快递公司是否存在
        ExpressCompany company = expressCompanyMapper.selectById(companyId);
        if (company == null) {
            throw new IllegalArgumentException("快递公司不存在");
        }
        
        // 检查是否已关联
        int count = stationMapper.checkStationCompany(stationId, companyId);
        if (count > 0) {
            throw new IllegalArgumentException("该快递公司已添加");
        }
        
        return stationMapper.insertStationCompany(stationId, companyId) > 0;
    }

    /**
     * 删除支持的快递公司
     */
    @Override
    public boolean deleteStationCompany(Long stationId, Long companyId) {
        return stationMapper.deleteStationCompany(stationId, companyId) > 0;
    }
    
    /**
     * 获取完整地址
     */
    private String getFullAddress(Station station) {
        return station.getProvince() + " " + station.getCity() + " " + station.getDistrict() + " " + station.getAddress();
    }
    
    /**
     * 根据经纬度计算距离（使用Haversine公式）
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径（km）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    /**
     * 根据关键词搜索服务点
     */
    @Override
    public List<Map<String, Object>> searchStations(String keyword, Integer limit) {
        List<Station> stations = stationMapper.searchStations(keyword, limit);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Station station : stations) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", station.getId());
            map.put("name", station.getName());
            map.put("logo", station.getLogo());
            map.put("phone", station.getPhone());
            map.put("address", getFullAddress(station));
            map.put("businessHours", station.getBusinessHours());
            map.put("rating", station.getRating());
            map.put("reviewCount", station.getReviewCount());
            map.put("status", station.getStatus());
            
            // 查询照片（只返回第一张作为封面）
            List<String> photos = stationMapper.findStationPhotos(station.getId());
            if (!photos.isEmpty()) {
                map.put("coverPhoto", photos.get(0));
            } else {
                map.put("coverPhoto", "");
            }
            
            // 查询支持的快递公司（只返回公司名称列表）
            List<Map<String, Object>> companies = stationMapper.findSupportedCompanies(station.getId());
            List<String> companyNames = companies.stream()
                    .map(company -> (String) company.get("name"))
                    .collect(Collectors.toList());
            map.put("supportCompanies", companyNames);
            
            resultList.add(map);
        }
        
        return resultList;
    }
    
    /**
     * 管理员功能：获取服务点列表，带分页
     * @param page 当前页
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @param status 服务点状态
     * @param area 区域（省市区）
     * @return 包含服务点列表和统计数据的结果
     */
    @Override
    public Map<String, Object> getStationsWithPagination(Integer page, Integer pageSize, String keyword, Integer status, String area) {
        log.info("查询服务点列表: page={}, pageSize={}, keyword={}, status={}, area={}", 
                page, pageSize, keyword, status, area);
        
        // 分页参数
        Page<Station> pageParam = new Page<>(page, pageSize);
        
        // 查询条件
        LambdaQueryWrapper<Station> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词查询
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like(Station::getName, keyword)
                    .or()
                    .like(Station::getAddress, keyword)
                    .or()
                    .like(Station::getPhone, keyword);
        }
        
        // 状态查询
        if (status != null) {
            queryWrapper.eq(Station::getStatus, status);
        }
        
        // 区域查询
        if (area != null && !area.isEmpty()) {
            String[] areaParts = area.split(" ");
            if (areaParts.length > 0 && !areaParts[0].isEmpty()) {
                queryWrapper.eq(Station::getProvince, areaParts[0]);
            }
            if (areaParts.length > 1 && !areaParts[1].isEmpty()) {
                queryWrapper.eq(Station::getCity, areaParts[1]);
            }
            if (areaParts.length > 2 && !areaParts[2].isEmpty()) {
                queryWrapper.eq(Station::getDistrict, areaParts[2]);
            }
        }
        
        // 按更新时间倒序排序
        queryWrapper.orderByDesc(Station::getUpdatedAt);
        
        // 执行查询
        Page<Station> result = stationMapper.selectPage(pageParam, queryWrapper);
        
        // 转换为Map返回
        Map<String, Object> map = new HashMap<>();
        map.put("total", result.getTotal());
        map.put("pages", result.getPages());
        map.put("current", result.getCurrent());
        map.put("size", result.getSize());
        map.put("records", result.getRecords());
        
        // 查询统计数据
        LambdaQueryWrapper<Station> countWrapper = new LambdaQueryWrapper<>();
        long totalCount = stationMapper.selectCount(null);
        
        countWrapper.eq(Station::getStatus, 0); // 正常运营
        long activeCount = stationMapper.selectCount(countWrapper);
        
        countWrapper.clear();
        countWrapper.eq(Station::getStatus, 1); // 临时关闭
        long closedCount = stationMapper.selectCount(countWrapper);
        
        countWrapper.clear();
        countWrapper.eq(Station::getStatus, 2); // 永久关闭
        long permanentClosedCount = stationMapper.selectCount(countWrapper);
        
        // 添加统计数据
        map.put("statistics", new HashMap<String, Object>() {{
            put("total", totalCount);
            put("active", activeCount);
            put("closed", closedCount);
            put("permanentClosed", permanentClosedCount);
        }});
        
        return map;
    }
    
    /**
     * 管理员功能：创建服务点
     * @param stationData 服务点数据Map
     * @return 创建的服务点
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Station createStation(Map<String, Object> stationData) {
        log.info("创建服务点: {}", stationData);
        
        // 创建服务点对象
        Station station = new Station();
        
        // 设置服务点基本信息
        station.setName((String) stationData.get("name"));
        station.setLogo((String) stationData.get("logo"));
        station.setPhone((String) stationData.get("phone"));
        station.setProvince((String) stationData.get("province"));
        station.setCity((String) stationData.get("city"));
        station.setDistrict((String) stationData.get("district"));
        station.setAddress((String) stationData.get("address"));
        
        // 设置经纬度
        if (stationData.get("longitude") != null) {
            station.setLongitude(new BigDecimal(stationData.get("longitude").toString()));
        }
        if (stationData.get("latitude") != null) {
            station.setLatitude(new BigDecimal(stationData.get("latitude").toString()));
        }
        
        // 设置营业时间
        station.setBusinessHours((String) stationData.get("businessHours"));
        
        // 设置管理员信息
        station.setManagerName((String) stationData.get("managerName"));
        station.setManagerPhone((String) stationData.get("managerPhone"));
        
        // 设置初始值
        station.setRating(new BigDecimal("5.0"));
        station.setReviewCount(0);
        station.setStatus(0); // 正常状态
        
        LocalDateTime now = LocalDateTime.now();
        station.setCreatedAt(now);
        station.setUpdatedAt(now);
        
        // 保存服务点
        stationMapper.insert(station);
        
        // 处理照片
        @SuppressWarnings("unchecked")
        List<String> photos = (List<String>) stationData.get("photos");
        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                stationMapper.insertPhoto(station.getId(), photos.get(i), i + 1);
            }
        }
        
        // 处理快递公司
        @SuppressWarnings("unchecked")
        List<Integer> companyIds = (List<Integer>) stationData.get("companyIds");
        if (companyIds != null && !companyIds.isEmpty()) {
            for (Integer companyId : companyIds) {
                stationMapper.insertStationCompany(station.getId(), companyId.longValue());
            }
        }
        
        return station;
    }
    
    /**
     * 管理员功能：更新服务点
     * @param stationData 服务点数据Map
     * @return 更新后的服务点
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Station updateStation(Map<String, Object> stationData) {
        log.info("更新服务点: {}", stationData);
        
        // 获取服务点ID
        Long stationId = Long.valueOf(stationData.get("id").toString());
        
        // 检查服务点是否存在
        Station existingStation = stationMapper.selectById(stationId);
        if (existingStation == null) {
            log.warn("服务点不存在: id={}", stationId);
            throw new RuntimeException("服务点不存在");
        }
        
        // 更新服务点信息
        Station station = new Station();
        station.setId(stationId);
        station.setName((String) stationData.get("name"));
        station.setLogo((String) stationData.get("logo"));
        station.setPhone((String) stationData.get("phone"));
        station.setProvince((String) stationData.get("province"));
        station.setCity((String) stationData.get("city"));
        station.setDistrict((String) stationData.get("district"));
        station.setAddress((String) stationData.get("address"));
        
        // 设置经纬度
        if (stationData.get("longitude") != null) {
            station.setLongitude(new BigDecimal(stationData.get("longitude").toString()));
        }
        if (stationData.get("latitude") != null) {
            station.setLatitude(new BigDecimal(stationData.get("latitude").toString()));
        }
        
        // 设置营业时间
        station.setBusinessHours((String) stationData.get("businessHours"));
        
        // 设置管理员信息
        station.setManagerName((String) stationData.get("managerName"));
        station.setManagerPhone((String) stationData.get("managerPhone"));
        
        // 保留原有数据
        station.setRating(existingStation.getRating());
        station.setReviewCount(existingStation.getReviewCount());
        station.setStatus(existingStation.getStatus());
        station.setCreatedAt(existingStation.getCreatedAt());
        station.setUpdatedAt(LocalDateTime.now());
        
        // 更新服务点
        stationMapper.updateById(station);
        
        // 处理照片（先删除旧照片，再添加新照片）
        if (stationData.containsKey("photos")) {
            // 删除旧照片
            stationMapper.deleteStationPhotos(stationId);
            
            // 添加新照片
            @SuppressWarnings("unchecked")
            List<String> photos = (List<String>) stationData.get("photos");
            if (photos != null && !photos.isEmpty()) {
                for (int i = 0; i < photos.size(); i++) {
                    stationMapper.insertPhoto(stationId, photos.get(i), i + 1);
                }
            }
        }
        
        // 处理快递公司（先删除旧关联，再添加新关联）
        if (stationData.containsKey("companyIds")) {
            // 删除旧关联
            stationMapper.deleteStationCompanies(stationId);
            
            // 添加新关联
            @SuppressWarnings("unchecked")
            List<Integer> companyIds = (List<Integer>) stationData.get("companyIds");
            if (companyIds != null && !companyIds.isEmpty()) {
                for (Integer companyId : companyIds) {
                    stationMapper.insertStationCompany(stationId, companyId.longValue());
                }
            }
        }
        
        return station;
    }
    
    /**
     * 管理员功能：更新服务点状态
     * @param stationId 服务点ID
     * @param status 新状态
     * @return 更新后的服务点
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Station updateStationStatus(Long stationId, Integer status) {
        log.info("更新服务点状态: id={}, status={}", stationId, status);
        
        // 检查服务点是否存在
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            log.warn("服务点不存在: id={}", stationId);
            throw new RuntimeException("服务点不存在");
        }
        
        // 更新状态
        station.setStatus(status);
        station.setUpdatedAt(LocalDateTime.now());
        
        stationMapper.updateById(station);
        
        return station;
    }
    
    /**
     * 管理员功能：删除服务点
     * @param stationId 服务点ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStation(Long stationId) {
        log.info("删除服务点: id={}", stationId);
        
        // 检查服务点是否存在
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            log.warn("服务点不存在: id={}", stationId);
            return false;
        }
        
        try {
            // 删除服务点照片
            stationMapper.deleteStationPhotos(stationId);
            
            // 删除服务点支持的快递公司关联
            stationMapper.deleteStationCompanies(stationId);
            
            // 删除服务点
            int result = stationMapper.deleteById(stationId);
            
            return result > 0;
        } catch (Exception e) {
            log.error("删除服务点失败: id={}", stationId, e);
            throw new RuntimeException("删除服务点失败", e);
        }
    }
} 