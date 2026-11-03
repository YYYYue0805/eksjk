<template>
  <div class="eltm-unmapped-tab" v-loading="loading">
    <el-alert
      title="以下为 datamain/E路童萌 接口中尚未识别或未配置映射的原始数据（只读）。已进入标准栏目的内容请在对应模块查看。"
      type="info"
      :closable="false"
      show-icon
      class="source-tip"
    />

    <div class="toolbar">
      <el-select v-model="module" placeholder="全部目标模块" clearable style="width: 220px"
                 :disabled="false" @change="handleFilterChange">
        <el-option v-for="item in moduleOptions" :key="item.module"
                   :label="`${item.module}（${item.count}）`" :value="item.module" />
      </el-select>
      <el-select v-model="category" placeholder="全部类别" clearable style="width: 220px; margin-left: 12px"
                 :disabled="false" @change="handleFilterChange">
        <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </div>

    <el-table :data="records" border stripe size="small" style="width: 100%" row-key="id">
      <el-table-column label="接口" width="100">
        <template #default="{ row }">{{ apiText(row.apiCode) }}</template>
      </el-table-column>
      <el-table-column label="建议模块" width="140">
        <template #default="{ row }">
          <el-tag type="success" size="small">{{ row.suggestedModule || '其他/保留' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="类别" width="140">
        <template #default="{ row }">
          <el-tag :type="categoryTag(row.category)" size="small">{{ categoryText(row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="字段 / 来源" min-width="240" show-overflow-tooltip>
        <template #default="{ row }">{{ row.sourceName || row.sourceKey || '—' }}</template>
      </el-table-column>
      <el-table-column prop="reason" label="未处理原因" min-width="200" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">{{ statusText(row.status) }}</template>
      </el-table-column>
      <el-table-column label="同步时间" width="170">
        <template #default="{ row }">{{ formatTime(row.syncTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" :disabled="false" @click="openDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && records.length === 0" description="暂无未识别数据" />

    <div v-if="total > pageSize" class="pager">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="loadData"
      />
    </div>

    <el-drawer v-model="detailVisible" title="未识别数据详情" size="60%">
      <div v-loading="detailLoading" class="detail-body">
        <el-descriptions v-if="detail.id" :column="2" border size="small">
          <el-descriptions-item label="接口">{{ apiText(detail.apiCode) }}</el-descriptions-item>
          <el-descriptions-item label="类别">{{ categoryText(detail.category) }}</el-descriptions-item>
          <el-descriptions-item label="建议模块">{{ detail.suggestedModule || '其他/保留' }}</el-descriptions-item>
          <el-descriptions-item label="字段 / 来源" :span="2">
            {{ detail.sourceName || detail.sourceKey || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="来源键" :span="2">{{ detail.sourceKey || '—' }}</el-descriptions-item>
          <el-descriptions-item label="原因" :span="2">{{ detail.reason || '—' }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
          <el-descriptions-item label="同步时间">{{ formatTime(detail.syncTime) }}</el-descriptions-item>
        </el-descriptions>

        <template v-if="detail.rawValue">
          <div class="section-title">原始值</div>
          <pre class="raw-json">{{ detail.rawValue }}</pre>
        </template>

        <el-collapse v-if="detail.rawJson" class="raw-collapse">
          <el-collapse-item title="原始 JSON" name="raw">
            <pre class="raw-json">{{ prettyJson }}</pre>
          </el-collapse-item>
        </el-collapse>

        <el-empty v-if="!detailLoading && !detail.id" description="暂无详情" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { getEltmUnprocessed, getEltmUnprocessedDetail, getEltmUnprocessedModules } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const categories = [
  { value: 'UNKNOWN_COLUMN', label: '未知列' },
  { value: 'UNPARSED_JSON', label: '未解析 JSON' },
  { value: 'UNHANDLED_FORM', label: '未处理表单' },
  { value: 'UNMAPPED_FIELD', label: '未映射字段' },
  { value: 'UNKNOWN_INTERFACE_FIELD', label: '接口未知字段' }
]

const API_LABELS = {
  CXLIST: 'cxList',
  GETTPBYID: 'gettpbyid',
  GETZQBYID: 'getzqbyid'
}
const CATEGORY_LABELS = Object.fromEntries(categories.map(item => [item.value, item.label]))
const STATUS_LABELS = { PENDING: '待处理', MAPPED: '已映射', IGNORED: '已忽略' }

const loading = ref(false)
const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const category = ref('')
const module = ref('')
const moduleOptions = ref([])

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref({})

const prettyJson = computed(() => {
  const raw = detail.value?.rawJson
  if (!raw) return ''
  try {
    return JSON.stringify(JSON.parse(raw), null, 2)
  } catch {
    return raw
  }
})

function apiText(value) {
  return API_LABELS[value] || value || '—'
}

function categoryText(value) {
  return CATEGORY_LABELS[value] || value || '—'
}

function categoryTag(value) {
  switch (value) {
    case 'UNKNOWN_COLUMN': return 'warning'
    case 'UNHANDLED_FORM': return 'danger'
    case 'UNMAPPED_FIELD': return 'info'
    case 'UNKNOWN_INTERFACE_FIELD': return 'warning'
    default: return ''
  }
}

function statusText(value) {
  return STATUS_LABELS[value] || value || '—'
}

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
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (category.value) params.category = category.value
    if (module.value) params.module = module.value
    const res = await getEltmUnprocessed(props.patientId, params)
    const data = res.data || {}
    records.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载未识别数据失败', error)
  } finally {
    loading.value = false
  }
}

async function loadModules() {
  try {
    const res = await getEltmUnprocessedModules(props.patientId)
    moduleOptions.value = res.data || []
  } catch (error) {
    console.error('加载未识别数据模块统计失败', error)
  }
}

function handleFilterChange() {
  pageNum.value = 1
  loadData()
}

async function openDetail(row) {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = {}
  try {
    const res = await getEltmUnprocessedDetail(props.patientId, row.id)
    detail.value = res.data || {}
  } catch (error) {
    console.error('加载未识别数据详情失败', error)
  } finally {
    detailLoading.value = false
  }
}

watch(() => props.patientId, (value) => {
  if (value) {
    pageNum.value = 1
    loadModules()
    loadData()
  }
})

onMounted(() => {
  if (props.patientId) {
    loadModules()
    loadData()
  }
})
</script>

<style scoped>
.source-tip { margin-bottom: 16px; }
.toolbar { margin-bottom: 12px; }
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
</style>
