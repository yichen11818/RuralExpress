-- 使用数据库
USE rural_express;

-- ===== 修复物流信息查询问题 =====

-- 为t_order表添加tracking_no字段
ALTER TABLE `t_order` 
ADD COLUMN IF NOT EXISTS `tracking_no` VARCHAR(50) COMMENT '物流单号' AFTER `order_no`;

-- 为t_order表添加company_id字段，关联物流公司
ALTER TABLE `t_order` 
ADD COLUMN IF NOT EXISTS `company_id` BIGINT(20) COMMENT '物流公司ID' AFTER `courier_id`;

-- 为所有已有订单随机生成物流单号 (形如: RX + 14位数字)
UPDATE `t_order` 
SET `tracking_no` = CONCAT('RX', LPAD(FLOOR(RAND() * 100000000000000), 14, '0'))
WHERE `tracking_no` IS NULL OR `tracking_no` = '';

-- 创建物流公司表 (如果不存在)
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

-- 检查物流公司表中是否有数据
INSERT INTO `t_express_company` (`name`, `code`, `logo`, `status`)
SELECT '乡递通快递', 'RDEX', '/static/images/company-logo.png', 1
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM `t_express_company` WHERE `code` = 'RDEX');

-- 为已有订单分配物流公司
UPDATE `t_order` SET `company_id` = 
    (SELECT `id` FROM `t_express_company` WHERE `code` = 'RDEX' LIMIT 1)
WHERE `company_id` IS NULL;

-- 创建物流信息表 (如果不存在)
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

-- 检查SQL错误的具体语句是否可执行
SELECT 
    o.tracking_no, 
    o.status, 
    o.receiver_name, 
    o.receiver_address, 
    o.sender_name, 
    o.sender_address, 
    o.created_at, 
    o.updated_at, 
    '预计明天送达' as estimated_delivery_time, 
    CONCAT('快递包裹-', o.weight, 'kg') as package_description, 
    o.courier_id, 
    c.name as company_name, 
    c.logo as company_logo 
FROM t_order o 
LEFT JOIN t_express_company c ON o.company_id = c.id 
WHERE o.id = 18 LIMIT 1; 