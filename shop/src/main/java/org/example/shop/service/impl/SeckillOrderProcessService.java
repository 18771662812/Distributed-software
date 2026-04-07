package org.example.shop.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.shop.dto.PaymentSuccessMessage;
import org.example.shop.dto.SeckillOrderMessage;
import org.example.shop.entity.Inventory;
import org.example.shop.entity.SeckillActivity;
import org.example.shop.entity.SeckillOrder;
import org.example.shop.mapper.InventoryMapper;
import org.example.shop.mapper.SeckillActivityMapper;
import org.example.shop.mapper.SeckillOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 秒杀订单事务处理服务
 */
@Slf4j
@Service
public class SeckillOrderProcessService {

    private final SeckillOrderMapper seckillOrderMapper;
    private final SeckillActivityMapper seckillActivityMapper;
    private final InventoryMapper inventoryMapper;

    public SeckillOrderProcessService(SeckillOrderMapper seckillOrderMapper,
                                      SeckillActivityMapper seckillActivityMapper,
                                      InventoryMapper inventoryMapper) {
        this.seckillOrderMapper = seckillOrderMapper;
        this.seckillActivityMapper = seckillActivityMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Transactional
    public void createOrder(SeckillOrderMessage orderMessage) {
        SeckillOrder exists = seckillOrderMapper.selectByOrderId(orderMessage.getOrderId());
        if (exists != null) {
            log.info("订单已存在，跳过重复创建 - 订单ID: {}", orderMessage.getOrderId());
            return;
        }

        SeckillActivity activity = seckillActivityMapper.selectById(orderMessage.getSeckillActivityId());
        if (activity == null) {
            throw new IllegalStateException("秒杀活动不存在");
        }

        Inventory inventory = inventoryMapper.selectByActivityId(orderMessage.getSeckillActivityId());
        if (inventory == null) {
            throw new IllegalStateException("库存记录不存在，请先预热库存");
        }

        int confirmRows = inventoryMapper.confirmDeduction(
            orderMessage.getSeckillActivityId(),
            orderMessage.getQuantity(),
            inventory.getRemainingStock(),
            System.currentTimeMillis()
        );
        if (confirmRows == 0) {
            throw new IllegalStateException("数据库库存扣减失败");
        }

        int activityRows = seckillActivityMapper.decreaseRemainingStock(
            orderMessage.getSeckillActivityId(),
            orderMessage.getQuantity(),
            System.currentTimeMillis()
        );
        if (activityRows == 0) {
            throw new IllegalStateException("秒杀活动库存更新失败");
        }

        SeckillOrder order = new SeckillOrder();
        order.setOrderId(orderMessage.getOrderId());
        order.setUserId(orderMessage.getUserId());
        order.setSeckillActivityId(orderMessage.getSeckillActivityId());
        order.setProductId(activity.getProductId());
        order.setPrice(activity.getSeckillPrice() == null ? BigDecimal.ZERO : activity.getSeckillPrice());
        order.setQuantity(orderMessage.getQuantity());
        order.setStatus(0);
        order.setCreateTime(orderMessage.getCreateTime());
        order.setUpdateTime(orderMessage.getCreateTime());
        seckillOrderMapper.insert(order);
    }

    @Transactional
    public void updateOrderPaid(PaymentSuccessMessage paymentMessage) {
        int updated = seckillOrderMapper.updateStatus(
            paymentMessage.getOrderId(),
            0,
            1,
            paymentMessage.getPaymentMethod(),
            paymentMessage.getPayTime()
        );
        if (updated == 0) {
            SeckillOrder existing = seckillOrderMapper.selectByOrderId(paymentMessage.getOrderId());
            if (existing == null || existing.getStatus() == null || existing.getStatus() != 1) {
                throw new IllegalStateException("订单状态更新失败");
            }
        }
    }
}
