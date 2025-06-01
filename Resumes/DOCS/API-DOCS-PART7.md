
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
上传文件 /api/files/upload
这个API用于处理文件上传，主要应用场景包括：
用户上传头像
上传简历附件
上传证书图片等
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
获取文件列表 /api/files
这个API用于获取用户上传的文件列表，主要用于：
查看用户上传的所有文件
管理和重用已上传的文件
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
删除文件 /api/files/{fileId}
这个API用于删除指定的文件，主要用于：
删除不需要的头像
删除过期或不需要的文件
管理用户存储空间
```


```