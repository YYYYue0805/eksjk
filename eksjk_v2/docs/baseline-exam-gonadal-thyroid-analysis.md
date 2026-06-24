# 基线辅助检查-性腺B超-甲状腺功能 分析报告

> 日期：2026-06-02
> PPTX 已找到并分析，以下为完整实施方案和完成状态。

---

## 一、V1 系统分析

### 1.1 数据模型

V1 使用 Django ORM，每种疾病独立建表。性腺B超 (`gon_B_ult`) 分散在 5 个疾病子表中，均为自由文本字段：

| 模型 | 相关字段 | 类型 |
|------|---------|------|
| `Patient` | 无性腺B超/甲状腺专用字段 | - |
| `Case` (DSD) | `gon_B_ult`, `B_ult_image`(JSON), `bscanExplain`(JSON) | varchar(1024) |
| `Short` (FSS) | `gon_B_ult` | varchar(1024) |
| `SGA` | `gon_B_ult` | varchar(1024) |
| `Sexprecocity` (CPP) | `gon_B_ult` | varchar(1500) |
| `JzxShort` (SSS) | `gon_B_ult` | varchar(1024) |
| `Mas` | `thy_ult_con`, `adr_ult_con`, `ren_ult_con`, `thy_fun_ant_exa`, `goiter` | varchar(512) |
| `PatFoll` (随访) | `gon_B_ult`, `Jiagong`(甲功) | varchar(512/128) |

### 1.2 前端：DSD B超详细结构化字段（V1 特有优势）

V1 的 DSD.vue 包含 B超的尺寸结构化数据：

```js
// 女性患者 - 子宫+卵巢 B超
子宫大小: 三维 (uterusOne × uterusTwo × uterusThr) cm
内膜厚度: intima cm
左侧卵巢大小: 三维 (ovaLeftOne × ovaLeftTwo × ovaLeftThr) cm
右侧卵巢大小: 三维 (ovaRightOne × ovaRightTwo × ovaRightThr) cm
最大滤泡直径: follDiameter cm

// 男性患者 - 睾丸 B超
右侧睾丸大小: 三维 (testisLeftOne × testisLeftTwo × testisLeftThr) cm
左侧睾丸大小: 三维 (testisRightOne × testisRightTwo × testisRightThr) cm
```

### 1.3 激素检查时间

每个激素组都有独立的采样时间字段：`LHFSHTime`, `TTime`, `E2Time`, `DHTTime`, `FTTime`, `SHBGTime`, `IGFBPTime`, `AMHTime`, `INHBTime`, `MRITime`, `ACTHTime`, `HydTime`, `OHPTime`, `DHEATime`, `ADTime`

### 1.4 V1 问题

1. 性腺B超分布在 5 个疾病子表，缺乏统一视图
2. 全自由文本，无法结构化查询和统计
3. 随访表 `Jiagong`（甲功）只有一个自由文本字段，无法区分 TSH/FT3/FT4
4. 随访表中无甲状腺结构化字段
5. MAS 表 90+ 字段，大量 varchar(512) 自由文本，冗余严重

---

## 二、V2 系统分析

### 2.1 数据模型

V2 做了重大架构改进，将甲状腺和影像字段统一到 Patient 主表：

#### Patient 主表新增字段

| 字段 | 含义 | 存储格式 |
|------|------|---------|
| `tsh` | TSH (uIU/mL) | varchar |
| `ft3` | FT3 (pg/mL) | varchar |
| `ft4` | FT4 (ng/dL) | varchar |
| `tpoab` | TPOAb (IU/mL) | varchar |
| `tgab` | TgAb (IU/mL) | varchar |
| `gonBUlt` | 性腺B超 | 编码格式 `"状态\|描述"` |
| `pituitaryMri` | 垂体MRI | 编码格式 |
| `thyroidUlt` | 甲状腺B超 | 编码格式 |
| `bonMinDen` | 骨密度 | 编码格式 |

#### PatientFollowUp 表

