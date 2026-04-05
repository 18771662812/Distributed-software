# 🚀 秒杀系统完整运行指南

## 📋 前置条件

✅ Docker Desktop 已安装
✅ 所有代码文件已创建
✅ 所有配置文件已更新

## 🎯 运行步骤

### 第 1 步：清理旧容器和镜像

```powershell
# 进入项目目录
cd d:\DistributeProject

# 停止所有容器并删除卷（清理旧数据）
docker compose down -v

# 删除旧镜像（强制删除）
docker rmi distributeproject-backend1 -f
docker rmi distributeproject-backend2 -f
docker rmi distributeproject-frontend -f
```

### 第 2 步：启动所有服务

```powershell
# 重新构建并启动所有容器
docker compose up -d

# 等待 3-5 分钟让所有服务启动完成
```

### 第 3 步：验证服务状态

```powershell
# 查看所有容器状态
docker compose ps

# 预期输出：所有容器都应该是 Up (healthy) 状态
# NAME                STATUS
# shop-mysql          Up (healthy)
# shop-redis          Up (healthy)
# shop-zookeeper      Up (healthy)
# shop-kafka          Up (healthy)
# shop-backend-1      Up (healthy)
# shop-backend-2      Up (healthy)
# shop-nginx          Up (healthy)
# shop-frontend       Up (healthy)
```

### 第 4 步：库存预热

```powershell
# 预热秒杀活动 1 的库存
curl -X POST "http://localhost/seckill/preload?seckillActivityId=1"

# 预期响应：
# {"code":200,"message":"库存预热成功","data":null}
```

### 第 5 步：查看库存

```powershell
# 查看秒杀活动 1 的剩余库存
curl "http://localhost/seckill/stock/1"

# 预期响应：
# {"code":200,"message":"success","data":100}
```

### 第 6 步：测试秒杀下单

```powershell
# 用户 1 对秒杀活动 1 进行下单
curl -X POST "http://localhost/seckill/order?userId=1&seckillActivityId=1"

# 预期响应：
# {"code":200,"message":"秒杀下单成功，订单ID: 123456789","data":"123456789"}
```

### 第 7 步：测试防重机制

```powershell
# 同一用户再次下单（应该失败）
curl -X POST "http://localhost/seckill/order?userId=1&seckillActivityId=1"

# 预期响应：
# {"code":400,"message":"用户已下单","data":null}
```

### 第 8 步：查看库存变化

```powershell
# 查看库存是否减少
curl "http://localhost/seckill/stock/1"

# 预期响应：
# {"code":200,"message":"success","data":99}  # 从 100 减少到 99
```

## 🔍 监控和调试

### 查看后端日志

```powershell
# 查看后端实例 1 的日志
docker logs -f shop-backend-1

# 查看后端实例 2 的日志
docker logs -f shop-backend-2
```

### 查看 Kafka 消息

```powershell
# 进入 Kafka 容器
docker exec -it shop-kafka bash

# 查看秒杀订单主题的消息
kafka-console-consumer.sh \
  --bootstrap-servers localhost:9092 \
  --topic seckill-order \
  --from-beginning
```

### 查看 Redis 缓存

```powershell
# 进入 Redis 容器
docker exec -it shop-redis redis-cli

# 查看所有秒杀相关的键
KEYS seckill:*

# 查看库存
GET seckill:stock:1

# 查看已售
GET seckill:sold:1

# 查看用户下单记录
GET seckill:user:1:1
```

### 查看数据库

```powershell
# 进入 MySQL 容器
docker exec -it shop-mysql mysql -uroot -p123456

# 查看秒杀订单
USE shop;
SELECT * FROM seckill_order;

# 查看秒杀活动
SELECT * FROM seckill_activity;
```

## 📊 完整测试流程

### 场景 1：单用户秒杀

