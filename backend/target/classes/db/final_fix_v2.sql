-- 选择数据库
USE rural_express;

-- 清理所有大于ID 3的轮播图和公告数据（彻底清理重复数据）
DELETE FROM banner WHERE id > 3;
DELETE FROM notice WHERE id > 3;

-- 首先修改service_area字段长度
ALTER TABLE t_courier MODIFY COLUMN service_area VARCHAR(255) NOT NULL COMMENT '服务区域';

-- 检查是否存在测试用户
INSERT IGNORE INTO `t_user` (
    `id`, `phone`, `password`, `nickname`, `avatar`, `gender`, 
    `real_name`, `user_type`, `status`, `created_at`, `updated_at`
) VALUES (
    1, '13800138000', 
    '$2a$10$uxuOPJR9vUvK6uAJFVvYMOQlkqH1eqSBSOQ9VVMx9Cx/VLbAVIczS', -- 密码: 123456
    '测试用户', 
    '/static/images/default-avatar.png', 
    0, 
    '张三', 
    0, 
    0, 
    NOW(), 
    NOW()
);

-- 删除现有快递员数据（如果有）
DELETE FROM t_courier WHERE user_id = 1;

-- 添加测试快递员数据（使用较短的服务区域描述）
INSERT INTO `t_courier` (
    `user_id`, `service_area`, `service_status`, `audit_status`,
    `rating`, `balance`, `completed_orders`, `id_card_front`, `id_card_back`,
    `work_start_time`, `work_end_time`, `vehicle`, `introduction`, `response_time`,
    `created_at`, `updated_at`
) VALUES (
    1,
    '南山区',
    1, -- 接单中
    1, -- 已通过审核
    4.9,
    0.00,
    326,
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    '08:00',
    '20:00',
    '电动车',
    '5年快递配送经验，熟悉南山区域路线',
    5,
    NOW(),
    NOW()
);

-- 添加第二个测试快递员（确保查询能返回多条数据）
INSERT IGNORE INTO `t_user` (
    `id`, `phone`, `password`, `nickname`, `avatar`, `gender`, 
    `real_name`, `user_type`, `status`, `created_at`, `updated_at`
) VALUES (
    2, '13800138001', 
    '$2a$10$uxuOPJR9vUvK6uAJFVvYMOQlkqH1eqSBSOQ9VVMx9Cx/VLbAVIczS', -- 密码: 123456
    '测试快递员', 
    '/static/images/default-avatar.png', 
    1, 
    '李四', 
    1, 
    0, 
    NOW(), 
    NOW()
);

-- 添加第二个快递员（使用较短的服务区域描述）
INSERT INTO `t_courier` (
    `user_id`, `service_area`, `service_status`, `audit_status`,
    `rating`, `balance`, `completed_orders`, `id_card_front`, `id_card_back`,
    `work_start_time`, `work_end_time`, `vehicle`, `introduction`, `response_time`,
    `created_at`, `updated_at`
) VALUES (
    2,
    '福田区',
    1, -- 接单中
    1, -- 已通过审核
    4.7,
    50.00,
    218,
    '/static/images/id-card-front.jpg',
    '/static/images/id-card-back.jpg',
    '09:00',
    '21:00',
    '摩托车',
    '3年快递配送经验，服务态度好',
    8,
    NOW(),
    NOW()
); 