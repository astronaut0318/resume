import { defineStore } from 'pinia';
import { sendChatMessage, getChatHistory, clearChatHistory } from '@/api/chat';

/**
 * AI聊天状态管理
 */
export const useAiChat = defineStore('aiChat', {
  state: () => ({
    messages: [],
    sessionId: null,
    error: null,
  }),

  actions: {
    /**
     * 获取历史消息
     */
    async fetchHistory() {
      try {
        const response = await getChatHistory();
        // 兼容处理不同响应格式
        const historyData = response.data || response || [];
        this.messages = Array.isArray(historyData) ? historyData : [];
        
        // 如果有历史消息，更新会话ID
        if (this.messages.length > 0) {
          this.sessionId = this.messages[this.messages.length - 1].sessionId;
        }
        
        console.log('获取历史消息成功:', this.messages);
        return this.messages;
      } catch (error) {
        console.error('获取历史消息失败:', error);
        this.error = error.message || '获取历史消息失败';
        throw error;
      }
    },

    /**
     * 发送消息
     * @param {string} content 消息内容
     */
    async sendMessage(content) {
      try {
        console.log('发送消息:', content, '会话ID:', this.sessionId);
        
        // 先添加用户消息到列表
        const tempUserMsg = {
          content,
          messageType: 'user',
          createTime: new Date()
        };
        this.messages.push(tempUserMsg);

        // 发送消息到服务器
        const response = await sendChatMessage(content, this.sessionId);
        console.log('服务器响应:', response);
        
        // 兼容处理不同响应格式
        const aiMessage = response.data || response;
        
        // 检查是否有会话ID
        if (aiMessage && aiMessage.sessionId) {
          // 更新会话ID
          this.sessionId = aiMessage.sessionId;
          
          // 添加AI回复到消息列表
          this.messages.push(aiMessage);
          console.log('添加AI消息到列表:', aiMessage);
          
          return aiMessage;
        } else {
          throw new Error('响应数据格式不正确');
        }
      } catch (error) {
        console.error('发送消息失败:', error);
        this.error = error.message || '发送消息失败';
        
        // 添加错误消息
        this.messages.push({
          content: '抱歉，网络连接出现问题，请稍后再试。',
          messageType: 'ai',
          createTime: new Date()
        });
        
        throw error;
      }
    },

    /**
     * 清空历史记录
     */
    async clearHistory() {
      try {
        await clearChatHistory();
        this.messages = [];
        this.sessionId = null;
        this.error = null;
        console.log('清空历史成功');
      } catch (error) {
        console.error('清空历史失败:', error);
        this.error = error.message || '清空历史失败';
        throw error;
      }
    }
  }
}); 