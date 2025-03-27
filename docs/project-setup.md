# 乡递通(RuralExpress)项目设置

## 开发环境配置

### 前端开发环境
1. 安装Node.js：https://nodejs.org/zh-cn/ (推荐v16或更高版本)
2. 安装HBuilderX：https://www.dcloud.io/hbuilderx.html (用于UniApp开发)
3. 安装微信开发者工具：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html

### 后端开发环境
1. 安装JDK 11或更高版本：https://www.oracle.com/java/technologies/javase-downloads.html
2. 安装Maven：https://maven.apache.org/download.cgi
3. 安装MySQL 8.0：https://dev.mysql.com/downloads/mysql/
4. 安装Redis：https://redis.io/download
5. 安装IDE，推荐IntelliJ IDEA：https://www.jetbrains.com/idea/download/

### Docker环境(可选，用于后期部署)
1. 安装Docker：https://www.docker.com/products/docker-desktop
2. 安装Docker Compose：https://docs.docker.com/compose/install/

## 项目结构

```
RuralExpress/
├── frontend/             # 前端代码
│   ├── components/       # 公共组件
│   ├── pages/            # 页面
│   ├── static/           # 静态资源
│   ├── utils/            # 工具函数
│   └── App.vue           # 应用入口
│
├── backend/              # 后端代码
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/     # Java代码
│   │   │   └── resources/# 配置文件
│   │   └── test/         # 测试代码
│   └── pom.xml           # Maven配置
│
└── docs/                 # 项目文档
```

## 数据库设计

主要数据表设计请参考README中的"四、数据库设计"部分。

## API接口规范

遵循RESTful API设计规范:
- GET: 获取资源
- POST: 创建资源
- PUT: 更新资源
- DELETE: 删除资源

响应格式统一为JSON:
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
``` 