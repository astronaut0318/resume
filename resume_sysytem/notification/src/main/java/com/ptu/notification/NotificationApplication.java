package com.ptu.notification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 通知服务启动类
 * 负责通知相关微服务的启动与配置
 */
@SpringBootApplication
@MapperScan("com.ptu.notification.mapper")
@EnableFeignClients(basePackages = "com.ptu.notification.feign")
public class NotificationApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
} 