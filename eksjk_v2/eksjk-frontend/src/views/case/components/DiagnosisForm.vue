<template>
  <div class="diagnosis-form">
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="是否已治疗">
          <el-select v-model="localData.isTreated" :disabled="disabled" placeholder="请选择" clearable>
            <el-option label="已治疗" value="1" />
            <el-option label="未治疗" value="0" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12" /> <!-- 占位保持布局 -->
    </el-row>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="诊断结论">
          <el-input v-model="localData.diagnosis" type="textarea" :rows="4" :disabled="disabled" placeholder="请输入诊断结论" />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="次要诊断">
          <el-input v-model="localData.secondaryDiagnosis" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入次要诊断（选填）" />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="治疗方案">
          <el-input v-model="localData.treatmentPlan" type="textarea" :rows="4" :disabled="disabled" placeholder="请输入治疗方案" />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="标签">
          <el-input v-model="tagsStr" :disabled="disabled" placeholder="请输入标签，多个用逗号分隔" @blur="onTagsBlur" />
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue'])

const localData = reactive({
  diagnosis: '',
  secondaryDiagnosis: '',
  treatmentPlan: '',
  isTreated: '',
  tags: []
})

const tagsStr = computed({
  get: () => Array.isArray(localData.tags) ? localData.tags.join(',') : '',
  set: (val) => { /* handled in blur */ }
})

function onTagsBlur() {
  const str = tagsStr.value
  localData.tags = str ? str.split(',').map(t => t.trim()).filter(Boolean) : []
  syncToParent()
}

// 双向绑定
watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    Object.keys(localData).forEach(key => {
      if (val[key] !== undefined && val[key] !== null) {
        localData[key] = val[key]
      }
    })
  }
}, { immediate: true, deep: true })

watch(localData, () => {
  syncToParent()
}, { deep: true })

function syncToParent() {
  emit('update:modelValue', { ...localData, tags: [...localData.tags] })
}
</script>

<style scoped>
.diagnosis-form {
  padding: 0;
}
</style>
