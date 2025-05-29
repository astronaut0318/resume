# 简历系统API接口文档

## 统一响应格式

所有API接口均使用统一的响应格式：

```json
{
  "code": 200,       // 状态码：200成功，其他表示失败
  "message": "操作成功", // 消息描述
  "data": {}         // 返回数据，可能是对象、数组或null
}
```

## 错误码说明

| 错误码 | 说明 |
| ----- | ---- |
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 1. 用户服务 (User Service)

### 1.1 用户注册

- **URL**: `/api/users/register`
- **方法**: POST
- **描述**: 注册新用户
- **请求参数**:

```json
{
  "username": "zhangsan",
  "password": "Pssword123",
  "email": "zhangsan@example.com",
  "phone": "13800138000"
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "username": "zhangsan"
  }
}
```

### 1.2 获取用户信息

- **URL**: `/api/users/{userId}`
- **方法**: GET
- **描述**: 获取用户信息
- **请求参数**: 路径参数userId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "avatar": "http://example.com/avatar.jpg",
    "role": 0,
    "status": 1,
    "createTime": "2023-01-01 12:00:00"
  }
}
```

### 1.3 更新用户信息

- **URL**: `/api/users/{userId}`
- **方法**: PUT
- **描述**: 更新用户信息
- **请求参数**:

```json
{
  "email": "new_email@example.com",
  "phone": "13900139000",
  "avatar": "http://example.com/new_avatar.jpg"
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 1.4 获取用户详细信息

- **URL**: `/api/users/{userId}/details`
- **方法**: GET
- **描述**: 获取用户详细个人资料
- **请求参数**: 路径参数userId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "realName": "张三",
    "gender": 1,
    "birthday": "1990-01-01",
    "education": "本科",
    "workYears": 5,
    "address": "北京市朝阳区",
    "profile": "个人简介...",
    "createTime": "2023-01-01 12:00:00"
  }
}
```

### 1.5 更新用户详细信息

- **URL**: `/api/users/{userId}/details`
- **方法**: PUT
- **描述**: 更新用户详细个人资料
- **请求参数**:

```json
{
  "realName": "张三",
  "gender": 1,
  "birthday": "1990-01-01",
  "education": "硕士",
  "workYears": 6,
  "address": "上海市浦东新区",
  "profile": "新的个人简介..."
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 1.6 查询VIP会员状态

- **URL**: `/api/users/{userId}/vip`
- **方法**: GET
- **描述**: 查询用户VIP会员状态
- **请求参数**: 路径参数userId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "isVip": true,
    "level": 2,
    "startTime": "2023-01-01 00:00:00",
    "endTime": "2023-12-31 23:59:59",
    "remainingDays": 300
  }
}
```

## 2. 认证服务 (Auth Service)

### 2.1 用户登录

- **URL**: `/api/auth/login`
- **方法**: POST
- **描述**: 用户登录并获取令牌
- **请求参数**:

```json
{
  "username": "zhangsan",
  "password": "password123"
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "zhangsan",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

### 2.2 刷新令牌

- **URL**: `/api/auth/refresh`
- **方法**: POST
- **描述**: 刷新访问令牌
- **请求参数**:

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

### 2.3 退出登录

- **URL**: `/api/auth/logout`
- **方法**: POST
- **描述**: 用户退出登录
- **请求参数**: 无
- **响应结果**:

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

### 2.4 修改密码

- **URL**: `/api/auth/password`
- **方法**: PUT
- **描述**: 修改用户密码
- **请求参数**:

```json
{
  "oldPassword": "password123",
  "newPassword": "newPassword456"
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

