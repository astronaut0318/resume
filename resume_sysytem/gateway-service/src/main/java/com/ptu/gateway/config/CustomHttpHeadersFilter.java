package com.ptu.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义HTTP头部过滤器
 * 专门处理Knife4j和Swagger相关的头部问题
 */
@Slf4j
@Component
public class CustomHttpHeadersFilter implements GlobalFilter, Ordered {

    // 请求头大小警告阈值（字节）- 统计目的
    private static final int HEADER_SIZE_WARNING_THRESHOLD = 20000;
    
    // Swagger/Knife4j的路径，需要特别处理头部
    private static final String[] SWAGGER_PATHS = {
        "/doc.html", 
        "/swagger-ui.html", 
        "/swagger-ui/", 
        "/v2/api-docs", 
        "/v3/api-docs",
        "/swagger-resources"
    };
    
    // 需要保留的头部 - 白名单策略
    private static final String[] ESSENTIAL_HEADERS = {
        "host", 
        "connection", 
        "content-type",
        "content-length",
        "accept",
        "swagger-request-source" 
    };
    
    // 需要截断的请求头及其最大长度
    private static final Map<String, Integer> TRIM_HEADER_LIMITS = new HashMap<>();
    static {
        TRIM_HEADER_LIMITS.put("cookie", 500);          // Cookie精简版
        TRIM_HEADER_LIMITS.put("authorization", 500);   // 授权令牌
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest originalRequest = exchange.getRequest();
        final String path = originalRequest.getURI().getPath();
        
        // 只处理Swagger/Knife4j相关路径
        boolean isSwaggerPath = false;
        for (String swaggerPath : SWAGGER_PATHS) {
            if (path.contains(swaggerPath)) {
                isSwaggerPath = true;
                break;
            }
        }
        
        // 如果不是Swagger相关路径，直接放行
        if (!isSwaggerPath) {
            return chain.filter(exchange);
        }
        
        // 计算头部大小
        HttpHeaders headers = originalRequest.getHeaders();
        int headersSize = calculateHeadersSize(headers);
        
        // 创建新请求，使用白名单策略严格清理
        ServerHttpRequest.Builder builder = originalRequest.mutate();
        
        // 先清除所有头（白名单策略），只保留必要的头
        final HttpHeaders clearedHeaders = new HttpHeaders();
        
        // 只保留必要的头
        for (String header : ESSENTIAL_HEADERS) {
            if (headers.containsKey(header)) {
                clearedHeaders.addAll(header, headers.get(header));
            }
        }
        
        // 对需要保留但截断的头部特殊处理
        for (Map.Entry<String, Integer> entry : TRIM_HEADER_LIMITS.entrySet()) {
            final String headerName = entry.getKey();
            final int maxLength = entry.getValue();
            
            if (headers.containsKey(headerName)) {
                List<String> values = headers.get(headerName);
                for (String value : values) {
                    if (value.length() > maxLength) {
                        // 添加截断后的值
                        clearedHeaders.add(headerName, value.substring(0, maxLength));
                    } else {
                        // 保持原值
                        clearedHeaders.add(headerName, value);
                    }
                }
            }
        }
        
        // 替换所有头部
        builder.headers(httpHeaders -> {
            httpHeaders.clear();
            httpHeaders.addAll(clearedHeaders);
        });
        
        // 计算清理后的头部大小
        int newHeadersSize = calculateHeadersSize(clearedHeaders);
        
        // 记录头部清理情况
        if (headersSize > HEADER_SIZE_WARNING_THRESHOLD) {
            log.info("Swagger请求头已优化 - 路径: {}, 原始大小: {}字节, 清理后: {}字节, 减少: {}%", 
                    path, headersSize, newHeadersSize, 
                    Math.round((1 - (float)newHeadersSize/headersSize) * 100));
        }
        
        // 使用修改后的请求继续过滤器链
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    /**
     * 计算所有请求头大小
     */
    private int calculateHeadersSize(HttpHeaders headers) {
        int size = 0;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            size += entry.getKey().length();
            for (String value : entry.getValue()) {
                size += value.length();
            }
        }
        return size;
    }

    @Override
    public int getOrder() {
        // 高优先级执行，但要确保在请求日志过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE + 5;
    }
} 