package com.ruralexpress.dto.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 快递公司数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    /**
     * 快递公司ID
     */
    private Long id;

    /**
     * 快递公司编码
     */
    @NotBlank(message = "公司编码不能为空")
    @Size(max = 50, message = "公司编码长度不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "公司编码只能包含字母、数字、下划线和连字符")
    private String code;

    /**
     * 快递公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    @Size(max = 100, message = "公司名称长度不能超过100个字符")
    private String name;

    /**
     * 快递公司简称
     */
    @Size(max = 50, message = "公司简称长度不能超过50个字符")
    private String shortName;

    /**
     * 快递公司Logo
     */
    private String logo;

    /**
     * 联系人
     */
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactPerson;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
    private String contactPhone;

    /**
     * 公司地址
     */
    @Size(max = 200, message = "公司地址长度不能超过200个字符")
    private String address;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 状态 (0-禁用，1-启用)
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 