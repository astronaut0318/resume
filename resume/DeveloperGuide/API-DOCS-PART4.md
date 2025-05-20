## 6. AI 辅助服务 (AI Service) - 续

### 6.2 内容优化建议

- **URL**: `/api/ai/resume/optimize`
- **方法**: POST
- **描述**: 获取简历内容优化建议
- **请求参数**:

```json
{
  "resumeId": 1,
  "section": "workExperience"  // 可选，不传则优化整个简历
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "suggestions": [
      {
        "section": "workExperience",
        "item": 0,
        "original": "负责后端系统开发与维护...",
        "suggestion": "设计并实现了高并发支付系统，日均处理交易10万+，提升系统性能30%；优化数据库查询，减少响应时间50%。"
      },
      {
        "section": "skills",
        "suggestion": "建议将技能分类展示，并标注熟练程度，如：后端开发（精通Java、Spring Boot），数据库（熟练MySQL、Redis）。"
      }
    ]
  }
}
```

### 6.3 职位匹配分析

- **URL**: `/api/ai/resume/match`
- **方法**: POST
- **描述**: 分析简历与目标职位的匹配度
- **请求参数**:

```json
{
  "resumeId": 1,
  "position": "高级Java开发工程师",
  "jobDescription": "1. 负责公司核心业务系统的设计和开发；\n2. 参与系统架构设计和技术选型；\n3. 解决系统中的技术难题和性能瓶颈；\n4. 技术要求：Java/Spring Cloud/MySQL/Redis/消息队列，5年以上经验。"
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "分析成功",
  "data": {
    "matchScore": 75,
    "analysis": "您的简历与目标职位匹配度为75%，基本满足职位要求。",
    "advantages": [
      "Java和Spring技术栈匹配度高",
      "有相关行业经验"
    ],
    "weaknesses": [
      "架构设计经验较少体现",
      "消息队列相关经验未突出"
    ],
    "suggestions": [
      "突出您在系统架构方面的经验",
      "增加消息队列相关项目经验描述",
      "强调解决复杂技术问题的案例"
    ]
  }
}
```

### 6.4 自动生成简历内容

- **URL**: `/api/ai/resume/generate`
- **方法**: POST
- **描述**: 根据用户提供的关键信息，自动生成简历内容
- **请求参数**:

```json
{
  "position": "Java开发工程师",
  "industry": "互联网",
  "workYears": 3,
  "keySkills": ["Java", "Spring Boot", "MySQL"],
  "education": {
    "school": "北京大学",
    "major": "计算机科学",
    "degree": "本科",
    "graduationYear": 2020
  }
}
```

- **响应结果**:

```json
{
  "code": 200,
  "message": "生成成功",
  "data": {
    "profile": "具有3年互联网行业Java开发经验，熟练掌握Java核心技术和Spring Boot框架，具备扎实的MySQL数据库设计和优化能力。善于解决复杂技术问题，具有良好的团队协作精神。",
    "workExperience": [
      {
        "company": "推荐公司名：某科技有限公司",
        "position": "Java开发工程师",
        "responsibilities": [
          "负责公司核心业务系统的开发和维护",
          "参与微服务架构设计和实现",
          "优化SQL查询，提升系统性能",
          "解决生产环境中的技术问题"
        ]
      }
    ],
    "projects": [
      {
        "name": "推荐项目：用户中心系统",
        "description": "基于Spring Boot的用户管理系统，实现用户注册、登录、授权等功能。",
        "highlights": [
          "使用Spring Security实现权限控制",
          "引入Redis缓存提升性能",
          "设计并实现分布式会话管理"
        ]
      }
    ]
  }
}
```

## 7. 文件服务 (File Service)

### 7.1 上传文件

- **URL**: `/api/files/upload`
- **方法**: POST
- **描述**: 上传文件
- **请求参数**: 使用multipart/form-data格式
  - file: 文件对象
  - type: 文件类型 (可选，如avatar、resume等)
- **响应结果**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "fileId": 1,
    "fileName": "example.jpg",
    "filePath": "http://example.com/files/example.jpg",
    "fileSize": 102400,
    "fileType": "image/jpeg"
  }
}
```

### 7.2 下载文件

- **URL**: `/api/files/{fileId}/download`
- **方法**: GET
- **描述**: 下载文件
- **请求参数**: 路径参数fileId
- **响应结果**: 文件二进制流

### 7.3 导出简历为PDF

- **URL**: `/api/files/resume/{resumeId}/pdf`
- **方法**: GET
- **描述**: 将简历导出为PDF格式
- **请求参数**: 路径参数resumeId
- **响应结果**: PDF文件二进制流，或者：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "downloadUrl": "http://example.com/files/resume_1.pdf"
  }
}
```

### 7.4 导出简历为Word

- **URL**: `/api/files/resume/{resumeId}/word`
- **方法**: GET
- **描述**: 将简历导出为Word格式
- **请求参数**: 路径参数resumeId
- **响应结果**: Word文件二进制流，或者：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "downloadUrl": "http://example.com/files/resume_1.docx"
  }
}
```

### 7.5 获取文件列表

- **URL**: `/api/files`
- **方法**: GET
- **描述**: 获取当前用户的文件列表
- **请求参数**: 
  - type: 文件类型 (可选)
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
        "originalName": "头像.jpg",
        "fileName": "avatar_1.jpg",
        "filePath": "http://example.com/files/avatar_1.jpg",
        "fileSize": 102400,
        "fileType": "image/jpeg",
        "createTime": "2023-01-01 12:00:00"
      },
      {
        "id": 2,
        "originalName": "简历.pdf",
        "fileName": "resume_1.pdf",
        "filePath": "http://example.com/files/resume_1.pdf",
        "fileSize": 204800,
        "fileType": "application/pdf",
        "createTime": "2023-01-02 12:00:00"
      }
    ]
  }
}
```

### 7.6 删除文件

- **URL**: `/api/files/{fileId}`
- **方法**: DELETE
- **描述**: 删除指定文件
- **请求参数**: 路径参数fileId
- **响应结果**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

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