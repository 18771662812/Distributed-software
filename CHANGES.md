# 项目修改总结

## 📋 修改清单

### 一、后端代码修改

#### 1. pom.xml - 添加依赖
**位置**：`shop/pom.xml`
**修改内容**：
- 添加 `spring-boot-starter-data-redis` - Redis 支持
- 添加 `jedis` - Redis 连接池
- 添加 `jackson-databind` - JSON 序列化

#### 2. RedisConfig.java - Redis 配置类（新增）
**位置**：`shop/src/main/java/org/example/shop/config/RedisConfig.java`
**功能**：
- 配置 RedisTemplate
- 设置 JSON 序列化方式
- 配置 String 和 Hash 的序列化

#### 3. CacheUtil.java - 缓存工具类（新增）
**位置**：`shop/src/main/java/org/example/shop/util/CacheUtil.java`
**核心功能**：
- `get()` - 获取缓存
- `set()` - 设置缓存（防雪崩：随机过期时间）
- `setNull()` - 设置空值缓存（防穿透）
- `isNull()` - 检查空值缓存（防穿透）
- `tryLock()` - 获取互斥锁（防击穿）
- `unlock()` - 释放互斥锁（防击穿）
- `delete()` - 删除缓存
- `hasKey()` - 检查缓存存在

#### 4. Product.java - 商品实体类（新增）
**位置**：`shop/src/main/java/org/example/shop/entity/Product.java`
**字段**：
- id, name, description, price, stock, category, imageUrl, status, createTime, updateTime

#### 5. ProductMapper.java - 商品 Mapper 接口（新增）
**位置**：`shop/src/main/java/org/example/shop/mapper/ProductMapper.java`
**方法**：
- selectById() - 根据 ID 查询
- selectAll() - 查询所有
- insert() - 插入
- update() - 更新
- delete() - 删除

#### 6. ProductMapper.xml - 商品 Mapper XML（新增）
**位置**：`shop/src/main/resources/mapper/ProductMapper.xml`
**SQL 语句**：
- SELECT 查询商品
- INSERT 插入商品
- UPDATE 更新商品
- DELETE 删除商品

#### 7. ProductService.java - 商品服务接口（新增）
**位置**：`shop/src/main/java/org/example/shop/service/ProductService.java`
**方法**：
- getProductDetail() - 获取商品详情（带缓存）
- getAllProducts() - 获取所有商品
- createProduct() - 创建商品
- updateProduct() - 更新商品
- deleteProduct() - 删除商品

#### 8. ProductServiceImpl.java - 商品服务实现（新增）
**位置**：`shop/src/main/java/org/example/shop/service/impl/ProductServiceImpl.java`
**核心逻辑**：
- 实现完整的缓存防御机制
- 缓存穿透：存储空值
- 缓存击穿：互斥锁 + 双重检查
- 缓存雪崩：随机过期时间
- 更新/删除时清除缓存

#### 9. ProductController.java - 商品控制器（新增）
**位置**：`shop/src/main/java/org/example/shop/Controller/ProductController.java`
**端点**：
- GET /product/{id} - 获取商品详情
- GET /product/list - 获取所有商品
- POST /product - 创建商品
- PUT /product/{id} - 更新商品
- DELETE /product/{id} - 删除商品

#### 10. application.yaml - 配置文件（已更新）
**位置**：`shop/src/main/resources/application.yaml`
**修改内容**：
- 数据库 URL 改为 `jdbc:mysql://mysql:3306/shop`（Docker 容器名）
- 添加 Redis 配置（host: redis, port: 6379）
- 添加 Redis 连接池配置

### 二、前端代码修改

#### 1. Dockerfile - 前端 Docker 镜像（新增）
**位置**：`front_end/Dockerfile`
**特点**：
- 多阶段构建（builder + runtime）
- 使用 Node 22 构建
- 使用 Nginx Alpine 运行

