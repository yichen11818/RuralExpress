-- 使用数据库
USE rural_express;
-- 添加缺失的用户数据
-- 创建快递员标签表
CREATE TABLE IF NOT EXISTS `t_courier_tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `courier_id` BIGINT(20) NOT NULL COMMENT '快递员ID',
    `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `tag_type` TINYINT(1) DEFAULT 0 COMMENT '标签类型(0-系统标签,1-用户标签)',
    `count` INT DEFAULT 1 COMMENT '标签次数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_courier_id` (`courier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员标签表';

-- ================ 新增快递员评价 ================
-- 为ID=7的快递员(青岛快递员1)添加评价
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(5, 7, 10, 5, '送货速度很快，态度也很好', 0, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(6, 7, 11, 4, '快递完好送达，配送员很守时', 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 7, 12, 5, '服务非常专业，下雨天还准时送达', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 为ID=8的快递员(青岛快递员2)添加评价
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(8, 8, 13, 4, '快递员很有礼貌，包装完好', 0, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(9, 8, 14, 5, '服务很周到，还主动帮忙搬上楼', 0, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 8, 15, 3, '送货时间有点延迟，但态度还不错', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 为ID=2的快递员(福田快递员)添加更多评价
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(11, 2, 16, 5, '快递员非常专业，物品完好送达', 0, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(12, 2, 17, 4, '服务态度很好，值得表扬', 0, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));

-- ================ 添加快递员回复 ================
-- 为快递员ID=7的评价添加回复
UPDATE `t_review` SET `reply` = '感谢您的好评，我们会继续提供优质服务' WHERE `courier_id` = 7 AND `id` = 5;
UPDATE `t_review` SET `reply` = '谢谢您的肯定，我们一直以准时为服务准则' WHERE `courier_id` = 7 AND `id` = 6;
UPDATE `t_review` SET `reply` = '非常感谢您的认可，无论天气如何我们都会尽力提供最好的服务' WHERE `courier_id` = 7 AND `id` = 7;

-- 为快递员ID=8的评价添加回复
UPDATE `t_review` SET `reply` = '谢谢您的评价，我们将继续保持良好的服务态度' WHERE `courier_id` = 8 AND `id` = 8;
UPDATE `t_review` SET `reply` = '感谢您的好评，为客户提供便利是我们的责任' WHERE `courier_id` = 8 AND `id` = 9;
UPDATE `t_review` SET `reply` = '非常抱歉造成延误，我们会改进配送时间管理，谢谢您的理解' WHERE `courier_id` = 8 AND `id` = 10;

-- 为快递员ID=2的评价添加更多回复
UPDATE `t_review` SET `reply` = '感谢您的认可，我们会保持专业服务水准' WHERE `courier_id` = 2 AND `id` = 11;
UPDATE `t_review` SET `reply` = '谢谢您的好评，欢迎再次选择我们的服务' WHERE `courier_id` = 2 AND `id` = 12;

-- ================ 添加快递员标签 ================
-- 插入快递员标签数据
INSERT INTO `t_courier_tag` 
    (`courier_id`, `tag_name`, `tag_type`, `count`, `created_at`, `updated_at`) 
VALUES 
    -- 快递员ID=2的标签
    (2, '送货快', 0, 15, NOW(), NOW()),
    (2, '态度好', 0, 12, NOW(), NOW()),
    (2, '专业', 0, 8, NOW(), NOW()),
    (2, '礼貌', 1, 6, NOW(), NOW()),
    
    -- 快递员ID=5的标签
    (5, '送货快', 0, 18, NOW(), NOW()),
    (5, '守时', 0, 14, NOW(), NOW()),
    (5, '热情', 1, 9, NOW(), NOW()),
    
    -- 快递员ID=7的标签
    (7, '准时', 0, 10, NOW(), NOW()),
    (7, '服务好', 0, 8, NOW(), NOW()),
    (7, '专业', 1, 5, NOW(), NOW()),
    (7, '有耐心', 1, 3, NOW(), NOW()),
    
    -- 快递员ID=8的标签
    (8, '礼貌', 0, 7, NOW(), NOW()),
    (8, '细心', 0, 5, NOW(), NOW()),
    (8, '热情', 1, 4, NOW(), NOW());

-- 显示新增的评价记录
SELECT * FROM `t_review` ORDER BY courier_id, id;

-- 显示新增的标签记录
SELECT * FROM `t_courier_tag` ORDER BY courier_id, count DESC; 