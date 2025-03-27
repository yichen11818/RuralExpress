package com.ruralexpress.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 快递员信息更新数据传输对象
 */
@Data
public class CourierUpdateDto {
    
    /**
     * 服务区域
     */
    private String serviceArea;
    
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
    
    /**
     * 身份证正面照片URL
     */
    private String idCardFront;
    
    /**
     * 身份证背面照片URL
     */
    private String idCardBack;
} 