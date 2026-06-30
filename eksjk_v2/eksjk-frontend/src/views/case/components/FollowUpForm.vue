<template>
  <el-drawer :model-value="visible" :title="isEdit ? '编辑随访记录' : '新增随访记录'" size="50%"
             @close="handleClose" @update:model-value="val => emit('update:visible', val)">
    <el-form ref="formRef" :model="formData" label-width="120px">
      <el-card shadow="never" class="form-section">
        <template #header><span>基础测量</span></template>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="随访日期" prop="follTime" :rules="[{ required: true, message: '请选择随访日期' }]">
              <el-date-picker v-model="formData.follTime" type="date" placeholder="选择日期"
                              value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input v-model="formData.age" placeholder="年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身高">
              <el-input v-model="formData.ht" placeholder="cm" @blur="calcBmi">
                <template #suffix>cm</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体重">
              <el-input v-model="formData.wt" placeholder="kg" @blur="calcBmi">
                <template #suffix>kg</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="BMI">
              <el-input v-model="formData.bmi" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="体脂率">
              <el-input v-model="formData.bodyFat" placeholder="%">
                <template #suffix>%</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="腰围">
              <el-input v-model="formData.waistline" placeholder="cm">
                <template #suffix>cm</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="臀围">
              <el-input v-model="formData.hips" placeholder="cm">
                <template #suffix>cm</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>骨龄与发育</span></template>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="R系列骨龄">
              <el-input v-model="formData.rboneAge" placeholder="岁" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="C系列骨龄">
              <el-input v-model="formData.cboneAge" placeholder="岁" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生殖器分期">
              <el-input v-model="formData.genStag" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阴毛分期">
              <el-input v-model="formData.pubStag" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>实验室检查</span></template>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="IGF-1">
              <el-input v-model="formData.igf1" placeholder="ng/ml" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="IGFBP-3">
              <el-input v-model="formData.igfbp3" placeholder="ug/ml" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="LH">
              <el-input v-model="formData.lh" placeholder="miu/ml" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="FSH">
              <el-input v-model="formData.fsh" placeholder="miu/ml" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="E2">
              <el-input v-model="formData.e2" placeholder="pg/ml" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="T">
              <el-input v-model="formData.t" placeholder="ng/dL" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="空腹血糖">
              <el-input v-model="formData.fasBloodGlu" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="空腹胰岛素">
              <el-input v-model="formData.fasInsulin" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="糖化血红蛋白">
              <el-input v-model="formData.glyHem" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>诊疗方案</span></template>
        <el-form-item label="性腺B超">
          <el-input v-model="formData.gonBUlt" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="骨密度">
          <el-input v-model="formData.bonMinDen" />
        </el-form-item>
        <el-form-item label="诊疗方案">
          <el-input v-model="formData.diaTreaPlan" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="其他">
          <el-input v-model="formData.other" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="是否达终身高">
          <el-select v-model="formData.isFinalhei">
            <el-option label="是" value="是" />
            <el-option label="否" value="否" />
            <el-option label="无" value="无" />
          </el-select>
        </el-form-item>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>眼科检查</span></template>
        <el-form-item label="是否有做眼科检查">
          <el-radio-group v-model="eyeExam.hasExam" @change="syncEyeExam">
            <el-radio value="0">无</el-radio>
            <el-radio value="1">有</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="eyeExam.hasExam === '1'">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="检查时间">
                <el-date-picker v-model="eyeExam.examDate" type="date"
                  value-format="YYYY-MM-DD" placeholder="请选择日期" @change="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="裸眼视力 右">
                <el-input v-model="eyeExam.nakedVisionRight" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="裸眼视力 左">
                <el-input v-model="eyeExam.nakedVisionLeft" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="矫正视力 右">
                <el-input v-model="eyeExam.correctedVisionRight" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="矫正视力 左">
                <el-input v-model="eyeExam.correctedVisionLeft" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="眼轴 右 (cm)">
                <el-input v-model="eyeExam.axialLengthRight" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="眼轴 左 (cm)">
                <el-input v-model="eyeExam.axialLengthLeft" @input="syncEyeExam" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-card>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { createFollowUp, updateFollowUp } from '@/api/followup'

