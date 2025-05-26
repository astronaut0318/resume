package com.ptu.document.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtils {

    /**
     * 生成token
     *
     * @param claims 数据声明
     * @param secret 密钥
     * @param expireTime 过期时间(秒)
     * @return token
     */
    public static String generateToken(Map<String, Object> claims, String secret, long expireTime) {
        Date expirationDate = new Date(System.currentTimeMillis() + expireTime * 1000);
        
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
    
    /**
     * 生成OnlyOffice文档编辑器的JWT token
     *
     * @param sourceType 文档来源类型
     * @param sourceId 文档来源ID
     * @param userId 用户ID
     * @param mode 编辑模式
     * @param secret JWT密钥
     * @param expireTime 过期时间(秒)
     * @return token
     */
    public static String generateDocumentToken(String sourceType, Long sourceId, Long userId, 
                                            String mode, String secret, int expireTime) {
        try {
            // 创建Claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("sourceType", sourceType);
            claims.put("sourceId", sourceId);
            claims.put("userId", userId);
            claims.put("mode", mode);
            
            // 设置JWT参数
            JwtBuilder builder = Jwts.builder()
                    .setSubject("document-access")
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expireTime * 1000L))
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)));
            
            // 生成JWT
            return builder.compact();
        } catch (Exception e) {
            log.error("生成JWT令牌异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 解析token
     *
     * @param token token
     * @param secret 密钥
     * @return 数据声明
     */
    public static Claims parseToken(String token, String secret) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build();
            
            return parser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.error("解析JWT令牌异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 验证token是否有效
     *
     * @param token token
     * @param secret 密钥
     * @return 是否有效
     */
    public static boolean validateToken(String token, String secret) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build();
            
            Claims claims = parser.parseClaimsJws(token).getBody();
            
            // 检查是否过期
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            log.error("验证JWT令牌异常: {}", e.getMessage());
            return false;
        }
    }
} 