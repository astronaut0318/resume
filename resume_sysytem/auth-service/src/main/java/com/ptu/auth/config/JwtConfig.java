package com.ptu.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间(秒)
     */
    private long expiration;

    /**
     * 刷新令牌过期时间(秒)
     */
    private long refreshExpiration;

    /**
     * 请求头名称
     */
    private String header;

    /**
     * 令牌前缀
     */
    private String tokenPrefix;
} 