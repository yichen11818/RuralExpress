package com.ruralexpress.service.impl;

import com.ruralexpress.entity.Banner;
import com.ruralexpress.mapper.BannerMapper;
import com.ruralexpress.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Banner服务实现类
 */
@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;
    
    @Override
    public List<Banner> getAllActiveBanners() {
        return bannerMapper.findAllActive();
    }
    
    @Override
    public List<Banner> getAllBanners() {
        return bannerMapper.findAll();
    }
    
    @Override
    public Banner getBannerById(Long id) {
        return bannerMapper.findById(id);
    }
    
    @Override
    public Banner createBanner(Banner banner) {
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.insert(banner);
        return banner;
    }
    
    @Override
    public Banner updateBanner(Banner banner) {
        banner.setUpdateTime(LocalDateTime.now());
        bannerMapper.update(banner);
        return bannerMapper.findById(banner.getId());
    }
    
    @Override
    public boolean deleteBanner(Long id) {
        return bannerMapper.delete(id) > 0;
    }
} 