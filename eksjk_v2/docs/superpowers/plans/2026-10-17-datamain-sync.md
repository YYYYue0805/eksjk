# datamain 第三方数据同步 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在本地 MySQL 完成 datamain 5354 名患者及全部明细的迁移验证，确认无误后部署到阿里云 staging 并执行同样的同步。

**Architecture:** 新增 `DatamainApiClient` 流式读取 49.6MB cxList 响应；`DatamainSyncService` 负责锁、日志、行哈希与编排；`DatamainSyncTxService` 负责单患者事务写入（患者主体 + 13 类明细）；替换 `EltmController` 的旧 `/getpatient` 同步入口。知意同意书基础记录来自 `consent_json`，正文/签名/PDF 通过 `getzqbyid` 按需补拉。

**Tech Stack:** Java 21、Spring Boot 3.4、MyBatis-Plus 3.5、Jackson Streaming、MySQL 8.0、JUnit 5、Python 验证脚本。

---

## 文件结构

### 新增

| 文件 | 职责 |
|------|------|
| `eksjk_v2/init-sql/31-datamain-sync.sql` | 患者表扩列 + 16 张新表 + 知情同意表扩展 |
| `eksjk-model/.../entity/EltmPatientProfile.java` | 外部患者扩展属性 |
| `eksjk-model/.../entity/EltmPatientRaw.java` | 66 列原始快照 |
| `eksjk-model/.../entity/EltmHospitalBind.java` | 医患医院绑定 |
| `eksjk-model/.../entity/EltmProject.java` | 所属项目 |
| `eksjk-model/.../entity/EltmFormSubmission.java` | 表单提交 |
| `eksjk-model/.../entity/EltmFormAnswer.java` | 表单答案 |
| `eksjk-model/.../entity/EltmPatientPic.java` | 病历图片 |
| `eksjk-model/.../entity/EltmPhysicalRecord.java` | 体征测量记录 |
| `eksjk-model/.../entity/EltmPhysicalItem.java` | 体征属性明细 |
| `eksjk-model/.../entity/EltmAssessmentReport.java` | 测评报告 |
| `eksjk-model/.../entity/EltmExerciseRecord.java` | 运动记录 |
| `eksjk-model/.../entity/EltmExerciseSummary.java` | 运动汇总 |
| `eksjk-model/.../entity/EltmSleepRecord.java` | 睡眠记录 |
| `eksjk-model/.../entity/EltmSleepSummary.java` | 睡眠汇总 |
| `eksjk-model/.../entity/EltmSyncLog.java` | 同步日志 |
| `eksjk-model/.../entity/EltmSyncError.java` | 同步失败明细 |
| `eksjk-mapper/.../mapper/Eltm*Mapper.java` | 上述 16 张表的 Mapper |
| `eksjk-service/.../sync/DatamainApiClient.java` | 流式 HTTP 客户端 |
| `eksjk-service/.../sync/CxListRow.java` | 一行 66 列的访问器 |
| `eksjk-service/.../sync/DatamainSyncService.java` + `Impl` | 同步编排 |
| `eksjk-service/.../sync/DatamainSyncTxService.java` | 单患者事务写入 |
| `eksjk-service/.../sync/DatamainParseUtil.java` | 日期、性别、年龄、JSON 解析 |
| `eksjk-service/.../sync/DatamainDetailParser.java` | 12 类 JSON 明细解析 |
| `eksjk-web/.../controller/EltmController.java` | 改：sync 改用新服务，新增日志查询 |
| `eksjk-service/src/test/.../DatamainParseUtilTest.java` | 解析单元测试 |
| `eksjk-service/src/test/resources/datamain-row-sample.json` | 真实行样本（脱敏） |

### 修改

| 文件 | 修改 |
|------|------|
| `Patient.java` | 增加 `externalId`、`externalUpdatedAt`、`syncRowHash` |
| `InformedConsent.java` | 增加签署记录字段 |
| `application-dev.yml` / `application-cloud.yml` | 增加 `eksjk.datamain.*` 配置 |
| `EltmSyncServiceImpl.java` | 保留但不再被 Controller 使用；后续删除 |

---

## Task 1: 数据库迁移脚本

**Files:**
- Create: `eksjk_v2/init-sql/31-datamain-sync.sql`
- Run locally: `mysql -u eksjk -peksjk123 eksjk_db < ...`

