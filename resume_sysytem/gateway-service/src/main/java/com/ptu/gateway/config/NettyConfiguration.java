package com.ptu.gateway.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.server.HttpServer;

/**
 * Netty服务器配置
 * 用于解决请求头太大导致的431错误
 */
@Configuration
public class NettyConfiguration {

    /**
     * 自定义Netty服务器配置
     * 增加HTTP头部大小限制
     */
    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.addServerCustomizers(new CustomNettyServerCustomizer());
    }

    private static class CustomNettyServerCustomizer implements NettyServerCustomizer {
        @Override
        public HttpServer apply(HttpServer httpServer) {
            return httpServer
                    .httpRequestDecoder(spec -> 
                        // 设置最大头部大小为64KB
                        spec.maxHeaderSize(65536)
                            // 最大初始行长度
                            .maxInitialLineLength(10000)
                            // 验证头部大小
                            .validateHeaders(true)
                    );
        }
    }
} 