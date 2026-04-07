package org.example.shop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.shop.entity.Inventory;

@Mapper
public interface InventoryMapper {

    Inventory selectByActivityId(Long seckillActivityId);

    int insert(Inventory inventory);

    int refreshStock(@Param("seckillActivityId") Long seckillActivityId,
                     @Param("totalStock") Integer totalStock,
                     @Param("soldStock") Integer soldStock,
                     @Param("remainingStock") Integer remainingStock,
                     @Param("updateTime") Long updateTime);

    int confirmDeduction(@Param("seckillActivityId") Long seckillActivityId,
                         @Param("quantity") Integer quantity,
                         @Param("expectedRemainingStock") Integer expectedRemainingStock,
                         @Param("updateTime") Long updateTime);

    int rollbackDeduction(@Param("seckillActivityId") Long seckillActivityId,
                          @Param("quantity") Integer quantity);
}
