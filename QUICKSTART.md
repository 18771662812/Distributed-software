# 快速启动指南

## 一、环境检查

```bash
# 检查 Docker 是否安装
docker --version

# 检查 Docker Compose 是否安装
docker-compose --version
```

## 二、启动服务

```bash
# 进入项目目录
cd d:\DistributeProject

# 启动所有服务（首次启动会构建镜像，耗时较长）
docker-compose up -d

# 查看容器状态
docker-compose ps
```

## 三、验证服务

### 3.1 检查容器健康状态
```bash
# 等待所有容器启动完成（约 1-2 分钟）
docker-compose ps

# 应该看到所有容器状态为 "Up"
```

### 3.2 测试后端实例
```bash
# 测试后端实例 1（端口 8081）
curl http://localhost:8081/user/login

# 测试后端实例 2（端口 8082）
curl http://localhost:8082/user/login

# 预期返回：400 Bad Request（因为没有提供请求体）
```

### 3.3 测试负载均衡
```bash
# 轮询模式（端口 80）
curl http://localhost/product/1

# 权重模式（端口 8001）
curl http://localhost:8001/product/1

# 最少连接模式（端口 8002）
curl http://localhost:8002/product/1

# IP Hash 模式（端口 8003）
curl http://localhost:8003/product/1

# 预期返回：JSON 格式的商品信息
```

### 3.4 测试前端应用
```bash
# 在浏览器中打开
http://localhost:3000
```

## 四、测试 API

### 4.1 用户注册
```bash
curl -X POST http://localhost/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "phone": "13800138000",
    "email": "test@example.com"
  }'
```

### 4.2 用户登录
```bash
curl -X POST http://localhost/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

### 4.3 获取商品详情（测试缓存）
```bash
# 第一次请求（缓存未命中，查询数据库）
time curl http://localhost/product/1

# 第二次请求（缓存命中，响应更快）
time curl http://localhost/product/1
```

### 4.4 获取所有商品
```bash
curl http://localhost/product/list
```

## 五、查看日志

### 5.1 查看所有容器日志
```bash
docker-compose logs -f
```

### 5.2 查看特定容器日志
```bash
# 后端实例 1
docker logs -f shop-backend-1

# 后端实例 2
docker logs -f shop-backend-2

# Nginx 负载均衡器
docker logs -f shop-nginx

# Redis 缓存
docker logs -f shop-redis

# MySQL 数据库
docker logs -f shop-mysql
```

## 六、验证缓存效果

### 6.1 进入 Redis 容器
```bash
docker exec -it shop-redis redis-cli
```

### 6.2 查看缓存键
```bash
# 查看所有缓存键
KEYS *

# 查看商品缓存
KEYS product:*

# 查看空值缓存
KEYS cache:null:*

# 查看互斥锁
KEYS lock:*
```

### 6.3 查看缓存值
```bash
# 查看商品 1 的缓存
GET product:1

# 查看缓存过期时间
TTL product:1

# 查看缓存类型
TYPE product:1
```

## 七、验证负载均衡

### 7.1 查看 Nginx 访问日志
```bash
# 实时查看日志
docker exec shop-nginx tail -f /var/log/nginx/access.log

# 查看最近 20 行
docker exec shop-nginx tail -20 /var/log/nginx/access.log
```

### 7.2 统计请求分布
```bash
# 统计分配到 backend1 的请求数
docker exec shop-nginx grep "backend1" /var/log/nginx/access.log | wc -l

# 统计分配到 backend2 的请求数
docker exec shop-nginx grep "backend2" /var/log/nginx/access.log | wc -l
```

## 八、性能测试（使用 JMeter）

### 8.1 安装 JMeter
- 下载：https://jmeter.apache.org/download_jmeter.html
- 解压后运行：`bin/jmeter.sh` 或 `bin/jmeter.bat`

### 8.2 创建简单测试
1. 新建 Test Plan
2. 添加 Thread Group（100 个线程，10 次循环）
3. 添加 HTTP Request（GET http://localhost/product/1）
4. 添加 Summary Report 监听器
5. 运行测试

### 8.3 查看测试结果
- Average: 平均响应时间
- Min/Max: 最小/最大响应时间
- Throughput: 吞吐量（请求/秒）
- Error %: 错误率

详见 `JMETER_GUIDE.md`

## 九、数据库操作

### 9.1 进入 MySQL 容器
```bash
docker exec -it shop-mysql mysql -uroot -p123456
```

### 9.2 查看数据库
```sql
-- 查看所有数据库
SHOW DATABASES;

-- 使用 shop 数据库
USE shop;

-- 查看所有表
SHOW TABLES;

-- 查看用户表
SELECT * FROM user;

-- 查看商品表
SELECT * FROM product;
```

### 9.3 插入测试数据
```sql
-- 插入商品
INSERT INTO product (name, description, price, stock, category, status, create_time, update_time)
VALUES ('测试商品', '这是一个测试商品', 99.99, 100, '测试', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
```

## 十、停止和清理

### 10.1 停止服务
```bash
# 停止所有容器
docker-compose stop

# 停止并删除容器
docker-compose down

# 停止并删除容器和卷（清理数据）
docker-compose down -v
```

### 10.2 查看容器资源使用
```bash
# 查看实时资源使用情况
docker stats

# 查看特定容器资源使用
docker stats shop-backend-1
```

## 十一、常见问题

### Q: 容器启动失败
**A**: 
```bash
# 查看错误日志
docker-compose logs

# 检查端口是否被占用
netstat -an | grep LISTEN

# 清理旧容器
docker-compose down -v
docker system prune -a
```

### Q: 无法连接到数据库
**A**:
```bash
# 检查 MySQL 容器是否运行
docker ps | grep mysql

# 查看 MySQL 日志
docker logs shop-mysql

# 测试连接
docker exec shop-mysql mysql -uroot -p123456 -e "SELECT 1"
```

### Q: Redis 连接失败
**A**:
```bash
# 检查 Redis 容器是否运行
docker ps | grep redis

# 测试 Redis 连接
docker exec shop-redis redis-cli ping

# 应该返回 PONG
```

### Q: 前端无法访问
**A**:
```bash
# 检查前端容器是否运行
docker ps | grep frontend

# 查看前端日志
docker logs shop-frontend

# 测试 Nginx 健康检查
curl http://localhost/health
```

## 十二、性能优化建议

1. **增加后端实例**：修改 `docker-compose.yml`，添加 `backend3`, `backend4` 等
2. **增加 Redis 内存**：修改 `docker-compose.yml` 中 Redis 的内存限制
3. **优化数据库**：添加索引，使用查询优化
4. **启用 HTTP 压缩**：已在 Nginx 配置中启用 Gzip
5. **使用 CDN**：将静态资源上传到 CDN

## 十三、架构文档

详见 `ARCHITECTURE.md` 了解完整的系统架构和设计说明。

## 十四、测试指南

详见 `JMETER_GUIDE.md` 了解如何使用 JMeter 进行性能测试。
