import { createPinia } from 'pinia'
import { useAiChat } from './aiChatStore'

// 创建Pinia实例
const pinia = createPinia()

// 导出 Pinia 实例
export default pinia

// 导出所有 store
export {
  useAiChat
} 