package com.ptu.document;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 文档服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.ptu.document.mapper")
@EnableFeignClients(basePackages = "com.ptu.document.feign")
public class DocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }
} 