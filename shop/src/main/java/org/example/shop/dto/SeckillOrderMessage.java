package org.example.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀订单创建消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderMessage {
    private Long orderId;
    private Long userId;
    private Long seckillActivityId;
    private Integer quantity;
    private Long createTime;
}
