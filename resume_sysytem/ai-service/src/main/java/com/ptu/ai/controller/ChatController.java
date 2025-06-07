package com.ptu.ai.controller;

import com.ptu.ai.dto.ChatMessageRequestDTO;
import com.ptu.ai.service.ChatService;
import com.ptu.ai.vo.ChatMessageVO;
import com.ptu.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI聊天接口控制器
 * 用于处理浮动对话框的AI对话请求
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    // 线程池，用于处理SSE事件
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 发送消息给AI并获取回复
     */
    @PostMapping("/send")
    public R<ChatMessageVO> sendMessage(@Valid @RequestBody ChatMessageRequestDTO dto) {
        Long userId = getCurrentUserId();
        ChatMessageVO vo = chatService.sendMessage(dto, userId);
        return R.ok(vo);
    }
    
    /**
     * 发送消息给AI并使用流式方式获取回复
     */
    @PostMapping(value = "/send/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessageStream(@Valid @RequestBody ChatMessageRequestDTO dto) {
        Long userId = getCurrentUserId();
        SseEmitter emitter = new SseEmitter(60000L); // 60秒超时
        
        executorService.execute(() -> {
            try {
                // 开始流式处理
                chatService.sendMessageStream(dto, userId, emitter);
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data(e.getMessage()));
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            }
        });
        
        // 设置完成回调
        emitter.onCompletion(() -> {
            System.out.println("SSE完成");
        });
        
        // 设置超时回调
        emitter.onTimeout(() -> {
            System.out.println("SSE超时");
            emitter.complete();
        });
        
        // 设置错误回调
        emitter.onError((e) -> {
            System.out.println("SSE发生错误: " + e.getMessage());
            emitter.completeWithError(e);
        });
        
        return emitter;
    }

    /**
     * 获取历史对话记录
     */
    @GetMapping("/history")
    public R<List<ChatMessageVO>> getHistory() {
        Long userId = getCurrentUserId();
        List<ChatMessageVO> history = chatService.getHistoryMessages(userId);
        return R.ok(history);
    }

    /**
     * 清空对话历史
     */
    @DeleteMapping("/history")
    public R<Void> clearHistory() {
        Long userId = getCurrentUserId();
        chatService.clearHistory(userId);
        return R.ok();
    }

    /**
     * 获取当前登录用户ID（需集成JWT或网关鉴权，示例用1L）
     */
    private Long getCurrentUserId() {
        // TODO: 实现真实用户ID获取逻辑
        return 1L;
    }
} 