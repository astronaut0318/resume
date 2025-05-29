package com.ptu.gateway.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 网关配置类，支持白名单路径自动注入
 */
@Configuration
@ConfigurationProperties(prefix = "gateway.security")
public class GatewayConfig {
    /**
     * 网关安全白名单（无需认证的接口），自动从application.yml注入
     */
    private List<String> ignoreUrls;

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }

    /**
     * 限流的key解析器，使用请求IP作为限流键
     */
    @Bean
    public org.springframework.cloud.gateway.filter.ratelimit.KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * 自定义Netty服务器工厂，增加请求头大小限制和超时设置
     */
    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            factory.addServerCustomizers(httpServer -> {
                return httpServer
                        .httpRequestDecoder(decoder -> {
                            // 设置最大头部大小为64KB
                            decoder.maxHeaderSize(65536);
                            // 设置初始行最大长度
                            decoder.maxInitialLineLength(10000);
                            return decoder;
                        })
                        .doOnConnection(connection -> {
                            // 添加读超时处理器，30秒
                            connection.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS));
                            // 添加写超时处理器，30秒
                            connection.addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS));
                        })
                        .wiretap(true); // 启用线路调试，便于排查问题
            });
        };
    }
    
    /**
     * 处理跨域请求的过滤器
     */
    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                HttpHeaders requestHeaders = request.getHeaders();
                ServerHttpResponse response = exchange.getResponse();
                org.springframework.http.HttpHeaders headers = response.getHeaders();
                headers.add("Access-Control-Allow-Origin", requestHeaders.getOrigin());
                headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                headers.add("Access-Control-Allow-Credentials", "true");
                headers.add("Access-Control-Max-Age", "3600");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            
            return chain.filter(exchange);
        };
    }
} 