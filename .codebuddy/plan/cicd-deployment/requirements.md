# 需求文档：EKSJK 发布部署 CI/CD 流程

## 引言

本文档描述 EKSJK（儿科生长发育数据管理系统）基于 **GitOps 理念**的三阶段发布部署流程的需求规范。

系统包含以下子应用：
- `eksjk-backend`：Spring Boot 后端服务
- `eksjk-frontend`：Vue3 前端 Web 应用
- `eksjk-miniapp`：家长端微信小程序
- `eksjk-miniapp-doctor`：医生端微信小程序

**部署简化原则：** 系统采用轻量化、低成本部署策略：
- **不引入 Redis**：缓存层使用 JVM 内存缓存（Caffeine），会话管理使用 JWT 无状态方案；
- **单副本部署**：每个环境后端服务仅运行 1 个 Pod 副本，不做水平扩展，以降低云资源成本；
- **共享云资源**：测试与生产共用同一套阿里云资源实例，通过逻辑隔离（Namespace / 数据库名 / Bucket 名）区分环境，最大化降低云资源费用。

三个部署阶段定义如下：

| 阶段 | 环境名称 | 资源位置 | 触发方式 |
|------|----------|----------|----------|
| 阶段一 | 本地开发（local） | 开发者本机 | 手动 |
| 阶段二 | 测试验证（staging） | 阿里云 | 推送到 `develop` 分支自动触发 |
| 阶段三 | 正式发布（production） | 阿里云 | 推送 `release/v*` tag 自动触发 |

**云资源共享策略（最小化成本）：**

| 资源 | 共享方式 | Staging 标识 | Production 标识 |
|------|---------|-------------|----------------|
| ACK 集群 | 共用 1 个集群、1 个节点 | Namespace: `eksjk-staging` | Namespace: `eksjk-prod` |
| RDS MySQL | 共用 1 个实例 | 数据库名: `eksjk_staging` | 数据库名: `eksjk_prod` |
| OSS 对象存储 | 共用 1 个账号 | Bucket: `eksjk-staging` | Bucket: `eksjk-prod` |
| ACR 镜像仓库 | 共用个人版（免费） | 镜像 tag 含 `staging` | 镜像 tag 含版本号 |

各阶段**最小化组件清单**（无 Redis，单副本，共享云资源）：

| 组件 | 本地（local） | 测试（staging） | 生产（production） |
|------|--------------|----------------|-------------------|
| 后端服务 | Docker 容器 × 1 | ACK Pod × 1（`eksjk-staging`） | ACK Pod × 1（`eksjk-prod`） |
| 前端服务 | Docker 容器 × 1 | ACK Pod × 1（`eksjk-staging`） | ACK Pod × 1（`eksjk-prod`） |
| 数据库 | MySQL 容器 × 1 | RDS 实例（库名 `eksjk_staging`） | RDS 实例（库名 `eksjk_prod`） |
| 对象存储 | MinIO 容器 × 1 | OSS Bucket `eksjk-staging` | OSS Bucket `eksjk-prod` |
| 缓存 | Caffeine（JVM 内存） | Caffeine（JVM 内存） | Caffeine（JVM 内存） |

---

## 需求

### 需求 1：环境隔离与资源规划

**用户故事：** 作为运维工程师，我希望测试和生产环境共享同一套阿里云资源实例并通过逻辑隔离区分，以便在保障数据安全的前提下最大化降低云资源成本。

#### 验收标准

1. WHEN 系统部署到测试或生产阶段 THEN 系统 SHALL 使用同一个 ACK 集群，通过独立的 Kubernetes Namespace（`eksjk-staging`、`eksjk-prod`）进行资源隔离，不得跨 Namespace 访问对方资源。
2. WHEN 系统部署到测试或生产阶段 THEN 系统 SHALL 使用同一个阿里云 RDS MySQL 实例，通过不同数据库名（`eksjk_staging`、`eksjk_prod`）进行数据隔离，不得共用同一数据库名。
3. WHEN 系统部署到测试或生产阶段 THEN 系统 SHALL 使用阿里云 OSS，通过不同 Bucket 名（`eksjk-staging`、`eksjk-prod`）进行文件隔离，不得共用同一 Bucket。
4. WHEN 系统部署到本地开发阶段 THEN 系统 SHALL 使用本地 Docker 运行 MySQL 和 MinIO（S3 兼容）容器，不依赖任何云资源。
5. IF 某阶段的配置（数据库连接、OSS 密钥等）发生变更 THEN 系统 SHALL 通过 Kubernetes Secret/ConfigMap 管理，不得将敏感信息提交到 Git 仓库。
6. WHEN 系统在任意阶段部署 THEN 系统 SHALL 不包含 Redis 组件，不得在 Kubernetes 清单或 Docker Compose 中声明 Redis 容器/服务。
7. WHEN 为 ACK 集群中的各 Namespace 配置资源时 THEN 系统 SHALL 为每个 Namespace 设置 ResourceQuota，防止 staging 环境抢占 prod 环境资源。

