package com.ruralexpress.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌工具类
 */
@Slf4j
@Component
public class JwtTokenUtil {
    
    @Value("${app.jwt.secret-key}")
    private String secretKeyString;
    
    private Key secretKey;
    
    @Value("${app.jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds;
    
    @Value("${app.jwt.token-validity-in-seconds-for-remember-me}")
    private long tokenValidityInSecondsForRememberMe;
    
    /**
     * 初始化密钥
     */
    @PostConstruct
    public void init() {
        log.info("初始化JWT密钥");
        try {
            // 使用密钥生成算法创建安全的密钥
            secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
            log.info("JWT密钥初始化成功");
        } catch (Exception e) {
            log.error("JWT密钥初始化失败: {}", e.getMessage(), e);
            // 如果配置的密钥不够强，创建一个随机安全密钥
            secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            log.info("已创建安全的随机JWT密钥替代");
        }
    }
    
    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        String idStr = getClaimFromToken(token, Claims::getSubject);
        return Long.parseLong(idStr);
    }
    
    /**
     * 从令牌中获取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 从令牌中获取指定声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从令牌中获取所有声明
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 检查令牌是否已过期
     * @param token JWT令牌
     * @return 是否已过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 生成令牌
     * @param userId 用户ID
     * @param rememberMe 是否记住我
     * @return JWT令牌
     */
    public String generateToken(Long userId, boolean rememberMe) {
        try {
            Map<String, Object> claims = new HashMap<>();
            long now = System.currentTimeMillis();
            long expiration = rememberMe 
                    ? now + tokenValidityInSecondsForRememberMe * 1000
                    : now + tokenValidityInSeconds * 1000;
                    
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userId.toString())
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(expiration))
                    .signWith(secretKey)
                    .compact();
        } catch (Exception e) {
            log.error("生成JWT令牌失败: {}", e.getMessage(), e);
            throw new RuntimeException("无法生成认证令牌，请稍后重试", e);
        }
    }
    
    /**
     * 验证令牌
     * @param token JWT令牌
     * @return 是否有效
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("验证令牌失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 