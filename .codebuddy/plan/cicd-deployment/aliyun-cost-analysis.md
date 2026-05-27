# 阿里云资源依赖清单与部署成本分析

> 基于 EKSJK V2 项目现有架构（单副本、无 Redis、共享云资源、GitOps CI/CD）整理

---

## 一、阿里云产品依赖清单

### 1.1 必选产品（核心运行依赖）

| # | 产品名称 | 产品代码 | 用途 | 数量 |
|---|----------|----------|------|------|
| 1 | **容器服务 ACK**（Kubernetes） | ACK | 运行后端/前端 Pod，staging 和 prod 共用同一集群，通过 Namespace 隔离 | 1 个集群 |
| 2 | **云数据库 RDS MySQL** | RDS | 业务数据持久化，staging 和 prod 共用同一实例，通过数据库名隔离 | 1 个实例 |
| 3 | **对象存储 OSS** | OSS | 医学影像、DICOM 文件等存储，staging 和 prod 使用不同 Bucket 名隔离 | 2 个 Bucket |
| 4 | **容器镜像服务 ACR 个人版** | ACR | 存储 CI 构建的 Docker 镜像，个人版免费，staging + prod 共用 | 1 个（免费） |
| 5 | **公网负载均衡 CLB（staging）** | SLB | staging 环境独立公网入口，绑定独立 EIP，直接访问 staging 服务 | 1 个 |
| 6 | **公网负载均衡 CLB（prod）** | SLB | prod 环境独立公网入口，绑定独立 EIP，直接访问 prod 服务 | 1 个 |

### 1.2 可选产品（推荐配套）

| # | 产品名称 | 产品代码 | 用途 | 是否必须 |
|---|----------|----------|------|----------|
| 7 | **专有网络 VPC** | VPC | ACK 集群、RDS 内网互通，隔离公网访问 | 推荐 |
| 8 | **云解析 DNS** | DNS | 绑定自定义域名（如 `eksjk.example.com`） | 可选 |
| 9 | **SSL 证书服务** | SSL | HTTPS 证书，保障数据传输安全 | 可选 |

### 1.3 CI/CD 相关（GitHub Actions 驱动，无需额外阿里云产品）

| 工具 | 说明 |
|------|------|
| GitHub Actions | CI/CD 流水线执行引擎（免费额度内） |
| 阿里云 ACR 个人版 | 镜像仓库（CI 推送、CD 拉取），**免费** |
| `kubectl` / `aliyun-cli` | GitHub Actions Runner 中调用，操作 ACK 集群 |

---

## 二、云资源共享隔离策略

```
阿里云账号
├── ACK 集群（1个，ecs.c6.xlarge 4C8G × 1 节点）
│   ├── Namespace: eksjk-staging   → staging 环境（backend × 1, frontend × 1）
│   └── Namespace: eksjk-prod      → production 环境（backend × 1, frontend × 1）
│
├── RDS MySQL 实例（1个，1C2G）
│   ├── 数据库: eksjk_staging      → staging 数据
│   ├── 数据库: eksjk_prod         → production 数据
│   ├── 账号: eksjk_staging_user   → 仅有 eksjk_staging 权限
│   └── 账号: eksjk_prod_user      → 仅有 eksjk_prod 权限
│
├── OSS（1个账号）
│   ├── Bucket: eksjk-staging      → staging 文件存储
│   └── Bucket: eksjk-prod         → production 文件存储
│
├── 公网 CLB-staging（独立 EIP-1）
│   └── 80/443 → ACK Ingress → eksjk-staging Namespace
│
├── 公网 CLB-prod（独立 EIP-2）
│   └── 80/443 → ACK Ingress → eksjk-prod Namespace
│
└── ACR 个人版（免费）
    └── 命名空间: eksjk
        ├── eksjk-backend:{branch}-{sha}   → staging 镜像
        └── eksjk-backend:{version_tag}    → production 镜像
```

**公网访问路径（完全独立）：**
```
外网用户（测试）
  └─→ CLB-staging（EIP-1）
        └─→ ACK Ingress → eksjk-staging Namespace
              ├─→ staging.eksjk.example.com  → frontend Pod
              └─→ staging-api.eksjk.example.com → backend Pod

外网用户（生产）
  └─→ CLB-prod（EIP-2）
        └─→ ACK Ingress → eksjk-prod Namespace
              ├─→ eksjk.example.com          → frontend Pod
              └─→ api.eksjk.example.com      → backend Pod
```

> ✅ **隔离优势：** staging 和 prod 拥有完全独立的公网 IP，互不影响。即使 staging 流量异常或 CLB 配置错误，也不会波及 prod 的公网入口。

---

## 三、资源规格建议

### 3.1 ACK 容器服务（共享单节点）

> 单节点同时承载 staging + prod 的所有 Pod

| 规格 | 节点数 | Pod 分布 |
|------|--------|---------|
| ecs.c6.xlarge（4C8G） | 1 | staging-backend + staging-frontend + prod-backend + prod-frontend |

**Pod 资源占用汇总：**

| Pod | CPU requests | Memory requests | CPU limits | Memory limits |
|-----|-------------|----------------|-----------|--------------|
| staging-backend | 250m | 512Mi | 1000m | 1Gi |
| staging-frontend | 50m | 64Mi | 100m | 128Mi |
| prod-backend | 250m | 512Mi | 1000m | 1Gi |
| prod-frontend | 50m | 64Mi | 100m | 128Mi |
| **合计 requests** | **600m** | **1152Mi** | — | — |

4C8G 节点（可用约 3.5C/7G）可轻松承载，留有充足余量。

