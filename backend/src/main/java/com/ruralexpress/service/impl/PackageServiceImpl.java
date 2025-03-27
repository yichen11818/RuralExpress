package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.ExpressCompany;
import com.ruralexpress.entity.Package;
import com.ruralexpress.entity.Station;
import com.ruralexpress.mapper.CourierMapper;
import com.ruralexpress.mapper.ExpressCompanyMapper;
import com.ruralexpress.mapper.PackageMapper;
import com.ruralexpress.mapper.StationMapper;
import com.ruralexpress.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包裹服务实现类
 */
@Service
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageMapper packageMapper;
    
    @Autowired
    private ExpressCompanyMapper expressCompanyMapper;
    
    @Autowired
    private StationMapper stationMapper;
    
    @Autowired
    private CourierMapper courierMapper;

    /**
     * 添加包裹
     */
    @Override
    @Transactional
    public Long addPackage(Package packageInfo) {
        // 设置初始状态
        packageInfo.setStatus(0); // 待收状态
        packageInfo.setCreatedAt(LocalDateTime.now());
        packageInfo.setUpdatedAt(LocalDateTime.now());
        
        // 保存包裹信息
        packageMapper.insert(packageInfo);
        
        // 添加物流记录
        if (packageInfo.getCompanyId() != null) {
            ExpressCompany company = expressCompanyMapper.selectById(packageInfo.getCompanyId());
            String companyName = company != null ? company.getName() : "未知";
            String trackingInfo = "包裹已被" + companyName + "揽收";
            addPackageTrackingRecord(packageInfo.getId(), trackingInfo, "");
        }
        
        return packageInfo.getId();
    }

    /**
     * 根据ID获取包裹信息
     */
    @Override
    public Map<String, Object> getPackageById(Long packageId) {
        // 查询包裹信息
        Package packageInfo = packageMapper.selectById(packageId);
        if (packageInfo == null) {
            throw new IllegalArgumentException("包裹不存在");
        }
        
        // 查询快递公司
        ExpressCompany company = null;
        if (packageInfo.getCompanyId() != null) {
            company = expressCompanyMapper.selectById(packageInfo.getCompanyId());
        }
        
        // 查询服务点
        Station station = null;
        if (packageInfo.getStationId() != null) {
            station = stationMapper.selectById(packageInfo.getStationId());
        }
        
        // 查询快递员
        Courier courier = null;
        if (packageInfo.getCourierId() != null) {
            courier = courierMapper.selectById(packageInfo.getCourierId());
        }
        
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("id", packageInfo.getId());
        result.put("userId", packageInfo.getUserId());
        result.put("trackingNo", packageInfo.getTrackingNo());
        result.put("receiverName", packageInfo.getReceiverName());
        result.put("receiverPhone", packageInfo.getReceiverPhone());
        result.put("receiverAddress", packageInfo.getReceiverAddress());
        result.put("status", packageInfo.getStatus());
        result.put("statusText", getStatusText(packageInfo.getStatus()));
        result.put("remark", packageInfo.getRemark());
        result.put("estimatedDeliveryTime", packageInfo.getEstimatedDeliveryTime());
        result.put("signedTime", packageInfo.getSignedTime());
        result.put("deliveryType", packageInfo.getDeliveryType());
        result.put("deliveryTypeText", getDeliveryTypeText(packageInfo.getDeliveryType()));
        result.put("createdAt", packageInfo.getCreatedAt());
        
        // 添加关联信息
        if (company != null) {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("id", company.getId());
            companyMap.put("name", company.getName());
            companyMap.put("logo", company.getLogo());
            companyMap.put("code", company.getCode());
            result.put("company", companyMap);
        }
        
        if (station != null) {
            Map<String, Object> stationMap = new HashMap<>();
            stationMap.put("id", station.getId());
            stationMap.put("name", station.getName());
            stationMap.put("phone", station.getPhone());
            stationMap.put("address", getStationAddress(station));
            result.put("station", stationMap);
        }
        
        if (courier != null) {
            Map<String, Object> courierMap = new HashMap<>();
            courierMap.put("id", courier.getId());
            courierMap.put("name", "快递员"); // 实际应从用户表获取姓名
            courierMap.put("phone", "1234567890"); // 实际应从用户表获取电话
            result.put("courier", courierMap);
        }
        
        return result;
    }

    /**
     * 根据运单号查询包裹
     */
    @Override
    public Map<String, Object> getPackageByTrackingNo(String trackingNo, Long companyId) {
        Package packageInfo = packageMapper.findByTrackingNo(trackingNo, companyId);
        if (packageInfo == null) {
            throw new IllegalArgumentException("包裹不存在，请确认运单号和快递公司是否正确");
        }
        
        return getPackageById(packageInfo.getId());
    }

    /**
     * 根据公司编码和运单号查询包裹
     */
    @Override
    public Map<String, Object> getPackageByTrackingNoAndCompanyCode(String trackingNo, String companyCode) {
        Package packageInfo = packageMapper.findByTrackingNoAndCompanyCode(trackingNo, companyCode);
        if (packageInfo == null) {
            throw new IllegalArgumentException("包裹不存在，请确认运单号和快递公司是否正确");
        }
        
        return getPackageById(packageInfo.getId());
    }

    /**
     * 获取用户待收包裹列表
     */
    @Override
    public IPage<Map<String, Object>> getUserPendingPackages(Page<Package> page, Long userId) {
        // 查询待收包裹
        IPage<Package> packagePage = packageMapper.findPendingByUserId(page, userId);
        
        // 转换为Map列表
        return convertPackagePage(packagePage);
    }

    /**
     * 获取用户已收包裹列表
     */
    @Override
    public IPage<Map<String, Object>> getUserReceivedPackages(Page<Package> page, Long userId) {
        // 查询已收包裹
        IPage<Package> packagePage = packageMapper.findReceivedByUserId(page, userId);
        
        // 转换为Map列表
        return convertPackagePage(packagePage);
    }

    /**
     * 获取服务点待取包裹列表
     */
    @Override
    public IPage<Map<String, Object>> getStationPendingPackages(Page<Package> page, Long stationId) {
        // 查询服务点待取包裹
        IPage<Package> packagePage = packageMapper.findByStationIdAndStatus(page, stationId, 0);
        
        // 转换为Map列表
        return convertPackagePage(packagePage);
    }

    /**
     * 获取快递员待送包裹列表
     */
    @Override
    public IPage<Map<String, Object>> getCourierPendingPackages(Page<Package> page, Long courierId) {
        // 查询快递员待送包裹
        IPage<Package> packagePage = packageMapper.findByCourierIdAndStatus(page, courierId, 0);
        
        // 转换为Map列表
        return convertPackagePage(packagePage);
    }

    /**
     * 更新包裹状态
     */
    @Override
    @Transactional
    public boolean updatePackageStatus(Long packageId, Integer status) {
        // 查询包裹信息
        Package packageInfo = packageMapper.selectById(packageId);
        if (packageInfo == null) {
            throw new IllegalArgumentException("包裹不存在");
        }
        
        // 更新状态
        int result = packageMapper.updateStatus(packageId, status);
        
        // 添加物流记录
        if (result > 0) {
            String trackingInfo = "包裹" + getStatusText(status);
            addPackageTrackingRecord(packageId, trackingInfo, "");
        }
        
        return result > 0;
    }

    /**
     * 签收包裹
     */
    @Override
    @Transactional
    public boolean signPackage(Long packageId, String signedImageUrl) {
        // 查询包裹信息
        Package packageInfo = packageMapper.selectById(packageId);
        if (packageInfo == null) {
            throw new IllegalArgumentException("包裹不存在");
        }
        
        // 更新状态为已签收
        packageInfo.setStatus(2); // 已签收
        packageInfo.setSignedTime(LocalDateTime.now());
        int result = packageMapper.updateById(packageInfo);
        
        // 添加物流记录
        if (result > 0) {
            String trackingInfo = "包裹已签收";
            addPackageTrackingRecord(packageId, trackingInfo, "");
            
            // 如果包裹分配了快递员，增加快递员完成订单数
            if (packageInfo.getCourierId() != null) {
                courierMapper.incrementCompletedOrders(packageInfo.getCourierId());
            }
        }
        
        return result > 0;
    }

    /**
     * 获取包裹物流记录
     */
    @Override
    public List<Map<String, Object>> getPackageTrackingRecords(Long packageId) {
        return packageMapper.findTrackingRecords(packageId);
    }

    /**
     * 添加包裹物流记录
     */
    @Override
    public boolean addPackageTrackingRecord(Long packageId, String info, String location) {
        LocalDateTime trackingTime = LocalDateTime.now();
        return packageMapper.insertTrackingRecord(packageId, info, trackingTime, location) > 0;
    }

    /**
     * 分配包裹到服务点
     */
    @Override
    @Transactional
    public boolean assignPackageToStation(Long packageId, Long stationId) {
        // 查询包裹信息
        Package packageInfo = packageMapper.selectById(packageId);
        if (packageInfo == null) {
            throw new IllegalArgumentException("包裹不存在");
        }
        
        // 查询服务点信息
        Station station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new IllegalArgumentException("服务点不存在");
        }
        
        // 更新包裹服务点
        packageInfo.setStationId(stationId);
        packageInfo.setDeliveryType(1); // 设置为服务点自提
        packageInfo.setUpdatedAt(LocalDateTime.now());
        int result = packageMapper.updateById(packageInfo);
        
        // 添加物流记录
        if (result > 0) {
            String trackingInfo = "包裹已分配到服务点: " + station.getName();
            addPackageTrackingRecord(packageId, trackingInfo, getStationAddress(station));
        }
        
        return result > 0;
    }

    /**
     * 分配包裹到快递员
     */
    @Override
    @Transactional
    public boolean assignPackageToCourier(Long packageId, Long courierId) {
        // 查询包裹信息
        Package packageInfo = packageMapper.selectById(packageId);
        if (packageInfo == null) {
            throw new IllegalArgumentException("包裹不存在");
        }
        
        // 查询快递员信息
        Courier courier = courierMapper.selectById(courierId);
        if (courier == null) {
            throw new IllegalArgumentException("快递员不存在");
        }
        
        // 更新包裹快递员
        packageInfo.setCourierId(courierId);
        packageInfo.setDeliveryType(0); // 设置为上门派件
        packageInfo.setUpdatedAt(LocalDateTime.now());
        int result = packageMapper.updateById(packageInfo);
        
        // 添加物流记录
        if (result > 0) {
            String trackingInfo = "包裹已分配给快递员派送";
            addPackageTrackingRecord(packageId, trackingInfo, "");
        }
        
        return result > 0;
    }

    /**
     * 修改包裹信息
     */
    @Override
    public boolean updatePackageInfo(Long packageId, Package packageInfo) {
        // 查询包裹信息
        Package existPackage = packageMapper.selectById(packageId);
        if (existPackage == null) {
            throw new IllegalArgumentException("包裹不存在");
        }
        
        // 设置不可修改的字段
        packageInfo.setId(packageId);
        packageInfo.setUserId(existPackage.getUserId());
        packageInfo.setCompanyId(existPackage.getCompanyId());
        packageInfo.setTrackingNo(existPackage.getTrackingNo());
        packageInfo.setSignedTime(existPackage.getSignedTime());
        packageInfo.setCreatedAt(existPackage.getCreatedAt());
        packageInfo.setUpdatedAt(LocalDateTime.now());
        
        return packageMapper.updateById(packageInfo) > 0;
    }

    /**
     * 搜索包裹
     */
    @Override
    public List<Map<String, Object>> searchPackages(String keyword, Integer limit) {
        List<Package> packages = packageMapper.searchPackages(keyword, limit);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Package packageInfo : packages) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", packageInfo.getId());
            map.put("trackingNo", packageInfo.getTrackingNo());
            map.put("receiverName", packageInfo.getReceiverName());
            map.put("receiverPhone", packageInfo.getReceiverPhone());
            map.put("status", packageInfo.getStatus());
            map.put("statusText", getStatusText(packageInfo.getStatus()));
            
            // 查询快递公司
            if (packageInfo.getCompanyId() != null) {
                ExpressCompany company = expressCompanyMapper.selectById(packageInfo.getCompanyId());
                if (company != null) {
                    map.put("companyName", company.getName());
                    map.put("companyLogo", company.getLogo());
                }
            }
            
            resultList.add(map);
        }
        
        return resultList;
    }
    
    /**
     * 根据状态码获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        
        switch (status) {
            case 0: return "待收";
            case 1: return "已收";
            case 2: return "已签收";
            case 3: return "异常";
            default: return "未知";
        }
    }
    
    /**
     * 根据配送类型获取文本
     */
    private String getDeliveryTypeText(Integer deliveryType) {
        if (deliveryType == null) return "未知";
        
        switch (deliveryType) {
            case 0: return "上门派件";
            case 1: return "服务点自提";
            default: return "未知";
        }
    }
    
    /**
     * 获取服务点完整地址
     */
    private String getStationAddress(Station station) {
        return station.getProvince() + station.getCity() + station.getDistrict() + station.getAddress();
    }
    
    /**
     * 转换分页包裹信息为Map
     */
    private IPage<Map<String, Object>> convertPackagePage(IPage<Package> packagePage) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (Package packageInfo : packagePage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", packageInfo.getId());
            map.put("trackingNo", packageInfo.getTrackingNo());
            map.put("receiverName", packageInfo.getReceiverName());
            map.put("receiverPhone", packageInfo.getReceiverPhone());
            map.put("receiverAddress", packageInfo.getReceiverAddress());
            map.put("status", packageInfo.getStatus());
            map.put("statusText", getStatusText(packageInfo.getStatus()));
            map.put("estimatedDeliveryTime", packageInfo.getEstimatedDeliveryTime());
            map.put("signedTime", packageInfo.getSignedTime());
            map.put("deliveryType", packageInfo.getDeliveryType());
            map.put("deliveryTypeText", getDeliveryTypeText(packageInfo.getDeliveryType()));
            map.put("createdAt", packageInfo.getCreatedAt());
            
            // 查询快递公司
            if (packageInfo.getCompanyId() != null) {
                ExpressCompany company = expressCompanyMapper.selectById(packageInfo.getCompanyId());
                if (company != null) {
                    map.put("companyName", company.getName());
                    map.put("companyLogo", company.getLogo());
                }
            }
            
            // 查询服务点
            if (packageInfo.getStationId() != null) {
                Station station = stationMapper.selectById(packageInfo.getStationId());
                if (station != null) {
                    map.put("stationName", station.getName());
                    map.put("stationAddress", getStationAddress(station));
                }
            }
            
            resultList.add(map);
        }
        
        // 创建新的分页对象返回
        IPage<Map<String, Object>> resultPage = new Page<>(packagePage.getCurrent(), packagePage.getSize(), packagePage.getTotal());
        resultPage.setRecords(resultList);
        
        return resultPage;
    }
} 