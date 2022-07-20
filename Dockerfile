FROM openjdk:8u201-jdk-alpine3.9

RUN mkdir /app

WORKDIR /app

COPY target/sharing-blog-1.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD [ "java", "-jar", "sharing-blog-1.0-SNAPSHOT.jar" ]