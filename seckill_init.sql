-- 秒杀活动表
CREATE TABLE IF NOT EXISTS `seckill_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `activity_name` varchar(255) NOT NULL,
  `seckill_price` decimal(10, 2) NOT NULL,
  `total_stock` int NOT NULL,
  `remaining_stock` int NOT NULL,
  `start_time` bigint NOT NULL,
  `end_time` bigint NOT NULL,
  `status` int NOT NULL DEFAULT 0 COMMENT '0: 未开始, 1: 进行中, 2: 已结束',
  `create_time` bigint,
  `update_time` bigint,
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 秒杀订单表
CREATE TABLE IF NOT EXISTS `seckill_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL UNIQUE COMMENT '雪花算法生成的订单ID',
  `user_id` bigint NOT NULL,
  `seckill_activity_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `status` int NOT NULL DEFAULT 0 COMMENT '0: 待支付, 1: 已支付, 2: 已发货, 3: 已完成, 4: 已取消',
  `payment_method` varchar(50),
  `create_time` bigint,
  `update_time` bigint,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_seckill_activity_id` (`seckill_activity_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 库存表（可选，用于数据库端库存管理）
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `seckill_activity_id` bigint NOT NULL,
  `total_stock` int NOT NULL,
  `sold_stock` int NOT NULL DEFAULT 0,
  `remaining_stock` int NOT NULL,
  `create_time` bigint,
  `update_time` bigint,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_activity` (`product_id`, `seckill_activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例秒杀活动
INSERT INTO `seckill_activity` (`product_id`, `activity_name`, `seckill_price`, `total_stock`, `remaining_stock`, `start_time`, `end_time`, `status`, `create_time`, `update_time`)
VALUES 
(1, 'iPhone 15 Pro 秒杀', 5999.00, 100, 100, UNIX_TIMESTAMP()*1000, (UNIX_TIMESTAMP()+3600)*1000, 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
(2, 'MacBook Pro 秒杀', 15999.00, 50, 50, UNIX_TIMESTAMP()*1000, (UNIX_TIMESTAMP()+3600)*1000, 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
