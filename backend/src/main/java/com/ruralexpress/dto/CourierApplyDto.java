package com.ruralexpress.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 快递员申请数据传输对象
 */
@Data
public class CourierApplyDto {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 服务区域
     */
    @NotBlank(message = "服务区域不能为空")
    private String serviceArea;
    
    /**
     * 身份证正面照片URL
     */
    @NotBlank(message = "身份证正面照片不能为空")
    private String idCardFront;
    
    /**
     * 身份证背面照片URL
     */
    @NotBlank(message = "身份证背面照片不能为空")
    private String idCardBack;
    
    /**
     * 工作开始时间，格式：HH:mm
     */
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "工作开始时间格式错误")
    private String workStartTime;
    
    /**
     * 工作结束时间，格式：HH:mm
     */
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "工作结束时间格式错误")
    private String workEndTime;
    
    /**
     * 交通工具
     */
    private String vehicle;
    
    /**
     * 简介
     */
    private String introduction;
} 