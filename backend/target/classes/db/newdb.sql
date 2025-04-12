USE rural_express; 

INSERT IGNORE INTO t_user (phone, password, nickname, avatar, gender, real_name, id_card, verified, user_type, status, created_at, updated_at) VALUES
('13800138001', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'https://cdn.ruralexpress.cn/avatars/user1.jpg', 1, '张三', '加密的身份证号', 1, 0, 0, NOW(), NOW()),
('13800138002', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'https://cdn.ruralexpress.cn/avatars/user2.jpg', 1, '李四', '加密的身份证号', 1, 0, 0, NOW(), NOW()),
('13800138003', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'https://cdn.ruralexpress.cn/avatars/user3.jpg', 1, '王五', '加密的身份证号', 1, 0, 0, NOW(), NOW()),
('13800138004', 'e10adc3949ba59abbe56e057f20f883e', '赵六', 'https://cdn.ruralexpress.cn/avatars/user4.jpg', 1, '赵六', '加密的身份证号', 1, 1, 0, NOW(), NOW()),
('13800138005', 'e10adc3949ba59abbe56e057f20f883e', '钱七', 'https://cdn.ruralexpress.cn/avatars/user5.jpg', 1, '钱七', '加密的身份证号', 1, 1, 0, NOW(), NOW()),
('13800138006', 'e10adc3949ba59abbe56e057f20f883e', '孙八', 'https://cdn.ruralexpress.cn/avatars/user6.jpg', 2, '孙八', '加密的身份证号', 1, 1, 0, NOW(), NOW()),
('13800138007', 'e10adc3949ba59abbe56e057f20f883e', '周九', 'https://cdn.ruralexpress.cn/avatars/user7.jpg', 2, '周九', '加密的身份证号', 1, 0, 0, NOW(), NOW()),
('13800138008', 'e10adc3949ba59abbe56e057f20f883e', '吴十', 'https://cdn.ruralexpress.cn/avatars/user8.jpg', 1, '吴十', '加密的身份证号', 1, 0, 0, NOW(), NOW()),
('13800138009', 'e10adc3949ba59abbe56e057f20f883e', '郑十一', 'https://cdn.ruralexpress.cn/avatars/user9.jpg', 1, '郑十一', '加密的身份证号', 1, 2, 0, NOW(), NOW()),
('13800138010', 'e10adc3949ba59abbe56e057f20f883e', '王十二', 'https://cdn.ruralexpress.cn/avatars/user10.jpg', 2, '王十二', '加密的身份证号', 1, 0, 0, NOW(), NOW()); 

-- 插入地址数据
INSERT INTO t_address (user_id, name, phone, province, city, district, street, detail, is_default, created_at, updated_at) VALUES
(1, '张三', '13800138001', '浙江省', '杭州市', '西湖区', '文教路', '浙江大学紫金港校区', 1, NOW(), NOW()),
(1, '张三父母', '13800138001', '浙江省', '湖州市', '德清县', '乾元镇', '湖州德清县乾元镇农民新村12号', 0, NOW(), NOW()),
(2, '李四', '13800138002', '浙江省', '杭州市', '余杭区', '五常街道', '余杭五常街道塘栖小区8号楼3单元301', 1, NOW(), NOW()),
(3, '王五', '13800138003', '浙江省', '嘉兴市', '桐乡市', '梧桐街道', '嘉兴桐乡市梧桐街道振兴路89号', 1, NOW(), NOW()),
(4, '赵六', '13800138004', '浙江省', '金华市', '婺城区', '城中街道', '金华婺城区城中街道博爱路56号', 1, NOW(), NOW()),
(7, '周九', '13800138007', '浙江省', '衢州市', '柯城区', '新新街道', '衢州柯城区新新街道荷花路23号', 1, NOW(), NOW()),
(8, '吴十', '13800138008', '浙江省', '杭州市', '拱墅区', '湖墅街道', '杭州拱墅区湖墅街道湖墅南路18号', 1, NOW(), NOW()),
(10, '王十二', '13800138010', '浙江省', '温州市', '鹿城区', '五马街道', '温州鹿城区五马街道人民路99号', 1, NOW(), NOW()); 

