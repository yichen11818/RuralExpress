package com.ruralexpress.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruralexpress.entity.ExpressCompany;

import java.util.List;
import java.util.Map;

/**
 * 快递公司服务接口
 */
public interface ExpressCompanyService {

    /**
     * 添加快递公司
     *
     * @param company 快递公司信息
     * @return 快递公司ID
     */
    Long addExpressCompany(ExpressCompany company);

    /**
     * 根据ID获取快递公司信息
     *
     * @param companyId 快递公司ID
     * @return 快递公司详情
     */
    ExpressCompany getCompanyById(Long companyId);

    /**
     * 根据编码获取快递公司信息
     *
     * @param code 快递公司编码
     * @return 快递公司详情
     */
    ExpressCompany getCompanyByCode(String code);

    /**
     * 分页查询快递公司列表
     *
     * @param page 分页参数
     * @return 快递公司列表
     */
    IPage<ExpressCompany> getCompanyList(Page<ExpressCompany> page);
    
    /**
     * 分页查询快递公司列表(带搜索条件)
     *
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param status 状态(0-正常,1-禁用)
     * @return 快递公司列表
     */
    IPage<ExpressCompany> getCompanyList(Page<ExpressCompany> page, String keyword, Integer status);

    /**
     * 更新快递公司信息
     *
     * @param companyId 快递公司ID
     * @param company 快递公司信息
     * @return 是否成功
     */
    boolean updateCompany(Long companyId, ExpressCompany company);

    /**
     * 更新快递公司状态
     *
     * @param companyId 快递公司ID
     * @param status 状态，0-禁用，1-启用
     * @return 是否成功
     */
    boolean updateCompanyStatus(Long companyId, Integer status);

    /**
     * 删除快递公司
     *
     * @param companyId 快递公司ID
     * @return 是否成功
     */
    boolean deleteCompany(Long companyId);

    /**
     * 获取所有启用状态的快递公司
     *
     * @return 快递公司列表
     */
    List<Map<String, Object>> getAllEnabledCompanies();

    /**
     * 根据关键词搜索快递公司
     *
     * @param keyword 关键词
     * @param limit 限制返回数量
     * @return 快递公司列表
     */
    List<Map<String, Object>> searchCompanies(String keyword, Integer limit);
} 