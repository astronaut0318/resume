package com.ptu.document.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    
    /**
     * 服务地址
     */
    private String endpoint;
    
    /**
     * 访问密钥
     */
    private String accessKey;
    
    /**
     * 密钥
     */
    private String secretKey;
    
    /**
     * 存储桶配置
     */
    private Bucket bucket = new Bucket();
    
    @Data
    public static class Bucket {
        /**
         * 文档存储桶
         */
        private String document = "resume-documents";
        
        /**
         * 版本存储桶
         */
        private String version = "resume-document-versions";
    }
    
    /**
     * 获取文档存储桶名称
     */
    public String getDocumentBucket() {
        return bucket.getDocument();
    }
    
    /**
     * 获取版本存储桶名称
     */
    public String getVersionBucket() {
        return bucket.getVersion();
    }
    
    /**
     * 创建MinioClient
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
} 