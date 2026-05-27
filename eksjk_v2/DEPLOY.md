# EKSJK V2 阿里云部署手册

> 本文档描述将 EKSJK 应用部署到阿里云的完整流程。  
> 基于 staging 环境的实际部署经验编写，为生产环境部署提供准确参考。

---

## 总览：架构与环境

### 技术架构

```
用户浏览器
    ↓ HTTP
Ingress (Nginx Ingress Controller + CLB)
    ├── /api/*      → eksjk-backend (Spring Boot, port 8080)
    ├── /actuator/* → eksjk-backend (仅 staging 开放)
    └── /*          → eksjk-frontend (Nginx, port 80)
                         └── /api/* → proxy_pass → eksjk-backend:8080

外部依赖：
    ├── 阿里云 RDS MySQL Serverless
    └── 阿里云 OSS（S3 兼容协议）
```

### 环境对照表

| 项目 | Staging 环境 | Production 环境 |
|------|-------------|----------------|
| 命名空间 | `eksjk-staging` | `eksjk-prod` |
| 域名 | `staging.eksjk.zsmm.org.cn` | `prod.eksjk.zsmm.org.cn` |
| CLB 实例 ID | `lb-uf6im0fyodg6oe38zlbbb` | `lb-uf61bz8yvbsk8z61y4mhx` |
| Spring Profile | `cloud` | `cloud` |
| 后端副本数 | 1 | 1（可按需扩展） |
| 前端副本数 | 1 | 1（可按需扩展） |
| RDS 数据库 | `eksjk_staging` | `eksjk_prod` |
| OSS Bucket | `eksjk-staging` | `eksjk-prod` |
| 镜像 Tag 格式 | `develop-{short_sha}` | `{version_tag}`（如 v1.0.0） |

### 部署流程总览

```
第一阶段：本地准备（工具安装 + 配置填写）
    ↓
第二阶段：Terraform 创建云资源（VPC / ACK / RDS / OSS / CLB / RAM）
    ↓
第三阶段：初始化 K8s 集群（Namespace / Secret / ACR 拉取凭证）
    ↓
第四阶段：手动构建镜像并推送到 ACR
    ↓
第五阶段：部署 K8s 资源（Deployment / Service / Ingress / ConfigMap）
    ↓
第六阶段：配置 DNS 解析（绑定域名）
    ↓
第七阶段：验证上线
```

---

## 第一阶段：本地准备

### 1.1 安装必要工具

```bash
# macOS 使用 Homebrew 安装
brew install terraform        # 基础设施即代码工具
brew install kubectl          # K8s 命令行工具
brew install aliyun-cli       # 阿里云 CLI

# 验证安装
terraform version             # 应显示 >= 1.5.0
kubectl version --client      # 应显示版本信息
aliyun version                # 应显示版本信息
```

### 1.2 配置阿里云 CLI

```bash
aliyun configure
# 按提示填写：
#   Access Key ID:     <你的子账号 AccessKey ID>
#   Access Key Secret: <你的子账号 AccessKey Secret>
#   Default Region ID: cn-shanghai
#   Default Output:    json
```

### 1.3 填写 Terraform 变量文件

```bash
cd eksjk_v2/terraform

# 复制示例文件
cp terraform.tfvars.example terraform.tfvars

# 编辑 terraform.tfvars，填写以下必填项：
vim terraform.tfvars
```

需要填写的内容：

```hcl
# 必填：子账号 AccessKey
access_key = "你的 AccessKey ID"
secret_key = "你的 AccessKey Secret"

# 必填：RDS 数据库密码（自定义强密码）
rds_staging_password = "Staging@Eksjk2024!"
rds_prod_password    = "Prod@Eksjk2024!"

# 其他保持默认值即可
region            = "cn-shanghai"
availability_zone = "cn-shanghai-b"
```

> ⚠️ `terraform.tfvars` 已加入 `.gitignore`，**不会提交到 Git**，请妥善保管。

---

## 第二阶段：Terraform 创建云资源

### 2.0 【前置】授权 ACK 服务角色（首次使用必做）

> ⚠️ 如果跳过此步骤，`terraform apply` 时会报错：  
> `EntityNotExist.Role: The role not exists: acs:ram::xxxx:role/aliyuncsdefaultrole`

**操作步骤（仅需做一次，永久生效）：**

