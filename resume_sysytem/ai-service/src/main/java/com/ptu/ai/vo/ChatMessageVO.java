package com.ptu.ai.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI聊天消息响应实体
 */
@Data
public class ChatMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：user-用户消息，ai-AI回复
     */
    private String messageType;

    /**
     * 消息时间
     */
    private LocalDateTime createTime;
} 