
1. 用户管理模块

    主要功能
- 用户注册与登录
- 用户信息管理
- 用户认证
- 密码修改
- 手机号变更

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/UserController.java`
- 控制器：`backend/src/main/java/com/ruralexpress/controller/AuthController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/UserService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/UserServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/User.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/UserDto.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/LoginDto.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/PasswordChangeDto.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/PhoneChangeDto.java`

   2. 快递员管理模块

    主要功能
- 快递员申请
- 快递员信息管理
- 快递员审核
- 接单管理
- 快递员标签管理

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/CourierController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/CourierService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/CourierServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Courier.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/CourierTag.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/CourierDTO.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/CourierApplyDto.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/CourierUpdateDto.java`

   3. 订单管理模块

    主要功能
- 订单创建
- 订单查询
- 订单状态管理
- 订单取消
- 快递员接单

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/OrderController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/OrderService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/OrderServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Order.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/OrderCreateDto.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/OrderUpdateDto.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/OrderFilterDto.java`

   4. 支付管理模块

    主要功能
- 订单支付
- 支付记录管理
- 退款处理

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/PaymentController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/PaymentService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/PaymentServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Payment.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/PaymentRequestDto.java`

   5. 地址管理模块

    主要功能
- 用户地址添加
- 地址修改
- 地址删除
- 默认地址设置

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/AddressController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/AddressService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/AddressServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Address.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/AddressDTO.java`

   6. 物流管理模块

    主要功能
- 包裹信息管理
- 物流状态跟踪
- 物流公司管理

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/LogisticsController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/LogisticsService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/LogisticsServiceImpl.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/PackageService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/PackageServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Package.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/ExpressCompany.java`

   7. 站点管理模块

    主要功能
- 站点信息管理
- 站点查询
- 站点统计

    相关文件
- 服务接口：`backend/src/main/java/com/ruralexpress/service/StationService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/StationServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Station.java`

   8. 系统通知模块

    主要功能
- 系统公告管理
- 用户通知

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/NoticeController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/NoticeService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/NoticeServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Notice.java`

   9. 文件管理模块

    主要功能
- 文件上传
- 图片处理

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/FileController.java`
- 控制器：`backend/src/main/java/com/ruralexpress/controller/UploadController.java`

   10. 系统安全模块

    主要功能
- 用户认证
- JWT鉴权
- 安全过滤

    相关文件
- 配置：`backend/src/main/java/com/ruralexpress/config/SecurityConfig.java`
- 配置：`backend/src/main/java/com/ruralexpress/config/JwtAuthenticationFilter.java`
- 配置：`backend/src/main/java/com/ruralexpress/config/AppConfig.java`

   11. 首页与搜索模块

    主要功能
- 首页数据展示
- 订单搜索
- Banner管理

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/HomeController.java`
- 控制器：`backend/src/main/java/com/ruralexpress/controller/SearchController.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Banner.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/BannerService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/BannerServiceImpl.java`

   12. 公共基础模块

    主要功能
- 分页查询
- 异常处理
- API响应封装

    相关文件
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/PageQuery.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/PageResult.java`
- 工具类：`backend/src/main/java/com/ruralexpress/utils/ApiResult.java`
- 异常处理：`backend/src/main/java/com/ruralexpress/exception/BusinessException.java`

   13. 系统配置模块

    主要功能
- 系统参数配置
- 运行环境配置

    相关文件
- 实体类：`backend/src/main/java/com/ruralexpress/entity/SystemSettings.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/SystemSettingsService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/SystemSettingsServiceImpl.java`
- 配置文件：`backend/src/main/resources/application.yml`

*以下是补充模块划分*

14. 短信服务模块

    主要功能
- 短信验证码发送
- 短信通知

    相关文件
- 服务接口：`backend/src/main/java/com/ruralexpress/service/SmsService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/SmsServiceImpl.java`

   15. 公司管理模块

    主要功能
- 快递公司信息管理
- 公司资质审核

    相关文件
- 服务接口：`backend/src/main/java/com/ruralexpress/service/CompanyService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/CompanyServiceImpl.java`
- 实体类：`backend/src/main/java/com/ruralexpress/entity/Company.java`
- 数据传输对象：`backend/src/main/java/com/ruralexpress/dto/company/`目录下的相关DTO

   16. 数据统计模块

    主要功能
- 订单统计
- 用户统计
- 快递员统计
- 收入统计

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/admin/StatisticsController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/StatisticsService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/StatisticsServiceImpl.java`

   17. 管理员模块

    主要功能
- 系统管理
- 用户管理
- 订单管理
- 数据统计

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/admin/AdminController.java`
- 控制器：`backend/src/main/java/com/ruralexpress/controller/admin/UserManageController.java`
- 控制器：`backend/src/main/java/com/ruralexpress/controller/admin/OrderManageController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/AdminService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/AdminServiceImpl.java`

   18. 缓存管理模块

    主要功能
- Redis缓存管理
- 缓存策略配置

    相关文件
- 配置：`backend/src/main/resources/application.yml`中的Redis配置部分
- 工具类：`backend/src/main/java/com/ruralexpress/utils/RedisUtils.java`

   19. 数据库管理模块

    主要功能
- 数据库初始化
- 数据迁移
- 数据库备份

    相关文件
- 初始化脚本：`backend/src/main/resources/db/init.sql`
- MyBatis映射文件：`backend/src/main/resources/mapper/`目录下的XML文件

   20. 日志管理模块

    主要功能
- 操作日志记录
- 异常日志记录
- 系统运行日志

    相关文件
- 配置：`backend/src/main/resources/application.yml`中的日志配置部分
- 工具类：`backend/src/main/java/com/ruralexpress/utils/LogUtils.java`

   21. 定时任务模块

    主要功能
- 订单超时处理
- 数据清理
- 统计报表生成

    相关文件
- 定时任务类：`backend/src/main/java/com/ruralexpress/task/OrderTimeoutTask.java`
- 定时任务类：`backend/src/main/java/com/ruralexpress/task/DataCleanTask.java`
- 定时任务类：`backend/src/main/java/com/ruralexpress/task/ReportGenerateTask.java`

   22. 接口文档模块

    主要功能
- API文档生成
- 接口测试

    相关文件
- 配置：`backend/src/main/java/com/ruralexpress/config/Knife4jConfig.java`
- 接口文档注解：各Controller类中的Swagger注解

   23. 测试模块

    主要功能
- 单元测试
- 集成测试
- 接口测试

    相关文件
- 测试类：`backend/src/test/java/com/ruralexpress/`目录下的测试类
- 测试配置：`backend/src/test/resources/`目录下的测试配置文件

   24. 工具类模块

    主要功能
- 通用工具方法
- 数据转换
- 验证工具

    相关文件
- 工具类：`backend/src/main/java/com/ruralexpress/utils/`目录下的工具类
- 常量类：`backend/src/main/java/com/ruralexpress/constant/`目录下的常量类

   25. 验证码模块

    主要功能
- 图形验证码生成
- 验证码校验

    相关文件
- 控制器：`backend/src/main/java/com/ruralexpress/controller/CaptchaController.java`
- 服务接口：`backend/src/main/java/com/ruralexpress/service/CaptchaService.java`
- 服务实现：`backend/src/main/java/com/ruralexpress/service/impl/CaptchaServiceImpl.java`

