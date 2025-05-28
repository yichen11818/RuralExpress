package com.ruralexpress.common;

/**
 * API 统一响应结果封装类
 * @param <T> 数据类型
 */
public class ApiResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;
    public static final String SUCCESS_MESSAGE = "操作成功";
    public static final String ERROR_MESSAGE = "操作失败";

    public ApiResult() {
    }

    public ApiResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResult<T> success(String msg, T data) {
        return new ApiResult<>(SUCCESS_CODE, msg, data);
    }

    public static <T> ApiResult<T> fail() {
        return new ApiResult<>(ERROR_CODE, ERROR_MESSAGE, null);
    }

    public static <T> ApiResult<T> fail(String msg) {
        return new ApiResult<>(ERROR_CODE, msg, null);
    }

    public static <T> ApiResult<T> fail(Integer code, String msg) {
        return new ApiResult<>(code, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
} 