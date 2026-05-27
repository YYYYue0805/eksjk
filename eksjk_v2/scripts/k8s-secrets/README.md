# K8s Secret 初始化脚本

本目录包含 EKSJK 项目 staging / prod 两个环境的 Kubernetes Secret 初始化脚本。

---

## 目录结构

```
scripts/k8s-secrets/
├── README.md                  # 本文档
├── create-staging-secret.sh   # 创建/更新 staging Secret
├── create-prod-secret.sh      # 创建/更新 prod Secret
├── .env.staging.example       # staging 环境变量示例（可提交 Git）
├── .env.prod.example          # prod 环境变量示例（可提交 Git）
├── .env.staging               # staging 真实配置（已加入 .gitignore，禁止提交）
└── .env.prod                  # prod 真实配置（已加入 .gitignore，禁止提交）
```

---

## Secret 字段说明

两个环境的 Secret（`eksjk-staging-secret` / `eksjk-prod-secret`）包含以下字段：

| 字段名 | 说明 | 示例值 |
|--------|------|--------|
| `SPRING_DATASOURCE_URL` | RDS MySQL JDBC 连接串（含数据库名） | `jdbc:mysql://rm-xxx.mysql.rds.aliyuncs.com:3306/eksjk_staging?...` |
| `SPRING_DATASOURCE_USERNAME` | RDS 数据库账号 | `eksjk_staging_user` |
| `SPRING_DATASOURCE_PASSWORD` | RDS 数据库密码 | `your_password` |
| `EKSJK_UPLOAD_S3_ENDPOINT` | OSS Endpoint（推荐内网域名） | `https://oss-cn-shanghai-internal.aliyuncs.com` |
| `EKSJK_UPLOAD_S3_BUCKET` | OSS Bucket 名称 | `eksjk-staging` |
| `EKSJK_UPLOAD_S3_ACCESS_KEY` | OSS RAM 子账号 AccessKey ID | `LTAI5t...` |
| `EKSJK_UPLOAD_S3_SECRET_KEY` | OSS RAM 子账号 AccessKey Secret | `xxx...` |
| `EKSJK_JWT_SECRET` | JWT 签名密钥（≥32字符） | `openssl rand -base64 64` 生成 |

> **注意：** staging 和 prod 的 RDS 账号、OSS Bucket、JWT 密钥必须各自独立，不得共用。

---

## 前置条件

1. 已安装并配置 `kubectl`，且能访问目标 ACK 集群：
   ```bash
   kubectl cluster-info
   ```
2. 已通过 Terraform 创建好阿里云资源（RDS、OSS、ACK），并获取到连接信息：
   ```bash
   cd terraform/
   terraform output rds_connection_host
   terraform output oss_endpoint
   ```
3. 本地已有对应环境的 `.env.staging` 或 `.env.prod` 配置文件（从 `.example` 复制并填写）。

---

## 首次初始化步骤

### Staging 环境

```bash
# 1. 复制示例文件
cp scripts/k8s-secrets/.env.staging.example scripts/k8s-secrets/.env.staging

# 2. 编辑并填写真实值
vim scripts/k8s-secrets/.env.staging

# 3. 执行脚本（自动创建 Namespace 和 Secret）
bash scripts/k8s-secrets/create-staging-secret.sh
```

### Production 环境

```bash
# 1. 复制示例文件
cp scripts/k8s-secrets/.env.prod.example scripts/k8s-secrets/.env.prod

# 2. 编辑并填写真实值（生产密钥必须与 staging 不同）
vim scripts/k8s-secrets/.env.prod

# 3. 执行脚本（会要求二次确认）
bash scripts/k8s-secrets/create-prod-secret.sh
```

---

## 更新 Secret

当需要轮换密钥或修改配置时，直接修改 `.env.staging` / `.env.prod` 后重新执行脚本即可，脚本使用 `--dry-run=client -o yaml | kubectl apply -f -` 方式，**支持幂等更新**，不会报错。

```bash
# 更新 staging Secret
bash scripts/k8s-secrets/create-staging-secret.sh

# 更新 prod Secret（需二次确认）
bash scripts/k8s-secrets/create-prod-secret.sh
```

更新 Secret 后，需要重启对应 Pod 使新配置生效：
```bash
# 重启 staging 后端
kubectl rollout restart deployment/eksjk-backend -n eksjk-staging

# 重启 prod 后端
kubectl rollout restart deployment/eksjk-backend -n eksjk-prod
```

---

## 安全注意事项

- `.env.staging` 和 `.env.prod` 已加入项目根目录 `.gitignore`，**严禁提交到 Git 仓库**
- 只有 `.env.*.example` 示例文件（不含真实密钥）可以提交
- 生产环境 Secret 操作需要二次确认，防止误操作
- 建议定期轮换 JWT 密钥和 OSS AccessKey
