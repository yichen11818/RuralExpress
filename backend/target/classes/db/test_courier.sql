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