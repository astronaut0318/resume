package com.ptu.ai.service.impl;

import com.ptu.ai.dto.ChatMessageRequestDTO;
import com.ptu.ai.service.ChatService;
import com.ptu.ai.vo.ChatMessageVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI聊天服务实现
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Value("${ai.deepseek.api-url}")
    private String deepseekApiUrl;

    @Value("${ai.deepseek.api-key}")
    private String deepseekApiKey;

    @Resource
    private RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 简单缓存用户的会话历史（生产环境应使用Redis）
    private final Map<Long, List<ChatMessageVO>> userSessionHistory = new ConcurrentHashMap<>();
    private final Map<String, List<Map<String, String>>> sessionMessages = new ConcurrentHashMap<>();

    @Override
    public ChatMessageVO sendMessage(ChatMessageRequestDTO requestDTO, Long userId) {
        // 获取或创建会话ID
        String sessionId = requestDTO.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        // 创建用户消息
        ChatMessageVO userMessage = new ChatMessageVO();
        userMessage.setMessageId(UUID.randomUUID().toString());
        userMessage.setSessionId(sessionId);
        userMessage.setContent(requestDTO.getContent());
        userMessage.setMessageType("user");
        userMessage.setCreateTime(LocalDateTime.now());

        // 保存用户消息到历史记录
        saveMessageToHistory(userId, userMessage);

        // 准备与DeepSeek API交互的消息列表
        List<Map<String, String>> messages = sessionMessages.computeIfAbsent(sessionId, k -> new ArrayList<>());
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", requestDTO.getContent());
        messages.add(userMsg);

        // 调用大模型API获取回复
        ChatMessageVO aiResponse = callDeepSeekApi(sessionId, messages);
        aiResponse.setCreateTime(LocalDateTime.now());

        // 保存AI回复到历史记录
        saveMessageToHistory(userId, aiResponse);

        // 更新会话消息记录
        Map<String, String> assistantMsg = new HashMap<>();
        assistantMsg.put("role", "assistant");
        assistantMsg.put("content", aiResponse.getContent());
        messages.add(assistantMsg);

        return aiResponse;
    }
    
    @Override
    public void sendMessageStream(ChatMessageRequestDTO requestDTO, Long userId, SseEmitter emitter) {
        try {
            // 获取或创建会话ID
            String sessionId = requestDTO.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }
            
            final String finalSessionId = sessionId;

            // 创建用户消息
            ChatMessageVO userMessage = new ChatMessageVO();
            userMessage.setMessageId(UUID.randomUUID().toString());
            userMessage.setSessionId(sessionId);
            userMessage.setContent(requestDTO.getContent());
            userMessage.setMessageType("user");
            userMessage.setCreateTime(LocalDateTime.now());

            // 保存用户消息到历史记录
            saveMessageToHistory(userId, userMessage);

            // 准备与DeepSeek API交互的消息列表
            List<Map<String, String>> messages = sessionMessages.computeIfAbsent(sessionId, k -> new ArrayList<>());
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", requestDTO.getContent());
            messages.add(userMsg);
            
            // 创建AI消息对象
            String aiMessageId = UUID.randomUUID().toString();
            ChatMessageVO aiMessage = new ChatMessageVO();
            aiMessage.setMessageId(aiMessageId);
            aiMessage.setSessionId(sessionId);
            aiMessage.setContent("");
            aiMessage.setMessageType("ai");
            aiMessage.setCreateTime(LocalDateTime.now());
            
            // 模拟流式传输（实际环境中应连接支持流式响应的API）
            simulateStreamResponse(requestDTO.getContent(), aiMessage, emitter, messages);
            
            // 保存完整AI回复到历史记录
            saveMessageToHistory(userId, aiMessage);
            
        } catch (Exception e) {
            try {
                // 发送错误消息
                ChatMessageVO errorMessage = new ChatMessageVO();
                errorMessage.setMessageId(UUID.randomUUID().toString());
                errorMessage.setSessionId(requestDTO.getSessionId());
                errorMessage.setContent("抱歉，服务暂时不可用，请稍后再试。");
                errorMessage.setMessageType("ai");
                errorMessage.setCreateTime(LocalDateTime.now());
                
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(errorMessage));
                
                // 完成发送
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
    }

    @Override
    public List<ChatMessageVO> getHistoryMessages(Long userId) {
        return userSessionHistory.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public void clearHistory(Long userId) {
        userSessionHistory.remove(userId);
        // 如果需要清除具体会话，这里还需要根据userId查找对应的sessionId并清除
    }

    /**
     * 保存消息到历史记录
     */
    private void saveMessageToHistory(Long userId, ChatMessageVO message) {
        List<ChatMessageVO> history = userSessionHistory.computeIfAbsent(userId, k -> new ArrayList<>());
        history.add(message);
    }

    /**
     * 调用DeepSeek API获取AI回复
     */
    private ChatMessageVO callDeepSeekApi(String sessionId, List<Map<String, String>> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + deepseekApiKey);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("top_p", 0.95);
        requestBody.put("max_tokens", 1000);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    deepseekApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // 解析响应
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                Map<String, Object> choices = ((List<Map<String, Object>>) responseBody.get("choices")).get(0);
                Map<String, Object> message = (Map<String, Object>) choices.get("message");
                String content = (String) message.get("content");

                ChatMessageVO aiMessage = new ChatMessageVO();
                aiMessage.setMessageId(UUID.randomUUID().toString());
                aiMessage.setSessionId(sessionId);
                aiMessage.setContent(content);
                aiMessage.setMessageType("ai");
                
                return aiMessage;
            }
        } catch (Exception e) {
            // 异常处理，返回一个友好的错误消息
            ChatMessageVO errorMessage = new ChatMessageVO();
            errorMessage.setMessageId(UUID.randomUUID().toString());
            errorMessage.setSessionId(sessionId);
            errorMessage.setContent("抱歉，AI服务暂时不可用，请稍后再试。");
            errorMessage.setMessageType("ai");
            
            return errorMessage;
        }

        // 默认错误响应
        ChatMessageVO defaultMessage = new ChatMessageVO();
        defaultMessage.setMessageId(UUID.randomUUID().toString());
        defaultMessage.setSessionId(sessionId);
        defaultMessage.setContent("抱歉，无法处理您的请求。");
        defaultMessage.setMessageType("ai");
        
        return defaultMessage;
    }
    
    /**
     * 模拟流式响应
     * 在生产环境中，应该连接支持流式API的服务
     */
    private void simulateStreamResponse(String userInput, ChatMessageVO aiMessage, SseEmitter emitter, List<Map<String, String>> messages) {
        // 从常规API获取完整响应
        ChatMessageVO fullResponse = callDeepSeekApi(aiMessage.getSessionId(), messages);
        String fullContent = fullResponse.getContent();
        
        // 更新AI消息的内容
        aiMessage.setContent(fullContent);
        
        // 将完整内容分割成小块进行模拟流式传输
        try {
            StringBuilder currentContent = new StringBuilder();
            String[] words = fullContent.split(" ");
            
            for (int i = 0; i < words.length; i++) {
                // 休眠一小段时间模拟流式传输延迟
                Thread.sleep(50);
                
                // 添加下一个单词
                if (i > 0) {
                    currentContent.append(" ");
                }
                currentContent.append(words[i]);
                
                // 创建当前块的消息对象
                ChatMessageVO chunkMessage = new ChatMessageVO();
                chunkMessage.setMessageId(aiMessage.getMessageId());
                chunkMessage.setSessionId(aiMessage.getSessionId());
                chunkMessage.setContent(currentContent.toString());
                chunkMessage.setMessageType("ai");
                chunkMessage.setCreateTime(aiMessage.getCreateTime());
                
                // 发送事件
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(chunkMessage));
            }
            
            // 发送完成事件
            emitter.send(SseEmitter.event()
                    .name("complete")
                    .data(aiMessage));
            
            // 更新会话消息记录
            Map<String, String> assistantMsg = new HashMap<>();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", fullContent);
            messages.add(assistantMsg);
            
            // 完成发送
            emitter.complete();
        } catch (Exception e) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("流式传输过程中出错: " + e.getMessage()));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
    }
} 