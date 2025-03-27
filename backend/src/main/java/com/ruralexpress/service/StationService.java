package com.ruralexpress.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Station;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 服务点服务接口
 */
public interface StationService {

    /**
     * 创建服务点
     *
     * @param station 服务点信息
     * @param managerName 管理员姓名
     * @param managerPhone 管理员电话
     * @param photos 服务点照片URL列表
     * @param companyIds 支持的快递公司ID列表
     * @return 服务点ID
     */
    Long createStation(Station station, String managerName, String managerPhone, List<String> photos, List<Long> companyIds);

    /**
     * 获取服务点详情
     *
     * @param stationId 服务点ID
     * @return 服务点详细信息
     */
    Map<String, Object> getStationDetail(Long stationId);

    /**
     * 按区域查询服务点
     *
     * @param page 分页参数
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @param sortType 排序类型，0-默认，1-距离，2-评分
     * @return 服务点列表
     */
    IPage<Map<String, Object>> findStationsByArea(Page<Station> page, String province, String city, String district, Integer sortType);

    /**
     * 按距离查询服务点
     *
     * @param page 分页参数
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离范围（米）
     * @return 服务点列表
     */
    IPage<Map<String, Object>> findStationsByDistance(Page<Station> page, BigDecimal longitude, BigDecimal latitude, Integer distance);

    /**
     * 获取服务点支持的快递公司
     *
     * @param stationId 服务点ID
     * @return 快递公司列表
     */
    List<Map<String, Object>> getStationCompanies(Long stationId);

    /**
     * 获取服务点照片
     *
     * @param stationId 服务点ID
     * @return 照片URL列表
     */
    List<String> getStationPhotos(Long stationId);

    /**
     * 更新服务点评分
     *
     * @param stationId 服务点ID
     * @param rating 评分
     * @return 是否成功
     */
    boolean updateRating(Long stationId, BigDecimal rating);

    /**
     * 更新服务点状态
     *
     * @param stationId 服务点ID
     * @param status 状态，0-关闭，1-正常
     * @return 是否成功
     */
    boolean updateStatus(Long stationId, Integer status);

    /**
     * 获取附近的服务点
     *
     * @param stationId 当前服务点ID
     * @param limit 限制数量
     * @return 附近服务点列表
     */
    List<Map<String, Object>> getNearbyStations(Long stationId, Integer limit);

    /**
     * 更新服务点信息
     *
     * @param stationId 服务点ID
     * @param station 服务点信息
     * @return 是否成功
     */
    boolean updateStationInfo(Long stationId, Station station);

    /**
     * 添加服务点照片
     *
     * @param stationId 服务点ID
     * @param photoUrl 照片URL
     * @param sort 排序
     * @return 是否成功
     */
    boolean addStationPhoto(Long stationId, String photoUrl, Integer sort);

    /**
     * 删除服务点照片
     *
     * @param photoId 照片ID
     * @return 是否成功
     */
    boolean deleteStationPhoto(Long photoId);

    /**
     * 添加支持的快递公司
     *
     * @param stationId 服务点ID
     * @param companyId 快递公司ID
     * @return 是否成功
     */
    boolean addStationCompany(Long stationId, Long companyId);

    /**
     * 删除支持的快递公司
     *
     * @param stationId 服务点ID
     * @param companyId 快递公司ID
     * @return 是否成功
     */
    boolean deleteStationCompany(Long stationId, Long companyId);
} 