package com.ptu.document;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 文档服务启动类
 * 负责文档相关微服务的启动与配置
 */
@SpringBootApplication
@MapperScan("com.ptu.document.dao")
@EnableFeignClients(basePackages = "com.ptu.document.feign")
public class DocumentApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(DocumentApplication.class, args);
    }
} 