-- 插入快递员数据
INSERT INTO t_courier (user_id, service_area, service_status, audit_status, rating, balance, completed_orders, id_card_front, id_card_back, work_start_time, work_end_time, vehicle, introduction, response_time, longitude, latitude, created_at, updated_at) VALUES
(4, '浙江省 杭州市 西湖区', 1, 1, 4.8, 2568.50, 126, 'https://cdn.ruralexpress.cn/idcards/front1.jpg', 'https://cdn.ruralexpress.cn/idcards/back1.jpg', '08:00', '18:00', '电动车', '我是一名专业快递员，有3年配送经验，熟悉杭州西湖区域，服务态度好，配送速度快。', 15, 120.136250, 30.286167, NOW(), NOW()),
(5, '浙江省 杭州市 余杭区', 1, 1, 4.9, 3256.75, 186, 'https://cdn.ruralexpress.cn/idcards/front2.jpg', 'https://cdn.ruralexpress.cn/idcards/back2.jpg', '07:30', '19:30', '电动三轮车', '五年快递配送经验，熟悉余杭区各个小区和村庄，专注农村配送服务。', 12, 119.978438, 30.274427, NOW(), NOW()),
(6, '浙江省 嘉兴市 桐乡市', 0, 1, 4.7, 1892.25, 98, 'https://cdn.ruralexpress.cn/idcards/front3.jpg', 'https://cdn.ruralexpress.cn/idcards/back3.jpg', '08:30', '17:30', '摩托车', '熟悉桐乡市区及周边乡镇道路，能提供快速准确的配送服务。', 20, 120.564323, 30.630743, NOW(), NOW()); 

-- 插入服务点数据
INSERT INTO t_station (name, logo, phone, wechat, province, city, district, address, longitude, latitude, business_start_time, business_end_time, business_hours, rating, review_count, status, manager_name, manager_phone, created_at, updated_at) VALUES
('西湖区快递服务点', 'https://cdn.ruralexpress.cn/stations/logo1.jpg', '0571-88123456', 'xihukuaidi888', '浙江省', '杭州市', '西湖区', '文教路123号', 120.133034, 30.283957, '08:00', '20:00', '08:00-20:00', 4.8, 256, 0, '陈经理', '13900138001', NOW(), NOW()),
('余杭区快递驿站', 'https://cdn.ruralexpress.cn/stations/logo2.jpg', '0571-88234567', 'yuhangkuaidi999', '浙江省', '杭州市', '余杭区', '五常街道创新路88号', 119.985243, 30.273412, '07:30', '21:00', '07:30-21:00', 4.9, 328, 0, '李站长', '13900138002', NOW(), NOW()),
('桐乡市快递中心', 'https://cdn.ruralexpress.cn/stations/logo3.jpg', '0573-88345678', 'tongxiangkuaidi777', '浙江省', '嘉兴市', '桐乡市', '梧桐街道人民路56号', 120.565789, 30.629876, '08:00', '19:30', '08:00-19:30', 4.7, 175, 0, '王站长', '13900138003', NOW(), NOW()),
('乡村快递驿站', 'https://cdn.ruralexpress.cn/stations/logo4.jpg', '0572-88456789', 'xiangcunkuaidi666', '浙江省', '湖州市', '德清县', '乾元镇乾元路23号', 119.978654, 30.654321, '08:30', '18:30', '08:30-18:30', 4.6, 98, 0, '张站长', '13900138004', NOW(), NOW()),
('金华市快递服务中心', 'https://cdn.ruralexpress.cn/stations/logo5.jpg', '0579-88567890', 'jinhuakuaidi555', '浙江省', '金华市', '婺城区', '城中街道解放路120号', 119.654321, 29.123456, '08:00', '20:00', '08:00-20:00', 4.8, 210, 0, '赵经理', '13900138005', NOW(), NOW()); 

-- 插入服务点照片数据
INSERT INTO t_station_photo (station_id, photo_url, sort, created_at, updated_at) VALUES
(1, 'https://cdn.ruralexpress.cn/stations/photos/s1p1.jpg', 1, NOW(), NOW()),
(1, 'https://cdn.ruralexpress.cn/stations/photos/s1p2.jpg', 2, NOW(), NOW()),
(1, 'https://cdn.ruralexpress.cn/stations/photos/s1p3.jpg', 3, NOW(), NOW()),
(2, 'https://cdn.ruralexpress.cn/stations/photos/s2p1.jpg', 1, NOW(), NOW()),
(2, 'https://cdn.ruralexpress.cn/stations/photos/s2p2.jpg', 2, NOW(), NOW()),
(3, 'https://cdn.ruralexpress.cn/stations/photos/s3p1.jpg', 1, NOW(), NOW()),
(3, 'https://cdn.ruralexpress.cn/stations/photos/s3p2.jpg', 2, NOW(), NOW()),
(4, 'https://cdn.ruralexpress.cn/stations/photos/s4p1.jpg', 1, NOW(), NOW()),
(5, 'https://cdn.ruralexpress.cn/stations/photos/s5p1.jpg', 1, NOW(), NOW()),
(5, 'https://cdn.ruralexpress.cn/stations/photos/s5p2.jpg', 2, NOW(), NOW()); 

