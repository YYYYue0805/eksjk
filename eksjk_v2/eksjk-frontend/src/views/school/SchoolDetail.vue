<template>
  <div class="school-detail">
    <PageHeader :title="pageTitle">
      <template #actions>
        <el-button @click="router.back()">返回列表</el-button>
        <el-button v-if="isViewMode" type="primary" @click="switchToEdit">编辑</el-button>
        <el-button v-if="isCreateMode || isEditMode" type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </PageHeader>

    <!-- 基本信息表单 -->
    <el-card shadow="never" class="form-section">
      <template #header><span class="section-title">基本信息</span></template>
      <el-form ref="formRef" :model="formData" :disabled="isViewMode" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="编号" prop="num" :rules="[{ required: true, message: '请输入编号' }]">
              <el-input v-model="formData.num" placeholder="请输入编号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="班级">
              <el-input v-model="formData.sclass" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="姓名" prop="name" :rules="[{ required: true, message: '请输入姓名' }]">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="性别">
              <el-select v-model="formData.sex" placeholder="请选择">
                <el-option label="男" value="1" />
                <el-option label="女" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出生日期">
              <el-date-picker v-model="formData.birthTime" type="date" placeholder="选择日期"
                              value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手机号">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身高">
              <el-input v-model="formData.height" placeholder="cm"><template #suffix>cm</template></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体重">
              <el-input v-model="formData.weight" placeholder="kg"><template #suffix>kg</template></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="与孩子关系">
              <el-input v-model="formData.hhzgx" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 问卷标签页（仅查看/编辑模式） -->
    <el-card shadow="never" class="form-section" v-if="studentId">
      <template #header><span class="section-title">评估问卷</span></template>
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane v-for="(config, key) in questionnaireTypes" :key="key" :name="key">
          <template #label>
            <span>{{ config.short }}</span>
            <el-tag v-if="questionnaireStatus[key]" type="success" size="small" class="tab-tag">已填</el-tag>
            <el-tag v-else type="info" size="small" class="tab-tag">未填</el-tag>
          </template>
          <QuestionnaireForm
            :type="key"
            :data="questionnaires[key] || {}"
            :disabled="isViewMode"
            :student-id="studentId"
            @saved="onQuestionnaireSaved"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getStudentDetail, createStudent, updateStudent, questionnaireTypes } from '@/api/school'
import QuestionnaireForm from '@/views/school/components/QuestionnaireForm.vue'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)

const studentId = computed(() => route.params.id || '')
const isCreateMode = computed(() => route.name === 'SchoolCreate')
const isEditMode = computed(() => route.name === 'SchoolEdit')
const isViewMode = computed(() => route.name === 'SchoolView')

const pageTitle = computed(() => {
  if (isCreateMode.value) return '新增学生'
  if (isEditMode.value) return '编辑学生'
  return '学生详情'
})

const formData = reactive({
  num: '', sclass: '', name: '', sex: '', birthTime: '',
  phone: '', hhzgx: '', height: '', weight: ''
})

const saving = ref(false)
const activeTab = ref('cchkn')
const questionnaireStatus = ref({})
const questionnaires = ref({})

onMounted(() => {
  if (studentId.value && !isCreateMode.value) {
    loadDetail()
  }
})

async function loadDetail() {
  try {
    const res = await getStudentDetail(studentId.value)
    const data = res.data
    Object.keys(formData).forEach(key => {
      if (data[key] !== undefined && data[key] !== null) {
        formData[key] = data[key]
      }
    })
    questionnaireStatus.value = data.questionnaireStatus || {}
    questionnaires.value = data.questionnaires || {}
  } catch (error) {
    console.error('加载学生详情失败', error)
    ElMessage.error('加载学生详情失败')
  }
}

function switchToEdit() {
  router.push(`/school/${studentId.value}/edit`)
}

async function handleSave() {
  try {
    await formRef.value?.validate()
  } catch {
    ElMessage.warning('请完善必填信息')
    return
  }

  saving.value = true
  try {
    if (isCreateMode.value) {
      await createStudent(formData)
      ElMessage.success('新增成功')
      router.replace('/school')
    } else {
      await updateStudent(studentId.value, formData)
      ElMessage.success('保存成功')
      router.replace(`/school/${studentId.value}`)
    }
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

function onQuestionnaireSaved(type) {
  questionnaireStatus.value[type] = true
  loadDetail()
}
</script>

<style scoped>
.school-detail { padding: 0; }
.form-section { margin-bottom: 16px; }
.form-section :deep(.el-card__header) { padding: 12px 20px; background: #fafafa; }
.section-title { font-size: 15px; font-weight: 600; color: #303133; }
.tab-tag { margin-left: 6px; }
</style>
