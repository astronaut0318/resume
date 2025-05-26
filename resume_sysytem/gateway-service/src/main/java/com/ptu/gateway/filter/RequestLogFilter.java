package com.ptu.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求日志过滤器
 * 记录请求的路径、方法和处理时间
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求信息
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // 对Swagger请求不做详细日志，减少日志量
        if (isSwaggerRequest(path) && !log.isDebugEnabled()) {
            return chain.filter(exchange);
        }
        
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 计算处理时间
            long executeTime = System.currentTimeMillis() - startTime;
            
            // 获取响应状态
            ServerHttpResponse response = exchange.getResponse();
            HttpStatus statusCode = response.getStatusCode();
            
            // 记录请求处理情况
            if (statusCode == null) {
                log.info("请求处理 - 路径: {}, 方法: {}, 处理时间: {}ms", path, method, executeTime);
            } else if (statusCode.is2xxSuccessful()) {
                log.info("请求成功 - 路径: {}, 方法: {}, 状态码: {}, 处理时间: {}ms", 
                        path, method, statusCode.value(), executeTime);
            } else {
                log.warn("请求异常 - 路径: {}, 方法: {}, 状态码: {}, 处理时间: {}ms", 
                        path, method, statusCode.value(), executeTime);
            }
        }));
    }

    /**
     * 判断是否为Swagger相关请求
     */
    private boolean isSwaggerRequest(String path) {
        return path.endsWith("/doc.html") || 
               path.endsWith("/swagger-ui.html") || 
               path.contains("/swagger-resources") || 
               path.contains("/webjars/") || 
               path.contains("/v2/api-docs") || 
               path.contains("/v3/api-docs");
    }

    @Override
    public int getOrder() {
        // 设置为高优先级，但不是最高
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
} 