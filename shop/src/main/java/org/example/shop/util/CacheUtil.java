package org.example.shop.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类
 * 包含缓存穿透、击穿、雪崩的防御逻辑
 */
@Component
public class CacheUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // 空值缓存前缀
    private static final String CACHE_NULL_PREFIX = "cache:null:";
    // 互斥锁前缀
    private static final String LOCK_PREFIX = "lock:";
    // 空值缓存过期时间（秒）
    private static final long NULL_CACHE_EXPIRE = 2 * 60;

    public CacheUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取缓存值
     * @param key 缓存键
     * @return 缓存值，不存在返回 null
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存值（防雪崩：随机过期时间）
     * @param key 缓存键
     * @param value 缓存值
     * @param expireSeconds 过期时间（秒）
     */
    public void set(String key, Object value, long expireSeconds) {
        // 随机增加 0-5 分钟，防止缓存雪崩
        long randomExpire = expireSeconds + (long) (Math.random() * 300);
        redisTemplate.opsForValue().set(key, value, randomExpire, TimeUnit.SECONDS);
    }

    /**
     * 设置空值缓存（防缓存穿透）
     * @param key 缓存键
     */
    public void setNull(String key) {
        String nullKey = CACHE_NULL_PREFIX + key;
        redisTemplate.opsForValue().set(nullKey, "NULL", NULL_CACHE_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 检查是否为空值缓存（防缓存穿透）
     * @param key 缓存键
     * @return true 表示该键被缓存为空值
     */
    public boolean isNull(String key) {
        String nullKey = CACHE_NULL_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(nullKey));
    }

    /**
     * 尝试获取互斥锁（防缓存击穿）
     * @param key 缓存键
     * @param lockExpireSeconds 锁过期时间（秒）
     * @return true 表示获取锁成功
     */
    public boolean tryLock(String key, long lockExpireSeconds) {
        String lockKey = LOCK_PREFIX + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", lockExpireSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放互斥锁
     * @param key 缓存键
     */
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.delete(lockKey);
    }

    /**
     * 删除缓存
     * @param key 缓存键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
        redisTemplate.delete(CACHE_NULL_PREFIX + key);
    }

    /**
     * 检查缓存是否存在
     * @param key 缓存键
     * @return true 表示缓存存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
