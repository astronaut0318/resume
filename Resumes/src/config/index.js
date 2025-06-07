/**
 * 项目全局配置
 */

// API地址配置
export const API_CONFIG = {
  // 基础URL - 从环境变量获取或使用默认值
  BASE_URL: import.meta.env.VITE_API_BASE_URL || '/api',
  
  // AI聊天接口
  CHAT_API: {
    SEND_MESSAGE: '/chat/send',
    GET_HISTORY: '/chat/history',
    CLEAR_HISTORY: '/chat/history' // DELETE方法
  },
  
  // 超时设置
  TIMEOUT: {
    DEFAULT: 30000, // 默认超时30秒
    DOWNLOAD: 60000 // 下载超时60秒
  }
};

// 网站信息
export const SITE_INFO = {
  NAME: '智能简历系统',
  DESCRIPTION: '专业的简历创建与管理平台',
  VERSION: '1.0.0'
};

// AI助手相关配置
export const AI_ASSISTANT_CONFIG = {
  NAME: 'AI简历助手',
  SUGGESTIONS: [
    '如何优化我的简历？',
    '如何突出我的技能亮点？',
    '如何写好项目经历？'
  ]
};

export default {
  API_CONFIG,
  SITE_INFO,
  AI_ASSISTANT_CONFIG
}; 