package com.ptu.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 网关配置类
 */
@Configuration
public class GatewayConfig {

    @Value("${gateway.security.ignore-urls:#{null}}")
    private List<String> ignoreUrls;

    /**
     * 限流的key解析器，使用请求IP作为限流键
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * 获取白名单路径（不需要认证的路径）
     */
    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }
} 