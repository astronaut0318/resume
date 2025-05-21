<<<<<<< HEAD
# Vue 3 + Vite

This template should help get you started developing with Vue 3 in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about IDE Support for Vue in the [Vue Docs Scaling up Guide](https://vuejs.org/guide/scaling-up/tooling.html#ide-support).
=======
# resume
<<<<<<< HEAD
# 简历系统开发规范

## 一、项目基本规范

### 1. 项目结构规范

````
# 基于Spring Cloud的简历系统开发手册

## 项目简介

本项目是一个基于Spring Cloud的分布式简历系统，旨在为用户提供简历创建、编辑、管理及分享的一站式服务。系统支持多种简历模板，可在线预览、编辑并下载为PDF或Word格式，同时集成AI辅助功能，帮助用户优化简历内容。
nacos地址: 127.0.0.1:8848
namespace: f7c0123a-5594-4811-9fa2-0252d3776289

mysql: 
url: jdbc:mysql://localhost:3306/resume?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=Asia/Shanghai
username: root
password: 1234

redis:
host: 127.0.0.1
port: 6379
database: 0
timeout: 10000

## 核心功能

- 用户注册/登录功能
- 简历创建与编辑
- 简历模板选择
- 在线预览与下载(PDF、Word)
- 在线编辑简历
- 购买简历模板
- AI辅助编辑简历
- 模板收藏
- 模板分享

## 技术栈

### 后端技术
- **Java**: JDK 1.8
- **Maven**: 3.6.x
- **Spring Boot**: 2.6.13
- **Spring Cloud**: 2021.0.5
- **Spring Cloud Alibaba**: 2021.0.4.0
- **MySQL**: 8.0.28
- **MyBatis Plus**: 3.5.2
- **Redis**: 6.0+
- **RabbitMQ**: 3.8+
- **Nacos**: 服务注册、配置中心
- **Gateway**: API网关
- **Feign**: 声明式HTTP客户端
- **Sentinel**: 流量控制、熔断降级
- **JWT**: 0.9.1认证授权
- **MinIO**: 对象存储，用于存储模板文件
- **Hutool**: 5.8.11 工具库
- **Druid**: 1.2.8 数据库连接池
- **Swagger/OpenAPI**: 2.0.4 接口文档管理
- **springdoc-openapi**: 1.7.0 API文档框架


## 微服务架构

系统划分为以下微服务模块：

### 1. 用户服务 (User Service)
- 功能：用户信息管理、个人资料维护
- 主要API：
  - 用户注册
  - 用户信息修改
  - 用户信息查询

### 2. 认证服务 (Auth Service)
- 功能：用户认证、权限控制
- 主要API：
  - 用户登录
  - 刷新Token
  - 权限校验

### 3. 简历管理服务 (Resume Service)
- 功能：简历CRUD、简历版本管理
- 主要API：
  - 创建简历
  - 更新简历
  - 获取简历列表
  - 获取简历详情
  - 删除简历

### 4. 模板服务 (Template Service)
- 功能：模板管理、模板分类、模板收藏
- 主要API：
  - 获取模板列表
  - 获取模板详情
  - 收藏模板
  - 取消收藏
  - 分享模板

### 5. 订单支付服务 (Order Service)
- 功能：模板购买、订单管理、支付集成
- 主要API：
  - 创建订单
  - 支付订单
  - 查询订单状态
  - 获取订单历史

### 6. AI 辅助服务 (AI Service)
- 功能：简历内容分析、优化建议
- 主要API：
  - 简历分析
  - 内容优化建议
  - 职位匹配分析

### 7. 文件服务 (File Service)
- 功能：文件上传下载、格式转换(PDF/Word)
- 主要API：
  - 上传文件
  - 下载文件
  - 简历导出为PDF
  - 简历导出为Word

### 8. 通知服务 (Notification Service)
- 功能：系统通知、邮件提醒
- 主要API：
  - 发送邮件通知
  - 发送系统消息
  - 获取未读消息

### 9. 公共模块 (Common Module)
- 功能：为各微服务提供公共功能和工具类
- 主要组件：
  - 通用工具类
  - 统一响应格式
  - 全局异常处理
  - 通用配置类
  - 安全组件

#### 公共模块结构
```
common/
├── core/                      # 核心工具类及通用代码
│   ├── constants/             # 常量定义
│   ├── exception/             # 全局异常类
│   ├── utils/                 # 工具类
│   └── model/                 # 公共数据模型
├── service/                   # 数据访问、缓存和服务调用组件
├── security/                  # 安全相关配置及工具
└── web/                       # Web应用通用组件和日志处理
```

#### 公共模块主要功能
- **统一响应处理**：统一定义API响应格式，包括成功和失败响应
- **全局异常处理**：统一异常捕获和处理机制
- **JWT工具类**：处理JWT令牌的生成、解析和验证
- **Redis工具类**：封装常用的Redis操作
- **树形结构工具**：处理树形数据结构的工具类
- **文件操作工具**：处理文件上传下载的通用方法
- **加密解密工具**：提供常用的加密解密算法
- **参数校验工具**：统一的参数校验机制
- **日期时间工具**：处理日期时间的转换和计算
- **分布式锁实现**：基于Redis的分布式锁
- **MybatisPlus配置**：统一的ORM框架配置

## 数据库设计

### 用户模块
```sql
-- 用户表
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '角色：0-普通用户，1-VIP用户，2-管理员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户详情表
CREATE TABLE `user_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `education` varchar(50) DEFAULT NULL COMMENT '学历',
  `work_years` int DEFAULT NULL COMMENT '工作年限',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `profile` text COMMENT '个人简介',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户详情表';

-- VIP会员表
CREATE TABLE `vip_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `level` tinyint NOT NULL DEFAULT '1' COMMENT 'VIP等级',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP会员表';
```

### 简历模块
```sql
-- 简历表
CREATE TABLE `resumes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) NOT NULL COMMENT '简历标题',
  `template_id` bigint DEFAULT NULL COMMENT '模板ID',
  `content` longtext COMMENT '简历内容（JSON格式）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-草稿，1-已完成',
  `is_default` tinyint NOT NULL DEFAULT '0' COMMENT '是否默认简历：0-否，1-是',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 简历版本表