---

### 需求 2：代码仓库与分支策略

**用户故事：** 作为开发工程师，我希望有清晰的 Git 分支策略与代码托管规范，以便团队协作有序，并与 CI/CD 流程自动联动。

#### 验收标准

1. WHEN 开发者向 `feature/*` 分支提交代码 THEN 系统 SHALL 仅触发代码静态检查（Lint）和单元测试，不触发部署。
2. WHEN 开发者将代码合并到 `develop` 分支 THEN 系统 SHALL 自动触发测试环境（staging）的构建与部署流程。
3. WHEN 运维人员在 GitHub 上创建 `release/v*` 格式的 Tag THEN 系统 SHALL 自动触发生产环境（production）的构建与部署流程。
4. WHEN 生产部署流程被触发 THEN 系统 SHALL 要求至少一名审批人在 GitHub Actions 中手动确认后，方可继续执行部署。
5. IF 任意 CI/CD 流程步骤失败 THEN 系统 SHALL 停止后续步骤并通过通知渠道（钉钉/邮件）告警。

---

### 需求 3：CI 持续集成流程

**用户故事：** 作为开发工程师，我希望每次代码提交都能自动完成构建与测试，以便尽早发现问题，保证代码质量。

#### 验收标准

1. WHEN CI 流程被触发 THEN 系统 SHALL 自动完成后端 Maven 编译（`mvn clean package -DskipTests`）并生成可执行 JAR 包。
2. WHEN CI 流程被触发 THEN 系统 SHALL 自动完成前端 npm 依赖安装与构建（`npm install && npm run build`）。
3. WHEN 构建产物生成后 THEN 系统 SHALL 使用 Dockerfile 构建 Docker 镜像，并推送到阿里云 ACR 个人版镜像仓库。
4. WHEN 镜像推送成功 THEN 系统 SHALL 按照 `{service}:{branch}-{commit_sha}` 格式打标签，生产镜像额外打 `{service}:{version_tag}` 标签。
5. IF 后端或前端构建失败 THEN 系统 SHALL 不执行镜像构建和推送步骤，并标记流程为失败。

---

### 需求 4：CD 持续部署流程（GitOps）

**用户故事：** 作为运维工程师，我希望通过 GitOps 方式管理 Kubernetes 部署配置，以便所有环境变更都有 Git 记录可追溯，并支持快速回滚。

#### 验收标准

1. WHEN 镜像构建推送成功 THEN 系统 SHALL 自动更新 Git 仓库中对应环境的 Kubernetes 清单文件（`k8s/staging/` 或 `k8s/prod/` 目录下的 Deployment 镜像版本）。
2. WHEN Kubernetes 清单文件更新后 THEN 系统 SHALL 通过 `kubectl apply` 将变更应用到对应 Namespace。
3. WHEN 部署完成后 THEN 系统 SHALL 执行健康检查，验证 Pod 全部 Ready 且服务可访问。
4. IF 部署后健康检查失败 THEN 系统 SHALL 自动执行回滚（`kubectl rollout undo`）并发送告警通知。
5. WHEN 需要回滚到历史版本 THEN 运维人员 SHALL 能够通过修改 Git 仓库中的镜像版本号并提交，触发自动重新部署。

---

### 需求 5：阿里云云资源集成

**用户故事：** 作为运维工程师，我希望测试和生产环境共享同一套阿里云实例并通过配置区分，以便用最少的云资源成本支撑两套环境运行。

#### 验收标准

