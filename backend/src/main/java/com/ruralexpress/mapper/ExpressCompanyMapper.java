package com.ruralexpress.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruralexpress.entity.ExpressCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 快递公司 Mapper 接口
 */
@Mapper
public interface ExpressCompanyMapper extends BaseMapper<ExpressCompany> {
    
    /**
     * 统计关联该快递公司的包裹数量
     * @param companyId 快递公司ID
     * @return 包裹数量
     */
    @Select("SELECT COUNT(*) FROM t_package WHERE company_id = #{companyId}")
    int countRelatedPackages(@Param("companyId") Long companyId);
    
    /**
     * 统计关联该快递公司的服务点数量
     * @param companyId 快递公司ID
     * @return 服务点数量
     */
    @Select("SELECT COUNT(*) FROM t_station_company WHERE company_id = #{companyId}")
    int countRelatedStations(@Param("companyId") Long companyId);
    
    /**
     * 查询快递公司详情（包含关联的服务点数量）
     * @param companyId 快递公司ID
     * @return 快递公司信息
     */
    @Select("SELECT e.*, COUNT(DISTINCT sc.station_id) as station_count, COUNT(DISTINCT p.id) as package_count " +
            "FROM t_express_company e " +
            "LEFT JOIN t_station_company sc ON e.id = sc.company_id " +
            "LEFT JOIN t_package p ON e.id = p.company_id " +
            "WHERE e.id = #{companyId} " +
            "GROUP BY e.id")
    ExpressCompany findCompanyDetail(@Param("companyId") Long companyId);
    
    /**
     * 根据关键词搜索快递公司
     * @param keyword 关键词
     * @param limit 限制返回数量
     * @return 快递公司列表
     */
    @Select("SELECT * FROM t_express_company " +
            "WHERE status = 1 AND (name LIKE CONCAT('%', #{keyword}, '%') OR code LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY name LIMIT #{limit}")
    List<ExpressCompany> searchCompanies(@Param("keyword") String keyword, @Param("limit") Integer limit);
} 