| 字段 | 含义 | 状态 |
|------|------|------|
| `jiagong` | 甲功 | 仍为自由文本（兼容 V1） |
| `gonBUlt` | 性腺B超 | varchar |

#### MasCase 表

| 字段 | 含义 |
|------|------|
| `thyroidAbnormality` | 甲状腺异常（自由文本） |
| `adrenalUlt` | 肾上腺B超 |
| `renalUlt` | 肾脏B超 |

### 2.2 前端：统一表单 BaselineExamForm.vue

V2 使用统一的基线辅助检查表单，按疾病类型动态显示/隐藏：

**甲状腺功能区域**（显示条件：MAS, ELTM）：

| 字段 | 名称 | 单位 |
|------|------|------|
| TSH | 促甲状腺激素 | uIU/mL |
| FT3 | 游离T3 | pg/mL |
| FT4 | 游离T4 | ng/dL |
| TPOAb | 甲状腺过氧化物酶抗体 | IU/mL |
| TgAb | 甲状腺球蛋白抗体 | IU/mL |

**影像检查区域**（编码格式：`状态|描述`，0=未查, 1=正常, 2=异常）：

| 检查项 | DSD | CPP | FSS | MAS | SGA | SSS | ELTM |
|--------|-----|-----|-----|-----|-----|-----|------|
| 性腺B超 | ✓ | ✓ | ✓ | ✗ | ✓ | ✓ | ✗ |
| 甲状腺B超 | ✗ | ✓ | ✓ | ✗ | ✓ | ✓ | ✗ |
| 甲状腺功能 | ✗ | ✗ | ✗ | ✓ | ✗ | ✗ | ✓ |
| 垂体MRI | ✓ | ✓ | ✓ | ✗ | ✓ | ✓ | ✗ |
| 骨密度 | ✗ | ✗ | ✓ | ✗ | ✓ | ✓ | ✗ |
| 卵巢囊肿 | ✗ | ✓ | ✓ | ✗ | ✓ | ✓ | ✗ |

### 2.3 V2 优势（相比 V1）

1. **甲状腺功能结构化**：5 个独立字段 vs V1 的 1 个文本字段
2. **影像状态标准化**：编码格式 "未查/正常/异常" + 描述
3. **甲状腺B超独立**：CPP/FSS/SGA/SSS 可用，V1 仅 MAS 有
4. **数据统一存储**：Patient 主表统一管理，不再分散

---

## 三、V1 vs V2 差距分析

### 3.1 已改进

| 项目 | V1 | V2 |
|------|----|----|
| 甲状腺功能 | 随访仅 `Jiagong` 一个文本 | 5 个结构化字段 |
| 影像状态 | 纯自由文本 | 编码格式 + 描述 |
| 甲状腺B超范围 | 仅 MAS | CPP/FSS/SGA/SSS 均可用 |
| 数据位置 | 7 个疾病子表 | Patient 主表统一 |

### 3.2 缺失项

| 缺失项 | 严重度 | 说明 |
|--------|--------|------|
| B超尺寸结构化数据 | **高** | V1 DSD 有子宫/卵巢/睾丸三维尺寸，V2 仅一个 textarea |
| MAS 甲状腺B超显示 | **中** | V2 中 MAS 不显示甲状腺B超（仅有甲状腺功能） |
| 激素采样时间 | 中 | V1 每个激素有独立时间字段，V2 无 |
| 随访甲状腺结构化 | 中 | 随访表仍用 `jiagong` 文本 |
| MAS 性腺B超显示 | 低 | MAS 不显示性腺B超，但临床可能需要 |
| ELTM 甲状腺B超 | 低 | ELTM 有甲状腺功能但无甲状腺B超 |

### 3.3 架构问题

1. Patient 主表和 PatientFollowUp 存在字段重叠但格式不一致
2. `"status|description"` 管道符编码在医疗描述中可能被误解析
3. 激素值全为 varchar，不利于数值统计和趋势图

---

## 四、解决方案

