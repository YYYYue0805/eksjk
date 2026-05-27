# 实施计划：可观测性指标 + Grafana 大盘（Plan 2）

> 本任务清单基于 `observability-monitoring/requirements.md` 需求文档生成。
> 前置依赖：Plan 1 完成（K8S 环境就绪，Prometheus + Grafana 已部署）。

---

- [ ] 1. 配置 Micrometer + Prometheus 依赖与 Actuator 端点
   - 在后端 `pom.xml` 中引入 `micrometer-registry-prometheus` 依赖
   - 在 `application.yml` 中配置 Actuator 暴露端点：`prometheus`、`health`、`info`、`metrics`
   - 配置 `management.endpoints.web.exposure.include` 和 `management.metrics` 相关参数
   - 配置 `/actuator/info` 端点暴露应用版本号、构建时间、Git commit 信息（`spring-boot-maven-plugin` 的 `build-info` goal + `git-commit-id-plugin`）
   - 验证 `/actuator/prometheus` 端点可正常返回 OS 层、JVM 层、Spring 框架层的默认指标
   - _需求：1.1 ~ 1.4、2.1 ~ 2.4、3.1 ~ 3.6_

- [ ] 2. 实现业务应用层自定义指标（患者 + 随访）
   - 创建 `MetricsConfig` 配置类，注册自定义 Counter 和 Histogram（`eksjk_patient_operations_total`、`eksjk_patient_query_duration_seconds`、`eksjk_followup_operations_total`、`eksjk_followup_query_duration_seconds`）
   - 在患者 Service 层的 CRUD 方法中埋点：按 `operation` 和 `dis_class` 标签记录操作计数和查询耗时
   - 在随访 Service 层的 CRUD 方法中埋点：按 `operation` 和 `follow_up_type` 标签记录操作计数和查询耗时
   - _需求：4.1 ~ 4.2_

- [ ] 3. 实现业务应用层自定义指标（认证 + 导出 + 数据量统计）
   - 在认证模块中埋点：`eksjk_auth_login_total`（按 `result` 标签区分 success/failure）、`eksjk_auth_active_sessions`（Gauge）、`eksjk_auth_permission_denied_total`
   - 在数据导出模块中埋点：`eksjk_export_operations_total`、`eksjk_export_duration_seconds`
   - 创建定时任务（`@Scheduled`），定期查询数据库刷新 `eksjk_patient_total`（按 `dis_class` 分组）和 `eksjk_followup_total` Gauge 指标
   - _需求：4.3 ~ 4.5_

- [ ] 4. 设计并编写 Grafana 大盘 JSON 文件
   - 创建 `k8s/monitoring/grafana-dashboard.json`，包含 4 个 Row 分区：OS 系统层、JVM 运行时层、Spring 框架层、业务应用层
   - OS 系统层面板：CPU 使用率（整机 vs 进程）、系统负载、内存使用量、磁盘剩余空间
   - JVM 运行时层面板：堆内存使用率趋势、GC 停顿时间趋势、GC 频率、线程数分布
   - Spring 框架层面板：HTTP QPS 趋势、HTTP P95 响应时间、HTTP 错误率（4xx/5xx）、HikariCP 连接池状态、日志事件频率
   - 业务应用层面板：患者操作次数趋势（按疾病类型分组）、随访操作次数趋势、认证事件统计、导出统计、数据库存量统计
   - 配置阈值颜色变化（ERROR > 5次/分钟 → 红色、HTTP 错误率 > 5% → 红色、P95 > 2s → 黄色）
   - _需求：5.1 ~ 5.4_

- [ ] 5. 配置 Grafana 自动加载大盘并验证
   - 在 Grafana Deployment 中通过 ConfigMap 挂载 `grafana-dashboard.json` 到 provisioning 目录
   - 配置 Grafana provisioning YAML（dashboards provider + datasources），实现启动后自动加载大盘和 Prometheus 数据源
   - 更新 `k8s/monitoring/grafana-deployment.yaml` 中的 Volume 挂载配置
   - _需求：5.1_
