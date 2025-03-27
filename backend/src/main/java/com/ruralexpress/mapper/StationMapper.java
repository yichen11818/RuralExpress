package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.Station;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 服务点 Mapper 接口
 */
@Mapper
public interface StationMapper extends BaseMapper<Station> {
    
    /**
     * 根据区域查询服务点列表
     * @param page 分页参数
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @return 服务点分页列表
     */
    @Select("SELECT * FROM t_station " +
            "WHERE province = #{province} AND city = #{city} AND district = #{district} " +
            "AND status = 0 ORDER BY rating DESC")
    IPage<Station> findByArea(Page<Station> page, 
                            @Param("province") String province, 
                            @Param("city") String city, 
                            @Param("district") String district);
    
    /**
     * 根据经纬度查询附近的服务点
     * @param page 分页参数
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离(公里)
     * @return 服务点分页列表
     */
    @Select("SELECT *, " +
            "6378.137 * 2 * ASIN(SQRT(POW(SIN((#{latitude} * PI() / 180 - latitude * PI() / 180) / 2), 2) + " +
            "COS(#{latitude} * PI() / 180) * COS(latitude * PI() / 180) * " +
            "POW(SIN((#{longitude} * PI() / 180 - longitude * PI() / 180) / 2), 2))) AS distance " +
            "FROM t_station " +
            "WHERE status = 0 " +
            "HAVING distance <= #{distance} " +
            "ORDER BY distance")
    IPage<Station> findNearby(Page<Station> page, 
                            @Param("longitude") BigDecimal longitude, 
                            @Param("latitude") BigDecimal latitude, 
                            @Param("distance") Integer distance);
    
    /**
     * 更新服务点评分
     * @param stationId 服务点ID
     * @param rating 新评分
     * @return 影响行数
     */
    @Update("UPDATE t_station SET rating = #{rating}, review_count = review_count + 1 WHERE id = #{stationId}")
    int updateRating(@Param("stationId") Long stationId, @Param("rating") BigDecimal rating);
    
    /**
     * 查询服务点支持的快递公司
     * @param stationId 服务点ID
     * @return 快递公司列表
     */
    @Select("SELECT e.* FROM t_express_company e " +
            "JOIN t_station_company sc ON e.id = sc.company_id " +
            "WHERE sc.station_id = #{stationId} AND e.status = 1")
    List<Map<String, Object>> findSupportedCompanies(@Param("stationId") Long stationId);
    
    /**
     * 查询服务点图片
     * @param stationId 服务点ID
     * @return 图片URL列表
     */
    @Select("SELECT photo_url FROM t_station_photo WHERE station_id = #{stationId} ORDER BY sort")
    List<String> findStationPhotos(@Param("stationId") Long stationId);
    
    /**
     * 插入服务点图片
     * @param stationId 服务点ID
     * @param photoUrl 图片URL
     * @param sort 排序
     * @return 影响行数
     */
    @Insert("INSERT INTO t_station_photo(station_id, photo_url, sort, created_at, updated_at) " +
            "VALUES(#{stationId}, #{photoUrl}, #{sort}, NOW(), NOW())")
    int insertPhoto(@Param("stationId") Long stationId, @Param("photoUrl") String photoUrl, @Param("sort") Integer sort);
    
    /**
     * 删除服务点图片
     * @param photoId 图片ID
     * @return 影响行数
     */
    @Delete("DELETE FROM t_station_photo WHERE id = #{photoId}")
    int deletePhoto(@Param("photoId") Long photoId);
    
    /**
     * 插入服务点支持的快递公司
     * @param stationId 服务点ID
     * @param companyId 快递公司ID
     * @return 影响行数
     */
    @Insert("INSERT INTO t_station_company(station_id, company_id, created_at, updated_at) " +
            "VALUES(#{stationId}, #{companyId}, NOW(), NOW())")
    int insertStationCompany(@Param("stationId") Long stationId, @Param("companyId") Long companyId);
    
    /**
     * 删除服务点支持的快递公司
     * @param stationId 服务点ID
     * @param companyId 快递公司ID
     * @return 影响行数
     */
    @Delete("DELETE FROM t_station_company WHERE station_id = #{stationId} AND company_id = #{companyId}")
    int deleteStationCompany(@Param("stationId") Long stationId, @Param("companyId") Long companyId);
    
    /**
     * 检查服务点是否已支持该快递公司
     * @param stationId 服务点ID
     * @param companyId 快递公司ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_station_company WHERE station_id = #{stationId} AND company_id = #{companyId}")
    int checkStationCompany(@Param("stationId") Long stationId, @Param("companyId") Long companyId);
    
    /**
     * 根据服务点查询服务点附近其他服务点
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离(米)
     * @param limit 限制数量
     * @param excludeStationId 排除的服务点ID
     * @return 服务点列表
     */
    @Select("SELECT *, " +
            "6378.137 * 2 * ASIN(SQRT(POW(SIN((#{latitude} * PI() / 180 - latitude * PI() / 180) / 2), 2) + " +
            "COS(#{latitude} * PI() / 180) * COS(latitude * PI() / 180) * " +
            "POW(SIN((#{longitude} * PI() / 180 - longitude * PI() / 180) / 2), 2))) AS distance " +
            "FROM t_station " +
            "WHERE id != #{excludeStationId} AND status = 0 " +
            "HAVING distance <= #{distance} / 1000 " +  // 将米转换为公里
            "ORDER BY distance LIMIT #{limit}")
    List<Station> findNearbyStations(@Param("longitude") BigDecimal longitude,
                                   @Param("latitude") BigDecimal latitude,
                                   @Param("distance") Integer distance,
                                   @Param("limit") Integer limit,
                                   @Param("excludeStationId") Long excludeStationId);
} 