<template>
  <div class="case-detail">
    <PageHeader :title="pageTitle">
      <template #actions>
        <el-button @click="router.back()">返回列表</el-button>
        <el-button v-if="isViewMode" type="primary" @click="switchToEdit">编辑</el-button>
        <el-dropdown v-if="showReclassify" style="margin-left: 8px"
                      @command="handleReclassifyConfirm">
          <el-button type="warning">
            重新分类 <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="t in reclassifyTargets" :key="t.code" :command="t.code">
                迁移为：{{ t.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <template v-if="!isViewMode">
          <el-button @click="prevTab" :disabled="activeTab === tabList[0].name">上一步</el-button>
          <el-button @click="nextTab" :disabled="activeTab === tabList[tabList.length - 1].name">下一步</el-button>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </template>
      </template>
    </PageHeader>

    <!-- ELTM 智能诊断状态提示 -->
    <el-alert v-if="isViewMode && formData.diagnosisStatus === 'diagnosed'"
              title="已诊断 — 该病例已完成疾病分类，归属具体病种模块"
              type="success" show-icon :closable="false" style="margin-bottom: 16px" />
    <el-alert v-if="isViewMode && diseaseType === 'eltm' && formData.diagnosisStatus === 'uncertain'"
              title="待审查 — 系统无法自动确定该病例的病种分类，请根据检查信息手动判断并执行重新分类"
              type="warning" show-icon :closable="false" style="margin-bottom: 16px" />
    <el-alert v-if="isViewMode && diseaseType === 'eltm' && formData.diagnosisStatus === 'suggested'"
              title="建议归类 — 系统已根据检查指标给出分类建议，请在「重新分类」下拉菜单中选择目标病种"
              type="info" show-icon :closable="false" style="margin-bottom: 16px" />
    <el-alert v-if="isViewMode && diseaseType === 'eltm' && formData.diagnosisStatus === 'auto_classified'"
              title="已自动归类 — 该病例已被系统自动分类到目标病种"
              type="success" show-icon :closable="false" style="margin-bottom: 16px" />

    <!-- 审核发放状态提示 -->
    <el-alert v-if="isViewMode && formData.auditStatus === 'pending_review'"
              title="待审核 — 该病例尚未通过基线审核，请联系审核人员处理"
              type="warning" show-icon :closable="false" style="margin-bottom: 16px" />
    <el-alert v-if="isViewMode && formData.auditStatus === 'pending_release'"
              title="待发放 — 该病例基线已审核通过，待发放后正式生效"
              type="info" show-icon :closable="false" style="margin-bottom: 16px" />
    <el-alert v-if="isViewMode && formData.auditStatus === 'rejected'"
              :title="'已驳回 — ' + (formData.auditRemark || '未填写原因')"
              type="error" show-icon :closable="false" style="margin-bottom: 16px" />

    <el-form ref="formRef" :model="formData" :disabled="isViewMode" label-width="130px">
      <el-tabs v-model="activeTab" class="detail-tabs" type="border-card">

        <!-- Tab 1：基本信息（完全不变） -->
        <el-tab-pane label="基本信息" name="basic">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="患者姓名" prop="name"
                            :rules="[{ required: true, message: '请输入患者姓名', trigger: 'blur' }]">
                <el-input v-model="formData.name" placeholder="请输入患者姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="病例编号">
                <el-input v-model="formData.caseNum" disabled :placeholder="isCreateMode ? '保存后自动生成' : ''" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="病历号" prop="medrecNum"
                            :rules="[{ required: true, message: '请输入病历号', trigger: 'blur' }]">
                <el-input v-model="formData.medrecNum" placeholder="请输入病历号" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别" prop="sex"
                            :rules="[{ required: true, message: '请选择性别', trigger: 'change' }]">
                <el-select v-model="formData.sex" placeholder="请选择">
                  <el-option label="男" value="1" />
                  <el-option label="女" value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="国际疾病分类" prop="icd">
                <IcdAutocomplete v-model="formData.icd" :disabled="isViewMode" placeholder="请输入ICD编码或疾病名称搜索" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="致病基因" prop="categoryDescribe">
                <el-input v-model="formData.categoryDescribe" placeholder="请输入致病基因" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="出生日期" prop="birthTime"
                            :rules="[{ required: true, message: '请选择出生日期', trigger: 'change' }]">
                <el-date-picker v-model="formData.birthTime" type="date" placeholder="选择日期"
                                value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="身份证号码" prop="card"
                            :rules="[{ required: isCreateMode, message: '请输入身份证号码', trigger: 'blur' },
                                     { validator: validateIdCard, trigger: 'blur' }]">
                <el-input v-model="formData.card" placeholder="请输入身份证号码" maxlength="18" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="家庭地址" prop="famAdr">
                <el-input v-model="formData.famAdr" placeholder="请输入家庭地址" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="联系人姓名" prop="contactsName">
                <el-input v-model="formData.contactsName" placeholder="请输入联系人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="与患者关系" prop="relation">
                <el-input v-model="formData.relation" placeholder="请输入与患者关系" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="联系电话" prop="selfTel"
                            :rules="[{ required: true, message: '请输入联系电话', trigger: 'blur' },
                                     { validator: validatePhone, trigger: 'blur' }]">
                <el-input v-model="formData.selfTel" placeholder="请输入联系电话" maxlength="20" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="出生体重" prop="bwt"
                            :rules="[{ required: isCreateMode, message: '请输入出生体重', trigger: 'blur' }]">
                <el-input v-model="formData.bwt" placeholder="kg"><template #suffix>kg</template></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="出生身长" prop="bl">
                <el-input v-model="formData.bl" placeholder="cm"><template #suffix>cm</template></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="孕周" prop="gesWeek"
                            :rules="[{ required: isCreateMode, message: '请输入孕周', trigger: 'blur' }]">
                <el-input v-model="formData.gesWeek" placeholder="周"><template #suffix>周</template></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="分娩方式" prop="cesaSec"
                            :rules="[{ required: isCreateMode, message: '请选择分娩方式', trigger: 'change' }]">
                <el-select v-model="formData.cesaSec" placeholder="请选择">
                  <el-option label="顺产" value="0" /><el-option label="剖宫产" value="1" />
                  <el-option label="臀围产" value="2" /><el-option label="足先露" value="3" />
                  <el-option label="其他" value="4" /><el-option label="不详" value="5" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="窒息抢救史" prop="cesaAsphyxia">
                <el-select v-model="formData.cesaAsphyxia" placeholder="请选择">
                  <el-option label="无" value="1" /><el-option label="轻度窒息" value="2" />
                  <el-option label="重度窒息" value="3" /><el-option label="有" value="4" />
                  <el-option label="不详" value="5" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="民族" prop="ethnic">
                <el-input v-model="formData.ethnic" placeholder="请输入民族（选填）" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- Tab 2：基线-临床信息 -->
        <el-tab-pane label="基线-临床信息" name="baselineClinical">
          <!-- 体格检查 -->
          <div class="tab-section-title">体格检查</div>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="身高"><el-input v-model="formData.height" placeholder="cm" @blur="calcBmi"><template #suffix>cm</template></el-input></el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="体重"><el-input v-model="formData.weight" placeholder="kg" @blur="calcBmi"><template #suffix>kg</template></el-input></el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="BMI"><el-input v-model="bmiValue" disabled /></el-form-item>
            </el-col>
            <el-col v-if="['fss','cpp'].includes(diseaseType)" :span="6">
              <el-form-item label="身高SDS"><el-input v-model="formData.heightSds" /></el-form-item>
            </el-col>
            <el-col v-if="['fss','cpp'].includes(diseaseType)" :span="6">
              <el-form-item label="体重SDS"><el-input v-model="formData.weightSds" /></el-form-item>
            </el-col>
            <el-col v-if="['sga','sss'].includes(diseaseType)" :span="6">
              <el-form-item label="下部量(cm)"><el-input v-model="formData.lowerMeasure" /></el-form-item>
            </el-col>
            <!-- 性别相关生理字段 -->
            <template v-if="formData.sex === '2'">
              <el-col :span="6">
                <el-form-item label="乳腺发育(左)">
                  <el-select v-model="formData.breastDevLeft" placeholder="请选择" clearable :disabled="isViewMode">
                    <el-option v-for="i in 5" :key="i" :label="'B' + i" :value="'B' + i" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="乳腺发育(右)">
                  <el-select v-model="formData.breastDevRight" placeholder="请选择" clearable :disabled="isViewMode">
                    <el-option v-for="i in 5" :key="i" :label="'B' + i" :value="'B' + i" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="阴毛分期">
                  <el-select v-model="formData.pubStag" placeholder="请选择" clearable :disabled="isViewMode">
                    <el-option v-for="i in 5" :key="i" :label="String(i)" :value="String(i)" />
                  </el-select>
                </el-form-item>
              </el-col>
            </template>
            <template v-if="formData.sex === '1'">
              <el-col :span="6">
                <el-form-item label="外生殖器分期">
                  <el-select v-model="formData.genStag" placeholder="请选择" clearable :disabled="isViewMode">
                    <el-option v-for="i in 5" :key="i" :label="'G' + i" :value="'G' + i" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="阴毛分期">
                  <el-select v-model="formData.pubStag" placeholder="请选择" clearable :disabled="isViewMode">
                    <el-option v-for="i in 5" :key="i" :label="String(i)" :value="String(i)" />
                  </el-select>
                </el-form-item>
              </el-col>
            </template>
          </el-row>
          <!-- 体格检查扩展 -->
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="臂长">
                <el-input v-model="formData.armLength" placeholder="cm" :disabled="isViewMode">
                  <template #suffix>cm</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="特殊面容">
                <el-radio-group v-model="formData.specialFace" :disabled="isViewMode">
                  <el-radio value="1">无</el-radio>
                  <el-radio value="2">有</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="formData.specialFace === '2'" :span="12">
              <el-form-item label="描述">
                <el-input v-model="formData.specialFaceDesc" placeholder="请输入特殊面容具体情况" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="脊柱侧弯">
                <el-radio-group v-model="formData.scoliosis" :disabled="isViewMode">
                  <el-radio value="1">无</el-radio>
                  <el-radio value="2">有</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="formData.scoliosis === '2'" :span="12">
              <el-form-item label="描述">
                <el-input v-model="formData.scoliosisDesc" placeholder="请输入脊柱侧弯具体情况" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="皮疹">
                <el-radio-group v-model="formData.rash" :disabled="isViewMode">
                  <el-radio value="1">无</el-radio>
                  <el-radio value="2">有</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="formData.rash === '2'" :span="12">
              <el-form-item label="描述">
                <el-input v-model="formData.rashDesc" placeholder="请输入皮疹具体情况" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 疾病特有临床表现（从 DiseaseFormGeneric 移入） -->
          <template v-if="hasDiseaseClinicalFields">
            <el-divider />
            <div class="tab-section-title">{{ diseaseName }} 专项临床信息</div>
            <component :is="DiseaseFormGeneric" v-model="formData.diseaseData" :disabled="isViewMode" :disease-type="diseaseType" />
          </template>

          <!-- 病史 -->
          <el-divider />
          <div class="tab-section-title">病史</div>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="初诊时间">
                <el-date-picker v-model="formData.firVisTime" type="date" placeholder="选择日期"
                  value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="初诊年龄">
                <el-input v-model="formData.firVisAge" placeholder="岁" disabled />
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 生长速率（所有疾病类型） -->
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="生长速率">
                <el-radio-group v-model="formData.growRate" :disabled="isViewMode">
                  <el-radio value="1">不详</el-radio>
                  <el-radio value="2">请选择</el-radio>
                </el-radio-group>
                <el-select v-if="formData.growRate === '2'" v-model="formData.rate" placeholder="cm/年"
                  style="width:140px;margin-left:12px" :disabled="isViewMode">
                  <el-option v-for="v in growthRateOptions" :key="v" :label="v + ' cm/年'" :value="String(v)" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <!-- MAS 身高增长速度 -->
          <el-row v-if="diseaseType === 'mas'" :gutter="20">
            <el-col :span="8">
              <el-form-item label="身高增长速度">
                <el-input v-model="formData.diseaseData.heightRate" placeholder="cm/年" :disabled="isViewMode">
                  <template #suffix>cm/年</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 主诉、一般检查、初次遗精 -->
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="主诉">
                <el-input v-model="formData.chiCom" type="textarea" :rows="3" placeholder="请输入主诉" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 一般检查 -->
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="是否有一般检查">
                <el-radio-group v-model="formData.hasGeneralExam" :disabled="isViewMode">
                  <el-radio value="1">有</el-radio>
                  <el-radio value="0">无</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="formData.hasGeneralExam === '1'" :span="16">
              <el-form-item label="一般检查描述">
                <el-input v-model="formData.generalExamDesc" placeholder="请输入一般检查具体情况" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 是否有第二性征 -->
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="是否有第二性征">
                <el-radio-group v-model="formData.hasSecondarySexual" :disabled="isViewMode">
                  <el-radio value="1">有</el-radio>
                  <el-radio value="0">无</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="formData.hasSecondarySexual === '1'" :span="8">
              <el-form-item label="出现日期">
                <el-date-picker v-model="formData.secondarySexualDate" type="date" placeholder="选择日期"
                  value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
          </el-row>
          <!-- 初次遗精/月经初潮 -->
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="初次遗精/月经初潮">
                <el-radio-group v-model="formData.hasFirstEjaculation" :disabled="isViewMode">
                  <el-radio value="1">有</el-radio>
                  <el-radio value="0">无</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col v-if="formData.hasFirstEjaculation === '1'" :span="8">
              <el-form-item label="发生时间">
                <el-date-picker v-model="formData.firstEjaculationDate" type="month" placeholder="选择年月"
                  value-format="YYYY-MM" style="width:100%" :disabled="isViewMode" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider />
          <div class="tab-section-header">
            <div class="tab-section-title" style="margin-bottom:0">家族史</div>
            <div class="tab-section-actions">
              <el-button-group v-if="!isViewMode" size="small" style="margin-right:8px">
                <el-button :type="familyViewMode === 'table' ? 'primary' : ''" @click="familyViewMode = 'table'">表格视图</el-button>
                <el-button :type="familyViewMode === 'pedigree' ? 'primary' : ''" @click="familyViewMode = 'pedigree'">谱图视图</el-button>
              </el-button-group>
              <template v-if="familyViewMode === 'table' && !isViewMode">
                <el-button size="small" @click="addFamilyCustomColumn">
                  <el-icon><Plus /></el-icon>新增列
                </el-button>
                <el-button type="primary" size="small" @click="addFamilyMember">
                  <el-icon><Plus /></el-icon>新增行
                </el-button>
              </template>
            </div>
          </div>
          <el-table v-if="familyViewMode === 'table'" :data="familyMembers" border stripe size="small" style="width:100%;margin-top:12px">
            <el-table-column prop="relationship" label="与患者关系" width="120">
              <template #default="{ row }">
                <el-select v-model="row.relationship" placeholder="请选择" size="small" :disabled="isViewMode">
                  <el-option v-for="r in familyRelations" :key="r" :label="r" :value="r" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="sex" label="性别" width="80">
              <template #default="{ row }">
                <el-select v-model="row.sex" size="small" :disabled="isViewMode">
                  <el-option label="男" value="1" />
                  <el-option label="女" value="2" />
                  <el-option label="未知" value="0" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="birthYear" label="出生年份" width="100">
              <template #default="{ row }">
                <el-input v-model="row.birthYear" size="small" :disabled="isViewMode" placeholder="如 2015" />
              </template>
            </el-table-column>
            <el-table-column prop="height" label="身高(cm)" width="100">
              <template #default="{ row }">
                <el-input v-model="row.height" size="small" :disabled="isViewMode" />
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="体重(kg)" width="100">
              <template #default="{ row }">
                <el-input v-model="row.weight" size="small" :disabled="isViewMode" />
              </template>
            </el-table-column>
            <el-table-column prop="isAffected" label="患病" width="80">
              <template #default="{ row }">
                <el-select v-model="row.isAffected" size="small" :disabled="isViewMode">
                  <el-option label="是" value="1" />
                  <el-option label="否" value="0" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="hasSimilarDisease" label="类似疾病" width="100">
              <template #default="{ row }">
                <el-select v-model="row.hasSimilarDisease" size="small" :disabled="isViewMode">
                  <el-option label="是" value="是" />
                  <el-option label="否" value="否" />
                  <el-option label="不详" value="不详" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column prop="linkedMedrecNum" label="关联病历号" width="130">
              <template #default="{ row }">
                <el-input v-model="row.linkedMedrecNum" size="small" :disabled="isViewMode" placeholder="病历号" />
              </template>
            </el-table-column>
            <el-table-column prop="notes" label="备注" min-width="120">
              <template #default="{ row }">
                <el-input v-model="row.notes" size="small" :disabled="isViewMode" />
              </template>
            </el-table-column>
            <el-table-column v-for="col in familyCustomColumns" :key="col" :label="col" min-width="120">
              <template #header>
                <div class="custom-col-header">
                  <span>{{ col }}</span>
                  <el-button v-if="!isViewMode" size="small" text type="danger" @click="removeFamilyCustomColumn(col)">
                    <el-icon><Close /></el-icon>
                  </el-button>
                </div>
              </template>
              <template #default="{ row }">
                <el-input v-model="row[col]" size="small" :disabled="isViewMode" />
              </template>
            </el-table-column>
            <el-table-column v-if="!isViewMode" label="操作" width="120" fixed="right">
              <template #default="{ row, $index }">
                <el-button type="primary" size="small" text @click="openLinkPatientDialog($index)">关联</el-button>
                <el-button type="danger" size="small" text @click="removeFamilyMember($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 谱图视图 -->
          <PedigreeChart
            v-if="familyViewMode === 'pedigree'"
            :family-members="familyMembers"
            :patient-sex="formData.sex"
            :patient-name="formData.name"
            :patient-birth-year="extractYearFromDateStr(formData.birthTime)"
            :patient-age="formData.age"
            :patient-height="formData.height"
            :patient-disease="getDisClassName(formData.disClass)"
            :patient-dis-class="formData.disClass"
          />

          <!-- 关联病历弹窗 -->
          <el-dialog v-model="linkDialogVisible" title="关联已有病历" width="480px" :close-on-click-modal="false">
            <el-form label-width="80px">
              <el-form-item label="病历号">
                <el-input v-model="linkDialogSearchMedrec" placeholder="请输入要关联的病历号" @keyup.enter="searchPatientForLink">
                  <template #append>
                    <el-button @click="searchPatientForLink" :loading="linkDialogSearching">搜索</el-button>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item v-if="linkDialogSearchResult" label="搜索结果">
                <el-card shadow="never" class="link-result-card">
                  <div class="link-result-info">
                    <span class="link-result-name">{{ linkDialogSearchResult.name }}</span>
                    <el-tag size="small" :type="linkDialogSearchResult.sex === '1' ? '' : 'danger'">
                      {{ linkDialogSearchResult.sex === '1' ? '男' : '女' }}
                    </el-tag>
                    <el-tag size="small" type="info">{{ linkDialogSearchResult.disClassName }}</el-tag>
                  </div>
                  <div class="link-result-detail">
                    病历号：{{ linkDialogSearchResult.medrecNum }}
                  </div>
                </el-card>
              </el-form-item>
            </el-form>
            <template #footer>
              <el-button @click="linkDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="confirmLinkPatient" :disabled="!linkDialogSearchResult">确认关联</el-button>
            </template>
          </el-dialog>

          <el-divider />
          <div class="tab-section-title">既往史</div>

          <!-- DSD/MAS 患者级既往史 -->
          <template v-if="['dsd','mas'].includes(diseaseType)">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="既往史">
                  <el-input v-model="formData.pastHis" type="textarea" :rows="3" placeholder="请输入既往史" :disabled="isViewMode" />
                </el-form-item>
              </el-col>
            </el-row>
          </template>

          <!-- FSS/SGA/SSS 结构化既往史 -->
          <template v-if="['fss','sga','sss'].includes(diseaseType)">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="运动发育落后">
                  <el-radio-group v-model="formData.diseaseData.motDevBack" :disabled="isViewMode">
                    <el-radio value="1">无</el-radio>
                    <el-radio value="2">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col v-if="formData.diseaseData.motDevBack === '2'" :span="6">
                <el-form-item><el-input v-model="formData.diseaseData.sport" placeholder="描述" :disabled="isViewMode" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="语言发育落后">
                  <el-radio-group v-model="formData.diseaseData.lanDevBack" :disabled="isViewMode">
                    <el-radio value="1">无</el-radio>
                    <el-radio value="2">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col v-if="formData.diseaseData.lanDevBack === '2'" :span="6">
                <el-form-item><el-input v-model="formData.diseaseData.language" placeholder="描述" :disabled="isViewMode" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="智力发育落后">
                  <el-radio-group v-model="formData.diseaseData.intDevBack" :disabled="isViewMode">
                    <el-radio value="1">无</el-radio>
                    <el-radio value="2">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col v-if="formData.diseaseData.intDevBack === '2'" :span="6">
                <el-form-item><el-input v-model="formData.diseaseData.intelligence" placeholder="描述" :disabled="isViewMode" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="听力异常">
                  <el-radio-group v-model="formData.diseaseData.abnHear" :disabled="isViewMode">
                    <el-radio value="1">无</el-radio>
                    <el-radio value="2">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col v-if="formData.diseaseData.abnHear === '2'" :span="6">
                <el-form-item><el-input v-model="formData.diseaseData.hear" placeholder="描述" :disabled="isViewMode" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="反复感染史">
                  <el-radio-group v-model="formData.diseaseData.recInfHis" :disabled="isViewMode">
                    <el-radio value="1">无</el-radio>
                    <el-radio value="2">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col v-if="formData.diseaseData.recInfHis === '2'" :span="6">
                <el-form-item><el-input v-model="formData.diseaseData.infection" placeholder="描述" :disabled="isViewMode" /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="抽搐史">
                  <el-radio-group v-model="formData.diseaseData.conHis" :disabled="isViewMode">
                    <el-radio value="1">无</el-radio>
                    <el-radio value="2">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="其他">
                  <el-input v-model="formData.diseaseData.pastOther" placeholder="请输入其他既往史" :disabled="isViewMode" />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 诊疗方案 -->
            <TreatmentPlanForm v-model="treatmentPlanData" :disease-type="diseaseType" :disabled="isViewMode" :weight="Number(formData.weight) || 0" />
          </template>

          <!-- CPP 简化既往史 -->
          <template v-if="diseaseType === 'cpp'">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="既往史">
                  <el-radio-group v-model="formData.diseaseData.isHis" :disabled="isViewMode">
                    <el-radio value="1">健康</el-radio>
                    <el-radio value="2">异常</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col v-if="formData.diseaseData.isHis === '2'" :span="24">
                <el-form-item label="异常描述">
                  <el-input v-model="formData.diseaseData.oldHis" type="textarea" :rows="3"
                    placeholder="请输入既往的疾病及治疗情况" maxlength="1500" show-word-limit :disabled="isViewMode" />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 诊疗方案 -->
            <TreatmentPlanForm v-model="treatmentPlanData" :disease-type="diseaseType" :disabled="isViewMode" :weight="Number(formData.weight) || 0" />
          </template>

          <!-- ELTM 既往用药史 -->
          <template v-if="diseaseType === 'eltm'">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="既往用药史">
                  <el-radio-group v-model="formData.diseaseData.hasHistory" :disabled="isViewMode">
                    <el-radio value="无">无</el-radio>
                    <el-radio value="有">有</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
          </template>
        </el-tab-pane>

        <!-- Tab 3：基线-辅助检查 -->
        <el-tab-pane label="基线-辅助检查" name="baselineExam" lazy>
          <BaselineExamForm v-model="formData.examData" v-model:disease-data="formData.diseaseData" :disabled="isViewMode" :disease-type="diseaseType" :patient-id="patientId" :patient-sex="formData.sex" />
        </el-tab-pane>

        <!-- Tab 4：遗传学检查 -->
        <el-tab-pane label="遗传学检查" name="genetics" lazy>
          <GeneticsExamForm v-model="formData.geneticsData" :disabled="isViewMode" :disease-type="diseaseType" :patient-id="patientId" />
        </el-tab-pane>

        <!-- Tab 5：随访记录 -->
        <el-tab-pane v-if="!isCreateMode" label="随访记录" name="followup" lazy>
          <div class="tab-pane-header">
            <el-button type="primary" size="small" @click="showFollowUpForm = true">
              <el-icon><Plus /></el-icon>新增随访
            </el-button>
          </div>
          <FollowUpList :patient-id="patientId" ref="followUpListRef" />
        </el-tab-pane>

        <!-- Tab 6：诊断 -->
        <el-tab-pane label="诊断" name="diagnosis">
          <DiagnosisForm v-model="formData.diagnosisData" :disabled="isViewMode" />
        </el-tab-pane>

        <!-- Tab 7：GH不良事件 -->
        <el-tab-pane v-if="!isCreateMode" label="GH不良事件" name="ghAdverseEvent" lazy>
          <div class="tab-pane-header">
            <el-button type="primary" size="small" @click="showGhAdverseEventForm = true">
              <el-icon><Plus /></el-icon>新增不良事件
            </el-button>
          </div>
          <GhAdverseEventList :patient-id="patientId" ref="ghAdverseEventListRef" @edit="handleGhAdverseEventEdit" />
        </el-tab-pane>

      </el-tabs>
    </el-form>

    <FollowUpForm v-model:visible="showFollowUpForm" :patient-id="patientId"
                  :disease-type="diseaseType" @saved="onFollowUpSaved" />
    <GhAdverseEventForm v-model:visible="showGhAdverseEventForm" :patient-id="patientId"
                        :edit-data="ghAdverseEventEditData" @saved="onGhAdverseEventSaved" />
  </div>
