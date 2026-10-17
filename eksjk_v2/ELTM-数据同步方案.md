# ELTM（E路童萌）外部数据同步方案

## 1. 现状分析

### 1.1 现有同步机制

系统已有一个基础同步实现 `EltmSyncServiceImpl`，核心流程如下：

```
外部API (HTTP GET) → 解析响应 → 按eltm_id分组字段 → upsertPatient() → Patient + EltmCase 入库
```

**现有代码位置：**
- 接口：`eksjk-service/.../EltmSyncService.java`
- 实现：`eksjk-service/.../impl/EltmSyncServiceImpl.java`
- 控制器：`eksjk-web/.../controller/EltmController.java`
- 前端触发：`eksjk-frontend/src/api/eltm.js` → 病例列表页「同步数据」按钮

**配置项（application-dev.yml / application-cloud.yml）：**
```yaml
eksjk:
  eltm:
    sync-url: http://36.26.56.94:8026/datamain/getpatient
```

### 1.2 外部 API 响应格式（基于 V1 代码分析）

调用 `GET /datamain/getpatient`，返回格式：

```json
{
  "code": 200,
  "data": {
    "data": [
      [rowId, "eltm_id_xxx", 3059, "name", "张三", null, "import_user", "2024-01-15 10:30:00"],
      [rowId, "eltm_id_xxx", 3059, "age", "8", null, "import_user", "2024-01-15 10:30:00"],
      [rowId, "eltm_id_xxx", 3059, "sex", "1", null, "import_user", "2024-01-15 10:30:00"],
      ...
    ]
  }
}
```

**字段含义（行式数据，索引从 0 开始）：**

| 索引 | 含义 | 示例值 |
|------|------|--------|
| 0 | 行ID / 自增序号 | `12345` |
| 1 | ELTM患者唯一标识 | `eltm_xxxxx` |
| 2 | 计划ID（3059=ELTM计划） | `3059` |
| 3 | 字段名 | `name`, `age`, `sex`, `birthday`, `idCard`, `phone` |
| 4 | 字段值 | `张三` |
| 5 | 预留（通常null） | `null` |
| 6 | 导入人员 | `user_xxx` |
| 7 | 创建时间 | `2024-01-15 10:30:00` |

> ⚠️ **关键特征：这是一种「宽表转行」的数据格式。每个患者的多条记录（数据行）共享同一个 eltmId（索引1），通过索引3的字段名区分不同属性。一个 N 字段的患者对应 N 行数据。**

### 1.3 现有字段映射（V2 当前实现）

| 外部字段 (info[3]) | 目标表 | 目标字段 |
|---|---|---|
| `name` | `datamain_patient` | `name` |
| `age` | `datamain_patient` | `age` |
| `sex` | `datamain_patient` | `sex` |
| `phone` | `datamain_patient` | `self_tel`, `contacts_num` |
| `idCard` | `datamain_patient` | `card`, `idcard`（仅当长度<19） |
| `birthday` | `datamain_patient` | `birth_time` |
| `__imp_per` | `datamain_patient` | `imp_per`, `modify_per` |
| `__c_time` | `datamain_patient` | `c_time` |
| (全部原始字段) | `datamain_szfyeltm` | `raw_data`（JSON） |

### 1.4 现有实现的局限性

| 问题 | 影响 |
|------|------|
| 仅映射 6 个基本字段 | 外部库中如就诊信息、体格检查、化验结果等无法同步 |
| 硬编码字段映射 | 外部字段变更需要修改 Java 代码 |
| 一次性全量同步 | 无法增量同步，大量数据时耗时长 |
| 无变更追踪 | 已同步过的病例再次同步不会检测字段是否有变化 |
| 单线程逐条处理 | 大量数据同步慢 |
| 无重试/断点续传 | 中途失败需从头开始 |
| 外部 API 格式假设固定 | 如果公众号提供不同格式的接口，需要大量改动 |

---

## 2. 外部数据源分析

### 2.1 E路童萌公众号数据模型（基于 V1 SzfyEltm 模型推断）

V1 中 ELTM 公众号同步的数据除了 Patient 基本字段外，还包含 ELTM 特有的评估数据：

