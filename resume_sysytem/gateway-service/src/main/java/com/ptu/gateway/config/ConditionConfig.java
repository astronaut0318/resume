package com.ptu.gateway.config;

import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 条件配置，用于禁用某些bean
 */
@Configuration
public class ConditionConfig {
    
    /**
     * 禁用MinIO配置
     * 注意：此类什么都不做，只是用来覆盖common-service中的MinioConfig
     * 通过条件注解确保只在minio.enabled=false时才会生效
     */
    @Configuration
    @ConditionalOnProperty(name = "minio.enabled", havingValue = "false")
    static class DisableMinioConfig {
        
        /**
         * 提供一个模拟的MinioClient
         */
        @Bean
        @Primary
        public MinioClient minioClient() {
            // 使用一个有效的URL，避免创建客户端时的连接错误
            return MinioClient.builder()
                    .endpoint("http://mock-minio-for-gateway")
                    .credentials("mockAccessKey", "mockSecretKey")
                    .build();
        }
    }
} 