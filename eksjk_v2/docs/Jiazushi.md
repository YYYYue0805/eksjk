# 家族史模块 — 谱图可视化与多子女数据关联 实现方案

> 日期：2026-06-02
> 需求：1) 谱图可视化 — 以图谱形式直观展示家族成员的健康关系；2) 多子女数据关联 — 通过病历号关联同一家庭内所有患病家属（孩子）的病历

---

## 一、现状分析

### 1.1 V2 家族史数据模型

**Patient 主表**（`datamain_patient`）家族相关字段：

| 字段 | DB列 | 类型 | 说明 |
|------|------|------|------|
| `fht` | FHt | varchar | 父亲身高 |
| `mht` | MHt | varchar | 母亲身高 |
| `fhw` | FHw | varchar | 父亲体重 |
| `mhw` | MHw | varchar | 母亲体重 |
| `menAge` | menAge | varchar | 母亲初潮年龄 |
| `isBot` | isBot | varchar | 有无兄弟姐妹 |
| `familyHis` | familyHis | text | 家族史文本（V1遗留） |
| `familyMembers` | family_members | text | 家族成员 JSON 数组 |

**`familyMembers` JSON 结构**（当前）：
```json
[
  {
    "relationship": "父亲",
    "height": "175",
    "weight": "70",
    "hasSimilarDisease": "否",
    "notes": ""
  }
]
```
- 标准键：`relationship`、`height`、`weight`、`hasSimilarDisease`、`notes`
- 支持用户动态添加自定义列
- 10 种预设关系：父亲、母亲、兄、弟、姐、妹、祖父、祖母、外祖父、外祖母、其他

### 1.2 V2 前端家族史界面

位于 `CaseDetail.vue` 第 370-432 行，使用 `el-table` 表格展示：

| 列 | 控件 | 宽度 |
|----|------|------|
| 与患者关系 | el-select（10种关系） | 120px |
| 身高(cm) | el-input | 100px |
| 体重(kg) | el-input | 100px |
| 类似疾病 | el-select（是/否/不详） | 100px |
| 备注 | el-input | auto |
| 自定义列 | 用户动态添加 | 120px |

**核心问题**：
1. **缺少性别字段** — 谱图需要区分男女（方框/圆圈）
2. **缺少年龄/出生年份** — 谱图需要按代排列
3. **缺少代际标识** — 无法确定成员属于哪一代
4. **缺少患者关联** — 无法将家族成员关联到系统中的另一个患者病历
5. **无可视化图谱** — 纯表格展示，关系不直观
6. **无家庭分组** — 同一家庭的多名患者之间没有系统级关联

### 1.3 V1 对比

V1 同样没有谱图可视化和多子女关联。V1 的家族成员数据分散在各疾病子表的 `fam_his` 字段中（JSON 格式，含 `userNum` 字段但未做系统级关联）。

---

## 二、功能一：谱图可视化

### 2.1 医学谱图绘制标准

医学生谱图（Pedigree Chart）遵循国际标准符号：

| 符号 | 含义 |
|------|------|
| □ 方框 | 男性 |
| ○ 圆圈 | 女性 |
| ◇ 菱形 | 性别未知 |
| ■/● 实心填充 | 患病个体 |
| ◐ 半填充 | 携带者 |
| ↗ 箭头 | 先证者（索引患者） |
| / 斜线 | 已故 |
| — 水平线 | 配偶关系 |
| ┬ 水平+垂直线 | 子女连接 |
| ≡ 双线 | 近亲婚配 |

**代际布局**：
- 第一代（外/祖父母）在最上方
- 先证者所在代为中间
- 子女在下方
- 同代兄弟姐妹用水平横线连接

### 2.2 数据模型补充

#### A. 扩展 `familyMembers` JSON 字段

新增必选字段：

```json
{
  "relationship": "父亲",
  "sex": "1",
  "birthYear": "1975",
  "isAffected": "0",
  "isDeceased": "0",
  "generation": 1,
  "height": "175",
  "weight": "70",
  "hasSimilarDisease": "否",
  "notes": "",
  "linkedPatientId": null,
  "linkedMedrecNum": ""
}
```

| 新字段 | 类型 | 说明 |
|--------|------|------|
| `sex` | "1"/"2"/"0" | 性别（1=男, 2=女, 0=未知） |
| `birthYear` | string | 出生年份，用于计算年龄和代际 |
| `isAffected` | "0"/"1" | 是否患病（用于谱图填充） |
| `isDeceased` | "0"/"1" | 是否已故（用于谱图斜线） |
| `generation` | number | 代际编号（0=先证者同代, -1=上一代, 1=下一代） |
| `linkedPatientId` | string/null | 关联的患者 Hashids ID |
| `linkedMedrecNum` | string | 关联的病历号（用户输入） |

