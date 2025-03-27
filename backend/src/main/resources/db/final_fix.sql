-- 选择数据库
USE rural_express;

-- 清理所有大于ID 3的轮播图和公告数据（彻底清理重复数据）
DELETE FROM banner WHERE id > 3;
DELETE FROM notice WHERE id > 3;

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

-- 添加测试快递员数据
INSERT INTO `t_courier` (
    `user_id`, `service_area`, `service_status`, `audit_status`,
    `rating`, `balance`, `completed_orders`, `id_card_front`, `id_card_back`,
    `work_start_time`, `work_end_time`, `vehicle`, `introduction`, `response_time`,
    `created_at`, `updated_at`
) VALUES (
    1,
    '广东省深圳市南山区',
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

INSERT INTO `t_courier` (
    `user_id`, `service_area`, `service_status`, `audit_status`,
    `rating`, `balance`, `completed_orders`, `id_card_front`, `id_card_back`,
    `work_start_time`, `work_end_time`, `vehicle`, `introduction`, `response_time`,
    `created_at`, `updated_at`
) VALUES (
    2,
    '广东省深圳市福田区',
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

-- 为Notice表添加缺失的字段
ALTER TABLE notice 
ADD COLUMN title VARCHAR(255) COMMENT '公告标题' AFTER id,
ADD COLUMN source VARCHAR(100) DEFAULT '乡递通' COMMENT '公告来源' AFTER content,
ADD COLUMN category VARCHAR(50) COMMENT '公告分类' AFTER source, 
ADD COLUMN is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶' AFTER category,
ADD COLUMN status TINYINT DEFAULT 1 COMMENT '发布状态(0-草稿，1-已发布)' AFTER is_top,
ADD COLUMN view_count INT DEFAULT 0 COMMENT '浏览次数' AFTER status,
ADD COLUMN create_by BIGINT COMMENT '创建者ID' AFTER update_time,
ADD COLUMN update_by BIGINT COMMENT '更新者ID' AFTER create_by;

-- 更新现有数据，使其适配新的字段结构
UPDATE notice SET 
  title = CONCAT('公告: ', LEFT(content, 20), IF(LENGTH(content) > 20, '...', '')),  -- 使用content的前20个字符作为标题
  source = '乡递通系统',  -- 默认来源
  category = CASE 
      WHEN sort_order = 1 THEN '系统公告'
      WHEN sort_order = 2 THEN '活动通知'
      WHEN sort_order = 3 THEN '服务变更'
      ELSE '其他'
  END,  -- 根据sort_order设置分类
  is_top = CASE WHEN sort_order <= 2 THEN TRUE ELSE FALSE END,  -- 前两个排序的设为置顶
  status = CASE WHEN is_active = TRUE THEN 1 ELSE 0 END,  -- 活跃状态转为发布状态
  view_count = FLOOR(RAND() * 100) + 10,  -- 随机设置一些浏览量
  create_by = 1,  -- 默认系统管理员ID
  update_by = 1;  -- 默认系统管理员ID 