-- 使用数据库
USE rural_express;

-- ===== 第二阶段修复：添加更多缺失的物流字段 =====

-- 注意：如果字段已存在，以下语句可能会报错，但不影响后续执行
-- 为t_order表添加预计送达时间字段
ALTER TABLE `t_order` 
ADD COLUMN `estimated_delivery_time` DATETIME COMMENT '预计送达时间' 
AFTER `updated_at`;

-- 为t_order表添加包裹描述字段
ALTER TABLE `t_order` 
ADD COLUMN `package_description` VARCHAR(255) COMMENT '包裹描述' 
AFTER `weight`;

-- 更新已有订单的预计送达时间(为已有订单添加一个随机的预计送达时间，范围是下单时间后的1-3天)
UPDATE `t_order` 
SET `estimated_delivery_time` = DATE_ADD(`created_at`, INTERVAL FLOOR(1 + RAND() * 2) DAY)
WHERE `estimated_delivery_time` IS NULL AND `status` < 6;

-- 更新已有订单的包裹描述
UPDATE `t_order` 
SET `package_description` = CASE 
    WHEN `package_type` = 0 THEN '普通快递'
    WHEN `package_type` = 1 THEN '文件'
    WHEN `package_type` = 2 THEN '食品'
    WHEN `package_type` = 3 THEN '电子产品'
    WHEN `package_type` = 4 THEN '易碎品'
    ELSE CONCAT('包裹-', weight, 'kg')
END
WHERE `package_description` IS NULL;

-- 检查修改后的SQL查询是否可执行
SELECT 
    o.tracking_no, 
    o.status, 
    o.receiver_name, 
    o.receiver_address, 
    o.sender_name, 
    o.sender_address, 
    o.created_at, 
    o.updated_at, 
    o.estimated_delivery_time, 
    o.package_description, 
    o.courier_id, 
    c.name as company_name, 
    c.logo as company_logo 
FROM t_order o 
LEFT JOIN t_express_company c ON o.company_id = c.id 
WHERE o.id = 23 LIMIT 1;

-- 添加物流查询服务所需的视图
CREATE OR REPLACE VIEW v_order_logistics AS
SELECT 
    o.id as order_id,
    o.order_no,
    o.tracking_no,
    o.status,
    o.created_at as order_time,
    o.estimated_delivery_time,
    o.package_description,
    o.sender_name,
    o.sender_address,
    o.receiver_name,
    o.receiver_address,
    o.courier_id,
    u.real_name as courier_name,
    u.phone as courier_phone,
    c.name as company_name,
    c.logo as company_logo,
    c.code as company_code
FROM t_order o
LEFT JOIN t_express_company c ON o.company_id = c.id
LEFT JOIN t_courier cr ON o.courier_id = cr.id
LEFT JOIN t_user u ON cr.user_id = u.id; 