package com.ptu.gateway.controller;

import com.ptu.gateway.swagger.GatewaySwaggerProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger.web.*;

import java.util.List;

/**
 * Swagger控制器
 * 提供Swagger UI资源访问接口
 */
@RestController
@RequiredArgsConstructor
public class SwaggerController {

    private final GatewaySwaggerProvider swaggerResourcesProvider;

    @GetMapping("/swagger-resources/configuration/security")
    public ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return new ResponseEntity<>(SecurityConfigurationBuilder.builder().build(), HttpStatus.OK);
    }

    @GetMapping("/swagger-resources/configuration/ui")
    public ResponseEntity<UiConfiguration> uiConfiguration() {
        return new ResponseEntity<>(UiConfigurationBuilder.builder().build(), HttpStatus.OK);
    }

    @GetMapping("/swagger-resources")
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<>(swaggerResourcesProvider.get(), HttpStatus.OK);
    }
} 