#### B. Patient 表新增 `family_id` 字段

```sql
ALTER TABLE datamain_patient ADD COLUMN family_id VARCHAR(36) COMMENT '家庭分组ID（UUID），同一家庭的患者共享';
CREATE INDEX idx_family_id ON datamain_patient(family_id);
```

- 新建患者时自动生成 UUID
- 关联已有患者时使用对方的 `family_id`
- 通过 `family_id` 可查询同一家庭的所有患者

### 2.3 技术选型

**方案对比**：

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **自定义 SVG 组件** | 完全控制医学符号；与 Vue 集成好；可打印；无额外依赖 | 开发量大 | ★★★★★ |
| ECharts Graph | 已有依赖；交互丰富 | 医学符号定制困难；布局不受控 | ★★★ |
| 第三方谱图库 (pedigreejs) | 开箱即用 | 库老旧、文档少；可能不兼容 Vue 3 | ★★ |

**推荐：自定义 SVG 组件**

理由：
- 医学谱图符号集固定且有限，SVG 绘制成本可控
- 项目无额外依赖，维护简单
- 支持导出/打印（医学场景常见需求）
- 与 Element Plus 风格统一

### 2.4 SVG 谱图组件设计

#### 组件结构

```
PedigreeChart.vue
├── 数据预处理层：familyMembers → 代际分组图结构
├── 布局计算层：确定每个节点的 (x, y) 坐标
├── SVG 渲染层：
│   ├── <line> — 代际连接线、配偶线、子女连接线
│   ├── <rect> — 男性节点（方框 44×36）
│   ├── <circle> — 女性节点（半径 18）
│   ├── <text> — 标签（关系、年龄、身高）
│   └── <path> — 先证者箭头
└── 交互层：hover 详情、click 跳转关联病历
```

#### Props 定义

```js
{
  familyMembers: Array,     // 家族成员列表（含扩展字段）
  patientSex: String,       // 当前患者性别（'1'男/'2'女）
  patientId: String,        // 当前患者 Hashids ID
  patientName: String,      // 当前患者姓名
  patientBirthYear: String, // 当前患者出生年份
  patientDisease: String,   // 当前患者疾病
  disabled: Boolean,        // 是否只读模式
}
```

#### 布局算法

```
输入：familyMembers[] + 患者自身信息
步骤：
  1. 将患者作为先证者（gen=0, pos=center）
  2. 遍历 familyMembers，按 relationship 推断代际：
     - 祖父/祖母/外祖父/外祖母 → gen=-2
     - 父亲/母亲 → gen=-1
     - 兄/弟/姐/妹 → gen=0（同代）
     - 子女 → gen=+1
     - 其他 → 手动指定或默认 gen=0
  3. 配偶跟随对应成员（父亲↔母亲，患者↔配偶）
  4. 每代内部按出生年份从左到右排列
  5. 计算坐标：
     - Y: gen * rowHeight（每代间距 120px）
     - X: 居中排列，nodes间距 80px
  6. 绘制连接线：
     - 配偶之间：水平线
     - 父母到子女：垂直线 + 水平兄弟姐妹横线 + 各子女垂线
  7. 绘制节点符号 + 标签
```

### 2.5 前端 UI 集成

在 `CaseDetail.vue` 家族史区域增加两个视图切换：

```
┌─────────────────────────────────────────┐
│ 家族史    [表格视图] [谱图视图]          │
├─────────────────────────────────────────┤
│                                         │
│   (默认显示 el-table 表格)               │
│   或                                     │
│   (PedigreeChart SVG 谱图)              │
│                                         │
└─────────────────────────────────────────┘
```

- **表格视图**：保留现有编辑功能，新增性别、出生年份、患病状态列
- **谱图视图**：只读展示 + hover 查看详情 + 点击关联病历跳转
- 编辑模式强制切回表格视图

---

## 三、功能二：多子女数据关联

### 3.1 数据模型

#### A. 家庭分组机制

```
Patient 表
├── family_id (UUID)        ← 家庭分组标识
├── familyMembers (JSON)    ← 含 linkedPatientId / linkedMedrecNum
└── medrecNum               ← 病历号（用于搜索关联）
```