1. 使用**主账号**（或有 RAM 管理权限的子账号）登录 [阿里云控制台](https://console.aliyun.com)
2. 在顶部搜索框搜索 **"容器服务"** 并进入
3. 首次进入时会弹出 **"云资源访问授权"** 对话框，点击 **"前往授权"**
4. 在 RAM 授权页面点击 **"同意授权"**

**如果没有弹出授权对话框，手动访问以下链接完成授权：**

```
https://ram.console.aliyun.com/#/role/authorize?request={"ReturnUrl":"https://cs.console.aliyun.com/","Services":[{"Service":"CS","Roles":[{"RoleName":"AliyunCSManagedKubernetesRole","TemplateId":"AliyunCSManagedKubernetesRole"},{"RoleName":"AliyunCSDefaultRole","TemplateId":"Default"},{"RoleName":"AliyunCSClusterRole","TemplateId":"Cluster"}]}]}
```

**验证角色是否已创建：**

```bash
aliyun ram GetRole --RoleName AliyunCSDefaultRole
# 预期输出：包含 RoleId 的 JSON
```

### 2.1 初始化并创建资源

```bash
cd eksjk_v2/terraform

# 初始化（首次执行会下载 alicloud provider，需要几分钟）
terraform init

# 预览将要创建的资源
terraform plan

# 创建云资源（约需 15～30 分钟）
terraform apply
# 输入 yes 确认执行
```

### 2.2 记录关键输出值

```bash
# 查看并记录以下输出值（后续步骤需要用到）
terraform output rds_connection_string    # RDS 内网地址
terraform output clb_staging_ip           # staging 公网 IP
terraform output clb_prod_ip              # prod 公网 IP
terraform output clb_staging_id           # staging CLB 实例 ID
terraform output clb_prod_id              # prod CLB 实例 ID
```

> 📋 建议将以上输出值保存到本地安全的记事本中。

---

## 第三阶段：初始化 K8s 集群

### 3.1 获取 ACK KubeConfig

```bash
# 获取集群 ID
ACK_CLUSTER_ID=$(cd eksjk_v2/terraform && terraform output -raw ack_cluster_id)

# 通过阿里云 CLI 获取 KubeConfig
aliyun cs GET /k8s/${ACK_CLUSTER_ID}/user_config \
  --output json \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['config'])" \
  > ~/.kube/eksjk-config

# 后续所有 kubectl 命令都使用此配置
export KUBECONFIG=~/.kube/eksjk-config

# 验证集群连接
kubectl get nodes
# 预期输出：1 个节点处于 Ready 状态
```

> 💡 建议在 `~/.zshrc` 或 `~/.bashrc` 中添加别名：
> ```bash
> alias kack='KUBECONFIG=~/.kube/eksjk-config kubectl'
> ```

### 3.2 创建 Namespace 和资源配额

```bash
cd eksjk_v2

# Staging 环境
kubectl apply -f k8s/staging/namespace.yaml

# Production 环境
kubectl apply -f k8s/prod/namespace.yaml

# 验证
kubectl get namespaces | grep eksjk
# 预期输出：eksjk-staging 和 eksjk-prod 均为 Active
```

### 3.3 创建 ACR 镜像拉取凭证

```bash
ACR_REGISTRY="crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com"
ACR_USERNAME="你的 ACR 登录用户名"
ACR_PASSWORD="你的 ACR 登录密码"

# staging namespace
kubectl create secret docker-registry acr-secret \
  --namespace eksjk-staging \
  --docker-server=${ACR_REGISTRY} \
  --docker-username=${ACR_USERNAME} \
  --docker-password=${ACR_PASSWORD}

# prod namespace
kubectl create secret docker-registry acr-secret \
  --namespace eksjk-prod \
  --docker-server=${ACR_REGISTRY} \
  --docker-username=${ACR_USERNAME} \
  --docker-password=${ACR_PASSWORD}
```

### 3.4 创建 K8s Secret（敏感配置注入）

Secret 包含以下环境变量，通过 `envFrom.secretRef` 注入到后端 Pod：

| 环境变量 | 说明 | 示例值 |
|---------|------|--------|
| `SPRING_DATASOURCE_URL` | JDBC 连接串 | `jdbc:mysql://rm-xxx.mysql.rds.aliyuncs.com:3306/eksjk_staging?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai` |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | `eksjk_staging_user` |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | `Staging@Eksjk2024!` |
| `EKSJK_UPLOAD_S3_ENDPOINT` | OSS 端点（内网） | `https://oss-cn-shanghai-internal.aliyuncs.com` |
| `EKSJK_UPLOAD_S3_BUCKET` | OSS Bucket 名称 | `eksjk-staging` |
| `EKSJK_UPLOAD_S3_ACCESS_KEY` | OSS AccessKey ID | `LTAI5txxxxxxxxxx` |
| `EKSJK_UPLOAD_S3_SECRET_KEY` | OSS AccessKey Secret | `CRfh7Wxxxxxxxxxx` |
| `EKSJK_UPLOAD_S3_REGION` | OSS 区域 | `cn-shanghai` |
| `EKSJK_JWT_SECRET` | JWT 签名密钥 | 随机生成的 Base64 字符串 |

> ⚠️ **重要**：`EKSJK_UPLOAD_S3_REGION` 必须设置为 `cn-shanghai`，否则 S3 客户端会使用默认的 `us-east-1`，导致 OSS 访问失败。

**方式一：使用脚本创建（推荐）**

```bash
cd eksjk_v2/scripts/k8s-secrets

# 复制示例文件并填写真实值
cp .env.staging.example .env.staging
vim .env.staging

# 执行脚本创建 staging Secret
bash create-staging-secret.sh

# 验证
kubectl get secret eksjk-staging-secret -n eksjk-staging
```

**方式二：手动创建**

```bash
kubectl create secret generic eksjk-staging-secret \
  --namespace=eksjk-staging \
  --from-literal=SPRING_DATASOURCE_URL="jdbc:mysql://rm-xxx:3306/eksjk_staging?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=Asia/Shanghai" \
  --from-literal=SPRING_DATASOURCE_USERNAME="eksjk_staging_user" \
  --from-literal=SPRING_DATASOURCE_PASSWORD="你的数据库密码" \
  --from-literal=EKSJK_UPLOAD_S3_ENDPOINT="https://oss-cn-shanghai-internal.aliyuncs.com" \
  --from-literal=EKSJK_UPLOAD_S3_BUCKET="eksjk-staging" \
  --from-literal=EKSJK_UPLOAD_S3_ACCESS_KEY="你的OSS AK ID" \
  --from-literal=EKSJK_UPLOAD_S3_SECRET_KEY="你的OSS AK Secret" \
  --from-literal=EKSJK_UPLOAD_S3_REGION="cn-shanghai" \
  --from-literal=EKSJK_JWT_SECRET="$(openssl rand -base64 64)" \
  --dry-run=client -o yaml | kubectl apply -f -
```

> ⚠️ **OSS AKSK 说明**：OSS 使用独立的 RAM 子账号 AccessKey，需要具有对应 Bucket 的读写权限。不要与 Terraform 的 AccessKey 混用。

---

## 第四阶段：手动构建镜像并推送到 ACR

> 当前项目采用手动构建方式，后续可接入 GitHub Actions 实现自动化 CI/CD。

### 4.1 ACR 登录

```bash
ACR_REGISTRY="crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com"

docker login ${ACR_REGISTRY}
# 输入 ACR 用户名和密码
```

### 4.2 构建后端镜像

```bash
cd eksjk_v2/eksjk-backend

# 1. Maven 构建
mvn clean package -DskipTests -q

# 2. 获取当前 commit hash（用作镜像 tag）
SHORT_SHA=$(git rev-parse --short HEAD)
IMAGE_TAG="develop-${SHORT_SHA}"

# 3. 构建 Docker 镜像（注意：必须指定 linux/amd64 平台，ACK 节点是 x86 架构）
docker build --platform linux/amd64 \
  -t ${ACR_REGISTRY}/eksjk/eksjk-backend:${IMAGE_TAG} .

# 4. 推送到 ACR
docker push ${ACR_REGISTRY}/eksjk/eksjk-backend:${IMAGE_TAG}

echo "后端镜像: ${ACR_REGISTRY}/eksjk/eksjk-backend:${IMAGE_TAG}"
```

### 4.3 构建前端镜像

```bash
cd eksjk_v2/eksjk-frontend

# 1. 安装依赖并构建
npm install
npm run build

# 2. 构建 Docker 镜像
SHORT_SHA=$(git rev-parse --short HEAD)
IMAGE_TAG="develop-${SHORT_SHA}"

docker build --platform linux/amd64 \
  -t ${ACR_REGISTRY}/eksjk/eksjk-frontend:${IMAGE_TAG} .

# 3. 推送到 ACR
docker push ${ACR_REGISTRY}/eksjk/eksjk-frontend:${IMAGE_TAG}

echo "前端镜像: ${ACR_REGISTRY}/eksjk/eksjk-frontend:${IMAGE_TAG}"
```

### 4.4 生产环境镜像

生产环境使用版本号作为 tag（如 `v1.0.0`），而非 commit hash：

```bash
VERSION="v1.0.0"

# 后端
docker tag ${ACR_REGISTRY}/eksjk/eksjk-backend:develop-${SHORT_SHA} \
  ${ACR_REGISTRY}/eksjk/eksjk-backend:${VERSION}
docker push ${ACR_REGISTRY}/eksjk/eksjk-backend:${VERSION}

# 前端
docker tag ${ACR_REGISTRY}/eksjk/eksjk-frontend:develop-${SHORT_SHA} \
  ${ACR_REGISTRY}/eksjk/eksjk-frontend:${VERSION}
docker push ${ACR_REGISTRY}/eksjk/eksjk-frontend:${VERSION}
```

---

## 第五阶段：部署 K8s 资源

### 5.1 更新 Deployment 中的镜像版本

部署前需要更新 deployment.yaml 中的镜像 tag：

```bash
# Staging 环境 — 使用 develop-{short_sha} 格式
# 编辑 k8s/staging/backend/deployment.yaml 中的 image 字段
# 编辑 k8s/staging/frontend/deployment.yaml 中的 image 字段

# Production 环境 — 使用版本号格式
# 编辑 k8s/prod/backend/deployment.yaml 中的 image 字段
# 编辑 k8s/prod/frontend/deployment.yaml 中的 image 字段
```

### 5.2 部署 Staging 环境

```bash
cd eksjk_v2

# 部署前端 ConfigMap（Nginx 配置）
kubectl apply -f k8s/staging/frontend/configmap.yaml

# 部署后端
kubectl apply -f k8s/staging/backend/deployment.yaml
kubectl apply -f k8s/staging/backend/service.yaml

# 部署前端
kubectl apply -f k8s/staging/frontend/deployment.yaml
kubectl apply -f k8s/staging/frontend/service.yaml

# 部署 Ingress
kubectl apply -f k8s/staging/frontend/ingress.yaml

# 等待 Pod 就绪
kubectl wait --for=condition=ready pod -l app=eksjk-backend -n eksjk-staging --timeout=120s
kubectl wait --for=condition=ready pod -l app=eksjk-frontend -n eksjk-staging --timeout=60s
```

### 5.3 部署 Production 环境

```bash
cd eksjk_v2

# 1. 创建 Namespace（如果尚未创建）
kubectl apply -f k8s/prod/namespace.yaml

# 2. 创建 ACR 拉取凭证（如果尚未创建）
kubectl create secret docker-registry acr-secret \
  --namespace eksjk-prod \
  --docker-server=${ACR_REGISTRY} \
  --docker-username=${ACR_USERNAME} \
  --docker-password=${ACR_PASSWORD}

# 3. 创建 Secret（参考第三阶段 3.4 节，替换为 prod 环境的值）
# ⚠️ 生产环境的 JWT_SECRET 必须与 staging 不同！

# 4. 部署前端 ConfigMap
kubectl apply -f k8s/prod/frontend/configmap.yaml

# 5. 部署后端
kubectl apply -f k8s/prod/backend/deployment.yaml
kubectl apply -f k8s/prod/backend/service.yaml

# 6. 部署前端
kubectl apply -f k8s/prod/frontend/deployment.yaml
kubectl apply -f k8s/prod/frontend/service.yaml

# 7. 部署 Ingress
kubectl apply -f k8s/prod/frontend/ingress.yaml

# 8. 等待 Pod 就绪
kubectl wait --for=condition=ready pod -l app=eksjk-backend -n eksjk-prod --timeout=120s
kubectl wait --for=condition=ready pod -l app=eksjk-frontend -n eksjk-prod --timeout=60s
```

### 5.4 更新已有部署（日常发布）

```bash
# 方式一：更新 deployment.yaml 中的镜像 tag 后 apply
kubectl apply -f k8s/staging/backend/deployment.yaml

# 方式二：直接更新镜像（不修改 YAML 文件）
kubectl set image deployment/eksjk-backend \
  backend=${ACR_REGISTRY}/eksjk/eksjk-backend:develop-${SHORT_SHA} \
  -n eksjk-staging

# 方式三：仅重启 Pod（配置变更后使用）
kubectl rollout restart deployment/eksjk-backend -n eksjk-staging

# 查看滚动更新状态
kubectl rollout status deployment/eksjk-backend -n eksjk-staging --timeout=120s
```

---

## 第六阶段：配置 DNS 解析

登录域名服务商控制台（`zsmm.org.cn` 的 DNS 管理页面），添加以下 A 记录：

| 主机记录 | 记录类型 | 记录值 | TTL |
|---------|---------|--------|-----|
| `staging.eksjk` | A | CLB Staging 公网 IP | 600 |
| `prod.eksjk` | A | CLB Prod 公网 IP | 600 |

```bash
# 查看 CLB 公网 IP
cd eksjk_v2/terraform
terraform output clb_staging_ip
terraform output clb_prod_ip
```

DNS 生效后验证：

```bash
nslookup staging.eksjk.zsmm.org.cn
nslookup prod.eksjk.zsmm.org.cn
```

> 💡 如果本地 DNS 缓存导致解析不更新，可执行：
> ```bash
> sudo dscacheutil -flushcache && sudo killall -HUP mDNSResponder
> ```

---

## 第七阶段：验证上线

### 7.1 检查 Pod 运行状态

```bash
# Staging 环境
kubectl get pods -n eksjk-staging -o wide
# 预期：eksjk-backend 和 eksjk-frontend 均为 Running，READY 1/1

# Production 环境
kubectl get pods -n eksjk-prod -o wide
```

### 7.2 检查服务访问

```bash
# Staging 环境
curl -s http://staging.eksjk.zsmm.org.cn/           # 前端页面（应返回 HTML）
curl -s http://staging.eksjk.zsmm.org.cn/api/actuator/health  # 后端健康检查

# Production 环境
curl -s http://prod.eksjk.zsmm.org.cn/
curl -s http://prod.eksjk.zsmm.org.cn/api/actuator/health
```

### 7.3 查看 Pod 日志

```bash
# 后端日志（cloud profile 只输出到控制台，便于 kubectl logs 查看）
kubectl logs -n eksjk-staging deployment/eksjk-backend --tail=100
kubectl logs -n eksjk-staging deployment/eksjk-backend -f  # 实时跟踪

# 前端日志
kubectl logs -n eksjk-staging deployment/eksjk-frontend --tail=50
```

### 7.4 功能验证清单

| 功能 | 验证方式 | 预期结果 |
|------|---------|---------|
| 页面访问 | 浏览器打开域名 | 正常显示登录页 |
| 用户登录 | 使用系统管理员账号登录 | 登录成功，进入主页 |
| 文件上传 | 上传影像资料 | 上传成功，文件存储到 OSS |
| 数据库读写 | 新增/查询数据 | 操作成功 |
| 健康检查 | 访问 /api/actuator/health | 返回 `{"status":"UP"}` |

---

## 附录 A：K8s 资源清单

### Staging 环境文件结构

```
k8s/staging/
├── namespace.yaml              # Namespace + ResourceQuota
├── backend/
│   ├── deployment.yaml         # 后端 Deployment（1 副本）
│   └── service.yaml            # 后端 ClusterIP Service (8080)
└── frontend/
    ├── configmap.yaml          # Nginx 配置（含 API 代理规则）
    ├── deployment.yaml         # 前端 Deployment（1 副本）
    ├── service.yaml            # 前端 ClusterIP Service (80)
    └── ingress.yaml            # Ingress 路由规则
```

### Production 环境文件结构

```
k8s/prod/
├── namespace.yaml              # Namespace + ResourceQuota
├── backend/
│   ├── deployment.yaml         # 后端 Deployment（1 副本）
│   └── service.yaml            # 后端 ClusterIP Service (8080)
└── frontend/
    ├── configmap.yaml          # Nginx 配置（含 API 代理规则）
    ├── deployment.yaml         # 前端 Deployment（1 副本）
    ├── service.yaml            # 前端 ClusterIP Service (80)
    └── ingress.yaml            # Ingress 路由规则
```

### 关键配置说明

| 配置项 | Staging | Production | 说明 |
|--------|---------|------------|------|
| 后端 CPU requests | 200m | 250m | 生产环境略高 |
| 后端 Memory requests | 1Gi | 512Mi | 按需调整 |
| 后端 CPU limits | 1000m | 1000m | 最大 1 核 |
| 后端 Memory limits | 1Gi | 1Gi | 最大 1GB |
| 前端 CPU requests | 50m | 50m | Nginx 资源消耗低 |
| 前端 Memory requests | 128Mi | 64Mi | 按需调整 |
| Ingress proxy-body-size | 100m | 100m | 文件上传大小限制 |
| Liveness 初始延迟 | 60s | 60s | Spring Boot 启动时间 |
| Readiness 初始延迟 | 30s | 30s | 等待应用就绪 |

---

## 附录 B：后端配置说明

### Spring Profile 机制

| Profile | 用途 | 数据库 | 文件存储 |
|---------|------|--------|--------|
| `dev` | 本地开发 | 本地 MySQL | 本地 MinIO |
| `local` | 本地开发（备用） | 本地 MySQL | 本地 MinIO |
| `cloud` | 阿里云部署（staging + prod 均使用此 profile） | RDS MySQL（通过 Secret 注入） | 阿里云 OSS（S3 兼容） |
| `prod` | 备用（当前未使用，配置与 cloud 一致） | 同 cloud | 同 cloud |

> ⚠️ **重要**：staging 和 production 环境均使用 `cloud` profile（`SPRING_PROFILES_ACTIVE=cloud`），
> 通过 K8s Secret 中不同的环境变量值来区分环境（不同的数据库、OSS Bucket 等）。

### cloud Profile 配置文件

`application-cloud.yml` 中所有敏感配置通过环境变量注入：

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}