const props = defineProps({
  visible: { type: Boolean, default: false },
  patientId: { type: String, required: true },
  diseaseType: { type: String, default: '' },
  editData: { type: Object, default: null }
})

const emit = defineEmits(['update:visible', 'saved'])

const isEdit = computed(() => !!props.editData?.id)
const formRef = ref(null)
const saving = ref(false)

const formData = reactive({
  patientId: '',
  follTime: new Date().toISOString().slice(0, 19),
  age: '',
  ht: '',
  wt: '',
  bmi: '',
  bodyFat: '',
  waistline: '',
  hips: '',
  rboneAge: '',
  cboneAge: '',
  genStag: '',
  pubStag: '',
  igf1: '',
  igfbp3: '',
  lh: '',
  fsh: '',
  e2: '',
  t: '',
  fasBloodGlu: '',
  fasInsulin: '',
  glyHem: '',
  gonBUlt: '',
  bonMinDen: '',
  diaTreaPlan: '',
  other: '',
  isFinalhei: '',
  eyeExam: ''
})

// 眼科检查
const eyeExam = reactive({
  hasExam: '0',
  examDate: '',
  nakedVisionRight: '',
  nakedVisionLeft: '',
  correctedVisionRight: '',
  correctedVisionLeft: '',
  axialLengthRight: '',
  axialLengthLeft: ''
})

watch(() => props.visible, (val) => {
  if (val) {
    formData.patientId = props.patientId
    if (props.editData) {
      Object.keys(formData).forEach(key => {
        if (props.editData[key] !== undefined) {
          formData[key] = props.editData[key]
        }
      })
      parseEyeExam(props.editData.eyeExam)
    }
  }
})

function calcBmi() {
  if (formData.ht && formData.wt) {
    try {
      const h = parseFloat(formData.ht) / 100
      const w = parseFloat(formData.wt)
      if (h > 0 && w > 0) {
        formData.bmi = (w / (h * h)).toFixed(1)
      }
    } catch (e) {
      formData.bmi = ''
    }
  }
}

function syncEyeExam() {
  formData.eyeExam = JSON.stringify({ ...eyeExam })
}

function parseEyeExam(json) {
  if (!json) return
  try {
    const obj = JSON.parse(json)
    Object.keys(eyeExam).forEach(k => {
      if (obj[k] !== undefined) eyeExam[k] = obj[k]
    })
  } catch { /* ignore parse errors */ }
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
    if (isEdit.value) {
      await updateFollowUp(props.editData.id, formData)
    } else {
      await createFollowUp(formData)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    emit('saved')
    handleClose()
  } catch (error) {
    console.error('保存失败', error)
  } finally {
    saving.value = false
  }
}

function handleClose() {
  emit('update:visible', false)
  // 重置表单
  Object.keys(formData).forEach(key => {
    if (key === 'follTime') {
      formData[key] = new Date().toISOString().slice(0, 19)
    } else {
      formData[key] = ''
    }
  })
  // 重置眼科检查
  eyeExam.hasExam = '0'
  eyeExam.examDate = ''
  eyeExam.nakedVisionRight = ''
  eyeExam.nakedVisionLeft = ''
  eyeExam.correctedVisionRight = ''
  eyeExam.correctedVisionLeft = ''
  eyeExam.axialLengthRight = ''
  eyeExam.axialLengthLeft = ''
}
</script>

<style scoped>
.form-section {
  margin-bottom: 12px;
}
.form-section :deep(.el-card__header) {
  padding: 8px 16px;
  background: #fafafa;
  font-weight: 600;
  font-size: 14px;
}
</style>
