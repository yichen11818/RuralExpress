-- 使用数据库
USE rural_express;

-- ================ 为ID=9的快递员（夏）添加评价 ================
INSERT INTO `t_review` (
    `user_id`, `courier_id`, `order_id`, `rating`, `content`, 
    `anonymous`, `created_at`, `updated_at`
) VALUES 
(3, 9, 20, 5, '夏师傅送货非常准时，服务态度极好', 0, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
(4, 9, 21, 5, '快递送达很快，而且包装完好无损', 0, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(5, 9, 22, 4, '服务很规范，各方面都很满意', 0, DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(6, 9, 23, 5, '非常有礼貌的快递员，下雨天还很准时送达', 1, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(7, 9, 24, 5, '态度特别好，遇到问题能很好地解决', 0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ================ 添加快递员回复 ================
-- 为快递员ID=9的评价添加回复
UPDATE `t_review` SET `reply` = '感谢您的好评，能够为您提供好的服务是我的荣幸' WHERE `courier_id` = 9 AND `order_id` = 20;
UPDATE `t_review` SET `reply` = '谢谢您的认可，我会继续保持良好的服务水平' WHERE `courier_id` = 9 AND `order_id` = 21;
UPDATE `t_review` SET `reply` = '感谢您的评价，我们一直在努力提高服务质量' WHERE `courier_id` = 9 AND `order_id` = 22;
UPDATE `t_review` SET `reply` = '非常感谢您的好评，无论天气如何，准时送达是我们的责任' WHERE `courier_id` = 9 AND `order_id` = 23;
UPDATE `t_review` SET `reply` = '谢谢您的支持，我会一如既往地做好服务工作' WHERE `courier_id` = 9 AND `order_id` = 24;

-- 更新快递员评分计算
-- 此处假设评分为所有评价的平均值
UPDATE `t_courier` SET 
    `rating` = (SELECT AVG(`rating`) FROM `t_review` WHERE `courier_id` = 9),
    `updated_at` = NOW()
WHERE `id` = 9;

-- 显示新增的评价记录
SELECT * FROM `t_review` WHERE `courier_id` = 9 ORDER BY `created_at` DESC;

-- 显示快递员ID=9的信息（包括更新后的评分）
SELECT * FROM `t_courier` WHERE `id` = 9; 