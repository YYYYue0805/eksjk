# 可观测性监控系统需求文档

> **实施优先级：Plan 2**
> **前置依赖：Plan 1（K8S 部署迁移）完成，Prometheus 和 Grafana 已在 K8S 中运行**
> **后续依赖：Plan 4（测试验证）的性能测试需求依赖本 Plan 提供的指标数据**

> **精简说明：** Prometheus 和 Grafana 的 K8S 部署已合并至 Plan 1（需求 7）。本文档专注于：① 后端指标暴露的具体内容 ② Grafana 大盘的设计与配置。

## 引言

为了在本地开发和测试阶段能够直观地验证 EKSJK v2 系统的正确性，需要建立一套完整的可观测性体系。该体系包括：在 Spring Boot 后端暴露 OS 系统、JVM 运行时、Spring 框架和业务应用四个层次的监控指标，并通过 Grafana 大盘进行可视化展示。开发人员可通过大盘实时观察 API 响应情况、业务数据变化趋势和系统健康状态，从而快速定位重构后代码的正确性问题。

---

## 需求

### 需求 1：OS 系统层指标暴露

**用户故事：** 作为一名开发人员，我希望后端服务能够暴露宿主机操作系统层面的资源使用指标，以便判断系统性能瓶颈是否来自底层资源不足。

#### 验收标准

1. WHEN 后端服务启动 THEN 系统 SHALL 通过 `/actuator/prometheus` 端点暴露以下 OS 层指标：
   - `system.cpu.usage`：整机 CPU 使用率（0.0 ~ 1.0）
   - `process.cpu.usage`：当前 JVM 进程 CPU 使用率
   - `system.load.average.1m`：系统 1 分钟平均负载
   - `system.cpu.count`：可用 CPU 核心数
2. WHEN 后端服务运行时 THEN 系统 SHALL 暴露以下内存指标：
   - `system.memory.total`：宿主机总物理内存（字节）
   - `system.memory.free`：宿主机空闲物理内存（字节）
3. WHEN 后端服务运行时 THEN 系统 SHALL 暴露以下磁盘/文件描述符指标：
   - `process.files.open`：当前进程打开的文件描述符数量
   - `process.files.max`：系统允许的最大文件描述符数量
   - `disk.free`（标记 `path=/`）：根分区剩余磁盘空间
4. WHEN 后端服务运行时 THEN 系统 SHALL 暴露进程运行时长指标 `process.uptime`（秒）

---

### 需求 2：JVM 运行时层指标暴露

**用户故事：** 作为一名开发人员，我希望后端服务能够暴露详细的 JVM 运行时指标，以便快速定位内存泄漏、GC 停顿、线程阻塞等 JVM 层面的问题。

#### 验收标准

1. WHEN 后端服务运行时 THEN 系统 SHALL 暴露以下 JVM 内存指标（按内存区域分 `area` 标签：heap / nonheap）：
   - `jvm.memory.used`：已使用内存量
   - `jvm.memory.committed`：已提交内存量
   - `jvm.memory.max`：最大可用内存量
2. WHEN 后端服务运行时 THEN 系统 SHALL 暴露以下 GC 指标（按 `action` 和 `cause` 标签区分 minor/major GC）：
   - `jvm.gc.pause`：GC 停顿时间分布（Histogram，含 P50/P95/P99/Max）
   - `jvm.gc.memory.promoted`：每次 GC 晋升到老年代的内存量
   - `jvm.gc.memory.allocated`：两次 GC 之间新分配的内存量
   - `jvm.gc.live.data.size`：Full GC 后老年代存活数据大小
3. WHEN 后端服务运行时 THEN 系统 SHALL 暴露以下线程指标：
   - `jvm.threads.live`：当前存活线程数
   - `jvm.threads.daemon`：守护线程数
   - `jvm.threads.peak`：历史峰值线程数
   - `jvm.threads.states`（按 `state` 标签区分 RUNNABLE / BLOCKED / WAITING / TIMED_WAITING）
4. WHEN 后端服务运行时 THEN 系统 SHALL 暴露以下缓冲区指标（按 `id` 标签区分 direct / mapped）：
   - `jvm.buffer.count`：缓冲区数量
   - `jvm.buffer.memory.used`：缓冲区已使用内存
   - `jvm.buffer.total.capacity`：缓冲区总容量

---

### 需求 3：Spring 框架层指标暴露

**用户故事：** 作为一名开发人员，我希望后端服务能够暴露 Spring 框架各组件的运行指标，以便观察 HTTP 请求处理、数据库连接池、缓存和定时任务等框架层面的行为是否符合预期。

#### 验收标准

1. WHEN 有 HTTP 请求到达 THEN 系统 SHALL 暴露以下 HTTP 服务端指标（按 `uri`、`method`、`status`、`outcome` 标签区分）：
   - `http.server.requests`：请求计数与响应时间分布（Histogram，含 P50/P95/P99）
   - `http.server.requests.active`：当前正在处理的并发请求数
