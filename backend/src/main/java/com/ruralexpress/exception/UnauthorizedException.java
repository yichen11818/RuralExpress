package com.ruralexpress.exception;

/**
 * 未授权异常
 */
public class UnauthorizedException extends RuntimeException {
    
    /**
     * 构造方法
     * @param message 错误消息
     */
    public UnauthorizedException(String message) {
        super(message);
    }
    
    /**
     * 构造方法
     */
    public UnauthorizedException() {
        super("未授权，请先登录");
    }
} 