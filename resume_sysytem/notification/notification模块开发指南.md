# notification模块开发指南

## 目录结构

```
notification
├── src
│   ├── main
│   │   ├── java/com/ptu/notification
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   │   └── impl
│   │   │   ├── entity
│   │   │   ├── mapper
│   │   │   ├── dto
│   │   │   ├── vo
│   │   │   ├── feign
│   │   │   └── exception
│   │   └── resources
│   │       └── application.yml
│   └── test
└── notification模块开发指南.md
```

## 功能说明
- 通知增删查改、批量操作、标记已读/未读、统计未读、Mock邮件发送
- 严格遵循RESTful风格和统一响应格式
- 所有接口需登录校验
- 与user-service联动校验用户
- 统一异常处理、日志

## 开发进度
- [x] 目录结构与开发指南
- [x] pom依赖与配置
- [x] DTO/VO/Query对象
- [x] Feign集成
- [x] 实体与Mapper
- [x] Service与实现
- [x] Controller接口
- [ ] 单元测试
- [ ] 文档完善 