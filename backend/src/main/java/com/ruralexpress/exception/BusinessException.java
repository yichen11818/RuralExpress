package com.ruralexpress.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造方法
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this(400, message);
    }
} 