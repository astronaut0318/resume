package com.ptu.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptu.gateway.config.GatewayConfig;
import com.ptu.gateway.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final GatewayConfig gatewayConfig;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 直接放行doc.html和swagger相关资源
        if (path.endsWith("/doc.html") || 
            path.endsWith("/swagger-ui.html") || 
            path.contains("/swagger-resources") || 
            path.contains("/webjars/") || 
            path.contains("/v2/api-docs") || 
            path.contains("/v3/api-docs")) {
            // 降低日志级别，避免日志爆炸
            if(log.isDebugEnabled()) {
                log.debug("直接放行Swagger资源: {}", path);
            }
            return chain.filter(exchange);
        }
        
        // 1. 白名单路径直接放行
        List<String> ignoreUrls = gatewayConfig.getIgnoreUrls();
        
        // 输出路径匹配过程，便于调试
        log.debug("当前请求路径: {}", path);
        if (ignoreUrls != null) {
            for (String ignoreUrl : ignoreUrls) {
                if (pathMatcher.match(ignoreUrl, path)) {
                    log.debug("路径匹配白名单: {} 匹配 {}, 直接放行", ignoreUrl, path);
                    return chain.filter(exchange);
                }
            }
        }
        
        // 2. 获取token
        String token = getToken(request);
        if (StrUtil.isBlank(token)) {
            return unauthorized(exchange, "未授权，请先登录");
        }
        
        // 3. 验证token
        if (!jwtUtils.validateToken(token)) {
            return unauthorized(exchange, "Token无效或已过期");
        }
        
        // 4. 获取用户信息
        String username = jwtUtils.getUsernameFromToken(token);
        Long userId = jwtUtils.getUserIdFromToken(token);
        if (username == null || userId == null) {
            return unauthorized(exchange, "Token中不包含有效的用户信息");
        }
        
        // 5. 检查Redis中是否存在该token（退出登录会删除Redis中的token）
        String redisTokenKey = "token:" + userId;
        Object redisToken = redisTemplate.opsForValue().get(redisTokenKey);
        if (redisToken == null || !token.equals(redisToken.toString())) {
            return unauthorized(exchange, "Token已失效，请重新登录");
        }
        
        // 6. 延长token在Redis中的过期时间
        redisTemplate.expire(redisTokenKey, 24, TimeUnit.HOURS);
        
        // 7. 将用户信息传递到下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Username", username)
                .build();
        
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    /**
     * 获取请求头中的token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        
        // 如果以Bearer开头，去除Bearer
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        return token;
    }

    /**
     * 返回未授权的响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", HttpStatus.UNAUTHORIZED.value());
        result.put("message", message);
        result.put("data", null);
        
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("返回未授权响应失败: {}", e.getMessage());
            
            byte[] bytes = ("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}").getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        }
    }
} 