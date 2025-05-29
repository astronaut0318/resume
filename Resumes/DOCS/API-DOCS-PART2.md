## 3. 简历管理服务 (Resume Service)

### 3.1 创建简历

- **URL**: `/api/resumes`
- **方法**: POST
- **描述**: 创建新简历
- **请求参数**:

```json
{
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
  }
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "resumeId": 1
  }
}
```
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