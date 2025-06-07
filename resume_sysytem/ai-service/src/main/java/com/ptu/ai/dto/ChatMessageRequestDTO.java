package com.ptu.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * AI聊天消息请求实体
 */
@Data
public class ChatMessageRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 会话ID，首次对话为null，后续对话需要传入
     */
    private String sessionId;
} 