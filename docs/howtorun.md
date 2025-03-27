后端启动
后端项目是基于Spring Boot开发的服务器应用，需要配置好环境后启动：
安装JDK：
下载安装JDK 11或更高版本：https://www.oracle.com/java/technologies/javase-downloads.html
配置JAVA_HOME环境变量
安装Maven：
下载安装Maven：https://maven.apache.org/download.cgi
配置M2_HOME环境变量和添加到PATH
配置数据库：
安装MySQL 8.0：https://dev.mysql.com/downloads/mysql/
创建数据库：
修改application.yml中的数据库连接信息（用户名和密码）
安装Redis：
下载安装Redis：https://redis.io/download
Windows用户可使用：https://github.com/tporadowski/redis/releases
启动Redis服务
默认配置已设置为localhost:6379，如有不同请修改application.yml
使用IDE启动（推荐）：
使用IntelliJ IDEA打开后端项目目录
等待Maven加载依赖
运行RuralExpressApplication.java
使用命令行启动：
进入项目的backend目录
执行Maven命令构建项目：
   mvn clean package -DskipTests
运行生成的JAR文件：
   java -jar target/rural-express-0.0.1-SNAPSHOT.jar
访问应用
后端API服务：http://localhost:8080/api
前端：通过微信开发者工具预览