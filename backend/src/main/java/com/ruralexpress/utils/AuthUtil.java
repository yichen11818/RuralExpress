package com.ruralexpress.utils;

import com.ruralexpress.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 认证工具类，用于获取当前登录用户信息
 */
@Component
public class AuthUtil {

    /**
     * 用户ID在Session中的key
     */
    private static final String USER_ID_KEY = "userId";
    
    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public Long getCurrentUserId() {
        HttpServletRequest request = getRequest();
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            // 对于演示项目，如果没有登录，返回一个模拟用户ID
            return 1L;
            // 在实际项目中应该抛出异常
            //throw new BusinessException("用户未登录");
        }
        
        Object userIdObj = session.getAttribute(USER_ID_KEY);
        if (userIdObj == null) {
            // 对于演示项目，如果没有登录，返回一个模拟用户ID
            return 1L;
            // 在实际项目中应该抛出异常
            //throw new BusinessException("用户未登录");
        }
        
        return (Long) userIdObj;
    }
    
    /**
     * 获取当前请求对象
     * @return HttpServletRequest
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("获取请求上下文失败");
        }
        return attributes.getRequest();
    }
    
    /**
     * 设置当前登录用户ID（用于测试）
     * @param userId 用户ID
     */
    public void setCurrentUserId(Long userId) {
        HttpServletRequest request = getRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_ID_KEY, userId);
    }
} 