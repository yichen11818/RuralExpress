-- 选择数据库
USE rural_express;

-- 设置连接字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 修改表的字符集
ALTER TABLE t_courier CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 重新删除并插入快递员数据（使用直接插入中文）
DELETE FROM t_courier;

-- 添加第一个快递员数据
INSERT INTO `t_courier` (
    `id`, `user_id`, `service_area`, `service_status`, `audit_status`,
    `rating`, `balance`, `completed_orders`, `id_card_front`, `id_card_back`,
    `work_start_time`, `work_end_time`, `vehicle`, `introduction`, `response_time`,
    `created_at`, `updated_at`
) VALUES (
    1, 1, 
    '南山区', 
    1, 1,
    4.9, 0.00, 326,
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    '08:00', '20:00',
    '电动车',
    '5年快递配送经验，熟悉南山区域路线',
    5,
    NOW(), NOW()
);

-- 添加第二个快递员
INSERT INTO `t_courier` (
    `id`, `user_id`, `service_area`, `service_status`, `audit_status`,
    `rating`, `balance`, `completed_orders`, `id_card_front`, `id_card_back`,
    `work_start_time`, `work_end_time`, `vehicle`, `introduction`, `response_time`,
    `created_at`, `updated_at`
) VALUES (
    2, 2, 
    '福田区', 
    1, 1,
    4.7, 50.00, 218,
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    '09:00', '21:00',
    '摩托车',
    '3年快递配送经验，服务态度好',
    8,
    NOW(), NOW()
); 