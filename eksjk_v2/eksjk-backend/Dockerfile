# EKSJK V2 后端应用 Dockerfile
# 基于 Eclipse Temurin JRE 21 构建 Spring Boot 应用

FROM eclipse-temurin:21-jre-jammy

# 设置工作目录
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 复制 Maven 构建产物
COPY eksjk-web/target/eksjk-web-*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

# 复制外部配置目录（K8S ConfigMap 挂载点）
RUN mkdir -p /app/config

# 设置 JVM 参数
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用（加载外部配置目录）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.config.additional-location=optional:file:/app/config/ -jar app.jar"]