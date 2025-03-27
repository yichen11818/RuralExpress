-- 选择数据库
USE rural_express;

-- 首先检查字段定义
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'rural_express' 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'service_area';

-- 备份并删除旧表
RENAME TABLE t_courier TO t_courier_old;

-- 创建新表，确保service_area有足够长度
CREATE TABLE IF NOT EXISTS `t_courier` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '快递员ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '关联用户ID',
    `service_area` VARCHAR(255) NOT NULL COMMENT '服务区域',
    `service_status` TINYINT(1) DEFAULT 0 COMMENT '服务状态(0-休息,1-接单中)',
    `audit_status` TINYINT(1) DEFAULT 0 COMMENT '审核状态(0-待审核,1-已通过,2-未通过)',
    `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
    `balance` DECIMAL(10,2) DEFAULT 0 COMMENT '账户余额',
    `completed_orders` INT DEFAULT 0 COMMENT '已完成订单数',
    `id_card_front` VARCHAR(255) NOT NULL COMMENT '身份证正面照片URL',
    `id_card_back` VARCHAR(255) NOT NULL COMMENT '身份证背面照片URL',
    `work_start_time` VARCHAR(5) DEFAULT '08:00' COMMENT '工作开始时间',
    `work_end_time` VARCHAR(5) DEFAULT '20:00' COMMENT '工作结束时间',
    `vehicle` VARCHAR(50) COMMENT '交通工具',
    `introduction` VARCHAR(500) COMMENT '简介',
    `response_time` INT DEFAULT 15 COMMENT '平均响应时间(分钟)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员表';

-- 添加一个最简单的快递员测试数据
INSERT INTO `t_courier` (
    `user_id`, `service_area`, `id_card_front`, `id_card_back`, 
    `service_status`, `audit_status`,
    `created_at`, `updated_at`
) VALUES (
    1, 
    '南山', 
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    1, 
    1,
    NOW(), 
    NOW()
);

-- 添加另一个测试快递员
INSERT INTO `t_courier` (
    `user_id`, `service_area`, `id_card_front`, `id_card_back`, 
    `service_status`, `audit_status`,
    `created_at`, `updated_at`
) VALUES (
    2, 
    '福田', 
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    1, 
    1,
    NOW(), 
    NOW()
); 