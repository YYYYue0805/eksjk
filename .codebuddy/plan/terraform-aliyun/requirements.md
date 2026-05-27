# 需求文档：使用 Terraform 自动化管理阿里云云资源

## 引言

本文档描述 EKSJK 项目如何使用 **Terraform**（基础设施即代码，IaC）自动化购买和管理阿里云云资源，替代手动在阿里云控制台点击操作，实现云资源的版本化管理、可重复创建和一键销毁。

### 背景与目标

基于已确定的阿里云资源规划，需要自动化创建以下资源：

| 资源 | 规格 | 说明 |
|------|------|------|
| VPC + 交换机 | — | 网络基础设施，ACK/RDS 内网互通 |
| ACK 集群 + 节点 | ecs.c6.xlarge 4C8G × 1 | 共享单节点，staging/prod 通过 Namespace 隔离 |
| RDS MySQL 8.0 | mysql.n2.small.1 1C2G + 50GB | 共享单实例，staging/prod 通过数据库名隔离 |
| OSS Bucket × 2 | 标准存储 | `eksjk-staging` 和 `eksjk-prod` |
| CLB × 2 | slb.s1.small | staging 和 prod 各自独立公网 EIP |
| RAM 子账号 | CI/CD 专用 | 最小权限，供 GitHub Actions 使用 |

### Terraform 在整体流程中的位置

```
开发者 / 运维
    │
    ├── 1. 编写 Terraform 代码（.tf 文件，提交到 Git）
    │
    ├── 2. terraform plan   → 预览将要创建/变更的资源
    │
    ├── 3. terraform apply  → 实际购买并创建阿里云资源
    │        │
    │        ├── 创建 VPC / 交换机 / 安全组
    │        ├── 创建 ACK 集群 + Worker 节点
    │        ├── 创建 RDS MySQL 实例 + 数据库 + 账号
    │        ├── 创建 OSS Bucket × 2
    │        ├── 创建 CLB × 2（绑定独立 EIP）
    │        └── 创建 RAM 子账号 + 授权策略
    │
    └── 4. terraform output → 输出资源信息（IP、连接串等）供后续配置使用
```

---

## 需求

### 需求 1：Terraform 项目结构与状态管理

**用户故事：** 作为运维工程师，我希望 Terraform 代码有清晰的目录结构并将状态文件存储在远端，以便团队成员协作管理云资源，避免本地状态文件丢失导致资源失控。

#### 验收标准

1. WHEN 组织 Terraform 代码时 THEN 系统 SHALL 在项目根目录下创建 `terraform/` 目录，并按以下结构组织文件：
   - `main.tf`：资源定义入口
   - `variables.tf`：变量声明
   - `outputs.tf`：输出值定义
   - `versions.tf`：Provider 版本锁定
   - `terraform.tfvars.example`：变量示例文件（不含真实密钥，可提交 Git）
2. WHEN 配置 Terraform 状态存储时 THEN 系统 SHALL 使用**阿里云 OSS** 作为 Remote Backend 存储 `terraform.tfstate` 文件，Bucket 名为 `eksjk-terraform-state`，不得将状态文件提交到 Git 仓库。
3. WHEN 多人协作操作 Terraform 时 THEN 系统 SHALL 通过 OSS 的对象锁定机制防止并发 `terraform apply` 导致状态冲突。
4. WHEN 提交 Terraform 代码到 Git 时 THEN 系统 SHALL 在 `.gitignore` 中排除 `terraform.tfvars`、`.terraform/`、`*.tfstate`、`*.tfstate.backup` 等敏感或本地文件。
5. WHEN 声明 Provider 版本时 THEN 系统 SHALL 在 `versions.tf` 中锁定 `aliyun/alicloud` Provider 的具体版本号（如 `~> 1.220`），确保团队成员使用相同版本。

---

### 需求 2：网络基础设施（VPC / 交换机 / 安全组）

**用户故事：** 作为运维工程师，我希望通过 Terraform 自动创建 VPC 和网络配置，以便 ACK、RDS 等资源在同一私有网络内互通，同时隔离公网直接访问。

#### 验收标准

1. WHEN 执行 `terraform apply` 时 THEN 系统 SHALL 创建 1 个 VPC（CIDR `172.16.0.0/12`，名称 `eksjk-vpc`）作为所有资源的网络基础。
2. WHEN 创建 VPC 后 THEN 系统 SHALL 在指定可用区内创建至少 1 个交换机（vSwitch，CIDR `172.16.0.0/24`），供 ACK 节点和 RDS 使用。
3. WHEN 创建安全组时 THEN 系统 SHALL 创建 ACK 节点安全组，仅开放必要端口：集群内部通信端口、CLB 健康检查端口（80/443），默认拒绝其他入站流量。
4. WHEN 创建 RDS 安全组时 THEN 系统 SHALL 仅允许来自 ACK 节点安全组的 3306 端口访问，不得对公网开放 RDS 端口。
5. IF VPC 或交换机已存在（通过 `data source` 引用） THEN 系统 SHALL 支持复用已有网络资源，不重复创建。

