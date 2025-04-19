# 乡递通(RuralExpress)待办事项清单

## 前端页面开发

### 用户端页面
- pages/search/search ✅
- pages/delivery/send ✅
- pages/delivery/receive ✅
- pages/order/track ✅
- pages/courier/recruitment ✅
- pages/notice/detail ✅
- pages/courier/list ✅
- pages/courier/detail ✅
- pages/user/profile ⬜
- pages/user/wallet ⬜
- pages/user/settings ⬜
- pages/address/management ⬜
- pages/order/detail ⬜
- pages/payment/checkout ⬜
- pages/payment/result ⬜
- pages/evaluation/submit ⬜

### 快递员端页面
- pages/courier/dashboard ⬜
- pages/courier/task-list ⬜
- pages/courier/order-detail ⬜
- pages/courier/earnings ⬜
- pages/courier/schedule ⬜
- pages/courier/route-planning ⬜

### 管理员端页面
- pages/admin/login ⬜
- pages/admin/dashboard ⬜
- pages/admin/orders/index ✅
- pages/admin/orders/detail ⬜
- pages/admin/users/index ⬜
- pages/admin/users/detail ⬜
- pages/admin/couriers/index ⬜
- pages/admin/couriers/detail ⬜
- pages/admin/couriers/audit ⬜
- pages/admin/stations/index ⬜
- pages/admin/stations/form ⬜
- pages/admin/statistics/orders ⬜
- pages/admin/statistics/users ⬜
- pages/admin/statistics/finance ⬜
- pages/admin/system/settings ⬜
- pages/admin/system/logs ⬜

## 后端API开发

### 用户相关API
- 用户注册/登录 ✅
- 用户信息管理 ✅
- 用户地址管理 ✅
- 用户实名认证 ⬜
- 用户头像上传 ⬜
- 用户密码重置 ⬜

### 订单相关API
- 创建订单 ✅
- 查询订单列表 ✅
- 查询订单详情 ✅
- 取消订单 ✅
- 更新订单状态 ✅
- 订单评价 ⬜
- 订单搜索 ✅
- 订单导出 ⬜
- 批量操作订单 ⬜

### 快递员相关API
- 快递员申请 ✅
- 快递员资料更新 ✅
- 接单管理 ✅
- 快递员位置更新 ⬜
- 快递员工作状态管理 ✅
- 快递员收益查询 ⬜
- 快递员工作记录 ⬜

### 支付相关API
- 创建支付订单 ⬜
- 支付结果回调 ⬜
- 支付记录查询 ⬜
- 退款申请 ⬜
- 退款状态查询 ⬜

### 管理员相关API
- 管理员登录 ✅
- 用户管理 ⬜
- 订单管理 ✅
- 快递员管理 ⬜
- 快递员审核 ⬜
- 服务点管理 ⬜
- 系统配置管理 ⬜
- 数据统计 ⬜
- 系统日志查询 ⬜


## 测试与部署

- 单元测试编写 ⬜
- API测试用例 ⬜

## 文档完善

- API文档更新 ⬜
- 数据库文档更新 ⬜
- 部署文档编写 ⬜
- 使用手册编写 ⬜


本课题要求实现以下功能模块:
用户管理模块
(包括用户注册、登录、个人信息管理、实名认证等)

兼职快递员管理模块(包括兼职快递员招募、资格审核、任务分配、收益结算等功能,提升配送效率和服务质量):

快递订单管理模块(包括订单创建、订单查询、订单状态更新、异常处理等功能,保障订单处理的准确性和及时性）

评价反馈模块
(包括用户评价、互评、系统评价等功能,构建良好的服务生态):

数据分析与报表模块
（对用户现进行分析，生成可视化报表，为平台运营提供决策支持)