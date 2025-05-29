## 5. 订单支付服务 (Order Service)

### 5.1 创建订单

- **URL**: `/api/orders`
- **方法**: POST
- **描述**: 创建模板购买订单
- **请求参数**:

```json
{
  "templateId": 2,
  "payType": 1  // 1-支付宝，2-微信
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "orderNo": "2023011012345678",
    "amount": 19.90,
    "payUrl": "https://pay.example.com/pay?orderNo=2023011012345678"
  }
}
```

### 5.2 获取订单列表

- **URL**: `/api/orders`
- **方法**: GET
- **描述**: 获取当前用户的订单列表
- **请求参数**: 
  - status: 订单状态 (可选，0-待支付，1-已支付，2-已取消)
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
        "orderNo": "2023011012345678",
        "templateId": 2,
        "templateName": "商务风格简历模板",
        "amount": 19.90,
        "status": 1,
        "payType": 1,
        "payTime": "2023-01-10 12:30:00",
        "createTime": "2023-01-10 12:00:00"
      },
      {
        "orderNo": "2023011112345678",
        "templateId": 3,
        "templateName": "创意风格简历模板",
        "amount": 29.90,
        "status": 0,
        "payType": null,
        "payTime": null,
        "createTime": "2023-01-11 12:00:00"
      }
    ]
  }
}
```

### 5.3 获取订单详情

- **URL**: `/api/orders/{orderNo}`
- **方法**: GET
- **描述**: 获取订单详细信息
- **请求参数**: 路径参数orderNo
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "2023011012345678",
    "userId": 1,
    "templateId": 2,
    "templateName": "商务风格简历模板",
    "amount": 19.90,
    "status": 1,
    "payType": 1,
    "payTime": "2023-01-10 12:30:00",
    "createTime": "2023-01-10 12:00:00",
    "updateTime": "2023-01-10 12:30:00"
  }
}
```

### 5.4 取消订单

- **URL**: `/api/orders/{orderNo}/cancel`
- **方法**: PUT
- **描述**: 取消未支付的订单
- **请求参数**: 路径参数orderNo
- **响应结果**:

```json
{
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

### 5.5 支付回调

- **URL**: `/api/orders/notify/{payType}`
- **方法**: POST
- **描述**: 支付平台异步通知接口
- **请求参数**: 
  - payType: 支付方式 (1-支付宝，2-微信)
  - 其他参数根据支付平台要求
- **响应结果**: 根据支付平台要求的格式