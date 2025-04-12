-- 首先选择数据库
USE rural_express;

-- 直接添加经纬度字段（如果已存在会报错，可以忽略）
ALTER TABLE t_order 
ADD COLUMN sender_longitude DOUBLE NULL COMMENT '寄件地址经度',
ADD COLUMN sender_latitude DOUBLE NULL COMMENT '寄件地址纬度',
ADD COLUMN receiver_longitude DOUBLE NULL COMMENT '收件地址经度',
ADD COLUMN receiver_latitude DOUBLE NULL COMMENT '收件地址纬度'; 