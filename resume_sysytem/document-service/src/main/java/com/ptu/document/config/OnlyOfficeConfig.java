package com.ptu.document.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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
    
    /**
     * 文件格式配置
     */
    private Formats formats = new Formats();
    
    /**
     * 是否忽略SSL证书错误
     */
    private boolean ignoreSsl = false;
    
    /**
     * 允许有损编辑的文件格式
     */
    private String lossyEditFormats;
    
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
    
    @Data
    public static class Formats {
        /**
         * 可编辑格式
         */
        private Map<String, String> editable;
        
        /**
         * 可转换格式
         */
        private Map<String, String> convertible;
    }
} 