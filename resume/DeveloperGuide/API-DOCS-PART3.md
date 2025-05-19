## 4. 模板服务 (Template Service) - 续

### 4.3 获取模板详情

- **URL**: `/api/templates/{templateId}`
- **方法**: GET
- **描述**: 获取模板详细信息
- **请求参数**: 路径参数templateId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "categoryId": 1,
    "name": "简约风格简历模板",
    "thumbnail": "http://example.com/thumb1.jpg",
    "filePath": "http://example.com/templates/template1.html",
    "price": 0.00,
    "isFree": 1,
    "downloads": 1000,
    "description": "简约大气的简历模板，适合各行各业求职者使用。简洁明了的布局设计，突出个人能力和经验。",
    "status": 1,
    "createTime": "2023-01-01 12:00:00",
    "updateTime": "2023-01-01 12:00:00"
  }
}
```

### 4.4 收藏模板

- **URL**: `/api/templates/{templateId}/collect`
- **方法**: POST
- **描述**: 收藏模板
- **请求参数**: 路径参数templateId
- **响应结果**:

```json
{
  "code": 200,
  "message": "收藏成功",
  "data": null
}
```

### 4.5 取消收藏

- **URL**: `/api/templates/{templateId}/collect`
- **方法**: DELETE
- **描述**: 取消收藏模板
- **请求参数**: 路径参数templateId
- **响应结果**:

```json
{
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

### 4.6 获取收藏列表

- **URL**: `/api/templates/collections`
- **方法**: GET
- **描述**: 获取当前用户收藏的模板列表
- **请求参数**: 
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
        "templateId": 1,
        "name": "简约风格简历模板",
        "thumbnail": "http://example.com/thumb1.jpg",
        "price": 0.00,
        "isFree": 1,
        "collectTime": "2023-01-10 12:00:00"
      },
      {
        "id": 2,
        "templateId": 3,
        "name": "创意风格简历模板",
        "thumbnail": "http://example.com/thumb3.jpg",
        "price": 29.90,
        "isFree": 0,
        "collectTime": "2023-01-12 12:00:00"
      }
    ]
  }
}
```

### 4.7 分享模板

- **URL**: `/api/templates/{templateId}/share`
- **方法**: GET
- **描述**: 生成模板分享链接
- **请求参数**: 路径参数templateId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "shareUrl": "http://example.com/share/template?code=abcdef123456",
    "expireTime": "2023-01-20 12:00:00"
  }
}
```

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

## 6. AI 辅助服务 (AI Service)

### 6.1 简历分析

- **URL**: `/api/ai/resume/analyze`
- **方法**: POST
- **描述**: 分析简历内容并给出评分
- **请求参数**:

```json
{
  "resumeId": 1
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "分析成功",
  "data": {
    "score": 85,
    "analysis": {
      "overall": "简历整体质量良好，但仍有改进空间。",
      "basicInfo": "基本信息完整。",
      "education": "教育经历详实，但可以添加GPA或相关荣誉。",
      "workExperience": "工作经历描述可以更加量化，突出成果。",
      "skills": "技能描述较为笼统，建议具体化。"
    }
  }
}
```