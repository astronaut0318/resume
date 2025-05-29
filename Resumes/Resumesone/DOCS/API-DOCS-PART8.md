## 8. 通知服务 (Notification Service)

### 8.1 获取通知列表

- **URL**: `/api/notifications`
- **方法**: GET
- **描述**: 获取当前用户的通知列表
- **请求参数**: 
  - isRead: 是否已读 (可选，0-未读，1-已读)
  - type: 通知类型 (可选，1-系统通知，2-订单通知，3-其他)
  - page: 页码 (默认1)
  - size: 每页大小 (默认10)
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "list": [
      {
        "id": 1,
        "title": "订单支付成功",
        "content": "您的订单2023011012345678已支付成功",
        "type": 2,
        "isRead": 0,
        "createTime": "2023-01-10 12:30:00"
      },
      {
        "id": 2,
        "title": "系统公告",
        "content": "系统将于2023年1月15日进行维护升级",
        "type": 1,
        "isRead": 1,
        "createTime": "2023-01-11 12:00:00"
      }
    ]
  }
}
```
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