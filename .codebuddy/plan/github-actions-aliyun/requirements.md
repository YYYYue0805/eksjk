# 需求文档：GitHub Actions 与阿里云资源集成（CI/CD 打通方案）

## 引言

本文档描述 EKSJK 项目如何通过 **GitHub Actions** 打通阿里云云资源，实现从代码提交到服务上线的全自动化发布部署流程。

### 背景与目标

基于已确定的部署架构：
- 代码托管在 **GitHub**，CI/CD 引擎使用 **GitHub Actions**
- 镜像仓库使用 **阿里云 ACR 个人版**
- 容器运行环境使用 **阿里云 ACK**（共享单集群，staging/prod 通过 Namespace 隔离）
- 数据库使用 **阿里云 RDS MySQL**（共享单实例，通过数据库名隔离）
- 文件存储使用 **阿里云 OSS**（通过 Bucket 名隔离）
- 公网入口使用 **两个独立 CLB**（staging 和 prod 各自独立 EIP）

### 三阶段流水线触发策略

| 触发条件 | 目标环境 | 流水线动作 |
|---------|---------|-----------|
| `feature/*` 分支 push | — | 仅执行代码检查 + 单元测试 |
| `develop` 分支 push/merge | staging | 构建镜像 → 推送 ACR → 部署到 `eksjk-staging` Namespace |
| 创建 `release/v*` Tag | production | 构建镜像 → 推送 ACR → 人工审批 → 部署到 `eksjk-prod` Namespace |

---

## 需求

### 需求 1：GitHub Actions 与阿里云的身份认证授权

**用户故事：** 作为运维工程师，我希望 GitHub Actions 能够安全地访问阿里云 ACR、ACK 等资源，以便流水线可以自动推送镜像和部署服务，同时不将任何密钥硬编码在代码中。

#### 验收标准

1. WHEN 配置 GitHub Actions 与阿里云集成时 THEN 系统 SHALL 使用阿里云 RAM 子账号（而非主账号 AccessKey）创建专用的 CI/CD 服务账号，遵循最小权限原则。
2. WHEN 创建 CI/CD RAM 账号时 THEN 系统 SHALL 仅授予以下最小权限：ACR 镜像推送/拉取权限、ACK `kubectl apply` 部署权限（限定到指定 Namespace）、不授予 RDS/OSS 的写入权限。
3. WHEN 存储阿里云凭证时 THEN 系统 SHALL 将 `ALIYUN_ACCESS_KEY_ID`、`ALIYUN_ACCESS_KEY_SECRET`、`ACR_REGISTRY`、`ACK_KUBECONFIG` 等敏感信息存储在 **GitHub Repository Secrets** 中，不得出现在任何代码文件或 workflow YAML 的明文中。
4. WHEN GitHub Actions workflow 运行时 THEN 系统 SHALL 通过 `${{ secrets.XXX }}` 语法引用 Secrets，确保日志中不打印任何密钥内容。
5. WHEN 配置 ACK 集群访问时 THEN 系统 SHALL 使用阿里云 ACK 提供的 **KubeConfig 文件**（存储为 GitHub Secret），通过 `kubectl` 操作集群，不使用账号密码方式。

---

### 需求 2：Docker 镜像构建与推送到阿里云 ACR

**用户故事：** 作为开发工程师，我希望每次代码合并后 GitHub Actions 能自动构建 Docker 镜像并推送到阿里云 ACR，以便后续部署步骤可以直接拉取最新镜像。

#### 验收标准