</template>


<script setup>
import { ref, reactive, computed, onMounted, defineAsyncComponent, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Close, ArrowDown } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getPatientDetail, createPatient, updatePatient, searchPatientByMedrec, reclassifyPatient, diseaseTypes, getDisClassByType, getDisClassName } from '@/api/patient'
import FollowUpForm from '@/views/case/components/FollowUpForm.vue'
import IcdAutocomplete from '@/views/case/components/IcdAutocomplete.vue'
import DiagnosisForm from '@/views/case/components/DiagnosisForm.vue'
import GhAdverseEventForm from '@/views/case/components/AdverseEventForm.vue'

// 重组件改为异步加载，仅在 Tab 激活时才下载对应代码
const FollowUpList = defineAsyncComponent(() => import('@/views/case/components/FollowUpList.vue'))
const BaselineExamForm = defineAsyncComponent(() => import('@/views/case/components/BaselineExamForm.vue'))
const PedigreeChart = defineAsyncComponent(() => import('@/views/case/components/PedigreeChart.vue'))
const TreatmentPlanForm = defineAsyncComponent(() => import('@/views/case/components/TreatmentPlanForm.vue'))
const GeneticsExamForm = defineAsyncComponent(() => import('@/views/case/components/GeneticsExamForm.vue'))
const GhAdverseEventList = defineAsyncComponent(() => import('@/views/case/components/AdverseEventList.vue'))
const DiseaseFormGeneric = defineAsyncComponent(() => import('@/views/case/components/DiseaseFormGeneric.vue'))

