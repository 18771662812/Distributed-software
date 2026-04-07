package org.example.shop.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.shop.dto.PaymentSuccessMessage;
import org.example.shop.dto.SeckillOrderMessage;
import org.example.shop.service.impl.SeckillOrderProcessService;
import org.example.shop.service.impl.SeckillServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单Kafka消费者
 * 异步处理订单与库存落库、支付状态更新
 */
@Slf4j
@Component
public class SeckillOrderConsumer {

    private final ObjectMapper objectMapper;
    private final SeckillOrderProcessService orderProcessService;
    private final SeckillServiceImpl seckillService;

    public SeckillOrderConsumer(ObjectMapper objectMapper,
                                SeckillOrderProcessService orderProcessService,
                                SeckillServiceImpl seckillService) {
        this.objectMapper = objectMapper;
        this.orderProcessService = orderProcessService;
        this.seckillService = seckillService;
    }

    @KafkaListener(topics = "seckill-order", groupId = "seckill-order-group")
    public void consumeSeckillOrder(String message) {
        try {
            SeckillOrderMessage orderMessage = objectMapper.readValue(message, SeckillOrderMessage.class);
            orderProcessService.createOrder(orderMessage);
            log.info("订单处理成功: {}", orderMessage.getOrderId());
        } catch (Exception e) {
            log.error("订单处理失败: {}", message, e);
            rollbackFromMessage(message);
            throw new RuntimeException("订单处理失败", e);
        }
    }

    @KafkaListener(topics = "payment-success", groupId = "payment-success-group")
    public void consumePaymentSuccess(String message) {
        try {
            PaymentSuccessMessage paymentMessage = objectMapper.readValue(message, PaymentSuccessMessage.class);
            orderProcessService.updateOrderPaid(paymentMessage);
            log.info("订单支付状态更新成功: {}", paymentMessage.getOrderId());
        } catch (Exception e) {
            log.error("订单支付状态更新失败: {}", message, e);
            throw new RuntimeException("订单支付状态更新失败", e);
        }
    }

    private void rollbackFromMessage(String message) {
        try {
            SeckillOrderMessage orderMessage = objectMapper.readValue(message, SeckillOrderMessage.class);
            seckillService.rollbackRedisReservation(
                orderMessage.getUserId(),
                orderMessage.getSeckillActivityId(),
                orderMessage.getQuantity()
            );
        } catch (Exception ex) {
            log.error("订单失败后的Redis补偿回滚失败: {}", message, ex);
        }
    }
}
