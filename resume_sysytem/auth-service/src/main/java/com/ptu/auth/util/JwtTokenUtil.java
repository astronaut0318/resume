package com.ptu.auth.util;

import com.ptu.auth.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtConfig jwtConfig;

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        String userId = getClaimFromToken(token, Claims::getSubject);
        return Long.valueOf(userId);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("username", String.class));
    }

    /**
     * 从令牌中获取用户角色
     *
     * @param token 令牌
     * @return 用户角色
     */
    public Integer getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", Integer.class));
    }

    /**
     * 检查令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 从令牌中获取过期时间
     *
     * @param token 令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取指定的声明
     *
     * @param token          令牌
     * @param claimsResolver 声明解析器
     * @param <T>           声明类型
     * @return 声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     *
     * @param token 令牌
     * @return 声明
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 生成访问令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     角色
     * @return 访问令牌
     */
    public String generateAccessToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        return generateToken(claims, userId.toString(), jwtConfig.getExpiration());
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return generateToken(claims, userId.toString(), jwtConfig.getRefreshExpiration());
    }

    /**
     * 生成令牌
     *
     * @param claims      声明
     * @param subject     主题(用户ID)
     * @param expiration  过期时间(秒)
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证令牌是否有效
     *
     * @param token      令牌
     * @param userId     用户ID
     * @return 是否有效
     */
    public Boolean validateToken(String token, Long userId) {
        final Long userIdFromToken = getUserIdFromToken(token);
        return (userIdFromToken.equals(userId) && !isTokenExpired(token));
    }

    /**
     * 从授权头中提取令牌
     *
     * @param authHeader 授权头
     * @return 令牌
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(jwtConfig.getTokenPrefix())) {
            return authHeader.substring(jwtConfig.getTokenPrefix().length()).trim();
        }
        return null;
    }
} 