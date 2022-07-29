# sharing-blog

[English](https://github.com/magicalmuggle/sharing-blog/) | 简体中文

[![GitHub license](https://img.shields.io/github/license/magicalmuggle/sharing-blog)](https://github.com/magicalmuggle/sharing-blog/blob/main/LICENSE)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/magicalmuggle/sharing-blog/Java%20CI)

本项目是一个多人共享博客平台，使用 Spring Boot 作为开发框架。

## 准备工作

1. 克隆项目：
```shell
# 切换到用来存储项目的目录
cd ~/Projects/
git clone https://github.com/magicalmuggle/sharing-blog
```

2. 使用 [Docker](https://www.docker.com/) 来运行 [MySQL](https://www.mysql.com/)：
```shell
# 切换到用来持久化数据的目录
cd ~/Docker-Data/ && mkdir mysql-data
# 记得修改项目中的数据库密码
docker run --name mysql -p 3306:3306 -v ${PWD}/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=blog -d mysql:8
```

3. 使用 [Flyway](https://flywaydb.org/) 来迁移数据库：
```shell
cd ~/Projects/sharing-blog/
mvn flyway:migrate
```

4. 使用 [Maven](https://maven.apache.org/) 来打包项目：
```shell
cd ~/Projects/sharing-blog/
mvn clean package
```

## 用法

通过 jar 包来启动应用：
```shell
cd ~/Projects/sharing-blog/
java -jar target/sharing-blog-1.0-SNAPSHOT.jar
```

或者使用 Docker 构建并运行镜像来启动应用：
```shell
cd ~/Projects/sharing-blog/
docker build . -t sharing-blog
docker run -it -p 8080:8080 sharing-blog
```
