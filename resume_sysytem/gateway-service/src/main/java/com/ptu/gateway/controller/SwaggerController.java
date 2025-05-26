package com.ptu.gateway.controller;

import com.ptu.gateway.swagger.GatewaySwaggerProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger控制器
 * 提供Swagger UI资源访问接口
 */
@RestController
@RequiredArgsConstructor
public class SwaggerController {

    private final GatewaySwaggerProvider swaggerResourcesProvider;

    /**
     * 提供Swagger安全配置
     */
    @GetMapping("/swagger-resources/configuration/security")
    public ResponseEntity<Map<String, Object>> securityConfiguration() {
        Map<String, Object> securityConfig = new HashMap<>();
        securityConfig.put("apiKey", "");
        securityConfig.put("apiKeyName", "Authorization");
        securityConfig.put("apiKeyVehicle", "header");
        return new ResponseEntity<>(securityConfig, HttpStatus.OK);
    }

    /**
     * 提供Swagger UI配置
     */
    @GetMapping("/swagger-resources/configuration/ui")
    public ResponseEntity<Map<String, Object>> uiConfiguration() {
        Map<String, Object> uiConfig = new HashMap<>();
        uiConfig.put("deepLinking", true);
        uiConfig.put("displayOperationId", false);
        uiConfig.put("defaultModelsExpandDepth", 1);
        uiConfig.put("defaultModelExpandDepth", 1);
        uiConfig.put("defaultModelRendering", "example");
        uiConfig.put("displayRequestDuration", false);
        uiConfig.put("docExpansion", "none");
        uiConfig.put("showExtensions", false);
        return new ResponseEntity<>(uiConfig, HttpStatus.OK);
    }

    /**
     * 提供Swagger资源列表
     */
    @GetMapping("/swagger-resources")
    public ResponseEntity<List<Map<String, String>>> swaggerResources() {
        return new ResponseEntity<>(swaggerResourcesProvider.getSwaggerResources(), HttpStatus.OK);
    }
} 