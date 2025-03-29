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
@TableName("t_company")
public class Company {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 快递公司编码
     */
    private String code;

    /**
     * 快递公司名称
     */
    private String name;

    /**
     * 快递公司简称
     */
    private String shortName;

    /**
     * 快递公司Logo
     */
    private String logo;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态 (0-禁用，1-启用)
     */
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