**关联逻辑**：
1. 新建患者 A → 自动生成 `family_id = UUID-A`
2. 新建患者 B → 自动生成 `family_id = UUID-B`
3. 在患者 A 的家族史中，用户填写"弟弟"并输入弟弟的病历号 → 系统搜索病历号，找到患者 B
4. 用户确认关联 → 系统将患者 B 的 `family_id` 更新为 `UUID-A`，同时患者 B 的家族史自动补充患者 A 的信息

#### B. 关联状态机

```
[新建患者] → family_id = 自动UUID（独立家庭）
[关联操作] → 查询目标病历号 → 确认 → 合并 family_id
[解除关联] → 恢复独立 family_id + 清理交叉引用
```

### 3.2 API 设计

#### 新增接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/patients/search-by-medrec?medrecNum=xxx` | 按病历号精确搜索患者（返回基本信息） |
| GET | `/api/patients/family/{familyId}` | 查询同一家庭的所有患者列表 |
| PUT | `/api/patients/{id}/link-family` | 将当前患者关联到目标患者的家庭 |
| PUT | `/api/patients/{id}/unlink-family` | 解除当前患者与家庭的关联 |

#### 接口详情

**1. 按病历号搜索患者**

```
GET /api/patients/search-by-medrec?medrecNum=MH20230001

Response:
{
  "code": 200,
  "data": {
    "id": "abc123",           // Hashids 编码
    "name": "张三",
    "sex": "1",
    "birthTime": "2015-03-15",
    "disClass": "10000006",
    "disClassName": "家族性矮小",
    "medrecNum": "MH20230001",
    "familyId": "uuid-xxx"
  }
}
```

**2. 查询家庭成员**

```
GET /api/patients/family/{familyId}

Response:
{
  "code": 200,
  "data": {
    "familyId": "uuid-xxx",
    "members": [
      { "id": "abc123", "name": "张三", "sex": "1", "disClassName": "家族性矮小", "relation": "兄" },
      { "id": "def456", "name": "张四", "sex": "1", "disClassName": "家族性矮小", "relation": "弟" }
    ]
  }
}
```

**3. 关联家庭成员**

```
PUT /api/patients/{id}/link-family
Body: { "targetMedrecNum": "MH20230001" }

Response:
{
  "code": 200,
  "message": "已关联到家庭",
  "data": { "familyId": "uuid-xxx" }
}
```

后端逻辑：
1. 根据 `targetMedrecNum` 查找目标患者，获取其 `familyId`
2. 将当前患者的 `familyId` 更新为目标患者的 `familyId`
3. 更新双方的 `familyMembers` JSON：如果当前患者是目标患者的"弟弟"，则在目标患者的 `familyMembers` 中补充"弟弟"记录，反之亦然
4. 事务保证一致性

### 3.3 前端交互流程

#### 家族成员表格新增"关联病历"按钮

```
┌──────────┬────────┬────────┬──────────┬────────┬──────────────┐
│ 关系     │ 性别   │ 身高   │ 类似疾病 │ 备注   │ 操作         │
├──────────┼────────┼────────┼──────────┼────────┼──────────────┤
│ 弟弟     │ 男     │ 145    │ 是       │        │ [删除][关联] │
│ 妹妹     │ 女     │ 140    │ 否       │        │ [删除][关联] │
└──────────┴────────┴────────┴──────────┴────────┴──────────────┘
```

**关联病历弹窗**：

```
┌─────────────────────────────────────┐
│  关联已有病历                       │
│                                     │
│  请输入病历号：                      │
│  ┌─────────────────────────────┐    │
│  │ MH20230001                  │    │
│  └─────────────────────────────┘    │
│  [搜索]                             │
│                                     │
│  搜索结果：                          │
│  ┌─────────────────────────────┐    │
│  │ ○ 张三 | 男 | 10岁 | 家族性矮小│  │
│  └─────────────────────────────┘    │
│                                     │
│  关系：弟弟  ← 自动填入             │
│                                     │
│  [确认关联]  [取消]                │
└─────────────────────────────────────┘
```

#### 关联后的谱图效果

关联后的家庭成员在谱图中**可点击**，点击跳转到该家庭成员的病历详情页。已关联的成员节点显示特殊标记（如虚线边框或链接图标）。

### 3.4 谱图中的多子女展示

```
              Ⅰ                 □────○
                             祖父  祖母
                                 │
              Ⅱ            □────○
                         父亲  母亲
                        ┌───┴───┐
              Ⅲ       ↗□      □
                    先证者   弟弟
                   (张三)   (张四)
                   10岁     7岁
                  145cm    120cm
                  患病     患病
                  [当前]  [点击查看]
```

- 先证者（当前患者）用箭头 `↗` 标识
- 同代兄弟姐妹并排在同一行，用水平横线连接
- 已关联的病历节点可点击跳转
- 未关联的节点仅显示信息，不可点击

