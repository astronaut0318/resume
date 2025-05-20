package com.ptu.resume.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理
 *
 * @author PTU
 */
@Slf4j
@Order(-1)
@Configuration
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * 处理异常
     *
     * @param exchange 服务网络交换机
     * @param ex 异常
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        
        // 设置响应的Content-Type
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 根据不同异常类型，返回不同的状态码
        if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            return responseError(response, "服务未找到");
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            response.setStatusCode(responseStatusException.getStatus());
            return responseError(response, responseStatusException.getMessage());
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return responseError(response, "网关异常：" + ex.getMessage());
        }
    }
    
    /**
     * 返回错误响应
     *
     * @param response HTTP响应
     * @param errorMessage 错误信息
     * @return Mono<Void>
     */
    private Mono<Void> responseError(ServerHttpResponse response, String errorMessage) {
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        HttpStatus status = response.getStatusCode();
        String errorJson = String.format("{\"code\":%d,\"msg\":\"%s\"}", 
                status != null ? status.value() : HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                errorMessage);
        log.error("[Gateway Error] {}", errorJson);
        return response.writeWith(
                Mono.just(dataBufferFactory.wrap(errorJson.getBytes())));
    }
} 