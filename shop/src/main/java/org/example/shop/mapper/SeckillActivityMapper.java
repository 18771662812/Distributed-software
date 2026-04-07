package org.example.shop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.shop.entity.SeckillActivity;

@Mapper
public interface SeckillActivityMapper {

    SeckillActivity selectById(Long id);

    int decreaseRemainingStock(@Param("id") Long id,
                               @Param("quantity") Integer quantity,
                               @Param("updateTime") Long updateTime);
}