- [ ] 编写脚本：`ALTER TABLE datamain_patient` 增加 `external_id BIGINT UNIQUE`、`external_updated_at`、`sync_row_hash`。
- [ ] 创建 16 张表（DDL 见 `datamain-数据同步对接方案.md` 第 4 章，含 `eltm_physical_record` + `eltm_physical_item`）。
- [ ] 扩展 `informed_consent`，放宽 `file_name`/`oss_url` 非空并增加 sign 相关列、`UNIQUE KEY uk_sign_id`。
- [ ] 本地执行脚本，`SHOW TABLES LIKE 'eltm_%'` 应有 16 张表，`SHOW COLUMNS FROM datamain_patient LIKE 'external_id'` 有结果。
- [ ] 记录执行结果到计划末尾。

## Task 2: 实体与 Mapper

**Files:**
- Create: 16 个 `Eltm*` Entity + 16 个 `Eltm*Mapper`
- Modify: `Patient.java`、`InformedConsent.java`

- [ ] 实体字段与 DDL 一一对应，使用 `@Data`、`@TableName`、`@TableId(type = IdType.AUTO)`。
- [ ] 日期字段统一 `LocalDateTime`，金额/数值按 DDL 使用 `BigDecimal`。
- [ ] Mapper 接口 `extends BaseMapper<T>`，加 `@Mapper`。
- [ ] `mvn compile -pl eksjk-model,eksjk-mapper -am -q` 通过。

## Task 3: 解析工具与行模型

**Files:**
- Create: `DatamainParseUtil.java`、`CxListRow.java`
- Test: `DatamainParseUtilTest.java`

- [ ] `parseDateTime(String)` 支持 `yyyy-MM-dd`、`yyyy-MM-dd HH:mm:ss`、`yyyy-MM-dd'T'HH:mm:ss`、带 `.000000` 的小数秒。
- [ ] `normalizeSex(String genderCode, String genderText)`：1→"1"，2→"2"，男/女兜底。
- [ ] `calcAge(LocalDate birthday)`：返回 `X岁Y月`，不足 1 岁 `Y月`，生日为空返回 null。
- [ ] `parseJson(String)`：空白返回 null；解析失败返回 null 并记录 debug 日志。
- [ ] `CxListRow`：包装 `ArrayNode`，提供 `long id()`、`String str(int)`、`LocalDateTime date(int)`、`JsonNode json(int)`、`String rawJson()`。
- [ ] 单元测试覆盖性别、年龄、日期、JSON 字符串/对象。
- [ ] `mvn test -pl eksjk-service -Dtest=DatamainParseUtilTest -q` 通过。

## Task 4: 流式 HTTP 客户端

**Files:**
- Create: `DatamainApiClient.java`

- [ ] 使用 `java.net.http.HttpClient` + `BodyHandlers.ofInputStream()`。
- [ ] 用 Jackson `JsonParser` 定位 `data.data` 数组，逐行 `mapper.readTree(parser)`，每行回调 `Consumer<ArrayNode>`，不构建全量对象树。
- [ ] 校验 HTTP 200、根 `code == 0`、每行列数 == 66，列数不符记 error 并跳过。
- [ ] 支持连接/读取超时与 3 次重试（5s/30s/120s）。
- [ ] 用本地缓存的 `tmp/datamain-verify/cxlist.json` 写一个临时 main 或测试，确认可逐行读取 5354 行且内存平稳。

## Task 5: 明细解析器

**Files:**
- Create: `DatamainDetailParser.java`
- Test: 使用真实样本行

- [ ] `parseHospitalBinds`：doctor_id/doctor_name/hospital_id/hospital_name/province/city/district/assignment_source。
- [ ] `parseProjects`：project_id/project_name/relation_sources/first_relation_time。
- [ ] `parseForms`：form_data_id、form_key、form_name、form_type、project_*、source_type、serial_number、to_patient_id、patient_plan_id、third_patient_id、submitted_at、complete_time、complete_person_type、score、evaluation_result、result_description、latitude_compute_score、original_data。
- [ ] `parsePics`：pic_id、image_url、image_type、diagnosis_date、medical_record_name、remark、pic_source_type、type_content、create/update。
- [ ] `parsePhysical`：按 `(measure_time)` 分组，属性项保留 attribute_id/code/value、archive_id、form_key、data_source、three_level、archives_type、create/update。
- [ ] `parseReports`：report_id、model_id、model_name、form_key/name、project_id/name、user_form_data_id、score、total_score、evaluation_result、result_description、report_remark_value、report_suggest_value、base_check_date_value、base_next_check_date_value、create/update。
- [ ] `parseExercises` / `parseExerciseSummaries` / `parseSleeps` / `parseSleepSummaries`：按实测字段。
- [ ] `parseConsents`：sign_id、project_id、group_id、informed_consent_id、doctor_id、sign_status、三个签名时间、create/update。
- [ ] 所有解析对 null/空数组/非法日期安全；解析失败的明细写 `eltm_sync_error`，不影响患者主体。
- [ ] 单元测试至少覆盖每个解析器的 1 条真实样本。

