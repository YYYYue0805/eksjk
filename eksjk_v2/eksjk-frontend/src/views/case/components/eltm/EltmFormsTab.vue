<template>
  <div class="eltm-forms-tab" v-loading="loading">
    <el-alert title="数据来源：E路童萌，只读展示。表单题目来自外部问卷，部分题目可能没有中文名称。" type="info"
              :closable="false" show-icon class="source-tip" />

    <el-table :data="records" border stripe size="small" style="width: 100%" row-key="id">
      <el-table-column prop="formName" label="表单名称" min-width="220" show-overflow-tooltip />
      <el-table-column prop="projectName" label="所属项目" min-width="180" show-overflow-tooltip />
      <el-table-column prop="submittedAt" label="提交时间" width="170">
        <template #default="{ row }">{{ formatTime(row.submittedAt) }}</template>
      </el-table-column>
      <el-table-column prop="score" label="得分" width="90" />
      <el-table-column prop="evaluationResult" label="评估结果" min-width="160" show-overflow-tooltip />
      <el-table-column prop="resultDescription" label="结果描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" :disabled="false" @click="openDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && records.length === 0" description="暂无表单数据" />

    <div v-if="total > pageSize" class="pager">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="loadData"
      />
    </div>

    <el-drawer v-model="detailVisible" title="表单详情" size="70%">
      <div v-loading="detailLoading" class="detail-body">
        <el-descriptions v-if="detail.submission" :column="2" border size="small">
          <el-descriptions-item label="表单名称">{{ detail.submission.formName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="所属项目">{{ detail.submission.projectName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ detail.submission.submittedAt || '—' }}</el-descriptions-item>
          <el-descriptions-item label="得分">{{ detail.submission.score || '—' }}</el-descriptions-item>
          <el-descriptions-item label="评估结果" :span="2">{{ detail.submission.evaluationResult || '—' }}</el-descriptions-item>
          <el-descriptions-item label="结果描述" :span="2">
            <div class="rich-text">{{ stripHtml(detail.submission.resultDescription) || '—' }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <template v-if="formRows.length > 0">
          <div class="section-title">答题内容</div>
          <el-table :data="formRows" border stripe size="small" style="width: 100%">
            <el-table-column prop="key" label="字段" width="260" show-overflow-tooltip />
            <el-table-column prop="label" label="题目" width="240" show-overflow-tooltip>
              <template #default="{ row }">{{ row.label || '—' }}</template>
            </el-table-column>
            <el-table-column prop="value" label="答案" min-width="260" show-overflow-tooltip />
          </el-table>
        </template>

        <el-collapse v-if="detail.submission?.originalData" class="raw-collapse">
          <el-collapse-item title="原始答题 JSON" name="raw">
            <pre class="raw-json">{{ prettyJson }}</pre>
          </el-collapse-item>
        </el-collapse>

        <el-empty v-if="!detailLoading && !detail.submission" description="暂无表单详情" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { getEltmFormDetail, getEltmForms } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const loading = ref(false)
const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref({})
const formRows = ref([])

const prettyJson = computed(() => {
  const raw = detail.value?.submission?.originalData
  if (!raw) return ''
  try {
    return JSON.stringify(JSON.parse(raw), null, 2)
  } catch {
    return raw
  }
})

async function loadData() {
  loading.value = true
  try {
    const res = await getEltmForms(props.patientId, {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    const data = res.data || {}
    records.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载表单数据失败', error)
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = {}
  formRows.value = []
  try {
    const res = await getEltmFormDetail(props.patientId, row.formDataId)
    detail.value = res.data || {}
    formRows.value = parseFormData(detail.value?.submission?.originalData)
  } catch (error) {
    console.error('加载表单详情失败', error)
  } finally {
    detailLoading.value = false
  }
}

function formatTime(value) {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

function stripHtml(value) {  if (!value) return ''
  return String(value)
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/(p|div|li|tr)>/gi, '\n')
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/g, ' ')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&amp;/g, '&')
    .trim()
}

function parseFormData(raw) {
  if (!raw) return []
  let obj
  try {
    obj = JSON.parse(raw)
  } catch {
    return []
  }
  if (!obj || typeof obj !== 'object') return []
  if (Array.isArray(obj)) {
    return obj.map((item, index) => ({ key: String(index + 1), label: '', value: formatAnswer(item) }))
  }
  return Object.entries(obj).map(([key, value]) => ({
    key,
    label: value?.label || '',
    value: formatAnswer(value)
  }))
}

function formatAnswer(value) {  if (value === null || value === undefined || value === '') return '—'
  if (typeof value === 'string' || typeof value === 'number' || typeof value === 'boolean') {
    return String(value)
  }
  if (Array.isArray(value)) {
    return value.map(formatAnswer).filter(item => item && item !== '—').join('；') || '—'
  }
  if (typeof value === 'object') {
    if (value.labelList && value.value !== undefined) {
      const rawValues = Array.isArray(value.value) ? value.value : [value.value]
      const labels = rawValues
        .map(item => {
          if (item === null || item === undefined || item === '') return ''
          return value.labelList[item] || String(item)
        })
        .filter(Boolean)
      if (labels.length > 0) return labels.join('、')
    }
    if ('value' in value) return formatAnswer(value.value)
    if (Array.isArray(value.rows)) return value.rows.map(formatAnswer).join('；') || '—'
    try {
      return JSON.stringify(value)
    } catch {
      return String(value)
    }
  }
  return String(value)
}

watch(() => props.patientId, (value) => {
  if (value) {
    pageNum.value = 1
    loadData()
  }
})

onMounted(() => {
  if (props.patientId) loadData()
})
</script>

<style scoped>
.source-tip { margin-bottom: 16px; }
.pager { display: flex; justify-content: flex-end; margin-top: 16px; }
.detail-body { padding: 0 4px 24px; }
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 24px 0 12px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}
.raw-collapse { margin-top: 20px; }
.raw-json {
  max-height: 420px;
  overflow: auto;
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}
.rich-text { white-space: pre-wrap; line-height: 1.7; }
</style>