const route = useRoute()
const router = useRouter()
const formRef = ref(null)

const diseaseType = computed(() => route.params.type)
const patientId = computed(() => route.params.id || '')
const isCreateMode = computed(() => route.name === 'CaseCreate')
const isEditMode = computed(() => route.name === 'CaseEdit')
const isViewMode = computed(() => route.name === 'CaseView')

const diseaseName = computed(() => diseaseTypes[diseaseType.value]?.name || '')
const pageTitle = computed(() => {
  if (isCreateMode.value) return '新建 ' + diseaseName.value + ' 病例'
  if (isEditMode.value) return '编辑 ' + diseaseName.value + ' 病例'
  return diseaseName.value + ' 病例详情'
})

const hasDiseaseClinicalFields = computed(() => {
  const d = diseaseType.value
  return ['dsd','fss','cpp','mas','sss'].includes(d)
})

const tabList = computed(() => [
  { name: 'basic', label: '基本信息' },
  { name: 'baselineClinical', label: '基线-临床信息' },
  { name: 'baselineExam', label: '基线-辅助检查' },
  { name: 'genetics', label: '遗传学检查' },
  { name: 'followup', label: '随访记录' },
  { name: 'diagnosis', label: '诊断' },
  { name: 'ghAdverseEvent', label: 'GH不良事件' }
])

