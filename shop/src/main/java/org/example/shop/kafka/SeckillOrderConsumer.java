package org.example.shop.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单Kafka消费者
 * 异步处理订单落库，实现削峰填谷
 */
@Slf4j
@Component
public class SeckillOrderConsumer {

    /**
     * 消费秒杀订单消息
     * @param message 订单消息
     */
    @KafkaListener(topics = "seckill-order", groupId = "seckill-order-group")
    public void consumeSeckillOrder(String message) {
        try {
            log.info("收到秒杀订单消息: {}", message);

            // 1. 解析消息（实际应使用JSON反序列化）
            // SeckillOrderMessage orderMsg = JSON.parseObject(message, SeckillOrderMessage.class);

            // 2. 创建订单（异步落库）
            createOrder(message);

            // 3. 更新库存（可选）
            // updateInventory(orderMsg);

            log.info("订单处理成功: {}", message);

        } catch (Exception e) {
            log.error("订单处理失败: {}", message, e);
            // 这里可以实现重试机制或死信队列处理
        }
    }

    /**
     * 创建订单（异步落库）
     */
    private void createOrder(String message) {
        try {
            // 模拟数据库操作
            // INSERT INTO seckill_order (order_id, user_id, seckill_activity_id, ...)
            // VALUES (...)

            // 实际应调用订单Service进行数据库操作
            Thread.sleep(100); // 模拟数据库操作耗时

            log.info("订单已落库: {}", message);

        } catch (Exception e) {
            log.error("订单落库失败", e);
            throw new RuntimeException("订单落库失败", e);
        }
    }

    /**
     * 更新库存（可选的补偿机制）
     */
    private void updateInventory(String message) {
        try {
            // 更新数据库库存表
            log.info("库存已更新: {}", message);
        } catch (Exception e) {
            log.error("库存更新失败", e);
        }
    }
}
