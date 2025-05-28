package com.ptu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户服务启动类
 * 负责用户相关微服务的启动与配置
 */
@SpringBootApplication
@MapperScan("com.ptu.user.mapper")
public class UserApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
} 