const activeTab = ref('basic')

// 重分类可选目标病种（排除ELTM自身）
const reclassifyTargets = computed(() => {
  return Object.entries(diseaseTypes)
    .filter(([key]) => key !== 'eltm')
    .map(([key, val]) => ({ key, ...val }))
})

// 仅ELTM未分类患者可重新分类
const showReclassify = computed(() => {
  return isViewMode.value && formData.disClass === '10000007'
})

const reclassifying = ref(false)

async function handleReclassifyConfirm(targetDisClass) {
  const target = reclassifyTargets.value.find(t => t.code === targetDisClass)
  if (!target) return
  try {
    await ElMessageBox.confirm(
      `确定将当前病例重新分类为「${target.name}」吗？系统将自动生成新的病例编号。`,
      '确认重新分类',
      { confirmButtonText: '确定迁移', cancelButtonText: '取消', type: 'warning' }
    )
  } catch { return }

  reclassifying.value = true
  try {
    const res = await reclassifyPatient(patientId.value, targetDisClass)
    ElMessage.success(`重新分类成功，新病例编号：${res.data?.caseNum || ''}`)
    router.replace(`/case/${target.key}/${patientId.value}`)
  } catch (error) {
    ElMessage.error('重新分类失败')
  } finally {
    reclassifying.value = false
  }
}

