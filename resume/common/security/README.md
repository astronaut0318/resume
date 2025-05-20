# 公共安全模块

## 模块说明
公共安全模块提供了简历系统的安全相关功能，包括JWT认证、权限控制、密码加密等安全机制。该模块为系统提供统一的安全解决方案，确保系统的安全性和可靠性。

## 目录结构
```
security/
├── config/                # 安全配置
│   └── WebSecurityConfig.java  # Web安全配置类
├── utils/                 # 安全工具类
│   └── JwtUtils.java        # JWT工具类
└── annotation/            # 安全注解
    └── RequiresPermission.java  # 权限要求注解
```

## 核心功能

### 1. JWT认证
系统使用JWT（JSON Web Token）进行用户认证，主要功能包括：
- 生成JWT令牌
- 解析JWT令牌
- 验证JWT令牌有效性
- 刷新JWT令牌

### 2. 安全配置
`WebSecurityConfig`类提供了Spring Security的配置，包括：
- 配置不需要认证的URL
- 配置密码编码器
- 禁用SESSION
- 配置无状态认证
- 配置权限控制

### 3. 安全工具
系统提供了多种安全工具类，简化安全开发：
- `JwtUtils` - JWT工具类，提供JWT令牌的生成、解析和验证功能

## 依赖说明
该模块依赖于core模块，同时被其他业务模块依赖。

## 使用示例

### JWT工具使用示例
```java
// 注入JwtUtils
@Autowired
private JwtUtils jwtUtils;

// 生成令牌
String token = jwtUtils.generateToken(userId);

// 从令牌中获取用户ID
Long userId = jwtUtils.getUserIdFromToken(token);

// 验证令牌有效性
Boolean valid = jwtUtils.validateToken(token);

// 刷新令牌
String newToken = jwtUtils.refreshToken(token);
```

### 安全配置使用示例
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfig {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 调用父类配置
        super.configure(http);
        
        // 添加额外的安全配置
        http.authorizeRequests()
            .antMatchers("/api/public/**").permitAll()
            .antMatchers("/api/admin/**").hasRole("ADMIN");
    }
}
```

## 安全最佳实践
1. 始终使用HTTPS协议
2. JWT令牌有效期不宜过长，建议不超过24小时
3. 敏感操作应该二次验证
4. 密码必须加密存储，推荐使用BCrypt算法
5. 定期轮换密钥
6. 实施请求频率限制，防止暴力攻击
7. 记录安全相关日志，便于审计
8. 定期检查依赖的安全漏洞