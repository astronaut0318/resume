package com.ptu.gateway.swagger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 网关服务Swagger资源提供者 - 基于Knife4j
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewaySwaggerProvider {
    
    /**
     * API文档版本
     */
    @Value("${knife4j.gateway.api-docs-version:2.0}")
    private String apiDocsVersion;

    /**
     * API文档路径
     */
    @Value("${knife4j.gateway.api-docs-path:/v2/api-docs}")
    private String apiDocsPath;

    /**
     * 是否支持v3 API文档
     */
    @Value("${knife4j.gateway.support-v3:true}")
    private boolean supportV3;
    
    /**
     * 从Knife4j配置中读取服务列表
     */
    @Value("${knife4j.gateway.routes[0].name:用户服务}")
    private String defaultServiceName;
    
    /**
     * 微服务列表
     */
    private static final String[] SERVICES = {
        "user", "auth", "resume", "template", "file", "order", "ai", "notification"
    };
    
    /**
     * 微服务显示名称
     */
    private static final String[] SERVICE_NAMES = {
        "用户服务", "认证服务", "简历服务", "模板服务", "文件服务", "订单服务", "AI服务", "通知服务"
    };

    /**
     * 获取Swagger资源列表
     */
    public List<Map<String, String>> getSwaggerResources() {
        List<Map<String, String>> resources = new ArrayList<>();
        
        // 添加服务的v2版本API文档
        for (int i = 0; i < SERVICES.length; i++) {
            resources.add(createResource(SERVICE_NAMES[i], "/" + SERVICES[i] + apiDocsPath));
        }
        
        // 如果支持v3，添加v3版本API文档
        if (supportV3) {
            String v3Path = apiDocsPath.replace("/v2/", "/v3/");
            for (int i = 0; i < SERVICES.length; i++) {
                resources.add(createResource(SERVICE_NAMES[i] + "(OpenAPI3)", "/" + SERVICES[i] + v3Path));
            }
        }
        
        log.debug("Knife4j Swagger资源列表: {}", resources);
        return resources;
    }
    
    private Map<String, String> createResource(String name, String location) {
        Map<String, String> resource = new HashMap<>();
        resource.put("name", name);
        resource.put("location", location);
        resource.put("version", apiDocsVersion);
        return resource;
    }
} 