function prevTab() {
  const idx = tabList.value.findIndex(t => t.name === activeTab.value)
  if (idx > 0) activeTab.value = tabList.value[idx - 1].name
}
function nextTab() {
  const idx = tabList.value.findIndex(t => t.name === activeTab.value)
  if (idx < tabList.value.length - 1) activeTab.value = tabList.value[idx + 1].name
}

function validateIdCard(_rule, value, callback) {
  if (!value) return callback()
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if (!reg.test(value)) return callback(new Error('身份证号码格式不正确'))
  callback()
}

function validatePhone(_rule, value, callback) {
  if (!value) return callback()
  const reg = /^(1[3-9]\d{9}|0\d{2,3}-?\d{7,8})$/
  if (!reg.test(value)) return callback(new Error('联系电话格式不正确'))
  callback()
}

function extractBirthYearFromIdCard(card) {
  if (!card) return null
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  if (!reg.test(card)) return null
  if (card.length === 18) {
    const year = parseInt(card.substring(6, 10), 10)
    if (isNaN(year) || year < 1900 || year > 2100) return null
    return year
  }
  if (card.length === 15) {
    const year = 1900 + parseInt(card.substring(6, 8), 10)
    if (isNaN(year) || year < 1900 || year > 1999) return null
    return year
  }
  return null
}

function extractYearFromDateStr(dateStr) {
  if (!dateStr || typeof dateStr !== 'string') return null
  const match = dateStr.match(/^(\d{4})/)
  if (!match) return null
  const year = parseInt(match[1], 10)
  if (isNaN(year) || year < 1900 || year > 2100) return null
  return year
}

const FAMILY_STANDARD_KEYS = ['relationship', 'sex', 'birthYear', 'height', 'weight', 'isAffected', 'isDeceased', 'generation', 'hasSimilarDisease', 'notes', 'linkedPatientId', 'linkedMedrecNum', 'linkedDiseaseType']
const familyRelations = ['父亲', '母亲', '兄', '弟', '姐', '妹', '祖父', '祖母', '外祖父', '外祖母', '其他']
const familyMembers = reactive([])
const familyViewMode = ref('table')  // 'table' | 'pedigree'
const familyCustomColumns = ref([])

