package com.ptu.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * Swagger请求头处理过滤器
 * 用于处理Swagger文档请求，添加前缀并优化请求头
 */
@Slf4j
@Component
public class SwaggerHeaderFilter extends AbstractGatewayFilterFactory<SwaggerHeaderFilter.Config> {

    private static final String HEADER_NAME = "X-Forwarded-Prefix";
    
    public SwaggerHeaderFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            
            // 处理Swagger API文档路径
            if (path.contains("/v2/api-docs") || path.contains("/v3/api-docs")) {
                // 获取基础路径，用于设置X-Forwarded-Prefix
                String basePath = path;
                if (path.contains("/v2/api-docs")) {
                    basePath = path.substring(0, path.indexOf("/v2/api-docs"));
                } else {
                    basePath = path.substring(0, path.indexOf("/v3/api-docs"));
                }
                
                log.debug("设置Swagger请求前缀: {}", basePath);
                
                // 构建新请求，添加前缀头
                ServerHttpRequest newRequest = request.mutate()
                        .header(HEADER_NAME, basePath)
                        .build();
                
                // 传递修改后的请求
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
            
            // 其他Swagger资源请求直接放行
            return chain.filter(exchange);
        };
    }
    
    /**
     * 配置类
     */
    public static class Config {
        // 配置项（当前无需配置）
    }
} 