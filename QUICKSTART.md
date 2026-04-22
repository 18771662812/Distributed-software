# 快速启动指南

## 一、环境检查

```bash
# 检查 Docker 是否安装
docker --version

# 检查 Docker Compose 是否可用
docker compose version
```

## 二、启动服务

```bash
# 进入项目目录
cd d:\DistributeProject

# 首次启动建议强制重建
docker compose up -d --build

# 查看容器状态
docker compose ps
```

### 2.1 预期服务

启动成功后，应至少看到以下服务：

- `shop-mysql`
- `shop-redis`
- `shop-zookeeper`
- `shop-kafka`
- `shop-nacos`
- `shop-backend-1`
- `shop-backend-2`
- `shop-gateway`
- `shop-nginx`
- `shop-frontend`

说明：
- `Nacos` 负责服务注册与配置管理
- `Gateway` 是新的统一服务入口
- 根目录 `Nginx` 仍保留用于原有负载均衡演示

---

## 三、验证基础服务

### 3.1 检查容器健康状态

```bash
docker compose ps
```

重点关注以下服务状态：

- `shop-nacos` → `Up`
- `shop-backend-1` → `Up`
- `shop-backend-2` → `Up`
- `shop-gateway` → `Up`

### 3.2 打开页面入口

```text
前端应用：http://localhost:3000
Nacos 控制台：http://localhost:8848/nacos
Gateway 健康检查：http://localhost:8088/actuator/health
```

---

## 四、Nacos 配置中心初始化

为了验证“配置管理 + 动态刷新”，需要先在 Nacos 中创建一条测试配置。

### 4.1 新增配置

进入 Nacos 控制台后，创建配置：

- Data ID: `shop-service.yaml`
- Group: `DEFAULT_GROUP`

配置内容：

```yaml
shop:
  dynamic-message: 这是来自 Nacos 的动态配置
```

### 4.2 这一步的作用

这条配置会被 `shop-service` 在启动时加载，用于验证：

1. 服务是否成功接入 Nacos 配置中心
2. 远程配置是否覆盖本地默认值
3. 修改配置后是否能动态刷新

---

## 五、验证服务注册发现

### 5.1 在 Nacos 控制台查看服务列表

进入：

```text
http://localhost:8848/nacos
```

应能看到至少两个服务：

- `shop-service`
- `gateway-service`

### 5.2 验证 `shop-service` 有两个实例

在服务详情中，应看到两个实例，对应：

- `backend1`
- `backend2`

这表示两个后端实例都已经成功注册到 Nacos。

---

## 六、通过 Gateway 调用服务

### 6.1 验证 Gateway 是否可用

```bash
curl http://localhost:8088/actuator/health
```

预期返回健康状态 JSON。

### 6.2 通过 Gateway 调用后端实例信息接口

```bash
curl http://localhost:8088/shop/system/instance
```

预期返回类似：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "serviceName": "shop-service",
    "instanceName": "backend1",
    "port": "8080",
    "hostname": "..."
  }
}
```

多执行几次，`instanceName` 可能会在 `backend1` / `backend2` 之间变化，这说明：

- 请求是通过 Gateway 进入的
- Gateway 是按服务名 `lb://shop-service` 动态路由的
- 多实例服务发现与转发正常

---

## 七、验证 Nacos 动态配置

### 7.1 查看当前动态配置值

```bash
curl http://localhost:8088/shop/config/runtime
```

预期返回：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "dynamicMessage": "这是来自 Nacos 的动态配置",
    "sourceHint": "请在 Nacos 中修改 Data ID = shop-service.yaml 里的 shop.dynamic-message"
  }
}
```

### 7.2 修改 Nacos 配置

在 Nacos 控制台中，将：

```yaml
shop:
  dynamic-message: 这是来自 Nacos 的动态配置
```

改成：

```yaml
shop:
  dynamic-message: 我刚刚在 Nacos 修改了配置
```

保存后再次请求：

```bash
curl http://localhost:8088/shop/config/runtime
```

如果返回值已经变化，说明：

- Nacos 配置中心生效
- 配置刷新成功
- 无需重启后端即可动态更新属性

---

## 八、测试业务 API

以下示例仍可测试你的业务接口，建议优先通过 Gateway 地址访问。

### 8.1 用户注册

```bash
curl -X POST http://localhost:8088/shop/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "phone": "13800138000",
    "email": "test@example.com"
  }'