---

### 需求 3：ACK 容器集群与节点池

**用户故事：** 作为运维工程师，我希望通过 Terraform 自动创建 ACK 集群和工作节点，以便 Kubernetes 环境开箱即用，无需在控制台手动配置。

#### 验收标准

1. WHEN 执行 `terraform apply` 时 THEN 系统 SHALL 创建 1 个 ACK 托管版集群（`ManagedKubernetes`），集群名称为 `eksjk-cluster`，Kubernetes 版本指定为变量可配置。
2. WHEN 创建 ACK 集群时 THEN 系统 SHALL 将集群的 Pod 网络 CIDR 和 Service CIDR 配置为与 VPC 不冲突的地址段（如 Pod `10.0.0.0/16`，Service `10.96.0.0/16`）。
3. WHEN 创建工作节点池时 THEN 系统 SHALL 创建 1 个节点池，节点规格为 `ecs.c6.xlarge`（4C8G），节点数量为 `1`，系统盘 40GB SSD，不配置自动伸缩。
4. WHEN ACK 集群创建完成后 THEN 系统 SHALL 通过 `terraform output` 输出集群的 KubeConfig 内容，供运维人员配置 `kubectl` 和 GitHub Actions Secrets 使用。
5. WHEN 创建 ACK 集群时 THEN 系统 SHALL 为集群开启日志服务（SLS）集成，将容器日志自动采集到阿里云日志服务，方便问题排查。

---

### 需求 4：RDS MySQL 实例与数据库账号

**用户故事：** 作为运维工程师，我希望通过 Terraform 自动创建 RDS 实例并初始化 staging/prod 两个数据库及对应账号，以便数据库环境开箱即用且账号权限精确隔离。

#### 验收标准

1. WHEN 执行 `terraform apply` 时 THEN 系统 SHALL 创建 1 个 RDS MySQL 8.0 实例，规格为 `mysql.n2.small.1`（1C2G），存储 50GB SSD，实例名称为 `eksjk-rds`。
2. WHEN 创建 RDS 实例时 THEN 系统 SHALL 将实例部署在与 ACK 节点相同的 VPC 和交换机内，仅开放内网访问，不绑定公网地址。
3. WHEN RDS 实例创建完成后 THEN 系统 SHALL 自动创建 2 个数据库：`eksjk_staging` 和 `eksjk_prod`，字符集均为 `utf8mb4`。
4. WHEN 创建数据库账号时 THEN 系统 SHALL 创建 2 个独立账号：`eksjk_staging_user`（仅有 `eksjk_staging` 的读写权限）和 `eksjk_prod_user`（仅有 `eksjk_prod` 的读写权限），账号密码通过 Terraform 变量传入，不硬编码。
5. WHEN RDS 实例创建完成后 THEN 系统 SHALL 开启自动备份策略：每日凌晨 2:00 备份，保留 7 天。
6. WHEN 执行 `terraform output` 时 THEN 系统 SHALL 输出 RDS 内网连接地址（host）和端口，供配置 Kubernetes Secret 使用，账号密码不输出到终端（标记为 `sensitive = true`）。

---

### 需求 5：OSS 对象存储 Bucket

**用户故事：** 作为运维工程师，我希望通过 Terraform 自动创建并配置 OSS Bucket，以便文件存储环境开箱即用，且 staging/prod 数据完全隔离。

#### 验收标准

1. WHEN 执行 `terraform apply` 时 THEN 系统 SHALL 创建 2 个 OSS Bucket：`eksjk-staging`（测试环境文件）和 `eksjk-prod`（生产环境文件），存储类型均为标准存储。
2. WHEN 创建 OSS Bucket 时 THEN 系统 SHALL 将 Bucket 访问权限设置为**私有**（`private`），不允许公网匿名访问，所有访问必须通过 AccessKey 鉴权。
3. WHEN 创建 `eksjk-staging` Bucket 时 THEN 系统 SHALL 配置生命周期规则：超过 30 天未访问的对象自动转为低频存储，超过 90 天自动删除，以控制测试数据存储成本。
4. WHEN 创建 `eksjk-prod` Bucket 时 THEN 系统 SHALL 开启版本控制，防止文件被意外覆盖或删除，保留历史版本。
5. WHEN 创建 Terraform 状态存储 Bucket 时 THEN 系统 SHALL 额外创建 `eksjk-terraform-state` Bucket，专用于存储 Terraform 状态文件，开启版本控制。

