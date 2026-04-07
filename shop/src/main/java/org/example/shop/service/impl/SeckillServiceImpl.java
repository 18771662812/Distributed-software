package org.example.shop.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.shop.dto.PaymentSuccessMessage;
import org.example.shop.dto.SeckillOrderMessage;
import org.example.shop.entity.Inventory;
import org.example.shop.entity.SeckillActivity;
import org.example.shop.entity.SeckillOrder;
import org.example.shop.mapper.InventoryMapper;
import org.example.shop.mapper.SeckillActivityMapper;
import org.example.shop.mapper.SeckillOrderMapper;
import org.example.shop.service.SeckillService;
import org.example.shop.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀服务实现类
 * 核心流程：幂等性检查 → Redis库存预扣减 → Kafka消息发送 → 异步订单/库存落库
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    private static final String SECKILL_STOCK_PREFIX = "seckill:stock:";
    private static final String SECKILL_SOLD_PREFIX = "seckill:sold:";
    private static final String SECKILL_USER_PREFIX = "seckill:user:";

    private static final String SECKILL_ORDER_TOPIC = "seckill-order";
    private static final String PAYMENT_SUCCESS_TOPIC = "payment-success";

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SeckillActivityMapper seckillActivityMapper;
    private final InventoryMapper inventoryMapper;
    private final SeckillOrderMapper seckillOrderMapper;
    private final ObjectMapper objectMapper;

    private final SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1);

    @Autowired
    public SeckillServiceImpl(RedisTemplate<String, Object> redisTemplate,
                              @Autowired(required = false) KafkaTemplate<String, String> kafkaTemplate,
                              SeckillActivityMapper seckillActivityMapper,
                              InventoryMapper inventoryMapper,
                              SeckillOrderMapper seckillOrderMapper,
                              ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.seckillActivityMapper = seckillActivityMapper;
        this.inventoryMapper = inventoryMapper;
        this.seckillOrderMapper = seckillOrderMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public String seckillOrder(Long userId, Long seckillActivityId) {
        try {
            if (isLocalSoldOut(seckillActivityId)) {
                return "error:库存已售罄";
            }

            SeckillActivity activity = seckillActivityMapper.selectById(seckillActivityId);
            if (activity == null || activity.getStatus() == null || activity.getStatus() != 1) {
                return "error:秒杀活动不存在或未开始";
            }

            String result = executeStockDeduction(userId, seckillActivityId, 1);
            if (!result.startsWith("success:")) {
                return result;
            }

            long orderId = idGenerator.nextId();
            SeckillOrderMessage orderMessage = new SeckillOrderMessage(
                orderId,
                userId,
                seckillActivityId,
                1,
                System.currentTimeMillis()
            );

            try {
                sendMessage(SECKILL_ORDER_TOPIC, orderId, orderMessage);
            } catch (Exception e) {
                rollbackRedisReservation(userId, seckillActivityId, 1);
                log.error("秒杀下单消息发送失败，已回滚Redis预扣减 - 订单ID: {}", orderId, e);
                return "error:下单失败，请重试";
            }

            log.info("秒杀下单成功 - 用户ID: {}, 活动ID: {}, 订单ID: {}", userId, seckillActivityId, orderId);
            return "success:" + orderId;
        } catch (Exception e) {
            log.error("秒杀下单异常", e);
            return "error:系统异常";
        }
    }

    @Override
    public void preloadStock(Long seckillActivityId) {
        SeckillActivity activity = seckillActivityMapper.selectById(seckillActivityId);
        if (activity == null) {
            throw new IllegalArgumentException("秒杀活动不存在");
        }

        Integer remainingStock = activity.getRemainingStock() == null ? 0 : activity.getRemainingStock();
        Integer totalStock = activity.getTotalStock() == null ? remainingStock : activity.getTotalStock();
        Integer soldStock = Math.max(totalStock - remainingStock, 0);
        long now = System.currentTimeMillis();

        redisTemplate.opsForValue().set(stockKey(seckillActivityId), remainingStock);
        redisTemplate.opsForValue().set(soldKey(seckillActivityId), soldStock);

        Inventory inventory = inventoryMapper.selectByActivityId(seckillActivityId);
        if (inventory == null) {
            inventoryMapper.insert(new Inventory(
                null,
                activity.getProductId(),
                seckillActivityId,
                totalStock,
                soldStock,
                remainingStock,
                now,
                now
            ));
        } else {
            inventoryMapper.refreshStock(seckillActivityId, totalStock, soldStock, remainingStock, now);
        }

        log.info("库存预热成功 - 活动ID: {}, 库存: {}", seckillActivityId, remainingStock);
    }

    @Override
    public Integer getRemainStock(Long seckillActivityId) {
        Object stock = redisTemplate.opsForValue().get(stockKey(seckillActivityId));
        return stock != null ? Integer.parseInt(stock.toString()) : 0;
    }

    @Override
    public String payOrder(Long orderId, String paymentMethod) {
        SeckillOrder order = seckillOrderMapper.selectByOrderId(orderId);
        if (order == null) {
            return "error:订单不存在";
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            return "success:订单已支付";
        }
        if (order.getStatus() != null && order.getStatus() != 0) {
            return "error:当前订单状态不允许支付";
        }

        PaymentSuccessMessage message = new PaymentSuccessMessage(orderId, paymentMethod, System.currentTimeMillis());
        try {
            sendMessage(PAYMENT_SUCCESS_TOPIC, orderId, message);
            return "success:支付成功，订单状态更新中";
        } catch (Exception e) {
            log.error("支付消息发送失败 - 订单ID: {}", orderId, e);
            return "error:支付失败，请重试";
        }
    }

    @Override
    public SeckillOrder getOrder(Long orderId) {
        return seckillOrderMapper.selectByOrderId(orderId);
    }

    public void rollbackRedisReservation(Long userId, Long seckillActivityId, Integer quantity) {
        String userOrderKey = userOrderKey(seckillActivityId, userId);
        redisTemplate.opsForValue().increment(stockKey(seckillActivityId), quantity);
        redisTemplate.opsForValue().decrement(soldKey(seckillActivityId), quantity);
        redisTemplate.delete(userOrderKey);
        inventoryMapper.rollbackDeduction(seckillActivityId, quantity);
        log.warn("Redis库存预扣减已回滚 - 用户ID: {}, 活动ID: {}, 数量: {}", userId, seckillActivityId, quantity);
    }

    private String executeStockDeduction(Long userId, Long seckillActivityId, Integer quantity) {
        try {
            DefaultRedisScript<List> script = new DefaultRedisScript<>();
            script.setScriptText(buildLuaScript());
            script.setResultType(List.class);

            List<Object> result = redisTemplate.execute(
                script,
                Arrays.asList(stockKey(seckillActivityId), soldKey(seckillActivityId)),
                String.valueOf(quantity),
                String.valueOf(userId),
                userOrderKey(seckillActivityId, userId)
            );

            if (result != null && result.size() >= 2) {
                Long success = Long.parseLong(String.valueOf(result.get(0)));
                String message = String.valueOf(result.get(1));
                return success == 1 ? "success:" + message : "error:" + message;
            }
            return "error:Lua脚本执行失败";
        } catch (Exception e) {
            log.error("Lua脚本执行异常", e);
            return "error:库存扣减失败";
        }
    }

    private void sendMessage(String topic, Long key, Object payload) throws Exception {
        if (kafkaTemplate == null) {
            throw new IllegalStateException("Kafka 不可用");
        }
        String message = toJson(payload);
        kafkaTemplate.send(topic, String.valueOf(key), message).get(5, TimeUnit.SECONDS);
        log.info("Kafka消息发送成功 - topic: {}, key: {}", topic, key);
    }

    private String toJson(Object payload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(payload);
    }

    private String buildLuaScript() {
        return "local stock = redis.call('GET', KEYS[1])\n" +
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
    }

    private String stockKey(Long seckillActivityId) {
        return SECKILL_STOCK_PREFIX + seckillActivityId;
    }

    private String soldKey(Long seckillActivityId) {
        return SECKILL_SOLD_PREFIX + seckillActivityId;
    }

    private String userOrderKey(Long seckillActivityId, Long userId) {
        return SECKILL_USER_PREFIX + seckillActivityId + ":" + userId;
    }

    private boolean isLocalSoldOut(Long seckillActivityId) {
        return false;
    }
}
