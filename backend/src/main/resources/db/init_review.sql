USE rural_express;
-- 评价表
CREATE TABLE IF NOT EXISTS `t_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `courier_id` bigint(20) NOT NULL COMMENT '快递员ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `rating` int(11) NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `tags` varchar(255) DEFAULT NULL COMMENT '评价标签(JSON数组)',
  `reply` varchar(500) DEFAULT NULL COMMENT '快递员回复',
  `anonymous` tinyint(4) DEFAULT '0' COMMENT '是否匿名(0-否,1-是)',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_courier_id` (`courier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 评价图片表
CREATE TABLE IF NOT EXISTS `t_review_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `review_id` bigint(20) NOT NULL COMMENT '评价ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `sort` int(11) DEFAULT '0' COMMENT '排序号',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_review_id` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价图片表';

ALTER TABLE `t_review` 
ADD COLUMN `tags` varchar(255) DEFAULT NULL COMMENT '评价标签(JSON数组)' AFTER `content`; 