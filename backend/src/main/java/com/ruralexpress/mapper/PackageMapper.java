package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Package;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 包裹 Mapper 接口
 */
@Mapper
public interface PackageMapper extends BaseMapper<Package> {
    
    /**
     * 根据用户ID查询包裹列表(待收取)
     * @param page 分页参数
     * @param userId 用户ID
     * @return 包裹分页列表
     */
    @Select("SELECT p.*, e.name as company_name, e.logo as company_logo " +
            "FROM t_package p " +
            "JOIN t_express_company e ON p.company_id = e.id " +
            "WHERE p.user_id = #{userId} AND p.status = 0 " +
            "ORDER BY p.created_at DESC")
    IPage<Package> findPendingByUserId(Page<Package> page, @Param("userId") Long userId);
    
    /**
     * 根据用户ID查询包裹列表(已收取)
     * @param page 分页参数
     * @param userId 用户ID
     * @return 包裹分页列表
     */
    @Select("SELECT p.*, e.name as company_name, e.logo as company_logo " +
            "FROM t_package p " +
            "JOIN t_express_company e ON p.company_id = e.id " +
            "WHERE p.user_id = #{userId} AND p.status IN (1, 2) " +
            "ORDER BY p.updated_at DESC")
    IPage<Package> findReceivedByUserId(Page<Package> page, @Param("userId") Long userId);
    
    /**
     * 根据快递单号和快递公司查询包裹
     * @param trackingNo 快递单号
     * @param companyId 快递公司ID
     * @return 包裹
     */
    @Select("SELECT p.*, e.name as company_name, e.logo as company_logo " +
            "FROM t_package p " +
            "JOIN t_express_company e ON p.company_id = e.id " +
            "WHERE p.tracking_no = #{trackingNo} AND p.company_id = #{companyId}")
    Package findByTrackingNo(@Param("trackingNo") String trackingNo, @Param("companyId") Long companyId);
    
    /**
     * 根据快递单号和快递公司代码查询包裹
     * @param trackingNo 快递单号
     * @param companyCode 快递公司代码
     * @return 包裹
     */
    @Select("SELECT p.*, e.name as company_name, e.logo as company_logo " +
            "FROM t_package p " +
            "JOIN t_express_company e ON p.company_id = e.id " +
            "WHERE p.tracking_no = #{trackingNo} AND e.code = #{companyCode}")
    Package findByTrackingNoAndCompanyCode(@Param("trackingNo") String trackingNo, @Param("companyCode") String companyCode);
    
    /**
     * 更新包裹状态
     * @param packageId 包裹ID
     * @param status 包裹状态
     * @return 影响行数
     */
    @Update("UPDATE t_package SET status = #{status}, updated_at = NOW() WHERE id = #{packageId}")
    int updateStatus(@Param("packageId") Long packageId, @Param("status") Integer status);
    
    /**
     * 根据服务点ID查询包裹列表
     * @param page 分页参数
     * @param stationId 服务点ID
     * @param status 包裹状态
     * @return 包裹分页列表
     */
    @Select("SELECT p.*, e.name as company_name, e.logo as company_logo " +
            "FROM t_package p " +
            "JOIN t_express_company e ON p.company_id = e.id " +
            "WHERE p.station_id = #{stationId} AND p.status = #{status} " +
            "ORDER BY p.created_at DESC")
    IPage<Package> findByStationIdAndStatus(Page<Package> page, 
                                         @Param("stationId") Long stationId, 
                                         @Param("status") Integer status);
    
    /**
     * 根据快递员ID查询包裹列表
     * @param page 分页参数
     * @param courierId 快递员ID
     * @param status 包裹状态
     * @return 包裹分页列表
     */
    @Select("SELECT p.*, e.name as company_name, e.logo as company_logo " +
            "FROM t_package p " +
            "JOIN t_express_company e ON p.company_id = e.id " +
            "WHERE p.courier_id = #{courierId} AND p.status = #{status} " +
            "ORDER BY p.created_at DESC")
    IPage<Package> findByCourierIdAndStatus(Page<Package> page, 
                                         @Param("courierId") Long courierId, 
                                         @Param("status") Integer status);
    
    /**
     * 查询包裹物流记录
     * @param packageId 包裹ID
     * @return 物流记录列表
     */
    @Select("SELECT * FROM t_package_tracking WHERE package_id = #{packageId} ORDER BY tracking_time DESC")
    List<Map<String, Object>> findTrackingRecords(@Param("packageId") Long packageId);
    
    /**
     * 添加包裹物流记录
     * @param packageId 包裹ID
     * @param info 物流信息
     * @param trackingTime 物流时间
     * @param location 位置
     * @return 影响行数
     */
    @Insert("INSERT INTO t_package_tracking(package_id, tracking_info, tracking_time, location, created_at, updated_at) " +
            "VALUES(#{packageId}, #{info}, #{trackingTime}, #{location}, NOW(), NOW())")
    int insertTrackingRecord(@Param("packageId") Long packageId, 
                           @Param("info") String info, 
                           @Param("trackingTime") LocalDateTime trackingTime, 
                           @Param("location") String location);
    
    /**
     * 搜索包裹
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 包裹列表
     */
    @Select("SELECT p.*, e.name as company_name, e.logo as company_logo " +
            "FROM t_package p " +
            "JOIN t_express_company e ON p.company_id = e.id " +
            "WHERE p.tracking_no LIKE CONCAT('%', #{keyword}, '%') " +
            "OR p.receiver_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR p.receiver_phone LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY p.created_at DESC LIMIT #{limit}")
    List<Package> searchPackages(@Param("keyword") String keyword, @Param("limit") Integer limit);
} 