package com.ptu.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 全局Jackson配置类
 * 统一处理Java8时间类型序列化格式，解决LocalDateTime等类型返回前端报错问题
 * 时间格式：yyyy-MM-dd HH:mm:ss
 */
@Configuration
public class JacksonConfig implements WebMvcConfigurer {

    /**
     * 统一的时间格式
     */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 配置全局ObjectMapper，注册JavaTimeModule并指定时间格式
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册Java8时间模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 指定LocalDateTime序列化格式
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
        // 指定LocalDateTime反序列化格式
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_PATTERN)));
        objectMapper.registerModule(javaTimeModule);
        // 禁用时间戳输出
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    /**
     * 配置消息转换器，使用自定义ObjectMapper
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).setObjectMapper(jacksonObjectMapper());
            }
        }
    }
} 