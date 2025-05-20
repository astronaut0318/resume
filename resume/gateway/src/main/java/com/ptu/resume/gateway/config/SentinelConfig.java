package com.ptu.resume.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Sentinel限流配置
 *
 * @author PTU
 */
@Configuration
public class SentinelConfig {

    @PostConstruct
    public void init() {
        initGatewayRules();
        initBlockHandler();
    }

    /**
     * 初始化限流规则
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        
        // 用户服务API组的限流规则
        rules.add(new GatewayFlowRule("user-api")
                .setCount(100)  // QPS阈值
                .setIntervalSec(1)  // 统计时间窗口，单位是秒
        );
        
        // 简历服务API组的限流规则
        rules.add(new GatewayFlowRule("resume-api")
                .setCount(50)
                .setIntervalSec(1)
        );
        
        // 认证服务API组的限流规则
        rules.add(new GatewayFlowRule("auth-api")
                .setCount(200)
                .setIntervalSec(1)
        );
        
        // 加载限流规则
        GatewayRuleManager.loadRules(rules);
        
        // 定义API分组
        initApiDefinitions();
    }

    /**
     * 定义API分组
     */
    private void initApiDefinitions() {
        Set<ApiDefinition> definitions = new HashSet<>();
        
        // 用户服务API定义
        ApiDefinition userApi = new ApiDefinition("user-api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/user/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        definitions.add(userApi);
        
        // 简历服务API定义
        ApiDefinition resumeApi = new ApiDefinition("resume-api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/resume/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        definitions.add(resumeApi);
        
        // 认证服务API定义
        ApiDefinition authApi = new ApiDefinition("auth-api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    add(new ApiPathPredicateItem().setPattern("/auth/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        definitions.add(authApi);
        
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    /**
     * 自定义限流响应
     */
    private void initBlockHandler() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 429);
            response.put("msg", "请求过于频繁，请稍后重试");
            
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response);
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
} 