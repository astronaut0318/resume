## 4. 模板服务 (Template Service)

### 4.1 获取模板分类列表

- **URL**: `/api/templates/categories`
- **方法**: GET
- **描述**: 获取模板分类列表
- **请求参数**: 无
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "简约风格",
      "sort": 1,
      "status": 1
    },
    {
      "id": 2,
      "name": "商务风格",
      "sort": 2,
      "status": 1
    },
    {
      "id": 3,
      "name": "创意风格",
      "sort": 3,
      "status": 1
    }
  ]
}
```

### 4.2 获取模板列表

- **URL**: `/api/templates`
- **方法**: GET
- **描述**: 获取模板列表，支持分页和筛选
- **请求参数**: 
  - categoryId: 分类ID (可选)
  - isFree: 是否免费 (可选，0-付费，1-免费)
  - keyword: 搜索关键词 (可选)
  - page: 页码 (默认1)
  - size: 每页大小 (默认10)
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 20,
    "list": [
      {
        "id": 1,
        "categoryId": 1,
        "name": "简约风格简历模板",
        "thumbnail": "http://example.com/thumb1.jpg",
        "price": 0.00,
        "isFree": 1,
        "downloads": 1000,
        "description": "简约大气的简历模板",
        "status": 1
      },
      {
        "id": 2,
        "categoryId": 2,
        "name": "商务风格简历模板",
        "thumbnail": "http://example.com/thumb2.jpg",
        "price": 19.90,
        "isFree": 0,
        "downloads": 500,
        "description": "专业商务风格简历模板",
        "status": 1
      }
    ]
  }
}
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