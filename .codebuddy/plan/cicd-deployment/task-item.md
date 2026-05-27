# 实施计划：EKSJK CI/CD 全链路发布部署

> 覆盖范围：Terraform 云资源创建 → K8s 清单重构 → GitHub Actions 流水线 → 本地开发环境

---

- [ ] 1. Terraform 基础设施代码（云资源一键创建）
   - 创建 `terraform/` 目录，编写 `versions.tf`（锁定 alicloud provider `~> 1.220`）、`variables.tf`、`outputs.tf`、`terraform.tfvars.example`
   - 编写网络资源：VPC（`172.16.0.0/12`）、vSwitch、ACK 节点安全组（仅开放 80/443 及集群内部端口）、RDS 安全组（仅允许 ACK 节点访问 3306）
   - 编写 ACK 托管版集群（`eksjk-cluster`，`ecs.c6.xlarge` 单节点，Pod CIDR `10.0.0.0/16`，开启 SLS 日志）
   - 编写 RDS MySQL 8.0（`mysql.n2.small.1`，50GB，创建 `eksjk_staging`/`eksjk_prod` 两个库及独立账号，开启每日自动备份）
   - 编写 OSS Bucket × 3（`eksjk-staging` 含生命周期规则、`eksjk-prod` 开启版本控制、`eksjk-terraform-state` 作为 Remote Backend）
   - 编写 CLB × 2（`eksjk-clb-staging`/`eksjk-clb-prod`，各绑定独立 EIP，按量付费）
   - 编写 RAM 子账号 `eksjk-cicd`（授予 ACR Full + 自定义 ACK Namespace 级别权限），`outputs.tf` 中敏感字段标记 `sensitive = true`
   - 配置 OSS Remote Backend，更新 `.gitignore` 排除 `terraform.tfvars`/`.terraform/`/`*.tfstate`
   - _需求：Terraform 需求 1～7_

- [ ] 2. 重构 K8s 清单目录结构（staging / prod 双环境）
   - 将现有 `k8s/` 目录重构为 `k8s/staging/` 和 `k8s/prod/` 两套独立清单
   - 编写 `k8s/staging/namespace.yaml`（`eksjk-staging`）和 `k8s/prod/namespace.yaml`（`eksjk-prod`），各含 ResourceQuota（staging: 1500m/1.5Gi，prod: 1500m/1.5Gi）
   - 编写 staging/prod 各自的 `backend/deployment.yaml`（`replicas: 1`，无 HPA，通过 `envFrom.secretRef` 引用 `eksjk-{env}-secret`）
   - 编写 staging/prod 各自的 `frontend/deployment.yaml`、`frontend/service.yaml`、`frontend/ingress.yaml`
   - 编写 `frontend/service.yaml` 中 `LoadBalancer` 类型 Service，通过 Annotation `service.beta.kubernetes.io/alibaba-cloud-loadbalancer-id` 绑定 Terraform 创建的 CLB 实例
   - 删除 K8s 清单中所有 Redis 相关资源（Deployment/Service/PVC）
   - _需求：CI/CD 需求 1.1、1.6、1.7、4.1、8.5、8.6_

- [ ] 3. 编写 K8s Secret 初始化脚本
   - 创建 `scripts/k8s-secrets/` 目录，编写 `create-staging-secret.sh` 和 `create-prod-secret.sh`
   - 脚本通过 `kubectl create secret generic eksjk-{env}-secret` 创建包含以下字段的 Secret：RDS host/port/user/password/database、OSS endpoint/bucket/access-key/secret-key、JWT 签名密钥
   - 脚本支持 `--dry-run -o yaml | kubectl apply -f -` 幂等更新方式
   - 编写 `README.md` 说明 Secret 字段含义及首次初始化步骤
   - _需求：GitHub Actions 需求 5.1、5.2、5.3、5.4_

- [ ] 4. 编写 GitHub Actions CI workflow（`ci.yml`）
   - 触发条件：所有分支 push；`ubuntu-latest` runner
   - 后端 job：`actions/cache` 缓存 `~/.m2` → `mvn clean package -DskipTests` → `docker build` → `docker login` ACR → 按 `{service}:develop-{short_sha}` 打 tag 推送
   - 前端 job：`actions/cache` 缓存 `node_modules` → `npm install && npm run build` → `docker build` → 推送 ACR
   - 任意 job 失败立即终止，不进入后续步骤（`set -e`）
   - _需求：CI/CD 需求 3.1～3.5，GitHub Actions 需求 2.1～2.6、6.3、6.4、6.5_

- [ ] 5. 编写 GitHub Actions staging 部署 workflow（`deploy-staging.yml`）
   - 触发条件：`develop` 分支 push，依赖 `ci.yml` 成功完成（`workflow_run`）
   - 使用 `yq` 将 `k8s/staging/` 下 Deployment 的镜像 tag 替换为最新 `develop-{short_sha}`
   - `git commit` + `git push` 提交变更，commit message 含 `[skip ci]`
   - 配置 KubeConfig（从 GitHub Secret 读取）→ `kubectl apply -f k8s/staging/` → `kubectl rollout status --timeout=120s`
   - 失败时执行 `kubectl rollout undo`，最后通过 `if: always()` 发送部署结果通知（含环境/镜像版本/状态/耗时/Actions 链接）
   - _需求：CI/CD 需求 4.1～4.4、7.1，GitHub Actions 需求 3.1～3.5、7.1、7.2_

- [ ] 6. 编写 GitHub Actions prod 部署 workflow（`deploy-prod.yml`）
   - 触发条件：创建 `release/v*` Tag
   - 构建阶段：同 CI 流程，镜像 tag 使用 `{service}:{version_tag}` 并同时打 `latest`
   - 部署阶段：配置 GitHub Environment `production`（Required Reviewer ≥ 1），流水线在此暂停等待人工审批
   - 审批通过后：`yq` 更新 `k8s/prod/` 镜像版本 → `git commit [skip ci]` → `kubectl apply -f k8s/prod/` → `kubectl rollout status`
   - 失败时自动回滚，成功时自动创建 GitHub Release（Release Notes 基于 Tag 间 commit 生成）
   - 最后发送部署结果通知
   - _需求：CI/CD 需求 2.3、2.4、4.1～4.5、7.1、7.2，GitHub Actions 需求 4.1～4.5、7.3_

- [ ] 7. 编写 Terraform GitHub Actions workflow（`terraform.yml`）
   - PR 触发：`terraform fmt --check` + `terraform validate` + `terraform plan`，plan 结果以 PR 评论形式输出
   - `main` 分支合并后：支持 `workflow_dispatch` 手动触发 `terraform apply`，不自动执行
   - 所有敏感变量（AccessKey、RDS 密码等）从 GitHub Secrets 读取为 `TF_VAR_*` 环境变量
   - apply 完成后将 CLB 公网 IP、RDS 连接地址等关键 output 记录到 Actions 日志
   - _需求：Terraform 需求 8.1～8.5_

- [ ] 8. 本地开发环境一键启动脚本优化
   - 更新 `scripts/local-dev/start.sh`：启动 MySQL + MinIO 容器（不启动 Redis），执行 `init-sql/` 初始化，检测端口冲突并提示
   - 确认 `application-local.yml` 配置完整（MySQL 本地连接、MinIO S3 配置），不含 `spring.redis.*` 配置块
   - 确认 `pom.xml` 不含 Redis 相关依赖（`spring-data-redis`/`lettuce-core`/`jedis`），`application*.yml` 无 `spring.redis.*`
   - _需求：CI/CD 需求 6.1～6.5、8.1～8.3_
