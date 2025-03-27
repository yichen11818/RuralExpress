package com.ruralexpress.mapper;

import com.ruralexpress.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Banner数据访问接口
 */
@Mapper
@Repository
public interface BannerMapper {
    
    /**
     * 获取所有激活的Banner，按排序顺序
     * @return Banner列表
     */
    List<Banner> findAllActive();
    
    /**
     * 获取所有Banner
     * @return Banner列表
     */
    List<Banner> findAll();
    
    /**
     * 根据ID获取Banner
     * @param id Banner ID
     * @return Banner对象
     */
    Banner findById(Long id);
    
    /**
     * 插入新Banner
     * @param banner Banner对象
     * @return 影响行数
     */
    int insert(Banner banner);
    
    /**
     * 更新Banner
     * @param banner Banner对象
     * @return 影响行数
     */
    int update(Banner banner);
    
    /**
     * 删除Banner
     * @param id Banner ID
     * @return 影响行数
     */
    int delete(Long id);
} 