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
| receiver_name | VARCHAR(50) | 收件人姓名 | 非空 |
| receiver_phone | VARCHAR(20) | 收件人手机号 | 非空 |
| receiver_address | VARCHAR(255) | 收件地址 | 非空 |
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