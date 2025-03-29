package com.ruralexpress.controller.admin;

import com.ruralexpress.dto.PageResult;
import com.ruralexpress.dto.company.CompanyDTO;
import com.ruralexpress.dto.company.CompanyQueryDTO;
import com.ruralexpress.service.CompanyService;
import com.ruralexpress.utils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 快递公司管理控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/companies")
public class AdminCompanyController {

    private final CompanyService companyService;

    /**
     * 获取快递公司列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词
     * @param status 状态
     * @return 快递公司列表
     */
    @GetMapping
    public ApiResult<PageResult<CompanyDTO>> getCompanyList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status) {
        
        log.info("获取快递公司列表: pageNum={}, pageSize={}, keyword={}, status={}", 
                pageNum, pageSize, keyword, status);
        
        CompanyQueryDTO query = new CompanyQueryDTO();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setKeyword(keyword);
        query.setStatus(status);
        
        try {
            PageResult<CompanyDTO> result = companyService.getCompanyList(query);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("获取快递公司列表失败", e);
            return ApiResult.serverError("获取快递公司列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取快递公司详情
     * @param id 快递公司ID
     * @return 快递公司详情
     */
    @GetMapping("/{id}")
    public ApiResult<CompanyDTO> getCompanyDetail(@PathVariable Long id) {
        log.info("获取快递公司详情: id={}", id);
        
        try {
            CompanyDTO company = companyService.getCompanyById(id);
            if (company == null) {
                return ApiResult.error(404, "快递公司不存在");
            }
            return ApiResult.success(company);
        } catch (Exception e) {
            log.error("获取快递公司详情失败", e);
            return ApiResult.serverError("获取快递公司详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建快递公司
     * @param companyDTO 快递公司信息
     * @return 创建结果
     */
    @PostMapping
    public ApiResult<CompanyDTO> createCompany(@RequestBody @Validated CompanyDTO companyDTO) {
        log.info("创建快递公司: {}", companyDTO);
        
        try {
            CompanyDTO createdCompany = companyService.createCompany(companyDTO);
            return ApiResult.success(createdCompany);
        } catch (Exception e) {
            log.error("创建快递公司失败", e);
            return ApiResult.serverError("创建快递公司失败: " + e.getMessage());
        }
    }

    /**
     * 更新快递公司
     * @param id 快递公司ID
     * @param companyDTO 快递公司信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @RequestBody @Validated CompanyDTO companyDTO) {
        
        log.info("更新快递公司: id={}, company={}", id, companyDTO);
        companyDTO.setId(id);
        
        try {
            CompanyDTO updatedCompany = companyService.updateCompany(companyDTO);
            if (updatedCompany == null) {
                return ApiResult.error(404, "快递公司不存在");
            }
            return ApiResult.success(updatedCompany);
        } catch (Exception e) {
            log.error("更新快递公司失败", e);
            return ApiResult.serverError("更新快递公司失败: " + e.getMessage());
        }
    }

    /**
     * 更新快递公司状态
     * @param id 快递公司ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ApiResult<Boolean> updateCompanyStatus(
            @PathVariable Long id,
            @RequestParam("status") Integer status) {
        
        log.info("更新快递公司状态: id={}, status={}", id, status);
        
        try {
            if (status != 0 && status != 1) {
                return ApiResult.error(400, "状态值无效，只能为0或1");
            }
            
            boolean success = companyService.updateCompanyStatus(id, status);
            if (!success) {
                return ApiResult.error(404, "快递公司不存在");
            }
            return ApiResult.success(true);
        } catch (Exception e) {
            log.error("更新快递公司状态失败", e);
            return ApiResult.serverError("更新快递公司状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除快递公司
     * @param id 快递公司ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> deleteCompany(@PathVariable Long id) {
        log.info("删除快递公司: id={}", id);
        
        try {
            boolean success = companyService.deleteCompany(id);
            if (!success) {
                return ApiResult.error(404, "快递公司不存在或无法删除");
            }
            return ApiResult.success(true);
        } catch (Exception e) {
            log.error("删除快递公司失败", e);
            return ApiResult.serverError("删除快递公司失败: " + e.getMessage());
        }
    }
} 