---

### 需求 6：RAM 子账号与权限策略（CI/CD 专用）

**用户故事：** 作为运维工程师，我希望通过 Terraform 自动创建 GitHub Actions 专用的 RAM 子账号并精确授权，以便 CI/CD 流水线能访问 ACR 和 ACK，同时不具备操作 RDS 和 OSS 的权限。

#### 验收标准

1. WHEN 执行 `terraform apply` 时 THEN 系统 SHALL 创建 1 个 RAM 子账号，名称为 `eksjk-cicd`，仅用于 GitHub Actions 流水线，不用于人工登录控制台。
2. WHEN 为 RAM 账号授权时 THEN 系统 SHALL 仅授予以下最小权限策略：
   - `AliyunContainerRegistryFullAccess`：ACR 镜像推送/拉取
   - 自定义策略：仅允许对 `eksjk-staging` 和 `eksjk-prod` 两个 Namespace 执行 `kubectl apply/get/rollout` 操作
3. WHEN RAM 账号创建完成后 THEN 系统 SHALL 为该账号创建 AccessKey，并通过 `terraform output` 以 `sensitive` 方式输出，供运维人员配置到 GitHub Repository Secrets。
4. WHEN 创建自定义 ACK 权限策略时 THEN 系统 SHALL 使用 Terraform 的 `alicloud_ram_policy` 资源定义 JSON 策略文档，策略内容版本化管理在 Git 中。
5. IF RAM 账号的 AccessKey 需要轮换 THEN 运维人员 SHALL 能够通过 `terraform apply` 重新生成 AccessKey，旧 Key 自动失效。

---

### 需求 7：CLB 负载均衡（staging / prod 各自独立）

**用户故事：** 作为运维工程师，我希望通过 Terraform 创建两个独立的公网 CLB，以便 staging 和 prod 各自拥有独立的公网 IP，互不影响。

#### 验收标准

1. WHEN 执行 `terraform apply` 时 THEN 系统 SHALL 创建 2 个 CLB 实例：`eksjk-clb-staging` 和 `eksjk-clb-prod`，规格均为 `slb.s1.small`，计费方式为按量付费。
2. WHEN 创建 CLB 时 THEN 系统 SHALL 为每个 CLB 分配独立的公网 EIP，staging 和 prod 使用不同的公网 IP 地址。
3. WHEN CLB 创建完成后 THEN 系统 SHALL 通过 `terraform output` 输出两个 CLB 的公网 IP 地址，供运维人员配置 DNS 解析和 ACK Ingress 使用。
4. WHEN ACK 集群中创建 Kubernetes `LoadBalancer` 类型 Service 时 THEN 系统 SHALL 通过 Annotation（`service.beta.kubernetes.io/alibaba-cloud-loadbalancer-id`）将 Service 绑定到 Terraform 创建的指定 CLB 实例，而非自动新建 CLB。
5. IF CLB 实例被意外删除 THEN 系统 SHALL 能够通过 `terraform apply` 重新创建并恢复，不影响 ACK 集群内部的 Pod 运行。

---

### 需求 8：Terraform 与 GitHub Actions 的集成

**用户故事：** 作为运维工程师，我希望 Terraform 的执行也能集成到 GitHub Actions 中，以便基础设施变更同样有 PR Review 和自动化检查，实现 GitOps 全链路管理。

#### 验收标准

1. WHEN 向 `main` 分支提交 `terraform/` 目录下的变更时 THEN 系统 SHALL 触发 GitHub Actions workflow（`terraform.yml`），自动执行 `terraform fmt --check` 和 `terraform validate` 进行格式和语法检查。
2. WHEN 提交 Pull Request 修改 Terraform 代码时 THEN 系统 SHALL 在 PR 中自动执行 `terraform plan`，并将 plan 结果以评论形式输出到 PR，供 Reviewer 审查资源变更影响。
3. WHEN PR 合并到 `main` 分支后 THEN 系统 SHALL 需要人工在 GitHub Actions 中手动触发 `terraform apply`（通过 `workflow_dispatch`），不自动执行，防止误操作。
4. WHEN GitHub Actions 执行 Terraform 时 THEN 系统 SHALL 从 GitHub Secrets 中读取阿里云 AccessKey（`TF_VAR_access_key` / `TF_VAR_secret_key`）和 RDS 密码等敏感变量，不硬编码在 workflow 文件中。
5. WHEN `terraform apply` 执行完成后 THEN 系统 SHALL 将关键输出（CLB 公网 IP、RDS 连接地址等）记录到 GitHub Actions 日志，方便运维人员查阅。
