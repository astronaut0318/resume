## 10. 管理员API (Admin API)

### 10.1 用户管理

#### 10.1.1 获取用户列表

- **URL**: `/api/admin/users`
- **方法**: GET
- **描述**: 获取用户列表
- **请求参数**: 
  - username: 用户名关键词 (可选)
  - email: 邮箱关键词 (可选)
  - role: 角色 (可选， 0-普通用户，1-VIP用户，2-终身会员，3-管理员)
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
  "role": 1  // 0-普通用户，1-VIP用户，2-终身会员，3-管理员
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