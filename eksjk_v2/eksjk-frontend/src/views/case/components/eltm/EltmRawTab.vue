<template>
  <div class="eltm-raw-tab" v-loading="loading">
    <el-alert
      title="以下为该患者从 datamain/E路童萌 cxList 同步的原始行快照（66 列，只读），用于核对接口数据。"
      type="info"
      :closable="false"
      show-icon
      class="source-tip"
    />

    <template v-if="raw">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="外部患者ID">{{ raw.externalId || '—' }}</el-descriptions-item>
        <el-descriptions-item label="同步时间">{{ formatTime(raw.syncTime) }}</el-descriptions-item>
        <el-descriptions-item label="行指纹" :span="2">{{ raw.rowHash || '—' }}</el-descriptions-item>
      </el-descriptions>

      <el-collapse v-model="active" class="raw-collapse">
        <el-collapse-item title="cxList 原始行 JSON（66 列）" name="raw">
          <pre class="raw-json">{{ prettyJson }}</pre>
        </el-collapse-item>
      </el-collapse>
    </template>

    <el-empty v-if="!loading && !raw" description="暂无原始行数据" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { getEltmPatientRaw } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const loading = ref(false)
const raw = ref(null)
const active = ref('raw')

const prettyJson = computed(() => {
  if (!raw.value?.rawJson) return ''
  try {
    return JSON.stringify(JSON.parse(raw.value.rawJson), null, 2)
  } catch {
    return raw.value.rawJson
  }
})

function formatTime(value) {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

async function loadData() {
  loading.value = true
  try {
    const res = await getEltmPatientRaw(props.patientId)
    raw.value = res.data || null
  } catch (error) {
    console.error('加载原始行失败', error)
  } finally {
    loading.value = false
  }
}

watch(() => props.patientId, (value) => {
  if (value) loadData()
})

onMounted(() => {
  if (props.patientId) loadData()
})
</script>

<style scoped>
.source-tip { margin-bottom: 16px; }
.raw-collapse { margin-top: 20px; }
.raw-json {
  max-height: 520px;
  overflow: auto;
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