CREATE TABLE `resume_versions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resume_id` bigint NOT NULL COMMENT '简历ID',
  `version` int NOT NULL COMMENT '版本号',
  `content` longtext COMMENT '简历内容（JSON格式）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resume_version` (`resume_id`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历版本表';
```

### 模板模块
```sql
-- 模板分类表
CREATE TABLE `template_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板分类表';

-- 模板表
CREATE TABLE `templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `thumbnail` varchar(255) NOT NULL COMMENT '缩略图',
  `file_path` varchar(255) NOT NULL COMMENT '模板文件路径',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
  `is_free` tinyint NOT NULL DEFAULT '0' COMMENT '是否免费：0-否，1-是',
  `downloads` int NOT NULL DEFAULT '0' COMMENT '下载次数',
  `description` text COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板表';

-- 模板收藏表
CREATE TABLE `template_collections` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_template` (`user_id`,`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板收藏表';
```

### 订单模块
```sql
-- 订单表
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `pay_type` tinyint DEFAULT NULL COMMENT '支付方式：1-支付宝，2-微信',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待支付，1-已支付，2-已取消',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 支付记录表
CREATE TABLE `payment_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `trade_no` varchar(100) DEFAULT NULL COMMENT '交易流水号',
  `pay_type` tinyint NOT NULL COMMENT '支付方式：1-支付宝，2-微信',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` tinyint NOT NULL COMMENT '状态：0-处理中，1-成功，2-失败',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';
```

### 文件模块
```sql
-- 文件表
CREATE TABLE `files` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_name` varchar(255) NOT NULL COMMENT '存储文件名',
  `file_path` varchar(255) NOT NULL COMMENT '文件路径',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(50) NOT NULL COMMENT '文件类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';
```

### 通知模块
```sql
-- 通知表
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` tinyint NOT NULL COMMENT '类型：1-系统通知，2-订单通知，3-其他',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
```

## 用户角色说明

系统定义了以下三种用户角色，每种角色拥有不同的权限和功能：

### 1. 普通用户（求职者）
- 基本简历创建和编辑
- 使用免费模板
- 导出简历为PDF/Word
- 收藏模板

### 2. VIP用户（付费用户）
- 包含普通用户所有权限
- 使用所有付费模板
- AI辅助简历优化
- 高级简历格式定制
- 无限简历版本保存

### 3. 系统管理员
- 用户管理
- 模板管理
- 内容审核
- 系统配置
- 数据统计分析

## 开发环境配置

### 环境需求
- JDK 1.8
- Maven 3.6.x
- MySQL 8.0.28
- Redis 6.0+
- RabbitMQ 3.8+
- Node.js 14+

### 本地开发环境搭建
1. 克隆代码仓库
2. 配置MySQL数据库，执行数据库初始化脚本
3. 配置Redis，RabbitMQ等中间件
4. 配置Nacos服务
5. 启动各微服务模块
6. 启动前端应用

## 项目启动顺序
1. Nacos (服务注册中心和配置中心)
2. Redis & RabbitMQ (中间件)
3. Gateway (API网关)
4. Auth Service (认证服务)
5. 其他业务服务

## 开发指南

### 代码规范
- 遵循阿里巴巴Java开发手册
- 接口统一使用RESTful风格
- 使用统一的返回格式
- 注释完整，尤其是接口文档注释


### 接口文档

#### Swagger API文档
- 项目集成了Swagger 3.0（SpringDoc OpenAPI）进行API文档管理
- 每个微服务模块都配置了独立的Swagger文档
- 访问方式：`http://{服务地址}:{端口}/swagger-ui/index.html`
- 网关聚合文档：`http://{网关地址}:{端口}/swagger-ui/index.html`

