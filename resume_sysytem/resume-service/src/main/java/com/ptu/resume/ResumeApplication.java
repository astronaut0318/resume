package com.ptu.resume;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 简历服务启动类
 * 负责简历相关微服务的启动与配置
 */
@SpringBootApplication
@MapperScan("com.ptu.resume.mapper")
public class ResumeApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ResumeApplication.class, args);
    }
} 