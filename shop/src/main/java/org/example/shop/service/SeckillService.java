package org.example.shop.service;

/**
 * 秒杀服务接口
 */
public interface SeckillService {
    
    /**
     * 秒杀下单
     * @param userId 用户ID
     * @param seckillActivityId 秒杀活动ID
     * @return 订单ID（成功）或错误信息（失败）
     */
    String seckillOrder(Long userId, Long seckillActivityId);
    
    /**
     * 库存预热
     * @param seckillActivityId 秒杀活动ID
     */
    void preloadStock(Long seckillActivityId);
    
    /**
     * 获取秒杀活动剩余库存
     * @param seckillActivityId 秒杀活动ID
     * @return 剩余库存数量
     */
    Integer getRemainStock(Long seckillActivityId);
}
