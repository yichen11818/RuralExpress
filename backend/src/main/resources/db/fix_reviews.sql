-- 使用数据库
USE rural_express;

-- ================ 新增快递员评价 ================
-- 清空已有评价数据（避免重复）
DELETE FROM `t_review`;

-- 为ID=2的快递员(福田快递员)添加评价
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(1, 2, 1, 5, '快递员服务很好，送货速度快', 0, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, 2, 2, 4, '服务态度不错，但送货稍有延迟', 0, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(11, 2, 16, 5, '快递员非常专业，物品完好送达', 0, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(12, 2, 17, 4, '服务态度很好，值得表扬', 0, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 为ID=5的快递员添加评价
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(3, 5, 3, 5, '快递员非常热情，服务很周到', 1, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 5, 4, 3, '送货速度可以再快一些', 0, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));

-- 为ID=7的快递员(青岛快递员1)添加评价
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(5, 7, 10, 5, '送货速度很快，态度也很好', 0, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(6, 7, 11, 4, '快递完好送达，配送员很守时', 0, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 7, 12, 5, '服务非常专业，下雨天还准时送达', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 为ID=8的快递员(青岛快递员2)添加评价
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(8, 8, 13, 4, '快递员很有礼貌，包装完好', 0, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(9, 8, 14, 5, '服务很周到，还主动帮忙搬上楼', 0, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 8, 15, 3, '送货时间有点延迟，但态度还不错', 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ================ 添加快递员回复 ================
-- 为快递员ID=2的评价添加回复
UPDATE `t_review` SET `reply` = '感谢您的好评，我会继续努力' WHERE `courier_id` = 2 AND `id` = 1;
UPDATE `t_review` SET `reply` = '非常抱歉送货有延迟，下次会更加注意时间' WHERE `courier_id` = 2 AND `id` = 2;
UPDATE `t_review` SET `reply` = '感谢您的认可，我们会保持专业服务水准' WHERE `courier_id` = 2 AND `id` = 3;
UPDATE `t_review` SET `reply` = '谢谢您的好评，欢迎再次选择我们的服务' WHERE `courier_id` = 2 AND `id` = 4;

-- 为快递员ID=5的评价添加回复
UPDATE `t_review` SET `reply` = '感谢您的好评，我们一直致力于提供优质服务' WHERE `courier_id` = 5 AND `id` = 5;
UPDATE `t_review` SET `reply` = '感谢您的反馈，我们会努力提高配送速度' WHERE `courier_id` = 5 AND `id` = 6;

-- 为快递员ID=7的评价添加回复
UPDATE `t_review` SET `reply` = '感谢您的好评，我们会继续提供优质服务' WHERE `courier_id` = 7 AND `id` = 7;
UPDATE `t_review` SET `reply` = '谢谢您的肯定，我们一直以准时为服务准则' WHERE `courier_id` = 7 AND `id` = 8;
UPDATE `t_review` SET `reply` = '非常感谢您的认可，无论天气如何我们都会尽力提供最好的服务' WHERE `courier_id` = 7 AND `id` = 9;

-- 为快递员ID=8的评价添加回复
UPDATE `t_review` SET `reply` = '谢谢您的评价，我们将继续保持良好的服务态度' WHERE `courier_id` = 8 AND `id` = 10;
UPDATE `t_review` SET `reply` = '感谢您的好评，为客户提供便利是我们的责任' WHERE `courier_id` = 8 AND `id` = 11;
UPDATE `t_review` SET `reply` = '非常抱歉造成延误，我们会改进配送时间管理，谢谢您的理解' WHERE `courier_id` = 8 AND `id` = 12;

-- 显示新增的评价记录
SELECT * FROM `t_review` ORDER BY courier_id, id; 