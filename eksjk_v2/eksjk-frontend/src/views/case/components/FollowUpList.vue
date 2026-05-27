<template>
  <div class="followup-list">
    <!-- 生长曲线图表 -->
    <div v-if="list.length > 0" class="growth-chart-section">
      <el-radio-group v-model="chartType" size="small" style="margin-bottom: 12px">
        <el-radio-button value="height">身高</el-radio-button>
        <el-radio-button value="weight">体重</el-radio-button>
        <el-radio-button value="bmi">BMI</el-radio-button>
      </el-radio-group>
      <div ref="chartRef" class="chart-container"></div>
    </div>
    <el-timeline v-if="list.length > 0">
      <el-timeline-item v-for="item in list" :key="item.id"
                        :timestamp="formatDate(item.follTime)" placement="top">
        <el-card shadow="hover" class="followup-card">
          <div class="followup-summary">
            <div class="metrics">
              <span class="metric" v-if="item.ht">
                <label>身高</label> {{ item.ht }} cm
              </span>
              <span class="metric" v-if="item.wt">
                <label>体重</label> {{ item.wt }} kg
              </span>
              <span class="metric" v-if="item.bmi">
                <label>BMI</label> {{ item.bmi }}
              </span>
              <span class="metric" v-if="item.rboneAge && item.rboneAge !== '无'">
                <label>R骨龄</label> {{ item.rboneAge }}
              </span>
              <span class="metric" v-if="item.cboneAge && item.cboneAge !== '无'">
                <label>C骨龄</label> {{ item.cboneAge }}
              </span>
            </div>
            <div class="actions">
              <el-button link type="primary" size="small" @click="handleView(item)">查看</el-button>
              <el-button link type="primary" size="small" @click="handleEdit(item)">编辑</el-button>
              <el-popconfirm title="确定删除该随访记录吗？" @confirm="handleDelete(item)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
          <div class="followup-extra" v-if="item.diaTreaPlan && item.diaTreaPlan !== '无'">
            <label>诊疗方案：</label>{{ item.diaTreaPlan }}
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无随访记录" />

    <!-- 随访详情弹窗 -->
    <el-drawer v-model="detailVisible" title="随访详情" size="60%">
      <div v-if="currentDetail" class="followup-detail">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="随访日期">{{ formatDate(currentDetail.follTime) }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentDetail.age }}</el-descriptions-item>
          <el-descriptions-item label="身高(cm)">{{ currentDetail.ht }}</el-descriptions-item>
          <el-descriptions-item label="体重(kg)">{{ currentDetail.wt }}</el-descriptions-item>
          <el-descriptions-item label="BMI">{{ currentDetail.bmi }}</el-descriptions-item>
          <el-descriptions-item label="体脂率(%)">{{ currentDetail.bodyFat }}</el-descriptions-item>
          <el-descriptions-item label="腰围(cm)">{{ currentDetail.waistline }}</el-descriptions-item>
          <el-descriptions-item label="臀围(cm)">{{ currentDetail.hips }}</el-descriptions-item>
          <el-descriptions-item label="R骨龄">{{ currentDetail.rboneAge }}</el-descriptions-item>
          <el-descriptions-item label="C骨龄">{{ currentDetail.cboneAge }}</el-descriptions-item>
          <el-descriptions-item label="生殖器分期">{{ currentDetail.genStag }}</el-descriptions-item>
          <el-descriptions-item label="阴毛分期">{{ currentDetail.pubStag }}</el-descriptions-item>
          <el-descriptions-item label="IGF-1">{{ currentDetail.igf1 }}</el-descriptions-item>
          <el-descriptions-item label="IGFBP-3">{{ currentDetail.igfbp3 }}</el-descriptions-item>
          <el-descriptions-item label="LH">{{ currentDetail.lh }}</el-descriptions-item>
          <el-descriptions-item label="FSH">{{ currentDetail.fsh }}</el-descriptions-item>
          <el-descriptions-item label="E2">{{ currentDetail.e2 }}</el-descriptions-item>
          <el-descriptions-item label="T">{{ currentDetail.t }}</el-descriptions-item>
          <el-descriptions-item label="空腹血糖">{{ currentDetail.fasBloodGlu }}</el-descriptions-item>
          <el-descriptions-item label="空腹胰岛素">{{ currentDetail.fasInsulin }}</el-descriptions-item>
          <el-descriptions-item label="糖化血红蛋白">{{ currentDetail.glyHem }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentDetail.diaTreaPlan" style="margin-top: 16px">
          <h4>诊疗方案</h4>
          <p>{{ currentDetail.diaTreaPlan }}</p>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getFollowUpList, getFollowUpDetail, deleteFollowUp } from '@/api/followup'
