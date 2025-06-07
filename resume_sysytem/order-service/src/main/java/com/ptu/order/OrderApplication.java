package com.ptu.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 订单服务启动类
 * 负责订单相关微服务的启动与配置
 */
@SpringBootApplication
@MapperScan("com.ptu.order.mapper")
@EnableFeignClients(basePackages = "com.ptu.order.feign")
@ComponentScan(basePackages = {"com.ptu.order", "com.ptu.common"})
@EnableConfigurationProperties
public class OrderApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
} 