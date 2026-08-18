# 知情同意书模块 设计文档

- **日期**：2026-08-18
- **状态**：已确认
- **范围**：`eksjk_v2`（后端 Spring Boot 3 / 前端 Vue 3）

## 1. 背景与目标

为 EKSJK 病例详情页新增「知情同意书」模块，展示从 E路童萌（ELTM）微信端同步的已签署知情同意书文件。

**核心约束**：
- 知情同意书只能从 E路童萌微信端同步，**不允许手动上传**
- 外部对接阶段先留占位，用 mock 数据跑通完整链路，真实接口地址/字段可后续替换
- 通过 `eltm_id` 将外部记录关联到本系统患者

**用户已确认的决策**：
| 决策点 | 结论 |
|--------|------|
| 外部接口现状 | 先做平台侧，对接留占位（设计好接口和定时任务框架，mock 数据跑通） |
| 同步触发方式 | 定时轮询（每天凌晨 2 点）+ 手动触发 |
| 医生列语义 | 展示公众号端新增病例的医生（同步数据带出） |
| 关联键 | 用 `eltm_id` 关联本系统患者 |

## 2. 页面与交互

### 2.1 病例详情新增 Tab

位置：在「GH不良事件」右侧新增「知情同意书」，顺序为：

```
基本信息 / 基线-临床信息 / 基线-辅助检查 / 遗传学检查 / 随访记录 / 诊断 / GH 不良事件 / 知情同意书(新增)
```

### 2.2 Tab 内容

- 参照遗传学检查的文件列表布局（`report-section` 样式），**无上传按钮**（只读）
- 显示已签署的知情同意书文件列表，字段：

| 字段 | 说明 |
|------|------|
| 文件名称 | `file_name` |
| 签署时间 | `sign_time` |
| 患者姓名 | `patient_name`（取当前病例姓名即可，外部记录冗余保存） |
| 来源 | 固定显示「E路童萌」 |
| 操作 | 查看 / 下载（使用返回的 `ossUrl` 直接打开） |

- 无数据时显示 `el-empty` 空状态

## 3. 数据存储

新建独立表 `informed_consent`（不用现有 `file_note`，字段不够）：

