package com.ruralexpress.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.dto.PageResult;
import com.ruralexpress.dto.company.CompanyDTO;
import com.ruralexpress.dto.company.CompanyQueryDTO;
import com.ruralexpress.entity.Company;
import com.ruralexpress.mapper.CompanyMapper;
import com.ruralexpress.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 快递公司服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyMapper companyMapper;

    @Override
    public PageResult<CompanyDTO> getCompanyList(CompanyQueryDTO query) {
        log.info("查询快递公司列表: {}", query);
        
        // 构建查询条件
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词查询（名称、编码、简称）
        if (StringUtils.isNotBlank(query.getKeyword())) {
            queryWrapper.like(Company::getName, query.getKeyword())
                    .or()
                    .like(Company::getCode, query.getKeyword())
                    .or()
                    .like(Company::getShortName, query.getKeyword());
        }
        
        // 状态查询
        if (query.getStatus() != null) {
            queryWrapper.eq(Company::getStatus, query.getStatus());
        }
        
        // 按排序号和创建时间排序
        queryWrapper.orderByAsc(Company::getSort).orderByDesc(Company::getCreateTime);
        
        // 分页查询
        Page<Company> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<Company> resultPage = companyMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<CompanyDTO> records = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        PageResult<CompanyDTO> pageResult = new PageResult<>();
        pageResult.setList(records);
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setPages(resultPage.getPages());
        pageResult.setPageNum(resultPage.getCurrent());
        pageResult.setPageSize(resultPage.getSize());
        
        return pageResult;
    }

    @Override
    public CompanyDTO getCompanyById(Long id) {
        log.info("查询快递公司详情: id={}", id);
        
        Company company = companyMapper.selectById(id);
        if (company == null) {
            log.warn("快递公司不存在: id={}", id);
            return null;
        }
        
        return convertToDTO(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        log.info("创建快递公司: {}", companyDTO);
        
        // 检查编码是否已存在
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Company::getCode, companyDTO.getCode());
        Long count = companyMapper.selectCount(queryWrapper);
        if (count > 0) {
            log.warn("快递公司编码已存在: code={}", companyDTO.getCode());
            throw new RuntimeException("快递公司编码已存在");
        }
        
        // 转换为实体
        Company company = new Company();
        BeanUtils.copyProperties(companyDTO, company);
        
        // 设置默认值
        if (company.getSort() == null) {
            company.setSort(0);
        }
        
        LocalDateTime now = LocalDateTime.now();
        company.setCreateTime(now);
        company.setUpdateTime(now);
        
        // 保存实体
        companyMapper.insert(company);
        
        // 返回创建后的对象
        return convertToDTO(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyDTO updateCompany(CompanyDTO companyDTO) {
        log.info("更新快递公司: {}", companyDTO);
        
        // 检查快递公司是否存在
        Company existingCompany = companyMapper.selectById(companyDTO.getId());
        if (existingCompany == null) {
            log.warn("快递公司不存在: id={}", companyDTO.getId());
            return null;
        }
        
        // 检查编码是否已被其他公司使用
        if (!existingCompany.getCode().equals(companyDTO.getCode())) {
            LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Company::getCode, companyDTO.getCode());
            Company otherCompany = companyMapper.selectOne(queryWrapper);
            if (otherCompany != null && !otherCompany.getId().equals(companyDTO.getId())) {
                log.warn("快递公司编码已被其他公司使用: code={}", companyDTO.getCode());
                throw new RuntimeException("快递公司编码已被其他公司使用");
            }
        }
        
        // 转换为实体
        Company company = new Company();
        BeanUtils.copyProperties(companyDTO, company);
        
        // 设置更新时间
        company.setUpdateTime(LocalDateTime.now());
        
        // 保存实体
        companyMapper.updateById(company);
        
        // 返回更新后的对象
        return convertToDTO(companyMapper.selectById(company.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCompanyStatus(Long id, Integer status) {
        log.info("更新快递公司状态: id={}, status={}", id, status);
        
        // 检查快递公司是否存在
        Company existingCompany = companyMapper.selectById(id);
        if (existingCompany == null) {
            log.warn("快递公司不存在: id={}", id);
            return false;
        }
        
        // 更新状态
        LambdaUpdateWrapper<Company> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Company::getId, id)
                .set(Company::getStatus, status)
                .set(Company::getUpdateTime, LocalDateTime.now());
        
        return companyMapper.update(null, updateWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCompany(Long id) {
        log.info("删除快递公司: id={}", id);
        
        // 检查快递公司是否存在
        Company existingCompany = companyMapper.selectById(id);
        if (existingCompany == null) {
            log.warn("快递公司不存在: id={}", id);
            return false;
        }
        
        // TODO: 检查是否有关联数据
        
        // 删除快递公司
        return companyMapper.deleteById(id) > 0;
    }
    
    /**
     * 将实体转换为DTO
     * @param company 快递公司实体
     * @return 快递公司DTO
     */
    private CompanyDTO convertToDTO(Company company) {
        if (company == null) {
            return null;
        }
        
        CompanyDTO dto = new CompanyDTO();
        BeanUtils.copyProperties(company, dto);
        return dto;
    }
} 