import * as echarts from 'echarts'

const props = defineProps({
  patientId: { type: String, required: true }
})

const emit = defineEmits(['edit'])

const list = ref([])
const detailVisible = ref(false)
const currentDetail = ref(null)
const chartType = ref('height')
const chartRef = ref(null)
let chartInstance = null

onMounted(() => {
  if (props.patientId) loadList()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

watch(() => props.patientId, (val) => {
  if (val) loadList()
})

// 生长曲线切换
watch(chartType, () => {
  if (list.value.length > 0) nextTick(() => renderChart())
})

async function loadList() {
  try {
    const res = await getFollowUpList(props.patientId)
    list.value = res.data || []
    if (list.value.length > 0) nextTick(() => renderChart())
  } catch (error) {
    console.error('load followup list failed', error)
  }
}

function renderChart() {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }

  // 按随访日期升序排列
  const sorted = [...list.value].sort((a, b) => {
    if (!a.follTime) return -1
    if (!b.follTime) return 1
    return new Date(a.follTime) - new Date(b.follTime)
  })

  const dates = sorted.map(item => {
    if (!item.follTime) return ''
    return new Date(item.follTime).toLocaleDateString('zh-CN')
  })
  const ages = sorted.map(item => item.age || '')

  const xLabels = ages.some(a => a !== '') ? ages : dates

  let yData, yLabel, yUnit
  switch (chartType.value) {
    case 'height':
      yData = sorted.map(item => parseFloat(item.ht) || null)
      yLabel = '身高'
      yUnit = 'cm'
      break
    case 'weight':
      yData = sorted.map(item => parseFloat(item.wt) || null)
      yLabel = '体重'
      yUnit = 'kg'
      break
    case 'bmi':
      yData = sorted.map(item => parseFloat(item.bmi) || null)
      yLabel = 'BMI'
      yUnit = ''
      break
  }

  const option = {
    title: {
      text: yLabel + '变化曲线',
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600 }
    },
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        const p = params[0]
        return (xLabels[0] && ages[0] ? (ages[p.dataIndex] || dates[p.dataIndex]) : p.axisValue) + '<br/>' + yLabel + ': ' + (p.value !== null ? p.value + ' ' + yUnit : '无数据')
      }
    },
    xAxis: {
      type: 'category',
      data: xLabels,
      name: '随访时间',
      axisLabel: { rotate: 30 }
    },
    yAxis: {
      type: 'value',
      name: yLabel + (yUnit ? ' (' + yUnit + ')' : '')
    },
    series: [{
      data: yData,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 2, color: chartType.value === 'height' ? '#409eff' : chartType.value === 'weight' ? '#67c23a' : '#e6a23c' },
      itemStyle: { color: chartType.value === 'height' ? '#409eff' : chartType.value === 'weight' ? '#67c23a' : '#e6a23c' },
      markPoint: {
        data: yData.length > 0 ? [{ type: 'max', name: '最大值' }, { type: 'min', name: '最小值' }] : []
      }
    }],
    grid: { left: 60, right: 40, top: 50, bottom: 60 }
  }

  chartInstance.setOption(option, true)
}

async function handleView(item) {
  try {
    const res = await getFollowUpDetail(item.id)
    currentDetail.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('加载随访详情失败', error)
  }
}

function handleEdit(item) {
  emit('edit', item)
}

async function handleDelete(item) {
  try {
    await deleteFollowUp(item.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error) {
    console.error('删除失败', error)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 暴露刷新方法
function refresh() {
  loadList()
}

defineExpose({ refresh })
</script>

<style scoped>
.followup-card {
  cursor: pointer;
}
.growth-chart-section {
  margin-bottom: 24px;
}
.chart-container {
  width: 100%;
  height: 300px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.followup-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.metrics {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}
.metric {
  font-size: 14px;
  color: #606266;
}
.metric label {
  color: #909399;
  margin-right: 4px;
}
.followup-extra {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
}
.followup-extra label {
  font-weight: 500;
}
</style>
