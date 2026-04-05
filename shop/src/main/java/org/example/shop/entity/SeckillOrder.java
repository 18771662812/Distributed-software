package org.example.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId; // 雪花算法生成的订单ID
    private Long userId;
    private Long seckillActivityId;
    private Long productId;
    private BigDecimal price;
    private Integer quantity;
    private Integer status; // 0: 待支付, 1: 已支付, 2: 已发货, 3: 已完成, 4: 已取消
    private String paymentMethod;
    private Long createTime;
    private Long updateTime;
}
