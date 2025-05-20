package com.ptu.resume.auth.util;

import com.ptu.resume.auth.vo.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 *
 * @author PTU
 */
@Component
public class JwtUtil {

    /**
     * 密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 刷新令牌过期时间（毫秒）
     */
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 从令牌中获取过期日期
     *
     * @param token 令牌
     * @return 过期日期
     */
    public Date getExpirationDateFromToken(String token) {
            Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从令牌中获取载荷
     *
     * @param token 令牌
     * @return 载荷
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证令牌是否过期
     *
     * @param token 令牌
     * @return true: 已过期，false: 未过期
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成访问令牌
     *
     * @param user 用户信息
     * @return 访问令牌
     */
    public String generateAccessToken(UserVO user) {
        Map<String, Object> claims = new HashMap<>(8);
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());
        
        return generateToken(String.valueOf(user.getId()), claims, expiration);
    }

    /**
     * 生成刷新令牌
     *
     * @param user 用户信息
     * @return 刷新令牌
     */
    public String generateRefreshToken(UserVO user) {
        Map<String, Object> claims = new HashMap<>(8);
        claims.put("userId", user.getId());
        
        return generateToken(String.valueOf(user.getId()), claims, refreshExpiration);
    }

    /**
     * 生成令牌
     *
     * @param subject 主题
     * @param claims 载荷
     * @param expiration 过期时间
     * @return 令牌
     */
    private String generateToken(String subject, Map<String, Object> claims, long expiration) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 刷新令牌
     *
     * @param token 令牌
     * @return 新的令牌
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + expiration));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @return true: 有效，false: 无效
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}