# 需求文档：删除 docker-compose，迁移至 Kubernetes 本地部署

> **实施优先级：Plan 1（最高优先级）**
> **前置依赖：无**
> **后续依赖：Plan 2（可观测性指标+大盘）、Plan 3（Mock 数据）、Plan 4（测试验证）均依赖本 Plan 提供的 K8S 运行环境**

> **合并说明：** 本文档合并了以下三份原始需求文档：
> - `replace-docker-compose-with-k8s`（K8S 迁移）
> - `k8s-local-deploy`（K8S 本地部署）
> - `remove-redis-use-caffeine`（移除 Redis + 引入 Caffeine）
>
> 移除 Redis 和引入 Caffeine 属于部署迁移前的架构清理工作，工作量较小，作为本 Plan 的前置步骤一并完成。

## 引言

当前 EKSJK V2 系统使用 `docker-compose.yml` 作为容器编排方案，且配置了实际未使用的 Redis 服务。随着系统进入测试验证阶段，需要：

1. **清理冗余依赖**：移除未使用的 Redis，引入 Caffeine 内存缓存替代
2. **统一部署平台**：删除 `docker-compose.yml`，迁移至 Kubernetes（Rancher Desktop 本地 K8S）
3. **完善基础设施**：创建完整的 K8S 资源清单，提供一键部署能力

**关键约束：**
- Redis 经代码分析确认为冗余服务（后端无任何 Redis 依赖），K8S 迁移中**不包含** Redis
- 系统仅依赖 MySQL 作为外部存储，引入 Caffeine 作为 JVM 内存缓存
- 镜像构建使用 Rancher Desktop 的 `nerdctl`，不使用 Docker Desktop

**逻辑依赖关系：**
```
需求1（移除 Redis + 引入 Caffeine）→ 需求2（删除 docker-compose）
    ↓
需求3（K8S 目录结构 + Namespace）
    ↓
需求4（MySQL）→ 需求5（后端+专用配置）→ 需求6（前端+Ingress）→ 需求7（监控组件部署）
    ↓
需求8（一键部署和清理脚本）
    ↓
需求9（部署文档 + 更新 claude.md）
```

---

## 需求

### 需求 1：移除 Redis 并引入 Caffeine 内存缓存

**用户故事：** 作为一名开发者，我希望移除未使用的 Redis 依赖，并引入 Caffeine 作为轻量级内存缓存，以便简化系统架构，减少部署依赖。

> **背景：** 当前 `docker-compose.yml` 中配置了 Redis 服务，但后端代码中无任何 Redis 使用（无 `spring-boot-starter-data-redis`、无 `RedisTemplate`、Sa-Token 使用 JWT 无状态模式）。Redis 是"配置了但未使用"的冗余中间件。

#### 验收标准

1. WHEN 查看 `docker-compose.yml`（删除前） THEN 系统 SHALL 不包含 `redis` 服务定义、`redis_data` 数据卷声明、`SPRING_REDIS_HOST`/`SPRING_REDIS_PORT` 环境变量
2. WHEN 检查所有 `pom.xml` THEN 系统 SHALL 不包含任何 Redis 相关依赖（`spring-boot-starter-data-redis`、`sa-token-redis` 等）
3. WHEN 查看父 `pom.xml` 的 `dependencyManagement` THEN 系统 SHALL 包含 `com.github.ben-manes.caffeine:caffeine` 依赖声明，版本统一管理
4. WHEN 查看 `eksjk-service` 模块的 `pom.xml` THEN 系统 SHALL 引入 `spring-boot-starter-cache` 和 `caffeine` 依赖
5. WHEN 查看 `application.yml` THEN 系统 SHALL 包含 `spring.cache.type: caffeine` 配置，并配置合理的默认缓存参数
6. WHEN 应用启动 THEN 系统 SHALL 激活 `@EnableCaching` 注解，Spring Cache 抽象层正常工作
7. WHEN 查看 `eksjk-common` 或 `eksjk-web` 模块 THEN 系统 SHALL 包含 `CacheConfig` 配置类，定义常用缓存名称常量和独立的过期策略

---

### 需求 2：删除 docker-compose.yml

**用户故事：** 作为一名开发者，我希望删除 `docker-compose.yml` 文件，以便统一使用 K8S 作为唯一的容器编排平台，避免两套部署方案并存造成混乱。

#### 验收标准

