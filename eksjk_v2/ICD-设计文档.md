# 国际疾病分类(ICD)字段改造设计方案

## 1. 现状分析

### 1.1 当前实现

- **前端**：`CaseDetail.vue` 中 ICD 字段为普通 `<el-input>` 文本框，无任何约束或辅助输入
- **后端**：`Patient.icd` 为 `VARCHAR(50)` 自由文本字段，无校验逻辑
- **数据库**：`datamain_patient.ICD VARCHAR(50)`
- **数据源**：项目根目录下有 `ICD10(3).xlsx`（23,068行，20,484个唯一编码），但未被系统使用

### 1.2 问题

用户可输入任意文本，无法保证 ICD 编码的规范性和准确性。

---

## 2. 数据源分析

### 2.1 ICD10(3).xlsx 结构

| 列 | 说明 | 示例 |
|----|------|------|
| `main_code` | ICD-10 主编码 | `E34.301`, `Q56.100` |
| `sub_code` | 亚目编码（`*` 编码的临床表现位置） | `G01*`, `K77.0*` |
| `description` | 中文疾病名称 | `矮小症`, `男性假两性畸形` |

### 2.2 数据统计

| 项目 | 数值 |
|------|------|
| 总行数 | 23,068 |
| 有效编码数 | 20,484 |
| 标准编码（如 `E34.301`） | 19,501 |
| 剑号编码（如 `A01.001†`） | 984 |
| 含亚目编码（`†`/`*` 双重分类） | 3,116 |
| 空 main_code（标题行） | 2,583 |

### 2.3 章节分布

| 首字母 | 章节名称 | 条目数 |
|--------|----------|--------|
| A-B | 传染病和寄生虫病 | 1,577 |
| C-D | 肿瘤 | 2,476 |
| **E** | **内分泌、营养和代谢疾病** | **826** |
| F-G | 精神和神经系统疾病 | 1,294 |
| H | 眼和附器/耳和乳突疾病 | 853 |
| I-J-K | 循环/呼吸/消化系统疾病 | 3,191 |
| L | 皮肤和皮下组织疾病 | 630 |
| M | 肌肉骨骼系统和结缔组织疾病 | 1,644 |
| N | 泌尿生殖系统疾病 | 993 |
| O | 妊娠、分娩和产褥期 | 1,018 |
| P | 起源于围生期的某些情况 | 486 |
| **Q** | **先天畸形、变形和染色体异常** | **1,373** |
| R | 症状、体征和异常临床所见 | 577 |
| S-T | 损伤、中毒和外因 | 2,559 |
| Z | 影响健康状态和保健机构接触的因素 | 986 |

> E + Q 两个章节共约 2,199 条，是儿科内分泌系统最相关的编码范围。

### 2.4 与儿科内分泌直接相关的重点编码示例

| 编码 | 名称 | 关联疾病 |
|------|------|----------|
| `E22.802` | 中枢性性早熟 | CPP |
| `E25.802` | 女性肾上腺性假两性畸形 | DSD |
| `E30.100` | 性早熟 | CPP |
| `E30.101` | 周围性性早熟 | CPP |
| `E30.000` | 青春期延迟 | 发育评估 |
| `E34.300` | 身材矮小症 | SSS, GHD |
| `E34.301` | 矮小症 | SSS |
| `E34.302` | 家族性身材矮小症 | SSS |
| `E23.005` | 垂体性矮小症 | GHD |
| `E23.009` | 生长激素缺乏症 | GHD |
| `Q56.000` | 两性畸形 | DSD |
| `Q56.100` | 男性假两性畸形 | DSD |
| `Q56.200` | 女性假两性畸形 | DSD |
| `Q54.000-Q54.900` | 尿道下裂(7亚目) | DSD |
| `Q53.x00` | 隐睾 | DSD |
| `Q96.901` | 男性性腺发育不全症 | DSD |
| `Q78.000` | 成骨不全 | FSS |
| `Q77.100` | 致死性身材矮小症 | FSS |
| `P05.000-P08.200` | 小于胎龄儿系列 | SGA |

---

## 3. 设计方案

### 3.1 架构总览

