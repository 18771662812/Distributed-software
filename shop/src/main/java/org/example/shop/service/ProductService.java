package org.example.shop.service;

import org.example.shop.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    /**
     * 获取商品详情（带缓存）
     */
    Product getProductDetail(Long id);

    /**
     * 获取所有商品
     */
    List<Product> getAllProducts();

    /**
     * 创建商品
     */
    boolean createProduct(Product product);

    /**
     * 更新商品
     */
    boolean updateProduct(Product product);

    /**
     * 删除商品
     */
    boolean deleteProduct(Long id);
}
