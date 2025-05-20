# 基于Spring Cloud的简历系统

## 项目简介
本项目是一个基于Spring Cloud的分布式简历系统，旨在为用户提供简历创建、编辑、管理及分享的一站式服务。系统支持多种简历模板，可在线预览、编辑并下载为PDF或Word格式，同时集成AI辅助功能，帮助用户优化简历内容。

## 系统架构
项目采用微服务架构，主要包含以下模块：

```
resume/
├── common/                                 # 公共模块
│   ├── core/                              # 核心工具类及通用代码
│   ├── service/                           # 数据访问、缓存和服务调用组件
│   ├── security/                          # 安全相关配置及工具
│   └── web/                               # Web应用通用组件和日志处理
├── gateway/                               # 网关服务
├── auth/                                  # 认证服务
├── user/                                  # 用户服务
├── resume/                                # 简历服务
├── template/                              # 模板服务
├── order/                                 # 订单服务
├── ai/                                    # AI辅助服务
├── file/                                  # 文件服务
└── notification/                          # 通知服务
```

## 技术栈
- **Java**: JDK 1.8
- **Spring Boot**: 2.6.13
- **Spring Cloud**: 2021.0.5
- **Spring Cloud Alibaba**: 2021.0.4.0
- **MySQL**: 8.0.28
- **MyBatis Plus**: 3.5.2
- **Redis**: 使用Spring Data Redis
- **Nacos**: 服务注册与配置中心
- **Gateway**: API网关
- **Sentinel**: 熔断限流
- **OpenFeign**: 服务调用
- **JWT**: 认证授权
- **MinIO/阿里云OSS**: 对象存储
- **RabbitMQ**: 消息队列
- **Sleuth + Zipkin**: 链路追踪

## 模块说明

### 公共模块
- **common/core**: 核心工具类、通用常量、异常处理机制和公共数据模型
  - 统一响应模型(R)、分页结果封装(PageResult)
  - 基础异常类和业务异常类
  - 日期工具类、字符串工具类等
- **common/web**: Web应用通用组件和全局异常处理
  - 全局异常处理器
  - 日志处理
- **common/service**: 数据访问、缓存和服务调用组件
  - Redis服务接口及实现
- **common/security**: 安全相关配置及工具
  - JWT工具类
  - Spring Security配置

### 业务模块
- **gateway**: 网关服务，系统统一入口
- **auth**: 认证服务，负责用户认证与授权
- **user**: 用户服务，管理用户信息
- **resume**: 简历服务，核心业务模块
- **template**: 模板服务，管理简历模板
- **order**: 订单服务，处理支付交易
- **ai**: AI辅助服务，提供智能优化
- **file**: 文件服务，处理文件上传下载
- **notification**: 通知服务，管理系统消息

## 开发进度
- [x] 基础框架搭建
- [x] 公共模块开发
  - [x] 公共核心模块
  - [x] 公共Web模块 
  - [x] 公共服务模块
  - [x] 公共安全模块
- [ ] 网关服务开发
- [ ] 用户认证服务开发
- [ ] 用户服务开发
- [ ] 简历服务开发
- [ ] 模板服务开发
- [ ] 订单服务开发
- [ ] 文件服务开发
- [ ] AI辅助服务开发
- [ ] 通知服务开发

## 后续开发计划
1. 开发网关服务，实现统一入口和路由转发
2. 开发用户和认证服务，实现基本的用户管理和登录功能
3. 开发简历和模板服务，实现核心业务功能
4. 开发订单服务，实现支付功能
5. 开发AI辅助功能，提升用户体验
6. 完善文件和通知服务，提供完整的应用功能

## 开发文档

### 模块文档
- [核心模块文档](./common/core/README.md)
- [安全模块文档](./common/security/README.md)
- [服务模块文档](./common/service/README.md)
- [Web模块文档](./common/web/README.md)
- [公共模块总览](./common/README.md)

### API文档
- 系统启动后访问：http://localhost:8080/doc.html

### 开发指南
开发过程请严格遵循《DeveloperGuide》文件夹中的规范：
- 编码规范
- API设计规范
- 数据库设计规范
- 微服务开发规范