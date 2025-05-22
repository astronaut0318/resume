package com.ptu.template.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;


import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 解决Springfox 3.0.0与Spring Boot 2.7.x之间的兼容性问题
 * 修复java.lang.NullPointerException at springfox.documentation.spring.web.WebMvcPatternsRequestConditionWrapper.getPatterns
 */
@Component
public class SpringfoxHandlerProviderBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
            customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
        }
        return bean;
    }

    private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
        List<T> copy = Optional.ofNullable(mappings)
                .orElse(Collections.emptyList())
                .stream()
                .filter(mapping -> mapping.getPatternParser() == null)
                .collect(Collectors.toList());
        
        if (mappings != null) {
            mappings.clear();
            mappings.addAll(copy);
        }
    }

    @SuppressWarnings("unchecked")
    private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
        try {
            Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
            if (field == null) {
                return Collections.emptyList();
            }
            field.setAccessible(true);
            return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            return Collections.emptyList();
        }
    }
}