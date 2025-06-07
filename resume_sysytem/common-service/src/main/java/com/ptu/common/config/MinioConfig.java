package com.ptu.common.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置类
 */
@Data
@Configuration
@ConditionalOnClass(MinioClient.class)
@ConditionalOnProperty(prefix = "minio", name = "endpoint")
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
         * 模板文件存储桶
         */
        private String template = "resume-templates";
        
        /**
         * 缩略图存储桶
         */
        private String thumbnail = "resume-thumbnails";

        /**
         * 头像文件存储桶
         */
        private String avatar = "resume-avatars";
    }

    /**
     * 创建MinioClient对象
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
} 