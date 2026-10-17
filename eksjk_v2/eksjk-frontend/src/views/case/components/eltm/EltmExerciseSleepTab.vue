<template>
  <div class="eltm-exercise-sleep" v-loading="loading">
    <el-alert title="数据来源：E路童萌，只读展示" type="info" :closable="false" show-icon class="source-tip" />

    <div class="section-title">运动记录</div>
    <el-table :data="exercises" border stripe size="small" style="width: 100%">
      <el-table-column prop="recordDate" label="日期" width="110" />
      <el-table-column prop="categoryName" label="分类" min-width="120" show-overflow-tooltip />
      <el-table-column prop="exerciseName" label="运动项目" min-width="140" show-overflow-tooltip />
      <el-table-column prop="durationSeconds" label="时长(秒)" width="100" />
      <el-table-column prop="caloriesBurned" label="消耗(kcal)" width="110" />
      <el-table-column prop="metValue" label="MET" width="80" />
      <el-table-column prop="intensityLevel" label="强度" width="80" />
    </el-table>
    <el-empty v-if="!loading && exercises.length === 0" description="暂无运动记录" :image-size="60" />

    <div class="section-title">运动汇总</div>
    <el-table :data="exerciseSummaries" border stripe size="small" style="width: 100%">
      <el-table-column prop="summaryDate" label="日期" width="110" />
      <el-table-column prop="totalCount" label="次数" width="80" />
      <el-table-column prop="totalDurationSeconds" label="总时长(秒)" width="110" />
      <el-table-column prop="totalCalories" label="总消耗(kcal)" width="120" />
      <el-table-column prop="mvpaMinutes" label="MVPA(分钟)" width="110" />
      <el-table-column prop="exerciseTypes" label="运动类型" min-width="160" show-overflow-tooltip />
    </el-table>
    <el-empty v-if="!loading && exerciseSummaries.length === 0" description="暂无运动汇总" :image-size="60" />

    <div class="section-title">睡眠记录</div>
    <el-table :data="sleeps" border stripe size="small" style="width: 100%">
      <el-table-column prop="recordDate" label="日期" width="110" />
      <el-table-column label="入睡时间" width="170">
        <template #default="{ row }">{{ formatTime(row.sleepStartTime) }}</template>
      </el-table-column>
      <el-table-column label="起床时间" width="170">
        <template #default="{ row }">{{ formatTime(row.sleepEndTime) }}</template>
      </el-table-column>
      <el-table-column label="时长" width="100">
        <template #default="{ row }">{{ formatDuration(row.sleepDurationHours, row.sleepDurationMinutes) }}</template>
      </el-table-column>
      <el-table-column prop="awakeTimes" label="夜醒次数" width="90" />
      <el-table-column prop="sleepWay" label="入睡方式" min-width="110" show-overflow-tooltip />
      <el-table-column prop="sleepHabits" label="睡眠习惯" min-width="140" show-overflow-tooltip />
    </el-table>
    <el-empty v-if="!loading && sleeps.length === 0" description="暂无睡眠记录" :image-size="60" />

    <div class="section-title">睡眠汇总</div>
    <el-table :data="sleepSummaries" border stripe size="small" style="width: 100%">
      <el-table-column prop="summaryDate" label="日期" width="110" />
      <el-table-column prop="totalDurationHours" label="总时长(小时)" width="120" />
      <el-table-column label="是否达标" width="100">
        <template #default="{ row }">{{ row.isTargetReached === 1 ? '是' : row.isTargetReached === 0 ? '否' : '—' }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!loading && sleepSummaries.length === 0" description="暂无睡眠汇总" :image-size="60" />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { getEltmExerciseSleep } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const loading = ref(false)
const exercises = ref([])
const exerciseSummaries = ref([])
const sleeps = ref([])
const sleepSummaries = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getEltmExerciseSleep(props.patientId)
    const data = res.data || {}
    exercises.value = data.exercises || []
    exerciseSummaries.value = data.exerciseSummaries || []
    sleeps.value = data.sleeps || []
    sleepSummaries.value = data.sleepSummaries || []
  } catch (error) {
    console.error('加载运动睡眠数据失败', error)
  } finally {
    loading.value = false
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

function formatDuration(hours, minutes) {
  const h = Number(hours) || 0
  const m = Number(minutes) || 0
  if (!h && !m) return '—'
  return `${h}小时${m}分`
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
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 24px 0 12px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}
.section-title:first-of-type { margin-top: 0; }
</style>