-- 插入快递公司数据
INSERT INTO t_express_company (name, logo, code, status, created_at, updated_at) VALUES
('中通快递', 'https://cdn.ruralexpress.cn/companies/zto.png', 'ZTO', 1, NOW(), NOW()),
('圆通速递', 'https://cdn.ruralexpress.cn/companies/yto.png', 'YTO', 1, NOW(), NOW()),
('申通快递', 'https://cdn.ruralexpress.cn/companies/sto.png', 'STO', 1, NOW(), NOW()),
('韵达快递', 'https://cdn.ruralexpress.cn/companies/yunda.png', 'YUNDA', 1, NOW(), NOW()),
('顺丰速运', 'https://cdn.ruralexpress.cn/companies/sf.png', 'SF', 1, NOW(), NOW()),
('邮政EMS', 'https://cdn.ruralexpress.cn/companies/ems.png', 'EMS', 1, NOW(), NOW()),
('京东物流', 'https://cdn.ruralexpress.cn/companies/jd.png', 'JD', 1, NOW(), NOW()),
('百世快递', 'https://cdn.ruralexpress.cn/companies/best.png', 'BEST', 1, NOW(), NOW()); 

-- 插入服务点支持的快递公司数据
INSERT INTO t_station_company (station_id, company_id, created_at, updated_at) VALUES
(1, 1, NOW(), NOW()),
(1, 2, NOW(), NOW()),
(1, 3, NOW(), NOW()),
(1, 4, NOW(), NOW()),
(1, 5, NOW(), NOW()),
(2, 1, NOW(), NOW()),
(2, 2, NOW(), NOW()),
(2, 3, NOW(), NOW()),
(2, 7, NOW(), NOW()),
(3, 1, NOW(), NOW()),
(3, 2, NOW(), NOW()),
(3, 6, NOW(), NOW()),
(4, 1, NOW(), NOW()),
(4, 2, NOW(), NOW()),
(4, 4, NOW(), NOW()),
(5, 1, NOW(), NOW()),
(5, 2, NOW(), NOW()),
(5, 3, NOW(), NOW()),
(5, 4, NOW(), NOW()),
(5, 5, NOW(), NOW()),
(5, 8, NOW(), NOW()); 

-- 插入订单数据
INSERT INTO t_order (order_no, user_id, courier_id, sender_name, sender_phone, sender_address, sender_longitude, sender_latitude, receiver_name, receiver_phone, receiver_address, receiver_longitude, receiver_latitude, package_type, weight, price, status, expected_pickup_time, actual_pickup_time, expected_delivery_time, actual_delivery_time, remark, created_at, updated_at) VALUES
('202307010001123456', 1, 1, '张三', '13800138001', '浙江省杭州市西湖区文教路123号', 120.136250, 30.286167, '李四', '13800138002', '浙江省杭州市余杭区五常街道塘栖小区8号楼3单元301', 119.978438, 30.274427, 1, 2.5, 15.00, 6, '2023-07-01 10:00:00', '2023-07-01 09:50:00', '2023-07-01 15:00:00', '2023-07-01 14:30:00', '轻拿轻放，谢谢', '2023-07-01 08:30:00', '2023-07-01 14:35:00'),
('202307020002123456', 2, 1, '李四', '13800138002', '浙江省杭州市余杭区五常街道塘栖小区8号楼3单元301', 119.978438, 30.274427, '王五', '13800138003', '浙江省嘉兴市桐乡市梧桐街道振兴路89号', 120.564323, 30.630743, 0, 1.2, 12.00, 6, '2023-07-02 11:00:00', '2023-07-02 10:45:00', '2023-07-02 16:00:00', '2023-07-02 15:40:00', '包装好一点', '2023-07-02 09:20:00', '2023-07-02 15:45:00'),
('202307030003123456', 3, 2, '王五', '13800138003', '浙江省嘉兴市桐乡市梧桐街道振兴路89号', 120.564323, 30.630743, '赵六', '13800138004', '浙江省金华市婺城区城中街道博爱路56号', 119.654321, 29.123456, 2, 5.8, 25.00, 5, '2023-07-03 09:30:00', '2023-07-03 09:20:00', '2023-07-03 17:00:00', '2023-07-03 16:50:00', '易碎物品，小心搬运', '2023-07-03 08:00:00', '2023-07-03 16:55:00'),
('202307040004123456', 7, 2, '周九', '13800138007', '浙江省衢州市柯城区新新街道荷花路23号', 118.876543, 28.987654, '吴十', '13800138008', '浙江省杭州市拱墅区湖墅街道湖墅南路18号', 120.152469, 30.308176, 1, 3.2, 18.00, 4, '2023-07-04 10:00:00', '2023-07-04 10:10:00', '2023-07-04 16:00:00', NULL, '不要放门口，电话联系', '2023-07-04 08:45:00', '2023-07-04 13:20:00'),
('202307050005123456', 8, 3, '吴十', '13800138008', '浙江省杭州市拱墅区湖墅街道湖墅南路18号', 120.152469, 30.308176, '王十二', '13800138010', '浙江省温州市鹿城区五马街道人民路99号', 120.654987, 28.018765, 0, 0.8, 10.00, 3, '2023-07-05 11:30:00', '2023-07-05 11:35:00', '2023-07-05 17:30:00', NULL, '书籍，不要弄湿', '2023-07-05 09:30:00', '2023-07-05 12:00:00'),
('202307060006123456', 10, NULL, '王十二', '13800138010', '浙江省温州市鹿城区五马街道人民路99号', 120.654987, 28.018765, '张三', '13800138001', '浙江省杭州市西湖区文教路浙江大学紫金港校区', 120.133034, 30.283957, 1, 2.0, 15.00, 0, '2023-07-06 14:00:00', NULL, '2023-07-06 20:00:00', NULL, '生日礼物，勿拆', '2023-07-06 10:20:00', '2023-07-06 10:20:00'); 

