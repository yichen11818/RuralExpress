-- 选择数据库
USE rural_express;

-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `gender` TINYINT(1) DEFAULT 0 COMMENT '性别(0-未知,1-男,2-女)',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `verified` TINYINT(1) DEFAULT 0 COMMENT '是否实名认证(0-否,1-是)',
    `user_type` TINYINT(1) DEFAULT 0 COMMENT '用户类型(0-普通用户,1-快递员,2-管理员)',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态(0-正常,1-禁用)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 地址表
CREATE TABLE IF NOT EXISTS `t_address` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `street` VARCHAR(50) COMMENT '街道',
    `detail` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认地址(0-否,1-是)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址表';

-- 快递员表
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

-- 创建轮播图表
CREATE TABLE IF NOT EXISTS banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    link_url VARCHAR(255) COMMENT '点击链接URL',
    title VARCHAR(100) COMMENT '标题',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '轮播图表';

-- 创建公告表
CREATE TABLE IF NOT EXISTS notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL COMMENT '公告内容',
    link_url VARCHAR(255) COMMENT '点击链接URL',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    start_time DATETIME COMMENT '开始展示时间',
    end_time DATETIME COMMENT '结束展示时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '公告表';

-- 插入轮播图测试数据
INSERT INTO banner (image_url, link_url, title, sort_order, is_active)
VALUES 
('/static/images/banner1.jpg', '/pages/notice/detail?id=1', '乡递通平台上线啦', 1, 1),
('/static/images/banner2.jpg', '/pages/activity/detail?id=1', '快递送货优惠活动', 2, 1),
('/static/images/banner3.jpg', '/pages/courier/recruitment', '快递员招募计划', 3, 1);

-- 插入公告测试数据
INSERT INTO notice (content, link_url, sort_order, is_active)
VALUES 
('乡递通平台正式上线，为乡村快递提供便捷服务', '/pages/notice/detail?id=1', 1, 1),
('成为快递员，每单收益最高可达10元', '/pages/courier/recruitment', 2, 1),
('乡递通招募村镇快递员，详情查看招募页面', '/pages/notice/detail?id=3', 3, 1);

-- 添加一个测试用户数据
INSERT INTO `t_user` (
    `phone`, `password`, `nickname`, `avatar`, `gender`, 
    `real_name`, `user_type`, `status`, `created_at`, `updated_at`
) VALUES (
    '13800138000', 
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