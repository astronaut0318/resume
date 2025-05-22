package com.ptu.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * SpringFox配置
 * 用于禁用默认的SwaggerResourcesProvider
 */
@Configuration
public class SpringFoxConfig {

    /**
     * 禁用SpringFox默认的InMemorySwaggerResourcesProvider
     * 返回一个空Provider，被标记为ConditionalOnMissingBean，
     * 这样当有其他Provider存在时，这个不会被实例化
     */
    @Bean
    @ConditionalOnMissingBean(InMemorySwaggerResourcesProvider.class)
    public SwaggerResourcesProvider inMemorySwaggerResourcesProvider() {
        return () -> java.util.Collections.emptyList();
    }
} 