### 4.1 P0 - 修复 MAS 显示逻辑（立即实施）

修改 `BaselineExamForm.vue` 中的显示条件：

```js
// 1. MAS 应显示甲状腺B超
// 修改前: const showThyroidUlt = computed(() => ['cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))
// 修改后:
const showThyroidUlt = computed(() => ['cpp', 'fss', 'sga', 'sss', 'mas'].includes(props.diseaseType))

// 2. MAS 应显示性腺B超
// 修改前: const showGonBUlt = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))
// 修改后:
const showGonBUlt = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'mas'].includes(props.diseaseType))
```

### 4.2 P1 - 补充 B 超尺寸结构化数据

**数据库**：Patient 表新增 JSON 字段 `gon_b_ult_detail`

```sql
ALTER TABLE datamain_patient ADD COLUMN gon_b_ult_detail TEXT;
```

**JSON 结构**：
```json
{
  "sex": "2",
  "uterus": { "length": "", "width": "", "height": "" },
  "endometriumThickness": "",
  "leftOvary": { "length": "", "width": "", "height": "" },
  "rightOvary": { "length": "", "width": "", "height": "" },
  "maxFollicleDiameter": "",
  "leftTestis": { "length": "", "width": "", "height": "" },
  "rightTestis": { "length": "", "width": "", "height": "" },
  "tesSize": ""
}
```

**前端**：BaselineExamForm.vue 当性腺B超异常且为 DSD 时，按性别展开尺寸输入框。

### 4.3 P1 - 随访表甲状腺结构化

```sql
ALTER TABLE datamain_patfoll ADD COLUMN tsh VARCHAR(20);
ALTER TABLE datamain_patfoll ADD COLUMN ft3 VARCHAR(20);
ALTER TABLE datamain_patfoll ADD COLUMN ft4 VARCHAR(20);
ALTER TABLE datamain_patfoll ADD COLUMN tpoab VARCHAR(20);
ALTER TABLE datamain_patfoll ADD COLUMN tgab VARCHAR(20);
```

同步更新 `PatientFollowUp.java` 和随访前端表单。

### 4.4 P2 - 激素检查时间

在 Patient 实体和 BaselineExamForm 中为每组激素添加 `_checkDate` 字段：
- `hormoneCheckDate` — 性激素检查时间
- `thyroidCheckDate` — 甲状腺检查时间
- `adrenalCheckDate` — 肾上腺检查时间
- `growthFactorCheckDate` — 生长因子检查时间

### 4.5 P3 - 独立辅助检查表（长期目标）

建立 `exam_baseline` 表，将辅助检查从 Patient 主表分离，支持多次检查记录和数值类型统计。

---

## 五、实施优先级

| 优先级 | 任务 | 工作量 | 文件数 |
|--------|------|--------|--------|
| P0 | MAS 甲状腺B超/性腺B超显示修复 | 0.5h | 1 |
| P1 | B 超尺寸 JSON 字段 | 3h | 3 |
| P1 | 随访表甲状腺结构化 | 2h | 4 |
| P2 | 激素检查时间 | 1h | 2 |
| P2 | MAS 甲状腺联动 | 2h | 3 |
| P3 | 独立辅助检查表 | 8h | 10+ |

---

## 六、涉及文件

### 需修改的 V2 文件

- `eksjk_v2/eksjk-frontend/src/views/case/components/BaselineExamForm.vue`
- `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/entity/Patient.java`
- `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/entity/PatientFollowUp.java`
- `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/entity/MasCase.java`
- `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/dto/PatientDTO.java`
- `eksjk_v2/eksjk-backend/eksjk-model/src/main/java/com/eksjk/model/vo/PatientVO.java`
- `eksjk_v2/init-sql/` 增量 DDL 脚本

### V1 参考文件（不改动）

- `eksjk_v1/ek-frontend/src/components/common/DSD.vue`
- `eksjk_v1/ek-frontend/src/components/common/MAS.vue`
- `eksjk_v1/eksjk/datamain/models.py`