// 诊疗方案数据
const treatmentPlanData = reactive({
  diaPlan: '1', rhGH: '', rhGHdose: '', rhGHdoseKG: '',
  PEGrhGHdose: '', PEGrhGHdoseKG: '',
  GnRHa: '', GnRHadose: '',
  planData: [], otherMedicine: '',
  rhCustomizationDiaPlan: '', rhCustomizationPrompt: '',
  PEGrhCustomizationPrompt: '', rhCustomizationPromptKG: '',
  PEGrhCustomizationPromptKG: ''
})

function addFamilyMember() {
  const member = {
    relationship: '',
    sex: '1',
    birthYear: '',
    height: '',
    weight: '',
    isAffected: '0',
    isDeceased: '0',
    generation: 0,
    hasSimilarDisease: '否',
    notes: '',
    linkedPatientId: null,
    linkedMedrecNum: '',
    linkedDiseaseType: ''
  }
  familyCustomColumns.value.forEach(col => { member[col] = '' })
  familyMembers.push(member)
}

function removeFamilyMember(index) {
  familyMembers.splice(index, 1)
}

// 关联病历弹窗状态
const linkDialogVisible = ref(false)
const linkDialogSearchMedrec = ref('')
const linkDialogSearchResult = ref(null)
const linkDialogSearching = ref(false)
const linkDialogTargetIndex = ref(-1)

function openLinkPatientDialog(index) {
  linkDialogTargetIndex.value = index
  linkDialogSearchMedrec.value = familyMembers[index].linkedMedrecNum || ''
  linkDialogSearchResult.value = null
  linkDialogVisible.value = true
}

async function searchPatientForLink() {
  if (!linkDialogSearchMedrec.value.trim()) {
    ElMessage.warning('请输入病历号')
    return
  }
  linkDialogSearching.value = true
  try {
    const res = await searchPatientByMedrec(linkDialogSearchMedrec.value.trim())
    if (res.code === 200 && res.data) {
      linkDialogSearchResult.value = res.data
    } else {
      linkDialogSearchResult.value = null
      ElMessage.info('未找到匹配的患者')
    }
  } catch {
    linkDialogSearchResult.value = null
    ElMessage.error('搜索失败')
  } finally {
    linkDialogSearching.value = false
  }
}

function confirmLinkPatient() {
  if (!linkDialogSearchResult.value) return
  const idx = linkDialogTargetIndex.value
  const member = familyMembers[idx]
  member.linkedPatientId = linkDialogSearchResult.value.id
  member.linkedMedrecNum = linkDialogSearchResult.value.medrecNum
  // 从 disClass 反查 diseaseType（用于谱图跳转）
  const dtEntry = Object.entries(diseaseTypes).find(([_, v]) => v.code === linkDialogSearchResult.value.disClass)
  member.linkedDiseaseType = dtEntry ? dtEntry[0] : ''
  // 如果未填关系，尝试从搜索结果推断
  if (!member.relationship) {
    member.relationship = '其他'
  }
  linkDialogVisible.value = false
  ElMessage.success('已关联病历')
}

function addFamilyCustomColumn() {
  ElMessageBox.prompt('请输入新列名称', '新增自定义列', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S+/,
    inputErrorMessage: '列名不能为空'
  }).then(({ value }) => {
    const colName = value.trim()
    if (familyCustomColumns.value.includes(colName)) {
      ElMessage.warning('该列已存在')
      return
    }
    familyCustomColumns.value.push(colName)
    familyMembers.forEach(member => { member[colName] = '' })
  }).catch(() => {})
}

function removeFamilyCustomColumn(col) {
  familyCustomColumns.value = familyCustomColumns.value.filter(c => c !== col)
  familyMembers.forEach(member => { delete member[col] })
}

const formData = reactive({
  disClass: '', caseNum: '', medrecNum: '', name: '', sex: '', birthTime: '',
  relation: '', selfTel: '', chiCom: '', ethnic: '',
  card: '', famAdr: '', contactsName: '', categoryDescribe: '',
  icd: '',
  height: '', weight: '',
  fht: '', mht: '', fhw: '', mhw: '', menAge: '', isBot: '', familyHis: '',
  pastHis: '', pastTime: '', pastHeight: '', pastWeight: '',
  firVisTime: '', firVisAge: '', growRate: '', rate: '',
  hasGeneralExam: '', generalExamDesc: '', firstEjaculation: '',
  hasFirstEjaculation: '', firstEjaculationDate: '',
  hasSecondarySexual: '', secondarySexualDate: '',
  gesWeek: '', bwt: '', bl: '', cesaSec: '', cesaAsphyxia: '',
  genStag: '', pubStag: '', heightSds: '', weightSds: '', lowerMeasure: '',
  armLength: '',
  specialFace: '', specialFaceDesc: '',
  scoliosis: '', scoliosisDesc: '',
  rash: '', rashDesc: '',
  breastDevLeft: '', breastDevRight: '',
  examData: {},
  geneticsData: {},
  diagnosisData: {},
  diseaseData: {
    motDevBack: '1', sport: '',
    lanDevBack: '1', language: '',
    intDevBack: '1', intelligence: '',
    abnHear: '1', hear: '',
    recInfHis: '1', infection: '',
    conHis: '1', pastOther: ''
  },
  auditStatus: '',
  auditRemark: ''
})

const saving = ref(false)
const bmiValue = ref('')
const showFollowUpForm = ref(false)
const followUpListRef = ref(null)
const showGhAdverseEventForm = ref(false)
const ghAdverseEventListRef = ref(null)
const ghAdverseEventEditData = ref(null)

onMounted(() => {
  formData.disClass = getDisClassByType(diseaseType.value)
  if (patientId.value && !isCreateMode.value) loadDetail()
})

// 自动计算初诊年龄：year(firVisTime) - birthYearFromIdCard(card)
watch(
  () => [formData.card, formData.firVisTime],
  ([card, firVisTime]) => {
    if (!firVisTime) {
      formData.firVisAge = ''
      return
    }
    const birthYear = extractBirthYearFromIdCard(card)
    const visitYear = extractYearFromDateStr(firVisTime)
    if (birthYear !== null && visitYear !== null) {
      const age = visitYear - birthYear
      formData.firVisAge = age >= 0 ? String(age) : ''
    } else {
      formData.firVisAge = ''
    }
  }
)

