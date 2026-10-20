# ===== 后端 Dockerfile（多阶段构建，构建上下文为仓库根目录）=====

# 第一阶段：Maven 打包
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 先复制各模块 pom.xml，利用 Docker 缓存加速依赖下载
COPY seecen_mall/mall_server/pom.xml ./
COPY seecen_mall/mall_server/mall_common/pom.xml mall_common/
COPY seecen_mall/mall_server/mall_core/pom.xml mall_core/
COPY seecen_mall/mall_server/mall_ai/pom.xml mall_ai/
RUN mvn -B dependency:go-offline -DskipTests || true

# 再复制后端源码执行打包
COPY seecen_mall/mall_server/ ./
RUN mvn -B clean package -DskipTests

# 第二阶段：仅保留 JRE 运行环境
FROM eclipse-temurin:21-jre
WORKDIR /app

# 上传目录，并把现有商品图片直接打包进镜像，云端也能显示商品图
RUN mkdir -p /app/upload
COPY product-images/ /app/upload/
ENV UPLOAD_PATH=/app/upload/

# 复制可执行 jar（spring-boot-maven-plugin 在 mall_core 模块）
COPY --from=build /app/mall_core/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