eksjk:
  upload:
    storage-type: s3
    s3:
      endpoint: ${EKSJK_UPLOAD_S3_ENDPOINT}
      region: ${EKSJK_UPLOAD_S3_REGION:cn-shanghai}
      bucket: ${EKSJK_UPLOAD_S3_BUCKET}
      access-key: ${EKSJK_UPLOAD_S3_ACCESS_KEY}
      secret-key: ${EKSJK_UPLOAD_S3_SECRET_KEY}
```

### S3 兼容性说明

- **阿里云 OSS** 使用 Virtual-Hosted Style 访问（`pathStyleAccessEnabled = false`）
- **MinIO** 使用 Path Style 访问（`pathStyleAccessEnabled = true`）
- `S3Config.java` 会根据 endpoint 自动判断访问方式：
  - 包含 `aliyuncs.com` 或 `amazonaws.com` → Virtual-Hosted Style
  - 其他（如 localhost MinIO）→ Path Style
- 也可通过 `eksjk.upload.s3.path-style` 配置项手动覆盖

---

## 附录 C：常用运维命令

### 日常操作

```bash
# 查看所有 Pod 状态
kubectl get pods -n eksjk-staging -o wide
kubectl get pods -n eksjk-prod -o wide

# 查看后端日志
kubectl logs -n eksjk-staging deployment/eksjk-backend --tail=100
kubectl logs -n eksjk-staging deployment/eksjk-backend -f  # 实时跟踪

