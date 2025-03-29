# 乡递通(RuralExpress)数据库设计

## 用户表 (t_user)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT | 用户ID | 主键, 自增 |
| phone | VARCHAR(20) | 手机号 | 唯一, 非空 |
| password | VARCHAR(100) | 密码(加密存储) | 非空 |
| nickname | VARCHAR(50) | 昵称 | 可空 |
| avatar | VARCHAR(255) | 头像URL | 可空 |
| gender | TINYINT(1) | 性别(0-未知,1-男,2-女) | 默认0 |
| real_name | VARCHAR(50) | 真实姓名 | 可空 |
| id_card | VARCHAR(18) | 身份证号(加密存储) | 可空 |
| verified | TINYINT(1) | 是否实名认证(0-否,1-是) | 默认0 |
| user_type | TINYINT(1) | 用户类型(0-普通用户,1-快递员,2-管理员) | 默认0 |
| status | TINYINT(1) | 状态(0-正常,1-禁用) | 默认0 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 地址表 (t_address)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 地址ID | 主键, 自增 |
| user_id | BIGINT(20) | 用户ID | 外键, 非空 |
| name | VARCHAR(50) | 收货人姓名 | 非空 |
| phone | VARCHAR(20) | 收货人手机号 | 非空 |
| province | VARCHAR(50) | 省份 | 非空 |
| city | VARCHAR(50) | 城市 | 非空 |
| district | VARCHAR(50) | 区县 | 非空 |
| street | VARCHAR(50) | 街道 | 可空 |
| detail | VARCHAR(255) | 详细地址 | 非空 |
| is_default | TINYINT(1) | 是否默认地址(0-否,1-是) | 默认0 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 快递员表 (t_courier)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 快递员ID | 主键, 自增 |
| user_id | BIGINT(20) | 关联用户ID | 外键, 非空 |
| service_area | VARCHAR(255) | 服务区域 | 非空 |
| service_status | TINYINT(1) | 服务状态(0-休息,1-接单中) | 默认0 |
| audit_status | TINYINT(1) | 审核状态(0-待审核,1-已通过,2-未通过) | 默认0 |
| rating | DECIMAL(2,1) | 评分 | 默认5.0 |
| balance | DECIMAL(10,2) | 账户余额 | 默认0 |
| completed_orders | INT | 已完成订单数 | 默认0 |
| id_card_front | VARCHAR(255) | 身份证正面照片URL | 非空 |
| id_card_back | VARCHAR(255) | 身份证背面照片URL | 非空 |
| work_start_time | VARCHAR(20) | 工作开始时间 | 可空 |
| work_end_time | VARCHAR(20) | 工作结束时间 | 可空 |
| vehicle | VARCHAR(50) | 交通工具 | 可空 |
| introduction | VARCHAR(500) | 简介 | 可空 |
| response_time | INT | 平均响应时间(分钟) | 可空 |
| longitude | DECIMAL(10,6) | 经度 | 可空 |
| latitude | DECIMAL(10,6) | 纬度 | 可空 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 订单表 (t_order)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 订单ID | 主键, 自增 |
| order_no | VARCHAR(30) | 订单编号 | 唯一, 非空 |
| user_id | BIGINT(20) | 用户ID | 外键, 非空 |
| courier_id | BIGINT(20) | 快递员ID | 外键, 可空 |
| sender_name | VARCHAR(50) | 寄件人姓名 | 非空 |
| sender_phone | VARCHAR(20) | 寄件人手机号 | 非空 |
| sender_address | VARCHAR(255) | 寄件地址 | 非空 |
| sender_longitude | DOUBLE | 寄件地址经度 | 可空 |
| sender_latitude | DOUBLE | 寄件地址纬度 | 可空 |
| receiver_name | VARCHAR(50) | 收件人姓名 | 非空 |
| receiver_phone | VARCHAR(20) | 收件人手机号 | 非空 |
| receiver_address | VARCHAR(255) | 收件地址 | 非空 |
| receiver_longitude | DOUBLE | 收件地址经度 | 可空 |
| receiver_latitude | DOUBLE | 收件地址纬度 | 可空 |
| package_type | TINYINT(1) | 包裹类型(0-小件,1-中件,2-大件) | 非空 |
| weight | DECIMAL(10,2) | 重量(kg) | 可空 |
| price | DECIMAL(10,2) | 配送费 | 非空 |
| status | TINYINT(1) | 订单状态(0-待接单,1-已接单,2-取件中,3-已取件,4-配送中,5-已送达,6-已完成,7-已取消) | 默认0 |
| expected_pickup_time | DATETIME | 期望取件时间 | 可空 |
| actual_pickup_time | DATETIME | 实际取件时间 | 可空 |
| expected_delivery_time | DATETIME | 期望送达时间 | 可空 |
| actual_delivery_time | DATETIME | 实际送达时间 | 可空 |
| remark | VARCHAR(255) | 备注 | 可空 |
| cancel_reason | VARCHAR(255) | 取消原因 | 可空 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 订单评价表 (t_evaluation)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 评价ID | 主键, 自增 |
| order_id | BIGINT(20) | 订单ID | 外键, 非空 |
| user_id | BIGINT(20) | 用户ID | 外键, 非空 |
| courier_id | BIGINT(20) | 快递员ID | 外键, 非空 |
| score | TINYINT(1) | 评分(1-5) | 非空 |
| content | VARCHAR(500) | 评价内容 | 可空 |
| user_to_courier | TINYINT(1) | 评价方向(0-用户评价快递员,1-快递员评价用户) | 默认0 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 支付记录表 (t_payment)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 支付记录ID | 主键, 自增 |
| user_id | BIGINT(20) | 用户ID | 外键, 非空 |
| order_id | BIGINT(20) | 订单ID | 外键, 非空 |
| payment_no | VARCHAR(64) | 支付流水号 | 唯一, 非空 |
| channel | VARCHAR(20) | 支付渠道 | 非空 |
| amount | DECIMAL(10,2) | 支付金额 | 非空 |
| status | TINYINT(1) | 状态(0-未支付,1-已支付,2-已退款) | 默认0 |
| payment_time | DATETIME | 支付时间 | 可空 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 系统配置表 (t_config)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 配置ID | 主键, 自增 |
| config_key | VARCHAR(64) | 配置键 | 唯一, 非空 |
| config_value | VARCHAR(255) | 配置值 | 非空 |
| description | VARCHAR(255) | 配置描述 | 可空 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 服务点表 (t_station)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 服务点ID | 主键, 自增 |
| name | VARCHAR(100) | 服务点名称 | 非空 |
| logo | VARCHAR(255) | 服务点LOGO | 可空 |
| phone | VARCHAR(20) | 联系电话 | 非空 |
| wechat | VARCHAR(50) | 微信联系号 | 可空 |
| province | VARCHAR(50) | 省份 | 非空 |
| city | VARCHAR(50) | 城市 | 非空 |
| district | VARCHAR(50) | 区县 | 非空 |
| address | VARCHAR(255) | 详细地址 | 非空 |
| longitude | DECIMAL(10,6) | 经度 | 可空 |
| latitude | DECIMAL(10,6) | 纬度 | 可空 |
| business_start_time | VARCHAR(20) | 营业开始时间 | 可空 |
| business_end_time | VARCHAR(20) | 营业结束时间 | 可空 |
| business_hours | VARCHAR(50) | 营业时间 | 可空 |
| rating | DECIMAL(2,1) | 评分 | 默认5.0 |
| review_count | INT | 评价数量 | 默认0 |
| status | TINYINT(1) | 状态(0-正常,1-暂停营业,2-永久关闭) | 默认0 |
| manager_name | VARCHAR(50) | 管理员姓名 | 可空 |
| manager_phone | VARCHAR(20) | 管理员电话 | 可空 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 服务点照片表 (t_station_photo)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | 照片ID | 主键, 自增 |
| station_id | BIGINT(20) | 服务点ID | 外键, 非空 |
| photo_url | VARCHAR(255) | 照片URL | 非空 |
| sort | INT | 排序 | 非空 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 服务点支持的快递公司表 (t_station_company)

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT(20) | ID | 主键, 自增 |
| station_id | BIGINT(20) | 服务点ID | 外键, 非空 |
| company_id | BIGINT(20) | 快递公司ID | 外键, 非空 |
| created_at | DATETIME | 创建时间 | 非空 |
| updated_at | DATETIME | 更新时间 | 非空 |