## Task 6: 单患者事务写入

**Files:**
- Create: `DatamainSyncTxService.java`

- [ ] `@Transactional(propagation = REQUIRES_NEW, rollbackFor = Exception.class)` 方法 `syncPatient(CxListRow row, String rowHash)`。
- [ ] 按 `external_id` 查患者；不存在则按 `eltm_id`、`idcard`、`phone+name+birthday` 兜底匹配；仍无则新建（`dis_class=10000007`、`del_flg=1`、`case_num` 生成、`audit_status=pending_review`）。
- [ ] 外部字段只写白名单；空值不覆盖本地已有值；不动 `audit_*`、诊断、治疗等字段。
- [ ] 写入/更新 `eltm_patient_profile`、`eltm_patient_raw`（raw_json + row_hash）。
- [ ] 明细写入策略：
  - 项目/医院绑定/表单/图片/报告/运动/睡眠/体征：先按 `patient_id` 删除旧数据，再批量插入（外部数据，可重建）。
  - 知情同意：按 `sign_id` upsert，保留已补拉的 content/签名/PDF 字段。
- [ ] 体征：先插 `eltm_physical_record` 拿 id，再插 `eltm_physical_item`。
- [ ] 事务失败回滚该患者，异常向上抛给编排层记录 `eltm_sync_error`。

## Task 7: 同步编排

**Files:**
- Create: `DatamainSyncService.java` + `DatamainSyncServiceImpl.java`

- [ ] `syncFromExternal(String triggerType)`：
  1. `GET_LOCK('eltm_sync', 0)`，拿不到抛“同步进行中”。
  2. 插入 `eltm_sync_log`（RUNNING）。
  3. 流式读取 cxList，逐行计算 SHA-256（66 列规范化 JSON）。
  4. 已存在且 hash 相同 → skipped++；否则调用 TxService。
  5. 每 100 行打印进度。
  6. 更新日志 SUCCESS/PARTIAL/FAILED，释放锁。
- [ ] 返回 `{total, newCount, updatedCount, skippedCount, failedCount, elapsedSeconds}`。
- [ ] `@Scheduled(cron = "${eksjk.datamain.sync-cron:0 10 2 * * ?}")`，受 `eksjk.datamain.sync-enabled` 控制。
- [ ] 并发保护：`AtomicBoolean` + MySQL 锁双重保护。

## Task 8: Controller 与配置

**Files:**
- Modify: `EltmController.java`
- Modify: `application-dev.yml`、`application-cloud.yml`

- [ ] `POST /api/eltm/sync` 改为调用 `DatamainSyncService`，权限 `@SaCheckRole("super_admin")`。
- [ ] 新增 `GET /api/eltm/sync-logs?page=1&size=20`。
- [ ] 配置：
```yaml
eksjk:
  datamain:
    base-url: http://36.26.56.94:8026
    cx-list-path: /datamain/cxList
    connect-timeout-seconds: 10
    read-timeout-seconds: 180
    max-retry: 3
    sync-enabled: false
    sync-cron: "0 10 2 * * ?"
```
- [ ] 本地启动后端，`curl -X POST localhost:8080/api/eltm/sync` 能触发同步。

## Task 9: 本地验证

