<template>
  <div class="adverse-event-list">
    <el-table :data="list" border stripe v-loading="loading" empty-text="暂无不良事件记录">
      <el-table-column prop="occurrenceDate" label="发生时间" width="120" />
      <el-table-column prop="severity" label="严重程度" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.severity" :type="severityTagType(row.severity)" size="small">
            {{ row.severity }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ghCausality" label="GH关联性" min-width="120" show-overflow-tooltip />
      <el-table-column label="具体表现" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          {{ formatReactions(row) }}
        </template>
      </el-table-column>
      <el-table-column prop="outcome" label="结局" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.outcome" :type="outcomeTagType(row.outcome)" size="small">
            {{ row.outcome }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="记录时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" text @click="handleView(row)">查看</el-button>
          <el-button size="small" type="primary" text @click="emit('edit', row)">编辑</el-button>
          <el-popconfirm title="确定删除该不良事件记录？" confirm-button-text="确定" cancel-button-text="取消"
                         @confirm="handleDelete(row)">
            <template #reference>
              <el-button size="small" type="danger" text>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdverseEventList, getAdverseEventDetail, deleteAdverseEvent } from '@/api/gh-adverse-event'

const props = defineProps({
  patientId: { type: String, required: true }
})

const emit = defineEmits(['edit'])

const list = ref([])
const loading = ref(false)

async function fetchList() {
  if (!props.patientId) return
  loading.value = true
  try {
    const res = await getAdverseEventList(props.patientId)
    if (res.code === 200) {
      list.value = res.data || []
    }
  } catch (error) {
    console.error('获取不良事件列表失败', error)
  } finally {
    loading.value = false
  }
}

async function handleView(row) {
  try {
    const res = await getAdverseEventDetail(row.id)
    if (res.code === 200) {
      emit('edit', res.data)
    }
  } catch (error) {
    console.error('获取不良事件详情失败', error)
  }
}

async function handleDelete(row) {
  try {
    await deleteAdverseEvent(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch (error) {
    console.error('删除不良事件失败', error)
  }
}

function formatReactions(row) {
  const parts = []
  // 遍历所有反应字段
  const fields = [
    { key: 'localReactions', label: '局部' },
    { key: 'systemicReactions', label: '全身' },
    { key: 'endocrineReactions', label: '内分泌' },
    { key: 'neuroReactions', label: '神经' },
    { key: 'skinReactions', label: '皮肤' }
  ]
  for (const { key, label } of fields) {
    const val = row[key]
    if (val) {
      const items = typeof val === 'string' ? val : ''
      if (items) parts.push(`${label}:${items}`)
    }
  }
  if (row.otherRareReaction) parts.push(`其他:${row.otherRareReaction}`)
  return parts.join('; ') || '-'
}

function severityTagType(severity) {
  const map = { '轻度': 'info', '中度': 'warning', '重度': 'danger', '危及生命': 'danger', '致残': 'danger', '住院': 'warning' }
  return map[severity] || 'info'
}

function outcomeTagType(outcome) {
  const map = { '痊愈': 'success', '好转': '', '未好转': 'warning', '加重': 'danger', '死亡': 'danger', '后遗症': 'warning' }
  return map[outcome] || 'info'
}

defineExpose({ refresh: fetchList })

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.adverse-event-list { margin-top: 0; }
</style>
