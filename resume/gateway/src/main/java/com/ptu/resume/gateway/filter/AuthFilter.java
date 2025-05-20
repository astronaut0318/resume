package com.ptu.resume.gateway.filter;

import com.ptu.resume.gateway.config.SecurityConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 * 
 * @author PTU
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private SecurityConfig securityConfig;
    
    /**
     * 过滤器执行逻辑
     * 
     * @param exchange 服务网络交换机
     * @param chain 网关过滤器链
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        // 检查URI是否在白名单中
        String url = request.getURI().getPath();
        if (isWhiteListUrl(url)) {
            return chain.filter(exchange);
        }
        
        // 获取并验证Token
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            return unauthorizedResponse(response, "令牌不能为空");
        }
        
        try {
            Claims claims = parseToken(token);
            // 将用户信息传递到下游服务
            ServerHttpRequest newRequest = request.mutate()
                    .header("userId", claims.getSubject())
                    .build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        } catch (Exception e) {
            return unauthorizedResponse(response, "令牌验证失败");
        }
    }
    
    /**
     * 设置过滤器优先级
     * 
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return 0;
    }
    
    /**
     * 解析JWT令牌
     * 
     * @param token JWT令牌
     * @return 声明信息
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(securityConfig.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 获取请求Header中的Token
     * 
     * @param request HTTP请求
     * @return Token字符串
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotEmpty(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
    
    /**
     * 检查URL是否在白名单中
     * 
     * @param url 请求URL
     * @return 是否在白名单中
     */
    private boolean isWhiteListUrl(String url) {
        // 添加特定端点白名单检查
        if (url.endsWith("/health") || url.contains("/actuator/")) {
            return true;
        }
        
        List<String> whiteList = securityConfig.getIgnore().getWhites();
        return whiteList.stream().anyMatch(pattern -> 
                matchPath(url, pattern));
    }
    
    /**
     * 路径匹配
     * 
     * @param url 请求URL
     * @param pattern 匹配模式
     * @return 是否匹配
     */
    private boolean matchPath(String url, String pattern) {
        if (pattern.contains("*")) {
            String patternPrefix = pattern.substring(0, pattern.indexOf("*"));
            return url.startsWith(patternPrefix);
        }
        return url.equals(pattern);
    }
    
    /**
     * 返回未授权响应
     * 
     * @param response HTTP响应
     * @param msg 错误消息
     * @return Mono<Void>
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String msg) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"msg\":\"" + msg + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Flux.just(buffer));
    }
} 