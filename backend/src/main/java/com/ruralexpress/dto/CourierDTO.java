package com.ruralexpress.dto;

import lombok.Data;

/**
 * 快递员数据传输对象
 * 用于管理员API操作
 */
@Data
public class CourierDTO {
    /**
     * ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 工号
     */
    private String workNo;
    
    /**
     * 服务区域
     */
    private String serviceArea;
    
    /**
     * 状态(0-正常,1-禁用,2-待审核)
     */
    private Integer status;
} 