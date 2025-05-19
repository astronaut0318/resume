## 8. 通知服务 (Notification Service) - 续

### 8.2 获取通知详情

- **URL**: `/api/notifications/{notificationId}`
- **方法**: GET
- **描述**: 获取通知详情
- **请求参数**: 路径参数notificationId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "title": "订单支付成功",
    "content": "您的订单2023011012345678已支付成功，您现在可以使用该模板创建简历了。",
    "type": 2,
    "isRead": 0,
    "createTime": "2023-01-10 12:30:00"
  }
}
```

### 8.3 标记通知为已读

- **URL**: `/api/notifications/{notificationId}/read`
- **方法**: PUT
- **描述**: 标记指定通知为已读
- **请求参数**: 路径参数notificationId
- **响应结果**:

```json
{
  "code": 200,
  "message": "标记成功",
  "data": null
}
```

### 8.4 标记所有通知为已读

- **URL**: `/api/notifications/read-all`
- **方法**: PUT
- **描述**: 标记所有通知为已读
- **请求参数**: 无
- **响应结果**:

```json
{
  "code": 200,
  "message": "标记成功",
  "data": null
}
```

### 8.5 删除通知

- **URL**: `/api/notifications/{notificationId}`
- **方法**: DELETE
- **描述**: 删除指定通知
- **请求参数**: 路径参数notificationId
- **响应结果**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 8.6 获取未读通知数量

- **URL**: `/api/notifications/unread-count`
- **方法**: GET
- **描述**: 获取未读通知数量
- **请求参数**: 无
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "count": 3
  }
}
```

### 8.7 发送邮件通知

- **URL**: `/api/notifications/email`
- **方法**: POST
- **描述**: 发送邮件通知（仅管理员可用）
- **请求参数**:

```json
{
  "userIds": [1, 2, 3],  // 接收用户ID列表，为空则发送给所有用户
  "subject": "系统升级通知",
  "content": "尊敬的用户，系统将于2023年1月15日进行维护升级，届时系统将暂停服务2小时。",
  "saveToSystem": true   // 是否同时保存到系统通知
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "发送成功",
  "data": {
    "successCount": 3,
    "failCount": 0
  }
}
```

## 9. 网关API (Gateway API)

### 9.1 获取系统服务状态

- **URL**: `/api/status`
- **方法**: GET
- **描述**: 获取各微服务的运行状态
- **请求参数**: 无
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "services": [
      {
        "name": "user-service",
        "status": "UP",
        "instances": 2
      },
      {
        "name": "auth-service",
        "status": "UP",
        "instances": 2
      },
      {
        "name": "resume-service",
        "status": "UP",
        "instances": 2
      },
      {
        "name": "template-service",
        "status": "UP",
        "instances": 2
      },
      {
        "name": "order-service",
        "status": "UP",
        "instances": 2
      },
      {
        "name": "ai-service",
        "status": "UP",
        "instances": 1
      },
      {
        "name": "file-service",
        "status": "UP",
        "instances": 2
      },
      {
        "name": "notification-service",
        "status": "UP",
        "instances": 2
      }
    ]
  }
}
```

### 9.2 刷新配置

- **URL**: `/api/config/refresh`
- **方法**: POST
- **描述**: 刷新指定服务的配置（仅管理员可用）
- **请求参数**:

```json
{
  "service": "user-service"  // 服务名，不传则刷新所有服务
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "刷新成功",
  "data": null
}
```

## 10. 管理员API (Admin API)

### 10.1 用户管理

#### 10.1.1 获取用户列表

- **URL**: `/api/admin/users`
- **方法**: GET
- **描述**: 获取用户列表
- **请求参数**: 
  - username: 用户名关键词 (可选)
  - email: 邮箱关键词 (可选)
  - role: 角色 (可选，0-普通用户，1-VIP用户，2-管理员)
  - status: 状态 (可选，0-禁用，1-正常)
  - page: 页码 (默认1)
  - size: 每页大小 (默认10)
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "username": "zhangsan",
        "email": "zhangsan@example.com",
        "phone": "13800138000",
        "role": 0,
        "status": 1,
        "createTime": "2023-01-01 12:00:00"
      },
      {
        "id": 2,
        "username": "lisi",
        "email": "lisi@example.com",
        "phone": "13800138001",
        "role": 1,
        "status": 1,
        "createTime": "2023-01-02 12:00:00"
      }
    ]
  }
}
```

#### 10.1.2 禁用/启用用户

- **URL**: `/api/admin/users/{userId}/status`
- **方法**: PUT
- **描述**: 禁用或启用用户
- **请求参数**:

```json
{
  "status": 0  // 0-禁用，1-启用
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 10.1.3 修改用户角色

- **URL**: `/api/admin/users/{userId}/role`
- **方法**: PUT
- **描述**: 修改用户角色
- **请求参数**:

```json
{
  "role": 1  // 0-普通用户，1-VIP用户，2-管理员
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 10.2 模板管理

#### 10.2.1 创建模板分类

- **URL**: `/api/admin/template-categories`
- **方法**: POST
- **描述**: 创建模板分类
- **请求参数**:

```json
{
  "name": "科技风格",
  "sort": 4
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 4
  }
}
```