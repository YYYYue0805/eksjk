<template>
  <el-autocomplete
    :model-value="modelValue"
    :fetch-suggestions="querySearch"
    :placeholder="placeholder"
    :disabled="disabled"
    clearable
    value-key="value"
    @update:model-value="$emit('update:modelValue', $event)"
    @select="handleSelect"
  />
</template>

<script setup>
import { icdOptions } from '@/data/icdData'

const props = defineProps({
  modelValue: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  placeholder: { type: String, default: '请输入ICD编码或疾病名称搜索' }
})

const emit = defineEmits(['update:modelValue'])

function querySearch(queryString, callback) {
  if (!queryString || queryString.trim() === '') {
    callback(icdOptions.slice(0, 20))
    return
  }
  const q = queryString.trim().toLowerCase()
  const results = []
  // 先匹配编码前缀
  for (const item of icdOptions) {
    if (item.value.toLowerCase().startsWith(q)) {
      results.push(item)
      if (results.length >= 20) break
    }
  }
  // 再匹配名称包含
  if (results.length < 20) {
    const existingValues = new Set(results.map(r => r.value))
    for (const item of icdOptions) {
      if (!existingValues.has(item.value) && item.label.toLowerCase().includes(q)) {
        results.push(item)
        existingValues.add(item.value)
        if (results.length >= 20) break
      }
    }
  }
  callback(results)
}

function handleSelect(item) {
  emit('update:modelValue', item.value)
}
</script>
