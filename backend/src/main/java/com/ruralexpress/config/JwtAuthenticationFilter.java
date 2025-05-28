package com.ruralexpress.config;

import com.ruralexpress.entity.User;
import com.ruralexpress.mapper.UserMapper;
import com.ruralexpress.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.info("请求路径: {}, 方法: {}", requestURI, method);
        
        try {
            // 从请求中获取令牌
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                log.info("JWT令牌存在，尝试验证");
                // 如果令牌不为空且有效，则设置认证信息
                if (jwtTokenUtil.validateToken(jwt)) {
                    // 从令牌中获取用户ID
                    Long userId = jwtTokenUtil.getUserIdFromToken(jwt);
                    log.info("JWT令牌验证成功，用户ID: {}", userId);
                    
                    // 查询用户信息，获取用户类型
                    User user = userMapper.selectById(userId);
                    if (user != null) {
                        // 根据用户类型设置不同的权限
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        
                        // 基本用户角色
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                        
                        // 根据用户类型添加额外角色
                        if (user.getUserType() == 1) {
                            // 快递员
                            authorities.add(new SimpleGrantedAuthority("ROLE_COURIER"));
                        } else if (user.getUserType() == 2) {
                            // 管理员
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        }
                        
                        // 创建认证信息
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userId, null, authorities);
                        
                        // 设置认证信息
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.info("用户{}已被授予权限: {}", userId, authorities);
                    } else {
                        log.warn("无法找到用户: {}", userId);
                    }
                } else {
                    log.warn("JWT令牌无效");
                }
            } else {
                log.info("请求中不包含JWT令牌");
            }
        } catch (Exception e) {
            log.error("无法设置用户认证: {}", e.getMessage(), e);
        }
        
        // 继续过滤链
        chain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取JWT令牌
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // 从Authorization头中获取令牌
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 