# 进入后端容器调试
kubectl exec -it -n eksjk-staging deployment/eksjk-backend -- bash

# 查看 Secret 字段列表（不显示值）
kubectl get secret eksjk-staging-secret -n eksjk-staging -o jsonpath='{.data}' | \
  python3 -c "import sys,json; [print(f'  - {k}') for k in sorted(json.load(sys.stdin).keys())]"

# 查看 Secret 某个字段的值
kubectl get secret eksjk-staging-secret -n eksjk-staging \
  -o jsonpath='{.data.EKSJK_UPLOAD_S3_ACCESS_KEY}' | base64 -d
```

### 更新 K8s Secret

```bash
# 方式一：使用脚本（推荐）
cd eksjk_v2/scripts/k8s-secrets
vim .env.staging  # 修改配置值
bash create-staging-secret.sh
kubectl rollout restart deployment/eksjk-backend -n eksjk-staging

# 方式二：patch 单个字段
kubectl patch secret eksjk-staging-secret -n eksjk-staging \
  --type='json' \
  -p='[{"op":"replace","path":"/data/EKSJK_UPLOAD_S3_ACCESS_KEY","value":"'$(echo -n "新的AK" | base64)'"}]'
kubectl rollout restart deployment/eksjk-backend -n eksjk-staging
```

### 回滚操作

```bash
# 回滚到上一个版本
kubectl rollout undo deployment/eksjk-backend -n eksjk-staging

