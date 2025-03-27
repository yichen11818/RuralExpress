package com.ruralexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 包裹实体类
 */
@Data
@TableName("t_package")
public class Package {
    
    /**
     * 包裹ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 快递公司ID
     */
    private Long companyId;
    
    /**
     * 快递单号
     */
    private String trackingNo;
    
    /**
     * 收件人姓名
     */
    private String receiverName;
    
    /**
     * 收件人电话
     */
    private String receiverPhone;
    
    /**
     * 收件地址
     */
    private String receiverAddress;
    
    /**
     * 包裹状态(0-待收取,1-已收取,2-已签收,3-异常)
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 预计送达时间
     */
    private LocalDateTime estimatedDeliveryTime;
    
    /**
     * 签收时间
     */
    private LocalDateTime signedTime;
    
    /**
     * 取件方式(0-上门取件,1-服务点自取)
     */
    private Integer deliveryType;
    
    /**
     * 服务点ID
     */
    private Long stationId;
    
    /**
     * 快递员ID
     */
    private Long courierId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 