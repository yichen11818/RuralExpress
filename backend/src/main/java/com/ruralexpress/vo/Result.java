package com.ruralexpress.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应结果封装
 */
@Data
@NoArgsConstructor
@ApiModel(description = "响应结果")
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "状态码", required = true)
    private Integer code;
    
    @ApiModelProperty(value = "是否成功", required = true)
    private Boolean success;
    
    @ApiModelProperty(value = "返回消息", required = true)
    private String message;
    
    @ApiModelProperty(value = "返回数据")
    private T data;
    
    private Result(Integer code, String message, T data, Boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }
    
    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, true);
    }
    
    /**
     * 成功返回结果
     * 
     * @param data 返回数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, true);
    }
    
    /**
     * 成功返回结果
     * 
     * @param data 返回数据
     * @param message 返回消息
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(200, message, data, true);
    }
    
    /**
     * 失败返回结果
     * 
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null, false);
    }
    
    /**
     * 失败返回结果
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, false);
    }
    
    /**
     * 参数验证失败返回结果
     * 
     * @param message 错误消息
     */
    public static <T> Result<T> validateFailed(String message) {
        return new Result<>(400, message, null, false);
    }
    
    /**
     * 未登录返回结果
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "暂未登录或token已过期", null, false);
    }
    
    /**
     * 未授权返回结果
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "没有相关权限", null, false);
    }
} 