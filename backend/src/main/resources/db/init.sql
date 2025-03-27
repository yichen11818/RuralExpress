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
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
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
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
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
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_courier_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员表';

-- 快递员标签表
CREATE TABLE IF NOT EXISTS `t_courier_tag` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `courier_id` BIGINT(20) NOT NULL COMMENT '快递员ID',
    `tag_name` VARCHAR(50) NOT NULL COMMENT '标签内容',
    `tag_type` TINYINT(1) DEFAULT 0 COMMENT '标签类型(0-系统标签,1-用户评价标签)',
    `count` INT DEFAULT 1 COMMENT '标签计数',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_courier_tag` (`courier_id`, `tag_name`),
    CONSTRAINT `fk_tag_courier` FOREIGN KEY (`courier_id`) REFERENCES `t_courier` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员标签表';

-- 订单表
CREATE TABLE IF NOT EXISTS `t_order` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(30) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `courier_id` BIGINT(20) COMMENT '快递员ID',
    `sender_name` VARCHAR(50) NOT NULL COMMENT '寄件人姓名',
    `sender_phone` VARCHAR(20) NOT NULL COMMENT '寄件人手机号',
    `sender_address` VARCHAR(255) NOT NULL COMMENT '寄件地址',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收件人手机号',
    `receiver_address` VARCHAR(255) NOT NULL COMMENT '收件地址',
    `package_type` TINYINT(1) NOT NULL COMMENT '包裹类型(0-小件,1-中件,2-大件)',
    `weight` DECIMAL(10,2) COMMENT '重量(kg)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '配送费',
    `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '订单状态(0-待接单,1-已接单,2-取件中,3-已取件,4-配送中,5-已送达,6-已完成,7-已取消)',
    `expected_pickup_time` DATETIME COMMENT '期望取件时间',
    `actual_pickup_time` DATETIME COMMENT '实际取件时间',
    `expected_delivery_time` DATETIME COMMENT '期望送达时间',
    `actual_delivery_time` DATETIME COMMENT '实际送达时间',
    `remark` VARCHAR(255) COMMENT '备注',
    `cancel_reason` VARCHAR(255) COMMENT '取消原因',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_courier_id` (`courier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单评价表
