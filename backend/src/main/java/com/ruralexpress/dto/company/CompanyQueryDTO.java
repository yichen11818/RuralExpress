package com.ruralexpress.dto.company;

import com.ruralexpress.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 快递公司查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyQueryDTO extends PageQuery {

    /**
     * 关键词（公司名称、编码、简称）
     */
    private String keyword;

    /**
     * 状态 (0-禁用，1-启用)
     */
    private Integer status;
} 