#### 2. nginx.conf - 前端 Nginx 配置（新增）
**位置**：`front_end/nginx.conf`
**功能**：
- 静态资源缓存（30 天）
- Gzip 压缩
- API 代理到后端负载均衡器
- SPA 路由处理
- 健康检查端点

#### 3. .dockerignore - Docker 忽略文件（新增）
**位置**：`front_end/.dockerignore`
**内容**：
- node_modules, dist, .git 等不需要的文件

### 三、基础设施配置

#### 1. docker-compose.yml - Docker 编排文件（新增）
**位置**：`docker-compose.yml`
**服务**：
- mysql - MySQL 8.0 数据库
- redis - Redis 7 缓存
- backend1 - 后端实例 1（端口 8081）
- backend2 - 后端实例 2（端口 8082）
- nginx - Nginx 负载均衡器（端口 80, 8001, 8002, 8003）
- frontend - 前端应用（端口 3000）

**特点**：
- 健康检查配置
- 依赖关系管理
- 环境变量配置
- 卷挂载配置
- 网络隔离

#### 2. nginx.conf - Nginx 负载均衡配置（新增）
**位置**：`nginx.conf`
**负载均衡模式**：
- 轮询（端口 80）：`upstream backend_round_robin`
- 权重（端口 8001）：`upstream backend_weighted` (7:3)
- 最少连接（端口 8002）：`upstream backend_least_conn`
- IP Hash（端口 8003）：`upstream backend_ip_hash`

**特点**：
- 4 个独立的 upstream 配置
- 4 个独立的 server 块监听不同端口
- 完整的代理配置
- 健康检查端点

#### 3. init.sql - 数据库初始化脚本（新增）
**位置**：`init.sql`
**内容**：
- 创建 user 表（用户表）
- 创建 product 表（商品表）
- 插入 5 条示例商品数据

#### 4. .env.example - 环境配置示例（新增）
**位置**：`.env.example`
**配置项**：
- MySQL 配置
- Redis 配置
- 后端配置
- 前端配置
- Nginx 配置
- 时区配置

#### 5. shop/.dockerignore - 后端 Docker 忽略文件（新增）
**位置**：`shop/.dockerignore`
**内容**：
- .git, target, *.log 等不需要的文件

#### 6. shop/Dockerfile - 后端 Docker 镜像（新增）
**位置**：`shop/Dockerfile`
**特点**：
- 多阶段构建（builder + runtime）
- 使用 Maven 3.9 构建
- 使用 Eclipse Temurin 17 JRE 运行
- 基于 Alpine Linux（轻量级）

### 四、文档文件

#### 1. ARCHITECTURE.md - 架构设计文档（新增）
**内容**：
- 系统架构图
- 缓存防御机制详解
- 负载均衡配置说明
- 快速开始指南
- API 端点列表
- 文件结构
- 性能优化建议
- 监控和日志
- 常见问题
- 扩展建议

#### 2. QUICKSTART.md - 快速启动指南（新增）
**内容**：
- 环境检查
- 启动服务
- 验证服务
- 测试 API
- 查看日志
- 验证缓存效果
- 验证负载均衡
- 性能测试
- 数据库操作
- 停止和清理
- 常见问题

#### 3. JMETER_GUIDE.md - JMeter 测试指南（新增）
**内容**：
- 环境准备
- JMeter 测试场景（4 种负载均衡模式）
- 运行测试
- 验证负载均衡效果
- 性能指标分析
- 压力测试建议
- 缓存穿透/击穿/雪崩测试
- 常见问题排查
- 保存和导出结果

#### 4. README.md - 项目总览（新增）
**内容**：
- 项目概述
- 核心功能
- 项目结构
- 快速开始
- 缓存防御机制详解
- 负载均衡配置
- 系统架构
- API 端点
- 性能测试
- 文档列表
- 主要修改
- 核心特性表
- 常见问题
- 性能优化建议

## 🎯 关键实现

### 缓存防御三大机制

