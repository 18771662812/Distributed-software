package org.example.shop.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.shop.entity.SeckillActivity;
import org.example.shop.service.SeckillService;
import org.example.shop.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀服务实现类
 * 核心流程：幂等性检查 → Redis库存扣减 → Kafka消息发送 → 异步订单落库
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    // 雪花算法ID生成器（机器ID为1）
    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1);

    // Redis键前缀
    private static final String SECKILL_STOCK_PREFIX = "seckill:stock:";
    private static final String SECKILL_SOLD_PREFIX = "seckill:sold:";
    private static final String SECKILL_USER_PREFIX = "seckill:user:";
    private static final String SECKILL_ACTIVITY_PREFIX = "seckill:activity:";

    // Kafka主题
    private static final String SECKILL_ORDER_TOPIC = "seckill-order";

    /**
     * 秒杀下单核心逻辑
     */
    @Override
    public String seckillOrder(Long userId, Long seckillActivityId) {
        try {
            // 1. 本地JVM缓存检查（可选，减少Redis请求）
            if (isLocalSoldOut(seckillActivityId)) {
                return "error:库存已售罄";
            }

            // 2. 执行Lua脚本进行原子扣减
            String result = executeStockDeduction(userId, seckillActivityId);
            if (!result.startsWith("success")) {
                return result;
            }

            // 3. 生成订单ID（雪花算法）
            long orderId = idGenerator.nextId();

            // 4. 发送Kafka消息进行异步订单落库
            sendOrderMessage(orderId, userId, seckillActivityId);

            log.info("秒杀下单成功 - 用户ID: {}, 活动ID: {}, 订单ID: {}", userId, seckillActivityId, orderId);
            return "success:" + orderId;

        } catch (Exception e) {
            log.error("秒杀下单异常", e);
            return "error:系统异常";
        }
    }

    /**
     * 执行Lua脚本进行库存扣减
     */
    private String executeStockDeduction(Long userId, Long seckillActivityId) {
        try {
            // Lua脚本内容
            String luaScript = "local stock = redis.call('GET', KEYS[1])\n" +
                "if not stock then\n" +
                "    return {0, '库存不存在'}\n" +
                "end\n" +
                "stock = tonumber(stock)\n" +
                "local quantity = tonumber(ARGV[1])\n" +
                "if stock < quantity then\n" +
                "    return {0, '库存不足'}\n" +
                "end\n" +
                "local userOrderKey = ARGV[3]\n" +
                "local userOrdered = redis.call('EXISTS', userOrderKey)\n" +
                "if userOrdered == 1 then\n" +
                "    return {0, '用户已下单'}\n" +
                "end\n" +
                "redis.call('DECRBY', KEYS[1], quantity)\n" +
                "redis.call('INCRBY', KEYS[2], quantity)\n" +
                "redis.call('SETEX', userOrderKey, 3600, ARGV[2])\n" +
                "return {1, ARGV[2]}\n";

            DefaultRedisScript<List> script = new DefaultRedisScript<>();
            script.setScriptText(luaScript);
            script.setResultType(List.class);

            String stockKey = SECKILL_STOCK_PREFIX + seckillActivityId;
            String soldKey = SECKILL_SOLD_PREFIX + seckillActivityId;
            String userOrderKey = SECKILL_USER_PREFIX + seckillActivityId + ":" + userId;

            List<Object> result = redisTemplate.execute(
                script,
                Arrays.asList(stockKey, soldKey),
                "1",
                String.valueOf(userId),
                userOrderKey
            );

            if (result != null && result.size() > 0) {
                Long success = (Long) result.get(0);
                String message = (String) result.get(1);

                if (success == 1) {
                    return "success:" + message;
                } else {
                    return "error:" + message;
                }
            }

            return "error:Lua脚本执行失败";

        } catch (Exception e) {
            log.error("Lua脚本执行异常", e);
            return "error:库存扣减失败";
        }
    }

    /**
     * 发送Kafka消息
     */
    private void sendOrderMessage(Long orderId, Long userId, Long seckillActivityId) {
        try {
            if (kafkaTemplate == null) {
                log.warn("KafkaTemplate 不可用，跳过异步消息发送 - 订单ID: {}", orderId);
                return;
            }
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", orderId);
            orderData.put("userId", userId);
            orderData.put("seckillActivityId", seckillActivityId);
            orderData.put("createTime", System.currentTimeMillis());

            // 转换为JSON字符串（简化版，实际应使用ObjectMapper）
            String message = orderData.toString();

            kafkaTemplate.send(SECKILL_ORDER_TOPIC, String.valueOf(orderId), message);
            log.info("Kafka消息发送成功 - 订单ID: {}", orderId);

        } catch (Exception e) {
            log.error("Kafka消息发送失败", e);
            // 这里可以实现补偿机制，如重试或人工介入
        }
    }

    /**
     * 库存预热
     */
    @Override
    public void preloadStock(Long seckillActivityId) {
        try {
            // 从数据库获取秒杀活动信息（这里简化处理）
            String stockKey = SECKILL_STOCK_PREFIX + seckillActivityId;
            String soldKey = SECKILL_SOLD_PREFIX + seckillActivityId;

            // 假设库存为1000
            Integer totalStock = 1000;

            redisTemplate.opsForValue().set(stockKey, totalStock);
            redisTemplate.opsForValue().set(soldKey, 0);

            log.info("库存预热成功 - 活动ID: {}, 库存: {}", seckillActivityId, totalStock);

        } catch (Exception e) {
            log.error("库存预热失败", e);
        }
    }

    /**
     * 获取剩余库存
     */
    @Override
    public Integer getRemainStock(Long seckillActivityId) {
        try {
            String stockKey = SECKILL_STOCK_PREFIX + seckillActivityId;
            Object stock = redisTemplate.opsForValue().get(stockKey);
            return stock != null ? Integer.parseInt(stock.toString()) : 0;
        } catch (Exception e) {
            log.error("获取库存失败", e);
            return 0;
        }
    }

    /**
     * 本地JVM缓存检查（可选优化）
     */
    private boolean isLocalSoldOut(Long seckillActivityId) {
        // 这里可以使用Guava Cache或其他本地缓存
        // 简化版本直接返回false
        return false;
    }
}
