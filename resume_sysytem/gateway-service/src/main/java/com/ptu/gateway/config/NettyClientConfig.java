package com.ptu.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * 自定义Netty客户端配置
 * 用于设置HTTP头大小限制和超时时间
 */
@Configuration
public class NettyClientConfig {

    /**
     * 自定义WebClient配置，配置较大的请求头大小限制
     */
    @Bean
    @Primary
    public WebClient webClient() {
        // 使用自定义HTTP客户端
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient());
        return WebClient.builder()
                .clientConnector(connector)
                .build();
    }

    /**
     * 自定义HTTP客户端配置
     * 设置较大的头部大小限制和更长的超时时间
     */
    @Bean
    public HttpClient httpClient() {
        // 创建连接提供者，增加最大连接数和超时时间
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(1000)      // 提高最大连接数
                .maxIdleTime(Duration.ofSeconds(60))  // 增加空闲时间
                .maxLifeTime(Duration.ofMinutes(10))  // 增加最大生命周期
                .pendingAcquireTimeout(Duration.ofSeconds(30))  // 提高等待超时
                .build();

        // 使用自定义连接提供者创建HTTP客户端
        return HttpClient.create(provider)
                // 响应超时设置为30秒，原来是10秒
                .responseTimeout(Duration.ofSeconds(30))
                // 设置请求解码器
                .httpResponseDecoder(decoder -> {
                    // 最大头部大小设置为64KB
                    decoder.maxHeaderSize(65536);
                    // 最大初始行大小
                    decoder.maxInitialLineLength(10000);
                    // 验证头部是否有效
                    decoder.validateHeaders(true);
                    return decoder;
                });
    }
    
    /**
     * 自定义HTTP客户端属性配置
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.cloud.gateway.httpclient")
    public HttpClientProperties httpClientProperties() {
        return new HttpClientProperties();
    }
} 