1. WHEN CI 流程触发后端构建 THEN 系统 SHALL 先执行 `mvn clean package -DskipTests` 生成 JAR 包，再执行 `docker build` 构建后端镜像。
2. WHEN CI 流程触发前端构建 THEN 系统 SHALL 先执行 `npm install && npm run build` 生成静态产物，再执行 `docker build` 构建前端 Nginx 镜像。
3. WHEN 推送镜像到 ACR 前 THEN 系统 SHALL 使用 `docker login` 命令登录阿里云 ACR 个人版（`registry.cn-{region}.aliyuncs.com`），登录凭证从 GitHub Secrets 读取。
4. WHEN 推送 staging 镜像时 THEN 系统 SHALL 使用 `{service}:develop-{short_sha}` 格式打标签并推送，其中 `short_sha` 为当前 commit 的前 7 位。
5. WHEN 推送 production 镜像时 THEN 系统 SHALL 使用 `{service}:{version_tag}` 格式打标签（如 `eksjk-backend:v1.2.0`）并推送，同时打 `latest` 标签。
6. IF 镜像构建或推送失败 THEN 系统 SHALL 立即终止流水线并标记为失败，不进入部署步骤。

---

### 需求 3：GitOps 方式更新 Kubernetes 清单并部署到 ACK

**用户故事：** 作为运维工程师，我希望 GitHub Actions 在镜像推送成功后，自动更新 Git 仓库中的 K8s 清单文件并应用到 ACK 集群，以便所有部署变更都有 Git 记录可追溯。

#### 验收标准

1. WHEN 镜像推送成功后 THEN 系统 SHALL 在 GitHub Actions 中使用脚本（如 `sed` 或 `yq`）自动将 `k8s/staging/` 或 `k8s/prod/` 目录下 Deployment 文件中的镜像版本号替换为最新构建的镜像 tag。
2. WHEN K8s 清单文件中的镜像版本更新后 THEN 系统 SHALL 通过 `git commit` + `git push` 将变更提交回 Git 仓库，commit message 格式为 `chore(deploy): update {env} image to {tag} [skip ci]`，其中 `[skip ci]` 防止触发新一轮 CI。
3. WHEN Git 提交完成后 THEN 系统 SHALL 使用存储在 GitHub Secrets 中的 KubeConfig，通过 `kubectl apply -f k8s/{env}/` 将最新清单应用到对应 Namespace。
4. WHEN `kubectl apply` 执行后 THEN 系统 SHALL 执行 `kubectl rollout status deployment/{service} -n eksjk-{env} --timeout=120s` 等待 Pod 滚动更新完成。
5. IF `kubectl rollout status` 超时或失败 THEN 系统 SHALL 执行 `kubectl rollout undo deployment/{service} -n eksjk-{env}` 自动回滚，并将失败信息写入 GitHub Actions 日志。

---

### 需求 4：生产环境部署的人工审批门控

**用户故事：** 作为运维工程师，我希望生产环境的部署必须经过人工审批才能执行，以便防止未经确认的代码直接上线到生产环境。

#### 验收标准

1. WHEN 创建 `release/v*` Tag 触发生产部署流水线时 THEN 系统 SHALL 在构建和推送镜像完成后，暂停流水线并等待审批，不自动执行部署步骤。
2. WHEN 流水线等待审批时 THEN 系统 SHALL 通过 GitHub Actions **Environment Protection Rules** 实现审批门控，审批人在 GitHub 界面点击 "Review deployments" 确认后方可继续。
3. WHEN 配置生产审批环境时 THEN 系统 SHALL 在 GitHub Repository Settings 中创建名为 `production` 的 Environment，并配置至少 1 名 Required Reviewer。
4. WHEN 审批人批准部署后 THEN 系统 SHALL 继续执行后续的 `kubectl apply` 部署步骤。
5. IF 审批人拒绝或超时（默认 30 天）THEN 系统 SHALL 取消本次部署流水线，不执行任何 K8s 变更。

---

### 需求 5：环境变量与 K8s Secret 的管理机制

**用户故事：** 作为运维工程师，我希望 RDS 连接信息、OSS 密钥等敏感配置通过 Kubernetes Secret 注入到 Pod，而不是硬编码在镜像或代码中，以便安全地管理各环境的差异化配置。

