package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Courier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 快递员 Mapper 接口
 */
@Mapper
public interface CourierMapper extends BaseMapper<Courier> {
    
    /**
     * 根据区域查询快递员列表
     * @param page 分页参数
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @return 快递员分页列表
     */
    @Select("SELECT c.* FROM t_courier c " +
            "JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.service_area LIKE CONCAT('%', #{province}, ' ', #{city}, ' ', #{district}, '%') " +
            "AND c.audit_status = 1 AND c.service_status = 1 " +
            "ORDER BY c.rating DESC")
    IPage<Courier> findByArea(Page<Courier> page, 
                             @Param("province") String province, 
                             @Param("city") String city, 
                             @Param("district") String district);
    
    /**
     * 根据经纬度查询附近的快递员
     * @param page 分页参数
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离(公里)
     * @return 快递员分页列表
     */
    @Select("SELECT c.*, " +
            "6378.137 * 2 * ASIN(SQRT(POW(SIN((#{latitude} * PI() / 180 - c.latitude * PI() / 180) / 2), 2) + " +
            "COS(#{latitude} * PI() / 180) * COS(c.latitude * PI() / 180) * " +
            "POW(SIN((#{longitude} * PI() / 180 - c.longitude * PI() / 180) / 2), 2))) AS distance " +
            "FROM t_courier c " +
            "JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.audit_status = 1 AND c.service_status = 1 " +
            "HAVING distance <= #{distance} " +
            "ORDER BY distance")
    IPage<Courier> findByDistance(Page<Courier> page, 
                                 @Param("longitude") BigDecimal longitude, 
                                 @Param("latitude") BigDecimal latitude, 
                                 @Param("distance") Integer distance);
    
    /**
     * 更新服务状态
     * @param courierId 快递员ID
     * @param serviceStatus 服务状态
     * @return 影响行数
     */
    @Select("UPDATE t_courier SET service_status = #{serviceStatus}, updated_at = NOW() WHERE id = #{courierId}")
    int updateServiceStatus(@Param("courierId") Long courierId, @Param("serviceStatus") Integer serviceStatus);
    
    /**
     * 更新评分
     * @param courierId 快递员ID
     * @param rating 评分
     * @return 影响行数
     */
    @Select("UPDATE t_courier SET rating = #{rating}, updated_at = NOW() WHERE id = #{courierId}")
    int updateRating(@Param("courierId") Long courierId, @Param("rating") BigDecimal rating);
    
    /**
     * 增加完成订单数
     * @param courierId 快递员ID
     * @return 影响行数
     */
    @Select("UPDATE t_courier SET completed_orders = completed_orders + 1, updated_at = NOW() WHERE id = #{courierId}")
    int incrementCompletedOrders(@Param("courierId") Long courierId);
    
    /**
     * 根据关键词搜索快递员
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 快递员列表
     */
    @Select("SELECT c.* FROM t_courier c " +
            "JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.audit_status = 1 " +
            "AND (u.nickname LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.phone LIKE CONCAT('%', #{keyword}, '%') " +
            "OR c.service_area LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY c.rating DESC, c.completed_orders DESC LIMIT #{limit}")
    List<Courier> searchCouriers(@Param("keyword") String keyword, @Param("limit") Integer limit);
    
    /**
     * 查询快递员关联的订单数量
     * @param courierId 快递员ID
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM t_order WHERE courier_id = #{courierId}")
    int countCourierOrders(@Param("courierId") Long courierId);
    
    /**
     * 查询活跃的快递员(无关键词查询使用)
     * @param limit 限制数量
     * @return 快递员列表
     */
    @Select("SELECT c.* FROM t_courier c " +
            "JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.audit_status = 1 " +
            "ORDER BY c.rating DESC, c.completed_orders DESC LIMIT #{limit}")
    List<Courier> selectActiveCouriers(@Param("limit") Integer limit);
} 