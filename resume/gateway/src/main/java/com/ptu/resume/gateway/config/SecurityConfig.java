package com.ptu.resume.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置类
 *
 * @author PTU
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityConfig {
    
    /**
     * JWT密钥
     */
    private String secretKey = "abcdefghijklmnopqrstuvwxyz1234567890";
    
    /**
     * 白名单配置
     */
    private Ignore ignore = new Ignore();
    
    /**
     * 内部白名单配置类
     */
    @Data
    public static class Ignore {
        /**
         * 白名单URL列表
         */
        private List<String> whites = new ArrayList<>();
    }
} 