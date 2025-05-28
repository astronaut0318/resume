package com.ptu.auth;

import com.ptu.common.config.JacksonConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * 认证服务启动类
 * 负责认证相关微服务的启动与配置
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.ptu.auth.feign")
@Import(JacksonConfig.class)
public class AuthApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
} 