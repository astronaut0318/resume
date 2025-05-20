# file 文件服务模块

## 模块说明
文件服务模块负责系统中所有文件的上传、下载、存储和管理功能，支持简历附件、用户头像、模板资源等各类文件的处理。

## 目录结构
```
file/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ptu/
│   │   │           └── resume/
│   │   │               └── file/
│   │   │                   ├── controller/   # 控制器层，处理HTTP请求
│   │   │                   ├── service/      # 服务接口层
│   │   │                   │   └── impl/     # 服务实现层
│   │   │                   ├── mapper/       # 数据访问层
│   │   │                   ├── entity/       # 实体类层
│   │   │                   ├── dto/          # 数据传输对象层
│   │   │                   ├── vo/           # 视图对象层
│   │   │                   ├── query/        # 查询对象层
│   │   │                   ├── config/       # 配置类层
│   │   │                   ├── storage/      # 存储实现
│   │   │                   │   ├── local/    # 本地存储
│   │   │                   │   ├── oss/      # 阿里云OSS
│   │   │                   │   └── minio/    # MinIO
│   │   │                   ├── processor/    # 文件处理器
│   │   │                   └── util/         # 工具类
│   │   └── resources/
│   │       ├── mapper/      # MyBatis映射文件
```

## 功能清单
- 文件上传
  - 单文件上传
  - 多文件上传
  - 大文件分片上传
  - 断点续传
- 文件下载
  - 普通下载
  - 流式下载
  - 批量下载
  - 限速下载
- 文件管理
  - 文件信息查询
  - 文件删除
  - 文件移动
  - 文件重命名
- 存储策略
  - 本地存储
  - 阿里云OSS存储
  - MinIO存储
  - 混合存储
- 文件处理
  - 图片压缩
  - 水印添加
  - 格式转换
  - 文档预览

## 数据表设计
- file_info：文件信息表
- file_chunk：文件分片信息表
- file_storage：存储策略配置表
- file_process：文件处理记录表
- file_download：文件下载记录表

## 技术栈
- Spring Boot
- MyBatis Plus
- MinIO/OSS/S3 (对象存储服务)
- Spring Cache

## 数据流
1. 客户端通过API请求上传/下载文件
2. 控制器层接收请求并校验
3. 服务层处理业务逻辑
4. 通过对象存储服务进行实际文件操作
5. 文件元数据存储在数据库中

## 依赖服务
- 依赖 `common` 模块中的 `core` 组件
- 依赖 `common` 模块中的 `web` 组件
- 依赖 `common` 模块中的 `service` 组件
- 依赖 MinIO、阿里云OSS等存储SDK
- 依赖图片处理、文档处理相关库

## 接口说明
- POST /files/upload：上传文件
- POST /files/upload/chunk：分片上传
- POST /files/upload/merge：合并分片
- GET /files/{id}：下载文件
- GET /files/{id}/info：获取文件信息
- DELETE /files/{id}：删除文件
- PUT /files/{id}/name：重命名文件
- POST /files/process/image：处理图片
- GET /files/preview/{id}：预览文件

## 开发进度
- [x] 基础框架配置
- [x] 基本文件上传下载
- [ ] 分片上传功能
- [ ] 多存储策略支持
- [ ] 文件处理功能
- [ ] 文件预览功能 