# 实施计划：K8S 部署迁移（Plan 1）

> 本任务清单基于 `replace-docker-compose-with-k8s/requirements.md` 需求文档生成。
> 实施顺序严格按照需求间的逻辑依赖关系排列。

---

- [ ] 1. 移除 Redis 相关配置并引入 Caffeine 缓存依赖
   - 检查并清理所有 `pom.xml` 中的 Redis 相关依赖（`spring-boot-starter-data-redis`、`sa-token-redis` 等）
   - 在父 `pom.xml` 的 `dependencyManagement` 中添加 `com.github.ben-manes.caffeine:caffeine` 版本声明
   - 在 `eksjk-service` 模块的 `pom.xml` 中引入 `spring-boot-starter-cache` 和 `caffeine` 依赖
   - 在 `application.yml` 中添加 `spring.cache.type: caffeine` 配置及默认缓存参数
   - 创建 `CacheConfig` 配置类（含 `@EnableCaching`），定义缓存名称常量和过期策略
   - 清理 `docker-compose.yml` 中的 `redis` 服务定义、`redis_data` 数据卷、Redis 环境变量
   - _需求：1.1 ~ 1.7_

- [ ] 2. 删除 docker-compose.yml 并创建 K8S 目录结构
   - 删除 `eksjk_v2/docker-compose.yml` 及项目中所有 `docker-compose*.yml` 文件
   - 创建 `eksjk_v2/k8s/` 目录及子目录：`mysql/`、`backend/`、`frontend/`、`monitoring/`
   - 编写 `k8s/namespace.yaml`，定义 `eksjk` Namespace
   - _需求：2.1 ~ 2.2、3.1 ~ 3.4_

- [ ] 3. 编写 MySQL K8S 资源清单
   - 创建 `k8s/mysql/secret.yaml`：MySQL root 密码、业务用户名密码（Base64 编码）
   - 创建 `k8s/mysql/configmap.yaml`：初始化 SQL 脚本、字符集配置（utf8mb4）
   - 创建 `k8s/mysql/pvc.yaml`：PersistentVolumeClaim（≥ 5Gi）
   - 创建 `k8s/mysql/statefulset.yaml`：MySQL 8.0 StatefulSet，挂载 initdb 脚本和 PVC，配置资源限制
   - 创建 `k8s/mysql/service.yaml`：ClusterIP Service（端口 3306）
   - _需求：4.1 ~ 4.6_

- [ ] 4. 编写后端 Spring Boot K8S 资源清单（含 application-k8s.yml）
   - 创建 `k8s/backend/configmap.yaml`：包含 `application-k8s.yml` 内容（MySQL 使用 K8S 内部 DNS，不含 Redis 配置）
   - 创建 `k8s/backend/deployment.yaml`：后端 Deployment，配置 `SPRING_PROFILES_ACTIVE=k8s`、从 MySQL Secret 引用数据库凭据、`imagePullPolicy: Never`、initContainers 等待 MySQL 就绪、livenessProbe/readinessProbe 指向 `/actuator/health`、资源限制
   - 创建 `k8s/backend/service.yaml`：ClusterIP Service（端口 8080）
   - 确保后端 Deployment 配置暴露 `/actuator/prometheus` 端点
   - _需求：5.1 ~ 5.9_

- [ ] 5. 编写前端 Nginx K8S 资源清单（含 Ingress）
   - 创建 `k8s/frontend/configmap.yaml`：Nginx 配置文件（API 代理指向 `http://eksjk-backend:8080`）
   - 创建 `k8s/frontend/deployment.yaml`：前端 Deployment，`imagePullPolicy: Never`，资源限制
   - 创建 `k8s/frontend/service.yaml`：ClusterIP Service（端口 80）
   - 创建 `k8s/frontend/ingress.yaml`：Ingress 路由规则（`/api/` → 后端、`/` → 前端、`/grafana/` → Grafana）
   - 同时提供 NodePort Service 作为备选访问方式
   - _需求：6.1 ~ 6.6_

- [ ] 6. 编写监控组件 K8S 资源清单（Prometheus + Grafana）
   - 创建 `k8s/monitoring/prometheus-configmap.yaml`：scrape_configs 配置（抓取后端 `/actuator/prometheus`，间隔 15s）
   - 创建 `k8s/monitoring/prometheus-pvc.yaml`：Prometheus 数据持久化
   - 创建 `k8s/monitoring/prometheus-deployment.yaml`：Prometheus Deployment + Service，配置资源限制
   - 创建 `k8s/monitoring/grafana-pvc.yaml`：Grafana 数据持久化
   - 创建 `k8s/monitoring/grafana-deployment.yaml`：Grafana Deployment + Service，预置 Prometheus 数据源，默认账号 `admin/admin`，配置资源限制
   - _需求：7.1 ~ 7.5_

- [ ] 7. 编写一键部署脚本（deploy.sh）和清理脚本（teardown.sh）
   - 编写 `k8s/deploy.sh`：前置检查（kubectl/nerdctl/mvn/npm）→ 构建后端镜像 → 构建前端镜像 → 按序应用 K8S 资源 → 等待 Pod 就绪 → 输出访问地址
   - 实现 `--skip-build` 参数（跳过镜像构建）和 `--only-monitoring` 参数（仅部署监控）
   - 编写 `k8s/teardown.sh`：删除 eksjk 命名空间资源（默认保留 PVC），`--with-data` 参数同时删除 PVC
   - 清理脚本需打印资源列表并要求用户确认，脚本包含中文注释
   - _需求：8.1 ~ 8.8_

- [ ] 8. 编写部署文档并更新 claude.md
   - 编写 `k8s/README.md`：前置条件、快速开始（3 步）、服务访问地址汇总、kubectl 命令速查、常见问题排查、增量更新命令
   - 更新 `claude.md`：部署架构章节改为 K8S 方案、移除 Docker Compose 和 Redis 描述、新增 Caffeine 技术选型说明、更新部署注意事项
   - _需求：9.1 ~ 9.4_