| 分类 | 字段 | 说明 |
|------|------|------|
| **基本信息** | name, sex, birthday, idCard, phone | 已映射 |
| **出生信息** | 出生身长、出生体重、胎龄、胎次、产次 | 部分映射（`BL`, `BWt`, `ges_week`, `parity`, `pronum`） |
| **家庭信息** | 父亲身高、母亲身高、家族史 | 部分映射（`FHt`, `MHt`, `family_his`） |
| **地址信息** | 籍贯、家庭住址 | 可映射（`nat_pla`, `fam_adr`） |
| **就诊信息** | 主诉/现病史、民族 | 可映射（`category_describe`, `ethnic`） |
| **评估症状** | 一般症状、代谢症状、骨骼肌肉症状、内分泌症状 | 需映射到 EltmCase 或 Patient |
| **体格检查** | 身高、体重、BMI、 Tanner分期 | 可映射到 Patient |
| **小程序关联** | 微信小程序身份标识 | 可映射（`xcx_card`） |

### 2.2 与外部接口对接的关键问题

1. **字段名不一致**：外部库的字段名（如 `input1757343120305` 这种模板ID标识）与 V2 系统完全不同
2. **编码体系不同**：性别可能是 `男/女`、`1/2`、`M/F` 等不同编码
3. **日期格式不同**：可能是 `yyyy-MM-dd`、`yyyy/MM/dd`、时间戳等
4. **数据完整度不同**：外部库字段可能为空、部分填写或格式不规范

---

## 3. 同步方案设计

### 3.1 总体架构

```
┌─────────────────────────────────────────────────────┐
│                    E路童萌公众号数据库                    │
│                      (外部系统)                         │
└─────────────────────┬───────────────────────────────┘
                      │ 开放 HTTP API
                      ▼
┌─────────────────────────────────────────────────────┐
│              EKSJK V2 同步模块（升级后）                 │
│                                                       │
│  ┌─────────────┐   ┌──────────────┐   ┌───────────┐ │
│  │ API 适配层   │ → │ 字段映射引擎  │ → │ 数据写入层 │ │
│  │ (多格式支持) │   │ (可配置映射)  │   │ (批量+事务)│ │
│  └─────────────┘   └──────────────┘   └───────────┘ │
│        │                                       │       │
│        ▼                                       ▼       │
│  ┌──────────┐                          ┌──────────┐  │
│  │ 同步日志  │                          │ 患者+病例 │  │
│  │ (审计追踪)│                          │ (MySQL)   │  │
│  └──────────┘                          └──────────┘  │
└─────────────────────────────────────────────────────┘
```

### 3.2 同步策略

根据公众号接口开放情况，推荐三种模式组合使用：

#### 模式一：全量同步（已有，需优化）

- **适用场景**：初次同步、数据修复
- **触发方式**：手动触发（前端按钮）或定时任务
- **逻辑**：拉取全部数据 → 遍历 upsert → 记录结果

#### 模式二：增量同步（推荐新增）

- **适用场景**：日常同步，公众号有新增/更新数据时
- **触发方式**：定时任务（如每天凌晨 2:00）+ 手动触发
- **逻辑**：
  ```
  请求参数带 lastSyncTime → 外部接口返回该时间后有变更的数据
  → 逐一处理 → 更新 lastSyncTime
  ```
- **对外部接口的要求**：
  - 支持 `?since=2024-01-15T10:00:00` 参数
  - 或提供独立的 `/getpatient-updated` 端点
  - 返回数据中包含 `updated_at` 字段

#### 模式三：单条实时同步（可选）

- **适用场景**：公众号端用户填写资料后即时推送
- **触发方式**：外部系统主动回调（Webhook）
- **逻辑**：接收回调 → 按 eltmId 更新单条 → 返回确认
- **需要新增**：一个无需登录的回调接收端点

### 3.3 字段映射方案

#### 3.3.1 映射配置化

将字段映射从硬编码改为配置文件驱动，建议使用 YAML 配置：

