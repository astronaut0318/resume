package com.ptu.ai.service;

import com.ptu.ai.dto.ChatMessageRequestDTO;
import com.ptu.ai.vo.ChatMessageVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * AI聊天服务接口
 */
public interface ChatService {

    /**
     * 发送消息并获取AI回复
     *
     * @param dto 消息请求
     * @param userId 用户ID
     * @return AI回复消息
     */
    ChatMessageVO sendMessage(ChatMessageRequestDTO dto, Long userId);

    /**
     * 以流式方式发送消息并获取AI回复
     *
     * @param dto 消息请求
     * @param userId 用户ID
     * @param emitter SSE事件发射器
     */
    void sendMessageStream(ChatMessageRequestDTO dto, Long userId, SseEmitter emitter);

    /**
     * 获取历史消息
     *
     * @param userId 用户ID
     * @return 历史消息列表
     */
    List<ChatMessageVO> getHistoryMessages(Long userId);

    /**
     * 清空历史消息
     *
     * @param userId 用户ID
     */
    void clearHistory(Long userId);
} 