1. WHEN 查看 `eksjk_v2/` 目录 THEN 系统 SHALL 不存在 `docker-compose.yml` 文件
2. WHEN 查看整个项目目录 THEN 系统 SHALL 不存在任何 `docker-compose*.yml` 文件

---

### 需求 3：创建 K8S 目录结构与 Namespace

**用户故事：** 作为一名开发者，我希望有一个清晰的 K8S 资源清单目录和独立的命名空间，以便所有 K8S 配置文件集中管理，与其他应用隔离。

#### 验收标准

1. WHEN 查看 `eksjk_v2/` 目录 THEN 系统 SHALL 包含 `k8s/` 子目录
2. WHEN 查看 `k8s/` 目录 THEN 系统 SHALL 包含以下子目录结构：
   ```
   k8s/
   ├── namespace.yaml          # 命名空间
   ├── deploy.sh               # 一键部署脚本
   ├── teardown.sh             # 一键清理脚本
   ├── README.md               # 部署文档
   ├── mysql/                  # MySQL 相关资源
   ├── backend/                # 后端 Spring Boot 相关资源
   ├── frontend/               # 前端 Nginx 相关资源
   └── monitoring/             # 监控相关资源（Prometheus、Grafana）
   ```
3. WHEN 应用 `k8s/namespace.yaml` THEN 系统 SHALL 创建名为 `eksjk` 的 Namespace
4. WHEN 查看所有 K8S 资源清单 THEN 系统 SHALL 所有资源的 `metadata.namespace` 均为 `eksjk`

---

### 需求 4：创建 MySQL K8S 资源

**用户故事：** 作为一名开发者，我希望 MySQL 以 K8S StatefulSet 方式部署，以便数据持久化存储，并在 Pod 重启后数据不丢失。

#### 验收标准

1. WHEN 查看 `k8s/mysql/` THEN 系统 SHALL 包含以下文件：
   - `secret.yaml` — 存储 MySQL root 密码、业务用户名和密码（Base64 编码）
   - `configmap.yaml` — 存储 MySQL 初始化 SQL 脚本内容和字符集配置
   - `pvc.yaml` — 定义 MySQL 数据持久化存储（PersistentVolumeClaim，容量 ≥ 5Gi）
   - `statefulset.yaml` — MySQL StatefulSet 部署定义，使用 `mysql:8.0` 镜像
   - `service.yaml` — MySQL ClusterIP Service（端口 3306），仅集群内部访问
2. WHEN MySQL Pod 首次启动 THEN 系统 SHALL 自动执行 ConfigMap 中的初始化 SQL 脚本（通过挂载 `/docker-entrypoint-initdb.d/` 实现）
3. WHEN MySQL Pod 重启 THEN 系统 SHALL 数据持久化不丢失（PVC 数据保留）
4. WHEN 查看 MySQL StatefulSet THEN 系统 SHALL 配置字符集为 `utf8mb4`，排序规则为 `utf8mb4_unicode_ci`
5. IF MySQL Secret 中的密码 THEN 系统 SHALL 使用 Base64 编码存储，不明文写入资源清单
6. WHEN 定义 MySQL StatefulSet THEN 系统 SHALL 配置资源限制：`requests: {cpu: 250m, memory: 512Mi}`，`limits: {cpu: 500m, memory: 1Gi}`

---

### 需求 5：创建后端 Spring Boot K8S 资源（含专用配置）

**用户故事：** 作为一名开发者，我希望后端 Spring Boot 应用以 K8S Deployment 方式部署，并使用 K8S 专用配置文件正确连接集群内部服务，支持健康检查和滚动更新。

#### 验收标准

1. WHEN 查看 `k8s/backend/` THEN 系统 SHALL 包含以下文件：
   - `configmap.yaml` — 存储 `application-k8s.yml` 配置内容和非敏感环境配置
   - `deployment.yaml` — 后端 Deployment 定义，使用本地构建的 `eksjk-backend:latest` 镜像
   - `service.yaml` — 后端 ClusterIP Service，暴露 8080 端口供集群内访问
2. WHEN 后端在 K8S 中运行时 THEN 系统 SHALL 激活 `k8s` Spring Profile（通过环境变量 `SPRING_PROFILES_ACTIVE=k8s`），加载 `application-k8s.yml`
3. WHEN 定义 `application-k8s.yml` THEN 系统 SHALL 使用 K8S Service 内部 DNS 名称连接依赖服务：
   - MySQL：`jdbc:mysql://eksjk-mysql.eksjk.svc.cluster.local:3306/eksjk`
   - **不包含**任何 Redis 相关配置
