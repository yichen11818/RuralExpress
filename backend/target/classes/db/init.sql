USE rural_express;
-- 创建系统设置表
CREATE TABLE IF NOT EXISTS `t_system_settings` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '设置ID',
    `setting_key` VARCHAR(100) NOT NULL COMMENT '设置键',
    `setting_value` TEXT COMMENT '设置值',
    `description` VARCHAR(255) COMMENT '设置描述',
    `created_at` DATETIME NOT NULL COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 系统设置初始数据
INSERT IGNORE INTO `t_system_settings` (`setting_key`, `setting_value`, `description`, `created_at`, `updated_at`) VALUES
('siteName', '"乡村速递"', '系统名称', NOW(), NOW()),
('siteLogo', '"/static/images/logo.png"', '系统LOGO', NOW(), NOW()),
('servicePhone', '"400-123-4567"', '客服电话', NOW(), NOW()),
('enableRegister', '1', '开放注册', NOW(), NOW()),
('enableCaptcha', '1', '验证码', NOW(), NOW()),
('enableSmsNotify', '1', '短信通知', NOW(), NOW()),
('enableEmailNotify', '0', '邮件通知', NOW(), NOW()),
('enableAppNotify', '1', '应用内通知', NOW(), NOW()),
('maintenanceMode', '0', '系统维护模式', NOW(), NOW()),
('maintenanceMessage', '"系统正在维护中，请稍后再试..."', '维护说明', NOW(), NOW());