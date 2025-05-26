package com.ptu.document.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OnlyOffice配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "onlyoffice")
public class OnlyOfficeConfig {
    
    /**
     * 文档服务器地址
     */
    private String documentServerUrl;
    
    /**
     * 回调URL
     */
    private String callbackUrl;
    
    /**
     * JWT密钥
     */
    private String jwtSecret;
    
    /**
     * Token有效期（秒）
     */
    private Integer tokenTtl = 300;
    
    /**
     * 文档配置
     */
    private Document document = new Document();
    
    @Data
    public static class Document {
        /**
         * 存储路径
         */
        private String storagePath = "documents";
        
        /**
         * 是否启用版本控制
         */
        private boolean versionsEnabled = true;
        
        /**
         * 最大版本数
         */
        private int maxVersions = 10;
    }
} 