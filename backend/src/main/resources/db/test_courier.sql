-- 检查列是否已存在，避免重复添加导致错误
-- business_hours字段已存在，无需再添加

-- 添加manager_name字段（如果不存在）
SELECT COUNT(*) INTO @manager_name_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_station' 
AND COLUMN_NAME = 'manager_name';

SET @add_manager_name = CONCAT('ALTER TABLE t_station ADD COLUMN manager_name VARCHAR(50) COMMENT "服务点负责人姓名"');

SET @sql_manager_name = IF(@manager_name_exists = 0, @add_manager_name, 'SELECT "manager_name已存在，无需添加"');
PREPARE stmt FROM @sql_manager_name;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加manager_phone字段（如果不存在）
SELECT COUNT(*) INTO @manager_phone_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_station' 
AND COLUMN_NAME = 'manager_phone';

SET @add_manager_phone = CONCAT('ALTER TABLE t_station ADD COLUMN manager_phone VARCHAR(20) COMMENT "服务点负责人电话"');

SET @sql_manager_phone = IF(@manager_phone_exists = 0, @add_manager_phone, 'SELECT "manager_phone已存在，无需添加"');
PREPARE stmt FROM @sql_manager_phone;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为快递员表添加新字段（如果不存在）
-- 添加实时位置信息
SELECT COUNT(*) INTO @current_lat_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'current_latitude';

SET @add_current_lat = CONCAT('ALTER TABLE t_courier ADD COLUMN current_latitude DECIMAL(10,6) COMMENT "当前纬度"');

SET @sql_current_lat = IF(@current_lat_exists = 0, @add_current_lat, 'SELECT "current_latitude已存在，无需添加"');
PREPARE stmt FROM @sql_current_lat;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @current_lng_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'current_longitude';

SET @add_current_lng = CONCAT('ALTER TABLE t_courier ADD COLUMN current_longitude DECIMAL(10,6) COMMENT "当前经度"');

SET @sql_current_lng = IF(@current_lng_exists = 0, @add_current_lng, 'SELECT "current_longitude已存在，无需添加"');
PREPARE stmt FROM @sql_current_lng;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @location_updated_at_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'location_updated_at';

SET @add_location_updated_at = CONCAT('ALTER TABLE t_courier ADD COLUMN location_updated_at DATETIME COMMENT "位置更新时间"');

SET @sql_location_updated_at = IF(@location_updated_at_exists = 0, @add_location_updated_at, 'SELECT "location_updated_at已存在，无需添加"');
PREPARE stmt FROM @sql_location_updated_at;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加接单统计信息
SELECT COUNT(*) INTO @canceled_orders_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'canceled_orders';

SET @add_canceled_orders = CONCAT('ALTER TABLE t_courier ADD COLUMN canceled_orders INT DEFAULT 0 COMMENT "取消订单数"');

SET @sql_canceled_orders = IF(@canceled_orders_exists = 0, @add_canceled_orders, 'SELECT "canceled_orders已存在，无需添加"');
PREPARE stmt FROM @sql_canceled_orders;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @monthly_orders_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'monthly_orders';

SET @add_monthly_orders = CONCAT('ALTER TABLE t_courier ADD COLUMN monthly_orders INT DEFAULT 0 COMMENT "月订单数"');

SET @sql_monthly_orders = IF(@monthly_orders_exists = 0, @add_monthly_orders, 'SELECT "monthly_orders已存在，无需添加"');
PREPARE stmt FROM @sql_monthly_orders;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @monthly_income_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'monthly_income';

SET @add_monthly_income = CONCAT('ALTER TABLE t_courier ADD COLUMN monthly_income DECIMAL(10,2) DEFAULT 0 COMMENT "月收入"');

SET @sql_monthly_income = IF(@monthly_income_exists = 0, @add_monthly_income, 'SELECT "monthly_income已存在，无需添加"');
PREPARE stmt FROM @sql_monthly_income;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加账户信息
SELECT COUNT(*) INTO @bank_account_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'bank_account';

SET @add_bank_account = CONCAT('ALTER TABLE t_courier ADD COLUMN bank_account VARCHAR(50) COMMENT "银行账号"');