```sql
CREATE TABLE `informed_consent` (
  `id`              BIGINT AUTO_INCREMENT PRIMARY KEY,
  `patient_id`      BIGINT NOT NULL COMMENT '本系统患者ID',
  `eltm_id`         VARCHAR(64) DEFAULT NULL COMMENT 'E路童萌患者唯一标识',
  `external_file_id` VARCHAR(128) DEFAULT NULL COMMENT 'E路童萌外部文件ID',
  `file_name`       VARCHAR(255) NOT NULL COMMENT '文件名称',
  `oss_url`         VARCHAR(512) NOT NULL COMMENT 'OSS 文件地址',
  `sign_time`       DATETIME DEFAULT NULL COMMENT '签署时间',
  `patient_name`    VARCHAR(64) DEFAULT NULL COMMENT '患者姓名',
  `doctor_name`     VARCHAR(64) DEFAULT NULL COMMENT '公众号端医生姓名',
  `source`          VARCHAR(32) DEFAULT 'ELTM' COMMENT '来源',
  `sync_time`       DATETIME DEFAULT NULL COMMENT '同步时间',
  `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_deleted`      TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_patient_id` (`patient_id`),
  KEY `idx_eltm_id` (`eltm_id`)
) COMMENT='知情同意书';
```

- 逻辑删除遵循 MyBatis-Plus `@TableLogic`（`is_deleted`），与 `datamain_patient` 一致
- 幂等：按 `external_file_id` 去重（同一外部文件只保留一条）

## 4. 后端设计

### 4.1 分层结构（遵循现有三层架构）

| 层 | 类 | 说明 |
|----|-----|------|
| Entity | `InformedConsent` | `@TableName("informed_consent")` |
| Mapper | `InformedConsentMapper` | 继承 `BaseMapper` |
| VO | `InformedConsentVO` | 对外返回：id, patientId, externalFileId, fileName, signTime, patientName, doctorName, source, ossUrl |
| Service | `InformedConsentService`（接口）+ `InformedConsentServiceImpl` | 列表查询、同步 |
| Sync | `InformedConsentSyncService`（接口）+ `InformedConsentSyncServiceImpl` | 外部同步（占位） |
| Controller | `InformedConsentController` | `/api/informed-consent/*` |

### 4.2 接口清单

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | `/api/informed-consent/list-by-patient?patientId=xxx` | 按患者查列表 | `@SaCheckLogin` |
| POST | `/api/informed-consent/sync` | 手动触发同步 | `@SaCheckRole`（super_admin / hospital_admin） |
| GET | `/api/informed-consent/sync-status` | 最近同步状态 | `@SaCheckLogin` |

**不提供** create/upload/delete 接口（只读 + 同步）。

- `patientId` 使用 Hashids 编码，Controller 中 `HashidsUtil.decode`
- 返回格式统一 `R<T>`（`{ code, message, data }`）

### 4.3 同步服务（占位）

- 配置项（`application.yml` + dev/cloud 覆盖）：

```yaml
eksjk:
  informed-consent:
    sync-url: http://36.26.56.94:8026/datamain/getconsent  # 占位，对接后替换
    source: ELTM
```

- `InformedConsentSyncServiceImpl.syncFromExternal()`：
  1. 调外部接口（占位阶段直接构造 mock 数据，不走真实 HTTP）
  2. 解析记录（预期字段：eltmId、fileId、fileName、ossUrl、signTime、patientName、doctorName）
  3. 按 `eltm_id` 匹配本系统患者（`datamain_patient.eltm_id`）
  4. 按 `external_file_id` upsert（存在则更新，不存在则插入）
  5. 返回统计：`{ total, newCount, updatedCount, failedCount }`

- **定时任务**：`@Scheduled(cron = "0 0 2 * * ?")` 每天凌晨 2:00 执行（放在 sync 实现类的独立方法，便于单独关闭）

```java
@Scheduled(cron = "0 0 2 * * ?")
public void scheduledSync() {
    informedConsentSyncService.syncFromExternal();
}
```

### 4.4 病例列表标记 + 医生列

- `PatientVO` 新增字段：
  - `informedConsentStatus`：`signed` / `not_signed`
  - `doctorName`：公众号端医生姓名
- `PatientServiceImpl.queryPage` 扩展（复用现有 `latestFollowUpMap` 的批量查询模式）：
  - 批量查询本页患者集合的知情同意书记录（`in` patientId 集合，`is_deleted=0`）
  - 按 `patient_id` 分组
  - 有记录 → `informedConsentStatus = signed`
  - `doctorName` 取该患者**最近一条**（`sign_time` 倒序）的 `doctor_name`
- `PatientServiceImpl.convertToVO` 填充上述两个字段

## 5. 前端设计

### 5.1 文件清单

| 文件 | 变更 |
|------|------|
| `src/views/case/CaseDetail.vue` | `tabList` 追加知情同意书；新增 `<el-tab-pane name="informedConsent">` |
| `src/views/case/components/InformedConsentList.vue` | **新增**，只读文件列表组件 |
| `src/views/case/CaseList.vue` | 新增「知情同意书」「医生」两列 |
| `src/api/informedConsent.js` | **新增**，接口封装 |

### 5.2 `CaseDetail.vue`

```js
const tabList = computed(() => [
  ...,
  { name: 'ghAdverseEvent', label: 'GH不良事件' },
  { name: 'informedConsent', label: '知情同意书' }
])
```

```html
<el-tab-pane label="知情同意书" name="informedConsent" lazy>
  <InformedConsentList :patient-id="patientId" />
</el-tab-pane>
```

组件异步加载：`defineAsyncComponent(() => import('@/views/case/components/InformedConsentList.vue'))`

### 5.3 `InformedConsentList.vue`

- props：`patientId`（String，Hashids 编码）
- 参照遗传学检查 `report-section` 样式，但**无上传按钮**
- 加载：`onMounted` / `watch(patientId)` 调 `getInformedConsentList(patientId)`
- 列表项：文件名 + 签署时间 + 患者姓名 + 来源（E路童萌）+ 操作（查看/下载，`window.open(ossUrl)`）
- 空状态：`el-empty description="暂无知情同意书"`

### 5.4 `CaseList.vue` 新增两列

```html
<el-table-column label="知情同意书" width="100" align="center">
  <template #default="{ row }">
    <el-tag v-if="row.informedConsentStatus === 'signed'" type="success" size="small">已签署</el-tag>
    <span v-else style="color:#c0c4cc">未签署</span>
  </template>
</el-table-column>
<el-table-column prop="doctorName" label="医生" width="100" show-overflow-tooltip />
```

### 5.5 API 封装（`src/api/informedConsent.js`）

```js
export function getInformedConsentList(patientId) {
  return request({ url: '/api/informed-consent/list-by-patient', method: 'get', params: { patientId } })
}
export function syncInformedConsent() {
  return request({ url: '/api/informed-consent/sync', method: 'post' })
}
```

## 6. 数据库迁移

新增增量 SQL：`eksjk_v2/init-sql/30-informed-consent.sql`

```sql
CREATE TABLE IF NOT EXISTS `informed_consent` (...);
```

本地 `eksjk_db` 需执行；staging 通过 K8s Pod 连 RDS 执行（遵循现有迁移流程）。

> 说明：迁移序号为 30（原计划 28 与 `28-followup-imaging-lab.sql` 冲突，已修正）。
> 对接真实接口后建议追加 `UNIQUE KEY uk_external_file_id (external_file_id)`，在 schema 层保证同步幂等。

## 7. 测试

- 后端：`mvn clean package -pl eksjk-web -am -DskipTests` 编译通过
- 前端：`npm run build` 编译通过
- 本地联调：
  1. 启动后端（dev）+ 前端
  2. 手动触发 `POST /api/informed-consent/sync`，确认 mock 数据入库
  3. 打开 ELTM 病例详情 → 知情同意书 Tab，确认列表展示
  4. 病例列表确认「已签署」标签和「医生」列
- 占位 mock：可在 dev profile 下构造 1-2 条对应已同步 ELTM 患者（eltm_id 匹配）的知情同意书记录

## 8. 待真实对接时替换项

- `eksjk.informed-consent.sync-url` 指向真实接口；`eksjk.informed-consent.sync-enabled` 已作为定时同步开关（占位阶段生产默认 `false`，本地 dev 为 `true`）
- 占位 mock 逻辑替换为真实 HTTP 调用 + OAuth/Token 认证
- `oss_url` 若为带签名临时 URL，考虑改为后端代理下载接口
- 医生字段来源若为 ELTM 病例同步（getpatient）而非知情同意书接口，改为从 `patient` 表取 `doctor_name`
- 追加 `UNIQUE KEY uk_external_file_id` 保证同步幂等
