# file模块开发指南

## 一、模块定位
file模块负责简历系统中模板、简历、头像三类文件的上传、下载、删除、列表、OnlyOffice保存等统一文件管理，底层集成MinIO对象存储。

## 二、技术栈
- Spring Boot 2.7.14
- Spring Cloud 2021.0.8
- MyBatis Plus 3.5.2
- MinIO 8.5.3
- Lombok 1.18.26
- Swagger 2.9.2
- Hutool 5.8.26

## 三、目录结构
```
file-service/
├── src/main/java/com/ptu/file/
│   ├── config/           # MinIO、Swagger等配置类
│   ├── controller/       # 文件API接口
│   ├── dto/              # 数据传输对象（OnlyOffice回调等）
│   ├── entity/           # 实体类（FileEntity）
│   ├── mapper/           # MyBatis Plus Mapper
│   ├── service/          # Service接口
│   ├── service/impl/     # Service实现
│   ├── util/             # 工具类（统一响应R等）
│   └── vo/               # 视图对象（FileVO、FileUploadVO等）
└── src/main/resources/
    └── file模块开发指南.md
```

## 四、功能说明
- 支持模板、简历、头像三类文件的上传、下载、删除、分页列表
- 支持OnlyOffice在线编辑保存回调
- 文件统一存储于MinIO，元数据入库MySQL
- 提供Swagger在线API文档

## 五、核心配置说明
- MinIO配置：application.yml中`minio.endpoint`、`minio.accessKey`、`minio.secretKey`、`minio.bucket.file`
- Swagger配置：访问`/swagger-ui.html`查看API文档

## 六、主要接口说明
- `POST   /api/files/upload`         文件上传（type: template/resume/avatar，bizId: 业务ID）
- `GET    /api/files/{fileId}/download` 文件下载
- `DELETE /api/files/{fileId}`       删除文件
- `GET    /api/files`                文件列表（type、bizId、userId、page、size）
- `POST   /api/files/onlyoffice/save` OnlyOffice保存回调

## 七、开发进度
- [x] 数据库表结构与实体类
- [x] DTO/VO对象
- [x] Service与实现
- [x] Controller与API接口
- [x] MinIO、Swagger配置
- [x] 统一响应类R
- [ ] 业务逻辑完善与联调

## 八、注意事项
- 仅支持模板、简历、头像三类文件类型，其他类型不予支持
- OnlyOffice保存由file模块统一处理
- 无安全校验，接口直接可用
- 统一响应格式，异常处理规范

## 九、后续计划
- 完善文件上传/下载/删除/回调等业务逻辑
- 与resume、template、document等模块联调
- 完善接口文档与单元测试 