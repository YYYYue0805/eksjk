# datamain 第三方数据同步对接方案

> 编写日期：2026-10-17
> 数据源：`http://36.26.56.94:8026`（datamain 接口文档-整理版.docx，2026-10-16 实测）
> 对接目标：EKSJK V2（`eksjk_v2`），替换现有基于 `/datamain/getpatient` 的 ELTM 同步
> 落库策略：全量关系化（患者基本信息 + 各业务明细分别建表）
> 同步方式：首次全量 + 每日定时 + 手动触发

---

## 0. 结论摘要

1. **三个接口的关系**：`cxList` 返回患者主体和明细 JSON（表单、图片、体征、报告、运动、睡眠、知情同意的**基础记录**）。实测发现 **`consent_json` 不含知情同意书正文、签名图片和 PDF 字段，这些只能通过 `getzqbyid` 获取**；`gettpbyid` 与 `pic_json` 数据重复，日常无需逐患者调用；`getzqbyid` 建议按“变更患者增量补拉 + 查看时按需补拉”处理。
2. **患者主键**：以 `cxList` 列 0（外部患者 id）作为唯一业务键。`datamain_patient` 新增 `external_id BIGINT UNIQUE`，不要再依赖旧的 `eltm_id` 字符串。
3. **列 54-65 是 JSON 明细字符串**（不是嵌套对象，需二次 `json.loads`）：按明细类型拆表；实测已确认全部内部字段（见附录 C），表单用 `form_data_id`、图片用 `pic_id`、报告用 `report_id`、运动/睡眠用 `record_id`、签署用 `sign_id` 作为幂等键，不再依赖哈希猜键。
4. **`cxList` 每次全量约 49.6MB、10 秒、5354 行（2026-10-17 实测）、无分页无增量参数**：采用“全量拉取 + 行指纹比对 + 仅变更行落库”的伪增量策略；定时任务每日凌晨执行，手动按钮复用同一入口。
5. **体征数据是 EAV 结构**：`physical_json` 每行是“一个属性一条记录”（`attribute_code` / `attribute_value` / `measure_time`），实测 2347 名患者共 78,045 条属性、5,295 次测量；落库为“测量记录 + 属性明细”两张表，而不是宽表。
6. **直接替换旧同步**：删除 `EltmSyncServiceImpl` 中 `/getpatient` 行式解析逻辑，改为新的 `CxListSyncService`；`EltmController` 的 `/api/eltm/sync` 接口保持不变。
7. **必须保护本地数据**：外部空值不得覆盖本地已有值；审核状态、诊断、治疗方案等医生编辑字段不在同步覆盖范围内。

---

## 1. 接口现状

### 1.1 统一约定

| 项目 | 说明 |
|------|------|
| 基础地址 | `http://36.26.56.94:8026` |
| 请求方式 | GET |
| 返回结构 | `{"code":0,"data":{"data":[[列0,列1,...],...]}}`，二维数组、**无字段名** |
| 鉴权 | 无（实测明文 HTTP 直接访问） |
| 编码 | JSON / UTF-8 |
| 列序风险 | 对方一旦加列或调序，我方会整体错位，必须固化列序 + 启动时校验列数 |

### 1.2 三个接口

| 接口 | 用途 | 规模（2026-10-17 实测） | 是否日常调用 |
|------|------|--------------------------|--------------|
| `GET /datamain/cxList` | 全部患者及全部明细 JSON | 5354 行 × 66 列，约 49.6MB，约 10 秒 | 是 |
| `GET /datamain/gettpbyid?queryId={患者id}` | 单患者病历图片 | 5354 人中 181 人有图片记录 | 否，与 `pic_json` 重复，仅补拉/核对 |
| `GET /datamain/getzqbyid?queryId={患者id}` | 单患者知情同意书完整记录（正文、签名、PDF） | 5354 人中 3242 人有签署记录，共 3626 条 | 变更患者增量补拉；查看时按需补拉 |

### 1.3 cxList 的 66 列结构

- 列 0-53：患者主体与各类统计字段（标量）。
- 列 54-65：注册扩展、医院绑定、项目、表单、图片、体征、报告、运动、运动汇总、睡眠、睡眠汇总、知情同意，共 12 个 JSON 明细列。
- 列 7 `age` 口径混乱（2026-10-17 实测：空值 1461、纯数字串 3789、中文年龄串 99、其他 5），**明确不使用该列**，年龄一律由列 6 出生日期换算。
- 列 22/23 是少量有值的数字 id（分别 1422/1421 行有值，疑似旧系统患者/用户 id）；列 26/28/36/40/44/48 是数量类字段（全非空，范围 0~15+）。含义仍待确认，首期只存入 `raw_json`。

