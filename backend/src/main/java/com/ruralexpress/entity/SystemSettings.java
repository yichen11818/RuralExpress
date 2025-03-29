package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统设置实体类
 */
@Data
@TableName("t_system_settings")
public class SystemSettings {
    
    /**
     * 设置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 设置键
     */
    private String settingKey;
    
    /**
     * 设置值
     */
    private String settingValue;
    
    /**
     * 设置描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 系统名称（非数据库字段）
     */
    @TableField(exist = false)
    private String siteName;
    
    /**
     * 系统LOGO（非数据库字段）
     */
    @TableField(exist = false)
    private String siteLogo;
    
    /**
     * ICP备案号（非数据库字段）
     */
    @TableField(exist = false)
    private String icp;
    
    /**
     * 客服电话（非数据库字段）
     */
    @TableField(exist = false)
    private String servicePhone;
    
    /**
     * 是否开放注册（非数据库字段）
     */
    @TableField(exist = false)
    private Integer enableRegister;
    
    /**
     * 是否启用验证码（非数据库字段）
     */
    @TableField(exist = false)
    private Integer enableCaptcha;
    
    /**
     * 是否启用短信通知（非数据库字段）
     */
    @TableField(exist = false)
    private Integer enableSmsNotify;
    
    /**
     * 是否启用邮件通知（非数据库字段）
     */
    @TableField(exist = false)
    private Integer enableEmailNotify;
    
    /**
     * 是否启用应用内通知（非数据库字段）
     */
    @TableField(exist = false)
    private Integer enableAppNotify;
    
    /**
     * 是否启用维护模式（非数据库字段）
     */
    @TableField(exist = false)
    private Integer maintenanceMode;
    
    /**
     * 维护说明（非数据库字段）
     */
    @TableField(exist = false)
    private String maintenanceMessage;
} 