- [ ] 启动本地后端（`mvn spring-boot:run -pl eksjk-web -Dspring-boot.run.profiles=dev`）。
- [ ] 登录取 token，触发同步，等待完成。
- [ ] 校验 SQL：
```sql
SELECT COUNT(*) FROM datamain_patient WHERE external_id IS NOT NULL;              -- 期望 5354
SELECT COUNT(*) FROM eltm_patient_raw;                                            -- 期望 5354
SELECT COUNT(*) FROM eltm_patient_pic;                                            -- 期望 759
SELECT COUNT(*) FROM eltm_form_submission;                                        -- 期望 2561
SELECT COUNT(*) FROM eltm_physical_record;                                        -- 期望约 5295
SELECT COUNT(*) FROM eltm_physical_item;                                          -- 期望 78045
SELECT COUNT(*) FROM eltm_assessment_report;                                      -- 期望 69
SELECT COUNT(*) FROM informed_consent WHERE sign_id IS NOT NULL;                  -- 期望 3626
SELECT COUNT(*) FROM eltm_sync_error;                                             -- 期望 0
```
- [ ] 再次触发同步，第二次 `updatedCount=0`、`skippedCount=5354`，行数不变。
- [ ] 抽查 1 名有完整明细的患者，核对姓名、性别、生日、医院、图片 URL、体征值。
- [ ] 抽样 5 名患者调用 `getzqbyid` 补拉正文/签名/PDF，确认 `content`、`patient_sign_image`、`pdf_path` 有值。

## Task 10: 构建与 staging 部署

- [ ] 本地 `mvn clean package -pl eksjk-web -am -DskipTests -q` 通过。
- [ ] 备份 staging RDS（至少 `mysqldump` 相关表或创建快照）。
- [ ] 构建后端镜像并推送 ACR（tag 用当前 HEAD SHA）。
- [ ] `kubectl rollout restart deployment/eksjk-backend -n eksjk-staging`，确认 Pod 就绪。
- [ ] 在 staging 执行 `31-datamain-sync.sql`（通过 Pod 连 RDS 或跳板机）。
- [ ] 开启 `eksjk.datamain.sync-enabled` 或手动调用同步接口。
- [ ] 用同样的 SQL 校验行数；对比本地与 staging 的 `total/new/updated/skipped/failed`。

## Task 11: 文档与提交

- [ ] 更新 `datamain-数据同步对接方案.md` 的“实施状态”与实测结果。
- [ ] 提交代码与 SQL（不提交 `tmp/`）。
- [ ] 记录 staging 同步日志 ID 与校验结果。

---

## 风险与回滚

| 风险 | 处理 |
|------|------|
| 5354 患者写入导致本地库膨胀 | 本地库仅测试，保留原有数据；可随时按 `external_id IS NOT NULL` 清理 |
| staging 同步失败 | 先备份 RDS；同步为幂等 upsert；失败只影响当前患者事务 |
| 1Gi 内存限制 OOM | 流式解析，不构建全量对象树；同步期间观察 Pod 内存 |
| 外部接口字段变化 | 列数校验 + `raw_json` 留档；失败行写 `eltm_sync_error` |
| 旧同步入口冲突 | Controller 切到新服务，旧实现保留但不再调用 |

## 执行记录

### 2026-10-17 本地迁移

| 项目 | 结果 |
|------|------|
| 迁移脚本 | `eksjk_v2/init-sql/31-datamain-sync.sql` 已在本地 `eksjk_db` 执行 |
| 偏差 1 | `datamain_patient` 已接近行大小上限，只新增 `external_id`；`external_updated_at`、`row_hash` 放在 `eltm_patient_raw` |
| 偏差 2 | `datamain_patient.birth_time` 从 TIMESTAMP 改为 DATETIME（外部存在 1970 年前生日） |
| 偏差 3 | `eltm_patient_profile.latest_form_name` 扩大到 VARCHAR(500) |
| 代码 | 16 个 Entity/Mapper、流式客户端、12 类明细解析、事务写入、同步编排、Controller、定时任务 |
| 第一次全量 | 5354 行，新增 5195，失败 159（150 档案重复 + 8 生日超范围 + 1 字段超长） |
| 第二次全量 | 新增 159，更新 30，跳过 5165，失败 0，耗时 40 秒 |
| 数据校验 | patients=5354、raw=5354、profiles=5354、hospital_bind=4107、projects=4028、forms=2561、answers=24098、pics=759、physical_records=5296、physical_items=78059、reports=69、exercise=1、sleep=1、consents=3626 |
| 一致性 | 外部 id 无重复、raw 无孤儿、最新日志错误 0 |
| 同意书补拉 | 抽样 5 名患者调用 `POST /api/eltm/sync/consent/{externalId}`，正文 7407/7665 字符，患者签名 URL 已落库；base64 大图暂不落库 |
| 待办 | 第三次全量幂等验证；staging 备份 + 部署 + 同步 |
