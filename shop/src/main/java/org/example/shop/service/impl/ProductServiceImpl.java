package org.example.shop.service.impl;

import org.example.shop.entity.Product;
import org.example.shop.mapper.ProductMapper;
import org.example.shop.service.ProductService;
import org.example.shop.util.CacheUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 * 包含缓存穿透、击穿、雪崩的防御逻辑
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CacheUtil cacheUtil;

    // 缓存配置
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final long PRODUCT_CACHE_EXPIRE = 30 * 60; // 30 分钟
    private static final long LOCK_EXPIRE = 10; // 10 秒

    public ProductServiceImpl(ProductMapper productMapper, CacheUtil cacheUtil) {
        this.productMapper = productMapper;
        this.cacheUtil = cacheUtil;
    }

    /**
     * 获取商品详情（带缓存防御）
     * 防御策略：
     * 1. 缓存穿透：存储空值 + 布隆过滤器思想
     * 2. 缓存击穿：互斥锁
     * 3. 缓存雪崩：随机过期时间
     */
    @Override
    public Product getProductDetail(Long id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;

        // 1. 尝试从缓存获取
        Object cachedValue = cacheUtil.get(cacheKey);
        if (cachedValue != null) {
            return (Product) cachedValue;
        }

        // 2. 检查是否为空值缓存（防缓存穿透）
        if (cacheUtil.isNull(cacheKey)) {
            return null;
        }

        // 3. 尝试获取互斥锁（防缓存击穿）
        String lockKey = cacheKey + ":lock";
        boolean lockAcquired = cacheUtil.tryLock(lockKey, LOCK_EXPIRE);

        if (lockAcquired) {
            try {
                // 再次检查缓存（双重检查）
                cachedValue = cacheUtil.get(cacheKey);
                if (cachedValue != null) {
                    return (Product) cachedValue;
                }

                // 从数据库查询
                Product product = productMapper.selectById(id);

                if (product == null) {
                    // 存储空值缓存，防止缓存穿透
                    cacheUtil.setNull(cacheKey);
                } else {
                    // 设置缓存（防雪崩：随机过期时间）
                    cacheUtil.set(cacheKey, product, PRODUCT_CACHE_EXPIRE);
                }

                return product;
            } finally {
                // 释放互斥锁
                cacheUtil.unlock(lockKey);
            }
        } else {
            // 未获取到锁，等待后重试
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 递归重试
            return getProductDetail(id);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }

    @Override
    public boolean createProduct(Product product) {
        product.setCreateTime(System.currentTimeMillis());
        product.setUpdateTime(System.currentTimeMillis());
        return productMapper.insert(product) > 0;
    }

    @Override
    public boolean updateProduct(Product product) {
        product.setUpdateTime(System.currentTimeMillis());
        // 更新后清除缓存
        cacheUtil.delete(PRODUCT_CACHE_PREFIX + product.getId());
        return productMapper.update(product) > 0;
    }

    @Override
    public boolean deleteProduct(Long id) {
        // 删除前清除缓存
        cacheUtil.delete(PRODUCT_CACHE_PREFIX + id);
        return productMapper.delete(id) > 0;
    }
}
