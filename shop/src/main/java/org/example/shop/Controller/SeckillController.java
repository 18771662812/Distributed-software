package org.example.shop.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.shop.common.Result;
import org.example.shop.entity.SeckillOrder;
import org.example.shop.service.SeckillService;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀控制器
 */
@Slf4j
@RestController
@RequestMapping("/seckill")
public class SeckillController {

    private final SeckillService seckillService;

    public SeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    /**
     * 秒杀下单接口
     */
    @PostMapping("/order")
    public Result<String> seckillOrder(
        @RequestParam Long userId,
        @RequestParam Long seckillActivityId) {
        try {
            String result = seckillService.seckillOrder(userId, seckillActivityId);
            if (result.startsWith("success:")) {
                String orderId = result.substring(8);
                log.info("秒杀下单成功 - 用户ID: {}, 订单ID: {}", userId, orderId);
                return Result.success("秒杀下单成功，订单ID: " + orderId);
            }
            String errorMsg = result.substring(6);
            log.warn("秒杀下单失败 - 用户ID: {}, 原因: {}", userId, errorMsg);
            return Result.error(errorMsg);
        } catch (Exception e) {
            log.error("秒杀下单异常", e);
            return Result.error("系统异常，请稍后重试");
        }
    }

    /**
     * 库存预热接口
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
     */
    @GetMapping("/stock/{seckillActivityId}")
    public Result<Integer> getRemainStock(@PathVariable Long seckillActivityId) {
        try {
            return Result.success(seckillService.getRemainStock(seckillActivityId));
        } catch (Exception e) {
            log.error("获取库存失败", e);
            return Result.error("获取库存失败");
        }
    }

    /**
     * 支付订单接口
     */
    @PostMapping("/pay")
    public Result<String> payOrder(@RequestParam Long orderId,
                                   @RequestParam(defaultValue = "mock") String paymentMethod) {
        try {
            String result = seckillService.payOrder(orderId, paymentMethod);
            if (result.startsWith("success:")) {
                return Result.success(result.substring(8));
            }
            return Result.error(result.substring(6));
        } catch (Exception e) {
            log.error("支付订单异常", e);
            return Result.error("支付订单失败");
        }
    }

    /**
     * 查询订单接口
     */
    @GetMapping("/order/{orderId}")
    public Result<SeckillOrder> getOrder(@PathVariable Long orderId) {
        try {
            SeckillOrder order = seckillService.getOrder(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            log.error("查询订单异常", e);
            return Result.error("查询订单失败");
        }
    }
}