# 查看回滚历史
kubectl rollout history deployment/eksjk-backend -n eksjk-staging

# 回滚到指定版本
kubectl rollout undo deployment/eksjk-backend -n eksjk-staging --to-revision=2
```

### RDS 白名单

如果后端 Pod 无法连接 RDS，需要将 ACK 节点 IP 加入 RDS 白名单：

```bash
# 查看 ACK 节点内网 IP
kubectl get nodes -o wide
# 将 INTERNAL-IP 加入 RDS 白名单（阿里云控制台 → RDS → 数据安全性 → 白名单设置）
```

### 销毁所有云资源

```bash
# ⚠️ 危险操作！会删除所有云资源，包括 RDS 数据！
cd eksjk_v2/terraform
terraform destroy
# 输入 yes 确认
```

---

## 附录 D：生产部署检查清单

在部署生产环境前，请逐项确认：

### 基础设施

- [ ] RDS 白名单已添加 ACK 节点 IP
- [ ] OSS Bucket `eksjk-prod` 已创建
- [ ] OSS RAM 子账号已授权 `eksjk-prod` Bucket 读写权限
- [ ] CLB-prod 实例正常运行
- [ ] DNS 解析 `prod.eksjk.zsmm.org.cn` 已配置并生效

### K8s 资源

- [ ] `eksjk-prod` Namespace 已创建
- [ ] `acr-secret` 镜像拉取凭证已创建
- [ ] `eksjk-prod-secret` 已创建，包含所有 10 个环境变量（见 3.4 节）
- [ ] Secret 中 `EKSJK_UPLOAD_S3_REGION` 设置为 `cn-shanghai`
- [ ] Secret 中 `EKSJK_JWT_SECRET` 与 staging 不同
- [ ] prod ConfigMap 中 `server_name` 已更新为 `prod.eksjk.zsmm.org.cn`

### 镜像与部署

- [ ] 后端镜像已构建并推送到 ACR（使用版本号 tag）
- [ ] 前端镜像已构建并推送到 ACR（使用版本号 tag）
- [ ] `k8s/prod/backend/deployment.yaml` 中镜像 tag 已更新
- [ ] `k8s/prod/frontend/deployment.yaml` 中镜像 tag 已更新
- [ ] Ingress 中 CLB 实例 ID 正确（`lb-uf61bz8yvbsk8z61y4mhx`）

### 功能验证

- [ ] 页面可正常访问
- [ ] 用户可正常登录
- [ ] 文件上传功能正常
- [ ] 数据库读写正常
- [ ] 健康检查接口返回 UP

---

## 附录 E：费用提醒

| 资源 | 月费用估算 |
|------|-----------|
| ACK Worker 节点（ecs.g7a.xlarge 4C16G） | ≈ ¥694（按量 ¥0.964/时） |
| RDS MySQL Serverless（0.5~4 RCU） | ≈ ¥50～200 |
| OSS 存储（~60GB） | ≈ ¥15 |
| CLB-staging + CLB-prod | ≈ ¥40 |
| ACR 个人版 | 免费 |
| **月度合计**（低负载） | **≈ ¥400** |

> 💡 RDS Serverless 按实际 RCU 使用量按秒计费，空闲时自动缩容到 0.5 RCU（约 ¥50/月）。  
> 💡 不使用时可通过 `terraform destroy` 销毁资源以节省费用。

---

## 附录 F：Staging 环境已知配置（当前状态）

> 以下为 staging 环境当前实际运行的配置，供生产部署参考。

| 项目 | 当前值 |
|------|--------|
| 后端镜像 | `crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com/eksjk/eksjk-backend:develop-ff7c071` |
| 前端镜像 | `crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com/eksjk/eksjk-frontend:develop-1baab55` |
| Ingress IP | `8.133.198.117` |
| ACK 节点 | `cn-shanghai.172.16.0.88`（Alibaba Cloud Linux 3, K8s v1.33.3） |
| Spring Profile | `cloud` |
| OSS Endpoint | `https://oss-cn-shanghai-internal.aliyuncs.com`（内网） |
| OSS Bucket | `eksjk-staging` |
| OSS Region | `cn-shanghai` |
| Secret 名称 | `eksjk-staging-secret`（包含 10 个环境变量） |
| 域名 | `staging.eksjk.zsmm.org.cn` |
| CLB 实例 ID | `lb-uf6im0fyodg6oe38zlbbb` |

