# Common模块开发指南

## 一、模块概述

Common模块是整个简历系统的基础支撑模块，提供了各个服务共用的工具类、异常处理、API响应封装等通用功能。该模块被所有其他微服务依赖，用于确保整个系统的一致性和规范性。

## 二、目录结构

```
com.ptu.common
├── api               // API响应相关
│   ├── R.java        // 统一响应结果封装
│   ├── IResultCode.java    // 响应码接口
│   └── ResultCode.java     // 响应码枚举
├── config            // 配置类
│   └── SwaggerConfig.java  // Swagger文档配置
├── exception         // 异常相关
│   ├── BaseException.java  // 基础异常
│   └── BusinessException.java  // 业务异常
├── handler           // 处理器
│   └── GlobalExceptionHandler.java  // 全局异常处理器
└── util              // 工具类
    ├── DateTimeUtils.java  // 日期时间工具
    ├── PasswordUtil.java   // 密码处理工具
    └── StringUtils.java    // 字符串处理工具
```

## 三、核心组件说明

### 3.1 统一响应格式 (R.java)

#### 说明
`R<T>`是系统统一的API响应封装类，泛型`T`代表响应数据的类型。所有的Controller方法都应该返回`R<T>`类型，确保API响应的一致性。

#### 主要属性
- `code`: 响应状态码，200表示成功，其他表示失败
- `message`: 响应消息
- `data`: 响应数据，泛型T类型

#### 常用方法
- `R.ok()`: 创建成功响应，无数据
- `R.ok(T data)`: 创建成功响应，带数据
- `R.ok(String message, T data)`: 创建成功响应，自定义消息和数据
- `R.failed()`: 创建失败响应
- `R.failed(String message)`: 创建失败响应，自定义消息
- `R.failed(IResultCode resultCode)`: 创建失败响应，使用预定义响应码
- `R.paramError()`: 创建参数错误响应
- `R.unauthorized()`: 创建未授权响应
- `R.forbidden()`: 创建权限不足响应
- `R.notFound()`: 创建资源不存在响应
- `R.error()`: 创建服务器内部错误响应
- `isSuccess()`: 判断响应是否成功

#### 使用示例
```java
// 成功响应，无数据
return R.ok();

// 成功响应，带数据
return R.ok(user);

// 成功响应，自定义消息和数据
return R.ok("登录成功", tokenInfo);

// 失败响应
return R.failed("用户名或密码错误");

// 参数错误响应
return R.paramError("用户名不能为空");

// 判断响应是否成功
R<?> result = userService.getUserById(id);
if(!result.isSuccess()) {
    throw new BusinessException(result.getMessage());
}
```

### 3.2 响应码 (ResultCode.java)

#### 说明
预定义的系统响应状态码枚举，实现`IResultCode`接口。

#### 常用响应码
- `SUCCESS(200, "操作成功")`: 成功
- `FAILURE(500, "操作失败")`: 通用失败
- `PARAM_ERROR(400, "请求参数错误")`: 参数错误
- `UNAUTHORIZED(401, "未授权")`: 未授权
- `FORBIDDEN(403, "权限不足")`: 权限不足
- `NOT_FOUND(404, "资源不存在")`: 资源不存在
- `INTERNAL_SERVER_ERROR(500, "服务器内部错误")`: 服务器内部错误
- 其他业务响应码: 1001-9999

### 3.3 异常处理

#### BaseException
系统基础异常类，所有自定义异常的父类。

#### BusinessException
业务异常类，用于业务逻辑校验失败时抛出。

#### GlobalExceptionHandler
全局异常处理器，统一捕获系统中的各类异常并转换为规范的API响应。

#### 使用示例
```java
// 抛出业务异常
if (user == null) {
    throw new BusinessException("用户不存在");
}

// 带响应码的异常
if (balance < amount) {
    throw new BusinessException(ResultCode.PARAM_ERROR, "余额不足");
}
```

### 3.4 工具类

#### DateTimeUtils
日期时间处理工具类，提供各种日期时间格式化、解析和计算功能。

#### StringUtils
字符串处理工具类，提供字符串判空、截取、格式化等常用操作。

#### PasswordUtil
密码处理工具类，提供密码加密和验证功能。

## 四、使用规范

### 4.1 统一响应规范
1. 所有Controller方法必须返回`R<T>`类型
2. 成功响应统一使用`R.ok()`系列方法
3. 失败响应统一使用`R.failed()`系列方法或其他预定义响应方法

### 4.2 异常处理规范
1. 不允许直接抛出`RuntimeException`，应使用`BusinessException`
2. 捕获的异常必须记录日志，不允许吞掉异常
3. 在Service层应捕获并转换第三方异常为业务异常
4. 参数校验失败应抛出`BusinessException`，并使用`PARAM_ERROR`响应码

### 4.3 工具类使用规范
1. 优先使用common模块提供的工具类，避免重复造轮子
2. 如需扩展工具类功能，应在common模块中添加，并补充单元测试和文档

## 五、开发进度

| 功能模块 | 完成度 | 说明 |
|---------|-------|------|
| API响应格式 | 100% | 已完成 |
| 异常处理 | 100% | 已完成 |
| Swagger配置 | 100% | 已完成 |
| 通用工具类 | 80% | 基本完成，可能需要后续扩展 |
| 安全相关工具 | 50% | 仅完成基础密码工具，JWT等待实现 |

## 六、未来计划

1. 添加更多实用工具类
   - 文件处理工具
   - JSON工具类封装
   - 加解密工具类

2. 增强异常处理功能
   - 支持多语言异常消息
   - 异常记录日志增强

3. 安全组件增强
   - 添加JWT工具类
   - 添加权限检查工具

## 七、注意事项

1. common模块变更需谨慎，可能影响所有服务
2. 工具类应保持高性能和线程安全
3. 新增功能必须编写完整的单元测试
4. 修改已有功能需确保向下兼容 