## 搜索功能说明

系统提供了多种搜索接口，支持通过关键词搜索不同类型的数据：

### 包裹搜索 (searchPackages)

通过包裹编号、运单号、寄件人信息、收件人信息等关键词搜索包裹。

### 订单搜索 (searchOrders)

通过订单编号(order_no)、寄件人信息(sender_name, sender_phone, sender_address)、收件人信息(receiver_name, receiver_phone, receiver_address)进行模糊搜索订单。

### 快递员搜索 (searchCouriers)

通过快递员关联的用户信息(nickname, real_name, phone)或服务区域(service_area)进行模糊搜索，按评分和完成订单数排序。

### 服务点搜索 (searchStations)

通过服务点名称(name)、位置信息(province, city, district, address)或联系电话(phone)进行模糊搜索，按评价数和评分排序。

## 表关系说明

### 用户相关表关系
1. `t_user` 和 `t_courier` 之间通过 `user_id` 字段关联，一个用户可以成为一名快递员
2. `t_user` 和 `t_address` 之间通过 `user_id` 字段关联，一个用户可以有多个地址

### 订单相关表关系
1. `t_order` 和 `t_user` 通过 `user_id` 字段关联，表示下单用户
2. `t_order` 和 `t_courier` 通过 `courier_id` 字段关联，表示接单快递员
3. `t_order` 和 `t_evaluation` 通过 `order_id` 字段关联，一个订单可以有多条评价记录
4. `t_order` 和 `t_payment` 通过 `order_id` 字段关联，一个订单对应一条支付记录

