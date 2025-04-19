/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 90001 (9.0.1)
 Source Host           : localhost:3306
 Source Schema         : rural_express

 Target Server Type    : MySQL
 Target Server Version : 90001 (9.0.1)
 File Encoding         : 65001

 Date: 15/04/2025 21:10:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人姓名',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `address_type` tinyint NULL DEFAULT 0 COMMENT '地址类型：0-收货地址，1-发货地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 1, '1', '15579961188', '北京市', '北京市', '东城区', '1', 0, 1, '2025-03-30 01:03:03', '2025-03-30 01:03:08', 0);

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '点击链接URL',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否激活',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (49, '/static/images/banner1.jpg', '/pages/notice/detail?id=31', '乡递通平台上线啦', 1, 1, '2025-03-26 19:51:48', '2025-03-30 00:02:41');
INSERT INTO `banner` VALUES (50, '/static/images/banner2.jpg', '/pages/activity/detail?id=1', '快递送货优惠活动', 2, 1, '2025-03-26 19:51:48', '2025-03-26 19:51:48');
INSERT INTO `banner` VALUES (51, '/static/images/banner3.jpg', '/pages/courier/recruitment', '快递员招募计划', 3, 1, '2025-03-26 19:51:48', '2025-03-26 19:51:48');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '点击链接URL',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否激活',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始展示时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束展示时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (31, '乡递通平台正式上线，为乡村快递提供便捷服务', '/pages/notice/detail?id=1', 1, 1, NULL, NULL, '2025-03-25 14:05:44', '2025-03-30 00:46:45');
INSERT INTO `notice` VALUES (32, '成为快递员，每单收益最高可达10元', '/pages/courier/recruitment', 2, 1, NULL, NULL, '2025-03-25 14:05:44', '2025-04-15 01:45:25');
INSERT INTO `notice` VALUES (33, '乡递通招募村镇快递员，详情查看招募页面', '/pages/notice/detail?id=3', 3, 1, NULL, NULL, '2025-03-25 14:05:44', '2025-03-25 14:05:44');

