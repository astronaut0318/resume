package com.ptu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 模板服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.ptu.template.mapper")
@ComponentScan({"com.ptu.template", "com.ptu.common"})
public class TemplateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TemplateServiceApplication.class, args);
    }
} 