package org.example.shop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.shop.entity.SeckillOrder;

@Mapper
public interface SeckillOrderMapper {

    int insert(SeckillOrder seckillOrder);

    SeckillOrder selectByOrderId(Long orderId);

    int updateStatus(@Param("orderId") Long orderId,
                     @Param("currentStatus") Integer currentStatus,
                     @Param("targetStatus") Integer targetStatus,
                     @Param("paymentMethod") String paymentMethod,
                     @Param("updateTime") Long updateTime);
}
