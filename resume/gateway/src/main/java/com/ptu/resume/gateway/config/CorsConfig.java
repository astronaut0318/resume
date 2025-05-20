package com.ptu.resume.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 跨域配置
 *
 * @author PTU
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法，如GET、POST等
        config.addAllowedMethod("*");
        // 允许所有源站点
        config.addAllowedOriginPattern("*");
        // 允许携带Cookie
        config.setAllowCredentials(true);
        
        // 配置生效的路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
} 