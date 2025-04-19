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

-- 添加缺失的用户数据
INSERT INTO `t_user` 
    (`id`, `phone`, `password`, `nickname`, `avatar`, `gender`, 
    `real_name`, `user_type`, `status`, `created_at`, `updated_at`) 
VALUES 
    -- 对应快递员ID=2的用户
    (2, '13800138021', '$2a$10$uxuOPJR9vUvK6uAJFVvYMOQlkqH1eqSBSOQ9VVMx9Cx/VLbAVIczS', '福田快递员', 
    '/static/images/default-avatar.png', 1, '李四', 1, 0, 
    NOW(), NOW()),
    
    -- 对应快递员ID=7的用户
    (103, '13800138103', '$2a$10$uxuOPJR9vUvK6uAJFVvYMOQlkqH1eqSBSOQ9VVMx9Cx/VLbAVIczS', '青岛快递员1', 
    '/static/images/default-avatar.png', 1, '王配送', 1, 0, 
    NOW(), NOW()),
    
    -- 对应快递员ID=8的用户
    (104, '13800138104', '$2a$10$uxuOPJR9vUvK6uAJFVvYMOQlkqH1eqSBSOQ9VVMx9Cx/VLbAVIczS', '青岛快递员2', 
    '/static/images/default-avatar.png', 1, '张配送', 1, 0, 
    NOW(), NOW());

-- 提示：执行上述SQL后，需要更新status字段值
-- 根据你提供的数据，快递员status应设置为1(正常)
UPDATE `t_courier` SET `status` = 1 WHERE `id` IN (2, 7, 8);

-- 显示已添加的用户记录
SELECT * FROM `t_user` WHERE `id` IN (2, 103, 104);

-- 显示对应的快递员记录
SELECT * FROM `t_courier` WHERE `user_id` IN (2, 103, 104);

-- ===== 添加t_order表所需字段 =====

-- 为t_order表添加tracking_no字段
ALTER TABLE `t_order` 
ADD COLUMN `tracking_no` VARCHAR(50) COMMENT '物流单号' AFTER `order_no`;

-- 为t_order表添加company_id字段，关联物流公司
ALTER TABLE `t_order` 
ADD COLUMN `company_id` BIGINT(20) COMMENT '物流公司ID' AFTER `courier_id`;

-- 为所有已有订单随机生成物流单号 (形如: RX + 14位数字)
UPDATE `t_order` 
SET `tracking_no` = CONCAT('RX', LPAD(FLOOR(RAND() * 100000000000000), 14, '0'))
WHERE `tracking_no` IS NULL;

-- 创建物流公司表
CREATE TABLE IF NOT EXISTS `t_express_company` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '公司ID',
    `name` VARCHAR(50) NOT NULL COMMENT '公司名称',
    `code` VARCHAR(20) COMMENT '公司编码',
    `logo` VARCHAR(255) COMMENT '公司Logo',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态(0-停用,1-启用)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流公司表';

-- 添加物流公司测试数据
INSERT INTO `t_express_company` (`name`, `code`, `logo`, `status`) VALUES
('乡递通快递', 'RDEX', '/static/images/company-logo.png', 1),
('顺丰速运', 'SF', '/static/images/sf-logo.png', 1),
('中通快递', 'ZTO', '/static/images/zto-logo.png', 1),
('申通快递', 'STO', '/static/images/sto-logo.png', 1),
('圆通速递', 'YTO', '/static/images/yto-logo.png', 1);

-- 为已有订单分配物流公司
UPDATE `t_order` SET `company_id` = 1 WHERE `company_id` IS NULL;

-- 创建物流信息表
CREATE TABLE IF NOT EXISTS `t_logistics` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '物流ID',
    `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
    `tracking_no` VARCHAR(50) COMMENT '物流单号',
    `company_id` BIGINT(20) COMMENT '物流公司ID',
    `content` VARCHAR(500) NOT NULL COMMENT '物流内容',
    `operator` VARCHAR(50) COMMENT '操作人',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_tracking_no` (`tracking_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表'; 