# Gateway网关服务

## 模块简介

Gateway模块是整个简历系统的API网关，负责请求的路由、鉴权、限流等功能。作为系统的流量入口，它提供了统一的接入点，同时通过一系列的过滤器实现了安全控制和流量管理。

## 功能特性

- **路由转发**：根据配置将请求转发到相应的微服务
- **统一鉴权**：通过JWT验证实现API调用的身份认证
- **限流控制**：集成Sentinel实现针对API的限流保护
- **跨域支持**：提供CORS跨域资源共享配置
- **熔断降级**：服务不可用时提供友好的降级处理
- **日志追踪**：记录请求响应信息，便于问题排查

## 模块结构

```
gateway/
├── src/main/java/com/ptu/resume/gateway/
│   ├── config/           # 配置类
│   │   └── CorsConfig.java  # 跨域配置
│   ├── filter/           # 过滤器
│   │   ├── AuthFilter.java  # 权限校验过滤器
│   │   └── LoggingFilter.java  # 日志记录过滤器
│   ├── exception/        # 异常处理
│   ├── handler/          # 处理器
│   └── GatewayApplication.java  # 启动类
└── src/main/resources/
    ├── application.yml   # 应用配置
    └── bootstrap.yml     # 启动配置
```

## 关键配置

### 路由配置

路由定义在`application.yml`中，系统目前已配置以下路由：

- **用户服务**：/user/** → user-service
- **简历服务**：/resume/** → resume-service
- **认证服务**：/auth/** → auth-service
- **模板服务**：/template/** → template-service
- **订单服务**：/order/** → order-service
- **AI服务**：/ai/** → ai-service
- **文件服务**：/file/** → file-service
- **通知服务**：/notification/** → notification-service

### 安全配置

白名单路径配置在application.yml的security.ignore.whites部分，无需身份验证即可访问。

## 依赖组件

- **Spring Cloud Gateway**：基于WebFlux的API网关
- **Spring Cloud Alibaba Nacos**：服务注册与发现
- **Spring Cloud Alibaba Sentinel**：限流熔断
- **Redis**：缓存Token信息
- **JWT**：无状态身份验证

## 部署信息

- 默认端口：9000
- 服务名称：gateway-service
