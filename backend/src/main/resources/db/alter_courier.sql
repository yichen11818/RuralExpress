USE rural_express;

-- 给t_courier表添加rating_count字段
ALTER TABLE `t_courier` 
ADD COLUMN `rating_count` int(11) DEFAULT 0 COMMENT '评价数量' AFTER `rating`; 