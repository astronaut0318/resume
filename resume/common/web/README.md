# 公共Web模块

## 模块说明
公共Web模块提供了简历系统的Web应用通用组件，包括全局异常处理、日志处理、跨域配置等功能。该模块为系统提供了统一的Web层支持，简化了各业务模块的开发，保证了系统的一致性和可维护性。

## 目录结构
```
web/
├── handler/                # 处理器
│   └── GlobalExceptionHandler.java  # 全局异常处理器
├── interceptor/            # 拦截器
│   ├── LogInterceptor.java   # 日志拦截器
│   └── AuthInterceptor.java  # 认证拦截器
├── config/                 # 配置类
│   ├── WebMvcConfig.java     # Spring MVC配置
│   └── CorsConfig.java       # 跨域配置
└── aspect/                 # 切面
    ├── LogAspect.java        # 日志切面
    └── ApiLimitAspect.java   # 接口限流切面
```

## 核心功能

### 1. 全局异常处理
系统通过`GlobalExceptionHandler`统一处理异常，主要功能包括：
- 业务异常处理
- 参数校验异常处理
- 系统异常处理
- 未授权异常处理
- 接口统一响应格式

### 2. 日志处理
系统通过日志拦截器和切面实现统一的日志记录，主要功能包括：
- 请求日志记录
- 操作日志记录
- 异常日志记录
- 性能监控日志

### 3. 跨域配置
`CorsConfig`提供了统一的跨域配置，允许前后端分离架构下的跨域请求。

### 4. 接口限流
`ApiLimitAspect`提供了基于注解的接口限流功能，防止接口被恶意调用。

## 依赖说明
该模块依赖于core模块和security模块，同时被其他业务模块依赖。

## 使用示例

### 全局异常处理示例
全局异常处理器会自动捕获并处理系统中的异常，无需手动调用。

```java
// 抛出业务异常，将被GlobalExceptionHandler捕获并处理
throw new BusinessException("用户名已存在");

// 参数校验
@PostMapping("/create")
public R<UserVO> createUser(@Valid @RequestBody UserDTO userDTO) {
    // 如果参数校验失败，将由GlobalExceptionHandler处理
    return R.ok(userService.createUser(userDTO));
}
```

### 日志切面使用示例
```java
// 在需要记录操作日志的方法上添加注解
@OperationLog(description = "创建用户")
@PostMapping("/create")
public R<UserVO> createUser(@RequestBody UserDTO userDTO) {
    return R.ok(userService.createUser(userDTO));
}
```

### 接口限流使用示例
```java
// 限制接口1分钟内最多允许10次调用
@ApiLimit(value = 10, time = 60)
@GetMapping("/sensitive-data")
public R<Object> getSensitiveData() {
    return R.ok(dataService.getSensitiveData());
}
```

## Web开发最佳实践
1. 统一使用RESTful风格设计API
2. 请求和响应使用JSON格式
3. 接口参数必须进行校验
4. 敏感接口添加限流保护
5. 接口调用必须记录日志
6. 异常信息不要直接返回给前端
7. 统一使用R泛型类封装响应结果