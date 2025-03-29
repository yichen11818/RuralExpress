package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.ExpressCompany;
import com.ruralexpress.mapper.ExpressCompanyMapper;
import com.ruralexpress.service.ExpressCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快递公司服务实现类
 */
@Service
public class ExpressCompanyServiceImpl implements ExpressCompanyService {

    @Autowired
    private ExpressCompanyMapper expressCompanyMapper;

    /**
     * 添加快递公司
     */
    @Override
    public Long addExpressCompany(ExpressCompany company) {
        // 检查编码是否已存在
        LambdaQueryWrapper<ExpressCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExpressCompany::getCode, company.getCode());
        if (expressCompanyMapper.selectCount(queryWrapper) > 0) {
            throw new IllegalArgumentException("快递公司编码已存在");
        }
        
        // 设置初始状态
        company.setStatus(0); // 正常状态
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        
        // 保存快递公司信息
        expressCompanyMapper.insert(company);
        
        return company.getId();
    }

    /**
     * 根据ID获取快递公司信息
     */
    @Override
    public ExpressCompany getCompanyById(Long companyId) {
        return expressCompanyMapper.selectById(companyId);
    }

    /**
     * 根据编码获取快递公司信息
     */
    @Override
    public ExpressCompany getCompanyByCode(String code) {
        LambdaQueryWrapper<ExpressCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExpressCompany::getCode, code);
        return expressCompanyMapper.selectOne(queryWrapper);
    }

    /**
     * 分页查询快递公司列表
     */
    @Override
    public IPage<ExpressCompany> getCompanyList(Page<ExpressCompany> page) {
        LambdaQueryWrapper<ExpressCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(ExpressCompany::getUpdatedAt);
        return expressCompanyMapper.selectPage(page, queryWrapper);
    }
    
    /**
     * 分页查询快递公司列表(带搜索条件)
     */
    @Override
    public IPage<ExpressCompany> getCompanyList(Page<ExpressCompany> page, String keyword, Integer status) {
        LambdaQueryWrapper<ExpressCompany> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加关键词搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(ExpressCompany::getName, keyword)
                    .or()
                    .like(ExpressCompany::getCode, keyword)
            );
        }
        
        // 添加状态条件
        if (status != null) {
            queryWrapper.eq(ExpressCompany::getStatus, status);
        }
        
        // 按更新时间倒序排序
        queryWrapper.orderByDesc(ExpressCompany::getUpdatedAt);
        
        return expressCompanyMapper.selectPage(page, queryWrapper);
    }

    /**
     * 更新快递公司信息
     */
    @Override
    public boolean updateCompany(Long companyId, ExpressCompany company) {
        // 查询快递公司是否存在
        ExpressCompany existCompany = expressCompanyMapper.selectById(companyId);
        if (existCompany == null) {
            throw new IllegalArgumentException("快递公司不存在");
        }
        
        // 检查编码是否被其他快递公司使用
        if (!existCompany.getCode().equals(company.getCode())) {
            LambdaQueryWrapper<ExpressCompany> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExpressCompany::getCode, company.getCode());
            if (expressCompanyMapper.selectCount(queryWrapper) > 0) {
                throw new IllegalArgumentException("快递公司编码已存在");
            }
        }
        
        // 设置不可修改的字段
        company.setId(companyId);
        company.setCreatedAt(existCompany.getCreatedAt());
        company.setUpdatedAt(LocalDateTime.now());
        
        return expressCompanyMapper.updateById(company) > 0;
    }

    /**
     * 更新快递公司状态
     */
    @Override
    public boolean updateCompanyStatus(Long companyId, Integer status) {
        // 查询快递公司是否存在
        ExpressCompany company = expressCompanyMapper.selectById(companyId);
        if (company == null) {
            throw new IllegalArgumentException("快递公司不存在");
        }
        
        // 更新状态
        company.setStatus(status);
        company.setUpdatedAt(LocalDateTime.now());
        
        return expressCompanyMapper.updateById(company) > 0;
    }

    /**
     * 删除快递公司
     */
    @Override
    public boolean deleteCompany(Long companyId) {
        // 查询快递公司是否存在
        ExpressCompany company = expressCompanyMapper.selectById(companyId);
        if (company == null) {
            throw new IllegalArgumentException("快递公司不存在");
        }
        
        // 检查是否有关联的包裹
        if (expressCompanyMapper.countRelatedPackages(companyId) > 0) {
            throw new IllegalArgumentException("该快递公司有关联的包裹，无法删除");
        }
        
        // 检查是否有关联的服务点
        if (expressCompanyMapper.countRelatedStations(companyId) > 0) {
            throw new IllegalArgumentException("该快递公司有关联的服务点，无法删除");
        }
        
        return expressCompanyMapper.deleteById(companyId) > 0;
    }

    /**
     * 获取所有启用状态的快递公司
     */
    @Override
    public List<Map<String, Object>> getAllEnabledCompanies() {
        LambdaQueryWrapper<ExpressCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExpressCompany::getStatus, 0);
        queryWrapper.orderByAsc(ExpressCompany::getName);
        
        List<ExpressCompany> companies = expressCompanyMapper.selectList(queryWrapper);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ExpressCompany company : companies) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", company.getId());
            map.put("name", company.getName());
            map.put("code", company.getCode());
            map.put("logo", company.getLogo());
            resultList.add(map);
        }
        
        return resultList;
    }

    /**
     * 根据关键词搜索快递公司
     */
    @Override
    public List<Map<String, Object>> searchCompanies(String keyword, Integer limit) {
        // 构建搜索条件
        LambdaQueryWrapper<ExpressCompany> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExpressCompany::getStatus, 0);
        queryWrapper.and(wrapper -> 
            wrapper.like(ExpressCompany::getName, keyword)
                .or()
                .like(ExpressCompany::getCode, keyword)
        );
        queryWrapper.orderByAsc(ExpressCompany::getName);
        queryWrapper.last("LIMIT " + limit);
        
        List<ExpressCompany> companies = expressCompanyMapper.selectList(queryWrapper);
        
        // 转换为Map列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (ExpressCompany company : companies) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", company.getId());
            map.put("name", company.getName());
            map.put("code", company.getCode());
            map.put("logo", company.getLogo());
            resultList.add(map);
        }
        
        return resultList;
    }
} 