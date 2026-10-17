<template>
  <div class="eltm-reports-tab" v-loading="loading">
    <el-alert title="数据来源：E路童萌，只读展示" type="info" :closable="false" show-icon class="source-tip" />

    <el-table :data="records" border stripe size="small" style="width: 100%" row-key="id">
      <el-table-column prop="reportTime" label="报告时间" width="170">
        <template #default="{ row }">{{ formatTime(row.reportTime) }}</template>
      </el-table-column>
      <el-table-column prop="modelName" label="评估模型" min-width="180" show-overflow-tooltip />
      <el-table-column prop="formName" label="表单名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="projectName" label="所属项目" min-width="160" show-overflow-tooltip />
      <el-table-column label="得分" width="100">
        <template #default="{ row }">
          {{ row.score || '—' }}<span v-if="row.totalScore"> / {{ row.totalScore }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="evaluationResult" label="评估结果" min-width="140" show-overflow-tooltip />
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="report-detail">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="结果描述" :span="2">
                {{ stripHtml(row.resultDescription) || '—' }}
              </el-descriptions-item>
              <el-descriptions-item label="报告备注" :span="2">
                {{ stripHtml(row.reportRemarkValue) || '—' }}
              </el-descriptions-item>
              <el-descriptions-item label="报告建议" :span="2">
                {{ stripHtml(row.reportSuggestValue) || '—' }}
              </el-descriptions-item>
              <el-descriptions-item label="本次检查日期">{{ row.baseCheckDateValue || '—' }}</el-descriptions-item>
              <el-descriptions-item label="建议下次检查">{{ row.baseNextCheckDateValue || '—' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && records.length === 0" description="暂无评估报告" />

    <div v-if="total > pageSize" class="pager">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { getEltmReports } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const loading = ref(false)
const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)

function formatTime(value) {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

function stripHtml(value) {
  if (!value) return ''
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

async function loadData() {
  loading.value = true
  try {
    const res = await getEltmReports(props.patientId, {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    const data = res.data || {}
    records.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载评估报告失败', error)
  } finally {
    loading.value = false
  }
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
.report-detail { padding: 12px 20px; }
.pager { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
