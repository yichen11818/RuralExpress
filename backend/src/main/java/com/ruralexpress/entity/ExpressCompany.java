package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 快递公司实体类
 */
@Data
@TableName("t_express_company")
public class ExpressCompany {
    
    /**
     * 公司ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 公司名称
     */
    private String name;
    
    /**
     * 公司编码
     */
    private String code;
    
    /**
     * 公司LOGO
     */
    private String logo;
    
    /**
     * 公司联系电话
     */
    private String phone;
    
    /**
     * 公司官网
     */
    private String website;
    
    /**
     * 状态(0-正常,1-禁用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 