package com.ptu.order.feign;

import com.ptu.template.vo.TemplateVO;
import com.ptu.common.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Component;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 模板服务Feign客户端
 */
@FeignClient(name = "template-service", fallbackFactory = TemplateFeignClient.TemplateFeignFallbackFactory.class)
public interface TemplateFeignClient {

    /**
     * 根据模板ID获取模板详情
     * @param templateId 模板ID
     * @return 模板VO
     */
    @GetMapping("/api/templates/{templateId}")
    R<TemplateVO> getTemplateById(@PathVariable("templateId") Long templateId);

    /**
     * 降级工厂
     */
    @Component
    class TemplateFeignFallbackFactory implements FallbackFactory<TemplateFeignClient> {
        @Override
        public TemplateFeignClient create(Throwable cause) {
            return new TemplateFeignClient() {
                @Override
                public R<TemplateVO> getTemplateById(Long templateId) {
                    return R.failed("模板服务不可用");
                }
            };
        }
    }
} 