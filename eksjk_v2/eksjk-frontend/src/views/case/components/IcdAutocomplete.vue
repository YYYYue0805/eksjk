<template>
  <el-autocomplete
    v-model="displayText"
    :fetch-suggestions="querySearch"
    :placeholder="placeholder"
    :disabled="disabled"
    clearable
    value-key="label"
    @select="handleSelect"
  >
    <template #default="{ item }">
      <div class="icd-suggestion-item">{{ item.label }}</div>
    </template>
  </el-autocomplete>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  placeholder: { type: String, default: '请输入ICD编码或疾病名称搜索' }
})

const emit = defineEmits(['update:modelValue'])

// 动态加载 ICD 数据，仅在用户使用自动补全时才下载 2MB 数据
let icdCache = null
async function ensureIcdData() {
  if (!icdCache) {
    const mod = await import('@/data/icdData')
    icdCache = { options: mod.icdOptions, labelMap: mod.icdLabelMap }
  }
  return icdCache
}

async function codeToLabel(code) {
  if (!code) return ''
  try {
    const { labelMap } = await ensureIcdData()
    const name = labelMap[code]
    return name ? `${code} ${name}` : code
  } catch { return code }
}

const displayText = ref(props.modelValue || '')

// 异步加载 ICD 标签
onMounted(async () => {
  if (props.modelValue) {
    displayText.value = await codeToLabel(props.modelValue)
  }
})

watch(() => props.modelValue, async (val) => {
  displayText.value = val ? await codeToLabel(val) : ''
})

watch(displayText, (val) => {
  if (!val) {
    emit('update:modelValue', '')
  }
})

async function querySearch(queryString, callback) {
  const { options } = await ensureIcdData()
  if (!queryString || queryString.trim() === '') {
    callback(options.slice(0, 20))
    return
  }
  const q = queryString.trim().toLowerCase()
  const results = []
  for (const item of options) {
    if (item.value.toLowerCase().startsWith(q)) {
      results.push(item)
      if (results.length >= 20) break
    }
  }
  if (results.length < 20) {
    const existingValues = new Set(results.map(r => r.value))
    for (const item of options) {
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
  displayText.value = item.label
  emit('update:modelValue', item.value)
}
</script>

<style scoped>
.icd-suggestion-item {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
