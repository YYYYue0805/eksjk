<template>
  <div class="disease-form-generic">
    <el-alert v-if="!hasFields" type="info" :closable="false"
              description="该疾病类型的专项数据表单将根据具体疾病类型动态加载。" />
    <div v-else>
      <el-row :gutter="16">
        <el-col :span="colSpan(field)" v-for="field in visibleFields" :key="field.key">
          <el-form-item :label="field.label">
            <el-input v-if="field.type === 'text'"
                      v-model="modelValue[field.key]"
                      :placeholder="field.placeholder || ''"
                      :disabled="disabled" />
            <el-input v-else-if="field.type === 'textarea'"
                      v-model="modelValue[field.key]"
                      type="textarea" :rows="2"
                      :placeholder="field.placeholder || ''"
                      :disabled="disabled" />
            <el-select v-else-if="field.type === 'select'"
                       v-model="modelValue[field.key]"
                       :placeholder="field.placeholder || '请选择'"
                       :disabled="disabled" clearable>
              <el-option v-for="opt in field.options" :key="opt.value"
                         :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  disabled: { type: Boolean, default: false },
  diseaseType: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])

/**
 * 各疾病类型的字段定义（与数据库表结构一致）
 */
const diseaseFieldsMap = {
  // DSD - 性腺状态/生殖器（诊断/治疗方案→诊断Tab，核型→遗传学Tab，激素→辅助检查Tab）
  dsd: [
    { key: 'gonadalStatus', label: '性腺状态', type: 'textarea' },
    { key: 'externalGenitalia', label: '外生殖器', type: 'textarea', wide: true },
    { key: 'internalGenitalia', label: '内生殖器', type: 'textarea', wide: true }
  ],
  // FSS - 骨龄/SDS（诊断/方案→诊断Tab，基因检测→遗传学Tab）
  fss: [
    { key: 'boneAge', label: '骨龄（疾病专用）', type: 'text' },
    { key: 'heightSds', label: '身高标准差 (SDS)', type: 'text' }
  ],
  // CPP - 发病年龄/骨龄提前（诊断/方案→诊断Tab，LH/FSH峰值→辅助检查Tab）
  cpp: [
    { key: 'onsetAge', label: '发病年龄', type: 'text' },
    { key: 'boneAgeAdvance', label: '骨龄提前量', type: 'text' }
  ],
  // MAS - 临床表现（诊断/方案→诊断Tab）
  mas: [
    { key: 'cafeAuLaitSpots', label: '咖啡牛奶斑', type: 'textarea' },
    { key: 'fibrousDysplasia', label: '纤维性骨发育不良', type: 'textarea' },
    { key: 'precociousPuberty', label: '性早熟', type: 'textarea' },
    { key: 'thyroidAbnormality', label: '甲状腺异常', type: 'textarea' },
    { key: 'ghExcess', label: '生长激素过多', type: 'textarea' },
    { key: 'cushingSyndrome', label: '库欣综合征', type: 'textarea' },
    { key: 'phosphateWasting', label: '磷酸盐消耗', type: 'textarea' }
  ],
  // SGA - 出生信息/追赶生长（诊断/方案→诊断Tab）
  sga: [
    { key: 'birthWeight', label: '出生体重 (g)', type: 'text' },
    { key: 'birthLength', label: '出生身长 (cm)', type: 'text' },
    { key: 'gestationalAge', label: '胎龄 (周)', type: 'text' },
    { key: 'catchUpGrowth', label: '追赶生长', type: 'text' }
  ],
  // SSS - 父母身高/靶身高（诊断/方案→诊断Tab，基因检测→遗传学Tab）
  sss: [
    { key: 'fatherHeight', label: '父亲身高 (cm)', type: 'text' },
    { key: 'motherHeight', label: '母亲身高 (cm)', type: 'text' },
    { key: 'targetHeight', label: '遗传靶身高 (cm)', type: 'text' }
  ],
  // ELTM - 筛查/评估（诊断/方案→诊断Tab）
  eltm: [
    { key: 'screeningResult', label: '筛查结果', type: 'textarea' },
    { key: 'assessmentData', label: '评估数据', type: 'textarea', wide: true }
  ]
}

const visibleFields = computed(() => {
  return diseaseFieldsMap[props.diseaseType] || []
})

const hasFields = computed(() => visibleFields.value.length > 0)

function colSpan(field) {
  if (field.wide) return 24
  if (field.type === 'textarea') return 12
  return 8
}
</script>

<style scoped>
.disease-form-generic {
  padding: 0;
}
</style>
