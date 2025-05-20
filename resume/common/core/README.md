# 公共核心模块

## 模块说明
公共核心模块提供了整个简历系统的基础工具类、通用常量、异常处理机制和公共数据模型等核心功能。该模块被所有其他模块依赖，为整个系统提供基础支持。

## 目录结构
```
core/
├── constants/             # 常量定义
│   └── CommonConstants.java  # 系统通用常量
├── exception/             # 全局异常类
│   ├── BaseException.java    # 基础异常类
│   └── BusinessException.java  # 业务异常类
├── model/                 # 公共数据模型
│   ├── R.java               # 统一响应结果
│   └── PageResult.java      # 分页结果封装
└── utils/                 # 工具类
    ├── DateUtils.java       # 日期工具类
    └── StringUtils.java     # 字符串工具类
```

## 核心功能

### 1. 统一响应模型
系统使用`R<T>`类作为统一的响应模型，封装响应码、响应消息和响应数据。主要方法包括：
- `R.ok()` - 返回成功响应，无数据
- `R.ok(T data)` - 返回成功响应，携带数据
- `R.failed(String msg)` - 返回失败响应，携带错误消息
- `R.failed(Integer code, String msg)` - 返回失败响应，携带错误码和错误消息

### 2. 异常处理机制
系统定义了两类主要异常：
- `BaseException` - 基础异常类，所有自定义异常的父类
- `BusinessException` - 业务异常类，用于处理业务逻辑异常

### 3. 工具类
系统提供了多种工具类，简化开发：
- `DateUtils` - 日期处理工具类，提供日期格式化、解析及计算功能
- `StringUtils` - 字符串处理工具类，提供字符串判空、UUID生成、驼峰/下划线命名转换等功能

### 4. 常量定义
`CommonConstants`类定义了系统中使用的常量，包括：
- 成功/失败标记
- 删除/状态标记
- 验证码前缀
- 默认过期时间
- 分页参数
- 用户角色

## 依赖说明
该模块为基础模块，不依赖其他模块，但被所有其他模块依赖。

## 使用示例

### 统一响应示例
```java
// 成功响应
R<UserVO> result = R.ok(userVO);

// 带消息的成功响应
R<UserVO> result = R.ok(userVO, "获取用户信息成功");

// 失败响应
R<Void> result = R.failed("用户不存在");

// 带错误码的失败响应
R<Void> result = R.failed(404, "用户不存在");
```

### 异常处理示例
```java
// 抛出业务异常
if (user == null) {
    throw new BusinessException("用户不存在");
}

// 带错误码的业务异常
if (balance < amount) {
    throw new BusinessException(400, "余额不足");
}
```

### 工具类使用示例
```java
// 日期工具类
String now = DateUtils.getCurrentDateTime();
String formatted = DateUtils.format(date, DateUtils.PATTERN_DATE);
Date date = DateUtils.parse("2023-01-01", DateUtils.PATTERN_DATE);

// 字符串工具类
boolean empty = StringUtils.isEmpty(str);
boolean valid = StringUtils.isValidEmail("test@example.com");
String uuid = StringUtils.uuid();
```