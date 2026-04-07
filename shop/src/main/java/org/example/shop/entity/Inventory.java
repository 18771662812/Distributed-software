package org.example.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 库存实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private Long seckillActivityId;
    private Integer totalStock;
    private Integer soldStock;
    private Integer remainingStock;
    private Long createTime;
    private Long updateTime;
}
