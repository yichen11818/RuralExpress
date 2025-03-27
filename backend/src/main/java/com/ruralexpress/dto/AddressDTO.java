package com.ruralexpress.dto;

import lombok.Data;

/**
 * 地址数据传输对象
 */
@Data
public class AddressDTO {
    /**
     * 地址ID
     */
    private Long id;
    
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
     * 是否为默认地址
     */
    private Boolean isDefault;
} 