4. WHEN 查看后端 Deployment 的环境变量配置 THEN 系统 SHALL 包含：
   - `SPRING_DATASOURCE_USERNAME` 和 `SPRING_DATASOURCE_PASSWORD` 从 MySQL Secret 中引用
   - `TZ: Asia/Shanghai`（时区配置）
5. WHEN 查看后端 Deployment THEN 系统 SHALL 配置 `livenessProbe` 和 `readinessProbe`，探测路径为 `/actuator/health`
6. WHEN 查看后端 Deployment THEN 系统 SHALL 配置资源限制：`requests: {cpu: 250m, memory: 512Mi}`，`limits: {cpu: 1000m, memory: 1Gi}`
7. WHEN 后端 Pod 启动 THEN 系统 SHALL 通过 `initContainers` 等待 MySQL 就绪后再启动主容器
8. WHEN 查看后端 Deployment THEN 系统 SHALL 配置 `imagePullPolicy: Never`（使用本地镜像，不从远程拉取）
9. WHEN 后端在 K8S 中运行时 THEN 系统 SHALL 通过 `/actuator/prometheus` 端点暴露 Prometheus 指标，供 Prometheus 抓取

---

### 需求 6：创建前端 Nginx K8S 资源（含 Ingress）

**用户故事：** 作为一名开发者，我希望前端 Vue.js 应用以 K8S Deployment 方式部署，通过 Nginx 托管静态资源并通过 Ingress 统一暴露访问入口。

#### 验收标准

1. WHEN 查看 `k8s/frontend/` THEN 系统 SHALL 包含以下文件：
   - `configmap.yaml` — 存储 Nginx 配置文件内容（API 代理目标指向 `http://eksjk-backend:8080`）
   - `deployment.yaml` — 前端 Deployment 定义，使用本地构建的 `eksjk-frontend:latest` 镜像
   - `service.yaml` — 前端 ClusterIP Service，暴露 80 端口
   - `ingress.yaml` — Ingress 资源，统一暴露 HTTP 访问入口
2. WHEN 查看 Ingress THEN 系统 SHALL 配置路由规则：
   - `/api/` 路径转发至后端 Service
   - `/` 路径转发至前端 Service
   - Grafana 访问路由（如 `/grafana/`）
3. WHEN 通过浏览器访问 `http://localhost` THEN 系统 SHALL 能正常访问前端页面
4. WHEN 查看前端 Deployment THEN 系统 SHALL 配置 `imagePullPolicy: Never`（使用本地镜像）
5. WHEN 查看前端 Deployment THEN 系统 SHALL 配置资源限制：`requests: {cpu: 50m, memory: 64Mi}`，`limits: {cpu: 100m, memory: 128Mi}`
6. IF Rancher Desktop 未启用 Ingress Controller THEN 系统 SHALL 同时提供 NodePort 类型的 Service 作为备选访问方式

---

### 需求 7：创建监控组件 K8S 资源（Prometheus + Grafana 部署）

**用户故事：** 作为一名开发者，我希望 Prometheus 和 Grafana 也以 K8S 方式部署，以便在同一个 K8S 环境中统一管理所有服务。

> **边界说明：** 本需求仅负责 Prometheus 和 Grafana 的 K8S 部署。指标暴露的具体内容和 Grafana 大盘设计由 Plan 2（可观测性指标+大盘）负责。

#### 验收标准

1. WHEN 查看 `k8s/monitoring/` THEN 系统 SHALL 包含以下文件：
   - `prometheus-configmap.yaml` — Prometheus 抓取配置（scrape_configs，包含后端 `/actuator/prometheus` 端点，job 标签为 `eksjk-backend`）
   - `prometheus-deployment.yaml` — Prometheus Deployment 和 Service
   - `prometheus-pvc.yaml` — Prometheus 数据持久化存储
   - `grafana-deployment.yaml` — Grafana Deployment 和 Service
   - `grafana-pvc.yaml` — Grafana 数据持久化存储
2. WHEN Prometheus 启动 THEN 系统 SHALL 每隔 15 秒自动抓取后端 `/actuator/prometheus` 端点
3. WHEN 通过浏览器访问 Grafana THEN 系统 SHALL 能正常登录（默认账号 `admin/admin`），并预置 Prometheus 数据源
4. WHEN Prometheus/Grafana Pod 重启 THEN 系统 SHALL 数据持久化不丢失
5. WHEN 定义监控组件 THEN 系统 SHALL 配置合理的资源限制：
   - Prometheus：`requests: {cpu: 100m, memory: 256Mi}`，`limits: {cpu: 200m, memory: 512Mi}`
   - Grafana：`requests: {cpu: 50m, memory: 128Mi}`，`limits: {cpu: 100m, memory: 256Mi}`

