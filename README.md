# sharing-blog

English | [简体中文](https://github.com/magicalmuggle/sharing-blog/blob/main/README_CN.md)

[![GitHub license](https://img.shields.io/github/license/magicalmuggle/sharing-blog)](https://github.com/magicalmuggle/sharing-blog/blob/main/LICENSE)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/magicalmuggle/sharing-blog/Java%20CI)

This project is a multi-person sharing blog platform, using Spring Boot as the development framework.

## Preparation

1. Clone the project:
```shell
# Change to the directory used to store the project
cd ~/Projects/
git clone https://github.com/magicalmuggle/sharing-blog
```

2. Use [Docker](https://www.docker.com/) to run [MySQL](https://www.mysql.com/):
```shell
# Change to the directory used to persist the data
cd ~/Docker-Data/ && mkdir mysql-data
# Remember to change the database password in the project
docker run --name mysql -p 3306:3306 -v ${PWD}/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=blog -d mysql:8
```

3. Use [Flyway](https://flywaydb.org/) to migrate the database:
```shell
cd ~/Projects/sharing-blog/
mvn flyway:migrate
```

4. Use [Maven](https://maven.apache.org/) to package the project:
```shell
cd ~/Projects/sharing-blog/
mvn clean package
```

## Usage

Start the application through the jar package:
```shell
cd ~/Projects/sharing-blog/
java -jar target/sharing-blog-1.0-SNAPSHOT.jar
```

Or use Docker to build and run the image to start the application:
```shell
cd ~/Projects/sharing-blog/
docker build . -t sharing-blog
docker run -it -p 8080:8080 sharing-blog
```