```

### 8.2 用户登录

```bash
curl -X POST http://localhost:8088/shop/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

### 8.3 获取商品详情（通过 Gateway）

```bash
curl http://localhost:8088/shop/product/1
```

### 8.4 获取所有商品（通过 Gateway）

```bash
curl http://localhost:8088/shop/product/list
```

---

## 九、保留的 Nginx 负载均衡验证

如果你还要验证原有 Nginx 负载均衡能力，可以继续使用以下入口：

```bash
# 轮询模式（端口 80）
curl http://localhost/product/1

# 权重模式（端口 8001）
curl http://localhost:8001/product/1

# 最少连接模式（端口 8002）
curl http://localhost:8002/product/1

# IP Hash 模式（端口 8003）
curl http://localhost:8003/product/1
```

说明：
- 这些是原有 Nginx 反向代理入口
- 新增的 Spring Cloud Gateway 验证应优先使用 `http://localhost:8088/shop/...`

---

## 十、查看日志

### 10.1 查看所有容器日志

```bash
docker compose logs -f
```

### 10.2 查看关键服务日志

```bash
# Nacos
docker logs -f shop-nacos

# Gateway
docker logs -f shop-gateway

# 后端实例 1
docker logs -f shop-backend-1

# 后端实例 2
docker logs -f shop-backend-2

# Redis
docker logs -f shop-redis

# MySQL
docker logs -f shop-mysql
```

---

## 十一、验证缓存效果

### 11.1 进入 Redis 容器

```bash
docker exec -it shop-redis redis-cli
```

### 11.2 查看缓存键

```bash
KEYS *
KEYS product:*
KEYS cache:null:*
KEYS lock:*
```

### 11.3 查看缓存值

```bash
GET product:1
TTL product:1
TYPE product:1
```

---

## 十二、数据库操作

### 12.1 进入 MySQL 容器

```bash
docker exec -it shop-mysql mysql -uroot -p123456
```

### 12.2 查看数据库

```sql
SHOW DATABASES;
USE shop;
SHOW TABLES;
SELECT * FROM user;
SELECT * FROM product;
```

### 12.3 插入测试数据

```sql
INSERT INTO product (name, description, price, stock, category, status, create_time, update_time)
VALUES ('测试商品', '这是一个测试商品', 99.99, 100, '测试', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
```

---

## 十三、停止和清理

### 13.1 停止服务

```bash
# 停止所有容器
docker compose stop

# 停止并删除容器
docker compose down

# 停止并删除容器和卷（清理数据）
docker compose down -v
```

### 13.2 查看容器资源使用

```bash
docker stats
docker stats shop-backend-1
docker stats shop-gateway
```

---

## 十四、常见问题

### Q: Nacos 显示 unhealthy 或后端一直停在 Created
**A**:

```bash
# 查看 Nacos 日志
docker logs shop-nacos

# 查看容器状态
docker compose ps

# 重新创建容器
docker compose down
docker compose up -d
```

### Q: Docker 构建 Java 镜像时 Maven 下载依赖失败
**A**:

当前 `shop` 和 `gateway` 的 Dockerfile 已配置 Maven 镜像源。
若仍失败，可尝试：

```bash
docker compose build --no-cache backend1 backend2 gateway
```

### Q: Gateway 无法访问后端
**A**:

```bash
# 查看 Gateway 日志
docker logs shop-gateway

# 查看后端实例日志
docker logs shop-backend-1
docker logs shop-backend-2

# 检查 Nacos 中是否有 shop-service 两个实例
```

### Q: 动态配置没有刷新
**A**:

1. 确认 Nacos 中已创建：
   - Data ID: `shop-service.yaml`
   - Group: `DEFAULT_GROUP`
2. 确认访问的是：
   - `http://localhost:8088/shop/config/runtime`
3. 查看后端日志，确认服务已连上 Nacos

### Q: 前端无法访问
**A**:

```bash
docker logs shop-frontend
docker logs shop-nginx
curl http://localhost:3000
```

---

## 十五、说明

### 15.1 当前推荐的服务调用入口

推荐优先使用：

```text
http://localhost:8088/shop/...
```

因为这是基于：

- Nacos 服务注册发现
- Spring Cloud Gateway 动态路由

实现的新入口。

### 15.2 当前架构中两个入口并存

- `Gateway`：用于完成服务注册、动态路由、配置中心实验
- `Nginx`：保留原有负载均衡实验能力

---

