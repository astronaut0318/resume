# order模块开发指南

## 目录结构

```
order-service
├── src
│   ├── main
│   │   ├── java/com/ptu/order
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
│   │       ├── mapper
│   │       └── application.yml
│   └── test
```

## 功能说明
- 订单创建、查询、详情、取消、支付回调等核心功能
- 严格遵循RESTful风格和统一响应格式
- 支持与模板、用户等服务联动
- 统一异常处理、日志、权限校验

## 开发进度
- [x] 目录结构与开发指南
- [x] 实体、Mapper、DTO、VO
- [x] Service与实现
- [x] Controller接口
- [x] Feign集成
- [x] 异常处理
- [ ] 单元测试
- [ ] 文档完善 