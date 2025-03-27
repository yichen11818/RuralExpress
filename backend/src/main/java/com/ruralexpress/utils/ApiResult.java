package com.ruralexpress.utils;

import lombok.Data;

/**
 * 统一API响应结果封装
 */
@Data
public class ApiResult<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功结果
     * @param data 数据
     * @return API响应
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    /**
     * 成功结果
     * @param message 成功消息
     * @return API响应
     */
    public static <T> ApiResult<T> success(String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败结果
     * @param code 状态码
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResult<T> error(Integer code, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 参数错误
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResult<T> invalidParam(String message) {
        return error(400, message);
    }
    
    /**
     * 未认证
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResult<T> unauthorized(String message) {
        return error(401, message);
    }
    
    /**
     * 未授权
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResult<T> forbidden(String message) {
        return error(403, message);
    }
    
    /**
     * 资源不存在
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResult<T> notFound(String message) {
        return error(404, message);
    }
    
    /**
     * 服务器内部错误
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResult<T> serverError(String message) {
        return error(500, message);
    }
} 