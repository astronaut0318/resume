<template>
  <div class="ai-floating-chat" :class="{ 'expanded': isExpanded }">
    <!-- 聊天图标/收起按钮 -->
    <div class="chat-toggle" @click="toggleChat">
      <i class="icon" :class="isExpanded ? 'icon-minimize' : 'icon-chat'"></i>
      <span v-if="!isExpanded" class="tooltip">AI助手</span>
    </div>

    <!-- 聊天窗口 -->
    <div v-show="isExpanded" class="chat-container">
      <div class="chat-header">
        <div class="title">{{ aiConfig.NAME }}</div>
        <div class="actions">
          <button class="btn-clear" @click="clearHistory">清空对话</button>
          <button class="btn-close" @click="toggleChat">关闭</button>
        </div>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <div v-if="messages.length === 0" class="welcome-message">
          <p>您好！我是您的{{ aiConfig.NAME }}，有以下问题可以问我：</p>
          <ul>
            <li v-for="(suggestion, index) in aiConfig.SUGGESTIONS" 
                :key="index" 
                @click="selectSuggestion(suggestion)">
              {{ suggestion }}
            </li>
          </ul>
        </div>
        <div v-else>
          <div v-for="(msg, index) in messages" :key="index" class="message" :class="[msg.messageType, { 'streaming': msg.isStreaming }]">
            <div class="avatar" :class="msg.messageType"></div>
            <div class="content">
              <div class="bubble" :class="{ 'typing': msg.isStreaming && msg.content === '' }">
                <template v-if="msg.isStreaming && msg.content === ''">
                  <span class="dot"></span>
                  <span class="dot"></span>
                  <span class="dot"></span>
                </template>
                <span v-else>{{ msg.content }}</span>
              </div>
              <div class="time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <input 
          type="text" 
          v-model="userInput" 
          @keyup.enter="sendMessage" 
          placeholder="输入您的问题..." 
          :disabled="loading" 
        />
        <button class="btn-send" @click="sendMessage" :disabled="!userInput.trim() || loading">
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue';
import { useAiChat } from '@/stores';
import { AI_ASSISTANT_CONFIG } from '@/config';

export default {
  name: 'AIFloatingChat',
  setup() {
    const aiChatStore = useAiChat();
    const isExpanded = ref(false);
    const userInput = ref('');
    const loading = ref(false);
    const messagesContainer = ref(null);
    const aiConfig = AI_ASSISTANT_CONFIG;
    
    // 监听store中的消息变化
    const messages = computed(() => {
      console.log('当前消息列表:', aiChatStore.messages);
      return aiChatStore.messages || [];
    });
    
    const sessionId = computed(() => aiChatStore.sessionId);

    const toggleChat = () => {
      isExpanded.value = !isExpanded.value;
      if (isExpanded.value && messages.value.length === 0) {
        // 首次展开加载历史记录
        getHistory();
      }
    };

    const getHistory = async () => {
      loading.value = true;
      try {
        await aiChatStore.fetchHistory();
        console.log('历史记录加载完成');
      } catch (error) {
        console.error('获取历史消息失败', error);
      } finally {
        loading.value = false;
        scrollToBottom();
      }
    };

    const sendMessage = async () => {
      if (!userInput.value.trim() || loading.value) return;

      const userMessage = userInput.value.trim();
      userInput.value = '';
      loading.value = true;

      try {
        // 使用普通模式
        console.log('使用普通模式发送消息');
        await aiChatStore.sendMessage(userMessage);
        console.log('发送消息成功，当前消息列表:', messages.value);
      } catch (error) {
        console.error('发送消息失败', error);
      } finally {
        loading.value = false;
        scrollToBottom();
      }
    };

    const clearHistory = async () => {
      if (window.confirm('确定要清空所有对话记录吗？')) {
        try {
          await aiChatStore.clearHistory();
        } catch (error) {
          console.error('清空历史失败', error);
        }
      }
    };

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        }
      });
    };

    const formatTime = (time) => {
      if (!time) return '';
      
      try {
        const date = new Date(time);
        if (isNaN(date.getTime())) return '';
        
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        
        return `${hours}:${minutes}`;
      } catch (e) {
        console.error('时间格式化错误:', e);
        return '';
      }
    };

    const selectSuggestion = (suggestion) => {
      userInput.value = suggestion;
      sendMessage();
    };

    // 监听消息变化，自动滚动到底部
    watch(() => messages.value.length, () => {
      console.log('消息列表更新，总数:', messages.value.length);
      scrollToBottom();
    });

    // 组件挂载后初始化
    onMounted(() => {
      console.log('AI对话组件已挂载');
      if (isExpanded.value) {
        getHistory();
      }
    });

    return {
      isExpanded,
      userInput,
      loading,
      messages,
      sessionId,
      messagesContainer,
      toggleChat,
      getHistory,
      sendMessage,
      clearHistory,
      scrollToBottom,
      formatTime,
      selectSuggestion,
      aiConfig
    };
  }
};
</script>

