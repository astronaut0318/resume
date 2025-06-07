package com.ptu.document.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Users字段反序列化器
 * 兼容OnlyOffice回调中users字段的字符串和数组两种格式
 */
@Slf4j
public class UsersDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        
        List<String> result = new ArrayList<>();
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            
            // 处理数组格式
            if (node.isArray()) {
                for (JsonNode item : node) {
                    if (item.isTextual()) {
                        result.add(item.asText());
                    } else if (item.has("id")) {
                        result.add(item.get("id").asText());
                    }
                }
            } 
            // 处理字符串格式
            else if (node.isTextual()) {
                result.add(node.asText());
            }
            
            return result;
        } catch (Exception e) {
            log.error("解析users字段失败", e);
            return result;
        }
    }
} 