-- 插入订单评价数据
INSERT INTO t_evaluation (order_id, user_id, courier_id, score, content, user_to_courier, created_at, updated_at) VALUES
(1, 1, 1, 5, '送货速度快，服务态度好，包裹完好无损，非常满意！', 0, '2023-07-01 15:00:00', '2023-07-01 15:00:00'),
(1, 1, 1, 5, '客户很有礼貌，地址清晰，接收快速', 1, '2023-07-01 15:10:00', '2023-07-01 15:10:00'),
(2, 2, 1, 4, '配送及时，但包装稍微有点压痕，总体还不错', 0, '2023-07-02 16:20:00', '2023-07-02 16:20:00'),
(2, 2, 1, 5, '顾客很好，接收迅速', 1, '2023-07-02 16:25:00', '2023-07-02 16:25:00'),
(3, 3, 2, 5, '非常专业的快递员，物品很重但他搬运得很小心', 0, '2023-07-03 17:30:00', '2023-07-03 17:30:00'); 

-- 插入支付记录数据
INSERT INTO t_payment (user_id, order_id, payment_no, channel, amount, status, payment_time, created_at, updated_at) VALUES
(1, 1, 'PAY202307010001', '微信支付', 15.00, 1, '2023-07-01 08:35:00', '2023-07-01 08:30:00', '2023-07-01 08:35:00'),
(2, 2, 'PAY202307020002', '支付宝', 12.00, 1, '2023-07-02 09:25:00', '2023-07-02 09:20:00', '2023-07-02 09:25:00'),
(3, 3, 'PAY202307030003', '微信支付', 25.00, 1, '2023-07-03 08:05:00', '2023-07-03 08:00:00', '2023-07-03 08:05:00'),
(7, 4, 'PAY202307040004', '银行卡', 18.00, 1, '2023-07-04 08:50:00', '2023-07-04 08:45:00', '2023-07-04 08:50:00'),
(8, 5, 'PAY202307050005', '支付宝', 10.00, 1, '2023-07-05 09:35:00', '2023-07-05 09:30:00', '2023-07-05 09:35:00'),
(10, 6, 'PAY202307060006', '微信支付', 15.00, 1, '2023-07-06 10:25:00', '2023-07-06 10:20:00', '2023-07-06 10:25:00'); 

-- 插入系统配置数据
INSERT INTO t_config (config_key, config_value, description, created_at, updated_at) VALUES
('site_name', '乡递通', '网站名称', NOW(), NOW()),
('site_logo', 'https://cdn.ruralexpress.cn/logo.png', '网站Logo', NOW(), NOW()),
('service_phone', '400-123-4567', '客服电话', NOW(), NOW()),
('service_email', 'service@ruralexpress.cn', '客服邮箱', NOW(), NOW()),
('small_package_price', '10', '小件配送基础价格', NOW(), NOW()),
('medium_package_price', '15', '中件配送基础价格', NOW(), NOW()),
('large_package_price', '25', '大件配送基础价格', NOW(), NOW()),
('distance_price_rate', '0.5', '距离加价比例（元/公里）', NOW(), NOW()),
('courier_commission_rate', '0.8', '快递员佣金比例', NOW(), NOW()),
('platform_commission_rate', '0.2', '平台佣金比例', NOW(), NOW()),
('sms_sign', '乡递通', '短信签名', NOW(), NOW()),
('sms_template_verification', 'SMS_123456789', '验证码短信模板ID', NOW(), NOW()),
('sms_template_order_create', 'SMS_987654321', '订单创建通知短信模板ID', NOW(), NOW()),
('sms_template_order_assign', 'SMS_876543219', '订单指派通知短信模板ID', NOW(), NOW()),
('sms_template_order_complete', 'SMS_765432198', '订单完成通知短信模板ID', NOW(), NOW()); 
