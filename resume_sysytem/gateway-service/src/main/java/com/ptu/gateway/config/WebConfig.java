package com.ptu.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Web配置类
 * 处理静态资源和请求处理
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    /**
     * 配置HTTP消息编解码器 - 移除此方法，避免与Spring Boot自动配置冲突
     */
    /*
    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return ServerCodecConfigurer.create();
    }
    */

    /**
     * 配置静态资源处理器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI资源
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
        
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
        
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(false);
        
        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(false);
                
        // Knife4j资源
        registry.addResourceHandler("/webjars/css/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
        
        registry.addResourceHandler("/webjars/js/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
        
        registry.addResourceHandler("/webjars/img/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
    }
    
    /**
     * 添加CORS支持过滤器
     * 这是对application.yml中全局CORS配置的补充
     */
    // @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // 如果是OPTIONS请求，直接放行，由网关的globalcors配置处理
            if (request.getMethod() == HttpMethod.OPTIONS) {
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders headers = response.getHeaders();
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "authorization, content-type");
                headers.add("Access-Control-Max-Age", "3600");
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
            
            return chain.filter(exchange);
        };
    }
} 