CREATE TABLE IF NOT EXISTS `t_evaluation` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `courier_id` BIGINT(20) NOT NULL COMMENT '快递员ID',
    `score` TINYINT(1) NOT NULL COMMENT '评分(1-5)',
    `content` VARCHAR(500) COMMENT '评价内容',
    `service_tags` VARCHAR(255) COMMENT '服务标签,多个用逗号分隔',
    `courier_tags` VARCHAR(255) COMMENT '快递员标签,多个用逗号分隔',
    `images` VARCHAR(1000) COMMENT '评价图片,多个用逗号分隔',
    `is_anonymous` TINYINT(1) DEFAULT 0 COMMENT '是否匿名(0-否,1-是)',
    `reply` VARCHAR(500) COMMENT '快递员回复',
    `reply_time` DATETIME COMMENT '回复时间',
    `user_to_courier` TINYINT(1) DEFAULT 0 COMMENT '评价方向(0-用户评价快递员,1-快递员评价用户)',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_order_id` (`order_id`, `user_to_courier`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_courier_id` (`courier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS `t_payment` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
    `payment_no` VARCHAR(64) NOT NULL COMMENT '支付流水号',
    `channel` VARCHAR(20) NOT NULL COMMENT '支付渠道',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态(0-未支付,1-已支付,2-已退款)',
    `payment_time` DATETIME COMMENT '支付时间',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_payment_no` (`payment_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`),
    CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `t_order` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `t_config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(64) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(255) NOT NULL COMMENT '配置值',
    `description` VARCHAR(255) COMMENT '配置描述',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 快递公司表
CREATE TABLE IF NOT EXISTS `t_express_company` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '公司ID',
    `name` VARCHAR(50) NOT NULL COMMENT '公司名称',
    `code` VARCHAR(20) NOT NULL COMMENT '公司编码',
    `logo` VARCHAR(255) COMMENT '公司LOGO',
    `phone` VARCHAR(20) COMMENT '公司联系电话',
    `website` VARCHAR(255) COMMENT '公司官网',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态(0-正常,1-禁用)',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_company_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递公司表';

-- 服务点表
CREATE TABLE IF NOT EXISTS `t_station` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '服务点ID',
    `name` VARCHAR(100) NOT NULL COMMENT '服务点名称',
    `logo` VARCHAR(255) COMMENT '服务点LOGO',
    `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `wechat` VARCHAR(50) COMMENT '微信联系号',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `longitude` DECIMAL(10,6) COMMENT '经度',
    `latitude` DECIMAL(10,6) COMMENT '纬度',
    `business_start_time` VARCHAR(5) DEFAULT '08:00' COMMENT '营业开始时间',
    `business_end_time` VARCHAR(5) DEFAULT '21:00' COMMENT '营业结束时间',
    `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
    `review_count` INT DEFAULT 0 COMMENT '评价数量',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态(0-正常,1-暂停营业,2-永久关闭)',
    `manager` VARCHAR(50) COMMENT '负责人',
    `manager_phone` VARCHAR(20) COMMENT '负责人电话',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_location` (`province`, `city`, `district`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务点表';

-- 服务点支持的快递公司关联表
CREATE TABLE IF NOT EXISTS `t_station_company` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `station_id` BIGINT(20) NOT NULL COMMENT '服务点ID',
    `company_id` BIGINT(20) NOT NULL COMMENT '快递公司ID',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_station_company` (`station_id`, `company_id`),
    CONSTRAINT `fk_sc_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_sc_company` FOREIGN KEY (`company_id`) REFERENCES `t_express_company` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务点支持的快递公司关联表';

-- 服务点图片表
CREATE TABLE IF NOT EXISTS `t_station_photo` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `station_id` BIGINT(20) NOT NULL COMMENT '服务点ID',
    `photo_url` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_station_id` (`station_id`),
    CONSTRAINT `fk_photo_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务点图片表';

-- 服务点评价表
CREATE TABLE IF NOT EXISTS `t_station_review` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `station_id` BIGINT(20) NOT NULL COMMENT '服务点ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `rating` TINYINT(1) NOT NULL COMMENT '评分(1-5)',
    `content` VARCHAR(500) COMMENT '评价内容',
    `is_anonymous` TINYINT(1) DEFAULT 0 COMMENT '是否匿名(0-否,1-是)',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_station_id` (`station_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_review_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务点评价表';

-- 包裹表
CREATE TABLE IF NOT EXISTS `t_package` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '包裹ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `company_id` BIGINT(20) NOT NULL COMMENT '快递公司ID',
    `tracking_no` VARCHAR(50) NOT NULL COMMENT '快递单号',
    `receiver_name` VARCHAR(50) COMMENT '收件人姓名',
    `receiver_phone` VARCHAR(20) COMMENT '收件人电话',
    `receiver_address` VARCHAR(255) COMMENT '收件地址',
    `status` TINYINT(1) DEFAULT 0 COMMENT '包裹状态(0-待收取,1-已收取,2-已签收,3-异常)',
    `remark` VARCHAR(255) COMMENT '备注',
    `estimated_delivery_time` DATETIME COMMENT '预计送达时间',
    `signed_time` DATETIME COMMENT '签收时间',
    `delivery_type` TINYINT(1) DEFAULT 0 COMMENT '取件方式(0-上门取件,1-服务点自取)',
    `station_id` BIGINT(20) COMMENT '服务点ID',
    `courier_id` BIGINT(20) COMMENT '快递员ID',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_tracking` (`tracking_no`, `company_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_company_id` (`company_id`),
    KEY `idx_station_id` (`station_id`),
    KEY `idx_courier_id` (`courier_id`),
    CONSTRAINT `fk_package_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_package_company` FOREIGN KEY (`company_id`) REFERENCES `t_express_company` (`id`),
    CONSTRAINT `fk_package_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_package_courier` FOREIGN KEY (`courier_id`) REFERENCES `t_courier` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='包裹表';

-- 包裹物流记录表
CREATE TABLE IF NOT EXISTS `t_package_tracking` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `package_id` BIGINT(20) NOT NULL COMMENT '包裹ID',
    `tracking_info` VARCHAR(255) NOT NULL COMMENT '物流信息',
    `tracking_time` DATETIME NOT NULL COMMENT '物流时间',
    `location` VARCHAR(255) COMMENT '当前位置',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_package_id` (`package_id`),
    CONSTRAINT `fk_tracking_package` FOREIGN KEY (`package_id`) REFERENCES `t_package` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='包裹物流记录表';

-- 用户实名认证表
CREATE TABLE IF NOT EXISTS `t_user_verification` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '认证ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `id_card` VARCHAR(18) NOT NULL COMMENT '身份证号',
    `front_image` VARCHAR(255) NOT NULL COMMENT '身份证正面照片URL',
    `back_image` VARCHAR(255) NOT NULL COMMENT '身份证反面照片URL',
    `holding_image` VARCHAR(255) NOT NULL COMMENT '手持身份证照片URL',
    `verify_status` TINYINT(1) DEFAULT 0 COMMENT '审核状态(0-待审核,1-已通过,2-未通过)',
    `verify_time` DATETIME COMMENT '审核时间',
    `verify_remark` VARCHAR(255) COMMENT '审核备注',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_verification_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户实名认证表';

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

-- 初始化数据
INSERT IGNORE INTO `t_express_company` (`name`, `code`, `logo`, `phone`, `website`, `status`, `created_at`, `updated_at`)
VALUES 
('顺丰速运', 'SF', '/static/images/sf-logo.png', '95338', 'https://www.sf-express.com', 0, NOW(), NOW()),
('中通快递', 'ZTO', '/static/images/zt-logo.png', '95311', 'https://www.zto.com', 0, NOW(), NOW()),
('圆通速递', 'YTO', '/static/images/yt-logo.png', '95554', 'https://www.yto.net.cn', 0, NOW(), NOW()),
('韵达快递', 'YD', '/static/images/yd-logo.png', '95546', 'https://www.yundaex.com', 0, NOW(), NOW()),
('申通快递', 'STO', '/static/images/st-logo.png', '95543', 'https://www.sto.cn', 0, NOW(), NOW()),
('京东物流', 'JD', '/static/images/jd-logo.png', '950616', 'https://www.jdl.com', 0, NOW(), NOW());