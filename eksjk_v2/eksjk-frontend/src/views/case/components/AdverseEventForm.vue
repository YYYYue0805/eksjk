<template>
  <el-drawer :model-value="visible" :title="isEdit ? '编辑GH不良事件' : '新增GH不良事件'" size="55%"
             @close="handleClose" @closed="emit('closed')" @update:model-value="val => emit('update:visible', val)">
    <el-form ref="formRef" :model="formData" label-width="130px">
      <!-- 卡片1：患者端信息 -->
      <el-card shadow="never" class="form-section">
        <template #header><span>患者端信息</span></template>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="发生时间">
              <el-date-picker v-model="formData.occurrenceDate" type="date" placeholder="选择日期"
                              value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="发生症状">
          <el-input v-model="formData.symptoms" type="textarea" :rows="2" placeholder="请输入发生症状" />
        </el-form-item>
        <el-form-item label="发生原因">
          <el-input v-model="formData.cause" type="textarea" :rows="2" placeholder="请输入发生原因" />
        </el-form-item>
        <el-form-item label="采取措施">
          <el-input v-model="formData.measuresTaken" type="textarea" :rows="2" placeholder="请输入本次不良事件采取的措施" />
        </el-form-item>
      </el-card>

      <!-- 卡片2：事件要素 -->
      <el-card shadow="never" class="form-section">
        <template #header><span>事件要素</span></template>
        <el-form-item label="严重程度" prop="severity"
                      :rules="[{ required: true, message: '请选择严重程度', trigger: 'change' }]">
          <el-select v-model="formData.severity" placeholder="请选择" style="width:100%">
            <el-option label="轻度" value="轻度" />
            <el-option label="中度" value="中度" />
            <el-option label="重度" value="重度" />
            <el-option label="危及生命" value="危及生命" />
            <el-option label="致残" value="致残" />
            <el-option label="住院" value="住院" />
          </el-select>
        </el-form-item>
        <el-form-item label="GH关联性评价" prop="ghCausality"
                      :rules="[{ required: true, message: '请选择GH关联性评价', trigger: 'change' }]">
          <el-select v-model="formData.ghCausality" placeholder="请选择" style="width:100%">
            <el-option label="肯定相关" value="肯定相关" />
            <el-option label="很可能相关" value="很可能相关" />
            <el-option label="可能相关" value="可能相关" />
            <el-option label="可能无关" value="可能无关" />
            <el-option label="无关" value="无关" />
            <el-option label="待评价" value="待评价" />
          </el-select>
        </el-form-item>
      </el-card>

      <!-- 卡片3：具体表现（可多选） -->
      <el-card shadow="never" class="form-section">
        <template #header><span>具体表现（可多选）</span></template>

        <el-form-item label="局部不良反应">
          <el-checkbox-group v-model="formData.localReactions">
            <el-checkbox label="红肿" /><el-checkbox label="疼痛" />
            <el-checkbox label="硬结" /><el-checkbox label="瘙痒" />
            <el-checkbox label="皮疹" /><el-checkbox label="皮下脂肪萎缩" />
            <el-checkbox label="局部出血或瘀斑" /><el-checkbox label="其他" />
          </el-checkbox-group>
          <el-input v-if="formData.localReactions.includes('其他')"
                    v-model="formData.localOther" placeholder="请描述其他局部不良反应"
                    style="margin-top:8px" />
        </el-form-item>

        <el-form-item label="全身一般反应">
          <el-checkbox-group v-model="formData.systemicReactions">
            <el-checkbox label="头痛" /><el-checkbox label="乏力" />
            <el-checkbox label="发热" /><el-checkbox label="关节痛" />
            <el-checkbox label="肌肉痛" /><el-checkbox label="水肿(眼睑/下肢)" />
            <el-checkbox label="其他" />
          </el-checkbox-group>
          <el-input v-if="formData.systemicReactions.includes('其他')"
                    v-model="formData.systemicOther" placeholder="请描述其他全身反应"
                    style="margin-top:8px" />
        </el-form-item>

        <el-form-item label="内分泌/代谢相关">
          <el-checkbox-group v-model="formData.endocrineReactions">
            <el-checkbox label="血糖异常" /><el-checkbox label="甲状腺功能异常" />
            <el-checkbox label="水钠潴留电解质紊乱" /><el-checkbox label="肢端肥大样表现(长期用药)" />
            <el-checkbox label="其他" />
          </el-checkbox-group>
          <el-input v-if="formData.endocrineReactions.includes('其他')"
                    v-model="formData.endocrineOther" placeholder="请描述其他内分泌异常"
                    style="margin-top:8px" />
        </el-form-item>

        <el-form-item label="神经系统/眼部">
          <el-checkbox-group v-model="formData.neuroReactions">
            <el-checkbox label="良性颅内高压(假性脑瘤)" />
            <el-checkbox label="抽搐头晕嗜睡等" />
            <el-checkbox label="其他" />
          </el-checkbox-group>
          <el-input v-if="formData.neuroReactions.includes('其他')"
                    v-model="formData.neuroOther" placeholder="请描述其他神经/眼部异常"
                    style="margin-top:8px" />
        </el-form-item>

        <el-form-item label="皮肤过敏反应">
          <el-checkbox-group v-model="formData.skinReactions">
            <el-checkbox label="全身荨麻疹" /><el-checkbox label="过敏性皮炎" />
            <el-checkbox label="严重过敏反应" /><el-checkbox label="其他" />
          </el-checkbox-group>
          <el-input v-if="formData.skinReactions.includes('其他')"
                    v-model="formData.skinOther" placeholder="请描述其他皮肤过敏反应"
                    style="margin-top:8px" />
        </el-form-item>

        <el-form-item label="其他少见不良反应">
          <el-input v-model="formData.otherRareReaction" type="textarea" :rows="2"
                    placeholder="请输入其他少见不良反应描述" />
        </el-form-item>
      </el-card>

      <!-- 卡片4：诊疗干预 + 转归 -->
      <el-card shadow="never" class="form-section">
        <template #header><span>诊疗干预</span></template>
        <el-form-item label="医疗措施" prop="medicalIntervention"
                      :rules="[{ required: true, message: '请选择医疗措施', trigger: 'change' }]">
          <el-radio-group v-model="formData.medicalIntervention">
            <el-radio value="未干预">未干预</el-radio>
            <el-radio value="对症处理">对症处理</el-radio>
            <el-radio value="GH用药调整">GH用药调整</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.medicalIntervention === '对症处理'" label="药品名">
          <el-input v-model="formData.medicationName" placeholder="请输入使用的药品名称" />
        </el-form-item>
        <template v-if="formData.medicalIntervention === 'GH用药调整'">
          <el-form-item label="用药调整">
            <el-select v-model="formData.ghDoseAdjustment" placeholder="请选择" style="width:100%">
              <el-option label="继续用药" value="继续用药" />
              <el-option label="减量" value="减量" />
              <el-option label="暂停用药" value="暂停用药" />
              <el-option label="永久停药" value="永久停药" />
            </el-select>
          </el-form-item>
          <el-form-item label="调整原因">
            <el-input v-model="formData.adjustmentReason" type="textarea" :rows="2"
                      placeholder="请输入用药调整原因" />
          </el-form-item>
        </template>
      </el-card>

      <el-card shadow="never" class="form-section">
        <template #header><span>转归与随访</span></template>
        <el-form-item label="不良事件结局" prop="outcome"
                      :rules="[{ required: true, message: '请选择结局', trigger: 'change' }]">
          <el-select v-model="formData.outcome" placeholder="请选择" style="width:100%">
            <el-option label="痊愈" value="痊愈" />
            <el-option label="好转" value="好转" />
            <el-option label="未好转" value="未好转" />
            <el-option label="加重" value="加重" />
            <el-option label="死亡" value="死亡" />
            <el-option label="后遗症" value="后遗症" />
          </el-select>
        </el-form-item>
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
import { createAdverseEvent, updateAdverseEvent } from '@/api/gh-adverse-event'