```
ICD10(3).xlsx
     │
     ▼ Python 脚本（一次性转换）
icd-data.js（静态 JSON 数组）
     │
     ├──▶ IcdAutocomplete.vue（可复用组件）
     │         │
     │         ▼ el-autocomplete + 本地过滤
     │    CaseDetail.vue（新建/编辑/查看）
     │
     ▼ icdLabelMap（code → description 映射）
     CaseList.vue（列表展示中文名称）
```

### 3.2 数据转换：Excel → JS 模块

新建 Python 脚本 `scripts/generate-icd-data.py`，从 `ICD10(3).xlsx` 生成 `src/data/icdData.js`：

**输入**：`eksjk_v2/ICD10(3).xlsx`

**输出**：`eksjk_v2/eksjk-frontend/src/data/icdData.js`

**处理逻辑**：
1. 读取 Excel，过滤 `main_code` 为空的行（标题行）
2. 只取 `main_code` 和 `description` 两列（忽略用于双重分类的 `sub_code`）
3. 对 `main_code` 去除末尾的 `†` 剑号符号（`A01.001†` → `A01.001`），去重
4. 生成 JS 模块：
   ```js
   // 供 el-autocomplete 使用的完整数据
   export const icdOptions = [
     { value: 'E34.301', label: 'E34.301 矮小症' },
     { value: 'E30.100', label: 'E30.100 性早熟' },
     // ...约 19,500 条（去除 † 编码后）
   ]
   
   // 供列表展示的 code → name 映射
   export const icdLabelMap = {
     'E34.301': '矮小症',
     'E30.100': '性早熟',
     // ...
   }
   ```

**数据量评估**：
| 项目 | 估算 |
|------|------|
| 有效条目（去 † 去重后） | ~19,500 条 |
| 单条 JS 字节数 | ~80 bytes |
| 文件的原始大小 | ~1.6 MB |
| gzip 后大小（Vite 构建） | ~250 KB |

> 250KB gzip 后可接受。实际使用时浏览器只加载一次并被缓存，`el-autocomplete` 的本地过滤性能足够（对 2 万条数据进行 `indexOf` 匹配耗时 < 5ms）。

### 3.3 前端组件：IcdAutocomplete.vue

**位置**：`src/views/case/components/IcdAutocomplete.vue`

**实现方案**：使用 Element Plus 的 `el-autocomplete` 组件，基于本地数据过滤：

```vue
<template>
  <el-autocomplete
    v-model="modelValue"
    :fetch-suggestions="querySearch"
    :placeholder="placeholder"
    :disabled="disabled"
    clearable
    value-key="value"
    @select="handleSelect"
  />
</template>
```

**核心交互逻辑**：
| 行为 | 实现 |
|------|------|
| 用户输入文字 | `fetch-suggestions` 本地过滤 icdOptions，匹配编码或名称 |
| 用户选中建议项 | 输入框填入编码（如 `E34.301`），下拉收起 |
| 清空 | 支持 `clearable` |
| 失焦校验 | 可配置：若输入值不在有效编码列表中则置空或标红 |
| 只读模式 | `disabled` prop 由父组件控制 |

**过滤策略**：
- 匹配优先级：精确编码匹配 > 编码前缀匹配 > 名称包含匹配
- 最多返回 20 条建议，避免下拉列表过长
- 匹配关键字高亮（可选增强）

### 3.4 集成点

#### 3.4.1 CaseDetail.vue（病例表单）

修改位置：原 `<el-input>` 替换为 `<IcdAutocomplete>`

```html
<!-- 之前 -->
<el-form-item label="国际疾病分类" prop="icd">
  <el-input v-model="formData.icd" placeholder="请输入ICD编码" />
</el-form-item>

<!-- 之后 -->
<el-form-item label="国际疾病分类" prop="icd">
  <IcdAutocomplete 
    v-model="formData.icd" 
    :disabled="isViewMode"
    placeholder="请输入ICD编码或疾病名称搜索" 
  />
</el-form-item>
```

#### 3.4.2 CaseList.vue（病例列表展示）

当前列表中的 ICD 列直接显示原始编码（如 `E34.301`）。建议增加中文名称展示：