#### 1. 缓存穿透防御
```java
// 存储空值
cacheUtil.setNull(cacheKey);  // 前缀：cache:null:product:id

// 检查空值
if (cacheUtil.isNull(cacheKey)) {
    return null;
}
```

#### 2. 缓存击穿防御
```java
// 获取互斥锁
if (cacheUtil.tryLock(lockKey, 10)) {
    try {
        // 查询数据库
        Product product = productMapper.selectById(id);
        // 设置缓存
        cacheUtil.set(cacheKey, product, PRODUCT_CACHE_EXPIRE);
    } finally {
        cacheUtil.unlock(lockKey);
    }
}
```

#### 3. 缓存雪崩防御
```java
// 随机增加过期时间
long randomExpire = expireSeconds + (long) (Math.random() * 300);
redisTemplate.opsForValue().set(key, value, randomExpire, TimeUnit.SECONDS);
```

### 负载均衡四种模式

| 模式 | 端口 | 配置 | 特点 |
|------|------|------|------|
| 轮询 | 80 | `upstream backend_round_robin` | 均匀分配 |
| 权重 | 8001 | `weight=7; weight=3` | 7:3 分配 |
| 最少连接 | 8002 | `least_conn` | 优先分配给连接少的 |
| IP Hash | 8003 | `ip_hash` | 会话保持 |

## 📊 文件统计

### 新增文件
- 后端代码：7 个文件（配置、实体、Mapper、Service、Controller）
- 前端代码：2 个文件（Dockerfile、nginx.conf）
- 基础设施：6 个文件（docker-compose、nginx.conf、init.sql 等）
- 文档：4 个文件（ARCHITECTURE、QUICKSTART、JMETER_GUIDE、README）
- **总计：19 个新增文件**

### 修改文件
- pom.xml - 添加 Redis 依赖
- application.yaml - 添加 Redis 配置
- **总计：2 个修改文件**

## 🚀 部署流程

1. **构建镜像**：`docker-compose build`
2. **启动服务**：`docker-compose up -d`
3. **验证服务**：`docker-compose ps`
4. **测试 API**：`curl http://localhost/product/1`
5. **查看日志**：`docker-compose logs -f`
6. **停止服务**：`docker-compose down`

## ✅ 验证清单

- [x] Redis 配置完成
- [x] 缓存工具类实现（穿透、击穿、雪崩防御）
- [x] 商品管理系统完整实现
- [x] 后端 Dockerfile 创建
- [x] 前端 Dockerfile 创建
- [x] Nginx 负载均衡配置（4 种模式）
- [x] docker-compose.yml 编排
- [x] 数据库初始化脚本
- [x] 完整文档编写
- [x] JMeter 测试指南

## 📝 使用说明

### 启动项目
```bash
cd d:\DistributeProject
docker-compose up -d
```

### 测试缓存
```bash
# 第一次请求（缓存未命中）
curl http://localhost/product/1

# 第二次请求（缓存命中，更快）
curl http://localhost/product/1

# 查看 Redis 缓存
docker exec -it shop-redis redis-cli
KEYS product:*
GET product:1
TTL product:1
```

### 测试负载均衡
```bash
# 轮询模式
curl http://localhost/product/1

# 权重模式
curl http://localhost:8001/product/1

# 最少连接模式
curl http://localhost:8002/product/1

# IP Hash 模式
curl http://localhost:8003/product/1

# 查看 Nginx 日志
docker logs shop-nginx
```

### 性能测试
详见 `JMETER_GUIDE.md`

## 🎓 学习资源

- **缓存防御**：见 `ARCHITECTURE.md` 第 2 部分
- **负载均衡**：见 `ARCHITECTURE.md` 第 3 部分
- **Docker 部署**：见 `QUICKSTART.md`
- **性能测试**：见 `JMETER_GUIDE.md`

---

**修改完成时间**：2026-03-29
**修改者**：全栈架构专家
**版本**：1.0.0
