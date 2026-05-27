<template>
  <div class="case-detail">
    <PageHeader :title="pageTitle">
      <template #actions>
        <el-button @click="router.back()">返回列表</el-button>
        <el-button v-if="isViewMode" type="primary" @click="switchToEdit">编辑</el-button>
        <template v-if="!isViewMode">
          <el-button @click="prevTab" :disabled="activeTab === tabList[0].name">上一步</el-button>
          <el-button @click="nextTab" :disabled="activeTab === tabList[tabList.length - 1].name">下一步</el-button>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </template>
      </template>
    </PageHeader>

    <el-form ref="formRef" :model="formData" :disabled="isViewMode" label-width="120px">
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
            <el-col :span="6">
              <el-form-item label="R型骨龄"><el-input v-model="formData.rboneAge" placeholder="岁"><template #suffix>岁</template></el-input></el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="C型骨龄"><el-input v-model="formData.cboneAge" placeholder="岁"><template #suffix>岁</template></el-input></el-form-item>
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
            <el-col :span="6">
              <el-form-item label="生殖器发育(Tanner)">
                <el-select v-model="formData.genStag" placeholder="请选择" clearable>
                  <el-option v-for="i in 5" :key="i" :label="'I'.repeat(i)" :value="String(i)" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="阴毛发育(Tanner)">
                <el-select v-model="formData.pubStag" placeholder="请选择" clearable>
                  <el-option v-for="i in 5" :key="i" :label="'I'.repeat(i)" :value="String(i)" />
                </el-select>
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
                <el-input v-model="formData.firVisAge" placeholder="岁" :disabled="isViewMode" />
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
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="是否有一般检查">
                <el-radio-group v-model="formData.hasGeneralExam" :disabled="isViewMode">
                  <el-radio value="1">有</el-radio>
                  <el-radio value="0">无</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="初次遗精">
                <el-input v-model="formData.firstEjaculation" placeholder="请输入初次遗精" :disabled="isViewMode" />
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

          <el-divider />
          <div class="tab-section-title">家族史</div>
          <el-row :gutter="20">
            <el-col :span="6"><el-form-item label="父亲身高"><el-input v-model="formData.fht" placeholder="cm"><template #suffix>cm</template></el-input></el-form-item></el-col>
            <el-col :span="6"><el-form-item label="母亲身高"><el-input v-model="formData.mht" placeholder="cm"><template #suffix>cm</template></el-input></el-form-item></el-col>
            <el-col :span="6"><el-form-item label="父亲体重"><el-input v-model="formData.fhw" placeholder="kg"><template #suffix>kg</template></el-input></el-form-item></el-col>
            <el-col :span="6"><el-form-item label="母亲体重"><el-input v-model="formData.mhw" placeholder="kg"><template #suffix>kg</template></el-input></el-form-item></el-col>
            <el-col :span="6"><el-form-item label="初潮年龄"><el-input v-model="formData.menAge" placeholder="岁" /></el-form-item></el-col>
            <el-col :span="6">
              <el-form-item label="有无兄弟姐妹">
                <el-select v-model="formData.isBot" placeholder="请选择">
                  <el-option label="有" value="1" /><el-option label="无" value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24"><el-form-item label="家族史"><el-input v-model="formData.familyHis" type="textarea" :rows="3" placeholder="请输入家族史" /></el-form-item></el-col>
          </el-row>

          <el-divider />
          <div class="tab-section-title">既往史</div>

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
          </template>

          <!-- DSD/MAS 简单既往史 -->
          <template v-if="['dsd','mas'].includes(diseaseType)">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="既往史">
                  <el-input v-model="formData.pastHis" type="textarea" :rows="3" placeholder="请输入既往史" :disabled="isViewMode" />
                </el-form-item>
              </el-col>
            </el-row>
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
          <BaselineExamForm v-model="formData.examData" v-model:disease-data="formData.diseaseData" :disabled="isViewMode" :disease-type="diseaseType" :patient-id="patientId" />
        </el-tab-pane>

        <!-- Tab 4：遗传学检查 -->
        <el-tab-pane label="遗传学检查" name="genetics" lazy>
          <GeneticsExamForm v-model="formData.geneticsData" :disabled="isViewMode" :disease-type="diseaseType" />
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

      </el-tabs>
    </el-form>

    <FollowUpForm v-model:visible="showFollowUpForm" :patient-id="patientId"
                  :disease-type="diseaseType" @saved="onFollowUpSaved" />
  </div>
</template>


<script setup>
import { ref, reactive, computed, onMounted, defineAsyncComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import { getPatientDetail, createPatient, updatePatient, diseaseTypes, getDisClassByType } from '@/api/patient'
import FollowUpList from '@/views/case/components/FollowUpList.vue'
import FollowUpForm from '@/views/case/components/FollowUpForm.vue'
import IcdAutocomplete from '@/views/case/components/IcdAutocomplete.vue'
import BaselineExamForm from '@/views/case/components/BaselineExamForm.vue'
import GeneticsExamForm from '@/views/case/components/GeneticsExamForm.vue'
import DiagnosisForm from '@/views/case/components/DiagnosisForm.vue'

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
  return ['dsd','fss','cpp','mas','sga','sss','eltm'].includes(d)
})