const props = defineProps({
  visible: { type: Boolean, default: false },
  patientId: { type: String, required: true },
  editData: { type: Object, default: null }
})

const emit = defineEmits(['update:visible', 'saved', 'closed'])
const isEdit = computed(() => !!props.editData?.id)
const formRef = ref(null)
const saving = ref(false)

function defaultFormData() {
  return {
    patientId: '',
    occurrenceDate: '',
    symptoms: '',
    cause: '',
    measuresTaken: '',
    severity: '',
    ghCausality: '',
    localReactions: [],
    localOther: '',
    systemicReactions: [],
    systemicOther: '',
    endocrineReactions: [],
    endocrineOther: '',
    neuroReactions: [],
    neuroOther: '',
    skinReactions: [],
    skinOther: '',
    otherRareReaction: '',
    medicalIntervention: '',
    medicationName: '',
    ghDoseAdjustment: '',
    adjustmentReason: '',
    outcome: ''
  }
}

const formData = reactive(defaultFormData())

watch(() => props.visible, (val) => {
  if (val) {
    Object.assign(formData, defaultFormData())
    formData.patientId = props.patientId
    if (props.editData) {
      const d = props.editData
      formData.occurrenceDate = d.occurrenceDate || ''
      formData.symptoms = d.symptoms || ''
      formData.cause = d.cause || ''
      formData.measuresTaken = d.measuresTaken || ''
      formData.severity = d.severity || ''
      formData.ghCausality = d.ghCausality || ''
      formData.localReactions = toArray(d.localReactions)
      formData.localOther = d.localOther || ''
      formData.systemicReactions = toArray(d.systemicReactions)
      formData.systemicOther = d.systemicOther || ''
      formData.endocrineReactions = toArray(d.endocrineReactions)
      formData.endocrineOther = d.endocrineOther || ''
      formData.neuroReactions = toArray(d.neuroReactions)
      formData.neuroOther = d.neuroOther || ''
      formData.skinReactions = toArray(d.skinReactions)
      formData.skinOther = d.skinOther || ''
      formData.otherRareReaction = d.otherRareReaction || ''
      formData.medicalIntervention = d.medicalIntervention || ''
      formData.medicationName = d.medicationName || ''
      formData.ghDoseAdjustment = d.ghDoseAdjustment || ''
      formData.adjustmentReason = d.adjustmentReason || ''
      formData.outcome = d.outcome || ''
    }
  }
})

function toArray(val) {
  if (!val) return []
  if (Array.isArray(val)) return val
  if (typeof val === 'string') return val.split(',').map(s => s.trim()).filter(Boolean)
  return []
}

function booleanFieldsToComma(data) {
  const result = { ...data }
  for (const field of ['localReactions', 'systemicReactions', 'endocrineReactions', 'neuroReactions', 'skinReactions']) {
    if (Array.isArray(result[field])) {
      result[field] = result[field].join(',')
    }
  }
  result.patientId = props.patientId
  return result
}

async function handleSave() {
  try { await formRef.value?.validate() }
  catch { ElMessage.warning('请完善必填信息'); return }

  saving.value = true
  try {
    const payload = booleanFieldsToComma({ ...formData })
    if (isEdit.value) {
      await updateAdverseEvent(props.editData.id, payload)
      ElMessage.success('编辑成功')
    } else {
      await createAdverseEvent(payload)
      ElMessage.success('新增成功')
    }
    emit('saved')
    handleClose()
  } catch (error) {
    console.error('保存GH不良事件失败', error)
  } finally {
    saving.value = false
  }
}

function handleClose() {
  emit('update:visible', false)
}
</script>

<style scoped>
.form-section { margin-bottom: 16px; }
</style>
