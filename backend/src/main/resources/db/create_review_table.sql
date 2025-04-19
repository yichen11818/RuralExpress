-- 使用数据库
USE rural_express;

-- 创建评价表
CREATE TABLE IF NOT EXISTS `t_review` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `courier_id` BIGINT(20) NOT NULL COMMENT '快递员ID',
    `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
    `rating` INT(1) NOT NULL COMMENT '评分(1-5)',
    `content` VARCHAR(500) COMMENT '评价内容',
    `reply` VARCHAR(500) COMMENT '快递员回复',
    `anonymous` TINYINT(1) DEFAULT 0 COMMENT '是否匿名(0-否,1-是)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_courier_id` (`courier_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 添加一些测试数据
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(1, 2, 1, 5, '快递员服务很好，送货速度快', 0, NOW(), NOW()),
(2, 2, 2, 4, '服务态度不错，但送货稍有延迟', 0, NOW(), NOW()),
(3, 5, 3, 5, '快递员非常热情，服务很周到', 1, NOW(), NOW()),
(4, 5, 4, 3, '送货速度可以再快一些', 0, NOW(), NOW());

-- 添加快递员回复
UPDATE `t_review` SET `reply` = '感谢您的好评，我会继续努力' WHERE `id` = 1;
UPDATE `t_review` SET `reply` = '非常抱歉送货有延迟，下次会更加注意时间' WHERE `id` = 2; 