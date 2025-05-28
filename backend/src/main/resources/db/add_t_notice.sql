USE rural_express;
CREATE TABLE `t_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',
  `type` tinyint(1) NOT NULL COMMENT '消息类型(0-系统消息,1-快递员申请,2-订单通知)',
  `status` tinyint(1) DEFAULT 0 COMMENT '状态(0-未读,1-已读)',
  `target_id` bigint DEFAULT NULL COMMENT '关联ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者用户ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送者用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_receiver_status` (`receiver_id`, `status`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统消息表';