### 服务点相关表关系
1. `t_station` 和 `t_station_photo` 通过 `station_id` 字段关联，一个服务点可以有多张照片
2. `t_station` 和 `t_station_company` 通过 `station_id` 字段关联，一个服务点可以支持多个快递公司

## 开发注意事项

### 快递员信息获取
1. 快递员表(`t_courier`)不直接存储姓名等个人信息，这些信息存储在关联的用户表(`t_user`)中
2. 获取快递员姓名时，需要通过 `t_courier.user_id` 关联到 `t_user` 表，并使用 `t_user.nickname` 或 `t_user.real_name`
3. SQL查询示例:
   ```sql
   SELECT c.*, u.nickname, u.phone, u.avatar 
   FROM t_courier c 
   LEFT JOIN t_user u ON c.user_id = u.id 
   WHERE c.id = #{courierId}
   ```

### 订单查询优化
1. 订单查询时应使用表别名和列前缀，避免列名冲突
2. 对于关联查询，推荐使用以下方式:
   ```sql
   SELECT 
     o.id, o.order_no, /* 其他订单字段 */
     u.nickname as user_username,
     u2.nickname as courier_name
   FROM t_order o
   LEFT JOIN t_user u ON o.user_id = u.id
   LEFT JOIN t_courier c ON o.courier_id = c.id
   LEFT JOIN t_user u2 ON c.user_id = u2.id
   ```

### 数据索引建议
以下字段建议添加索引以提高查询性能:
1. `t_order.order_no`
2. `t_order.user_id`
3. `t_order.courier_id`
4. `t_order.status`
5. `t_order.created_at`
6. `t_courier.user_id`
7. `t_user.phone`

### 数据安全处理
1. 用户密码存储使用MD5+盐值加密
2. 身份证号等敏感信息需要加密存储
3. 支付信息需要符合金融安全要求

### 数据库字符集与排序规则
所有表使用 `utf8mb4` 字符集和 `utf8mb4_unicode_ci` 排序规则，以支持完整的Unicode字符集和Emoji 