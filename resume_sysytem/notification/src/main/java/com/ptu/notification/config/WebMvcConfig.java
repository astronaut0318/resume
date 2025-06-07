package com.ptu.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 添加CORS支持，确保前端可以正常请求API
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 配置CORS
     * 允许前端跨域访问API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许所有方法
                .allowedHeaders("*") // 允许所有头
                .allowCredentials(true) // 允许发送cookie
                .maxAge(3600); // 预检请求的缓存时间（秒）
    }
} 