**Namespace ResourceQuota 建议：**

| Namespace | CPU limits | Memory limits |
|-----------|-----------|--------------|
| eksjk-staging | 1500m | 1.5Gi |
| eksjk-prod | 1500m | 1.5Gi |

### 3.2 RDS MySQL（共享单实例）

| 规格 | 存储 | 数据库 | 说明 |
|------|------|--------|------|
| mysql.n2.small.1（1C2G） | 50GB SSD | eksjk_staging + eksjk_prod | 共用实例，开启自动备份，慢查询日志 |

- MySQL 版本：8.0
- 建议开启：每日自动备份、慢查询日志
- 账号隔离：staging 和 prod 各自独立账号，权限仅限对应数据库

### 3.3 OSS 对象存储

| Bucket | 存储类型 | 说明 |
|--------|---------|------|
| `eksjk-staging` | 标准存储 | 测试文件，可设置 30 天生命周期自动清理 |
| `eksjk-prod` | 标准存储 | 医学影像等重要文件，建议开启版本控制 |

- 访问方式：S3 兼容协议（`software.amazon.awssdk:s3`），无需改代码
- 两个 Bucket 使用同一组 AccessKey，通过 bucket 名称区分

### 3.4 公网 CLB（2 个，各自独立）

| 实例 | 绑定环境 | 公网 EIP | 规格 | 说明 |
|------|---------|---------|------|------|
| CLB-staging | eksjk-staging Namespace | EIP-1（独立） | slb.s1.small | 仅 staging 流量，测试人员使用 |
| CLB-prod | eksjk-prod Namespace | EIP-2（独立） | slb.s1.small | 仅 prod 流量，正式用户使用 |

**费用构成（每个 CLB）：**
- **实例费**：约 ¥0.02/小时 → **≈ ¥15/月**
- **公网流量费**：约 ¥0.8/GB（华东2）
  - CLB-staging：预估月流量 2GB（测试用）→ **≈ ¥2/月**
  - CLB-prod：预估月流量 10GB（正式用户）→ **≈ ¥8/月**

> 💡 **说明：** CLB 绑定的公网 EIP 费用已包含在 CLB 实例费中（ACK 创建 LoadBalancer Service 时自动分配）。

### 3.5 ACR 容器镜像服务

| 版本 | 费用 | 说明 |
|------|------|------|
| 个人版 | **免费** | staging + prod 共用，有并发拉取限制（小团队足够） |

- 镜像命名：`registry.cn-{region}.aliyuncs.com/eksjk/{service}:{tag}`
- 保留策略：保留最近 10 个 tag，自动清理旧镜像

---

## 四、月度费用估算

> 以**华东2（上海）**地域、**包年包月**（ECS/RDS）+ **按量**（CLB/OSS）为基准估算

| 产品 | 规格 | 计费方式 | 月费用估算 | 说明 |
|------|------|---------|-----------|------|
| ACK Worker 节点（ECS） | ecs.c6.xlarge 4C8G × 1 | 包年包月 | **≈ ¥290/月** | staging + prod 共用 |
| RDS MySQL | mysql.n2.small.1 1C2G + 50GB | 包年包月 | **≈ ¥135/月** | staging + prod 共用 |
| OSS 存储 | 标准存储 ~60GB + 请求费 | 按量 | **≈ ¥15/月** | 两个 Bucket 合计 |
| ACR 个人版 | — | 免费 | **¥0** | — |
| CLB-staging | slb.s1.small + 流量（预估 2GB） | 按量 | **≈ ¥17/月** | 实例费 ¥15 + 流量费 ¥2 |
| CLB-prod | slb.s1.small + 流量（预估 10GB） | 按量 | **≈ ¥23/月** | 实例费 ¥15 + 流量费 ¥8 |
| **月度合计** | | | **≈ ¥480/月** | |
| **年度合计** | | | **≈ ¥5,760/年** | |

> ⚠️ **说明：**
> - 以上为参考估算，实际价格以阿里云官网报价为准
> - CLB 流量费用随实际业务量浮动，医学影像文件较大时 prod 流量费用可能更高
> - 建议为 OSS 配置 CDN 加速，可降低 CLB 出流量费用（CDN 流量费约 ¥0.24/GB，比 CLB 直出便宜约 70%）

---

## 五、与历史方案对比

| 方案 | 月费用 | 年费用 | 说明 |
|------|--------|--------|------|
| 原方案（两套独立实例，按量） | ¥1,185 | ¥14,220 | staging + prod 各自独立 ACK/RDS |
| 上版优化（共享 ACK，包年） | ¥585 | ¥7,020 | 共享 ACK，但 RDS 仍两套 |
| 全共享 + 单 CLB | ¥463 | ¥5,556 | ACK + RDS + OSS + ACR 全部共享，1 个 CLB |
| **当前方案（全共享 + 双独立 CLB）** | **¥480** | **¥5,760** | staging/prod 各自独立 CLB+EIP，网络入口完全隔离 |

**当前方案相比原方案节省约 59%，年节省约 ¥8,460。**

---

## 六、本地开发环境（无云费用）

| 组件 | 本地替代方案 | 费用 |
|------|------------|------|
| MySQL | Docker 容器（mysql:8.0） | ¥0 |
| OSS | MinIO 容器（S3 兼容） | ¥0 |
| 镜像仓库 | 本地 Docker 镜像 | ¥0 |
| Kubernetes | Rancher Desktop / Docker Desktop | ¥0 |

本地开发环境完全免费，通过 `application-local.yml` 与云环境配置隔离。
