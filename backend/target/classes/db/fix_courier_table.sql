-- 选择数据库
USE rural_express;

-- 首先确保表存在
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
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递员表';

-- 修复 t_courier 表结构，添加缺失的字段
-- 注意：使用PROCEDURE避免列已存在时出错
DROP PROCEDURE IF EXISTS add_courier_columns;

DELIMITER //
CREATE PROCEDURE add_courier_columns()
BEGIN
    -- 检查并添加 work_start_time 列
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_name = 't_courier' 
        AND column_name = 'work_start_time'
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE t_courier 
        ADD COLUMN work_start_time VARCHAR(5) DEFAULT '08:00' COMMENT '工作开始时间';
    END IF;
    
    -- 检查并添加 work_end_time 列
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_name = 't_courier' 
        AND column_name = 'work_end_time'
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE t_courier 
        ADD COLUMN work_end_time VARCHAR(5) DEFAULT '20:00' COMMENT '工作结束时间';
    END IF;
    
    -- 检查并添加 vehicle 列
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_name = 't_courier' 
        AND column_name = 'vehicle'
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE t_courier 
        ADD COLUMN vehicle VARCHAR(50) COMMENT '交通工具';
    END IF;
    
    -- 检查并添加 introduction 列
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_name = 't_courier' 
        AND column_name = 'introduction'
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE t_courier 
        ADD COLUMN introduction VARCHAR(500) COMMENT '简介';
    END IF;
    
    -- 检查并添加 response_time 列
    IF NOT EXISTS (
        SELECT * FROM information_schema.columns 
        WHERE table_name = 't_courier' 
        AND column_name = 'response_time'
        AND table_schema = DATABASE()
    ) THEN
        ALTER TABLE t_courier 
        ADD COLUMN response_time INT DEFAULT 15 COMMENT '平均响应时间(分钟)';
    END IF;
    
END //
DELIMITER ;

-- 执行存储过程
CALL add_courier_columns();

-- 删除存储过程
DROP PROCEDURE IF EXISTS add_courier_columns;

-- 如果已经存在其他名称的字段，可以使用下面的语句重命名字段
-- 取消下面的注释并替换为您实际的字段名
-- ALTER TABLE t_courier CHANGE COLUMN workStartTime work_start_time VARCHAR(5);
-- ALTER TABLE t_courier CHANGE COLUMN workEndTime work_end_time VARCHAR(5);
-- ALTER TABLE t_courier CHANGE COLUMN responseTime response_time INT; 