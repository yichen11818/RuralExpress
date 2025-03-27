package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务点实体类
 */
@Data
@TableName("t_station")
public class Station {
    
    /**
     * 服务点ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 服务点名称
     */
    private String name;
    
    /**
     * 服务点LOGO
     */
    private String logo;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 微信联系号
     */
    private String wechat;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 经度
     */
    private BigDecimal longitude;
    
    /**
     * 纬度
     */
    private BigDecimal latitude;
    
    /**
     * 营业开始时间
     */
    private String businessStartTime;
    
    /**
     * 营业结束时间
     */
    private String businessEndTime;
    
    /**
     * 营业时间（例如：09:00-20:00）
     */
    private String businessHours;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 评价数量
     */
    private Integer reviewCount;
    
    /**
     * 状态(0-正常,1-暂停营业,2-永久关闭)
     */
    private Integer status;
    
    /**
     * 管理员姓名
     */
    private String managerName;
    
    /**
     * 管理员电话
     */
    private String managerPhone;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 