package com.ruralexpress.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Courier;
import com.ruralexpress.entity.CourierTag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 快递员服务接口
 */
public interface CourierService {
    
    /**
     * 申请成为快递员
     * @param userId 用户ID
     * @param courier 快递员信息
     * @param idCardFront 身份证正面照片
     * @param idCardBack 身份证背面照片
     * @return 快递员ID
     */
    Long applyCourier(Long userId, Courier courier, String idCardFront, String idCardBack);
    
    /**
     * 获取快递员详情
     * @param courierId 快递员ID
     * @return 快递员信息
     */
    Map<String, Object> getCourierDetail(Long courierId);
    
    /**
     * 按区域查询快递员
     * @param page 分页参数
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @param sortType 排序类型(0-距离,1-评分,2-订单数)
     * @return 快递员分页列表
     */
    IPage<Map<String, Object>> findCouriersByArea(Page<Courier> page, String province, String city, String district, Integer sortType);
    
    /**
     * 按距离查询快递员
     * @param page 分页参数
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离(公里)
     * @return 快递员分页列表
     */
    IPage<Map<String, Object>> findCouriersByDistance(Page<Courier> page, BigDecimal longitude, BigDecimal latitude, Integer distance);
    
    /**
     * 获取推荐的快递员列表
     * @param limit 返回记录数
     * @return 快递员列表
     */
    List<Courier> getRecommendedCouriers(int limit);
    
    /**
     * 获取快递员标签
     * @param courierId 快递员ID
     * @return 标签列表
     */
    List<CourierTag> getCourierTags(Long courierId);
    
    /**
     * 添加快递员标签
     * @param courierId 快递员ID
     * @param tagName 标签内容
     * @param tagType 标签类型(0-系统标签,1-用户评价标签)
     * @return 是否成功
     */
    boolean addCourierTag(Long courierId, String tagName, Integer tagType);
    
    /**
     * 审核快递员申请
     * @param courierId 快递员ID
     * @param auditStatus 审核状态(0-待审核,1-已通过,2-未通过)
     * @param auditRemark 审核备注
     * @return 是否成功
     */
    boolean auditCourier(Long courierId, Integer auditStatus, String auditRemark);
    
    /**
     * 修改服务状态
     * @param courierId 快递员ID
     * @param serviceStatus 服务状态(0-休息,1-接单中)
     * @return 是否成功
     */
    boolean updateServiceStatus(Long courierId, Integer serviceStatus);
    
    /**
     * 更新快递员评分
     * @param courierId 快递员ID
     * @param rating 新评分
     * @return 是否成功
     */
    boolean updateRating(Long courierId, BigDecimal rating);
    
    /**
     * 增加完成订单数
     * @param courierId 快递员ID
     * @return 是否成功
     */
    boolean incrementCompletedOrders(Long courierId);
    
    /**
     * 获取我的快递员信息
     * @param userId 用户ID
     * @return 快递员信息
     */
    Map<String, Object> getMyCourierInfo(Long userId);
    
    /**
     * 更新快递员信息
     * @param courierId 快递员ID
     * @param courier 快递员信息
     * @return 是否成功
     */
    boolean updateCourierInfo(Long courierId, Courier courier);
    
    /**
     * 根据位置信息获取附近的快递员
     * @param latitude 纬度
     * @param longitude 经度
     * @param limit 限制数量
     * @return 快递员列表
     */
    List<Courier> getNearestCouriers(double latitude, double longitude, int limit);
} 