1. WHEN 后端服务启动 THEN 系统 SHALL 通过环境变量注入 RDS MySQL 连接信息（host、port、user、password、database），其中 `database` 字段按环境分别指向 `eksjk_staging` 或 `eksjk_prod`。
2. WHEN 文件上传/下载功能被调用 THEN 系统 SHALL 使用阿里云 OSS（S3 兼容协议）作为存储后端，通过 `eksjk.upload.s3.bucket` 配置项区分 staging（`eksjk-staging`）和 prod（`eksjk-prod`）Bucket。
3. WHEN 部署到测试环境 THEN 系统 SHALL 使用 RDS 实例中的 `eksjk_staging` 数据库和 `eksjk-staging` OSS Bucket。
4. WHEN 部署到生产环境 THEN 系统 SHALL 使用 RDS 实例中的 `eksjk_prod` 数据库和 `eksjk-prod` OSS Bucket，并为 RDS 实例开启自动备份。
5. IF 阿里云 OSS 或 RDS 连接失败 THEN 系统 SHALL 在启动时输出明确的错误日志并拒绝启动，避免以降级模式运行导致数据丢失。
6. WHEN 配置 RDS 数据库账号时 THEN 系统 SHALL 为 staging 和 prod 分别创建独立的数据库账号，账号仅拥有对应数据库的权限，不得使用同一账号跨库访问。

---

### 需求 6：本地开发环境一键启动

**用户故事：** 作为开发工程师，我希望能够一键启动本地开发环境，以便快速进入开发状态，无需手动配置复杂的依赖。

#### 验收标准

1. WHEN 开发者执行本地启动脚本 THEN 系统 SHALL 自动启动本地 MySQL 和 MinIO 容器，并完成数据库初始化（执行 `init-sql/` 下的 SQL 文件）。
2. WHEN 本地依赖服务就绪后 THEN 系统 SHALL 支持通过 `application-local.yml` 配置文件切换到本地资源（MySQL、MinIO），与测试/生产配置完全隔离。
3. WHEN 开发者修改后端代码 THEN 系统 SHALL 支持热重载（Spring Boot DevTools），无需重启容器。
4. IF 本地端口冲突 THEN 系统 SHALL 在启动脚本中检测并提示冲突端口，引导开发者解决。
5. WHEN 开发者执行本地启动脚本 THEN 系统 SHALL 不启动 Redis 容器，本地环境与测试/生产环境保持一致的无 Redis 架构。

---

### 需求 7：可观测性与部署通知

**用户故事：** 作为运维工程师，我希望每次部署都有完整的日志记录和状态通知，以便及时了解部署结果并快速响应异常。

#### 验收标准

1. WHEN 任意阶段的 CI/CD 流程完成（成功或失败）THEN 系统 SHALL 发送部署结果通知，包含：环境名称、触发分支/Tag、镜像版本、部署状态、耗时。
2. WHEN 生产环境部署成功 THEN 系统 SHALL 在 GitHub Release 页面自动生成 Release Notes（基于 commit 信息）。
3. WHEN 服务运行时 THEN 系统 SHALL 通过现有 Prometheus + Grafana 监控栈采集应用指标，并在 Grafana 中展示部署事件标记。

---

### 需求 8：去除 Redis 依赖，单副本简化部署

**用户故事：** 作为运维工程师，我希望系统不依赖 Redis 且每个环境只运行单个后端节点，以便最大程度降低中间件数量和云资源成本。

#### 验收标准

1. WHEN 后端服务启动 THEN 系统 SHALL 使用 Caffeine 作为唯一缓存实现，不得引入 `spring-boot-starter-data-redis` 依赖。
2. WHEN 检查后端 `pom.xml` THEN 系统 SHALL 不包含任何 Redis 相关 Maven 依赖（如 `spring-data-redis`、`lettuce-core`、`jedis`）。
3. WHEN 检查任意环境的配置文件（`application*.yml`）THEN 系统 SHALL 不包含 `spring.redis.*` 配置块。
4. WHEN 用户登录后 THEN 系统 SHALL 使用 JWT（JSON Web Token）实现无状态会话管理，Token 有效期和刷新策略通过配置文件控制，不依赖外部存储。
5. WHEN 系统在 Kubernetes 中部署 THEN 系统 SHALL 不包含 Redis Deployment、Service 或 PersistentVolumeClaim 资源清单。
6. WHEN 编写 Kubernetes Deployment 清单 THEN 系统 SHALL 将后端服务的 `replicas` 设置为 `1`，不配置 HorizontalPodAutoscaler（HPA）。
7. WHEN 后端 Pod 发生重启或重新部署 THEN 系统 SHALL 依赖 JWT 的无状态特性保证用户会话在单副本重启后仍可通过重新登录恢复，无需持久化会话状态。
