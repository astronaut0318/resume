# ai模块开发指南

## 目录结构

```
ai-service
├── src
│   ├── main
│   │   ├── java/com/ptu/ai
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   │   └── impl
│   │   │   ├── dto
│   │   │   ├── vo
│   │   │   ├── feign
│   │   │   └── exception
│   │   └── resources
│   │       └── application.yml
│   └── test
└── ai模块开发指南.md
```

## 功能说明
- 简历分析、内容优化、职位匹配、自动生成等AI能力
- 严格遵循RESTful风格和统一响应格式
- deepseek-V3大模型API真实调用
- 所有接口需登录校验
- 与resume-service联动，获取/更新简历内容
- 统一异常处理、日志

## 开发进度
- [x] 目录结构与开发指南
- [x] pom依赖与配置
- [x] DTO/VO/Query对象
- [x] Feign集成
- [x] Service与实现
- [x] Controller接口
- [ ] 单元测试
- [ ] 文档完善 