---

## 四、实施计划

### 4.1 分阶段方案

| 阶段 | 内容 | 工作量 | 文件数 |
|------|------|--------|--------|
| **Phase 1** | 扩展 `familyMembers` JSON（性别/出生年份/患病/已故/代际）+ UI 表格新增列 | 2h | 1 (CaseDetail.vue) |
| **Phase 2** | 开发 `PedigreeChart.vue` SVG 谱图组件 | 6h | 2 (新建 PedigreeChart.vue + CaseDetail.vue 集成) |
| **Phase 3** | 后端 `family_id` 机制 + 病历号搜索 + 关联/解除 API | 4h | 5 (Patient.java/PatientDTO.java/PatientVO.java + PatientService + PatientController + DDL) |
| **Phase 4** | 前端关联病历弹窗 + 谱图可点击跳转 | 3h | 2 (CaseDetail.vue + PedigreeChart.vue) |
| **Phase 5** | 关联后双向数据同步 + 交叉引用维护 | 2h | 2 (PatientServiceImpl + CaseDetail.vue) |

**总工作量**：约 17h（2-3 个工作日）

### 4.2 详细实施步骤

#### Phase 1 — 数据模型扩展（2h）

**前端** `CaseDetail.vue`：
1. 扩展 `FAMILY_STANDARD_KEYS`：增加 `sex`、`birthYear`、`isAffected`、`isDeceased`、`generation`、`linkedPatientId`、`linkedMedrecNum`
2. `addFamilyMember()` 默认值补充：
   ```js
   const member = {
     relationship: '', sex: '1', birthYear: '',
     height: '', weight: '', isAffected: '0',
     isDeceased: '0', generation: 0,
     hasSimilarDisease: '否', notes: '',
     linkedPatientId: null, linkedMedrecNum: ''
   }
   ```
3. 表格新增列：性别（el-select）、出生年份（el-input）、患病（el-select 是/否）、关联病历号（el-input）
4. 表格移除"类似疾病"列（用 `isAffected` 替代），`hasSimilarDisease` 保留向后兼容

**兼容处理**：
- 读取旧 JSON 时，缺失的新字段自动补默认值
- `hasSimilarDisease === '是'` → `isAffected = '1'`
- 保存时同时写新旧字段

#### Phase 2 — SVG 谱图组件（6h）

**新建** `src/views/case/components/PedigreeChart.vue`：

1. **数据转换函数** `buildPedigreeData(familyMembers, patientInfo)`
   - 将患者自身作为先证者节点
   - 遍历 familyMembers，根据 relationship 推断代际
   - 对每个成员补充 sex、birthYear、isAffected
   - 建立配偶关系对（父亲↔母亲，祖父↔祖母等）

2. **布局计算函数** `computeLayout(pedigreeNodes, options)`
   - 按 generation 分组
   - 同代节点按 birthYear 排序
   - 计算 X/Y 坐标

3. **SVG 渲染**
   - 连接线层（z-index: 0）
   - 节点层（z-index: 1）：rect/circle + 填充色 + 斜线
   - 标签层（z-index: 2）：关系名、年龄、身高
   - 交互层（z-index: 3）：hover tooltip + click handler

4. **样式常量**
   ```js
   const MALE_COLOR = '#e8f4fd'      // 男性底色（浅蓝）
   const FEMALE_COLOR = '#fde8ef'     // 女性底色（浅粉）
   const AFFECTED_FILL = '#409EFF'    // 患病填充色
   const NODE_WIDTH = 44
   const NODE_HEIGHT = 36
   const GENERATION_GAP = 120         // 代际间距
   const SIBLING_GAP = 80             // 兄弟姐妹间距
   ```

5. **导出功能**：SVG → Canvas → PNG 下载（用于病历打印）

#### Phase 3 — 后端家庭关联（4h）

**DDL**：`init-sql/05-family-link.sql`
```sql
ALTER TABLE datamain_patient ADD COLUMN IF NOT EXISTS family_id VARCHAR(36) COMMENT '家庭分组ID';
CREATE INDEX IF NOT EXISTS idx_family_id ON datamain_patient(family_id);
UPDATE datamain_patient SET family_id = UUID() WHERE family_id IS NULL;
```

**Patient.java** 新增字段：
```java
/** 家庭分组ID（同一家庭的患者共享） */
@TableField("family_id")
private String familyId;
```

同步更新 `PatientDTO.java`、`PatientVO.java`。