const tabList = computed(() => [
  { name: 'basic', label: '基本信息' },
  { name: 'baselineClinical', label: '基线-临床信息' },
  { name: 'baselineExam', label: '基线-辅助检查' },
  { name: 'genetics', label: '遗传学检查' },
  { name: 'followup', label: '随访记录' },
  { name: 'diagnosis', label: '诊断' }
])

const activeTab = ref('basic')

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

const formData = reactive({
  disClass: '', caseNum: '', medrecNum: '', name: '', sex: '', birthTime: '',
  relation: '', selfTel: '', chiCom: '', ethnic: '',
  card: '', famAdr: '', contactsName: '', categoryDescribe: '',
  icd: '',
  height: '', weight: '', rboneAge: '', cboneAge: '',
  fht: '', mht: '', fhw: '', mhw: '', menAge: '', isBot: '', familyHis: '',
  pastHis: '', firVisAge: '', growRate: '', rate: '',
  hasGeneralExam: '', firstEjaculation: '',
  hasSecondarySexual: '', secondarySexualDate: '',
  gesWeek: '', bwt: '', bl: '', cesaSec: '', cesaAsphyxia: '',
  genStag: '', pubStag: '', heightSds: '', weightSds: '', lowerMeasure: '',
  examData: {},
  geneticsData: {},
  diagnosisData: {},
  diseaseData: {}
})

const saving = ref(false)
const bmiValue = ref('')
const showFollowUpForm = ref(false)
const followUpListRef = ref(null)

onMounted(() => {
  formData.disClass = getDisClassByType(diseaseType.value)
  // FSS/SGA/SSS 结构化既往史默认值（无=1）
  if (['fss','sga','sss'].includes(diseaseType.value)) {
    const defaults = {
      motDevBack: '1', sport: '',
      lanDevBack: '1', language: '',
      intDevBack: '1', intelligence: '',
      abnHear: '1', hear: '',
      recInfHis: '1', infection: '',
      conHis: '1', pastOther: ''
    }
    Object.keys(defaults).forEach(k => {
      if (!(k in formData.diseaseData)) formData.diseaseData[k] = defaults[k]
    })
  }
  if (patientId.value && !isCreateMode.value) loadDetail()
})

// 哪些字段属于哪个子对象（后端平铺 → 前端嵌套的映射）
const EXAM_FIELDS = ['lh','fsh','e2','t','prl','dht','ft','shbg','amh','inhb','igf1','igfbp3','fasBloodGlu','fasInsulin','glyHem','acth','cortisol','ohp','dheas','androstenedione','hcg','hcgt','hcgdht','hcgad','lhMax','fshMax','tsh','ft3','ft4','tpoab','tgab','gonBUlt','pituitaryMri','thyroidUlt','bonMinDen']
const GENETICS_FIELDS = ['karyotype','biologBank','biologBankFa','biologBankMo']
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
    if (data.diseaseData) Object.assign(formData.diseaseData, data.diseaseData)
    // 基因检测数组（后端存为JSON字符串，需解析）
    if (data.genData && typeof data.genData === 'string') {
      try { formData.geneticsData.genData = JSON.parse(data.genData) } catch {}
    } else if (data.genData) {
      formData.geneticsData.genData = data.genData
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
  rboneAge: 'baselineClinical', cboneAge: 'baselineClinical',
  firVisTime: 'baselineClinical', firVisAge: 'baselineClinical',
  growRate: 'baselineClinical', rate: 'baselineClinical',
  genStag: 'baselineClinical', pubStag: 'baselineClinical',
  heightSds: 'baselineClinical', weightSds: 'baselineClinical', lowerMeasure: 'baselineClinical',
  fht: 'baselineClinical', mht: 'baselineClinical', fhw: 'baselineClinical', mhw: 'baselineClinical',
  menAge: 'baselineClinical', isBot: 'baselineClinical', familyHis: 'baselineClinical',
  pastHis: 'baselineClinical', firVisAge: 'baselineClinical',
  chiCom: 'baselineClinical', hasGeneralExam: 'baselineClinical', firstEjaculation: 'baselineClinical',
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
      delete payload.geneticsData
    }
    if (payload.diagnosisData) { Object.assign(payload, payload.diagnosisData); delete payload.diagnosisData }
    // diseaseData 保持不变（后端已有 Map<String,Object> diseaseData 接收）

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
.tab-pane-header { display: flex; justify-content: flex-end; margin-bottom: 16px; }
.el-divider { margin: 24px 0; }
</style>
