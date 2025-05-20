# 公共模块（Common Module）

## 模块说明
公共模块是整个简历系统的基础支撑模块，为所有业务模块提供通用功能和工具类。该模块的设计目标是提高代码复用性、统一系统规范、简化业务开发流程。

## 子模块结构

公共模块包含以下四个子模块：

### 1. 核心模块（core）
提供基础工具类、通用常量、异常处理机制和公共数据模型等核心功能。

- **包路径**：`com.ptu.resume.core`
- **主要功能**：统一响应处理、全局异常定义、工具类、常量定义
- **详细说明**：[核心模块README](./core/README.md)

### 2. 安全模块（security）
提供安全相关功能，包括JWT认证、权限控制、密码加密等安全机制。

- **包路径**：`com.ptu.resume.security`
- **主要功能**：JWT认证、安全配置、权限控制
- **详细说明**：[安全模块README](./security/README.md)

### 3. 服务模块（service）
提供通用服务组件，包括数据访问、缓存、分布式锁等服务组件。

- **包路径**：`com.ptu.resume.service`
- **主要功能**：Redis服务、分布式锁、缓存服务
- **详细说明**：[服务模块README](./service/README.md)

### 4. Web模块（web）
提供Web应用通用组件，包括全局异常处理、日志处理、跨域配置等功能。

- **包路径**：`com.ptu.resume.web`
- **主要功能**：全局异常处理、日志处理、跨域配置
- **详细说明**：[Web模块README](./web/README.md)

## 依赖关系

四个子模块的依赖关系如下：
```
core <-- security
 ^        ^
 |        |
 |        |
service <-+
 ^
 |
web
```

- core：基础模块，不依赖其他模块
- security：依赖core模块
- service：依赖core模块
- web：依赖core、security和service模块

## 使用说明

### 1. 引入依赖
在各业务模块的pom.xml中引入所需的公共模块：

```xml
<!-- 引入核心模块 -->
<dependency>
    <groupId>com.ptu.resume</groupId>
    <artifactId>resume-common-core</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- 引入安全模块 -->
<dependency>
    <groupId>com.ptu.resume</groupId>
    <artifactId>resume-common-security</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- 引入服务模块 -->
<dependency>
    <groupId>com.ptu.resume</groupId>
    <artifactId>resume-common-service</artifactId>
    <version>${project.version}</version>
</dependency>

<!-- 引入Web模块 -->
<dependency>
    <groupId>com.ptu.resume</groupId>
    <artifactId>resume-common-web</artifactId>
    <version>${project.version}</version>
</dependency>
```

### 2. 开发规范

在使用公共模块进行开发时，请遵循以下规范：

1. 使用统一的响应格式（`R<T>`）
2. 使用统一的异常处理机制
3. 遵循RESTful API设计规范
4. 参数校验统一使用`@Valid`和校验注解
5. 日志记录使用日志切面和拦截器

## 功能概览

### 核心功能
- 统一响应模型
- 异常处理机制
- 工具类（日期、字符串等）
- 常量定义

### 安全功能
- JWT认证
- 权限控制
- 安全配置

### 服务功能
- Redis操作
- 分布式锁
- 缓存服务

### Web功能
- 全局异常处理
- 日志记录
- 跨域配置
- 接口限流 