```powershell
# 1. 预热库存
curl -X POST "http://localhost/seckill/preload?seckillActivityId=1"

# 2. 查看库存
curl "http://localhost/seckill/stock/1"

# 3. 用户 1 下单
curl -X POST "http://localhost/seckill/order?userId=1&seckillActivityId=1"

# 4. 用户 1 再次下单（应该失败）
curl -X POST "http://localhost/seckill/order?userId=1&seckillActivityId=1"

# 5. 查看库存（应该减少）
curl "http://localhost/seckill/stock/1"
```

### 场景 2：多用户秒杀

```powershell
# 1. 预热库存
curl -X POST "http://localhost/seckill/preload?seckillActivityId=1"

# 2. 用户 1 下单
curl -X POST "http://localhost/seckill/order?userId=1&seckillActivityId=1"

# 3. 用户 2 下单
curl -X POST "http://localhost/seckill/order?userId=2&seckillActivityId=1"

# 4. 用户 3 下单
curl -X POST "http://localhost/seckill/order?userId=3&seckillActivityId=1"

# 5. 查看库存（应该减少 3）
curl "http://localhost/seckill/stock/1"
```

### 场景 3：库存售罄

```powershell
# 1. 预热库存（假设只有 2 个）
# 需要修改 SeckillServiceImpl.java 中的 totalStock = 2

# 2. 用户 1 下单
curl -X POST "http://localhost/seckill/order?userId=1&seckillActivityId=1"

# 3. 用户 2 下单
curl -X POST "http://localhost/seckill/order?userId=2&seckillActivityId=1"

# 4. 用户 3 下单（应该失败，库存不足）
curl -X POST "http://localhost/seckill/order?userId=3&seckillActivityId=1"

# 预期响应：
# {"code":400,"message":"库存不足","data":null}
```

## 🌐 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端应用 | http://localhost:3000 | Vue3 前端 |
| 后端实例 1 | http://localhost:8081 | 直接访问 |
| 后端实例 2 | http://localhost:8082 | 直接访问 |
| Nginx 负载均衡 | http://localhost | 轮询模式 |
| Nginx 权重模式 | http://localhost:8001 | 7:3 权重 |
| Nginx 最少连接 | http://localhost:8002 | 最少连接 |
| Nginx IP Hash | http://localhost:8003 | IP Hash |
| MySQL | localhost:3306 | 数据库 |
| Redis | localhost:6379 | 缓存 |
| Kafka | localhost:9092 | 消息队列 |
| Zookeeper | localhost:2181 | Kafka 协调 |

## 🛑 停止服务

```powershell
# 停止所有容器（保留数据）
docker compose stop

# 停止并删除所有容器（保留数据）
docker compose down

# 停止并删除所有容器和数据
docker compose down -v
```

## 🐛 常见问题

### Q: 容器启动失败怎么办？
**A**: 
1. 查看日志：`docker logs shop-backend-1`
2. 检查端口是否被占用
3. 清理旧容器：`docker compose down -v`
4. 重新启动：`docker compose up -d`

### Q: 秒杀下单返回错误怎么办？
**A**:
1. 检查库存是否预热：`curl http://localhost/seckill/stock/1`
2. 查看后端日志：`docker logs shop-backend-1`
3. 检查 Redis 连接：`docker exec -it shop-redis redis-cli ping`
4. 检查 Kafka 连接：`docker logs shop-kafka`

### Q: 如何重置库存？
**A**:
```powershell
docker exec -it shop-redis redis-cli
DEL seckill:stock:1
DEL seckill:sold:1
DEL seckill:user:1:*
```

### Q: 如何查看订单是否落库？
**A**:
```powershell
docker exec -it shop-mysql mysql -uroot -p123456
USE shop;
SELECT * FROM seckill_order;
```

## 📚 相关文档

- [SECKILL_DESIGN.md](SECKILL_DESIGN.md) - 完整设计文档
- [SECKILL_QUICKREF.md](SECKILL_QUICKREF.md) - 快速参考
- [ARCHITECTURE.md](ARCHITECTURE.md) - 系统架构
- [QUICKSTART.md](QUICKSTART.md) - 快速启动

---

**版本**：1.0
**最后更新**：2026-03-30
**状态**：✅ 完整可运行
