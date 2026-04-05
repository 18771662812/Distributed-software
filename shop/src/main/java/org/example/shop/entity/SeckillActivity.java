package org.example.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀活动实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private String activityName;
    private BigDecimal seckillPrice;
    private Integer totalStock;
    private Integer remainingStock;
    private Long startTime;
    private Long endTime;
    private Integer status; // 0: 未开始, 1: 进行中, 2: 已结束
    private Long createTime;
    private Long updateTime;
}