---

### 需求 8：一键部署和清理脚本

**用户故事：** 作为一名开发者，我希望有一键部署和清理脚本，以便在 Rancher Desktop 本地 K8S 环境中快速完成镜像构建和服务部署，无需手动执行多条命令。

#### 验收标准

1. WHEN 执行 `k8s/deploy.sh` THEN 系统 SHALL 按顺序完成以下操作：
   1. 检查前置依赖（`kubectl`、`nerdctl`、`mvn`、`npm` 是否已安装）
   2. 检查 Rancher Desktop K8S 集群是否可达（`kubectl cluster-info`）
   3. 构建后端 Maven 项目（`mvn clean package -DskipTests`）
   4. 使用 `nerdctl build` 构建后端镜像（`eksjk-backend:latest`）
   5. 构建前端项目（`npm run build`）
   6. 使用 `nerdctl build` 构建前端镜像（`eksjk-frontend:latest`）
   7. 按顺序应用 K8S 资源清单（namespace → mysql → backend → frontend → monitoring）
   8. 等待所有 Pod 就绪（`kubectl wait --for=condition=ready pod`），超时 120 秒
   9. 输出各服务的访问地址和默认账号密码
2. WHEN 执行 `k8s/deploy.sh --skip-build` THEN 系统 SHALL 跳过镜像构建步骤，直接应用 K8S 资源清单
3. WHEN 执行 `k8s/deploy.sh --only-monitoring` THEN 系统 SHALL 仅部署 Prometheus 和 Grafana
4. WHEN 执行 `k8s/teardown.sh` THEN 系统 SHALL 删除 `eksjk` 命名空间下的所有资源，**但保留 PVC 数据卷**
5. WHEN 执行 `k8s/teardown.sh --with-data` THEN 系统 SHALL 同时删除 PVC，清除所有持久化数据
6. WHEN 执行清理操作 THEN 脚本 SHALL 在删除前打印将要删除的资源列表，并要求用户输入 `yes` 确认
7. WHEN 脚本执行过程中出现错误 THEN 系统 SHALL 输出明确的错误信息和排查建议，并以非零状态码退出
8. WHEN 查看脚本 THEN 系统 SHALL 包含中文注释说明每个步骤的目的

---

### 需求 9：部署文档与技术文档更新

**用户故事：** 作为一名开发者，我希望有清晰的本地 K8S 部署文档，并且 `claude.md` 能反映最新的部署架构和技术选型。

#### 验收标准

1. WHEN 开发人员查阅文档 THEN 系统 SHALL 提供 `k8s/README.md`，包含以下内容：
   - 前置条件：Rancher Desktop 版本要求（≥ 1.9）、需要启用的功能（Kubernetes、containerd）
   - 快速开始：3 步完成部署（配置 hosts → 执行 deploy.sh → 访问服务）
   - 各服务访问地址和默认账号密码汇总表
   - 常用 kubectl 命令速查（查看 Pod 状态、查看日志、进入容器）
   - 常见问题排查（镜像拉取失败、Pod 启动失败、Ingress 不通等）
   - 增量更新命令（仅重新构建后端镜像并滚动更新 Deployment）
2. WHEN 查看 `claude.md` 的"部署架构"章节 THEN 系统 SHALL 描述 K8S 部署方案，不再包含 Docker Compose 相关描述
3. WHEN 查看 `claude.md` 的"部署注意事项"章节 THEN 系统 SHALL 包含：
   - 使用 `k8s/deploy.sh` 一键部署到 Rancher Desktop 本地 K8S
   - 所有资源部署在 `eksjk` 命名空间
   - 镜像构建使用 `nerdctl`（Rancher Desktop），不使用 Docker Desktop
   - 系统仅依赖 MySQL 作为外部存储，不依赖 Redis
4. WHEN 查看 `claude.md` 的后端技术栈表格 THEN 系统 SHALL：
   - 不再包含 Redis 相关描述
   - 包含 Caffeine 内存缓存的技术选型说明（`Caffeine` — JVM 内存缓存，通过 Spring Cache 抽象层使用）
