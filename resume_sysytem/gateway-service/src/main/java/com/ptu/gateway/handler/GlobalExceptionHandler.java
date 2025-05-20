package com.ptu.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 网关全局异常处理器
 */
@Slf4j
@Order(-1)
@Configuration
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        
        // 设置响应的Content-Type
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 根据异常类型设置不同的状态码
        if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            return writeResponse(response, HttpStatus.SERVICE_UNAVAILABLE.value(), "服务不可用，请稍后重试");
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            response.setStatusCode(responseStatusException.getStatus());
            return writeResponse(response, responseStatusException.getStatus().value(), responseStatusException.getMessage());
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return writeResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        }
    }

    /**
     * 写入响应信息
     */
    private Mono<Void> writeResponse(ServerHttpResponse response, int code, String message) {
        return Mono.defer(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            
            // 构建响应结果
            Map<String, Object> result = new HashMap<>(3);
            result.put("code", code);
            result.put("message", message);
            result.put("success", false);
            
            // 将结果转换为JSON
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(result);
                return response.writeWith(Mono.just(bufferFactory.wrap(bytes)));
            } catch (JsonProcessingException e) {
                log.error("响应结果转换JSON失败: {}", e.getMessage());
                return Mono.error(e);
            }
        });
    }
} 