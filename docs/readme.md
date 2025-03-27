# 乡递通(RuralExpress)

乡递通是一款专为解决乡村地区快递配送"最后一公里"问题设计的微信小程序。通过连接兼职快递员与用户，提升乡村快递服务的覆盖率和效率。

## 项目背景

随着电子商务在乡村地区的普及，快递需求不断增长，但由于地理分散、人口密度低等因素，传统快递配送模式在乡村地区面临着成本高、效率低的挑战。乡递通旨在通过共享经济模式，招募当地村民成为兼职快递员，解决这一难题。

## 功能特点

- **用户管理**：用户注册、登录、个人信息管理、实名认证
- **兼职快递员管理**：快递员申请与审核、任务接单、路线规划、收益结算
- **快递订单管理**：快递下单、订单追踪、异常处理
- **支付与结算**：集成微信支付、多种支付方式、安全交易保障
- **评价反馈**：用户评价、快递员互评、投诉处理
- **数据分析**：订单分析、用户行为分析、绩效评估

## 技术架构

- **前端**：UniApp + Vue.js 3 + TypeScript
- **后端**：Java Spring Boot
- **数据库**：MySQL + Redis
- **部署**：Docker容器化

## 项目文档

- [项目设置指南](./project-setup.md)
- [开发计划](./development-plan.md)
- [数据库设计](./database-design.md)
- [技术栈选型](./tech-stack.md)

## 开始使用

### 环境要求

- Node.js v16+
- JDK 11+
- MySQL 8.0
- Redis

### 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 后端启动

```bash
# 进入后端目录
cd backend

# 构建项目
mvn clean package

# 启动服务
java -jar target/rural-express.jar
```

## 贡献指南

欢迎贡献代码或提出建议！请遵循以下步骤：

1. Fork 项目
2. 创建特性分支：`git checkout -b feature/your-feature-name`
3. 提交更改：`git commit -m 'Add some feature'`
4. 推送分支：`git push origin feature/your-feature-name`
5. 提交Pull Request

## 许可证

[MIT License](./LICENSE)

## 联系我们

- 项目负责人：[联系方式]
- 技术支持：[联系方式]