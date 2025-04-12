USE rural_express;

-- 首先查看当前已有的用户数据，看看哪些ID和手机号已被使用
SELECT id, phone FROM t_user ORDER BY id DESC LIMIT 10;

-- 然后使用新的、未被使用的ID和手机号插入数据
INSERT INTO t_user (phone, password, nickname, avatar, gender, real_name, id_card, verified, user_type, status, created_at, updated_at) VALUES 
('13811138001', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '李大壮', 'https://example.com/avatar1.jpg', 1, '李大壮', '360300199001010001', 1, 0, 0, NOW(), NOW()),
('13811138002', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '张小花', 'https://example.com/avatar2.jpg', 2, '张小花', '360300199001010002', 1, 0, 0, NOW(), NOW()),
('13811138003', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '王快递', 'https://example.com/avatar3.jpg', 1, '王快递', '360300199001010003', 1, 1, 0, NOW(), NOW()),
('13811138004', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '赵配送', 'https://example.com/avatar4.jpg', 1, '赵配送', '360300199001010004', 1, 1, 0, NOW(), NOW()),
('13811138005', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '管理员', 'https://example.com/avatar5.jpg', 1, '郑管理', '360300199001010005', 1, 2, 0, NOW(), NOW());

-- 尝试使用不同的跟踪号，不指定courier_id
INSERT INTO t_package (user_id, company_id, tracking_no, receiver_name, receiver_phone, receiver_address, sender_name, sender_phone, sender_address, weight, status, remark, estimated_delivery_time, signed_time, delivery_type, station_id, created_at, updated_at) VALUES 
(3, 1, 'SF3234567890', '李大壮', '13800138001', '山东省青岛市即墨区龙泉街道社区村66号', '顺丰快递', '95338', '广东省深圳市南山区科技园', 2.5, 1, '轻拿轻放，易碎物品', '2023-03-28 14:00:00', NULL, 1, 4, '2023-03-25 10:22:33', '2023-03-25 15:35:46'),
(4, 2, 'ZTO3876543210', '张小花', '13800138002', '山东省青岛市即墨区通济街道幸福村88号', '中通快递', '95311', '上海市青浦区华新镇', 1.8, 0, '生鲜水果，请及时签收', '2023-03-27 16:30:00', NULL, 0, NULL, '2023-03-26 09:15:27', '2023-03-26 09:15:27');