```html
<!-- 之前 -->
<el-table-column prop="icd" label="ICD" />

<!-- 之后 -->
<el-table-column label="ICD">
  <template #default="{ row }">
    {{ row.icd ? `${row.icd} ${icdLabelMap[row.icd] || ''}` : '' }}
  </template>
</el-table-column>
```

#### 3.4.3 数据模块引入

```js
// 在需要的组件中
import { icdOptions, icdLabelMap } from '@/data/icdData'
```

### 3.5 方案对比

| 维度 | 方案A：下拉选择器 | 方案B：后端API搜索 | **方案C：本地autocomplete（推荐）** |
|------|-------------------|---------------------|----------------------------------|
| 数据量 | 2万条渲染极慢 ❌ | 按需返回 ✅ | 一次加载JS，本地过滤 ✅ |
| 用户体验 | 滚动查找困难 ❌ | 需等待网络 ⚠️ | 即时过滤响应 ✅ |
| 开发工时 | 纯前端0.5天 | 前后端2天 | 纯前端1天 |
| 首屏加载 | JS过大(~1.6MB) ❌ | 无额外加载 ✅ | JS ~250KB gzip ✅ |
| 维护性 | 需每次构建 | 需维护DB+API | 仅需重新生成JS ✅ |

**推荐方案C**：本地 autocomplete。2万条数据对现代浏览器完全可接受，过滤速度毫秒级，且无需额外后端开发。

---

## 4. 文件变更清单

### 4.1 新增文件

| 文件 | 说明 |
|------|------|
| `scripts/generate-icd-data.py` | Python 脚本：Excel → JS 数据模块转换 |
| `eksjk-frontend/src/data/icdData.js` | 生成的 ICD 数据模块（含 icdOptions + icdLabelMap） |
| `eksjk-frontend/src/views/case/components/IcdAutocomplete.vue` | ICD 自动补全组件 |

### 4.2 修改文件

| 文件 | 修改内容 |
|------|---------|
| `eksjk-frontend/src/views/case/CaseDetail.vue` | ICD 字段从 `<el-input>` 改为 `<IcdAutocomplete>` |
| `eksjk-frontend/src/views/case/CaseList.vue` | ICD 列表列增加中文名称展示 |

### 4.3 可选增强（后续迭代）

| 项目 | 说明 |
|------|------|
| 失焦校验 | 输入值不在列表中时给用户提示 |
| ICD 层级树 | 按章节/亚目组织树形选择器 |
| 常用编码 | 按科室记录使用频率，置顶常用 ICD 编码 |
| 编码详情弹窗 | 点击编码查看完整 ICD 层级路径及说明 |

---

## 5. 实施步骤

### 步骤1：生成 ICD 数据模块

```bash
cd eksjk_v2
python3 scripts/generate-icd-data.py
```

### 步骤2：创建 IcdAutocomplete 组件

基于 `el-autocomplete` + 本地 `icdOptions` 过滤，支持：
- 输入关键词即时搜索（匹配编码或名称）
- 选中后填入纯编码
- 清空按钮
- disabled 模式（查看页）

### 步骤3：改造 CaseDetail.vue

ICD 表单字段替换为 IcdAutocomplete 组件。

### 步骤4：增强 CaseList.vue

ICD 列表列展示编码 + 中文名称。

### 步骤5：验证

| 验证场景 | 预期行为 |
|----------|---------|
| 新建病例，ICD 输入"矮" | 下拉显示所有含"矮"的编码（矮小症、垂体性矮小症等） |
| 选中建议项 | 输入框填入编码（如 E34.301） |
| 保存后查看详情 | ICD 字段显示编码，列表显示编码+中文名 |
| 搜索不存在的编码 | 无建议项，可配置为提示用户 |
| 查看模式 | 输入框不可编辑 |
| 编辑已有病例 | ICD 字段回填已有编码值 |

---

## 6. 数据更新流程

当 ICD-10 编码表有更新时：

1. 替换 `eksjk_v2/ICD10(3).xlsx` 为新版本
2. 运行 `python3 scripts/generate-icd-data.py` 重新生成 `icdData.js`
3. 提交两个文件到 Git