```yaml
eksjk:
  eltm:
    sync:
      # 外部接口地址
      url: http://36.26.56.94:8026/datamain/getpatient
      # 数据格式：row-based（行式）或 record-based（记录式）
      data-format: row-based
      
      # 字段映射规则
      field-mapping:
        # 目标表.datamain_patient 的映射
        datamain_patient:
          # 直接映射：外部字段 → 目标字段
          direct:
            name: name
            age: age
            sex: sex
            ethnic: ethnic
            category_describe: category_describe
            nat_pla: nat_pla
            fam_adr: fam_adr
            family_his: family_his
            xcx_card: xcx_card
            BL: bl
            BWt: bwt
            ges_week: ges_week
            parity: parity
            pronum: pronum
            height: height
            weight: weight
            bmi: bmi
            FHt: fht
            MHt: mht
            medrec_num: medrec_num
            self_tel: self_tel
            card: card
            idcard: idcard
            birth_time: birth_time
          
          # 一对多映射：一个外部字段映射到多个目标字段
          one-to-many:
            phone: [self_tel, contacts_num]
          
          # 转换映射：外部字段经过转换函数后赋值
          transform:
            sex:
              source: sex
              transformer: sexNormalizer        # "男"→"1", "女"→"2", "M"→"1", "F"→"2"
            birthday:
              source: birthday
              transformer: dateParser            # 多种日期格式统一解析
            idCard:
              source: idCard
              transformer: idCardValidator       # 校验+仅当合法时写入
        
        # 目标表.datamain_szfyeltm 的映射
        datamain_szfyeltm:
          direct:
            has_history: has_history
            gene_method: gene_method
            gene_res: gene_res
            gene_name: gene_name
            chrom: chrom
            gen_sym: gen_sym
            met_sym: met_sym
            bone_sym: bone_sym
            endo_sym: endo_sym
            other_sym: other_sym
          
          # JSON聚合映射：多个外部字段聚合到一个JSON字段
          json-aggregate:
            raw_data:                   # EltmCase.rawData
              strategy: all             # 存储全部外部原始字段
            assessment_data:            # EltmCase.assessmentData
              strategy: include
              fields: [score, level, evaluation]
```

#### 3.3.2 内置转换器

| 转换器名称 | 用途 | 示例 |
|-----------|------|------|
| `sexNormalizer` | 性别标准化 | `男`→`1`, `女`→`2`, `M`→`1`, `F`→`2` |
| `dateParser` | 日期解析 | `2024-01-15`、`2024/01/15`、`20240115` → `LocalDateTime` |
| `idCardValidator` | 身份证校验 | 校验长度和格式，非法返回 null |
| `phoneNormalizer` | 手机号清洗 | 去除空格、横线，校验 11 位 |
| `numberExtractor` | 数字提取 | `身高 120.5cm` → `120.5` |
| `booleanNormalizer` | 布尔值标准化 | `是/否`→`1/0`, `true/false`→`1/0` |
| `codeMapper` | 编码映射 | 自定义 lookup table（如民族编码映射） |

### 3.4 数据库设计增强

#### 3.4.1 同步日志表（新增）

