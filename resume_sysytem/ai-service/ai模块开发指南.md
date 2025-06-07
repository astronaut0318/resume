# ai模块开发指南

## 目录结构

```
ai-service
├── src
│   ├── main
│   │   ├── java/com/ptu/ai
│   │   │   ├── controller
│   │   │   │   ├── ResumeAIController.java
│   │   │   │   └── ChatController.java
│   │   │   ├── service
│   │   │   │   ├── ResumeAIService.java
│   │   │   │   ├── ChatService.java
│   │   │   │   └── impl
│   │   │   │       └── ChatServiceImpl.java
│   │   │   ├── dto
│   │   │   │   └── ChatMessageRequestDTO.java
│   │   │   ├── vo
│   │   │   │   └── ChatMessageVO.java
│   │   │   ├── config
│   │   │   │   └── RestTemplateConfig.java
│   │   │   ├── feign
│   │   │   └── exception
│   │   └── resources
│   │       ├── application.yml
│   │       └── static
│   │           ├── index.html
│   │           ├── ai-chat-demo.html
│   │           └── AIFloatingChat.vue
│   └── test
└── ai模块开发指南.md
```

## 功能说明
- 简历分析、内容优化、职位匹配、自动生成等AI能力
- 实时AI聊天对话框，可在任何页面中集成
- 严格遵循RESTful风格和统一响应格式
- deepseek-V3大模型API真实调用
- 所有接口需登录校验
- 与resume-service联动，获取/更新简历内容
- 统一异常处理、日志
- 前端演示页面和浮动对话框组件

## AI聊天功能说明
- 浮动对话框可在任何页面集成
- 支持会话历史保存和查看
- 支持清空会话历史
- 响应式UI设计，支持移动端和桌面端
- 与DeepSeek API实时交互，智能回答问题

## 使用方法

### 后端接口
- `/chat/send`: 发送消息给AI获取回复
- `/chat/history`: 获取历史消息记录
- `/chat/history`: (DELETE) 清空对话历史

### 前端集成
1. 将AIFloatingChat.vue组件导入到Vue项目中
2. 在需要显示聊天窗口的页面中添加组件:
```vue
<template>
  <div>
    <!-- 页面其他内容 -->
    <AIFloatingChat />
  </div>
</template>

<script>
import AIFloatingChat from '@/components/AIFloatingChat.vue';

export default {
  components: {
    AIFloatingChat
  }
}
</script>
```

## 开发进度
- [x] 目录结构与开发指南
- [x] pom依赖与配置
- [x] DTO/VO/Query对象
- [x] Feign集成
- [x] Service与实现
- [x] Controller接口
- [x] AI聊天实时对话功能
- [x] 前端浮动对话框组件
- [x] 演示页面
- [ ] 单元测试
- [ ] 文档完善 