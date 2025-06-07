package com.ptu.document.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Actions字段反序列化器
 * 兼容OnlyOffice回调中actions字段的字符串和数组两种格式
 */
@Slf4j
public class ActionsDeserializer extends JsonDeserializer<List<CallbackData.Action>> {

    @Override
    public List<CallbackData.Action> deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        
        List<CallbackData.Action> result = new ArrayList<>();
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            
            // 处理数组格式
            if (node.isArray()) {
                for (JsonNode item : node) {
                    if (item.isObject()) {
                        CallbackData.Action action = mapper.treeToValue(item, CallbackData.Action.class);
                        result.add(action);
                    }
                }
            } 
            // 处理字符串格式 - 如果是JSON字符串，尝试解析为数组
            else if (node.isTextual()) {
                String text = node.asText();
                try {
                    JsonNode arrayNode = mapper.readTree(text);
                    if (arrayNode.isArray()) {
                        for (JsonNode item : arrayNode) {
                            if (item.isObject()) {
                                CallbackData.Action action = mapper.treeToValue(item, CallbackData.Action.class);
                                result.add(action);
                            }
                        }
                    }
                } catch (Exception e) {
                    // 如果解析失败，忽略错误，返回空列表
                    log.warn("无法将文本解析为actions数组: {}", text);
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("解析actions字段失败", e);
            return result;
        }
    }
} 