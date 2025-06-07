import axios from 'axios';
import { API_CONFIG } from '@/config';

// 创建专用的axios实例
const chatApi = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  timeout: API_CONFIG.TIMEOUT.DEFAULT,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加token
chatApi.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一处理响应
chatApi.interceptors.response.use(
  response => {
    const res = response.data;
    // 处理标准接口格式：{ code: 200, message: "操作成功", data: {} }
    // 或 { message: "操作成功", data: {}, success: true } 格式
    if ((res.code !== undefined && res.code !== 200) || 
        (res.success !== undefined && !res.success)) {
      console.error('API错误:', res.message);
      return Promise.reject(new Error(res.message || '未知错误'));
    }
    return res;
  },
  error => {
    console.error('网络错误:', error);
    return Promise.reject(error);
  }
);

/**
 * 发送消息给AI获取回复
 * @param {string} content 消息内容
 * @param {string} sessionId 会话ID (可选)
 * @returns {Promise} 返回AI回复的消息
 */
export function sendChatMessage(content, sessionId = null) {
  return chatApi.post(API_CONFIG.CHAT_API.SEND_MESSAGE, {
    content,
    sessionId
  });
}

/**
 * 获取历史消息
 * @returns {Promise} 返回历史消息列表
 */
export function getChatHistory() {
  return chatApi.get(API_CONFIG.CHAT_API.GET_HISTORY);
}

/**
 * 清空聊天历史
 * @returns {Promise}
 */
export function clearChatHistory() {
  return chatApi.delete(API_CONFIG.CHAT_API.CLEAR_HISTORY);
}

export default {
  sendChatMessage,
  getChatHistory,
  clearChatHistory
}; 