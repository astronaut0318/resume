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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求日志过滤器
 * 增强版 - 添加请求头处理和优化功能
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    // 请求头大小警告阈值（字节）
    private static final int HEADER_SIZE_WARNING_THRESHOLD = 40000;
    
    // 请求头截断阈值（字节）- 超过此值将触发修改
    private static final int HEADER_SIZE_TRUNCATE_THRESHOLD = 50000;
    
    // 可能导致问题的大型请求头，及其最大允许长度
    private static final Map<String, Integer> LARGE_HEADER_LIMITS = new HashMap<>();
    static {
        // 设置可能导致问题的大型请求头的限制
        LARGE_HEADER_LIMITS.put("cookie", 10000);       // Cookie通常最大
        LARGE_HEADER_LIMITS.put("authorization", 2000); // 授权令牌
        LARGE_HEADER_LIMITS.put("x-forwarded-for", 100); // 转发信息
        LARGE_HEADER_LIMITS.put("user-agent", 500);     // 用户代理
        LARGE_HEADER_LIMITS.put("accept", 500);         // 接受内容类型
        LARGE_HEADER_LIMITS.put("accept-encoding", 100); // 编码类型
        LARGE_HEADER_LIMITS.put("accept-language", 100); // 语言设置
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取初始请求
        final ServerHttpRequest originalRequest = exchange.getRequest();
        final String path = originalRequest.getURI().getPath();
        final String method = originalRequest.getMethod().name();
        
        // Swagger相关请求特殊处理：最小化日志和处理
        if (isSwaggerRequest(path)) {
            // 对Swagger请求不做详细日志，减少日志量
            if (log.isDebugEnabled()) {
                log.debug("处理Swagger请求 - 路径: {}", path);
            }
            
            // 自动移除大请求头对Swagger请求
            ServerHttpRequest modifiedRequest = removeUnnecessaryHeaders(originalRequest);
            ServerWebExchange newExchange = exchange.mutate().request(modifiedRequest).build();
            
            // 直接放行，不做额外处理
            return chain.filter(newExchange);
        }
        
        // 计算请求头大小
        final int headersCount = originalRequest.getHeaders().size();
        final int headersSize = calculateHeadersSize(originalRequest.getHeaders());
        
        // 创建新的请求变量和处理请求头
        ServerWebExchange processedExchange = exchange;
        
        // 如果请求头超过截断阈值，创建新请求并修改头部
        if (headersSize > HEADER_SIZE_TRUNCATE_THRESHOLD) {
            ServerHttpRequest modifiedRequest = truncateHeaders(originalRequest);
            processedExchange = exchange.mutate().request(modifiedRequest).build();
            
            // 记录已处理信息
            log.info("请求头已处理 - 路径: {}, 原始大小: {}字节, 处理后大小: {}字节", 
                    path, headersSize, calculateHeadersSize(modifiedRequest.getHeaders()));
        }
        // 如果只是超过警告阈值，记录警告但不修改
        else if (headersSize > HEADER_SIZE_WARNING_THRESHOLD) {
            log.warn("请求头较大 - 路径: {}, 请求头数量: {}, 请求头大小: {}字节", 
                    path, headersCount, headersSize);
        }
        
        // 保存起始时间用于计算处理时间
        final long startTime = System.currentTimeMillis();
        
        // 为Mono.fromRunnable创建所需的final变量
        final ServerWebExchange finalExchange = processedExchange;
        
        // 执行过滤器链并添加完成后的处理逻辑
        return chain.filter(processedExchange).then(Mono.fromRunnable(() -> {
            // 计算处理时间
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            
            // 获取响应状态
            ServerHttpResponse response = finalExchange.getResponse();
            HttpStatus statusCode = response.getStatusCode();
            
            // 对错误请求进行特殊处理
            if (statusCode != null && !statusCode.is2xxSuccessful()) {
                if (statusCode == HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE) {
                    log.error("请求头过大 - 路径: {}, 状态码: {}, 处理时间: {}ms", 
                            path, statusCode, executeTime);
                    
                    // 仅在特定错误情况下记录头部信息
                    if (log.isDebugEnabled()) {
                        logImportantHeaders(originalRequest.getHeaders());
                    }
                } else {
                    log.warn("请求错误 - 路径: {}, 状态码: {}, 处理时间: {}ms", 
                            path, statusCode, executeTime);
                }
            }
        }));
    }

    /**
     * 截断过大的请求头
     */
    private ServerHttpRequest truncateHeaders(ServerHttpRequest request) {
        ServerHttpRequest.Builder builder = request.mutate();
        Map<String, List<String>> headers = request.getHeaders();
        
        // 处理每个超大的头部
        for (Map.Entry<String, Integer> entry : LARGE_HEADER_LIMITS.entrySet()) {
            final String headerName = entry.getKey();
            final int maxLength = entry.getValue();
            
            if (headers.containsKey(headerName)) {
                List<String> values = headers.get(headerName);
                for (String value : values) {
                    if (value.length() > maxLength) {
                        // 截断大型头部值并添加指示符
                        final String truncatedValue = value.substring(0, maxLength) + "...";
                        builder.headers(httpHeaders -> httpHeaders.set(headerName, truncatedValue));
                    }
                }
            }
        }
        
        // 完全移除一些不必要的头部
        final String[] unnecessaryHeaders = {
            "upgrade-insecure-requests", 
            "cache-control",
            "sec-fetch-site",
            "sec-fetch-mode",
            "sec-fetch-dest",
            "sec-ch-ua",
            "sec-ch-ua-mobile",
            "sec-ch-ua-platform"
        };
        
        for (final String header : unnecessaryHeaders) {
            if (headers.containsKey(header)) {
                builder.headers(httpHeaders -> httpHeaders.remove(header));
            }
        }
        
        return builder.build();
    }

    /**
     * 计算所有请求头大小
     */
    private int calculateHeadersSize(Map<String, List<String>> headers) {
        int size = 0;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            size += entry.getKey().length();
            for (String value : entry.getValue()) {
                size += value.length();
            }
        }
        return size;
    }
    
    /**
     * 记录重要的请求头信息（仅DEBUG级别）
     */
    private void logImportantHeaders(Map<String, List<String>> headers) {
        // 只记录关键头部
        final String[] importantHeaders = {"content-type", "accept", "cookie", "authorization"};
        
        for (final String header : importantHeaders) {
            if (headers.containsKey(header)) {
                final List<String> values = headers.get(header);
                // 记录头部信息，同时限制日志大小
                String valueStr = values.isEmpty() ? "" : values.get(0);
                if (valueStr.length() > 100) {
                    valueStr = valueStr.substring(0, 100) + "...";
                }
                log.debug("请求头 {}: 大小 {}字节, 值: {}", 
                        header, 
                        header.length() + calculateValuesSize(values), 
                        valueStr);
            }
        }
    }
    
    private int calculateValuesSize(List<String> values) {
        int size = 0;
        for (String value : values) {
            size += value.length();
        }
        return size;
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
    
    /**
     * 移除不必要的请求头，特别是针对Swagger请求
     */
    private ServerHttpRequest removeUnnecessaryHeaders(ServerHttpRequest request) {
        return request.mutate()
                .headers(headers -> {
                    // 移除可能导致请求头过大的头信息
                    headers.remove("cookie");
                    headers.remove("referer");
                    headers.remove("user-agent");
                    headers.remove("accept-language");
                    headers.remove("accept-encoding");
                    headers.remove("cache-control");
                    // 保留必要的头信息，如content-type等
                })
                .build();
    }

    @Override
    public int getOrder() {
        // 设置为高优先级，但不是最高，这样可以让其他可能的安全过滤器先执行
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
} 