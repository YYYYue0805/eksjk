<template>
  <div class="eltm-growth-tab" v-loading="loading">
    <el-alert title="数据来源：E路童萌，只读展示" type="info" :closable="false" show-icon class="source-tip" />

    <el-row :gutter="16" class="summary-row">
      <el-col :span="6">
        <el-card shadow="never" class="summary-card">
          <div class="summary-label">最新身高</div>
          <div class="summary-value">{{ latest.height || '—' }} <small>cm</small></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="summary-card">
          <div class="summary-label">最新体重</div>
          <div class="summary-value">{{ latest.weight || '—' }} <small>kg</small></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="summary-card">
          <div class="summary-label">最新 BMI</div>
          <div class="summary-value">{{ latest.bmi || '—' }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="summary-card">
          <div class="summary-label">测量次数</div>
          <div class="summary-value">{{ total }}</div>
        </el-card>
      </el-col>
    </el-row>

    <div v-if="records.length > 0" class="chart-section">
      <el-radio-group v-model="chartType" size="small" :disabled="false" style="margin-bottom: 12px">
        <el-radio-button value="height">身高</el-radio-button>
        <el-radio-button value="weight">体重</el-radio-button>
        <el-radio-button value="bmi">BMI</el-radio-button>
      </el-radio-group>
      <div ref="chartRef" class="chart-container"></div>
    </div>

    <el-table :data="records" border stripe size="small" style="width: 100%" row-key="id">
      <el-table-column prop="measureTime" label="测量时间" width="170" />
      <el-table-column label="身高(cm)" width="100">
        <template #default="{ row }">{{ row.items['height:Tab'] || '—' }}</template>
      </el-table-column>
      <el-table-column label="体重(kg)" width="100">
        <template #default="{ row }">{{ row.items['weight:Tab'] || '—' }}</template>
      </el-table-column>
      <el-table-column label="BMI" width="90">
        <template #default="{ row }">{{ row.items.bmi || '—' }}</template>
      </el-table-column>
      <el-table-column label="父亲身高" width="100">
        <template #default="{ row }">{{ row.items.fatherHeight || '—' }}</template>
      </el-table-column>
      <el-table-column label="母亲身高" width="100">
        <template #default="{ row }">{{ row.items.motherHeight || '—' }}</template>
      </el-table-column>
      <el-table-column label="预测身高" width="100">
        <template #default="{ row }">{{ row.items.expectHeight || '—' }}</template>
      </el-table-column>
      <el-table-column label="身高百分位" width="110">
        <template #default="{ row }">{{ row.items.heightP || '—' }}</template>
      </el-table-column>
      <el-table-column label="SDS" width="90">
        <template #default="{ row }">{{ row.items.standardDeviation || '—' }}</template>
      </el-table-column>
      <el-table-column prop="dataSource" label="来源" width="100" />
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="expand-items">
            <el-tag v-for="(value, code) in row.items" :key="code" size="small" class="item-tag">
              {{ attributeLabel(code) }}：{{ value }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && records.length === 0" description="暂无体征测量数据" />

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
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { getEltmOverview, getEltmPhysical } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const loading = ref(false)
const records = ref([])
const chartRecords = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const chartType = ref('height')
const chartRef = ref(null)
let chartInstance = null
let overview = ref({})

const ATTRIBUTE_LABELS = {
  'height:Tab': '身高',
  'weight:Tab': '体重',
  bmi: 'BMI',
  'sex:Tab': '性别',
  'birth:Tab': '出生日期',
  'Data:Tab': '数据日期',
  fatherHeight: '父亲身高',
  motherHeight: '母亲身高',
  expectHeight: '预测身高',
  inheritedHeight: '遗传身高',
  heightP: '身高百分位',
  weightP: '体重百分位',
  heightPredictPercentile: '预测身高百分位',
  standardDeviation: '标准差(SDS)',
  outpatientMedicalRecordUrl: '门诊病历',
  checklistResultUrl: '检查结果',
  testReportResultUrl: '检验报告',
  otherUrl: '其他资料'
}

const latest = computed(() => {
  const info = overview.value?.latestPhysical
  const items = info?.items || {}
  return {
    height: items['height:Tab'],
    weight: items['weight:Tab'],
    bmi: items.bmi,
    measureTime: info?.measureTime
  }
})

function attributeLabel(code) {
  return ATTRIBUTE_LABELS[code] || code
}

async function loadOverview() {
  try {
    const res = await getEltmOverview(props.patientId)
    overview.value = res.data || {}
  } catch (error) {
    console.error('加载 ELTM 概览失败', error)
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getEltmPhysical(props.patientId, {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    const data = res.data || {}
    records.value = data.records || []
    total.value = data.total || 0
    await nextTick()
    renderChart()
  } catch (error) {
    console.error('加载体征数据失败', error)
  } finally {
    loading.value = false
  }
}

async function loadChartData() {
  const all = []
  let page = 1
  const size = 100
  try {
    while (true) {
      const res = await getEltmPhysical(props.patientId, { pageNum: page, pageSize: size })
      const data = res.data || {}
      const list = data.records || []
      all.push(...list)
      const count = data.total || 0
      if (list.length === 0 || all.length >= count) break
      page++
    }
    chartRecords.value = all
    await nextTick()
    renderChart()
  } catch (error) {
    console.error('加载生长曲线数据失败', error)
  }
}

function renderChart() {
  if (!chartRef.value || chartRecords.value.length === 0) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  const sorted = [...chartRecords.value].sort((a, b) => new Date(a.measureTime) - new Date(b.measureTime))
  const labels = sorted.map(item => formatDate(item.measureTime))
  const field = chartType.value === 'height'
    ? 'height:Tab'
    : chartType.value === 'weight' ? 'weight:Tab' : 'bmi'
  const values = sorted.map(item => {
    const value = parseFloat(item.items?.[field])
    return Number.isNaN(value) ? null : value
  })
  const unit = chartType.value === 'height' ? 'cm' : chartType.value === 'weight' ? 'kg' : ''
  chartInstance.setOption({
    title: {
      text: `${chartType.value === 'height' ? '身高' : chartType.value === 'weight' ? '体重' : 'BMI'}变化曲线`,
      left: 'center',
      textStyle: { fontSize: 14, fontWeight: 600 }
    },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: labels, axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', name: unit },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbolSize: 8,
      connectNulls: true,
      lineStyle: { width: 2, color: '#409eff' },
      itemStyle: { color: '#409eff' }
    }],
    grid: { left: 60, right: 40, top: 50, bottom: 70 }
  }, true)
}

function formatDate(value) {
  if (!value) return ''
  return new Date(value).toLocaleDateString('zh-CN')
}

watch(chartType, () => nextTick(() => renderChart()))

watch(() => props.patientId, (value) => {
  if (value) {
    pageNum.value = 1
    chartRecords.value = []
    loadOverview()
    loadData()
    loadChartData()
  }
})

onMounted(() => {
  if (props.patientId) {
    loadOverview()
    loadData()
    loadChartData()
  }
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.source-tip { margin-bottom: 16px; }
.summary-row { margin-bottom: 20px; }
.summary-card { text-align: center; }
.summary-label { color: #909399; font-size: 13px; }
.summary-value { margin-top: 6px; font-size: 22px; font-weight: 600; color: #303133; }
.summary-value small { font-size: 12px; color: #909399; font-weight: 400; }
.chart-section { margin-bottom: 20px; }
.chart-container { width: 100%; height: 300px; border: 1px solid #ebeef5; border-radius: 4px; }
.expand-items { padding: 8px 16px; }
.item-tag { margin: 4px 8px 4px 0; }
.pager { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