2. WHEN 数据库连接池（HikariCP）运行时 THEN 系统 SHALL 暴露以下连接池指标：
   - `hikaricp.connections`：连接池总连接数
   - `hikaricp.connections.active`：当前活跃连接数
   - `hikaricp.connections.idle`：当前空闲连接数
   - `hikaricp.connections.pending`：等待获取连接的请求数
   - `hikaricp.connections.acquire`：获取连接耗时分布
   - `hikaricp.connections.timeout.total`：连接超时累计次数
3. WHEN 应用启动完成 THEN 系统 SHALL 通过 `/actuator/health` 端点暴露各组件健康状态（DB、磁盘空间等）
4. WHEN 应用启动完成 THEN 系统 SHALL 通过 `/actuator/info` 端点暴露应用版本号、构建时间、Git commit 信息
5. WHEN Logback 记录日志时 THEN 系统 SHALL 暴露日志指标（按 `level` 标签区分 ERROR / WARN / INFO / DEBUG）：
   - `logback.events`：各级别日志事件累计计数
6. WHEN 发生 Spring MVC 异常 THEN 系统 SHALL 在 `http.server.requests` 指标中通过 `exception` 标签记录异常类名

---

### 需求 4：业务应用层指标暴露

**用户故事：** 作为一名开发人员，我希望后端服务能够暴露 EKSJK 业务领域的自定义指标，以便直接观察患者数据、随访记录、疾病分布等业务操作的正确性和数量趋势。

#### 验收标准

1. WHEN 发生患者相关操作 THEN 系统 SHALL 暴露以下患者管理指标（按 `operation` 标签区分 create/query/update/delete，按 `dis_class` 标签区分疾病类型编码）：
   - `eksjk_patient_operations_total`：患者操作累计次数
   - `eksjk_patient_query_duration_seconds`：患者查询耗时分布（Histogram）
2. WHEN 发生随访记录相关操作 THEN 系统 SHALL 暴露以下随访管理指标（按 `operation` 标签区分，按 `follow_up_type` 标签区分普通随访/MAS随访）：
   - `eksjk_followup_operations_total`：随访操作累计次数
   - `eksjk_followup_query_duration_seconds`：随访查询耗时分布（Histogram）
3. WHEN 发生认证相关操作 THEN 系统 SHALL 暴露以下认证指标（按 `role` 标签区分角色）：
   - `eksjk_auth_login_total`：登录操作累计次数（按 `result` 标签区分 success/failure）
   - `eksjk_auth_active_sessions`：当前在线会话数（Gauge）
   - `eksjk_auth_permission_denied_total`：权限拒绝累计次数
4. WHEN 发生数据导出操作（EasyExcel） THEN 系统 SHALL 暴露以下导出指标：
   - `eksjk_export_operations_total`：导出操作累计次数
   - `eksjk_export_duration_seconds`：导出耗时分布（Histogram）
5. WHEN 系统运行时 THEN 系统 SHALL 暴露以下数据量统计指标（Gauge，定期刷新）：
   - `eksjk_patient_total`：患者总数（按 `dis_class` 标签区分各疾病类型）
   - `eksjk_followup_total`：随访记录总数

---

### 需求 5：Grafana 可视化大盘

**用户故事：** 作为一名开发人员，我希望通过 Grafana 大盘直观地观察系统运行状态和业务数据正确性，以便快速发现重构后代码的异常行为。

#### 验收标准

1. WHEN Grafana 启动后 THEN 系统 SHALL 自动加载预置的 EKSJK 监控大盘 JSON 文件，开发人员无需手动导入
2. WHEN 开发人员打开大盘 THEN 系统 SHALL 展示以下面板分区：
   - **OS 系统层**：CPU 使用率（整机 vs 进程）、系统负载、内存使用量、磁盘剩余空间
   - **JVM 运行时层**：堆内存使用率趋势、GC 停顿时间趋势、GC 频率、线程数分布
   - **Spring 框架层**：HTTP 请求 QPS 趋势、HTTP 响应时间 P95、HTTP 错误率（4xx/5xx）、HikariCP 连接池状态、日志事件频率
   - **业务应用层**：患者操作次数趋势（按疾病类型分组）、随访操作次数趋势、认证事件统计、数据导出统计、数据库存量统计
3. WHEN 某项指标超过预设阈值（如 ERROR 日志频率 > 5次/分钟、HTTP 错误率 > 5%、P95 响应时间 > 2s） THEN 大盘 SHALL 通过颜色变化（红/黄/绿）直观提示异常状态
4. WHEN 开发人员调整时间范围 THEN 大盘 SHALL 支持查看过去 1 小时、6 小时、24 小时的历史数据