SET @sql_bank_account = IF(@bank_account_exists = 0, @add_bank_account, 'SELECT "bank_account已存在，无需添加"');
PREPARE stmt FROM @sql_bank_account;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @bank_name_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'bank_name';

SET @add_bank_name = CONCAT('ALTER TABLE t_courier ADD COLUMN bank_name VARCHAR(50) COMMENT "开户银行"');

SET @sql_bank_name = IF(@bank_name_exists = 0, @add_bank_name, 'SELECT "bank_name已存在，无需添加"');
PREPARE stmt FROM @sql_bank_name;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加服务站点关联
SELECT COUNT(*) INTO @station_id_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'station_id';

SET @add_station_id = CONCAT('ALTER TABLE t_courier ADD COLUMN station_id BIGINT(20) COMMENT "关联服务站点ID"');

SET @sql_station_id = IF(@station_id_exists = 0, @add_station_id, 'SELECT "station_id已存在，无需添加"');
PREPARE stmt FROM @sql_station_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加其他有用信息
SELECT COUNT(*) INTO @availability_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'availability';

SET @add_availability = CONCAT('ALTER TABLE t_courier ADD COLUMN availability VARCHAR(100) DEFAULT "1,2,3,4,5,6,7" COMMENT "可用工作日(1-7代表周一至周日，逗号分隔)"');

SET @sql_availability = IF(@availability_exists = 0, @add_availability, 'SELECT "availability已存在，无需添加"');
PREPARE stmt FROM @sql_availability;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT COUNT(*) INTO @max_orders_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_courier' 
AND COLUMN_NAME = 'max_orders';

SET @add_max_orders = CONCAT('ALTER TABLE t_courier ADD COLUMN max_orders INT DEFAULT 20 COMMENT "每日最大接单数"');

SET @sql_max_orders = IF(@max_orders_exists = 0, @add_max_orders, 'SELECT "max_orders已存在，无需添加"');
PREPARE stmt FROM @sql_max_orders;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 保留原来的地址表创建语句
CREATE TABLE IF NOT EXISTS `address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收件人姓名',
  `phone` varchar(15) NOT NULL COMMENT '联系电话',
  `province` varchar(20) NOT NULL COMMENT '省份',
  `city` varchar(20) NOT NULL COMMENT '城市',
  `district` varchar(20) NOT NULL COMMENT '区县',
  `detail_address` varchar(200) NOT NULL COMMENT '详细地址',
  `address_type` tinyint DEFAULT '0' COMMENT '地址类型：0-收货地址，1-发货地址',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认地址：0-否，1-是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 创建快递公司表（如果不存在）
CREATE TABLE IF NOT EXISTS `t_company` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) DEFAULT NULL COMMENT '公司编码',
  `name` varchar(100) NOT NULL COMMENT '公司名称',
  `short_name` varchar(50) DEFAULT NULL COMMENT '公司简称',
  `logo` varchar(255) DEFAULT NULL COMMENT '公司logo',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '公司地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `sort` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递公司表';

-- 插入一些测试数据
INSERT INTO `t_company` (`code`, `name`, `short_name`, `logo`, `contact_person`, `contact_phone`, `address`, `remark`, `status`, `sort`)
VALUES
('SF', '顺丰速运', '顺丰', 'https://www.sf-express.com/logo.png', '张经理', '400-111-1111', '深圳市南山区科技园', '国内领先的快递公司', 1, 1),
('YTO', '圆通速递', '圆通', 'https://www.yto.net.cn/logo.png', '李经理', '400-222-2222', '上海市青浦区华新镇', '全国性快递物流服务商', 1, 2),
('ZTO', '中通快递', '中通', 'https://www.zto.com/logo.png', '王经理', '400-333-3333', '上海市青浦区华徐公路', '国内大型综合物流服务商', 1, 3),
('STO', '申通快递', '申通', 'https://www.sto.cn/logo.png', '赵经理', '400-444-4444', '上海市青浦区华徐公路', '全国性连锁快递企业', 1, 4),
('YUNDA', '韵达速递', '韵达', 'https://www.yundaex.com/logo.png', '钱经理', '400-555-5555', '上海市青浦区盈港东路', '全国性快递物流企业', 1, 5);