// 哪些字段属于哪个子对象（后端平铺 → 前端嵌套的映射）
const EXAM_FIELDS = ['lh','lhCheckDate','fsh','fshCheckDate','e2','e2CheckDate','t','tCheckDate','prl','prlCheckDate','dht','dhtCheckDate','ft','ftCheckDate','shbg','shbgCheckDate','amh','amhCheckDate','inhb','inhbCheckDate','igf1','igf1CheckDate','igfbp3','igfbp3CheckDate','fasBloodGlu','fasBloodGluCheckDate','fasInsulin','fasInsulinCheckDate','glyHem','glyHemCheckDate','acth','acthCheckDate','cortisol','cortisolCheckDate','ohp','ohpCheckDate','dheas','dheasCheckDate','androstenedione','androstenedioneCheckDate','hcg','hcgCheckDate','hcgt','hcgtCheckDate','hcgdht','hcgdhtCheckDate','hcgad','hcgadCheckDate','lhMax','lhMaxCheckDate','fshMax','fshMaxCheckDate','tsh','tshCheckDate','ft3','ft3CheckDate','ft4','ft4CheckDate','tpoab','tpoabCheckDate','tgab','tgabCheckDate','thyroidFunction','gonBUlt','pituitaryMri','thyroidUlt','bonMinDen']
const GENETICS_FIELDS = ['karyotype','biologBank','biologBankFa','biologBankMo','surgeryNote','pathologyResult']
const DIAGNOSIS_FIELDS = ['diagnosis','secondaryDiagnosis','treatmentPlan','isTreated']

async function loadDetail() {
  try {
    const res = await getPatientDetail(patientId.value)
    const data = res.data
    Object.keys(formData).forEach(key => {
      if (['examData','geneticsData','diagnosisData','diseaseData'].includes(key)) return
      if (data[key] !== undefined && data[key] !== null) formData[key] = data[key]
    })
    // 将后端平铺字段映射到前端嵌套子对象
    EXAM_FIELDS.forEach(f => { if (data[f] !== undefined && data[f] !== null) formData.examData[f] = data[f] })
    GENETICS_FIELDS.forEach(f => { if (data[f] !== undefined && data[f] !== null) formData.geneticsData[f] = data[f] })
    DIAGNOSIS_FIELDS.forEach(f => { if (data[f] !== undefined && data[f] !== null) formData.diagnosisData[f] = data[f] })
    // diagnosisData.diagnosis 也映射到 diseaseData（兼容从 diseaseData 读取的旧逻辑）
    if (data.diagnosis !== undefined && data.diagnosis !== null) formData.diseaseData.diagnosis = data.diagnosis
    if (data.treatmentPlan !== undefined && data.treatmentPlan !== null) formData.diseaseData.treatmentPlan = data.treatmentPlan
    if (data.diseaseData) {
      // 过滤 null 值，避免覆盖表单默认值
      const filtered = {}
      Object.keys(data.diseaseData).forEach(k => {
        if (data.diseaseData[k] !== null) filtered[k] = data.diseaseData[k]
      })
      Object.assign(formData.diseaseData, filtered)
    }
    // 基因检测数组（后端存为JSON字符串，需解析）
    if (data.genData && typeof data.genData === 'string') {
      try { formData.geneticsData.genData = JSON.parse(data.genData) } catch {}
    } else if (data.genData) {
      formData.geneticsData.genData = data.genData
    }
    // MAS/ELTM diseaseData 字段路由到 geneticsData
    if (data.diseaseData) {
      const masKeys = ['gnas','gnasSamLoc','genTesMet','detRes','detVer','mutSit']
      const eltmKeys = ['geneMethod','geneRes','geneName','genePoint','geneType','geneMode','chrom','chromOther']
      masKeys.forEach(k => { if (data.diseaseData[k] !== undefined && data.diseaseData[k] !== null) formData.geneticsData[k] = data.diseaseData[k] })
      eltmKeys.forEach(k => {
        if (data.diseaseData[k] !== undefined && data.diseaseData[k] !== null) {
          // chrom 可能是 JSON 字符串，需要解析
          if (k === 'chrom' && typeof data.diseaseData[k] === 'string') {
            try { formData.geneticsData[k] = JSON.parse(data.diseaseData[k]) } catch { formData.geneticsData[k] = data.diseaseData[k] }
          } else {
            formData.geneticsData[k] = data.diseaseData[k]
          }
        }
      })
    }
    // 诊疗方案反序列化
    if (data.diseaseData?.diaTreaPlan && typeof data.diseaseData.diaTreaPlan === 'string') {
      try {
        Object.assign(treatmentPlanData, JSON.parse(data.diseaseData.diaTreaPlan))
      } catch {}
    }
    // 家族成员 JSON 解析
    familyCustomColumns.value = []
    if (data.familyMembers) {
      try {
        const parsed = JSON.parse(data.familyMembers)
        // 向后兼容：旧版 JSON 缺少新字段时自动补默认值
        const normalized = parsed.map(row => ({
          relationship: row.relationship || '',
          sex: row.sex || (row.relationship === '父亲' || row.relationship === '兄' || row.relationship === '弟' || row.relationship === '祖父' || row.relationship === '外祖父' ? '1' : row.relationship === '母亲' || row.relationship === '姐' || row.relationship === '妹' || row.relationship === '祖母' || row.relationship === '外祖母' ? '2' : '0'),
          birthYear: row.birthYear || '',
          height: row.height || '',
          weight: row.weight || '',
          isAffected: row.isAffected || (row.hasSimilarDisease === '是' ? '1' : '0'),
          isDeceased: row.isDeceased || '0',
          generation: row.generation ?? 0,
          hasSimilarDisease: row.hasSimilarDisease || '否',
          notes: row.notes || '',
          linkedPatientId: row.linkedPatientId || null,
          linkedMedrecNum: row.linkedMedrecNum || '',
          linkedDiseaseType: row.linkedDiseaseType || ''
        }))
        familyMembers.splice(0, familyMembers.length, ...normalized)
        // 检测自定义列
        const customKeys = new Set()
        normalized.forEach(row => {
          Object.keys(row).forEach(k => {
            if (!FAMILY_STANDARD_KEYS.includes(k)) customKeys.add(k)
          })
        })
        familyCustomColumns.value = Array.from(customKeys)
      } catch {}
    } else {
      familyMembers.splice(0, familyMembers.length)
    }
    calcBmi()
  } catch (error) {
    console.error('加载病例详情失败', error)
    ElMessage.error('加载病例详情失败')
  }
}

