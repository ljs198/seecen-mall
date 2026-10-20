# ===== 后端 Dockerfile =====
FROM openjdk:21-slim
WORKDIR /app
COPY mall_server/ /app/
RUN cd /app && ./mvnw clean package -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "mall_core/target/mall_core.jar"]