#### API文档配置规范
- 所有Controller类都必须添加`@Tag(name = "xxx模块")`标签
- 所有接口方法都必须添加`@Operation(summary = "xxx功能")`注解
- 请求参数必须添加`@Parameter`或`@Schema`进行说明
- 响应结果必须指定`@ApiResponse`

#### Swagger配置示例
```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户信息相关接口")
public class UserController {

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public R<UserVO> getUserInfo(
            @Parameter(description = "用户ID", required = true) 
            @PathVariable Long userId) {
        // 业务逻辑
    }
}
```

#### 静态API文档
- 详细API文档采用Markdown格式编写，请参考以下文件：
  - [API-DOCS.md](API-DOCS.md): 用户服务、认证服务
  - [API-DOCS-PART2.md](API-DOCS-PART2.md): 简历服务、模板服务
  - [API-DOCS-PART3.md](API-DOCS-PART3.md): 模板服务(续)、订单服务、AI服务
  - [API-DOCS-PART4.md](API-DOCS-PART4.md): AI服务(续)、文件服务、通知服务
  - [API-DOCS-PART5.md](API-DOCS-PART5.md): 通知服务(续)、网关API、管理员API
- 有关API文档的使用和管理，请参考[API-DOCS-README.md](API-DOCS-README.md)

### 日志规范
- 每个微服务使用统一的日志配置
- 错误日志需要包含足够的上下文信息
- 生产环境禁止打印敏感信息


## 系统架构图

```
                           │
                           │ 用户请求
                           ▼
                    ┌─────────────┐
                    │ Gateway 网关 │
                    └─────────────┘
                           │
                           │
         ┌────────────────┼────────────────┐
         │                │                │
         ▼                ▼                ▼
  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
  │  用户服务    │  │  认证服务    │  │  简历服务    │
  └─────────────┘  └─────────────┘  └─────────────┘
         │                │                │
         │                │                │
         │          ┌─────────────┐        │
         └────────► │   Nacos     │ ◄──────┘
                    │ 服务注册中心  │
                    └─────────────┘
                           │
         ┌────────────────┼────────────────┐
         │                │                │
         ▼                ▼                ▼
  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
  │  模板服务    │  │  订单服务    │  │   AI服务    │
  └─────────────┘  └─────────────┘  └─────────────┘
         │                │                │
         │                │                │
         ▼                ▼                ▼
  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
  │  文件服务    │  │  通知服务    │  │    Redis    │
  └─────────────┘  └─────────────┘  └─────────────┘
         │                │                │
         │                │                │
         ▼                ▼                ▼
  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
  │   MinIO     │  │  RabbitMQ   │  │   MySQL     │
  └─────────────┘  └─────────────┘  └─────────────┘
```

## Swagger配置指南

### 1. 添加依赖
每个微服务模块的pom.xml中添加以下依赖：
```xml
<!-- Swagger OpenAPI 3.0 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.7.0</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-webmvc-core</artifactId>
    <version>1.7.0</version>
</dependency>
```

### 2. 配置Swagger

在每个微服务的配置类中添加：
```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("简历系统 API文档")
                        .version("v1.0.0")
                        .description("基于Spring Cloud的简历系统API文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("team@example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://example.com/docs"));
    }
}
```

### 3. 网关Swagger聚合配置
在Gateway服务的配置类中添加：
```java
@Configuration
public class SwaggerResourceConfig implements SwaggerResourcesProvider {

    private final RouteLocator routeLocator;
    private final GatewayProperties gatewayProperties;

    public SwaggerResourceConfig(RouteLocator routeLocator, GatewayProperties gatewayProperties) {
        this.routeLocator = routeLocator;
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();
        
        // 获取所有路由
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        
        // 过滤出API路由
        gatewayProperties.getRoutes().stream()
                .filter(routeDefinition -> routes.contains(routeDefinition.getId()))
                .forEach(routeDefinition -> {
                    routeDefinition.getPredicates().stream()
                            .filter(predicateDefinition -> 
                                    ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                            .forEach(predicateDefinition -> {
                                String path = predicateDefinition.getArgs().get("pattern")
                                        .replace("/**", "/v3/api-docs");
                                resources.add(swaggerResource(routeDefinition.getId(), path));
                            });
                });
        
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("3.0");
        return swaggerResource;
    }
}
```

## 后续开发计划

1. 第一阶段：基础框架搭建
   - 搭建微服务基础架构
   - 实现用户注册、登录功能
   - 简历基本CRUD功能
   - 集成Swagger API文档

2. 第二阶段：核心功能开发
   - 模板管理系统
   - 简历编辑器
   - 导出PDF/Word功能

3. 第三阶段：高级功能开发
   - AI辅助功能
   - 支付系统集成
   - 数据分析功能

4. 第四阶段：优化与上线
   - 性能优化
   - 安全加固
   - 部署上线
=======
简历系统
>>>>>>> 87bcf36b013608c1e6582d635662661dcdd8ccc1
>>>>>>> 247a8d6a4b300f2db18cafba860d77f287b1ed26
