# 乡递通(RuralExpress)技术栈

## 前端技术栈

### 基础框架
- **UniApp**: 跨平台前端框架，一套代码可发布到iOS、Android、Web等多个平台
- **Vue.js 3**: 渐进式JavaScript框架
- **TypeScript**: JavaScript的超集，提供类型检查

### UI组件库
- **uView UI**: 专为uniapp打造的UI框架
- **微信小程序组件**: 微信官方提供的小程序组件

### 状态管理
- **Pinia**: Vue.js的状态管理库，Vue 3的官方推荐

### 地图服务
- **腾讯地图API**: 提供位置服务、路线规划等功能

### HTTP请求
- **Axios**: 基于Promise的HTTP客户端
- **uni.request**: UniApp提供的网络请求API

## 后端技术栈

### 基础框架
- **Spring Boot 2.7.x**: 简化Spring应用开发的框架
- **Spring Security**: 安全框架，处理认证和授权
- **Spring Data JPA**: 简化数据库访问的框架

### 数据库
- **MySQL 8.0**: 关系型数据库
- **Redis**: 缓存和会话管理

### API文档
- **Swagger/OpenAPI 3.0**: API文档生成工具
- **Knife4j**: 基于Swagger的增强UI界面

### 支付集成
- **微信支付SDK**: 微信支付接口

### 消息队列
- **RocketMQ**: 分布式消息队列系统，处理异步任务

### 存储服务
- **阿里云OSS**: 对象存储服务，存储图片等静态资源

## 开发工具

### 前端开发
- **HBuilderX**: UniApp官方推荐IDE
- **微信开发者工具**: 微信小程序开发和调试工具
- **VS Code**: 轻量级代码编辑器

### 后端开发
- **IntelliJ IDEA**: Java开发IDE
- **Maven**: 项目构建和依赖管理工具

### 数据库工具
- **Navicat/DBeaver**: 数据库管理工具

### 版本控制
- **Git**: 分布式版本控制系统
- **GitHub/GitLab**: 代码托管平台

## 部署环境

### 容器化
- **Docker**: 应用容器引擎
- **Docker Compose**: 定义和运行多容器Docker应用

### 服务器
- **阿里云ECS**: 云服务器
- **Nginx**: Web服务器，用于反向代理和负载均衡

### CI/CD
- **Jenkins/GitHub Actions**: 持续集成/持续部署工具

## 监控与日志
- **Spring Boot Actuator**: 监控Spring Boot应用
- **ELK Stack**: Elasticsearch、Logstash和Kibana，用于日志管理
- **Prometheus + Grafana**: 系统监控和可视化 