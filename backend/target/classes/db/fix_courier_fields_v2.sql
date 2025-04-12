-- 选择数据库
USE rural_express;

-- 检查并添加字段（使用存储过程）
DELIMITER //
CREATE PROCEDURE AddColumnIfNotExists()
BEGIN
    -- 检查 work_start_time 字段是否存在
    IF NOT EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_NAME = 't_courier' 
        AND COLUMN_NAME = 'work_start_time'
        AND TABLE_SCHEMA = 'rural_express'
    ) THEN
        ALTER TABLE t_courier ADD COLUMN work_start_time VARCHAR(5) DEFAULT '08:00' COMMENT '工作时间起始';
    END IF;
    
    -- 检查 work_end_time 字段是否存在
    IF NOT EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_NAME = 't_courier' 
        AND COLUMN_NAME = 'work_end_time'
        AND TABLE_SCHEMA = 'rural_express'
    ) THEN
        ALTER TABLE t_courier ADD COLUMN work_end_time VARCHAR(5) DEFAULT '20:00' COMMENT '工作时间结束';
    END IF;
    
    -- 检查 vehicle 字段是否存在
    IF NOT EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_NAME = 't_courier' 
        AND COLUMN_NAME = 'vehicle'
        AND TABLE_SCHEMA = 'rural_express'
    ) THEN
        ALTER TABLE t_courier ADD COLUMN vehicle VARCHAR(50) COMMENT '交通工具';
    END IF;
    
    -- 检查 introduction 字段是否存在
    IF NOT EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_NAME = 't_courier' 
        AND COLUMN_NAME = 'introduction'
        AND TABLE_SCHEMA = 'rural_express'
    ) THEN
        ALTER TABLE t_courier ADD COLUMN introduction VARCHAR(500) COMMENT '个人简介';
    END IF;
    
    -- 检查 response_time 字段是否存在
    IF NOT EXISTS (
        SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
        WHERE TABLE_NAME = 't_courier' 
        AND COLUMN_NAME = 'response_time'
        AND TABLE_SCHEMA = 'rural_express'
    ) THEN
        ALTER TABLE t_courier ADD COLUMN response_time INT DEFAULT 15 COMMENT '响应时间';
    END IF;
END //
DELIMITER ;

-- 执行存储过程
CALL AddColumnIfNotExists();

-- 删除存储过程
DROP PROCEDURE IF EXISTS AddColumnIfNotExists;

-- 修改 created_at 和 updated_at 字段的默认值（如果需要）
ALTER TABLE t_courier 
MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 确保删除任何重复的数据
DELETE FROM banner WHERE id > 3;
DELETE FROM notice WHERE id > 3; 