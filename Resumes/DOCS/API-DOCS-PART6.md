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

