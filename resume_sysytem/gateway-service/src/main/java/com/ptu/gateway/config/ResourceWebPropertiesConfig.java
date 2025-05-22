package com.ptu.gateway.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 资源配置类
 */
@Configuration
public class ResourceWebPropertiesConfig {
    
    /**
     * 提供Web资源属性
     */
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
} 