---

## 附录 G：Staging 部署踩坑记录

> 以下是 staging 环境部署过程中遇到的问题和解决方案，为生产部署提供经验参考。

### 踩坑 1：缺少 `application-cloud.yml` 导致 S3 配置不生效

**现象**：文件上传失败，后端日志中没有 S3 初始化信息。

**原因**：Deployment 中设置了 `SPRING_PROFILES_ACTIVE=cloud`，但项目中没有 `application-cloud.yml` 文件。虽然环境变量可以覆盖默认配置，但缺少显式的 cloud profile 配置文件导致 S3 相关属性依赖默认值。

**解决**：创建 `application-cloud.yml`，显式声明所有通过环境变量注入的配置项。

### 踩坑 2：S3 pathStyleAccess 与阿里云 OSS 不兼容

**现象**：S3 客户端初始化成功，但上传文件时报错。

**原因**：`S3Config.java` 中硬编码了 `pathStyleAccessEnabled(true)`，这是为 MinIO 设置的。阿里云 OSS 只支持 Virtual-Hosted Style 访问，不支持 Path Style。

**解决**：修改 `S3Config.java`，根据 endpoint 自动判断访问方式：
- 包含 `aliyuncs.com` 或 `amazonaws.com` → Virtual-Hosted Style（`pathStyleAccessEnabled = false`）
- 其他（如 localhost MinIO）→ Path Style（`pathStyleAccessEnabled = true`）
- 也可通过 `eksjk.upload.s3.path-style` 配置项手动覆盖

