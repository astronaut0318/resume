package com.ptu.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 文件服务启动类
 * 负责文件相关微服务的启动与配置
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.ptu.file", "com.ptu.common.util", "com.ptu.common.config"})
@MapperScan("com.ptu.file.mapper")
public class FileApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }
} 