#### 验收标准

1. WHEN 部署到 staging 或 prod 环境时 THEN 系统 SHALL 通过 Kubernetes Secret（`eksjk-{env}-secret`）存储以下敏感配置：RDS 连接信息（host/port/user/password/database）、OSS AccessKey/SecretKey/Endpoint/Bucket、JWT 签名密钥。
2. WHEN 创建 Kubernetes Secret 时 THEN 系统 SHALL 通过 `kubectl create secret generic` 命令手动创建（一次性操作），Secret 内容不得提交到 Git 仓库。
3. WHEN 后端 Pod 启动时 THEN 系统 SHALL 通过 Deployment 的 `envFrom.secretRef` 或 `env.valueFrom.secretKeyRef` 将 Secret 中的值注入为环境变量，Spring Boot 通过 `${ENV_VAR}` 读取。
4. WHEN 需要更新某个环境的配置时 THEN 运维人员 SHALL 通过 `kubectl edit secret` 或重新 `kubectl create secret --dry-run -o yaml | kubectl apply -f -` 更新 Secret，无需重新构建镜像。
5. IF K8s Secret 不存在或缺少必要字段 THEN 系统 SHALL 在 Pod 启动时因环境变量缺失而报错，阻止服务以错误配置运行。

---

### 需求 6：GitHub Actions Workflow 文件结构规范

**用户故事：** 作为开发工程师，我希望 GitHub Actions 的 workflow 文件结构清晰、职责分明，以便团队成员能够快速理解和维护 CI/CD 流程。

#### 验收标准

1. WHEN 组织 workflow 文件时 THEN 系统 SHALL 在 `.github/workflows/` 目录下创建以下独立 workflow 文件，职责分离：
   - `ci.yml`：代码检查 + 构建 + 镜像推送（所有分支触发）
   - `deploy-staging.yml`：staging 环境部署（`develop` 分支触发）
   - `deploy-prod.yml`：production 环境部署（`release/v*` tag 触发，含审批）
2. WHEN 编写 workflow 文件时 THEN 系统 SHALL 将可复用的步骤（如 Docker 登录、kubectl 配置）提取为 **Composite Action** 或通过 `workflow_call` 复用，避免重复代码。
3. WHEN workflow 中需要使用阿里云工具时 THEN 系统 SHALL 优先使用官方或社区维护的 GitHub Action（如 `aliyun/acr-login-action`），减少手动 shell 脚本。
4. WHEN 配置 workflow 的运行环境时 THEN 系统 SHALL 使用 `ubuntu-latest` 作为 runner，并通过 `actions/cache` 缓存 Maven 依赖（`~/.m2`）和 npm 依赖（`node_modules`）以加速构建。
5. WHEN workflow 中执行 shell 命令时 THEN 系统 SHALL 在关键步骤添加 `set -e` 确保命令失败时立即退出，防止错误被忽略继续执行。

---

### 需求 7：流水线执行状态通知

**用户故事：** 作为运维工程师，我希望每次流水线执行完成后能收到通知，以便及时了解部署结果并快速响应异常。

#### 验收标准

1. WHEN 任意 workflow 执行完成（成功或失败）THEN 系统 SHALL 在最后一个 step 中发送通知，通知内容包含：环境名称、触发分支/Tag、镜像版本、执行状态（✅/❌）、执行耗时、GitHub Actions 运行链接。
2. WHEN staging 部署成功 THEN 系统 SHALL 通过 GitHub Actions 的 `if: always()` 条件确保无论成功失败都发送通知。
3. WHEN production 部署成功 THEN 系统 SHALL 额外触发 GitHub Release 的自动创建，Release Notes 基于本次 Tag 与上一个 Tag 之间的 commit 信息自动生成。
4. IF workflow 中任意步骤失败 THEN 系统 SHALL 在通知中明确标注失败的步骤名称，方便快速定位问题。