### 踩坑 3：K8s Secret 缺少 `EKSJK_UPLOAD_S3_REGION`

**现象**：S3 客户端使用默认 region `us-east-1`，导致 OSS 签名验证失败。

**原因**：创建 Secret 时遗漏了 `EKSJK_UPLOAD_S3_REGION` 字段。

**解决**：补充 `EKSJK_UPLOAD_S3_REGION=cn-shanghai` 到 Secret 中。

> ⚠️ 此字段已添加到 Secret 创建脚本和 `.env.*.example` 示例文件中。

### 踩坑 4：OSS AKSK 权限不足或错误

**现象**：文件上传返回 403 Forbidden 或 InvalidAccessKeyId。

**原因**：使用了错误的 AccessKey，或 RAM 子账号没有对应 Bucket 的读写权限。

**解决**：
1. 确认使用的是 OSS 专用 RAM 子账号的 AccessKey（非 Terraform 的 AccessKey）
2. 在阿里云 RAM 控制台确认子账号已授权 `AliyunOSSFullAccess` 或对应 Bucket 的自定义策略
3. 更新 Secret 后必须重启 Pod：`kubectl rollout restart deployment/eksjk-backend -n eksjk-staging`

### 踩坑 5：Logback 日志在容器中不输出

**现象**：`kubectl logs` 看不到应用日志，只有 Spring Boot banner。

**原因**：`logback-spring.xml` 中 cloud profile 配置了文件 Appender（`INFO_FILE`、`ERROR_FILE`），但容器中文件系统是临时的，且 Appender 引用问题导致日志不输出。

**解决**：cloud profile 的 logback 配置只保留 `CONSOLE` Appender，不使用文件 Appender。容器环境下日志通过 `kubectl logs` 查看即可。

### 踩坑 6：RDS 白名单未添加 ACK 节点 IP

**现象**：后端 Pod 启动后无法连接数据库，日志报 `Communications link failure`。

**原因**：RDS 默认白名单只包含 `127.0.0.1`，ACK 节点的内网 IP 不在白名单中。

**解决**：
```bash
# 查看 ACK 节点内网 IP
kubectl get nodes -o wide
# 将 INTERNAL-IP（如 172.16.0.88）加入 RDS 白名单
# 阿里云控制台 → RDS → 数据安全性 → 白名单设置
```

> 💡 建议将整个 VPC 网段（如 `172.16.0.0/16`）加入白名单，避免节点 IP 变化后需要重新配置。

### 踩坑 7：DNS 解析缓存导致域名不生效

