# 认证授权服务模块

## 模块说明

认证授权服务(Auth Service)是整个简历系统的核心安全模块，负责用户认证、授权管理、令牌处理等功能。该模块基于Spring Security和JWT实现，为整个系统提供统一的身份认证和访问控制机制。

## 功能特点

1. **用户认证**：支持用户名密码登录认证
2. **令牌管理**：基于JWT的令牌生成、验证和刷新
3. **权限控制**：细粒度的RBAC(基于角色的访问控制)权限模型
4. **安全防护**：防止常见Web安全威胁，如CSRF、XSS等

## 模块结构

```
auth/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ptu/resume/auth/
│   │   │       ├── config/           # 配置类
│   │   │       ├── controller/       # 控制器
│   │   │       ├── dto/              # 数据传输对象
│   │   │       ├── entity/           # 实体类
│   │   │       ├── mapper/           # MyBatis映射器
│   │   │       ├── service/          # 服务接口及实现
│   │   │       │   └── impl/         # 服务实现类
│   │   │       ├── util/             # 工具类
│   │   │       ├── vo/               # 视图对象
│   │   │       └── AuthApplication.java  # 启动类
│   │   └── resources/
│   │       ├── mapper/               # MyBatis XML映射文件
│   │       ├── db/                   # 数据库初始化脚本
│   │       ├── application.yml       # 应用配置
│   │       └── bootstrap.yml         # 引导配置
```

## 数据流

1. 用户通过Gateway网关访问Auth服务进行登录
2. Auth服务验证用户凭证并生成JWT令牌
3. JWT令牌返回给客户端并存储在Redis中
4. 用户后续请求携带JWT令牌访问其他服务
5. Gateway网关或其他服务通过Auth服务验证令牌有效性

## 技术栈

- Spring Boot 2.6.x
- Spring Security
- JWT (JSON Web Token)
- MyBatis Plus
- Redis
- MySQL

## API接口

### 认证相关

- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册
- `POST /auth/refresh` - 刷新令牌
- `POST /auth/logout` - 用户登出
- `GET /auth/user/info` - 获取当前用户信息

### 权限相关

- `GET /auth/roles` - 获取角色列表
- `POST /auth/roles/assign` - 分配角色
- `GET /auth/permissions` - 获取权限列表
- `POST /auth/permissions/assign` - 分配权限

## 依赖关系

- 依赖公共模块: core, web, service, security
- 被网关模块和其他业务模块依赖

## 部署信息

- 默认端口: 8081
- 服务名称: auth-service

## 开发进度

- [x] 用户登录认证
- [x] 令牌生成与验证
- [x] 角色管理
- [x] 权限管理
- [ ] OAuth2集成
- [ ] 第三方登录
