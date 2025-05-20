package com.ptu.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 请求日志过滤器
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String remoteAddress = request.getRemoteAddress().getHostString();
        
        // 记录请求头
        StringBuilder headerInfo = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            headerInfo.append(entry.getKey()).append(": ").append(String.join(",", entry.getValue())).append("; ");
        }
        
        log.info("收到请求 - 路径: {}, 方法: {}, 远程地址: {}, 请求头: {}", path, method, remoteAddress, headerInfo);
        
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            log.info("请求完成 - 路径: {}, 处理时间: {}ms, 状态码: {}", 
                    path, executeTime, exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        // 设置为最高优先级，确保这个过滤器首先执行
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 