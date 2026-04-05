package org.example.shop.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.shop.common.Result;
import org.example.shop.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀控制器
 */
@Slf4j
@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 秒杀下单接口
     * @param userId 用户ID
     * @param seckillActivityId 秒杀活动ID
     * @return 订单ID或错误信息
     */
    @PostMapping("/order")
    public Result<String> seckillOrder(
            @RequestParam Long userId,
            @RequestParam Long seckillActivityId) {
        try {
            String result = seckillService.seckillOrder(userId, seckillActivityId);

            if (result.startsWith("success")) {
                String orderId = result.substring(8);
                log.info("秒杀下单成功 - 用户ID: {}, 订单ID: {}", userId, orderId);
                return Result.success("秒杀下单成功，订单ID: " + orderId);
            } else {
                String errorMsg = result.substring(6);
                log.warn("秒杀下单失败 - 用户ID: {}, 原因: {}", userId, errorMsg);
                return Result.error(errorMsg);
            }

        } catch (Exception e) {
            log.error("秒杀下单异常", e);
            return Result.error("系统异常，请稍后重试");
        }
    }

    /**
     * 库存预热接口
     * @param seckillActivityId 秒杀活动ID
     * @return 预热结果
     */
    @PostMapping("/preload")
    public Result<String> preloadStock(@RequestParam Long seckillActivityId) {
        try {
            seckillService.preloadStock(seckillActivityId);
            log.info("库存预热成功 - 活动ID: {}", seckillActivityId);
            return Result.success("库存预热成功");
        } catch (Exception e) {
            log.error("库存预热失败", e);
            return Result.error("库存预热失败");
        }
    }

    /**
     * 获取剩余库存接口
     * @param seckillActivityId 秒杀活动ID
     * @return 剩余库存数量
     */
    @GetMapping("/stock/{seckillActivityId}")
    public Result<Integer> getRemainStock(@PathVariable Long seckillActivityId) {
        try {
            Integer remainStock = seckillService.getRemainStock(seckillActivityId);
            return Result.success(remainStock);
        } catch (Exception e) {
            log.error("获取库存失败", e);
            return Result.error("获取库存失败");
        }
    }
}
