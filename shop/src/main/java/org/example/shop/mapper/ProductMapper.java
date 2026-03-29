package org.example.shop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.shop.entity.Product;

/**
 * 商品 Mapper 接口
 */
@Mapper
public interface ProductMapper {
    /**
     * 根据 ID 查询商品
     */
    Product selectById(Long id);

    /**
     * 查询所有商品
     */
    java.util.List<Product> selectAll();

    /**
     * 插入商品
     */
    int insert(Product product);

    /**
     * 更新商品
     */
    int update(Product product);

    /**
     * 删除商品
     */
    int delete(Long id);
}
