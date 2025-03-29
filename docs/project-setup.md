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

## IDE配置

### IntelliJ IDEA配置

1. **推荐设置**
   - 使用2空格缩进或4空格缩进（项目统一）
   - 启用自动保存
   - 行宽设置为120字符
   - 启用代码格式化提示

2. **必装插件**
   - Lombok：支持Lombok注解
   - MapStruct Support：支持MapStruct映射
   - Spring Boot Assistant：Spring Boot开发辅助
   - Alibaba Java Coding Guidelines：阿里巴巴Java代码规范检查
   - SonarLint：代码质量检查
   - MySQL/Redis：数据库支持

3. **代码模板配置**
   推荐配置类和方法注释模板：
   
   ```java
   /**
    * ${NAME} - ${description}
    *
    * @author ${USER}
    * @date ${DATE} ${TIME}
    */
   ```

### HBuilderX配置

1. **推荐设置**
   - 使用2空格缩进
   - 启用ES6语法提示
   - 启用Vue语法提示

2. **必装插件**
   - uniapp-snippet：uni-app代码片段
   - eslint-js：JavaScript代码检查
   - eslint-vue：Vue代码检查
   - scss/sass：支持SCSS/SASS

3. **代码格式化**
   推荐使用prettier配置：
   
   ```json
   {
     "singleQuote": true,
     "semi": false,
     "printWidth": 100,
     "trailingComma": "none",
     "arrowParens": "avoid"
   }
   ```

## 代码质量管理

### 静态代码分析

1. **ESLint**
   前端代码检查工具，配置文件：`.eslintrc.js`

   ```javascript
   module.exports = {
     root: true,
     extends: [
       'plugin:vue/vue3-essential',
       'eslint:recommended',
       '@vue/typescript/recommended'
     ],
     parserOptions: {
       ecmaVersion: 2020
     },
     rules: {
       // 自定义规则
     }
   }
   ```

2. **阿里Java代码规约**
   后端代码规范，IDEA插件自动检查

3. **SonarQube**
   可选择部署SonarQube服务器，提供更全面的代码质量检查

### 测试框架

1. **JUnit 5**
   Java单元测试框架

2. **MockMvc**
   Spring Boot API测试工具

3. **jest**
   JavaScript单元测试框架

## 开发流程建议

### 新功能开发流程

1. 从最新的`dev`分支拉取新的功能分支：`feature/xxx`
2. 编写代码和单元测试
3. 确保代码通过所有静态检查和单元测试
4. 提交Pull Request到`dev`分支
5. 代码评审通过后合并

### 修复Bug流程

1. 从对应的版本分支创建修复分支：`bugfix/xxx`
2. 修复bug并添加单元测试
3. 确保代码通过所有静态检查和单元测试
4. 提交Pull Request到对应版本分支
5. 经评审后合并，并考虑是否需要向后移植

### 代码评审标准

1. 功能完整性：是否实现了需求的所有功能
2. 代码质量：是否符合代码规范
3. 测试覆盖率：是否有足够的测试
4. 性能考虑：是否存在性能问题
5. 安全性：是否存在安全漏洞

## 常用命令

### 后端

```bash
# 构建项目
mvn clean package

# 运行测试
mvn test

# 生成JavaDoc
mvn javadoc:javadoc

# 运行Spring Boot应用
mvn spring-boot:run
```

### 前端

```bash
# 安装依赖
npm install

# 开发模式运行
npm run dev:mp-weixin

# 构建微信小程序
npm run build:mp-weixin

# 代码检查
npm run lint
``` 