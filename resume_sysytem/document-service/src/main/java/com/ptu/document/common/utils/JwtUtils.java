package com.ptu.document.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成token
     *
     * @param claims 数据声明
     * @param secret 密钥
     * @param expireTime 过期时间(秒)
     * @return token
     */
    public static String generateToken(Map<String, Object> claims, String secret, long expireTime) {
        try {
            Date expirationDate = new Date(System.currentTimeMillis() + expireTime * 1000);
            
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(expirationDate)
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .compact();
        } catch (Exception e) {
            // 如果使用标准方法失败，尝试使用自定义JWT方法
            log.warn("标准JWT生成失败，尝试使用自定义JWT方法: {}", e.getMessage());
            return generateCustomJwtToken(claims, secret, expireTime);
        }
    }
    
    /**
     * 使用自定义实现生成JWT令牌，不依赖库的密钥长度限制
     * 
     * @param claims 数据声明
     * @param secret 密钥
     * @param expireTime 过期时间(秒)
     * @return token
     */
    public static String generateCustomJwtToken(Map<String, Object> claims, String secret, long expireTime) {
        try {
            // 添加过期时间
            claims.put("exp", System.currentTimeMillis() / 1000 + expireTime);
            
            // 1. 创建JWT头部
            Map<String, Object> header = new HashMap<>();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            
            // 2. 将头部和负载转换为Base64URL编码的字符串
            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(claims);
            
            String headerBase64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
            
            // 3. 创建签名
            String dataToSign = headerBase64 + "." + payloadBase64;
            byte[] signature = createHmacSha256Signature(dataToSign.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8));
            String signatureBase64 = base64UrlEncode(signature);
            
            // 4. 组合成JWT令牌
            return headerBase64 + "." + payloadBase64 + "." + signatureBase64;
        } catch (Exception e) {
            log.error("生成自定义JWT令牌异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 创建HMAC-SHA256签名
     */
    private static byte[] createHmacSha256Signature(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }
    
    /**
     * Base64URL编码
     */
    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
    
    /**
     * Base64URL解码
     */
    private static byte[] base64UrlDecode(String data) {
        return Base64.getUrlDecoder().decode(data);
    }
    
    /**
     * 使用Nimbus JOSE+JWT库生成token（可以接受较短密钥）
     * 
     * @param claims 数据声明
     * @param secret 密钥
     * @param expireTime 过期时间(秒)
     * @return token
     */
    public static String generateTokenWithNimbus(Map<String, Object> claims, String secret, long expireTime) {
        try {
            // 创建JWT负载
            JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
            
            // 添加所有声明
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                claimsSetBuilder.claim(entry.getKey(), entry.getValue());
            }
            
            // 添加过期时间
            claimsSetBuilder.expirationTime(new Date(System.currentTimeMillis() + expireTime * 1000));
            
            // 创建签名的JWT
            JWTClaimsSet claimsSet = claimsSetBuilder.build();
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            
            // 使用密钥签名
            JWSSigner signer = new MACSigner(secret.getBytes(StandardCharsets.UTF_8));
            signedJWT.sign(signer);
            
            // 输出为字符串
            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("使用Nimbus生成JWT令牌异常: {}", e.getMessage(), e);
            return generateCustomJwtToken(claims, secret, expireTime);
        }
    }
    
    /**
     * 使用较短密钥生成token（不推荐用于生产环境）
     * 
     * @param claims 数据声明
     * @param secret 密钥
     * @param expireTime 过期时间(秒)
     * @return token
     */
    public static String generateTokenWithWeakSecret(Map<String, Object> claims, String secret, long expireTime) {
        try {
            Date expirationDate = new Date(System.currentTimeMillis() + expireTime * 1000);
            
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(expirationDate)
                    .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
                    .compact();
        } catch (Exception e) {
            log.error("使用弱密钥生成JWT令牌异常: {}", e.getMessage(), e);
            return generateCustomJwtToken(claims, secret, expireTime);
        }
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
            
            // 直接使用自定义JWT生成方法
            return generateCustomJwtToken(claims, secret, expireTime);
        } catch (Exception e) {
            log.error("生成JWT令牌异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 自定义方法解析JWT令牌
     * 
     * @param token JWT令牌
     * @param secret 密钥
     * @return 解析的声明
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseCustomJwtToken(String token, String secret) {
        try {
            // 1. 分割JWT令牌
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }
            
            // 2. 解码头部和负载
            String headerJson = new String(base64UrlDecode(parts[0]), StandardCharsets.UTF_8);
            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            
            // 3. 验证签名
            String dataToSign = parts[0] + "." + parts[1];
            byte[] expectedSignature = createHmacSha256Signature(dataToSign.getBytes(StandardCharsets.UTF_8), 
                                                              secret.getBytes(StandardCharsets.UTF_8));
            byte[] actualSignature = base64UrlDecode(parts[2]);
            
            if (!constantTimeEquals(expectedSignature, actualSignature)) {
                throw new IllegalArgumentException("JWT signature verification failed");
            }
            
            // 4. 解析声明
            Map<String, Object> claims = objectMapper.readValue(payloadJson, Map.class);
            
            // 5. 检查过期时间
            Number expNum = (Number) claims.get("exp");
            if (expNum != null) {
                long expirationTime = expNum.longValue() * 1000;
                if (expirationTime < System.currentTimeMillis()) {
                    throw new IllegalArgumentException("JWT token is expired");
                }
            }
            
            return claims;
        } catch (Exception e) {
            log.error("解析自定义JWT令牌异常: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 恒定时间比较两个字节数组，防止时间攻击
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
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
            // 如果标准方法失败，尝试使用自定义解析方法
            log.warn("标准JWT解析方法失败，尝试使用自定义解析方法: {}", e.getMessage());
            try {
                Map<String, Object> claims = parseCustomJwtToken(token, secret);
                if (claims != null) {
                    Claims result = Jwts.claims();
                    result.putAll(claims);
                    return result;
                }
                return null;
            } catch (Exception ex) {
                log.error("使用自定义方法解析JWT令牌异常: {}", ex.getMessage(), ex);
                return null;
            }
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
            // 尝试使用自定义方法验证
            Map<String, Object> claims = parseCustomJwtToken(token, secret);
            return claims != null;
        } catch (Exception e) {
            log.error("验证JWT令牌异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建简单的签名
     * 
     * @param data 要签名的数据
     * @param secret 密钥
     * @return Base64编码的签名
     */
    public static String createSimpleSignature(String data, String secret) {
        try {
            byte[] signature = createHmacSha256Signature(data.getBytes(StandardCharsets.UTF_8), secret.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signature);
        } catch (Exception e) {
            log.error("创建签名异常: {}", e.getMessage(), e);
            return "";
        }
    }
} 