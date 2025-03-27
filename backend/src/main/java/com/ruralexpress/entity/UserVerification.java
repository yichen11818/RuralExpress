package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实名认证信息实体类
 */
@Data
@TableName("t_user_verification")
public class UserVerification {

    /**
     * 认证ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 身份证正面照片URL
     */
    private String frontImage;

    /**
     * 身份证反面照片URL
     */
    private String backImage;

    /**
     * 手持身份证照片URL
     */
    private String holdingImage;

    /**
     * 审核状态(0-待审核,1-已通过,2-未通过)
     */
    private Integer verifyStatus;

    /**
     * 审核时间
     */
    private LocalDateTime verifyTime;

    /**
     * 审核备注
     */
    private String verifyRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 