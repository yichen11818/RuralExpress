-- 首先选择数据库
USE rural_express;

-- 为t_order表添加经纬度字段
-- 使用IF NOT EXISTS检查避免重复添加

-- 检查sender_longitude字段是否存在
SELECT COUNT(*) INTO @sender_longitude_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'rural_express' 
AND TABLE_NAME = 't_order' 
AND COLUMN_NAME = 'sender_longitude';

-- 如果不存在，则添加sender_longitude字段
SET @add_sender_longitude = 'ALTER TABLE t_order ADD COLUMN sender_longitude DOUBLE NULL COMMENT "寄件地址经度"';
SET @sql_sender_longitude = IF(@sender_longitude_exists = 0, @add_sender_longitude, 'SELECT "sender_longitude已存在，无需添加"');
PREPARE stmt FROM @sql_sender_longitude;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查sender_latitude字段是否存在
SELECT COUNT(*) INTO @sender_latitude_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'rural_express' 
AND TABLE_NAME = 't_order' 
AND COLUMN_NAME = 'sender_latitude';

-- 如果不存在，则添加sender_latitude字段
SET @add_sender_latitude = 'ALTER TABLE t_order ADD COLUMN sender_latitude DOUBLE NULL COMMENT "寄件地址纬度"';
SET @sql_sender_latitude = IF(@sender_latitude_exists = 0, @add_sender_latitude, 'SELECT "sender_latitude已存在，无需添加"');
PREPARE stmt FROM @sql_sender_latitude;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查receiver_longitude字段是否存在
SELECT COUNT(*) INTO @receiver_longitude_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'rural_express' 
AND TABLE_NAME = 't_order' 
AND COLUMN_NAME = 'receiver_longitude';

-- 如果不存在，则添加receiver_longitude字段
SET @add_receiver_longitude = 'ALTER TABLE t_order ADD COLUMN receiver_longitude DOUBLE NULL COMMENT "收件地址经度"';
SET @sql_receiver_longitude = IF(@receiver_longitude_exists = 0, @add_receiver_longitude, 'SELECT "receiver_longitude已存在，无需添加"');
PREPARE stmt FROM @sql_receiver_longitude;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查receiver_latitude字段是否存在
SELECT COUNT(*) INTO @receiver_latitude_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'rural_express' 
AND TABLE_NAME = 't_order' 
AND COLUMN_NAME = 'receiver_latitude';

-- 如果不存在，则添加receiver_latitude字段
SET @add_receiver_latitude = 'ALTER TABLE t_order ADD COLUMN receiver_latitude DOUBLE NULL COMMENT "收件地址纬度"';
SET @sql_receiver_latitude = IF(@receiver_latitude_exists = 0, @add_receiver_latitude, 'SELECT "receiver_latitude已存在，无需添加"');
PREPARE stmt FROM @sql_receiver_latitude;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 