**PatientService.java** 新增方法：
```java
PatientVO searchByMedrecNum(String medrecNum);
List<PatientVO> getFamilyMembers(String familyId);
void linkFamily(String patientId, String targetMedrecNum);
void unlinkFamily(String patientId);
```

**PatientController.java** 新增接口（4 个，见 3.2 节）。

**PatientServiceImpl.java** 实现要点：
- `searchByMedrecNum`：精确匹配查询，返回脱敏信息
- `linkFamily`：
  1. 查目标患者，取其 familyId
  2. 查当前患者，更新 familyId
  3. 更新双方 familyMembers JSON（补充关联信息）
  4. @Transactional 保证原子性
- `unlinkFamily`：恢复独立 familyId，清理交叉引用

#### Phase 4 — 前端关联交互（3h）

**CaseDetail.vue 新增**：
1. "关联病历"按钮（每行操作列）
2. 关联病历弹窗（搜索 + 确认）
3. 表格视图 / 谱图视图 切换按钮
4. 引入 `PedigreeChart.vue` 组件

**PedigreeChart.vue 新增**：
1. 已关联节点可点击 → `router.push('/case/...')` 跳转
2. 关联节点特殊样式（边框加粗/虚线/图标）
3. hover 时显示详情 tooltip

**api/patient.js** 新增：
```js
export function searchPatientByMedrec(medrecNum) { ... }
export function getFamilyMembers(familyId) { ... }
export function linkFamilyMember(patientId, targetMedrecNum) { ... }
export function unlinkFamilyMember(patientId) { ... }
```

#### Phase 5 — 双向同步（2h）

**关联时的双向数据维护**：
- 患者 A 关联患者 B 为"弟弟"
- 系统自动在患者 B 的 `familyMembers` 中添加一条"哥哥"记录（含患者 A 的 linkedPatientId）
- 解除关联时同时清理双方的交叉引用
- 患者信息变更时（如身高体重更新），不自动同步到家族成员记录（尊重用户手动输入）

---

## 五、涉及文件清单

### 需新建

| 文件 | 说明 |
|------|------|
| `eksjk_v2/eksjk-frontend/src/views/case/components/PedigreeChart.vue` | SVG 谱图组件 |
| `eksjk_v2/init-sql/05-family-link.sql` | family_id DDL |
| `eksjk_v2/docs/Jiazushi.md` | 本文档 |

### 需修改

| 文件 | 修改内容 |
|------|---------|
| `eksjk_v2/eksjk-frontend/src/views/case/CaseDetail.vue` | 扩展 familyMembers 字段；新增关联病历弹窗；集成 PedigreeChart |
| `eksjk_v2/eksjk-frontend/src/api/patient.js` | 新增 4 个家庭关联 API |
| `eksjk_v2/eksjk-backend/eksjk-model/.../entity/Patient.java` | 新增 `familyId` 字段 |
| `eksjk_v2/eksjk-backend/eksjk-model/.../dto/PatientDTO.java` | 新增 `familyId` 字段 |
| `eksjk_v2/eksjk-backend/eksjk-model/.../vo/PatientVO.java` | 新增 `familyId` 字段 |
| `eksjk_v2/eksjk-backend/eksjk-service/.../PatientService.java` | 新增 4 个接口方法 |
| `eksjk_v2/eksjk-backend/eksjk-service/.../impl/PatientServiceImpl.java` | 实现家庭关联逻辑 |
| `eksjk_v2/eksjk-backend/eksjk-web/.../controller/PatientController.java` | 新增 4 个 API 端点 |

### 无需修改

- V1 代码（仅参考）
- 小程序端（本期不涉及）

---

## 六、风险与注意事项

1. **代际推断不是 100% 准确**：基于"关系"字符串推断，如"叔叔""姑姑"等无法自动判断代际，需用户手动调整 `generation` 字段
2. **并发关联**：两个用户同时关联同一个病历号 → 使用数据库行锁或乐观锁
3. **JSON 向后兼容**：旧版 `familyMembers` 缺少新字段，读取时需补默认值
4. **打印适配**：SVG 谱图需支持高 DPI 打印，建议同时提供 PNG 导出
5. **性能**：谱图组件使用 Vue 的 `shallowRef` 避免深度响应式开销（SVG 节点可能很多）

---

## 七、拓展可能（后续迭代）

1. **家族疾病遗传模式分析**：基于谱图自动识别常染色体显性/隐性、X连锁遗传模式
2. **家族生长曲线叠加**：同一家庭多名患者的身高体重曲线叠加展示
3. **小程序端谱图查看**：家长在小程序中查看家庭生长概览
4. **谱图导出为 PDF**：用于遗传咨询报告