-- ----------------------------
-- Table structure for t_address
-- ----------------------------
DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `street` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '街道',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NULL DEFAULT 0 COMMENT '是否默认地址(0-否,1-是)',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_address
-- ----------------------------
INSERT INTO `t_address` VALUES (1, 1, '张三', '13800138001', '浙江省', '杭州市', '西湖区', '文教路', '浙江大学紫金港校区', 1, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (2, 1, '张三父母', '13800138001', '浙江省', '湖州市', '德清县', '乾元镇', '湖州德清县乾元镇农民新村12号', 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (3, 2, '李四', '13800138002', '浙江省', '杭州市', '余杭区', '五常街道', '余杭五常街道塘栖小区8号楼3单元301', 1, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (4, 3, '王五', '13800138003', '浙江省', '嘉兴市', '桐乡市', '梧桐街道', '嘉兴桐乡市梧桐街道振兴路89号', 1, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (5, 4, '赵六', '13800138004', '浙江省', '金华市', '婺城区', '城中街道', '金华婺城区城中街道博爱路56号', 1, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (6, 7, '周九', '13800138007', '浙江省', '衢州市', '柯城区', '新新街道', '衢州柯城区新新街道荷花路23号', 1, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (7, 8, '吴十', '13800138008', '浙江省', '杭州市', '拱墅区', '湖墅街道', '杭州拱墅区湖墅街道湖墅南路18号', 1, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (8, 10, '王十二', '13800138010', '浙江省', '温州市', '鹿城区', '五马街道', '温州鹿城区五马街道人民路99号', 1, '2025-03-29 10:42:09', '2025-03-29 10:42:09');
INSERT INTO `t_address` VALUES (9, 1, '张三', '13800138001', '浙江省', '杭州市', '西湖区', '文教路', '浙江大学紫金港校区', 1, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (10, 1, '张三父母', '13800138001', '浙江省', '湖州市', '德清县', '乾元镇', '湖州德清县乾元镇农民新村12号', 0, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (11, 2, '李四', '13800138002', '浙江省', '杭州市', '余杭区', '五常街道', '余杭五常街道塘栖小区8号楼3单元301', 1, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (12, 3, '王五', '13800138003', '浙江省', '嘉兴市', '桐乡市', '梧桐街道', '嘉兴桐乡市梧桐街道振兴路89号', 1, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (13, 4, '赵六', '13800138004', '浙江省', '金华市', '婺城区', '城中街道', '金华婺城区城中街道博爱路56号', 1, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (14, 7, '周九', '13800138007', '浙江省', '衢州市', '柯城区', '新新街道', '衢州柯城区新新街道荷花路23号', 1, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (15, 8, '吴十', '13800138008', '浙江省', '杭州市', '拱墅区', '湖墅街道', '杭州拱墅区湖墅街道湖墅南路18号', 1, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (16, 10, '王十二', '13800138010', '浙江省', '温州市', '鹿城区', '五马街道', '温州鹿城区五马街道人民路99号', 1, '2025-03-29 10:44:00', '2025-03-29 10:44:00');
INSERT INTO `t_address` VALUES (17, 3, '李大壮', '13800138001', '山东省', '青岛市', '即墨区', '龙泉街道', '社区村66号', 1, '2025-04-01 21:15:31', '2025-04-01 21:15:31');
INSERT INTO `t_address` VALUES (18, 3, '李大壮(公司)', '13800138001', '山东省', '青岛市', '市北区', '市北街道', '敦化路328号', 0, '2025-04-01 21:15:31', '2025-04-01 21:15:31');
INSERT INTO `t_address` VALUES (19, 4, '张小花', '13800138002', '山东省', '青岛市', '即墨区', '通济街道', '幸福村88号', 1, '2025-04-01 21:15:31', '2025-04-01 21:15:31');
INSERT INTO `t_address` VALUES (20, 4, '张小花(学校)', '13800138002', '山东省', '青岛市', '崂山区', '科教园', '松岭路99号', 0, '2025-04-01 21:15:31', '2025-04-01 21:15:31');

-- ----------------------------
-- Table structure for t_company
-- ----------------------------
DROP TABLE IF EXISTS `t_company`;
CREATE TABLE `t_company`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司编码',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公司名称',
  `short_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司简称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司logo',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司地址',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '快递公司表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_company
-- ----------------------------
INSERT INTO `t_company` VALUES (1, 'SF', '顺丰速运', '顺丰', '/static/images/sf.png', '张经理', '400-111-1111', '深圳市南山区科技园', '国内领先的快递公司', 0, 1, '2025-03-30 01:25:11', '2025-04-15 02:13:40', 0);
INSERT INTO `t_company` VALUES (2, 'YTO', '圆通速递', '圆通', '/static/images/yt.png', '李经理', '400-222-2222', '上海市青浦区华新镇', '全国性快递物流服务商', 0, 2, '2025-03-30 01:25:11', '2025-04-15 02:13:58', 0);
INSERT INTO `t_company` VALUES (3, 'ZTO', '中通快递', '中通', '/static/images/zt.png', '王经理', '400-333-3333', '上海市青浦区华徐公路', '国内大型综合物流服务商', 0, 3, '2025-03-30 01:25:11', '2025-04-15 02:14:03', 0);
INSERT INTO `t_company` VALUES (4, 'STO', '申通快递', '申通', '/static/images/st.png', '赵经理', '400-444-4444', '上海市青浦区华徐公路', '全国性连锁快递企业', 0, 4, '2025-03-30 01:25:11', '2025-04-15 02:14:08', 0);
INSERT INTO `t_company` VALUES (5, 'YUNDA', '韵达速递', '韵达', '/static/images/yd.png', '钱经理', '400-555-5555', '上海市青浦区盈港东路', '全国性快递物流企业', 0, 5, '2025-03-30 01:25:11', '2025-04-15 02:14:13', 0);

-- ----------------------------
-- Table structure for t_config
-- ----------------------------
DROP TABLE IF EXISTS `t_config`;
CREATE TABLE `t_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_config
-- ----------------------------

-- ----------------------------
-- Table structure for t_courier
-- ----------------------------
DROP TABLE IF EXISTS `t_courier`;
CREATE TABLE `t_courier`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '蹇??鍛業D',
  `user_id` bigint NOT NULL COMMENT '鍏宠仈鐢ㄦ埛ID',
  `service_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鏈嶅姟鍖哄煙',
  `service_status` tinyint(1) NULL DEFAULT 0 COMMENT '鏈嶅姟鐘舵?(0-浼戞伅,1-鎺ュ崟涓?',
  `audit_status` tinyint(1) NULL DEFAULT 0 COMMENT '瀹℃牳鐘舵?(0-寰呭?鏍?1-宸查?杩?2-鏈??杩?',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '璇勫垎',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '璐︽埛浣欓?',
  `completed_orders` int NULL DEFAULT 0 COMMENT '宸插畬鎴愯?鍗曟暟',
  `id_card_front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '韬?唤璇佹?闈㈢収鐗嘦RL',
  `id_card_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '韬?唤璇佽儗闈㈢収鐗嘦RL',
  `work_start_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '08:00' COMMENT '宸ヤ綔寮??鏃堕棿',
  `work_end_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '20:00' COMMENT '宸ヤ綔缁撴潫鏃堕棿',
  `vehicle` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浜ら?宸ュ叿',
  `introduction` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '绠?粙',
  `response_time` int NULL DEFAULT 15 COMMENT '骞冲潎鍝嶅簲鏃堕棿(鍒嗛挓)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `status` int NULL DEFAULT 0 COMMENT '快递员状态(0-正常,1-禁用)',
  `current_latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '当前纬度',
  `current_longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '当前经度',
  `location_updated_at` datetime NULL DEFAULT NULL COMMENT '位置更新时间',
  `canceled_orders` int NULL DEFAULT 0 COMMENT '取消订单数',
  `monthly_orders` int NULL DEFAULT 0 COMMENT '月订单数',
  `monthly_income` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '月收入',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行账号',
  `bank_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户银行',
  `station_id` bigint NULL DEFAULT NULL COMMENT '关联服务站点ID',
  `availability` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1,2,3,4,5,6,7' COMMENT '可用工作日(1-7代表周一至周日，逗号分隔)',
  `max_orders` int NULL DEFAULT 20 COMMENT '每日最大接单数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_courier_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '蹇??鍛樿〃' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_courier
-- ----------------------------
INSERT INTO `t_courier` VALUES (2, 2, '福田区', 1, 1, 4.7, 50.00, 218, '/static/images/id-card-front.jpg', '/static/images/id-card-back.jpg', '09:00', '21:00', '摩托车', '3年快递配送经验，服务态度好', 8, '2025-03-25 10:39:24', '2025-03-25 10:39:24', 0, NULL, NULL, NULL, 0, 0, 0.00, NULL, NULL, NULL, '1,2,3,4,5,6,7', 20);
INSERT INTO `t_courier` VALUES (5, 1, '广东省深圳市南山区', 1, 1, 4.9, 0.00, 326, '/static/images/id-card-front.jpg', '/static/images/id-card-back.jpg', '08:00', '20:00', '电动车', '5年快递配送经验，熟悉南山区域路线', 5, '2025-03-25 11:14:01', '2025-03-25 11:14:01', 0, NULL, NULL, NULL, 0, 0, 0.00, NULL, NULL, NULL, '1,2,3,4,5,6,7', 20);
INSERT INTO `t_courier` VALUES (7, 103, '山东省青岛市即墨区', 1, 1, 4.8, 1200.50, 156, 'https://example.com/id_front1.jpg', 'https://example.com/id_back1.jpg', '08:00', '18:00', '电动三轮车', '5年配送经验，熟悉本地区域', 10, '2025-04-01 21:13:19', '2025-04-01 21:13:19', 1, NULL, NULL, NULL, 0, 0, 0.00, NULL, NULL, NULL, '1,2,3,4,5,6,7', 20);
INSERT INTO `t_courier` VALUES (8, 104, '山东省青岛市即墨区', 1, 1, 4.6, 890.20, 92, 'https://example.com/id_front2.jpg', 'https://example.com/id_back2.jpg', '09:00', '19:00', '摩托车', '3年物流经验，服务热情周到', 15, '2025-04-01 21:13:19', '2025-04-01 21:13:19', 1, NULL, NULL, NULL, 0, 0, 0.00, NULL, NULL, NULL, '1,2,3,4,5,6,7', 20);
INSERT INTO `t_courier` VALUES (9, 42, '123', 0, 1, 5.0, 0.00, 0, '/static/images/default-avatar.png', '/static/images/default-avatar.png', '08:00', '20:00', NULL, NULL, 15, '2025-04-13 12:32:04', '2025-04-13 12:32:04', 0, NULL, NULL, NULL, 0, 0, 0.00, NULL, NULL, NULL, '1,2,3,4,5,6,7', 20);

-- ----------------------------
-- Table structure for t_courier_old
-- ----------------------------
DROP TABLE IF EXISTS `t_courier_old`;
CREATE TABLE `t_courier_old`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '快递员ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `service_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鏈嶅姟鍖哄煙',
  `service_status` tinyint(1) NULL DEFAULT 0 COMMENT '服务状态(0-休息,1-接单中)',
  `audit_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态(0-待审核,1-已通过,2-未通过)',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '评分',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `completed_orders` int NULL DEFAULT 0 COMMENT '已完成订单数',
  `id_card_front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证正面照片URL',
  `id_card_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证背面照片URL',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `work_start_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '08:00' COMMENT '宸ヤ綔鏃堕棿璧峰?',
  `work_end_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '20:00' COMMENT '宸ヤ綔鏃堕棿缁撴潫',
  `vehicle` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浜ら?宸ュ叿',
  `introduction` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '涓?汉绠?粙',
  `response_time` int NULL DEFAULT 15 COMMENT '鍝嶅簲鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_courier_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '快递员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_courier_old
-- ----------------------------

-- ----------------------------
-- Table structure for t_courier_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_courier_tag`;
CREATE TABLE `t_courier_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `courier_id` bigint NOT NULL COMMENT '快递员ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签内容',
  `tag_type` tinyint(1) NULL DEFAULT 0 COMMENT '标签类型(0-系统标签,1-用户评价标签)',
  `count` int NULL DEFAULT 1 COMMENT '标签计数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_courier_tag`(`courier_id` ASC, `tag_name` ASC) USING BTREE,
  CONSTRAINT `fk_tag_courier` FOREIGN KEY (`courier_id`) REFERENCES `t_courier_old` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '快递员标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_courier_tag
-- ----------------------------

-- ----------------------------
-- Table structure for t_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `t_evaluation`;
CREATE TABLE `t_evaluation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `courier_id` bigint NOT NULL COMMENT '快递员ID',
  `score` tinyint(1) NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `user_to_courier` tinyint(1) NULL DEFAULT 0 COMMENT '评价方向(0-用户评价快递员,1-快递员评价用户)',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_courier_id`(`courier_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_evaluation
-- ----------------------------

-- ----------------------------
-- Table structure for t_express_company
-- ----------------------------
DROP TABLE IF EXISTS `t_express_company`;
CREATE TABLE `t_express_company`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公司ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公司名称',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公司编码',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司LOGO',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司联系电话',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公司官网',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0-正常,1-禁用)',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_company_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 265 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '快递公司表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_express_company
-- ----------------------------
INSERT INTO `t_express_company` VALUES (1, '顺丰速运', 'SF', '/static/images/sf.png', '95338', '95338', 'https://www.sf-express.com', 0, '2025-03-24 23:33:42', '2025-03-24 23:33:42');
INSERT INTO `t_express_company` VALUES (2, '中通快递', 'ZTO', '/static/images/zt.png', NULL, '95311', 'https://www.zto.com', 0, '2025-03-24 23:33:42', '2025-03-24 23:33:42');
INSERT INTO `t_express_company` VALUES (3, '圆通速递', 'YTO', '/static/images/yt.png', NULL, '95554', 'https://www.yto.net.cn', 0, '2025-03-24 23:33:42', '2025-03-24 23:33:42');
INSERT INTO `t_express_company` VALUES (4, '韵达快递', 'YD', '/static/images/yd.png', '95546', '95546', 'https://www.yundaex.com', 0, '2025-03-24 23:33:42', '2025-03-24 23:33:42');
INSERT INTO `t_express_company` VALUES (5, '申通快递', 'STO', '/static/images/st.png', '95543', '95543', 'https://www.sto.cn', 0, '2025-03-24 23:33:42', '2025-03-24 23:33:42');
INSERT INTO `t_express_company` VALUES (6, '京东物流', 'JD', '/static/images/jd.png', '950616', '950616', 'https://www.jdl.com', 0, '2025-03-24 23:33:42', '2025-03-24 23:33:42');
INSERT INTO `t_express_company` VALUES (207, '圆通快递', 'YT', '/static/images/yt.png', '95554', NULL, NULL, 1, '2025-03-30 20:31:19', '2025-03-30 20:31:19');
INSERT INTO `t_express_company` VALUES (208, '中通快递', 'ZT', '/static/images/zt.png', '95311', NULL, NULL, 1, '2025-03-30 20:31:19', '2025-03-30 20:31:19');
INSERT INTO `t_express_company` VALUES (209, '百世快递', 'HT', '/static/images/icon/ht.png', '95320', NULL, NULL, 1, '2025-03-30 20:31:19', '2025-03-30 20:31:19');
INSERT INTO `t_express_company` VALUES (210, '邮政快递', 'EMS', '/static/images/icon/ems.png', '11183', NULL, NULL, 1, '2025-03-30 20:31:19', '2025-03-30 20:31:19');
INSERT INTO `t_express_company` VALUES (211, '乡递通快递', 'RuralExpress', '/static/images/company-logo.png', '400-123-4567', NULL, NULL, 1, '2025-03-30 20:31:19', '2025-03-30 20:31:19');
INSERT INTO `t_express_company` VALUES (263, '德邦快递', 'DBKD', 'https://example.com/dbl_logo.png', NULL, '95353', 'https://www.deppon.com', 0, '2025-04-01 21:16:55', '2025-04-01 21:16:55');
INSERT INTO `t_express_company` VALUES (264, '极兔速递', 'JTSD', 'https://example.com/jtexpress_logo.png', NULL, '950999', 'https://www.jtexpress.com', 0, '2025-04-01 21:16:55', '2025-04-01 21:16:55');

-- ----------------------------
-- Table structure for t_logistics_trace
-- ----------------------------
DROP TABLE IF EXISTS `t_logistics_trace`;
CREATE TABLE `t_logistics_trace`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轨迹ID',
  `package_id` bigint NOT NULL COMMENT '包裹ID',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
  `time` datetime NOT NULL COMMENT '时间',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '位置',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_package_id`(`package_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '物流轨迹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_logistics_trace
-- ----------------------------
INSERT INTO `t_logistics_trace` VALUES (1, 1, '运输中', '2025-03-30 20:40:08', '【南昌市】快件正在通过江西分拨中心转运', '江西省南昌市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (2, 1, '已揽收', '2025-03-29 20:40:08', '【赣州市】快件已由【赣州南康网点】揽收，正发往【江西分拨中心】', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (3, 1, '已下单', '2025-03-28 20:40:08', '卖家已发货', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (4, 1, '订单创建', '2025-03-27 20:40:08', '买家已下单', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (5, 2, '派送中', '2025-03-30 20:40:08', '【南昌市】快件已到达【南昌分拨中心】，快递员正在派送', '江西省南昌市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (6, 2, '已到达', '2025-03-30 16:40:08', '【南昌市】快件已到达【南昌分拨中心】', '江西省南昌市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (7, 2, '运输中', '2025-03-29 20:40:08', '【赣州市】快件已从【赣州分拨中心】发出，正发往【南昌分拨中心】', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (8, 2, '已揽收', '2025-03-28 20:40:08', '【赣州市】快件已由【赣州南康网点】揽收', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (9, 2, '订单创建', '2025-03-27 20:40:08', '包裹已由商家发出', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (10, 3, '已签收', '2025-03-30 20:40:08', '【赣州市】包裹已签收，签收人：本人，感谢使用中通快递', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (11, 3, '派送中', '2025-03-30 16:40:08', '【赣州市】快件已由【赣州赣县区网点】安排派送，快递员：王师傅，电话：13812345678', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (12, 3, '已到达', '2025-03-29 20:40:08', '【赣州市】快件已到达【赣州赣县区网点】', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (13, 3, '运输中', '2025-03-28 20:40:08', '【南昌市】快件已从【南昌分拨中心】发出，正发往【赣州分拨中心】', '江西省南昌市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (14, 3, '已揽收', '2025-03-27 20:40:08', '【南昌市】快件已由【南昌东湖区网点】揽收', '江西省南昌市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (15, 4, '已到达', '2025-03-30 20:40:08', '【赣州市】包裹已到达【赣州章贡区网点】，等待派送', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (16, 4, '运输中', '2025-03-30 08:40:08', '【赣州市】包裹已到达【赣州分拨中心】', '江西省赣州市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (17, 4, '运输中', '2025-03-29 20:40:08', '【南昌市】包裹已从【南昌西湖区网点】发出，正发往【赣州分拨中心】', '江西省南昌市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (18, 4, '已揽收', '2025-03-28 20:40:08', '【南昌市】包裹已由【南昌西湖区网点】揽收', '江西省南昌市', '2025-03-30 20:40:08');
INSERT INTO `t_logistics_trace` VALUES (19, 1, '运输中', '2025-03-30 20:43:40', '【南昌市】快件正在通过江西分拨中心转运', '江西省南昌市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (20, 1, '已揽收', '2025-03-29 20:43:40', '【赣州市】快件已由【赣州南康网点】揽收，正发往【江西分拨中心】', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (21, 1, '已下单', '2025-03-28 20:43:40', '卖家已发货', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (22, 1, '订单创建', '2025-03-27 20:43:40', '买家已下单', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (23, 2, '派送中', '2025-03-30 20:43:40', '【南昌市】快件已到达【南昌分拨中心】，快递员正在派送', '江西省南昌市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (24, 2, '已到达', '2025-03-30 16:43:40', '【南昌市】快件已到达【南昌分拨中心】', '江西省南昌市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (25, 2, '运输中', '2025-03-29 20:43:40', '【赣州市】快件已从【赣州分拨中心】发出，正发往【南昌分拨中心】', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (26, 2, '已揽收', '2025-03-28 20:43:40', '【赣州市】快件已由【赣州南康网点】揽收', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (27, 2, '订单创建', '2025-03-27 20:43:40', '包裹已由商家发出', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (28, 3, '已签收', '2025-03-30 20:43:40', '【赣州市】包裹已签收，签收人：本人，感谢使用中通快递', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (29, 3, '派送中', '2025-03-30 16:43:40', '【赣州市】快件已由【赣州赣县区网点】安排派送，快递员：王师傅，电话：13812345678', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (30, 3, '已到达', '2025-03-29 20:43:40', '【赣州市】快件已到达【赣州赣县区网点】', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (31, 3, '运输中', '2025-03-28 20:43:40', '【南昌市】快件已从【南昌分拨中心】发出，正发往【赣州分拨中心】', '江西省南昌市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (32, 3, '已揽收', '2025-03-27 20:43:40', '【南昌市】快件已由【南昌东湖区网点】揽收', '江西省南昌市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (33, 4, '已到达', '2025-03-30 20:43:40', '【赣州市】包裹已到达【赣州章贡区网点】，等待派送', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (34, 4, '运输中', '2025-03-30 08:43:40', '【赣州市】包裹已到达【赣州分拨中心】', '江西省赣州市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (35, 4, '运输中', '2025-03-29 20:43:40', '【南昌市】包裹已从【南昌西湖区网点】发出，正发往【赣州分拨中心】', '江西省南昌市', '2025-03-30 20:43:40');
INSERT INTO `t_logistics_trace` VALUES (36, 4, '已揽收', '2025-03-28 20:43:40', '【南昌市】包裹已由【南昌西湖区网点】揽收', '江西省南昌市', '2025-03-30 20:43:40');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `courier_id` bigint NULL DEFAULT NULL COMMENT '快递员ID',
  `sender_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '寄件人姓名',
  `sender_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '寄件人手机号',
  `sender_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '寄件地址',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人手机号',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件地址',
  `package_type` tinyint(1) NOT NULL COMMENT '包裹类型(0-小件,1-中件,2-大件)',
  `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '重量(kg)',
  `price` decimal(10, 2) NOT NULL COMMENT '配送费',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '订单状态(0-待接单,1-已接单,2-取件中,3-已取件,4-配送中,5-已送达,6-已完成,7-已取消)',
  `expected_pickup_time` datetime NULL DEFAULT NULL COMMENT '期望取件时间',
  `actual_pickup_time` datetime NULL DEFAULT NULL COMMENT '实际取件时间',
  `expected_delivery_time` datetime NULL DEFAULT NULL COMMENT '期望送达时间',
  `actual_delivery_time` datetime NULL DEFAULT NULL COMMENT '实际送达时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `sender_longitude` double NULL DEFAULT NULL COMMENT '寄件地址经度',
  `sender_latitude` double NULL DEFAULT NULL COMMENT '寄件地址纬度',
  `receiver_longitude` double NULL DEFAULT NULL COMMENT '收件地址经度',
  `receiver_latitude` double NULL DEFAULT NULL COMMENT '收件地址纬度',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_courier_id`(`courier_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (1, 'RE20230325001', 3, 2, '李大壮', '13800138001', '山东省青岛市即墨区龙泉街道社区村66号', '王收件', '13700137001', '山东省青岛市市北区辽宁路167号', 1, 3.50, 15.00, 6, '2023-03-25 14:00:00', '2023-03-25 14:10:23', '2023-03-25 16:00:00', '2023-03-25 15:55:42', '小心轻放，内含易碎物品', NULL, '2023-03-25 10:15:30', '2023-03-25 16:10:22', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (2, 'RE20230326001', 4, 2, '张小花', '13800138002', '山东省青岛市即墨区通济街道幸福村88号', '赵收件', '13700137002', '山东省青岛市李沧区重庆中路92号', 0, 1.20, 8.00, 3, '2023-03-26 10:00:00', '2023-03-26 10:05:17', '2023-03-26 12:00:00', NULL, '普通物品', NULL, '2023-03-26 08:30:45', '2023-03-26 10:15:22', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (3, 'RE20230326002', 3, 5, '李大壮', '13800138001', '山东省青岛市即墨区龙泉街道社区村66号', '钱收件', '13700137003', '山东省青岛市崂山区科苑纬四路100号', 2, 8.00, 25.00, 2, '2023-03-26 15:00:00', NULL, '2023-03-26 17:00:00', NULL, '大件电器，需要两人搬运', NULL, '2023-03-26 11:42:18', '2023-03-26 14:22:33', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (4, 'RE20230405001', 1, 2, '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '李明', '13711112222', '山东省青岛市崂山区松岭路99号', 1, 2.80, 15.00, 3, '2023-04-05 10:00:00', '2023-04-05 10:15:20', '2023-04-05 16:00:00', NULL, '普通快递，不着急', NULL, '2025-04-01 21:20:46', '2025-04-01 21:20:46', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (5, 'RE20230406001', 1, 5, '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '王芳', '13722223333', '山东省青岛市黄岛区长江中路168号', 2, 6.50, 30.00, 1, '2023-04-06 14:00:00', NULL, '2023-04-06 18:00:00', NULL, '大件物品，需要两人搬运', NULL, '2025-04-01 21:20:46', '2025-04-01 21:20:46', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (6, 'RE20230407001', 1, NULL, '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '刘强', '13733334444', '山东省青岛市城阳区城阳街道正阳路388号', 0, 1.20, 10.00, 0, '2023-04-07 09:00:00', NULL, '2023-04-07 12:00:00', NULL, '小件快递，文件资料', NULL, '2025-04-01 21:20:46', '2025-04-01 21:20:46', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (7, 'RE20230410001', 1, 2, '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '李明', '13711112222', '山东省青岛市崂山区松岭路99号', 1, 2.80, 15.00, 0, '2025-04-02 21:24:26', NULL, '2025-04-03 21:24:26', NULL, '普通快递，不着急', NULL, '2025-04-01 21:24:26', '2025-04-01 21:24:26', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (8, 'RE20230410002', 1, 5, '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '王芳', '13722223333', '山东省青岛市黄岛区长江中路168号', 2, 6.50, 30.00, 4, '2025-03-31 21:24:26', '2025-03-31 21:24:26', '2025-04-02 21:24:26', NULL, '大件物品，需要搬运', NULL, '2025-03-31 21:24:26', '2025-04-01 21:24:26', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (9, 'RE20230410003', 1, 2, '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '刘强', '13733334444', '山东省青岛市城阳区城阳街道正阳路388号', 0, 1.20, 10.00, 5, '2025-03-29 21:24:26', '2025-03-29 21:24:26', '2025-03-31 21:24:26', '2025-03-31 21:24:26', '小件快递', NULL, '2025-03-29 21:24:26', '2025-03-31 21:24:26', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (10, '202504130043447287', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 00:43:45', '2025-04-13 00:43:45', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (11, '202504130043505142', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 00:43:51', '2025-04-13 00:43:51', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (12, '202504130043576176', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 00:43:58', '2025-04-13 00:43:58', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (13, '202504130048150444', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 00:48:16', '2025-04-13 00:48:16', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (14, '202504130051191196', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 00:51:20', '2025-04-13 00:51:20', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (15, '202504130102139328', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 01:02:13', '2025-04-13 01:02:13', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (16, '202504130102217687', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 01:02:21', '2025-04-13 01:02:21', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (17, '202504131110291720', 1, NULL, '1', '15579961188', '北京市北京市东城区 1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 11:10:29', '2025-04-13 14:32:56', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (18, '202504131117302249', 1, NULL, '用户1188', '15579961188', 'asfdsafd', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 11:17:30', '2025-04-13 11:17:30', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (19, '202504131129507507', 1, NULL, '用户1188', '15579961188', '13123', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 7, NULL, NULL, NULL, NULL, NULL, '用户主动取消', '2025-04-13 11:29:51', '2025-04-13 15:02:20', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (20, '202504131130040597', 1, NULL, '用户1188', '15579961188', '13123', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 7, NULL, NULL, NULL, NULL, NULL, '用户主动取消', '2025-04-13 11:30:05', '2025-04-13 15:02:13', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (21, '202504131145306437', 1, NULL, '用户1188', '15579961188', '1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 11:45:31', '2025-04-13 11:45:39', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (22, '202504131150073153', 1, NULL, '用户1188', '15579961188', '1', '1', '15579961188', '北京市北京市东城区 1', 0, 1.00, 15.00, 4, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 11:50:08', '2025-04-13 14:32:50', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (23, '202504131445148840', 1, NULL, '用户11881', '15579961188', '1', '1', '15579991111', '1', 0, 1.00, 15.00, 1, NULL, NULL, NULL, NULL, NULL, NULL, '2025-04-13 14:45:14', '2025-04-13 14:45:16', NULL, NULL, NULL, NULL);
INSERT INTO `t_order` VALUES (24, '202504131445193523', 1, NULL, '用户11881', '15579961188', '1', '1', '15579991111', '1', 0, 1.00, 15.00, 7, NULL, NULL, NULL, NULL, NULL, '用户主动取消', '2025-04-13 14:45:20', '2025-04-13 15:01:04', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for t_package
-- ----------------------------
DROP TABLE IF EXISTS `t_package`;
CREATE TABLE `t_package`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '包裹ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_id` bigint NOT NULL COMMENT '快递公司ID',
  `tracking_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '快递单号',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件人电话',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件地址',
  `sender_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发件人姓名',
  `sender_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发件人电话',
  `sender_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发件地址',
  `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '包裹重量(kg)',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '包裹状态(0-待收取,1-已收取,2-已签收,3-异常)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `estimated_delivery_time` datetime NULL DEFAULT NULL COMMENT '预计送达时间',
  `signed_time` datetime NULL DEFAULT NULL COMMENT '签收时间',
  `delivery_type` tinyint(1) NULL DEFAULT 0 COMMENT '取件方式(0-上门取件,1-服务点自取)',
  `station_id` bigint NULL DEFAULT NULL COMMENT '服务点ID',
  `courier_id` bigint NULL DEFAULT NULL COMMENT '快递员ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `order_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_tracking`(`tracking_no` ASC, `company_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_company_id`(`company_id` ASC) USING BTREE,
  INDEX `idx_station_id`(`station_id` ASC) USING BTREE,
  INDEX `idx_courier_id`(`courier_id` ASC) USING BTREE,
  CONSTRAINT `fk_package_company` FOREIGN KEY (`company_id`) REFERENCES `t_express_company` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_package_courier` FOREIGN KEY (`courier_id`) REFERENCES `t_courier_old` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_package_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_package_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '包裹表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_package
-- ----------------------------
INSERT INTO `t_package` VALUES (1, 33, 1, 'SF1234567890', '张三', '13800138000', '江西省南昌市青山湖区高新大道1888号', '李四', '13900139000', '江西省赣州市章贡区红旗大道123号', 2.00, 2, '文件包裹 2kg', '2025-04-01 20:40:08', NULL, 0, NULL, NULL, '2025-03-30 20:40:08', '2025-03-30 20:40:08', NULL);
INSERT INTO `t_package` VALUES (2, 33, 207, 'YT9876543210', '王五', '13700137000', '江西省赣州市南康区健康路456号', '赵六', '13600136000', '江西省南昌市青云谱区井冈山大道2000号', 1.00, 4, '衣服 1kg', '2025-03-31 20:40:08', NULL, 0, NULL, NULL, '2025-03-30 20:40:08', '2025-03-30 20:40:08', NULL);
INSERT INTO `t_package` VALUES (3, 33, 208, 'ZT5678901234', '孙七', '13500135000', '江西省赣州市赣县区红金大道789号', '周八', '13400134000', '江西省南昌市东湖区三经路555号', 3.00, 5, '电子产品 3kg', '2025-03-29 20:40:08', '2025-03-30 20:43:40', 0, NULL, NULL, '2025-03-27 20:40:08', '2025-03-27 20:40:08', NULL);
INSERT INTO `t_package` VALUES (4, 33, 6, 'JD2345678901', '吴九', '13300133000', '江西省赣州市章贡区黄金大道100号', '郑十', '13200132000', '江西省南昌市西湖区北京西路88号', 1.50, 3, '日用品 1.5kg', '2025-03-31 20:40:08', NULL, 0, NULL, NULL, '2025-03-28 20:40:08', '2025-03-28 20:40:08', NULL);
INSERT INTO `t_package` VALUES (19, 3, 1, 'SF2234567890', '李大壮', '13800138001', '山东省青岛市即墨区龙泉街道社区村66号', NULL, NULL, NULL, NULL, 1, '轻拿轻放，易碎物品', '2023-03-28 14:00:00', NULL, 1, 4, NULL, '2023-03-25 10:22:33', '2023-03-25 15:35:46', NULL);
INSERT INTO `t_package` VALUES (20, 4, 2, 'ZTO2876543210', '张小花', '13800138002', '山东省青岛市即墨区通济街道幸福村88号', NULL, NULL, NULL, NULL, 0, '生鲜水果，请及时签收', '2023-03-27 16:30:00', NULL, 0, NULL, NULL, '2023-03-26 09:15:27', '2023-03-26 09:15:27', NULL);
INSERT INTO `t_package` VALUES (21, 3, 3, 'YTO2432167890', '李大壮', '13800138001', '山东省青岛市即墨区龙泉街道社区村66号', NULL, NULL, NULL, NULL, 2, '普通快递', '2023-03-26 11:00:00', '2023-03-26 10:52:31', 1, 4, NULL, '2023-03-24 17:42:19', '2023-03-26 10:52:31', NULL);
INSERT INTO `t_package` VALUES (22, 4, 4, 'YD2357924680', '张小花', '13800138002', '山东省青岛市即墨区通济街道幸福村88号', NULL, NULL, NULL, NULL, 1, '衣物，勿压', '2023-03-29 12:00:00', NULL, 1, 5, NULL, '2023-03-27 08:39:10', '2023-03-27 14:23:45', NULL);
INSERT INTO `t_package` VALUES (23, 3, 5, 'STO2468013579', '李大壮', '13800138001', '山东省青岛市即墨区龙泉街道社区村66号', NULL, NULL, NULL, NULL, 0, '文件，请小心保管', '2023-03-30 09:30:00', NULL, 0, NULL, NULL, '2023-03-27 16:28:53', '2023-03-27 16:28:53', NULL);
INSERT INTO `t_package` VALUES (29, 3, 1, 'SF3234567890', '李大壮', '13800138001', '山东省青岛市即墨区龙泉街道社区村66号', '顺丰快递', '95338', '广东省深圳市南山区科技园', 2.50, 1, '轻拿轻放，易碎物品', '2023-03-28 14:00:00', NULL, 1, 4, NULL, '2023-03-25 10:22:33', '2023-03-25 15:35:46', NULL);
INSERT INTO `t_package` VALUES (30, 4, 2, 'ZTO3876543210', '张小花', '13800138002', '山东省青岛市即墨区通济街道幸福村88号', '中通快递', '95311', '上海市青浦区华新镇', 1.80, 0, '生鲜水果，请及时签收', '2023-03-27 16:30:00', NULL, 0, NULL, NULL, '2023-03-26 09:15:27', '2023-03-26 09:15:27', NULL);
INSERT INTO `t_package` VALUES (31, 1, 1, 'SF9876543210', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '李四', '13800001234', '北京市海淀区中关村南大街5号', 3.20, 0, '生日礼物，请轻拿轻放', '2023-04-05 16:00:00', NULL, 0, NULL, NULL, '2025-04-01 21:25:03', '2025-04-01 21:25:03', 4);
INSERT INTO `t_package` VALUES (32, 1, 2, 'ZTO1234567890', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '王五', '13800002222', '上海市浦东新区张江高科技园区博云路2号', 1.50, 1, '文件资料，已签收', '2023-04-02 10:00:00', '2023-04-02 09:45:30', 0, NULL, NULL, '2025-04-01 21:25:03', '2025-04-01 21:25:03', 4);
INSERT INTO `t_package` VALUES (33, 1, 3, 'YTO9876543210', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '赵六', '13800003333', '广州市天河区天河路385号', 4.80, 2, '衣物，不易压皱', '2023-04-08 14:00:00', NULL, 1, 4, NULL, '2025-04-01 21:25:03', '2025-04-01 21:25:03', 4);
INSERT INTO `t_package` VALUES (34, 1, 1, 'SF20230410001', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '顺丰快递', '95338', '广东省深圳市南山区科技园', 3.20, 0, '待收取包裹', '2025-04-03 21:24:26', NULL, 0, NULL, NULL, '2025-04-01 21:25:03', '2025-04-01 21:25:03', 4);
INSERT INTO `t_package` VALUES (35, 1, 2, 'ZTO20230410001', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '中通快递', '95311', '上海市青浦区华新镇', 1.50, 1, '已收取包裹', '2025-04-02 21:24:26', NULL, 0, NULL, NULL, '2025-04-01 21:25:03', '2025-04-01 21:25:03', 4);
INSERT INTO `t_package` VALUES (36, 1, 3, 'YTO20230410001', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '圆通快递', '95554', '上海市青浦区华徐公路', 2.30, 2, '已签收包裹', '2025-03-31 21:24:26', NULL, 1, NULL, NULL, '2025-04-01 21:25:03', '2025-04-01 21:25:03', 4);
INSERT INTO `t_package` VALUES (37, 1, 1, 'TEST000001', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '顺丰快递', '95338', '广东省深圳市', 2.50, 0, '待揽收测试包裹', '2025-04-03 21:31:49', NULL, 0, NULL, NULL, '2025-04-01 21:31:49', '2025-04-01 21:31:49', NULL);
INSERT INTO `t_package` VALUES (38, 1, 2, 'TEST000002', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '中通快递', '95311', '上海市闵行区', 1.80, 1, '已揽收测试包裹', '2025-04-02 21:31:49', NULL, 0, NULL, NULL, '2025-04-01 21:31:49', '2025-04-01 21:31:49', NULL);
INSERT INTO `t_package` VALUES (39, 1, 3, 'TEST000003', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '圆通快递', '95554', '上海市青浦区', 3.20, 2, '运输中测试包裹', '2025-04-02 21:31:49', NULL, 0, NULL, NULL, '2025-04-01 21:31:49', '2025-04-01 21:31:49', NULL);
INSERT INTO `t_package` VALUES (40, 1, 4, 'TEST000004', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '韵达快递', '95546', '上海市宝山区', 2.10, 3, '已到达测试包裹', '2025-04-01 21:31:49', NULL, 0, NULL, NULL, '2025-04-01 21:31:49', '2025-04-01 21:31:49', NULL);
INSERT INTO `t_package` VALUES (41, 1, 5, 'TEST000005', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '申通快递', '95543', '上海市松江区', 1.50, 4, '派送中测试包裹', '2025-04-01 21:31:49', NULL, 0, NULL, NULL, '2025-04-01 21:31:49', '2025-04-01 21:31:49', NULL);
INSERT INTO `t_package` VALUES (42, 1, 1, 'TEST000006', '张三', '13900001111', '山东省青岛市市北区辽宁路128号', '顺丰快递', '95338', '广东省深圳市', 0.80, 5, '已签收测试包裹', '2025-03-31 21:31:49', NULL, 0, NULL, NULL, '2025-03-30 21:31:49', '2025-04-01 21:31:49', NULL);

-- ----------------------------
-- Table structure for t_package_tracking
-- ----------------------------
DROP TABLE IF EXISTS `t_package_tracking`;
CREATE TABLE `t_package_tracking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `package_id` bigint NOT NULL COMMENT '包裹ID',
  `tracking_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '物流信息',
  `tracking_time` datetime NOT NULL COMMENT '物流时间',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前位置',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_package_id`(`package_id` ASC) USING BTREE,
  CONSTRAINT `fk_tracking_package` FOREIGN KEY (`package_id`) REFERENCES `t_package` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '包裹物流记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_package_tracking
-- ----------------------------

-- ----------------------------
-- Table structure for t_payment
-- ----------------------------
DROP TABLE IF EXISTS `t_payment`;
CREATE TABLE `t_payment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `payment_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付流水号',
  `channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付渠道',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0-未支付,1-已支付,2-已退款)',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_payment_no`(`payment_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `t_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_payment
-- ----------------------------
INSERT INTO `t_payment` VALUES (1, 3, 1, 'PAY202303250001', '微信支付', 15.00, 1, '2023-03-25 10:20:30', '2023-03-25 10:15:30', '2023-03-25 10:20:30');
INSERT INTO `t_payment` VALUES (2, 4, 2, 'PAY202303260001', '支付宝', 8.00, 1, '2023-03-26 08:35:45', '2023-03-26 08:30:45', '2023-03-26 08:35:45');
INSERT INTO `t_payment` VALUES (3, 3, 3, 'PAY202303260002', '银行卡', 25.00, 1, '2023-03-26 11:45:18', '2023-03-26 11:42:18', '2023-03-26 11:45:18');
INSERT INTO `t_payment` VALUES (4, 1, 2, 'PAY202304050001', '微信支付', 15.00, 1, '2023-04-05 09:05:30', '2023-04-05 09:00:30', '2023-04-05 09:05:30');
INSERT INTO `t_payment` VALUES (5, 1, 3, 'PAY202304060001', '支付宝', 30.00, 1, '2023-04-06 13:45:20', '2023-04-06 13:40:20', '2023-04-06 13:45:20');
INSERT INTO `t_payment` VALUES (6, 1, 4, 'PAY202304070001', '微信支付', 10.00, 0, NULL, '2023-04-07 08:55:10', '2023-04-07 08:55:10');
INSERT INTO `t_payment` VALUES (7, 1, 18, 'PAY202504131119019757', 'wxpay', 15.00, 0, NULL, '2025-04-13 11:19:01', '2025-04-13 11:19:01');
INSERT INTO `t_payment` VALUES (8, 1, 18, 'PAY202504131119046707', 'wxpay', 15.00, 0, NULL, '2025-04-13 11:19:05', '2025-04-13 11:19:05');
INSERT INTO `t_payment` VALUES (9, 1, 18, 'PAY202504131119078996', 'wxpay', 15.00, 0, NULL, '2025-04-13 11:19:07', '2025-04-13 11:19:07');
INSERT INTO `t_payment` VALUES (10, 1, 21, 'PAY202504131145341478', 'wxpay', 15.00, 1, '2025-04-13 11:45:39', '2025-04-13 11:45:35', '2025-04-13 11:45:39');
INSERT INTO `t_payment` VALUES (11, 1, 22, 'PAY202504131150098449', 'wxpay', 15.00, 1, '2025-04-13 11:50:09', '2025-04-13 11:50:09', '2025-04-13 11:50:09');
INSERT INTO `t_payment` VALUES (12, 1, 23, 'PAY202504131445153293', 'wxpay', 15.00, 1, '2025-04-13 14:45:16', '2025-04-13 14:45:16', '2025-04-13 14:45:16');

-- ----------------------------
-- Table structure for t_station
-- ----------------------------
DROP TABLE IF EXISTS `t_station`;
CREATE TABLE `t_station`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务点ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务点名称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务点LOGO',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `wechat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信联系号',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区县',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `business_start_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '08:00' COMMENT '营业开始时间',
  `business_end_time` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '21:00' COMMENT '营业结束时间',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '评分',
  `review_count` int NULL DEFAULT 0 COMMENT '评价数量',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0-正常,1-暂停营业,2-永久关闭)',
  `manager` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `manager_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人电话',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `business_hours` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `manager_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务点负责人姓名',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_location`(`province` ASC, `city` ASC, `district` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_station
-- ----------------------------
-- 服务点表需要先删除可能存在的记录以避免主键冲突
DELETE FROM `t_station_review` WHERE `station_id` IN (4, 5, 6);
DELETE FROM `t_station_photo` WHERE `station_id` IN (4, 5, 6);
DELETE FROM `t_station_company` WHERE `station_id` IN (4, 5, 6);
DELETE FROM `t_package` WHERE `station_id` IN (4, 5, 6);
DELETE FROM `t_station` WHERE `id` IN (4, 5, 6);

INSERT INTO `t_station` VALUES (4, '即墨区农村物流服务点1', 'https://example.com/station1_logo.png', '0532-86123456', 'wx123456', '山东省', '青岛市', '即墨区', '龙泉街道社区服务中心旁', 120.447162, 36.389401, '08:00', '20:00', 4.7, 52, 0, NULL, '13900138001', '2025-04-01 21:13:49', '2025-04-01 21:13:49', '08:00-20:00', '刘站长');
INSERT INTO `t_station` VALUES (5, '即墨区农村物流服务点2', 'https://example.com/station2_logo.png', '0532-86123457', 'wx123457', '山东省', '青岛市', '即墨区', '通济街道文化服务中心对面', 120.463770, 36.379712, '08:30', '19:30', 4.5, 38, 0, NULL, '13900138002', '2025-04-01 21:13:49', '2025-04-01 21:13:49', '08:30-19:30', '陈站长');
INSERT INTO `t_station` VALUES (6, '即墨区农村物流服务点3', 'https://example.com/station3_logo.png', '0532-86123458', 'wx123458', '山东省', '青岛市', '即墨区', '蓝村镇政府东100米', 120.414181, 36.355601, '08:00', '19:00', 4.8, 45, 0, NULL, '13900138003', '2025-04-01 21:13:49', '2025-04-01 21:13:49', '08:00-19:00', '孙站长');

-- ----------------------------
-- Table structure for t_station_company
-- ----------------------------
DROP TABLE IF EXISTS `t_station_company`;
CREATE TABLE `t_station_company`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `station_id` bigint NOT NULL COMMENT '服务点ID',
  `company_id` bigint NOT NULL COMMENT '快递公司ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_station_company`(`station_id` ASC, `company_id` ASC) USING BTREE,
  INDEX `fk_sc_company`(`company_id` ASC) USING BTREE,
  CONSTRAINT `fk_sc_company` FOREIGN KEY (`company_id`) REFERENCES `t_express_company` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sc_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务点支持的快递公司关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_station_company
-- ----------------------------

-- ----------------------------
-- Table structure for t_station_photo
-- ----------------------------
DROP TABLE IF EXISTS `t_station_photo`;
CREATE TABLE `t_station_photo`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `station_id` bigint NOT NULL COMMENT '服务点ID',
  `photo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_station_id`(`station_id` ASC) USING BTREE,
  CONSTRAINT `fk_photo_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务点图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_station_photo
-- ----------------------------

-- ----------------------------
-- Table structure for t_station_review
-- ----------------------------
DROP TABLE IF EXISTS `t_station_review`;
CREATE TABLE `t_station_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `station_id` bigint NOT NULL COMMENT '服务点ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` tinyint(1) NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `is_anonymous` tinyint(1) NULL DEFAULT 0 COMMENT '是否匿名(0-否,1-是)',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_station_id`(`station_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_review_station` FOREIGN KEY (`station_id`) REFERENCES `t_station` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务点评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_station_review
-- ----------------------------

-- ----------------------------
-- Table structure for t_system_settings
-- ----------------------------
DROP TABLE IF EXISTS `t_system_settings`;
CREATE TABLE `t_system_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `setting_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设置键',
  `setting_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '设置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设置描述',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_setting_key`(`setting_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_system_settings
-- ----------------------------
INSERT INTO `t_system_settings` VALUES (1, 'siteName', '\"乡村速递\"', '系统名称', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (2, 'siteLogo', '\"/static/images/logo.png\"', '系统LOGO', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (3, 'servicePhone', '\"400-123-4567\"', '客服电话', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (4, 'enableRegister', '1', '开放注册', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (5, 'enableCaptcha', '1', '验证码', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (6, 'enableSmsNotify', '1', '短信通知', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (7, 'enableEmailNotify', '0', '邮件通知', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (8, 'enableAppNotify', '1', '应用内通知', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (9, 'maintenanceMode', '0', '系统维护模式', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (10, 'maintenanceMessage', '\"系统正在维护中，请稍后再试...\"', '维护说明', '2025-03-30 17:53:42', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (11, 'icp', '\"\"', 'icp', '2025-03-30 18:50:37', '2025-03-30 18:50:42');
INSERT INTO `t_system_settings` VALUES (12, 'site_name', '\"乡递通\"', '网站名称', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (13, 'site_logo', '\"https://example.com/logo.png\"', '网站Logo', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (14, 'service_phone', '\"400-123-4567\"', '客服电话', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (15, 'small_package_price', '10', '小件配送基础价格', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (16, 'medium_package_price', '15', '中件配送基础价格', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (17, 'large_package_price', '25', '大件配送基础价格', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (18, 'distance_price_rate', '0.5', '距离加价比例（元/公里）', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (19, 'courier_commission_rate', '0.8', '快递员佣金比例', '2025-04-01 21:17:06', '2025-04-01 21:17:06');
INSERT INTO `t_system_settings` VALUES (20, 'platform_commission_rate', '0.2', '平台佣金比例', '2025-04-01 21:17:06', '2025-04-01 21:17:06');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别(0-未知,1-男,2-女)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `verified` tinyint(1) NULL DEFAULT 0 COMMENT '是否实名认证(0-否,1-是)',
  `user_type` tinyint(1) NULL DEFAULT 0 COMMENT '用户类型(0-普通用户,1-快递员,2-管理员)',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0-正常,1-禁用)',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户简介',
  `birthday` date NULL DEFAULT NULL COMMENT '用户生日',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, '15579961188', '$2a$10$6ZYpcR2UHIf/YBkLzVePjub07o0U9PP1iEFfzDGS0vC9N02BmysnG', '用户11881', NULL, 0, NULL, NULL, 0, 2, 0, '2025-03-24 06:29:42', '2025-04-13 14:22:56', '', NULL);
INSERT INTO `t_user` VALUES (3, '13800138001', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'https://cdn.ruralexpress.cn/avatars/user1.jpg', 1, '张三', '加密的身份证号', 1, 0, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (4, '13800138002', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'https://cdn.ruralexpress.cn/avatars/user2.jpg', 1, '李四', '加密的身份证号', 1, 0, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (5, '13800138003', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'https://cdn.ruralexpress.cn/avatars/user3.jpg', 1, '王五', '加密的身份证号', 1, 0, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (6, '13800138004', 'e10adc3949ba59abbe56e057f20f883e', '赵六', 'https://cdn.ruralexpress.cn/avatars/user4.jpg', 1, '赵六', '加密的身份证号', 1, 1, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (7, '13800138005', 'e10adc3949ba59abbe56e057f20f883e', '钱七', 'https://cdn.ruralexpress.cn/avatars/user5.jpg', 1, '钱七', '加密的身份证号', 1, 1, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (8, '13800138006', 'e10adc3949ba59abbe56e057f20f883e', '孙八', 'https://cdn.ruralexpress.cn/avatars/user6.jpg', 2, '孙八', '加密的身份证号', 1, 1, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (9, '13800138007', 'e10adc3949ba59abbe56e057f20f883e', '周九', 'https://cdn.ruralexpress.cn/avatars/user7.jpg', 2, '周九', '加密的身份证号', 1, 0, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (10, '13800138008', 'e10adc3949ba59abbe56e057f20f883e', '吴十', 'https://cdn.ruralexpress.cn/avatars/user8.jpg', 1, '吴十', '加密的身份证号', 1, 0, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (11, '13800138009', 'e10adc3949ba59abbe56e057f20f883e', '郑十一', 'https://cdn.ruralexpress.cn/avatars/user9.jpg', 1, '郑十一', '加密的身份证号', 1, 2, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (12, '13800138010', 'e10adc3949ba59abbe56e057f20f883e', '王十二', 'https://cdn.ruralexpress.cn/avatars/user10.jpg', 2, '王十二', '加密的身份证号', 1, 0, 0, '2025-03-29 10:42:09', '2025-03-29 10:42:09', NULL, NULL);
INSERT INTO `t_user` VALUES (33, '13800138000', '$2a$10$uxuOPJR9vUvK6uAJFVvYMOQlkqH1eqSBSOQ9VVMx9Cx/VLbAVIczS', '测试用户', '/static/images/default-avatar.png', 0, '张三', NULL, 0, 0, 0, '2025-03-30 20:30:02', '2025-03-30 20:30:02', NULL, NULL);
INSERT INTO `t_user` VALUES (34, '13811138001', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '李大壮', 'https://example.com/avatar1.jpg', 1, '李大壮', '360300199001010001', 1, 0, 0, '2025-04-01 21:12:46', '2025-04-01 21:12:46', NULL, NULL);
INSERT INTO `t_user` VALUES (35, '13811138002', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '张小花', 'https://example.com/avatar2.jpg', 2, '张小花', '360300199001010002', 1, 0, 0, '2025-04-01 21:12:46', '2025-04-01 21:12:46', NULL, NULL);
INSERT INTO `t_user` VALUES (36, '13811138003', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '王快递', 'https://example.com/avatar3.jpg', 1, '王快递', '360300199001010003', 1, 1, 0, '2025-04-01 21:12:46', '2025-04-01 21:12:46', NULL, NULL);
INSERT INTO `t_user` VALUES (37, '13811138004', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '赵配送', 'https://example.com/avatar4.jpg', 1, '赵配送', '360300199001010004', 1, 1, 0, '2025-04-01 21:12:46', '2025-04-01 21:12:46', NULL, NULL);
INSERT INTO `t_user` VALUES (38, '13811138005', '$2a$10$xPPoZIxBpXkCTj3WyBe/7.SrC3EYCBGgVQxv0Qv0rXFQpwwmbRSVi', '管理员', 'https://example.com/avatar5.jpg', 1, '郑管理', '360300199001010005', 1, 2, 0, '2025-04-01 21:12:46', '2025-04-01 21:12:46', NULL, NULL);
INSERT INTO `t_user` VALUES (42, '15579961100', '123123', '夏', NULL, 0, '夏', NULL, 0, 1, 0, '2025-04-13 12:32:04', '2025-04-13 12:32:04', NULL, NULL);
INSERT INTO `t_user` VALUES (43, '15579961101', '$2a$10$O7nZ11//YavRYgx8Mbq4vOiGwJYiJSicM84HROSnUKAlI5JdOduEa', '测试', NULL, 0, '测试', NULL, 0, 0, 1, '2025-04-13 14:29:40', '2025-04-13 14:32:21', NULL, NULL);
INSERT INTO `t_user` VALUES (44, '13879961188', '$2a$10$lvut1.IDWfUgYtsTJKg/peYwVM7h9sbR9JSGG/usIde3VQrWpMKzq', '用户1188', NULL, 0, NULL, NULL, 0, 0, 0, '2025-04-15 01:37:10', '2025-04-15 01:37:10', NULL, NULL);
INSERT INTO `t_user` VALUES (45, '17898451544', '$2a$10$gHpDL0.IzbrG3Nl4E7j8U.Ai0aWwwO8pt44MPqG/PnshkB0VnZ3QW', '用户1544', NULL, 0, NULL, NULL, 0, 0, 0, '2025-04-15 01:43:26', '2025-04-15 01:43:26', NULL, NULL);

-- ----------------------------
-- Table structure for t_user_verification
-- ----------------------------
DROP TABLE IF EXISTS `t_user_verification`;
CREATE TABLE `t_user_verification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `front_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证正面照片URL',
  `back_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证反面照片URL',
  `holding_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手持身份证照片URL',
  `verify_status` tinyint(1) NULL DEFAULT 0 COMMENT '审核状态(0-待审核,1-已通过,2-未通过)',
  `verify_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `verify_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_verification_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户实名认证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_verification
-- ----------------------------

-- ----------------------------
-- Table structure for t_review
-- ----------------------------
DROP TABLE IF EXISTS `t_review`;
CREATE TABLE `t_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `courier_id` bigint NOT NULL COMMENT '快递员ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `rating` int(1) NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '快递员回复',
  `anonymous` tinyint(1) NULL DEFAULT 0 COMMENT '是否匿名(0-否,1-是)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_courier_id`(`courier_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_review
-- ----------------------------
INSERT INTO `t_review` VALUES (1, 1, 2, 1, 5, '快递员服务很好，送货速度快', '感谢您的好评，我会继续努力', 0, '2025-04-15 10:00:00', '2025-04-15 10:30:00');
INSERT INTO `t_review` VALUES (2, 2, 2, 2, 4, '服务态度不错，但送货稍有延迟', '非常抱歉送货有延迟，下次会更加注意时间', 0, '2025-04-15 11:00:00', '2025-04-15 11:30:00');
INSERT INTO `t_review` VALUES (3, 3, 5, 3, 5, '快递员非常热情，服务很周到', NULL, 1, '2025-04-15 12:00:00', '2025-04-15 12:00:00');
INSERT INTO `t_review` VALUES (4, 4, 5, 4, 3, '送货速度可以再快一些', NULL, 0, '2025-04-15 13:00:00', '2025-04-15 13:00:00');

SET FOREIGN_KEY_CHECKS = 1;
