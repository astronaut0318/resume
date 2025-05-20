# 用户模块

## 模块说明
用户模块负责系统用户的管理，包括用户注册、信息维护、角色分配等功能。

## 目录结构
```
user/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ptu/
│   │   │           └── resume/
│   │   │               └── user/
│   │   │                   ├── controller/   # 控制器层，处理HTTP请求
│   │   │                   ├── service/      # 服务接口层
│   │   │                   │   └── impl/     # 服务实现层
│   │   │                   ├── mapper/       # 数据访问层
│   │   │                   ├── entity/       # 实体类层
│   │   │                   ├── dto/          # 数据传输对象层
│   │   │                   ├── vo/           # 视图对象层
│   │   │                   ├── query/        # 查询对象层
│   │   │                   ├── config/       # 配置类层
│   │   │                   └── util/         # 工具类
│   │   └── resources/
│   │       ├── mapper/      # MyBatis映射文件
│   │       ├── application.yml
│   │       └── bootstrap.yml
│   └── test/
└── pom.xml
```

## 功能清单
- 功能1（待开发）
- 功能2（待开发）
- 功能3（待开发）

## 数据表设计
- 表1：说明（待设计）
- 表2：说明（待设计）

## 技术栈
- Spring Boot
- MyBatis Plus
- Spring Cloud
- Redis

## 依赖服务
- 依赖 `common` 模块中的 `core` 组件
- 依赖 `common` 模块中的 `web` 组件
- 依赖 `common` 模块中的 `service` 组件

## 接口说明
- 接口1：说明（待开发）
- 接口2：说明（待开发）
- 接口3：说明（待开发）

## 开发进度
- [ ] 基础架构搭建
- [ ] 功能1开发
- [ ] 功能2开发
- [ ] 功能3开发
