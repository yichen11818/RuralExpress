-- 使用数据库
USE rural_express;

-- 查看原有约束
SELECT * FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'rural_express' 
AND TABLE_NAME = 't_courier_tag' 
AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 临时禁用外键约束检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除原有的外键约束
ALTER TABLE `t_courier_tag` DROP FOREIGN KEY `fk_tag_courier`;

-- 重新添加外键约束，指向正确的表
ALTER TABLE `t_courier_tag` 
ADD CONSTRAINT `fk_tag_courier` FOREIGN KEY (`courier_id`) 
REFERENCES `t_courier` (`id`) ON DELETE CASCADE;

-- 重新启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- ================ 添加快递员标签 ================
-- 清空已有标签数据（避免重复）
DELETE FROM `t_courier_tag`;

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
    (8, '热情', 1, 4, NOW(), NOW()),
    
    -- 快递员ID=9的标签
    (9, '有礼貌', 0, 5, NOW(), NOW()),
    (9, '准时', 0, 3, NOW(), NOW());

-- 确认标签已正确添加
SELECT * FROM `t_courier_tag` ORDER BY courier_id, count DESC; 