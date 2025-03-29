package com.ruralexpress.service;

import com.ruralexpress.dto.PageResult;
import com.ruralexpress.dto.company.CompanyDTO;
import com.ruralexpress.dto.company.CompanyQueryDTO;

/**
 * 快递公司服务接口
 */
public interface CompanyService {

    /**
     * 获取快递公司列表
     * @param query 查询条件
     * @return 快递公司列表
     */
    PageResult<CompanyDTO> getCompanyList(CompanyQueryDTO query);

    /**
     * 根据ID获取快递公司
     * @param id 快递公司ID
     * @return 快递公司
     */
    CompanyDTO getCompanyById(Long id);

    /**
     * 创建快递公司
     * @param companyDTO 快递公司信息
     * @return 创建后的快递公司
     */
    CompanyDTO createCompany(CompanyDTO companyDTO);

    /**
     * 更新快递公司
     * @param companyDTO 快递公司信息
     * @return 更新后的快递公司
     */
    CompanyDTO updateCompany(CompanyDTO companyDTO);

    /**
     * 更新快递公司状态
     * @param id 快递公司ID
     * @param status 状态 (0-禁用，1-启用)
     * @return 是否成功
     */
    boolean updateCompanyStatus(Long id, Integer status);

    /**
     * 删除快递公司
     * @param id 快递公司ID
     * @return 是否成功
     */
    boolean deleteCompany(Long id);
} 