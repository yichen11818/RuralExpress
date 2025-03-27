package com.ruralexpress.exception;

import com.ruralexpress.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMsg = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", errorMsg);
        return ApiResult.invalidParam(errorMsg);
    }
    
    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMsg = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", errorMsg);
        return ApiResult.invalidParam(errorMsg);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMsg = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("约束违反: {}", errorMsg);
        return ApiResult.invalidParam(errorMsg);
    }
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ApiResult.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 处理未授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResult<String> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("未授权: {}", e.getMessage());
        return ApiResult.unauthorized(e.getMessage());
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleException(Exception e) {
        // 记录异常详细信息
        log.error("系统异常: {}", e.getMessage(), e);
        
        // 获取异常的详细信息
        String errorDetails = e.getClass().getName() + ": " + e.getMessage();
        
        // 添加堆栈跟踪信息到日志
        Throwable cause = e.getCause();
        if (cause != null) {
            errorDetails += " | 原因: " + cause.getClass().getName() + ": " + cause.getMessage();
        }
        
        // 在开发环境中返回详细错误信息（生产环境应该返回通用错误消息）
        return ApiResult.serverError("服务器内部错误: " + errorDetails);
    }
} 