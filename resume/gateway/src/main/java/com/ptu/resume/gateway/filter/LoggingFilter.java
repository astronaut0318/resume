package com.ptu.resume.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志记录过滤器
 *
 * @author PTU
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

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
        String path = request.getPath().toString();
        String method = request.getMethod().name();
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        log.info("[Gateway] Request: {} {}", method, path);

        // 装饰响应以记录响应体
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        stopWatch.stop();
                        // 合并所有DataBuffer
                        List<byte[]> bytesList = new ArrayList<>();
                        dataBuffers.forEach(buffer -> {
                            byte[] content = new byte[buffer.readableByteCount()];
                            buffer.read(content);
                            DataBufferUtils.release(buffer);
                            bytesList.add(content);
                        });
                        
                        // 计算响应总大小
                        int size = bytesList.stream().mapToInt(bytes -> bytes.length).sum();
                        
                        log.info("[Gateway] Response: {} {} - Status: {} - Time: {}ms - Size: {} bytes", 
                                method, path, originalResponse.getStatusCode(), 
                                stopWatch.getTotalTimeMillis(), size);
                        
                        // 重新构建一个DataBuffer
                        byte[] allBytes = new byte[size];
                        int pos = 0;
                        for (byte[] bytes : bytesList) {
                            System.arraycopy(bytes, 0, allBytes, pos, bytes.length);
                            pos += bytes.length;
                        }
                        
                        return bufferFactory.wrap(allBytes);
                    }));
                }
                return super.writeWith(body);
            }
        };
        
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /**
     * 过滤器优先级
     * 
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return -1; // 在认证过滤器之前执行
    }
} 