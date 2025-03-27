package com.ruralexpress.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF，因为我们使用JWT，不需要防止CSRF攻击
            .csrf().disable()
            // 基于token，不需要session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 明确禁用HTTP基本认证
            .httpBasic().disable()
            // 请求授权
            .authorizeRequests()
            // 允许所有静态资源
            .antMatchers("/**/*.js", "/**/*.css", "/**/*.html", "/**/*.ico").permitAll()
            // 允许swagger相关请求
            .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/v2/**", "/doc.html").permitAll()
            // 允许health检查
            .antMatchers("/actuator/**").permitAll()
            // 允许公开接口 - 确保完整的路径匹配
            .antMatchers("/auth/**", "/auth/login", "/user/register", "/home/**").permitAll()
            // 其他请求需要认证
            .anyRequest().authenticated();
        
        // 添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // 禁用缓存
        http.headers().cacheControl();
    }
} 