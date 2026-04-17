# 多阶段构建：编译阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 使用国内 Maven 镜像源，避免构建阶段访问 central 超时
RUN mkdir -p /root/.m2 && printf '%s\n' \
'<?xml version="1.0" encoding="UTF-8"?>' \
'<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' \
'          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' \
'          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">' \
'  <mirrors>' \
'    <mirror>' \
'      <id>aliyunmaven</id>' \
'      <mirrorOf>*</mirrorOf>' \
'      <name>Aliyun Maven</name>' \
'      <url>https://maven.aliyun.com/repository/public</url>' \
'    </mirror>' \
'  </mirrors>' \
'</settings>' > /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/gateway-*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
