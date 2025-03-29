# 乡递通(RuralExpress)项目运行指南

## 后端启动
后端项目是基于Spring Boot开发的服务器应用，需要配置好环境后启动：

### 环境准备
1. **安装JDK**：
   - 下载安装JDK 11或更高版本：https://www.oracle.com/java/technologies/javase-downloads.html
   - 配置JAVA_HOME环境变量

2. **安装Maven**：
   - 下载安装Maven：https://maven.apache.org/download.cgi
   - 配置M2_HOME环境变量和添加到PATH

3. **配置数据库**：
   - 安装MySQL 8.0：https://dev.mysql.com/downloads/mysql/
   - 创建数据库：
   ```sql
   CREATE DATABASE rural_express CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
   - 修改application.yml中的数据库连接信息（用户名和密码）

4. **安装Redis**：
   - 下载安装Redis：https://redis.io/download
   - Windows用户可使用：https://github.com/tporadowski/redis/releases
   - 启动Redis服务
   - 默认配置已设置为localhost:6379，如有不同请修改application.yml

### 启动方式
1. **使用IDE启动（推荐）**：
   - 使用IntelliJ IDEA打开后端项目目录
   - 等待Maven加载依赖
   - 运行RuralExpressApplication.java

2. **使用命令行启动**：
   - 进入项目的backend目录
   - 执行Maven命令构建项目：
     ```
     mvn clean package -DskipTests
     ```
   - 运行生成的JAR文件：
     ```
     java -jar target/rural-express-0.0.1-SNAPSHOT.jar
     ```

### 环境变量配置
可以通过环境变量覆盖默认配置：

```bash
# 数据库配置
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/rural_express?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
export SPRING_DATASOURCE_USERNAME=用户名
export SPRING_DATASOURCE_PASSWORD=密码

# Redis配置
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379
export SPRING_REDIS_PASSWORD=

# 服务器端口
export SERVER_PORT=8080

# JWT配置
export JWT_SECRET=你的密钥
export JWT_EXPIRATION=86400000
```

## 前端启动
前端项目基于UniApp开发，可以通过以下方式启动：

### 环境准备
1. **安装Node.js**：
   - 下载安装Node.js 16+：https://nodejs.org/
   - 验证安装：`node -v` 和 `npm -v`

2. **安装HBuilderX**：
   - 下载安装HBuilderX：https://www.dcloud.io/hbuilderx.html

3. **安装微信开发者工具**：
   - 下载安装微信开发者工具：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html
   - 在微信开发者工具中登录并配置小程序AppID

### 启动方式
1. **使用HBuilderX启动（推荐）**：
   - 使用HBuilderX打开前端项目目录
   - 在HBuilderX中点击"运行">"运行到小程序模拟器">"微信开发者工具"

2. **使用命令行启动**：
   - 进入项目的frontend目录
   - 安装依赖：
     ```
     npm install
     ```
   - 启动开发服务器：
     ```
     npm run dev:mp-weixin
     ```
   - 在微信开发者工具中打开项目的dist/dev/mp-weixin目录

### 前端开发配置
可以通过修改以下配置文件调整前端行为：

- `manifest.json`：应用配置信息
- `frontend/utils/config.js`：API地址等全局配置
- `frontend/pages.json`：页面和导航配置

## 访问应用
- 后端API服务：http://localhost:8080/api
- 前端：通过微信开发者工具预览

## 常见问题解决

### 数据库连接问题
- **错误**: `Connection refused`
  - **解决方案**: 检查MySQL服务是否启动，用户名和密码是否正确

- **错误**: `Unknown database 'rural_express'`
  - **解决方案**: 确认数据库是否已创建，执行上述SQL语句创建数据库

### Redis连接问题
- **错误**: `Connection refused`
  - **解决方案**: 检查Redis服务是否启动，端口是否正确

### JWT认证问题
- **错误**: `JWT token is expired`
  - **解决方案**: 重新登录获取新token，或延长token过期时间

### 数据表不存在问题
- **错误**: `Table 'rural_express.t_xxx' doesn't exist`
  - **解决方案**: 
    1. 检查application.yml中的`spring.jpa.hibernate.ddl-auto`配置是否为`update`或`create`
    2. 或手动执行`backend/src/main/resources/db`目录下的SQL初始化脚本

### 跨域问题
- **错误**: `Access-Control-Allow-Origin` 相关错误
  - **解决方案**: 
    1. 检查后端CORS配置是否正确
    2. 确保前端请求URL与后端允许的域名匹配
    
### SQL语法错误
- **错误**: 出现SQL语法错误或列名未找到
  - **解决方案**:
    1. 检查表名前缀是否使用正确
    2. 检查列名是否正确，尤其是名称中含有下划线的列
    3. 确保关联查询时使用了正确的表别名，如 `o.user_id` 而不是 `user_id`

### 前端网络请求错误
- **错误**: 请求后端API失败
  - **解决方案**:
    1. 检查API地址配置是否正确
    2. 确认后端服务是否正常运行
    3. 检查请求格式是否符合接口要求