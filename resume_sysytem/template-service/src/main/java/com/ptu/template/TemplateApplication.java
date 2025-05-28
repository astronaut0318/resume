package com.ptu.template;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 模板服务启动类
 * 负责模板相关微服务的启动与配置
 */
@SpringBootApplication
@ComponentScan({"com.ptu.template", "com.ptu.common"})
@MapperScan("com.ptu.template.mapper")
public class TemplateApplication {
    /**
     * 应用主入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class, args);
    }
} 