详细逐列映射见 [附录 A](#附录-a-cxlist-66-列逐列映射)。

---

## 2. 本系统数据库现状

### 2.1 相关表清单（本地 `eksjk_db` 实测）

| 表名 | 列数 | 用途 | 与新接口的关系 |
|------|------|------|----------------|
| `datamain_patient` | 210 | 患者主表（基本信息 + 临床字段 + 审核字段） | 列 0-14 等主体字段落此表 |
| `datamain_szfyeltm` | 35 | ELTM 病例子表（评估、诊断、基因、既往史） | 与 cxList 无直接字段对应；对应文档第六节的 ELTM Excel |
| `datamain_patfoll` | 132 | 随访记录（体格、化验、检查） | cxList 不提供逐次随访，仅提供最新统计 |
| `informed_consent` | 13 | 知情同意书（当前按 PDF 文件设计） | 需扩展以承载 sign 记录 |
| `file_note` | 5 | 文件备注（仅 file_path + note，无 patient_id） | 不能直接承载外部图片 |
| `login_user` / `login_unit` | - | 用户/医院 | 外部 doctor_id、hospital_id 无对应关系 |

**结论：现有表只能承接患者主信息；图片、表单、体征、报告、运动、睡眠、签署记录均无对应表，需要新增。**

### 2.2 现有同步实现的问题

现有 `EltmSyncServiceImpl` 对接的是 `/datamain/getpatient`，返回“一字段一行”的 8 列数据：

| 问题 | 说明 |
|------|------|
| 数据源不同 | 新接口是 66 列宽表，旧的 8 列行式解析逻辑完全不适用 |
| 映射字段过少 | 只落 name/age/sex/phone/idCard/birthday 6 个字段 |
| 无幂等键 | `eltm_id` 无唯一索引，重复同步依赖先查后写 |
| 明细全部丢弃 | 表单、图片、体征、报告、知情同意等数据无法入库 |
| 事务失效 | `upsertPatient()` 是同类内部调用，`@Transactional` 不生效 |
| 无同步日志 | 失败只打日志，无法追溯 |

---

## 3. 字段差异分析

### 3.1 差异类型总览

| 差异类型 | 示例 | 处理策略 |
|----------|------|----------|
| 主键体系不同 | 外部 `id`（列 0） vs 本地自增 id / 旧 `eltm_id` | 新增 `external_id` 作为唯一业务键 |
| 一列拆多列 | `phone` → `self_tel` + `contacts_num` | 同步写两列 |
| 一列拆多表 | 列 54-65 JSON → 10 张明细表 | 逐类解析，原 JSON 同时留档 |
| 编码不一致 | `gender_code` 1/2 vs 本地 sex `'1'/'2'` | 以 gender_code 为准，文本兜底 |
| 日期格式不一 | `birthday`、`created_at` | 统一解析为 `yyyy-MM-dd HH:mm:ss` |
| 口径不一致 | `age` 列 | 不使用，按出生日期换算 |
| 本地无对应字段 | 注册来源、证件类型、项目、归属方式等 | 新增 `eltm_patient_profile` 承载 |
| 明细结构未完全确认 | 体征/报告/运动/睡眠 JSON 内部字段 | 先建表 + `raw_json`，确认后补列 |
| 本地保护字段 | 审核状态、诊断、治疗方案 | 同步白名单控制，禁止覆盖 |

### 3.2 患者主体字段映射（cxList 列 0-21、24-25）

| 列 | 外部字段 | 目标 | 转换规则 |
|----|----------|------|----------|
| 0 | id | `datamain_patient.external_id`（新增） | 唯一键；同时写入 `eltm_patient_profile.external_id` |
| 1 | name | `datamain_patient.name` | trim；空值不覆盖本地已有姓名 |
| 2 | phone | `datamain_patient.self_tel`、`contacts_num` | 去空格/横线 |
| 3 | id_card | `datamain_patient.idcard`、`card` | 长度 < 19 才写入；与本地不一致时不覆盖已有值 |
| 4 | gender_code | `datamain_patient.sex` | `1`→`'1'`（男），`2`→`'2'`（女） |
| 5 | gender | 仅兜底 | 当列 4 为空时：`男`→`1`，`女`→`2` |
| 6 | birthday | `datamain_patient.birth_time` | 解析为当日 00:00:00；非法值置空并记录错误 |
| 7 | age | 不落库 | 由 `birth_time` 换算，格式 `X岁Y月`；原始值进 `raw_json` |
| 8 | register_source | `eltm_patient_profile.register_source` | 原样 |
| 9 | id_type | `eltm_patient_profile.id_type` | 原样 |
| 10 | created_at | `datamain_patient.c_time`、`profile.external_created_at` | 解析日期时间 |
| 11 | doctor_name | `datamain_patient.doctor_name` | 原样 |
| 12 | doctor_id | `eltm_patient_profile.external_doctor_id` | 原样，暂不映射 login_user |
| 13 | hospital_name | `datamain_patient.hospital_name` | 原样；作为列表筛选依据 |
| 14 | hospital_id | `eltm_patient_profile.external_hospital_id` | 原样，暂不映射 login_unit |
| 15-18 | first_doctor/hospital_* | `eltm_patient_profile.first_*` | 原样（分号分隔文本） |
| 19 | assign_type | `eltm_patient_profile.assign_type` | 原样 |
| 20-21 | project_count / project_name | `eltm_patient_profile` | 原样 |
| 24 | patient_type | `eltm_patient_profile.patient_type` | 原样 |
| 25 | data_scope | `eltm_patient_profile.data_scope` | 原样 |

固定写入值（新建患者时）：

| 字段 | 值 |
|------|-----|
| `dis_class` | `10000007`（ELTM） |
| `del_flg` | `1` |
| `case_num` | 沿用现有 `ELTMyyyyMM + 三位序号` 规则生成 |
| `audit_status` | `pending_review` |
| `sync_status` / `sync_time` | `synced` / 当前时间 |
| `imp_per` / `modify_per` | `ELTM-SYNC` |
| `one_time` | 当前时间 |

### 3.3 统计字段映射（cxList 列 27、29-53）

| 列 | 外部字段 | 目标 |
|----|----------|------|
| 27 | form_count | `eltm_patient_profile.form_count` |
| 29-31 | latest_form_time / update / name | `profile.latest_form_*` |
| 32-34 | pic_count / latest_pic_* | `profile.pic_count`、`latest_pic_*` |
| 35-38 | physical_sign_count / latest_sign_* | `profile.physical_sign_count`、`latest_sign_*` |
| 39-42 | report_count / latest_report_* | `profile.report_count`、`latest_report_*` |
| 43-46 | exercise_flag / latest_exercise_* | `profile.exercise_flag`、`latest_exercise_*` |
| 47-50 | sleep_flag / latest_sleep_* | `profile.sleep_flag`、`latest_sleep_*` |
| 51-53 | consent_count / latest_consent_* | `profile.consent_count`、`latest_consent_*` |
| 22/23/26/28/36/40/44/48 | 待确认 | 仅存 `eltm_patient_raw.raw_json` |

### 3.4 JSON 明细列映射（cxList 列 54-65）

| 列 | JSON 列 | 目标表 | 解析策略 |
|----|---------|--------|----------|
| 54 | extend_data_json | 不入独立表 | 已知键 name/sex/birthday/registerSource 仅作兜底；全文进 raw |
| 55 | hospital_bind_json | `eltm_hospital_bind` | 一对多，逐条 upsert |
| 56 | project_json | `eltm_project` | 一对多，逐条 upsert |
| 57 | form_json | `eltm_form_submission` + `eltm_form_answer` | 以 `form_data_id` 唯一；original_data 尽力拆答案表，拆不出保留 JSON |
| 58 | pic_json | `eltm_patient_pic` | 以 `pic_id` 唯一；字段含 `type_content`、`diagnosis_date`、`medical_record_name` |
| 59 | physical_json | `eltm_physical_record` + `eltm_physical_item` | EAV：按 `(patient_id, measure_time)` 建测量记录，按属性建明细 |
| 60 | report_json | `eltm_assessment_report` | 以 `report_id` 唯一；扩展 model_id/project_id/result_description 等 |
| 61 | exercise_json | `eltm_exercise_record` | 以 `record_id` 唯一；含分类、MET、强度等字段 |
| 62 | exercise_summary_json | `eltm_exercise_summary` | 以 `summary_id` 唯一；按 `summary_date` 汇总 |
| 63 | sleep_json | `eltm_sleep_record` | 以 `record_id` 唯一；含起止时间、时长、睡眠习惯等 |
| 64 | sleep_summary_json | `eltm_sleep_summary` | 以 `summary_id` 唯一；按 `summary_date` 汇总 |
| 65 | consent_json | `informed_consent`（扩展） | 以 `sign_id` 唯一；**仅基础字段，正文/签名/PDF 需 `getzqbyid` 补充** |

### 3.5 gettpbyid 16 列映射

| 列 | 外部字段 | 目标 | 说明 |
|----|----------|------|------|
| 0 | pic_id | `eltm_patient_pic.pic_id` | 唯一键 |
| 1 | patient_id | `patient_id` / `external_patient_id` | 映射到本地患者 |
| 2 | 待确认（长整型 id） | `raw_json` | 待确认 |
| 3 | record_date | `record_date` | 日期 |
| 4 | record_name | `record_name` | 如“血型”“血糖”“性激素” |
| 5 | image_url | `image_url` | 外部 HTTPS 地址 |
| 6-7 | create_time / update_time | `external_create_time` / `external_update_time` | |
| 8-9 | 疑似上传医生 id | `raw_json` | 待确认 |
| 10 | pic_source_type | `pic_source_type` | 抽样均为 0 |
| 11 | source | `source` | 抽样均为 follow |
| 12 | 待确认（0/1/3） | `raw_json` | 字典待确认 |
| 13 | 待确认（关联 id） | `raw_json` | 待确认 |
| 14 | image_type | `image_type` | 0 检验 / 1 检查 / 2 影像 / 3 病历 / 4 处方 / 5 其它 / 6 出院小结 |
| 15 | 疑似 remark | `remark` | 字段名待确认 |

> 日常同步优先解析 cxList 列 58 的 `pic_json`，不再逐个患者调用 `gettpbyid`；该接口用于补拉和核对。
> 实测差异：`pic_json` 没有 `source` 字段，但多出 `type_content`、`diagnosis_date`、`medical_record_name`；`gettpbyid` 的 `record_date` 对应 `pic_json.diagnosis_date`，`record_name` 对应 `medical_record_name`。

### 3.6 getzqbyid 25 列映射

| 列 | 外部字段 | 目标（informed_consent 新增列） | 说明 |
|----|----------|-------------------------------|------|
| 0 | sign_id | `sign_id` | 唯一键 |
| 1 | project_id | `project_id` | |
| 2 | group_id | `group_id` | |
| 3 | informed_consent_id | `external_consent_id` | 外部模板 id |
| 4 | patient_id | `patient_id` / `external_patient_id` | 映射本地患者 |
| 5-6 | create_time / update_time | `external_create_time` / `external_update_time` | |
| 7-8 | 医生 id | `sign_doctor_id` / `raw_json` | 待确认 |
| 9 | content | `content` | HTML 富文本；展示时需前端转义/白名单 |
| 10 | sign_doctor_id | `sign_doctor_id` | 待确认 |
| 11 | patient_sign_image | `patient_sign_image` | URL 或 base64，见 5.4 |
| 12 | patient_sign_time | `patient_sign_time` / `sign_time` | |
| 13 | guardian_sign_image | `guardian_sign_image` | 同 5.4 |
| 14 | guardian_sign_time | `guardian_sign_time` | |
| 15 | doctor_sign_image | `doctor_sign_image` | 同 5.4 |
| 16 | doctor_sign_time | `doctor_sign_time` | |
| 17 | sign_status | `sign_status` | 字典待确认（抽样 1/3/4） |
| 18 | source | `source` | |
| 19 | pdf_path | `pdf_path` | |
| 20 | pdf_file_name | `pdf_file_name` | |
| 21 | pdf_error_msg | `pdf_error_msg` | |
| 22 | pdf_status | `pdf_status` | 0 待生成 / 1 成功 / 2 失败 / 3 跳过 |
| 23 | pdf_retry_count | `pdf_retry_count` | |
| 24 | pdf_generate_time | `pdf_generate_time` | |

> 实测（2026-10-17）：只有 `getzqbyid` 才返回正文（列 9）、患者签名 URL（列 11）、监护人/医生签名 base64（列 13/15）和 PDF 字段（列 19-24）；`consent_json` 仅有 `sign_id`、`project_id`、`group_id`、`informed_consent_id`、`doctor_id`、`sign_status`、三个签名时间和创建/更新时间。另外 PDF 列 21/23 的语义与文档推断（`pdf_error_msg` / `pdf_retry_count`）存在出入，需要对方确认列序。

---

## 4. 目标数据模型

### 4.1 关系概览

```
datamain_patient (1) ──< eltm_patient_profile (1:1，外部扩展属性)
        │
        ├──< eltm_patient_raw (1:1，66 列原始快照)
        ├──< eltm_hospital_bind (1:N)
        ├──< eltm_project (1:N)
        ├──< eltm_form_submission (1:N) ──< eltm_form_answer (1:N)
        ├──< eltm_patient_pic (1:N)
        ├──< eltm_physical_record (1:N) ──< eltm_physical_item (1:N)
        ├──< eltm_assessment_report (1:N)
        ├──< eltm_exercise_record (1:N)
        ├──< eltm_exercise_summary (1:N)
        ├──< eltm_sleep_record (1:N)
        ├──< eltm_sleep_summary (1:N)
        └──< informed_consent (1:N，按 sign_id 唯一)
```

所有明细表都带 `patient_id`（本地主键）、`external_patient_id`（外部列 0）和 `raw_json`，便于回溯。

### 4.2 患者表改造

```sql
ALTER TABLE datamain_patient
  ADD COLUMN external_id BIGINT NULL COMMENT 'datamain 外部患者主键(cxList 列0)',
  ADD COLUMN external_updated_at DATETIME NULL COMMENT '外部数据最近更新时间',
  ADD COLUMN sync_row_hash VARCHAR(64) NULL COMMENT '外部整行指纹，用于伪增量',
  ADD UNIQUE KEY uk_external_id (external_id);
```

> 存量数据处理：先按“旧 eltm_id = 新列 0 字符串”匹配；匹配不到再按“身份证号 → 手机号+姓名+出生日期”兜底；仍未匹配的数据保留原样，不强行合并。

### 4.3 新增表 DDL

```sql
-- 外部患者扩展属性（1:1）
CREATE TABLE eltm_patient_profile (
  id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id             BIGINT NOT NULL,
  external_id            BIGINT NOT NULL,
  register_source        VARCHAR(32),
  id_type                VARCHAR(32),
  external_created_at    DATETIME,
  external_doctor_id     VARCHAR(64),
  external_hospital_id   VARCHAR(64),
  first_doctor_names     VARCHAR(500),
  first_doctor_ids       VARCHAR(500),
  first_hospital_names   VARCHAR(500),
  first_hospital_ids     VARCHAR(500),
  assign_type            VARCHAR(100),
  project_count          INT,
  project_name           VARCHAR(500),
  patient_type           VARCHAR(32),
  data_scope             VARCHAR(64),
  form_count             INT,
  pic_count              INT,
  physical_sign_count    INT,
  report_count           INT,
  exercise_flag          VARCHAR(10),
  sleep_flag             VARCHAR(10),
  consent_count          INT,
  latest_form_time       DATETIME,
  latest_form_update     DATETIME,
  latest_form_name       VARCHAR(200),
  latest_pic_time        DATETIME,
  latest_pic_update      DATETIME,
  latest_sign_time       DATETIME,
  latest_sign_update     DATETIME,
  latest_report_time     DATETIME,
  latest_report_update   DATETIME,
  latest_exercise_time   DATETIME,
  latest_exercise_update DATETIME,
  latest_sleep_time      DATETIME,
  latest_sleep_update    DATETIME,
  latest_consent_time    DATETIME,
  latest_consent_update  DATETIME,
  sync_time              DATETIME,
  create_time            DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_external_id (external_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 外部患者扩展属性';

-- 66 列原始快照（1:1）
CREATE TABLE eltm_patient_raw (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id    BIGINT NOT NULL,
  external_id   BIGINT NOT NULL,
  row_hash      VARCHAR(64),
  raw_json      LONGTEXT,
  sync_time     DATETIME,
  UNIQUE KEY uk_external_id (external_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 患者原始行快照';

-- 医患-医院绑定
CREATE TABLE eltm_hospital_bind (
  id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id             BIGINT NOT NULL,
  external_patient_id    BIGINT NOT NULL,
  external_doctor_id     VARCHAR(64),
  doctor_name            VARCHAR(100),
  external_hospital_id   VARCHAR(64),
  hospital_name          VARCHAR(200),
  province               VARCHAR(50),
  city                   VARCHAR(50),
  district               VARCHAR(50),
  bind_source            VARCHAR(50),
  raw_json               LONGTEXT,
  sync_time              DATETIME,
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 医患医院绑定';

-- 所属项目
CREATE TABLE eltm_project (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id          BIGINT NOT NULL,
  external_patient_id BIGINT NOT NULL,
  external_project_id VARCHAR(64),
  project_name        VARCHAR(500),
  relation_source     VARCHAR(100),
  raw_json            LONGTEXT,
  sync_time           DATETIME,
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 患者所属项目';

-- 表单提交（form_data_id 实测全局唯一）
CREATE TABLE eltm_form_submission (
  id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id              BIGINT NOT NULL,
  external_patient_id     BIGINT NOT NULL,
  form_data_id            BIGINT NOT NULL,
  form_key                VARCHAR(128),
  form_name               VARCHAR(200),
  form_type               VARCHAR(50),
  project_id              VARCHAR(64),
  project_name            VARCHAR(500),
  source_type             VARCHAR(32),
  serial_number           INT,
  to_patient_id           BIGINT,
  patient_plan_id         BIGINT,
  third_patient_id        BIGINT,
  submitted_at            DATETIME,
  complete_time           BIGINT COMMENT '外部毫秒数/秒数，含义待确认',
  complete_person_type    INT,
  score                   VARCHAR(32),
  evaluation_result       TEXT,
  result_description      TEXT,
  latitude_compute_score  LONGTEXT,
  original_data           LONGTEXT COMMENT '原始答题数据 JSON',
  raw_json                LONGTEXT,
  sync_time               DATETIME,
  UNIQUE KEY uk_form_data_id (form_data_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 表单提交';

-- 表单答案（尽力拆分；拆不出时保留 original_data）
CREATE TABLE eltm_form_answer (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  submission_id BIGINT NOT NULL,
  field_key     VARCHAR(128),
  field_label   VARCHAR(200),
  field_value   TEXT,
  raw_json      LONGTEXT,
  KEY idx_submission_id (submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 表单答题明细';

-- 病历图片
CREATE TABLE eltm_patient_pic (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id           BIGINT NOT NULL,
  external_patient_id  BIGINT NOT NULL,
  pic_id               VARCHAR(64) NOT NULL,
  record_date          DATETIME COMMENT 'pic_json.diagnosis_date / gettpbyid.record_date',
  record_name          VARCHAR(200) COMMENT 'pic_json.medical_record_name / gettpbyid.record_name',
  type_content         VARCHAR(128),
  image_url            VARCHAR(512),
  image_type           VARCHAR(10) COMMENT '0检验/1检查/2影像/3病历/4处方/5其它/6出院小结',
  pic_source_type      VARCHAR(10),
  source               VARCHAR(32),
  remark               TEXT,
  external_create_time DATETIME,
  external_update_time DATETIME,
  raw_json             LONGTEXT,
  sync_time            DATETIME,
  UNIQUE KEY uk_pic_id (pic_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 病历图片';

-- 体征测量记录（实测为 EAV：每次测量包含 10~18 个属性）
CREATE TABLE eltm_physical_record (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id          BIGINT NOT NULL,
  external_patient_id BIGINT NOT NULL,
  measure_time        DATETIME NOT NULL,
  data_source         VARCHAR(32),
  archives_type       VARCHAR(32),
  item_count          INT,
  raw_json            LONGTEXT,
  sync_time           DATETIME,
  UNIQUE KEY uk_patient_measure_time (patient_id, measure_time),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 体征测量记录';

-- 体征属性明细（一个属性一行）
CREATE TABLE eltm_physical_item (
  id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
  record_id            BIGINT NOT NULL,
  external_patient_id  BIGINT NOT NULL,
  archive_id           BIGINT,
  form_key             VARCHAR(64),
  attribute_id         BIGINT,
  attribute_code       VARCHAR(128),
  attribute_value      TEXT,
  measure_time         DATETIME,
  data_source          VARCHAR(32),
  three_level          VARCHAR(64),
  archives_type        VARCHAR(32),
  external_create_time DATETIME,
  external_update_time DATETIME,
  raw_json             LONGTEXT,
  sync_time            DATETIME,
  UNIQUE KEY uk_record_attr (record_id, attribute_code),
  KEY idx_external_patient (external_patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 体征属性明细';

-- 测评报告（report_id 实测全局唯一）
CREATE TABLE eltm_assessment_report (
  id                            BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id                    BIGINT NOT NULL,
  external_patient_id           BIGINT NOT NULL,
  report_id                     BIGINT NOT NULL,
  model_id                      BIGINT,
  model_name                    VARCHAR(200),
  form_key                      VARCHAR(64),
  form_name                     VARCHAR(200),
  project_id                    VARCHAR(64),
  project_name                  VARCHAR(500),
  user_form_data_id             BIGINT,
  score                         VARCHAR(32),
  total_score                   VARCHAR(32),
  evaluation_result             TEXT,
  result_description            TEXT,
  report_remark_value           TEXT,
  report_suggest_value          TEXT,
  base_check_date_value         DATETIME,
  base_next_check_date_value    DATETIME,
  report_time                   DATETIME COMMENT 'create_time',
  external_create_time          DATETIME,
  external_update_time          DATETIME,
  raw_json                      LONGTEXT,
  sync_time                     DATETIME,
  UNIQUE KEY uk_report_id (report_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 测评报告';

-- 运动记录（record_id 实测全局唯一）
CREATE TABLE eltm_exercise_record (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id          BIGINT NOT NULL,
  external_patient_id BIGINT NOT NULL,
  record_id           BIGINT NOT NULL,
  record_date         DATE,
  category_name       VARCHAR(100),
  subcategory_name    VARCHAR(100),
  exercise_item_id    BIGINT,
  exercise_name       VARCHAR(200),
  duration_seconds    INT,
  calories_burned     DECIMAL(10,2),
  met_value           DECIMAL(6,2),
  weight_kg           DECIMAL(6,2),
  weight_source       INT,
  intensity_level     INT,
  age_group           VARCHAR(32),
  baby_age_days       INT,
  external_create_time DATETIME,
  external_update_time DATETIME,
  raw_json            LONGTEXT,
  sync_time           DATETIME,
  UNIQUE KEY uk_record_id (record_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 运动记录';

-- 运动汇总（summary_id 实测全局唯一）
CREATE TABLE eltm_exercise_summary (
  id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id              BIGINT NOT NULL,
  external_patient_id     BIGINT NOT NULL,
  summary_id              BIGINT NOT NULL,
  summary_date            DATE,
  total_count             INT,
  total_duration_seconds  INT,
  total_calories          DECIMAL(10,2),
  mvpa_minutes            INT,
  exercise_types          TEXT,
  is_target_reached       TINYINT,
  baby_age_days           INT,
  external_create_time    DATETIME,
  external_update_time    DATETIME,
  summary_json            LONGTEXT,
  raw_json                LONGTEXT,
  sync_time               DATETIME,
  UNIQUE KEY uk_summary_id (summary_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 运动汇总';

-- 睡眠记录（record_id 实测全局唯一）
CREATE TABLE eltm_sleep_record (
  id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id              BIGINT NOT NULL,
  external_patient_id     BIGINT NOT NULL,
  record_id               BIGINT NOT NULL,
  record_date             DATE,
  sleep_start_time        DATETIME,
  sleep_end_time          DATETIME,
  sleep_duration_hours    INT,
  sleep_duration_minutes  INT,
  awake_times             INT,
  sleep_way               VARCHAR(100),
  sleep_habits            TEXT,
  sleep_posture           VARCHAR(100),
  sleep_regularly         TINYINT,
  sleep_env_is_quiet      TINYINT,
  age_group               VARCHAR(32),
  baby_age_days           INT,
  external_create_time    DATETIME,
  external_update_time    DATETIME,
  raw_json                LONGTEXT,
  sync_time               DATETIME,
  UNIQUE KEY uk_record_id (record_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 睡眠记录';

-- 睡眠汇总（summary_id 实测全局唯一）
CREATE TABLE eltm_sleep_summary (
  id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id              BIGINT NOT NULL,
  external_patient_id     BIGINT NOT NULL,
  summary_id              BIGINT NOT NULL,
  summary_date            DATE,
  total_duration_hours    DECIMAL(6,2),
  is_target_reached       TINYINT,
  baby_age_days           INT,
  external_create_time    DATETIME,
  external_update_time    DATETIME,
  summary_json            LONGTEXT,
  raw_json                LONGTEXT,
  sync_time               DATETIME,
  UNIQUE KEY uk_summary_id (summary_id),
  KEY idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 睡眠汇总';

-- 同步日志
CREATE TABLE eltm_sync_log (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  sync_type     VARCHAR(20) NOT NULL COMMENT 'FULL / INCREMENTAL',
  trigger_type  VARCHAR(20) NOT NULL COMMENT 'MANUAL / SCHEDULED',
  start_time    DATETIME NOT NULL,
  end_time      DATETIME,
  total_count   INT DEFAULT 0,
  new_count     INT DEFAULT 0,
  updated_count INT DEFAULT 0,
  skipped_count INT DEFAULT 0,
  failed_count  INT DEFAULT 0,
  status        VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
  error_message TEXT,
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_start_time (start_time),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 数据同步日志';

-- 同步失败明细
CREATE TABLE eltm_sync_error (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  sync_log_id   BIGINT,
  external_id   BIGINT,
  stage         VARCHAR(50) COMMENT 'PATIENT/FORM/PIC/PHYSICAL/REPORT/EXERCISE/SLEEP/CONSENT',
  error_message TEXT,
  raw_json      LONGTEXT,
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_sync_log_id (sync_log_id),
  KEY idx_external_id (external_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM 同步失败明细';
```

### 4.4 知情同意书表改造

```sql
ALTER TABLE informed_consent
  MODIFY COLUMN file_name VARCHAR(255) NULL,
  MODIFY COLUMN oss_url VARCHAR(512) NULL,
  ADD COLUMN sign_id               VARCHAR(64)  NULL COMMENT '外部签署记录ID',
  ADD COLUMN external_patient_id   BIGINT       NULL COMMENT '外部患者ID',
  ADD COLUMN project_id            VARCHAR(64)  NULL,
  ADD COLUMN group_id              VARCHAR(64)  NULL,
  ADD COLUMN external_consent_id   VARCHAR(64)  NULL COMMENT '外部知情同意书模板ID',
  ADD COLUMN content               LONGTEXT     NULL COMMENT 'HTML 正文',
  ADD COLUMN sign_doctor_id        VARCHAR(64)  NULL,
  ADD COLUMN patient_sign_image    VARCHAR(512) NULL COMMENT '签名图片地址',
  ADD COLUMN patient_sign_time     DATETIME     NULL,
  ADD COLUMN guardian_sign_image   VARCHAR(512) NULL,
  ADD COLUMN guardian_sign_time    DATETIME     NULL,
  ADD COLUMN doctor_sign_image     VARCHAR(512) NULL,
  ADD COLUMN doctor_sign_time      DATETIME     NULL,
  ADD COLUMN sign_status           VARCHAR(10)  NULL COMMENT '签署状态，字典待确认',
  ADD COLUMN pdf_path              VARCHAR(512) NULL,
  ADD COLUMN pdf_file_name         VARCHAR(255) NULL,
  ADD COLUMN pdf_error_msg         VARCHAR(500) NULL,
  ADD COLUMN pdf_status            VARCHAR(10)  NULL COMMENT '0待生成/1成功/2失败/3跳过',
  ADD COLUMN pdf_retry_count       INT          NULL,
  ADD COLUMN pdf_generate_time     DATETIME     NULL,
  ADD COLUMN raw_json              LONGTEXT     NULL,
  ADD COLUMN external_create_time  DATETIME     NULL,
  ADD COLUMN external_update_time  DATETIME     NULL,
  ADD UNIQUE KEY uk_sign_id (sign_id);
```

> `sign_time` 取 `patient_sign_time`，为空时退化为 `doctor_sign_time`；`file_name`/`oss_url` 在仅有签名记录、PDF 未生成时允许为空。

---

## 5. 同步流程设计

### 5.1 总体流程

```
定时/手动触发
  └─ 获取同步锁（MySQL GET_LOCK('eltm_sync',0)）
      └─ 写 eltm_sync_log（RUNNING）
          └─ GET /datamain/cxList（流式解析）
              └─ 逐行处理（每行一个患者）
                  ├─ 计算 row_hash → 与 datamain_patient.sync_row_hash 比对
                  │    └─ 未变化：skipped_count++，跳过
                  ├─ 解析标量列 → upsert datamain_patient + profile + raw
                  ├─ 解析 12 个 JSON 列（二次 json.loads）→ 各明细表 upsert
                  ├─ 知情同意有变更的患者 → 调用 getzqbyid 补拉正文/签名/PDF
                  ├─ 单患者事务提交（失败只回滚该患者）
                  └─ 失败：写 eltm_sync_error，继续下一行
          └─ 更新 eltm_sync_log（SUCCESS/PARTIAL/FAILED）
      └─ 释放锁
```

### 5.2 伪增量策略

接口没有分页和增量参数，只能全量下载约 49.6MB（2026-10-17 实测）。为了降低数据库压力和缩短事务时间：

1. 对每行的 66 列原始值做规范化后计算 SHA-256，写入 `datamain_patient.sync_row_hash`。
2. 哈希未变化的患者直接跳过（仍会下载响应体，但不再写库）。
3. 哈希变化的患者才执行患者更新和明细重建。
4. 明细采用“**先按业务唯一键比对、再增删改**”的方式（`form_data_id`、`pic_id`、`report_id`、`record_id`、`summary_id`、`sign_id`），不用整表删除重建，避免丢失本地附加备注。

### 5.3 事务与批处理

- **每患者一个独立事务**：患者主表、profile、raw、各明细在一次事务内完成，单患者失败互不影响。
- 注意：不能在同一个类内部调用带 `@Transactional` 的方法（现有 `EltmSyncServiceImpl` 就存在该问题）。新实现拆成 `CxListSyncService`（编排）+ `CxListSyncTxService`（事务写入）两个 Bean。
- 每处理 100 个患者打印一次进度日志，便于观察 16 秒下载后的落库进度。

### 5.4 图片与签名文件处理

| 数据 | 处理 |
|------|------|
| `image_url`（病历图片） | 直接保存外部 URL，不转存（外部域名 `eksf.zsmm.org.cn`）；如后续要求离线可用再异步转存 |
| 患者签名图片（`getzqbyid` 列 11） | 实测为 URL，直接保存 |
| 监护人/医生签名图片（`getzqbyid` 列 13/15） | 实测为 `data:image/png;base64,...`，单张约 100KB；解码后通过 `FileService` 落存储（本地/S3），DB 只存相对路径 |
| PDF（`pdf_path`） | 保存外部 URL，不转存 |
| HTML 正文（`content`） | 原样保存；前端渲染必须做 XSS 过滤 |

### 5.5 定时与手动

| 项 | 设计 |
|----|------|
| 定时任务 | `@Scheduled(cron = "${eksjk.eltm.sync-cron:0 10 2 * * ?}")`，每日 02:10 |
| 开关 | `eksjk.eltm.sync-enabled`，默认 `false`，预发/生产确认后开启 |
| 手动触发 | 沿用 `POST /api/eltm/sync`，仅 `super_admin` 可调用 |
| 防并发 | MySQL `GET_LOCK`；拿不到锁直接返回“同步进行中” |
| 超时 | 连接 10s，读取 180s；接口实测约 10s，留足余量 |
| 重试 | HTTP 层最多 3 次，退避 5s/30s/120s；解析或入库错误不重试整批 |

### 5.6 HTTP 与内存

- 响应约 49.6MB，**禁止**用 `RestTemplate.getForObject(Map.class)` 一次性反序列化成巨型对象树。
- 使用 Jackson 流式解析：`JsonParser` 逐个读取 `data.data` 的行数组，每行转成 `ArrayNode` 后处理，处理完即释放。
- 建议生产 JVM 堆不低于 1GB；若后续数据量翻倍，再考虑落临时文件后流式读取。
- 请求头带 `Accept-Encoding: gzip`，服务端支持时可显著降低传输量。

### 5.7 删除与缺失处理

- 某患者连续 3 次同步都未出现在 `cxList` 中：只把 `sync_status` 置为 `missing`，**不物理删除、不软删除患者**。
- 明细记录消失：对应业务唯一键的记录标记为失效或删除（明细表为外部数据，可删除；患者主表受保护）。
- 已标记 `missing` 的患者再次出现时恢复为 `synced`。

### 5.8 知情同意书补拉策略（实测后新增）

`consent_json` 只有 12 个基础字段，正文、签名图片、PDF 只能通过 `getzqbyid` 获取。为避免每日对 3242 名患者逐一请求：

| 场景 | 策略 |
|------|------|
| 日常同步 | 只解析 `consent_json`，写入/更新 `informed_consent` 的基础字段（sign_id、状态、签署时间） |
| 变更补拉 | 若某患者的 `latest_consent_update` 或 `consent_json` 哈希发生变化，调用一次 `getzqbyid` 补拉该患者全部签署记录 |
| 首次全量 | 可选：按并发 4 + 限速（如 5 QPS）批量补拉 3242 名患者，预计 10~15 分钟；或先不补拉，等用户查看时按需加载 |
| 查看时补拉 | 前端打开“知情同意”Tab 时，若记录缺少正文/签名/PDF，则调用单患者补拉接口，成功后落库 |
| 失败处理 | 单患者补拉失败记入 `eltm_sync_error`，不影响该患者其他数据；下次同步自动重试 |

---

## 6. 字段转换与本地保护规则

### 6.1 转换规则

| 规则 | 说明 |
|------|------|
| 性别 | 优先 `gender_code`；1→`'1'`，2→`'2'`；文本 男/女 兜底 |
| 年龄 | 不使用列 7；`Period.between(birthday, now)` 换算，格式 `X岁Y月`，不足 1 岁为 `Y月` |
| 日期时间 | 支持 `yyyy-MM-dd`、`yyyy-MM-dd HH:mm:ss`、ISO8601；非法值置空并记 error |
| 手机号 | 去空格、横线；非 11 位仍保存（外部数据质量不可控），但记 warning |
| 身份证 | 仅长度 < 19 才写入；15/18 位直接保存；不在此处做校验位强校验 |
| 姓名 | trim；空值不覆盖本地；新建患者为空时显示为 `未填写姓名-{external_id}`（仅展示层，不写入） |
| JSON | 解析失败时整段写入 `raw_json`，该患者其他字段正常同步，记 error |
| HTML | 原样保存；前端渲染必须做 XSS 过滤 |
| 枚举 | 保存原始码值；展示字典在前端维护，字典未确认前显示原值 |

### 6.2 覆盖白名单（外部可写）

- `datamain_patient`：`name`、`sex`、`birth_time`、`self_tel`、`contacts_num`、`idcard`、`card`、`doctor_name`、`hospital_name`、`c_time`、`sync_status`、`sync_time`、`external_id`、`external_updated_at`、`sync_row_hash`。
- 新增患者时额外写：`dis_class`、`del_flg`、`case_num`、`one_time`、`audit_status=pending_review`、`imp_per`、`modify_per`。
- 明细表：全部外部数据。

### 6.3 本地保护字段（外部只读，禁止覆盖）

- `datamain_patient`：`diagnosis_status`、`audit_status`、`audit_by`、`audit_time`、`release_by`、`release_time`、`audit_remark`、所有临床检查/化验/体格字段、`treatment_opinion`、`surgery_note`、`pathology_result`、`gen_*` 等。
- `datamain_szfyeltm`：诊断、治疗方案、智能诊断结果。
- `informed_consent`：本地录入的 `doctor_name`、`file_name` 仅在外部有值时更新。
- 通用规则：**外部值为 null/空字符串时，一律不覆盖本地非空值**。

---

## 7. 配置与接口

```yaml
eksjk:
  datamain:
    base-url: http://36.26.56.94:8026
    cx-list-path: /datamain/cxList
    pic-path: /datamain/gettpbyid
    consent-path: /datamain/getzqbyid
    sync-enabled: false          # 上线验证后再开启
    sync-cron: "0 10 2 * * ?"    # 每日 02:10
    connect-timeout: 10s
    read-timeout: 180s
    max-retry: 3
```

| 接口 | 说明 |
|------|------|
| `POST /api/eltm/sync` | 手动全量同步（替换旧实现）；`@SaCheckRole("super_admin")` |
| `GET /api/eltm/sync-status` | 同步状态（患者数、最近同步时间、最近日志） |
| `GET /api/eltm/sync-logs` | 同步日志分页查询（新增） |
| `POST /api/eltm/sync/patient/{externalId}` | 单患者补拉（调用 gettpbyid + getzqbyid，新增，可选） |

---

## 8. 实施计划

| 阶段 | 内容 | 产出 |
|------|------|------|
| 0. 确认 | 抓取一份脱敏样本，确认未定义列、sign_status 字典、各 JSON 内部键；确认旧 `eltm_id` 与新列 0 的关系 | 字段确认清单、样本 fixture |
| 1. 表结构 | 执行第 4 章 DDL；新增 Entity/Mapper；为 `datamain_patient` 补 `external_id` 唯一索引 | 迁移脚本 + 模型层 |
| 2. 同步骨架 | `CxListClient`（流式 HTTP）、`CxListSyncService`、`CxListSyncTxService`、`eltm_sync_log/error` | 可跑通患者主表 + profile + raw |
| 3. 明细解析 | 医院绑定、项目、表单/答案、图片、体征、报告、运动、睡眠 | 10 张明细表落库 |
| 4. 同意书 | 扩展 `informed_consent`；签名 base64 落存储；PDF 状态 | 签署记录入库 |
| 5. 替换与调度 | 删除旧 getpatient 逻辑；接入定时任务、开关、锁、手动接口 | 生产可用 |
| 6. 验证 | 全量同步两次（幂等）、变更行同步、异常行、性能、脱敏 | 验收报告 |

> 文档第六节提到的 ELTM 测试 Excel（3329 行 × 33 列）不属于上述三个接口，需单独做导入脚本；其 `patient_id` 对应 cxList 列 0，映射目标为 `datamain_szfyeltm`，详见附录 B。

---

## 9. 测试与验收

| 用例 | 预期 |
|------|------|
| 同一份响应连续同步两次 | 第二次 `new_count=0`、`updated_count=0`、无重复数据 |
| 修改样本中一个患者的姓名/电话 | 只更新该患者，其余全部 skipped |
| 某患者 JSON 明细损坏 | 患者主体成功，系统生成一条 `eltm_sync_error`，整批不中断 |
| 外部字段为空、本地已有值 | 本地值保持不变 |
| 身份证明文超长（≥19） | 不写入 `idcard/card` |
| `age` 列异常（天数/中文串） | 不影响落库，年龄按生日计算正确 |
| 5328 行全量同步 | 单次耗时可控、内存不超限、日志完整 |
| 并发触发两次同步 | 第二次拿不到锁，直接返回“同步进行中” |
| 权限 | 非 `super_admin` 调用同步接口返回 403 |

---

## 10. 待确认事项

| 序号 | 问题 | 状态 / 影响 |
|------|------|-------------|
| 1 | cxList 列 0 是否等于旧 `/getpatient` 返回的 `eltm_id`？ | **未确认**。本地库仅 mock 数据，重合 0；需在 staging 用真实数据验证 |
| 2 | 列 22/23/26/28/36/40/44/48 的业务含义 | **部分确认**：22/23 为少量数字 id，26/28/36/40/44/48 为数量类字段；具体含义待对方确认 |
| 3 | `sign_status` 完整字典 | **实测取值 0/1/3/4**（0:98、1:320、3:3112、4:96），各值含义待确认 |
| 4 | 各 JSON 明细内部键 | **已确认**，见附录 C；无需再问 |
| 5 | `gettpbyid` 列 2/8/9/12/13/15、`getzqbyid` 列 7/8/10、PDF 列 19-24 的含义/列序 | **部分确认**：8/9 疑似医生 id，10 疑似 sign_doctor_id；PDF 列 21/23 与文档推断不一致，需对方给文字版 SQL |
| 6 | 5354 名患者是否全部属于 ELTM（disClass=10000007） | 待确认；是否存在其他病种混入 |
| 7 | 外部患者被删除后期望如何处理 | 待确认；当前设计为标记 `missing`，不删除 |
| 8 | 接口是否有频率限制、是否会改为 HTTPS/加鉴权 | 待确认；影响定时任务和 getzqbyid 批量补拉 |
| 9 | 是否允许把外部图片/PDF 转存到我方 OSS | 待确认；影响离线可用性 |
| 10 | ELTM Excel 数据是否仍需导入、`patient_id` 与新主键的对应关系 | 待确认；是否纳入本期 |
| 11 | `form_json.original_data` 的题目键如何映射为可读字段 | 待确认；确认前 `eltm_form_answer` 只做尽力拆分 |
| 12 | `physical_json` 每次测量缺失属性如何处理 | 待确认；当前按 EAV 存储，允许缺项 |
| 13 | 首次全量是否立即对 3242 名患者调用 `getzqbyid` 补拉签名/PDF | 待确认；涉及对方服务器压力，建议限速或改为按需补拉 |

---

## 11. 安全与合规

1. 外部接口为明文 HTTP、无鉴权，返回身份证号、手机号等敏感信息。上线前确认该地址仅限内网/VPN 可达，或要求对方增加鉴权。
2. 同步日志禁止打印完整姓名、身份证号、手机号、签名图片内容；只允许打印 `external_id` 与字段名。
3. 签名图片属于敏感生物特征数据，转存后需限制访问（沿用现有文件访问权限）。
4. 知情同意书 HTML 正文渲染前必须做 XSS 过滤。
5. 同步接口仅开放给超级管理员。

---

## 附录 A：cxList 66 列逐列映射

| 列 | 字段 | 目标 | 处理 |
|----|------|------|------|
| 0 | id | patient.external_id | 唯一业务键 |
| 1 | name | patient.name | trim，空不覆盖 |
| 2 | phone | patient.self_tel / contacts_num | 规范化 |
| 3 | id_card | patient.idcard / card | <19 才写 |
| 4 | gender_code | patient.sex | 1/2 |
| 5 | gender | sex 兜底 | 男/女 |
| 6 | birthday | patient.birth_time | 日期 |
| 7 | age | 不落库 | 按生日计算 |
| 8 | register_source | profile.register_source | |
| 9 | id_type | profile.id_type | |
| 10 | created_at | patient.c_time / profile.external_created_at | |
| 11 | doctor_name | patient.doctor_name | |
| 12 | doctor_id | profile.external_doctor_id | |
| 13 | hospital_name | patient.hospital_name | |
| 14 | hospital_id | profile.external_hospital_id | |
| 15 | first_doctor_names | profile.first_doctor_names | |
| 16 | first_doctor_ids | profile.first_doctor_ids | |
| 17 | first_hospital_names | profile.first_hospital_names | |
| 18 | first_hospital_ids | profile.first_hospital_ids | |
| 19 | assign_type | profile.assign_type | |
| 20 | project_count | profile.project_count | |
| 21 | project_name | profile.project_name | |
| 22 | 待确认 | raw_json | |
| 23 | 待确认 | raw_json | |
| 24 | patient_type | profile.patient_type | |
| 25 | data_scope | profile.data_scope | |
| 26 | 待确认 | raw_json | |
| 27 | form_count | profile.form_count | |
| 28 | 待确认 | raw_json | |
| 29 | latest_form_time | profile.latest_form_time | |
| 30 | latest_form_update | profile.latest_form_update | |
| 31 | latest_form_name | profile.latest_form_name | |
| 32 | pic_count | profile.pic_count | |
| 33 | latest_pic_time | profile.latest_pic_time | |
| 34 | latest_pic_update | profile.latest_pic_update | |
| 35 | physical_sign_count | profile.physical_sign_count | |
| 36 | 待确认 | raw_json | |
| 37 | latest_sign_time | profile.latest_sign_time | |
| 38 | latest_sign_update | profile.latest_sign_update | |
| 39 | report_count | profile.report_count | |
| 40 | 待确认 | raw_json | |
| 41 | latest_report_time | profile.latest_report_time | |
| 42 | latest_report_update | profile.latest_report_update | |
| 43 | exercise_flag | profile.exercise_flag | |
| 44 | 待确认 | raw_json | |
| 45 | latest_exercise_time | profile.latest_exercise_time | |
| 46 | latest_exercise_update | profile.latest_exercise_update | |
| 47 | sleep_flag | profile.sleep_flag | |
| 48 | 待确认 | raw_json | |
| 49 | latest_sleep_time | profile.latest_sleep_time | |
| 50 | latest_sleep_update | profile.latest_sleep_update | |
| 51 | consent_count | profile.consent_count | |
| 52 | latest_consent_time | profile.latest_consent_time | |
| 53 | latest_consent_update | profile.latest_consent_update | |
| 54 | extend_data_json | raw + 兜底解析 | |
| 55 | hospital_bind_json | eltm_hospital_bind | |
| 56 | project_json | eltm_project | |
| 57 | form_json | eltm_form_submission / eltm_form_answer | |
| 58 | pic_json | eltm_patient_pic | |
| 59 | physical_json | eltm_physical_record + eltm_physical_item | |
| 60 | report_json | eltm_assessment_report | |
| 61 | exercise_json | eltm_exercise_record | |
| 62 | exercise_summary_json | eltm_exercise_summary | |
| 63 | sleep_json | eltm_sleep_record | |
| 64 | sleep_summary_json | eltm_sleep_summary | |
| 65 | consent_json | informed_consent | |

---

## 附录 B：ELTM Excel 数据导入映射（独立于三个接口）

来源：文档第六节内嵌 Excel，3329 行 × 33 列，工作表 `datamain_szfyeltm`。

| Excel 字段 | 目标 | 说明 |
|------------|------|------|
| patient_id | 通过 `external_id` 关联本地患者 | 导入前必须先完成患者主体同步 |
| fam_his | `raw_data`（暂无独立列） | 建议后续新增 `fam_his` 列 |
| mot_dev_back / lan_dev_back / int_dev_back | `datamain_szfyeltm.mot_dev_back` 等 | 1=无 / 2=有 |
| abn_hear | `datamain_szfyeltm.abn_hear` | |
| med_his | `datamain_szfyeltm.has_history` + `raw_data` | 口径待确认 |
| phy_exa / lab_exa / electr / gon_B_ult | `raw_data` 或 `assessment_data` | 需确认是否拆列 |
| dia_trea_plan | `dia_trea_plan` / `treatment_plan` | |
| main_dia / sec_dia | `diagnosis` | 拼接 |
| follow_up | `raw_data` | |
| bio_sam_bank | `datamain_patient.biolog_bank_data` | 需确认 |
| gen_mut_name | `gene_name` | |
| mut_kind | `gene_type` | |

> Excel 导入建议做成一次性脚本（本地读取 → 校验 → 按 external_id upsert 到 `datamain_szfyeltm`），先以 `raw_data` 全量留档，再逐步拆列。

---

## 附录 C：实测验证结果（2026-10-17）

验证脚本：`eksjk_v2/scripts/datamain-verify/verify_datamain_api.py`
原始响应与报告：`tmp/datamain-verify/`（已在 `.gitignore` 中忽略）

```bash
# 全量下载并验证（默认缓存到 tmp/datamain-verify/cxlist.json）
python eksjk_v2/scripts/datamain-verify/verify_datamain_api.py --refresh --mysql

# 复用缓存，不访问外部接口
python eksjk_v2/scripts/datamain-verify/verify_datamain_api.py --skip-probe
```

### C.1 总体结构

| 项目 | 实测值 |
|------|--------|
| 响应大小 | 51,992,177 字节（约 49.6MB） |
| 下载耗时 | 约 10 秒 |
| 行数 | 5354 |
| 列数 | 全部 66 列，无短行/长行 |
| 患者 id 重复 | 0 |
| 明细列类型 | `extend_data_json` 等均为 **JSON 字符串**，需二次 `json.loads` |

### C.2 主体字段质量

| 字段 | 结果 |
|------|------|
| 性别编码 | `1`（男）2643 条，`2`（女）2357 条；与 `gender` 文本完全一致 |
| 出生日期 | 合法 4459 条，空值 895 条，非法 0 条 |
| 年龄列 | 纯数字串 3789、空值 1461、中文串 99、其他 5；**不可直接使用** |
| 待确认列 22/23 | 1422/1421 行有值，为数字 id（如 292、2005962631097225216） |
| 待确认列 26/28/36/40/44/48 | 全非空，数量类（0~15+） |

### C.3 明细数据量

| 明细 | 非空患者行 | 元素总数 | 唯一键 | 唯一键数量 |
|------|-----------|----------|--------|-----------|
| `extend_data_json` | 5062 | - | - | - |
| `hospital_bind_json` | 3730 | 4107 | - | - |
| `project_json` | 3835 | 4028 | - | - |
| `form_json` | 380 | 2561 | `form_data_id` | 2561 |
| `pic_json` | 181 | 759 | `pic_id` | 759 |
| `physical_json` | 2347 | 78,045 | `(patient_id, measure_time)` | 5295 次测量 |
| `report_json` | 37 | 69 | `report_id` | 69 |
| `exercise_json` | 1 | 1 | `record_id` | 1 |
| `exercise_summary_json` | 1 | 1 | `summary_id` | 1 |
| `sleep_json` | 1 | 1 | `record_id` | 1 |
| `sleep_summary_json` | 1 | 1 | `summary_id` | 1 |
| `consent_json` | 3242 | 3626 | `sign_id` | 3626 |

### C.4 明细内部字段（实测）

| 明细 | 字段 |
|------|------|
| `extend_data_json` | name, sex, registerSource, birthday, document_types, age, MTP, MTPTR, remarks, height, fht, mht, nation, class |
| `hospital_bind_json` | doctor_id, doctor_name, hospital_id, hospital_name, province, city, district, **assignment_source** |
| `project_json` | project_id, project_name, **relation_sources**, first_relation_time |
| `form_json` | form_data_id, form_key, form_name, form_type, project_id, project_name, source_type, serial_number, to_patient_id, patient_plan_id, third_patient_id, submitted_at, complete_time, complete_person_type, score, evaluation_result, result_description, latitude_compute_score, original_data |
| `pic_json` | pic_id, image_url, image_type, diagnosis_date, medical_record_name, remark, pic_source_type, type_content, create_time, update_time |
| `physical_json` | archive_id, form_key, attribute_id, attribute_code, attribute_value, measure_time, data_source, two_level, three_level, archives_type, create_time, update_time |
| `report_json` | report_id, model_id, model_name, form_key, form_name, project_id, project_name, user_form_data_id, score, total_score, evaluation_result, result_description, report_remark_value, report_suggest_value, base_check_date_value, base_next_check_date_value, create_time, update_time |
| `exercise_json` | record_id, exercise_item_id, exercise_name, category_name, subcategory_name, duration_seconds, calories_burned, met_value, weight_kg, weight_source, intensity_level, age_group, baby_age_days, record_date, create_time, update_time |
| `exercise_summary_json` | summary_id, summary_date, total_count, total_duration_seconds, total_calories, mvpa_minutes, exercise_types, is_target_reached, baby_age_days, create_time, update_time |
| `sleep_json` | record_id, record_date, sleep_start_time, sleep_end_time, sleep_duration_hours, sleep_duration_minutes, awake_times, sleep_way, sleep_habits, sleep_posture, sleep_regularly, sleep_env_is_quiet, age_group, baby_age_days, create_time, update_time |
| `sleep_summary_json` | summary_id, summary_date, total_duration_hours, is_target_reached, baby_age_days, create_time, update_time |
| `consent_json` | sign_id, project_id, group_id, informed_consent_id, doctor_id, sign_status, doctor_sign_time, patient_sign_time, guardian_sign_time, create_time, update_time |

### C.5 枚举分布

| 字段 | 分布 |
|------|------|
| `sign_status` | 3:3112，1:320，4:96，0:98 |
| `image_type`（pic_json） | 0:456，1:108，3:93，2:65，4:21，5:16 |
| `pic_source_type` | 0:749，1:6，3:4 |
| `pdf_status` | `consent_json` 中不存在，只能从 `getzqbyid` 获取（抽样为 0 待生成） |

### C.6 单患者接口抽样

| 接口 | 结果 |
|------|------|
| `gettpbyid` | 列数 16 校验通过；抽中患者 1 条记录，`image_type=3`、`source=follow`、`pic_source_type=0` |
| `getzqbyid` | 列数 25 校验通过；抽中患者 1 条记录，含 HTML 正文（约 2.8KB）、患者签名 URL、监护人/医生签名 base64（约 100KB/张）、PDF 路径/文件名/状态 |

### C.7 与本地库对比

本地 `eksjk_db` 当前只有 10 条患者、8 个 `eltm_id`，且均为 mock/样例数据：外部 5354 个 id 与本地 `eltm_id`、身份证、手机号的重合数均为 0。**该结果不能说明真实环境，需在 staging 上再跑一次对比。**