```sql
CREATE TABLE IF NOT EXISTS `eltm_sync_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sync_type` VARCHAR(20) NOT NULL COMMENT '同步类型: FULL/INCREMENTAL',
    `start_time` DATETIME NOT NULL COMMENT '同步开始时间',
    `end_time` DATETIME COMMENT '同步结束时间',
    `total_count` INT DEFAULT 0 COMMENT '接口返回总记录数',
    `new_count` INT DEFAULT 0 COMMENT '新增数量',
    `updated_count` INT DEFAULT 0 COMMENT '更新数量',
    `skipped_count` INT DEFAULT 0 COMMENT '跳过数量（无变更）',
    `failed_count` INT DEFAULT 0 COMMENT '失败数量',
    `status` VARCHAR(20) NOT NULL DEFAULT 'RUNNING' COMMENT '状态: RUNNING/SUCCESS/FAILED/PARTIAL',
    `error_message` TEXT COMMENT '错误信息',
    `external_last_id` VARCHAR(64) COMMENT '外部数据的最后一条ID（用于增量）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_sync_time` (`start_time`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ELTM数据同步日志';
```

#### 3.4.2 Patient 表同步追踪字段增强（部分已有）

```sql
-- 已有字段：sync_status, sync_time, eltm_id
-- 建议新增：
ALTER TABLE datamain_patient 
    ADD COLUMN IF NOT EXISTS `external_updated_at` DATETIME COMMENT '外部系统最后更新时间',
    ADD COLUMN IF NOT EXISTS `sync_version` INT DEFAULT 0 COMMENT '同步版本号';
```

### 3.5 错误处理与容错

```
同步失败的三种级别处理：

Level 1 - 单条失败（继续处理下一条）
  └→ 记录到 sync_log detail 表，最后统一报告

Level 2 - 外部接口异常（重试3次，指数退避）
  ├→ 第1次失败：等待 5s
  ├→ 第2次失败：等待 30s  
  ├→ 第3次失败：等待 2min
  └→ 仍失败：标记 sync_log FAILED，通知管理员

Level 3 - 数据库异常（事务回滚，立即中止）
  └→ 标记 FAILED，保留已完成的部分进度
```

---

## 4. 实施步骤

### 第一阶段：接口调研与验证（1天）

| 步骤 | 内容 | 产出 |
|------|------|------|
| 1.1 | 与公众号技术团队沟通，确认以下信息：<br>- API 完整 URL<br>- 认证方式（Token? IP白名单?）<br>- 完整字段列表及含义<br>- 是否支持增量查询（`?since=`参数）<br>- 数据格式（当前为行式，是否有记录式）<br>- 调用频率限制 | 接口文档 |
| 1.2 | 用 Postman/curl 实际调用接口，验证返回数据结构 | 真实 response 示例 |
| 1.3 | 对比外部字段与本系统字段，整理差异表 | 字段差异矩阵 |

### 第二阶段：同步模块改造（2-3天）

| 步骤 | 内容 | 涉及文件 |
|------|------|---------|
| 2.1 | 创建 `SyncFieldMapping` 配置类，支持 YAML 映射配置 | `eksjk-common` 新增 |
| 2.2 | 创建 `FieldTransformer` 转换器接口及内置实现 | `eksjk-service` 新增 |
| 2.3 | 改造 `EltmSyncServiceImpl.upsertPatient()`，支持配置化映射 + 变更检测 | `EltmSyncServiceImpl.java` |
| 2.4 | 新增增量同步逻辑 `syncIncremental()` | `EltmSyncServiceImpl.java` |
| 2.5 | 新增同步日志表 `eltm_sync_log` + Entity + Mapper | 新建 |
| 2.6 | 新增 `EltmSyncLogService` 记录每次同步详情 | `eksjk-service` 新增 |

### 第三阶段：接口对接与联调（1-2天）

| 步骤 | 内容 |
|------|------|
| 3.1 | 配置 staging 环境的外部接口地址（可先用 V1 接口验证） |
| 3.2 | 执行全量同步，验证新字段映射正确 |
| 3.3 | 模拟增量同步，验证去重 + 变更检测 |
| 3.4 | 验证错误场景：网络超时、接口返回异常、单条数据异常 |

### 第四阶段：控制器与前端增强（1天）

| 步骤 | 内容 |
|------|------|
| 4.1 | `EltmController` 新增增量同步 + 同步日志查询接口 |
| 4.2 | 前端 ELTM 列表页增加同步状态展示 + 同步日志查看 |
| 4.3 | 可选：增加定时任务配置（Spring Scheduler） |

---

## 5. 调用方式

### 5.1 手动同步（前端页面）

```
POST /api/eltm/sync                    # 全量同步
POST /api/eltm/sync?mode=incremental   # 增量同步
GET  /api/eltm/sync-status             # 同步统计
GET  /api/eltm/sync-logs?page=1&size=20 # 同步日志列表
```

### 5.2 定时自动同步

```java
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2:00
public void scheduledIncrementalSync() {
    eltmSyncService.syncIncremental();
}
```

### 5.3 外部回调（可选，需公众号支持）

```
POST /api/eltm/callback               # 无需登录（IP 白名单校验）
Body: { "eltmId": "xxx", "action": "update" }
```

---

## 6. 待确认事项清单

与公众号技术团队对接时需明确：

| 序号 | 问题 | 重要性 |
|------|------|--------|
| 1 | 接口完整 URL 是什么？ | 必须 |
| 2 | 接口是否需要认证？（Token / IP白名单 / 签名？） | 必须 |
| 3 | 接口返回的完整字段有哪些？字段含义是什么？ | 必须 |
| 4 | 接口是否支持增量查询？（按时间范围/按ID范围） | 高 |
| 5 | 数据更新时，接口返回的字段是全量还是仅变更字段？ | 高 |
| 6 | 调用频率有什么限制？（QPS / 每日上限） | 高 |
| 7 | 是否有数据变更回调/Webhook？ | 中 |
| 8 | 接口返回的数据格式是否可以提供「记录式」（一个患者一条完整记录）？ | 中 |
| 9 | 数据总量大约多少条？每日增量大约多少？ | 中 |
| 10 | 字段值是否存在需要清洗的情况？（如空值、异常值） | 低 |

---

## 7. 风险与注意事项

1. **ID 映射一致性**：外部 `eltm_id` 是本系统与公众号数据库关联的唯一桥梁，务必保证不丢失、不重复
2. **数据覆盖策略**：同步更新时，建议只覆盖「外部有值」的字段，不覆盖本系统已有且外部为空的字段（防止本地录入数据被清空）
3. **审核状态保护**：已通过审核（`audit_status = released`）的病例，同步更新时不应回退审核状态
4. **身份证号处理**：外部传入的身份证如长度 >= 19（可能为加密或脱敏），不应写入
5. **性能考虑**：如数据量 > 1万条，应分批处理（每批 500-1000 条），避免长时间占用数据库连接
6. **网络隔离**：确保 K8s 集群到外部接口 IP 的网络连通（阿里云 ACK → 36.26.56.94 是否可达）
