# 公共服务模块

## 模块说明
公共服务模块提供了简历系统的通用服务组件，包括数据访问、缓存等服务组件。该模块为系统提供了统一的服务层支持，简化了各业务模块的开发。

## 目录结构
```
service/
├── config/                 # 配置类
│   └── RedisConfig.java      # Redis配置类
├── redis/                  # Redis服务
│   ├── RedisService.java     # Redis服务接口
│   └── impl/                 # 接口实现
│       └── RedisServiceImpl.java  # Redis服务实现类
```

## 核心功能

### 1. Redis服务
系统使用Spring Data Redis作为缓存实现，`RedisService`提供了以下功能：
- 常规的缓存操作（设置、获取、删除）
- 键值对过期时间控制
- Hash结构的操作
- List结构的操作
- Set结构的操作
- ZSet结构的操作

Spring Data Redis是一个轻量级的Redis客户端封装，对于简单的缓存场景非常合适。Redis配置类`RedisConfig`提供了适当的序列化配置，使得缓存对象可以正确地序列化和反序列化。

## 依赖说明
该模块依赖于core模块，同时被其他业务模块依赖。

## 使用示例

### Redis服务使用示例
```java
// 注入RedisService
@Autowired
private RedisService redisService;

// 设置缓存
redisService.set("user:1", userVO);

// 设置带过期时间的缓存（30分钟）
redisService.set("user:1", userVO, 1800);

// 获取缓存
UserVO user = redisService.get("user:1");

// 删除缓存
redisService.delete("user:1");

// 设置Hash
redisService.hSet("userMap", "1", userVO);

// 获取Hash
UserVO user = redisService.hGet("userMap", "1");
```

## 服务最佳实践
1. 合理设置缓存过期时间，避免缓存雪崩
2. 使用缓存前缀，区分不同业务领域的缓存
3. 对缓存进行分级，重要数据与非重要数据分开存储
4. 数据一致性要求高的场景，谨慎使用缓存
5. 考虑添加缓存穿透和缓存击穿的防护措施

### 2. 分布式锁
`DistributedLock`