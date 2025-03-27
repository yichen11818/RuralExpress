package com.ruralexpress.service;

import com.ruralexpress.entity.Banner;

import java.util.List;

/**
 * Banner服务接口
 */
public interface BannerService {
    
    /**
     * 获取所有激活的Banner
     * @return Banner列表
     */
    List<Banner> getAllActiveBanners();
    
    /**
     * 获取所有Banner
     * @return Banner列表
     */
    List<Banner> getAllBanners();
    
    /**
     * 根据ID获取Banner
     * @param id Banner ID
     * @return Banner对象
     */
    Banner getBannerById(Long id);
    
    /**
     * 创建Banner
     * @param banner Banner对象
     * @return 创建后的Banner
     */
    Banner createBanner(Banner banner);
    
    /**
     * 更新Banner
     * @param banner Banner对象
     * @return 更新后的Banner
     */
    Banner updateBanner(Banner banner);
    
    /**
     * 删除Banner
     * @param id Banner ID
     * @return 是否删除成功
     */
    boolean deleteBanner(Long id);
} 