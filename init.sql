-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20),
  `email` varchar(100),
  `create_time` bigint,
  `update_time` bigint,
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `price` decimal(10, 2) NOT NULL,
  `stock` int NOT NULL DEFAULT 0,
  `category` varchar(100),
  `image_url` varchar(500),
  `status` int NOT NULL DEFAULT 1 COMMENT '0: 下架, 1: 上架',
  `create_time` bigint,
  `update_time` bigint,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例商品数据
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `category`, `status`, `create_time`, `update_time`) VALUES
('iPhone 15 Pro', '苹果最新旗舰手机', 7999.00, 100, '手机', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('MacBook Pro 16', '高性能笔记本电脑', 19999.00, 50, '电脑', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('AirPods Pro', '无线降噪耳机', 1999.00, 200, '配件', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('iPad Air', '平板电脑', 4999.00, 80, '平板', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('Apple Watch', '智能手表', 2999.00, 120, '穿戴', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);
