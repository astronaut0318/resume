## 3. 简历管理服务 (Resume Service) - 续

### 3.2 获取简历列表

- **URL**: `/api/resumes`
- **方法**: GET
- **描述**: 获取当前用户的简历列表
- **请求参数**: 
  - page: 页码(默认1)
  - size: 每页大小(默认10)
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
        "title": "软件工程师简历",
        "status": 1,
        "isDefault": 1,
        "createTime": "2023-01-01 12:00:00",
        "updateTime": "2023-01-02 12:00:00"
      },
      {
        "id": 2,
        "title": "产品经理简历",
        "status": 0,
        "isDefault": 0,
        "createTime": "2023-01-03 12:00:00",
        "updateTime": "2023-01-03 12:00:00"
      }
    ]
  }
}
```

### 3.3 获取简历详情

- **URL**: `/api/resumes/{resumeId}`
- **方法**: GET
- **描述**: 获取简历详细信息
- **请求参数**: 路径参数resumeId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "title": "软件工程师简历",
    "templateId": 1,
    "content": {
      "basicInfo": {
        "name": "张三",
        "phone": "13800138000",
        "email": "zhangsan@example.com"
      },
      "education": [
        {
          "school": "北京大学",
          "major": "计算机科学",
          "degree": "本科",
          "startDate": "2016-09-01",
          "endDate": "2020-07-01"
        }
      ],
      "workExperience": [
        {
          "company": "某科技公司",
          "position": "Java开发工程师",
          "startDate": "2020-07-01",
          "endDate": "2023-01-01",
          "description": "负责后端系统开发与维护..."
        }
      ],
      "skills": [
        "Java", "Spring Boot", "MySQL", "Redis"
      ]
    },
    "status": 1,
    "isDefault": 1,
    "createTime": "2023-01-01 12:00:00",
    "updateTime": "2023-01-02 12:00:00"
  }
}
```

### 3.4 更新简历

- **URL**: `/api/resumes/{resumeId}`
- **方法**: PUT
- **描述**: 更新简历内容
- **请求参数**:

```json
{
  "title": "高级软件工程师简历",
  "templateId": 2,
  "content": {
    "basicInfo": {
      "name": "张三",
      "phone": "13800138000",
      "email": "zhangsan@example.com"
    },
    "education": [
      {
        "school": "北京大学",
        "major": "计算机科学",
        "degree": "本科",
        "startDate": "2016-09-01",
        "endDate": "2020-07-01"
      }
    ],
    "workExperience": [
      {
        "company": "某科技公司",
        "position": "高级Java开发工程师",
        "startDate": "2020-07-01",
        "endDate": "2023-01-01",
        "description": "负责系统架构设计与团队管理..."
      }
    ],
    "skills": [
      "Java", "Spring Cloud", "MySQL", "Redis", "Docker"
    ]
  }
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

### 3.5 删除简历

- **URL**: `/api/resumes/{resumeId}`
- **方法**: DELETE
- **描述**: 删除简历
- **请求参数**: 路径参数resumeId
- **响应结果**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 3.6 设置默认简历

- **URL**: `/api/resumes/{resumeId}/default`
- **方法**: PUT
- **描述**: 设置默认简历
- **请求参数**: 路径参数resumeId
- **响应结果**:

```json
{
  "code": 200,
  "message": "设置成功",
  "data": null
}
```

### 3.7 获取简历版本历史

- **URL**: `/api/resumes/{resumeId}/versions`
- **方法**: GET
- **描述**: 获取简历的版本历史
- **请求参数**: 路径参数resumeId
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "resumeId": 1,
      "version": 1,
      "createTime": "2023-01-01 12:00:00"
    },
    {
      "id": 2,
      "resumeId": 1,
      "version": 2,
      "createTime": "2023-01-02 12:00:00"
    }
  ]
}
```

### 3.8 获取指定版本的简历内容

- **URL**: `/api/resumes/{resumeId}/versions/{version}`
- **方法**: GET
- **描述**: 获取指定版本的简历内容
- **请求参数**: 
  - 路径参数resumeId
  - 路径参数version
- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "resumeId": 1,
    "version": 1,
    "content": {
      "basicInfo": {
        "name": "张三",
        "phone": "13800138000",
        "email": "zhangsan@example.com"
      },
      "education": [...],
      "workExperience": [...],
      "skills": [...]
    },
    "createTime": "2023-01-01 12:00:00"
  }
}
```

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
```