package com.ruralexpress.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Package;

import java.util.List;
import java.util.Map;

/**
 * 包裹服务接口
 */
public interface PackageService {

    /**
     * 添加包裹
     *
     * @param packageInfo 包裹信息
     * @return 包裹ID
     */
    Long addPackage(Package packageInfo);

    /**
     * 根据ID获取包裹信息
     *
     * @param packageId 包裹ID
     * @return 包裹详情
     */
    Map<String, Object> getPackageById(Long packageId);

    /**
     * 根据运单号查询包裹
     *
     * @param trackingNo 运单号
     * @param companyId 快递公司ID
     * @return 包裹信息
     */
    Map<String, Object> getPackageByTrackingNo(String trackingNo, Long companyId);

    /**
     * 根据公司编码和运单号查询包裹
     *
     * @param trackingNo 运单号
     * @param companyCode 快递公司编码
     * @return 包裹信息
     */
    Map<String, Object> getPackageByTrackingNoAndCompanyCode(String trackingNo, String companyCode);

    /**
     * 获取用户待收包裹列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 包裹列表
     */
    IPage<Map<String, Object>> getUserPendingPackages(Page<Package> page, Long userId);

    /**
     * 获取用户已收包裹列表
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 包裹列表
     */
    IPage<Map<String, Object>> getUserReceivedPackages(Page<Package> page, Long userId);

    /**
     * 获取服务点待取包裹列表
     *
     * @param page 分页参数
     * @param stationId 服务点ID
     * @return 包裹列表
     */
    IPage<Map<String, Object>> getStationPendingPackages(Page<Package> page, Long stationId);

    /**
     * 获取快递员待送包裹列表
     *
     * @param page 分页参数
     * @param courierId 快递员ID
     * @return 包裹列表
     */
    IPage<Map<String, Object>> getCourierPendingPackages(Page<Package> page, Long courierId);

    /**
     * 更新包裹状态
     *
     * @param packageId 包裹ID
     * @param status 状态，0-待收，1-已收，2-已签收，3-异常
     * @return 是否成功
     */
    boolean updatePackageStatus(Long packageId, Integer status);

    /**
     * 签收包裹
     *
     * @param packageId 包裹ID
     * @param signedImageUrl 签收照片URL
     * @return 是否成功
     */
    boolean signPackage(Long packageId, String signedImageUrl);

    /**
     * 获取包裹物流记录
     *
     * @param packageId 包裹ID
     * @return 物流记录列表
     */
    List<Map<String, Object>> getPackageTrackingRecords(Long packageId);

    /**
     * 添加包裹物流记录
     *
     * @param packageId 包裹ID
     * @param info 物流信息
     * @param location 位置
     * @return 是否成功
     */
    boolean addPackageTrackingRecord(Long packageId, String info, String location);

    /**
     * 分配包裹到服务点
     *
     * @param packageId 包裹ID
     * @param stationId 服务点ID
     * @return 是否成功
     */
    boolean assignPackageToStation(Long packageId, Long stationId);

    /**
     * 分配包裹到快递员
     *
     * @param packageId 包裹ID
     * @param courierId 快递员ID
     * @return 是否成功
     */
    boolean assignPackageToCourier(Long packageId, Long courierId);

    /**
     * 修改包裹信息
     *
     * @param packageId 包裹ID
     * @param packageInfo 包裹信息
     * @return 是否成功
     */
    boolean updatePackageInfo(Long packageId, Package packageInfo);

    /**
     * 搜索包裹
     *
     * @param keyword 关键词（运单号/收件人/收件人电话）
     * @param limit 返回数量限制
     * @return 包裹列表
     */
    List<Map<String, Object>> searchPackages(String keyword, Integer limit);
} 