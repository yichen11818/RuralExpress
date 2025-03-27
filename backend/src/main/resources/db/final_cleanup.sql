-- 选择数据库
USE rural_express;

-- 删除所有旧的轮播图和公告数据
DELETE FROM banner;
DELETE FROM notice;

-- 重新插入轮播图数据（确保编码正确）
INSERT INTO banner (id, image_url, link_url, title, sort_order, is_active, create_time, update_time)
VALUES 
(1, '/static/images/banner1.jpg', '/pages/notice/detail?id=1', '乡递通平台上线啦', 1, 1, NOW(), NOW()),
(2, '/static/images/banner2.jpg', '/pages/activity/detail?id=1', '快递送货优惠活动', 2, 1, NOW(), NOW()),
(3, '/static/images/banner3.jpg', '/pages/courier/recruitment', '快递员招募计划', 3, 1, NOW(), NOW());

-- 重新插入公告数据（确保编码正确）
INSERT INTO notice (id, content, link_url, sort_order, is_active, create_time, update_time)
VALUES 
(1, '乡递通平台正式上线，为乡村快递提供便捷服务', '/pages/notice/detail?id=1', 1, 1, NOW(), NOW()),
(2, '成为快递员，每单收益最高可达10元', '/pages/courier/recruitment', 2, 1, NOW(), NOW()),
(3, '乡递通招募村镇快递员，详情查看招募页面', '/pages/notice/detail?id=3', 3, 1, NOW(), NOW());

-- 删除并重新插入快递员数据（修复编码问题）
DELETE FROM t_courier;

-- 添加测试快递员数据（使用 CONVERT 保证UTF8编码）
INSERT INTO `t_courier` (
    `id`, `user_id`, `service_area`, `service_status`, `audit_status`,
    `rating`, `balance`, `completed_orders`, `id_card_front`, `id_card_back`,
    `work_start_time`, `work_end_time`, `vehicle`, `introduction`, `response_time`,
    `created_at`, `updated_at`
) VALUES (
    1, 1, 
    CONVERT('南山区' USING utf8mb4), 
    1, 1,
    4.9, 0.00, 326,
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    '08:00', '20:00',
    CONVERT('电动车' USING utf8mb4),
    CONVERT('5年快递配送经验，熟悉南山区域路线' USING utf8mb4),
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
    CONVERT('福田区' USING utf8mb4), 
    1, 1,
    4.7, 50.00, 218,
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    '09:00', '21:00',
    CONVERT('摩托车' USING utf8mb4),
    CONVERT('3年快递配送经验，服务态度好' USING utf8mb4),
    8,
    NOW(), NOW()
); 