function calcBmi() {
  if (formData.height && formData.weight) {
    try {
      const h = parseFloat(formData.height) / 100
      const w = parseFloat(formData.weight)
      if (h > 0 && w > 0) { bmiValue.value = (w / (h * h)).toFixed(1) }
    } catch (e) { bmiValue.value = '' }
  } else { bmiValue.value = '' }
}

const growthRateOptions = computed(() => {
  const opts = []
  for (let v = 0.5; v <= 30; v += 0.5) opts.push(v)
  return opts
})

function switchToEdit() {
  router.push('/case/' + diseaseType.value + '/' + patientId.value + '/edit')
}

const FIELD_TAB_MAP = {
  height: 'baselineClinical', weight: 'baselineClinical',
  firVisTime: 'baselineClinical', firVisAge: 'baselineClinical',
  growRate: 'baselineClinical', rate: 'baselineClinical',
  genStag: 'baselineClinical', pubStag: 'baselineClinical',
  heightSds: 'baselineClinical', weightSds: 'baselineClinical', lowerMeasure: 'baselineClinical',
  armLength: 'baselineClinical',
  specialFace: 'baselineClinical', specialFaceDesc: 'baselineClinical',
  scoliosis: 'baselineClinical', scoliosisDesc: 'baselineClinical',
  rash: 'baselineClinical', rashDesc: 'baselineClinical',
  breastDevLeft: 'baselineClinical', breastDevRight: 'baselineClinical',
  fht: 'baselineClinical', mht: 'baselineClinical', fhw: 'baselineClinical', mhw: 'baselineClinical',
  menAge: 'baselineClinical', isBot: 'baselineClinical', familyHis: 'baselineClinical',
  pastHis: 'baselineClinical', firVisAge: 'baselineClinical',
  chiCom: 'baselineClinical', hasGeneralExam: 'baselineClinical', generalExamDesc: 'baselineClinical',
  firstEjaculation: 'baselineClinical', hasFirstEjaculation: 'baselineClinical', firstEjaculationDate: 'baselineClinical',
  hasSecondarySexual: 'baselineClinical', secondarySexualDate: 'baselineClinical'
}

function resolveTabByField(field) {
  return FIELD_TAB_MAP[field] || 'basic'
}

async function handleSave() {
  let validationFields = null
  try { await formRef.value?.validate() }
  catch (fields) {
    validationFields = fields
    ElMessage.warning('请完善必填信息')
    if (fields && typeof fields === 'object') {
      const firstErrorField = Object.keys(fields)[0]
      activeTab.value = firstErrorField ? resolveTabByField(firstErrorField) : 'basic'
    } else { activeTab.value = 'basic' }
    return
  }

  saving.value = true
  try {
    // 构造请求体：将嵌套子对象平铺到顶层（后端 PatientDTO 为平铺字段）
    const payload = { ...formData }
    if (payload.examData) { Object.assign(payload, payload.examData); delete payload.examData }
    if (payload.geneticsData) {
      const g = payload.geneticsData
      Object.assign(payload, g)
      // genData 数组序列化为 JSON 字符串
      if (g.genData && Array.isArray(g.genData)) payload.genData = JSON.stringify(g.genData)
      // MAS 遗传学字段路由到 diseaseData
      if (diseaseType.value === 'mas') {
        const masFields = ['gnas','gnasSamLoc','genTesMet','detRes','detVer','mutSit']
        const masData = {}
        masFields.forEach(k => { if (g[k] !== undefined) masData[k] = g[k] })
        if (Object.keys(masData).length > 0) {
          payload.diseaseData = { ...(payload.diseaseData || {}), ...masData }
        }
      }
      // ELTM 遗传学字段路由到 diseaseData
      if (diseaseType.value === 'eltm') {
        const eltmFields = ['geneMethod','geneRes','geneName','genePoint','geneType','geneMode','chrom','chromOther']
        const eltmData = {}
        eltmFields.forEach(k => {
          if (g[k] !== undefined && g[k] !== null) {
            // chrom 数组序列化
            eltmData[k] = (k === 'chrom' && Array.isArray(g[k])) ? JSON.stringify(g[k]) : g[k]
          }
        })
        if (Object.keys(eltmData).length > 0) {
          payload.diseaseData = { ...(payload.diseaseData || {}), ...eltmData }
        }
      }
      delete payload.geneticsData
    }
    if (payload.diagnosisData) { Object.assign(payload, payload.diagnosisData); delete payload.diagnosisData }
    // diseaseData 保持不变（后端已有 Map<String,Object> diseaseData 接收）
    // 诊疗方案序列化到 diseaseData
    if (['fss','sga','sss','cpp'].includes(diseaseType.value)) {
      payload.diseaseData = { ...(payload.diseaseData || {}), diaTreaPlan: JSON.stringify(treatmentPlanData) }
    }
    // 家族成员序列化为 JSON 字符串
    payload.familyMembers = familyMembers.length > 0 ? JSON.stringify(familyMembers) : ''

    if (isCreateMode.value) {
      const res = await createPatient(payload)
      ElMessage.success('新建成功，病例编号：' + (res.data?.caseNum || ''))
      router.replace('/case/' + diseaseType.value)
    } else {
      await updatePatient(patientId.value, payload)
      ElMessage.success('保存成功')
      router.replace('/case/' + diseaseType.value + '/' + patientId.value)
    }
  } catch (error) {
    console.error('保存失败', error)
  } finally { saving.value = false }
}

function onFollowUpSaved() {
  followUpListRef.value?.refresh()
}

function handleGhAdverseEventEdit(item) {
  ghAdverseEventEditData.value = item
  showGhAdverseEventForm.value = true
}

function onGhAdverseEventSaved() {
  ghAdverseEventEditData.value = null
  ghAdverseEventListRef.value?.refresh()
}
</script>

<style scoped>
.case-detail { padding: 0; }
.detail-tabs { border-radius: 4px; }
.detail-tabs :deep(.el-tabs__header) { margin-bottom: 0; }
.detail-tabs :deep(.el-tabs__content) { padding: 24px 20px; min-height: 400px; }
.tab-section-title {
  font-size: 15px; font-weight: 600; color: #303133;
  margin-bottom: 20px; padding-left: 10px; border-left: 3px solid #409eff;
}
.tab-section-header {
  display: flex; justify-content: space-between; align-items: center;
}
.tab-section-actions {
  display: flex; gap: 8px;
}
.tab-pane-header { display: flex; justify-content: flex-end; margin-bottom: 16px; }
.custom-col-header {
  display: flex; align-items: center; justify-content: space-between;
  width: 100%;
}
.el-divider { margin: 24px 0; }
</style>