<style scoped>
.ai-floating-chat {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  font-family: 'Arial', sans-serif;
}

.chat-toggle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #1890ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  align-self: flex-end;
}

.chat-toggle .icon {
  color: #fff;
  font-size: 28px;
}

.chat-toggle .tooltip {
  position: absolute;
  right: 70px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 14px;
  white-space: nowrap;
  visibility: hidden;
  opacity: 0;
  transition: opacity 0.3s;
}

.chat-toggle:hover .tooltip {
  visibility: visible;
  opacity: 1;
}

.chat-container {
  position: absolute;
  right: 0;
  bottom: 70px;
  width: 350px;
  height: 500px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 15px;
  background-color: #1890ff;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.chat-header .actions {
  display: flex;
  gap: 10px;
}

.chat-header button {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 12px;
  opacity: 0.8;
  transition: opacity 0.3s;
}

.chat-header button:hover {
  opacity: 1;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  background-color: #f5f5f5;
}

.welcome-message {
  background-color: rgba(24, 144, 255, 0.1);
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}

.welcome-message p {
  margin: 0 0 10px;
  font-weight: bold;
}

.welcome-message ul {
  margin: 0;
  padding-left: 20px;
}

.welcome-message li {
  margin: 5px 0;
  cursor: pointer;
  color: #1890ff;
}

.welcome-message li:hover {
  text-decoration: underline;
}

.message {
  display: flex;
  gap: 10px;
  max-width: 85%;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message .avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #1890ff;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.message .avatar.user {
  background-color: #52c41a;
}

.message .avatar.ai::after {
  content: "AI";
}

.message .avatar.user::after {
  content: "我";
}

.message .content {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.message .bubble {
  padding: 10px 15px;
  border-radius: 18px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  word-break: break-word;
  white-space: pre-line;
}

.message.user .bubble {
  background-color: #e6f7ff;
}

.message .time {
  font-size: 12px;
  color: #999;
  align-self: flex-end;
}

.bubble.typing {
  display: flex;
  align-items: center;
  gap: 4px;
  min-height: 24px;
  min-width: 60px;
}

.bubble.typing .dot {
  width: 8px;
  height: 8px;
  background-color: #999;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.bubble.typing .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.bubble.typing .dot:nth-child(2) {
  animation-delay: -0.16s;
}

/* 流式传输动画效果 */
.message.streaming .bubble {
  border-right: 2px solid #1890ff;
  animation: cursor-blink 0.8s infinite;
}

@keyframes cursor-blink {
  0%, 100% { border-color: transparent; }
  50% { border-color: #1890ff; }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.chat-input {
  padding: 15px;
  display: flex;
  gap: 10px;
  border-top: 1px solid #eee;
}

.chat-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
  transition: border-color 0.3s;
}

.chat-input input:focus {
  border-color: #1890ff;
}

.btn-send {
  padding: 0 15px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-send:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-send:not(:disabled):hover {
  background-color: #40a9ff;
}

/* 图标样式 */
.icon-chat::before {
  content: "💬";
}

.icon-minimize::before {
  content: "✖";
}

/* 响应式设计 */
@media (max-width: 480px) {
  .chat-container {
    width: calc(100vw - 40px);
    height: 70vh;
    right: 0;
    bottom: 70px;
  }
}
</style> 