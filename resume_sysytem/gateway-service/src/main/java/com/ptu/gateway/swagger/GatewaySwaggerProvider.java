package com.ptu.gateway.swagger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关服务Swagger资源提供者
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class GatewaySwaggerProvider implements SwaggerResourcesProvider {

    private final RouteLocator routeLocator;
    
    /**
     * API文档版本
     */
    @Value("${swagger.api-docs-version:v2}")
    private String apiDocsVersion;

    /**
     * API文档路径
     */
    @Value("${swagger.api-docs-path:/api-docs}")
    private String apiDocsPath;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        
        // 手动添加各服务的Swagger资源
        resources.add(createResource("Gateway API", "/v2/api-docs"));
        resources.add(createResource("用户服务", "/user/v2/api-docs"));
        resources.add(createResource("认证服务", "/auth/v2/api-docs"));
        resources.add(createResource("简历服务", "/resume/v2/api-docs"));
        resources.add(createResource("模板服务", "/template/v2/api-docs"));
        resources.add(createResource("文件服务", "/file/v2/api-docs"));
        resources.add(createResource("订单服务", "/order/v2/api-docs"));
        resources.add(createResource("AI服务", "/ai/v2/api-docs"));
        resources.add(createResource("通知服务", "/notification/v2/api-docs"));
        
        log.debug("Swagger资源列表: {}", resources);
        return resources;
    }
    
    private SwaggerResource createResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
} 