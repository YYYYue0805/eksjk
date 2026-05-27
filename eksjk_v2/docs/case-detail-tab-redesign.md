# EKSJK V2 病例详情页 Tab 重构 —— 完整对照方案

> 基准：V2 Tab 1（基本信息）完全不变，其余 Tab 按 V1 模块完整性审计后设计
> 日期：2026-05-24

---

## 一、最终 6-Tab 结构

| # | Tab 名称 | 来源 | 状态 |
|---|---------|------|------|
| 1 | 基本信息 | V2 现有 Tab 1 | **完全不变（20字段）** |
| 2 | 基线-临床信息 | V2 现有 临床资料 tab + V1 临床资料模块 | 扩展 |
| 3 | 基线-辅助检查 | V1 检验检查模块 | **全新** |
| 4 | 遗传学检查 | V1 遗传学检查模块 | **全新** |
| 5 | 随访记录 | V2 现有 + V1 生长曲线 | 扩展 |
| 6 | 诊断 | V1 诊断模块（从 DiseaseFormGeneric 移出） | **全新** |

---

## 二、Tab 1：基本信息 — 保持不变

20 个字段原样保留，不做任何增删改：

name, caseNum, medrecNum, sex, icd, categoryDescribe, birthTime, card, famAdr, contactsName, relation, selfTel, bwt, bl, gesWeek, cesaSec, cesaAsphyxia, ethnic, gonadalSex, chiCom

---

## 三、Tab 2：基线-临床信息

### 3.1 已有（从 V2 临床资料 tab 移入，14 字段）

height, weight, bmi(computed), rboneAge, cboneAge, fht, mht, fhw, mhw, menAge, isBot, familyHis, pastHis, firVisAge

### 3.2 P0 必须补充

**Tanner 分期（V1 全疾病共有）：**
| genStag | 生殖器发育 | select(I-V) |
| pubStag | 阴毛发育 | select(I-V) |

**DSD 特有（11字段）：**
penileLength, penileDia, tesVolume, prader, locaUreOri, rigTesPos, lefTesPos, breastDev, exGenitalia, bodyOther
→ V1 来源：DSD.vue "患者信息" tab

**矮小/CPP：**
| heightSds | 身高SDS | FSS, CPP |
| weightSds | 体重SDS | FSS, CPP |
| lowerMeasure | 下部量(cm) | SGA, SSS |
| onsetAge | 发病年龄 | CPP |
| boneAgeAdvance | 骨龄提前 | CPP |

**MAS 特有：**
cafeAuLaitSpots, fibrousDysplasia, precociousPuberty, thyroidAbnormality, ghExcess, cushingSyndrome, phosphateWasting

**ELTM 特有：**
generalSymptoms, metabolicSymptoms, boneMuscleSymptoms, endocrineSymptoms, otherSymptoms

### 3.3 P1 应该补充

- firVisTime（初诊时间）
- 家族成员扩展表（familyData 数组）

---

## 四、Tab 3：基线-辅助检查

### 4.1 P0 核心实验室（全疾病）

| lh | LH | fsh | FSH | e2 | E2 | t | T | prl | PRL |
| igf1 | IGF-1 | igfbp3 | IGFBP-3 |
| fasBloodGlu | 空腹血糖 | fasInsulin | 空腹胰岛素 | glyHem | 糖化血红蛋白 |

### 4.2 P0 DSD 专属

| dht | DHT | ft | FT | shbg | SHBG | amh | AMH | inhb | INHB |

### 4.3 P0 肾上腺

| acth | ACTH | cortisol | 皮质醇 | ohp | 17-OHP(DSD) | dheas | DHEA-S | ad | 雄烯二酮(DSD) |

### 4.4 P1 激发试验

| hcg/hcgt/hcgdht/hcgad | HCG激发(DSD) | lhMax/fshMax | GnRH激发(DSD,CPP) |

### 4.5 P0 影像

| gonBUlt | 性腺B超 | pituitaryMri | 垂体MRI | thyroidUlt | 甲状腺B超 | bonMinDen | 骨密度 |

### 4.6 影像上传

嵌入 FileUpload（骨龄图片/脊柱全长片/其他三类）

---

## 五、Tab 4：遗传学检查

### 5.1 P0

| karyotype | 染色体核型 (DSD, MAS, SSS) |

**基因检测表**（genData 数组，DSD/FSS/SSS 有）：
每条记录含 geneName, mutationSite, mutationType, sequencingMethod, inheritanceMode, pathogenicity

### 5.2 P1

| biologBank / biologBankFa / biologBankMo | 患者/父亲/母亲样本库 |

---

## 六、Tab 5：随访记录

### 6.1 已有
FollowUpList + FollowUpForm（约20字段）

### 6.2 P0 补充
生长曲线图表（ECharts 身高/体重/BMI 随时间变化 + 参考曲线）— V1 所有疾病子表单的随访 tab 都有

### 6.3 P1 补充
MAS 专项随访（外周性早熟/甲亢/GH过多/皮质醇增多/骨痛随访子表）

---

## 七、Tab 6：诊断

### 7.1 P0（从 DiseaseFormGeneric 移入）

| diagnosis | 诊断结论 | textarea/cascader |
| secondaryDiagnosis | 次要诊断 | textarea |
| treatmentPlan | 治疗方案 | textarea |
| isTreated | 是否已治疗 | select |

### 7.2 P1
V1 诊断级联选择器（生长激素缺乏症/特发性矮小/... → 子诊断）

---

## 八、后端新增字段

Patient 表新增约 35 列（Nullable VARCHAR），同步更新 DTO/VO/DDL：

```
Tanner: gen_stag, pub_stag
体格: height_sds, weight_sds, lower_measure
激素: lh, fsh, e2, t, prl, dht, ft, shbg, amh, inhb
生长/代谢: igf1, igfbp3, fas_blood_glu, fas_insulin, gly_hem
肾上腺: acth, cortisol, ohp, dheas, ad
激发: hcg, hcgt, hcgdht, hcgad, lh_max, fsh_max
甲状腺: tsh, ft3, ft4, tpoab, tgab
影像: gon_b_ult, pituitary_mri, thyroid_ult, bon_min_den
遗传: karyotype, biolog_bank, biolog_bank_fa, biolog_bank_mo
```

---

## 九、P2 暂不纳入（后续迭代）

| 模块 | V1 规模 | 延后原因 |
|------|---------|---------|
| MAS 9-tab 完整版 | ~170字段 | 极其复杂，先实现核心临床表现 |
| ELTM 不良事件+用药 | ~30字段 | 临床试验特有 |
| Cornerstone.js DICOM | ImageViewer.vue | 依赖第三方库 |
| 批量ZIP下载 | downZipPl | 独立功能 |
| 血/尿常规+生化全套 | FSS/DSD/CPP 检验检查 | 字段量大，可用 textarea 暂代 |

---

## 十、实施步骤

1. **后端**：新增约35个Patient表字段 + DTO/VO + DDL变更
2. **新建组件**：BaselineExamForm.vue, GeneticsExamForm.vue, DiagnosisForm.vue
3. **修改**: DiseaseFormGeneric.vue（移除已分配字段）, CaseDetail.vue（6-tab，Tab1不变）
4. **补充**: Tab5 生长曲线图表
5. **7疾病×3模式测试**