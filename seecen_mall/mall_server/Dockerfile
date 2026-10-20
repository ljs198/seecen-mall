# ===== 多阶段构建：第一阶段 Maven 打包 =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 先复制 pom.xml 单独下载依赖（利用 Docker 缓存加速后续构建）
COPY pom.xml .
COPY mall_common/pom.xml mall_common/
COPY mall_core/pom.xml mall_core/
COPY mall_ai/pom.xml mall_ai/
RUN mvn -B dependency:go-offline -DskipTests || true

# 再复制源码执行打包
COPY . .
RUN mvn -B clean package -DskipTests

# ===== 第二阶段：仅保留 JRE 运行 =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# 上传文件目录（Render 环境变量 UPLOAD_PATH 可覆盖）
RUN mkdir -p /app/upload
ENV UPLOAD_PATH=/app/upload/

# 复制可执行 jar（spring-boot-maven-plugin 在 mall_core 模块）
COPY --from=build /app/mall_core/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
