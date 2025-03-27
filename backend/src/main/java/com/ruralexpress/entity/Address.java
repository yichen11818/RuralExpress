package com.ruralexpress.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户地址实体类
 */
@Data
public class Address {
    /**
     * 地址ID
     */
    private Long id;
    
    /**
     * 所属用户ID
     */
    private Long userId;
    
    /**
     * 联系人姓名
     */
    private String name;
    
    /**
     * 联系人电话
     */
    private String phone;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区/县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 地址类型（家、公司、学校等）
     */
    private String addressType;
    
    /**
     * 是否为默认地址（1:是，0:否）
     */
    private Integer isDefault;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否删除（1:已删除，0:未删除）
     */
    private Integer isDeleted;
} 