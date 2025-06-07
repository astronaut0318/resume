package com.ptu.document.common.util;

import com.ptu.document.config.OnlyOfficeConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtil {

    /**
     * 生成JWT token
     * @param payload 数据载荷
     * @param config OnlyOffice配置
     * @return JWT token
     */
    public static String generateToken(Map<String, Object> payload, OnlyOfficeConfig config) {
        if (!StringUtils.hasText(config.getJwtSecret())) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        
        return Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + config.getTokenTtl() * 1000L))
                .signWith(SignatureAlgorithm.HS256, config.getJwtSecret().getBytes(StandardCharsets.UTF_8))
                .compact();
    }
    
    /**
     * 验证JWT token
     * @param token JWT token
     * @param config OnlyOffice配置
     * @return 验证结果
     */
    public static boolean validateToken(String token, OnlyOfficeConfig config) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(config.getJwtSecret())) {
            return false;
        }
        
        try {
            Jwts.parser()
                    .setSigningKey(config.getJwtSecret().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从JWT token中解析载荷
     * @param token JWT token
     * @param config OnlyOffice配置
     * @return 载荷Claims
     */
    public static Claims parseToken(String token, OnlyOfficeConfig config) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(config.getJwtSecret())) {
            return null;
        }
        
        try {
            return Jwts.parser()
                    .setSigningKey(config.getJwtSecret().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
} 