**现象**：修改了 DNS A 记录后，本地仍然解析到旧 IP。

**原因**：本地 DNS 缓存和 ISP DNS 缓存导致解析延迟。

**解决**：
```bash
# macOS 清除 DNS 缓存
sudo dscacheutil -flushcache && sudo killall -HUP mDNSResponder

# 验证解析结果
nslookup staging.eksjk.zsmm.org.cn
dig staging.eksjk.zsmm.org.cn
```

### 踩坑 8：前端 Nginx ConfigMap 中 server_name 未更新

**现象**：前端页面可以访问，但某些功能异常。

**原因**：ConfigMap 中的 `server_name` 还是模板默认值（如 `eksjk.example.com`），未更新为实际域名。

**解决**：更新 ConfigMap 中的 `server_name` 为实际域名，然后重新 apply 并重启前端 Pod。

---

## 附录 H：生产环境部署快速参考

> 假设 Terraform 资源已创建、ACK 集群已就绪，以下是生产环境部署的精简步骤。

### 一键部署脚本（参考）

```bash
#!/bin/bash
# 生产环境部署快速参考脚本
# 使用前请确认所有前置条件已满足（见附录 D 检查清单）

set -e
export KUBECONFIG=~/.kube/eksjk-config
ACR_REGISTRY="crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com"
VERSION="v1.0.0"  # 修改为实际版本号

echo "=== 第 1 步：构建后端镜像 ==="
cd eksjk_v2/eksjk-backend
mvn clean package -DskipTests -q
docker build --platform linux/amd64 -t ${ACR_REGISTRY}/eksjk/eksjk-backend:${VERSION} .
docker push ${ACR_REGISTRY}/eksjk/eksjk-backend:${VERSION}

echo "=== 第 2 步：构建前端镜像 ==="
cd ../eksjk-frontend
npm install && npm run build
docker build --platform linux/amd64 -t ${ACR_REGISTRY}/eksjk/eksjk-frontend:${VERSION} .
docker push ${ACR_REGISTRY}/eksjk/eksjk-frontend:${VERSION}

echo "=== 第 3 步：更新 deployment.yaml 中的镜像 tag ==="
cd ..
# 手动编辑 k8s/prod/backend/deployment.yaml 和 k8s/prod/frontend/deployment.yaml
# 将 image tag 更新为 ${VERSION}

echo "=== 第 4 步：创建 Namespace 和 Secret ==="
kubectl apply -f k8s/prod/namespace.yaml
cd scripts/k8s-secrets
cp .env.prod.example .env.prod  # 首次部署需要填写真实值
# vim .env.prod
bash create-prod-secret.sh

echo "=== 第 5 步：创建 ACR 拉取凭证 ==="
kubectl create secret docker-registry acr-secret \
  --namespace eksjk-prod \
  --docker-server=${ACR_REGISTRY} \
  --docker-username="你的ACR用户名" \
  --docker-password="你的ACR密码" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "=== 第 6 步：部署 K8s 资源 ==="
cd ../../
kubectl apply -f k8s/prod/frontend/configmap.yaml
kubectl apply -f k8s/prod/backend/deployment.yaml
kubectl apply -f k8s/prod/backend/service.yaml
kubectl apply -f k8s/prod/frontend/deployment.yaml
kubectl apply -f k8s/prod/frontend/service.yaml
kubectl apply -f k8s/prod/frontend/ingress.yaml

echo "=== 第 7 步：等待 Pod 就绪 ==="
kubectl wait --for=condition=ready pod -l app=eksjk-backend -n eksjk-prod --timeout=120s
kubectl wait --for=condition=ready pod -l app=eksjk-frontend -n eksjk-prod --timeout=60s

echo "=== 部署完成！==="
kubectl get pods -n eksjk-prod -o wide
echo "请配置 DNS 解析后访问: http://prod.eksjk.zsmm.org.cn"
```

### 日常发布流程（代码更新后）

```bash
export KUBECONFIG=~/.kube/eksjk-config
ACR_REGISTRY="crpi-s9dswjc6u3l5d0to.cn-shanghai.personal.cr.aliyuncs.com"
VERSION="v1.0.1"  # 新版本号

# 1. 构建并推送新镜像
cd eksjk_v2/eksjk-backend
mvn clean package -DskipTests -q
docker build --platform linux/amd64 -t ${ACR_REGISTRY}/eksjk/eksjk-backend:${VERSION} .
docker push ${ACR_REGISTRY}/eksjk/eksjk-backend:${VERSION}

# 2. 更新 deployment 镜像 tag 并 apply
# 编辑 k8s/prod/backend/deployment.yaml 中的 image tag
kubectl apply -f k8s/prod/backend/deployment.yaml

# 3. 等待滚动更新完成
kubectl rollout status deployment/eksjk-backend -n eksjk-prod --timeout=120s

# 4. 验证
curl -s http://prod.eksjk.zsmm.org.cn/api/actuator/health
```
