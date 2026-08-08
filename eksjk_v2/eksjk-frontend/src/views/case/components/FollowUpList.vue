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
          <div class="followup-extra" v-if="formatTreatment(item.diaTreaPlan)">
            <label>诊疗方案：</label>{{ formatTreatment(item.diaTreaPlan) }}
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无随访记录" />

    <!-- 随访详情弹窗 -->
    <el-drawer v-model="detailVisible" title="随访详情" size="70%">
      <div v-if="currentDetail" class="followup-detail">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="随访日期">{{ formatDate(currentDetail.follTime) }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ currentDetail.age }}</el-descriptions-item>
          <el-descriptions-item label="身高(cm)">{{ currentDetail.ht }}</el-descriptions-item>
          <el-descriptions-item label="体重(kg)">{{ currentDetail.wt }}</el-descriptions-item>
          <el-descriptions-item label="BMI">{{ currentDetail.bmi }}</el-descriptions-item>
          <el-descriptions-item label="生殖器分期">{{ currentDetail.genStag }}</el-descriptions-item>
          <el-descriptions-item label="阴毛分期">{{ currentDetail.pubStag }}</el-descriptions-item>
        </el-descriptions>
        <!-- 性激素 -->
        <h4 style="margin: 16px 0 8px">性激素及相关</h4>
        <el-descriptions :column="4" border>
          <el-descriptions-item label="LH(mIU/mL)">{{ currentDetail.lh }}<br><small>{{ currentDetail.lhCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="FSH(mIU/mL)">{{ currentDetail.fsh }}<br><small>{{ currentDetail.fshCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="E2(pg/mL)">{{ currentDetail.e2 }}<br><small>{{ currentDetail.e2CheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="T(ng/dL)">{{ currentDetail.t }}<br><small>{{ currentDetail.tCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="PRL(ng/mL)">{{ currentDetail.prl }}<br><small>{{ currentDetail.prlCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="DHT(ng/dL)">{{ currentDetail.dht }}<br><small>{{ currentDetail.dhtCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="FT(ng/dL)">{{ currentDetail.ft }}<br><small>{{ currentDetail.ftCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="SHBG(nmol/L)">{{ currentDetail.shbg }}<br><small>{{ currentDetail.shbgCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="AMH(ng/mL)">{{ currentDetail.amh }}<br><small>{{ currentDetail.amhCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="INHB(pg/mL)">{{ currentDetail.inhb }}<br><small>{{ currentDetail.inhbCheckDate }}</small></el-descriptions-item>
        </el-descriptions>
        <!-- 生长因子与代谢 -->
        <h4 style="margin: 16px 0 8px">生长因子与代谢</h4>
        <el-descriptions :column="4" border>
          <el-descriptions-item label="IGF-1(ng/mL)">{{ currentDetail.igf1 }}<br><small>{{ currentDetail.igf1CheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="IGFBP-3(ug/mL)">{{ currentDetail.igfbp3 }}<br><small>{{ currentDetail.igfbp3CheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="空腹血糖(mmol/L)">{{ currentDetail.fasBloodGlu }}<br><small>{{ currentDetail.fasBloodGluCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="空腹胰岛素(uIU/mL)">{{ currentDetail.fasInsulin }}<br><small>{{ currentDetail.fasInsulinCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="糖化血红蛋白(%)">{{ currentDetail.glyHem }}<br><small>{{ currentDetail.glyHemCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="HbA1c(%)">{{ currentDetail.glyHemA }}<br><small>{{ currentDetail.glyHemACheckDate }}</small></el-descriptions-item>
        </el-descriptions>
        <!-- 甲状腺功能 -->
        <h4 style="margin: 16px 0 8px">甲状腺功能</h4>
        <el-descriptions :column="4" border>
          <el-descriptions-item label="TSH(uIU/mL)">{{ currentDetail.tsh }}<br><small>{{ currentDetail.tshCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="FT3(pg/mL)">{{ currentDetail.ft3 }}<br><small>{{ currentDetail.ft3CheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="FT4(ng/dL)">{{ currentDetail.ft4 }}<br><small>{{ currentDetail.ft4CheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="TPOAb(IU/mL)">{{ currentDetail.tpoab }}<br><small>{{ currentDetail.tpoabCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="TgAb(IU/mL)">{{ currentDetail.tgab }}<br><small>{{ currentDetail.tgabCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="甲功评估">{{ currentDetail.thyroidFunction }}</el-descriptions-item>
        </el-descriptions>
        <!-- 肾上腺激素 -->
        <h4 style="margin: 16px 0 8px">肾上腺激素</h4>
        <el-descriptions :column="4" border>
          <el-descriptions-item label="ACTH(pg/mL)">{{ currentDetail.acth }}<br><small>{{ currentDetail.acthCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="皮质醇(ug/dL)">{{ currentDetail.cortisol }}<br><small>{{ currentDetail.cortisolCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="17-OHP(nmol/L)">{{ currentDetail.ohp }}<br><small>{{ currentDetail.ohpCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="DHEA-S(ug/dL)">{{ currentDetail.dheas }}<br><small>{{ currentDetail.dheasCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="雄烯二酮(ng/mL)">{{ currentDetail.androstenedione }}<br><small>{{ currentDetail.androstenedioneCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="AFP(ng/mL)">{{ currentDetail.afp }}<br><small>{{ currentDetail.afpCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="CEA(ng/mL)">{{ currentDetail.cea }}<br><small>{{ currentDetail.ceaCheckDate }}</small></el-descriptions-item>
        </el-descriptions>
        <!-- 激发试验 -->
        <h4 style="margin: 16px 0 8px">激发试验</h4>
        <el-descriptions :column="4" border>
          <el-descriptions-item label="HCG激发前T">{{ currentDetail.hcg }}<br><small>{{ currentDetail.hcgCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="HCG激发后T">{{ currentDetail.hcgt }}<br><small>{{ currentDetail.hcgtCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="HCG激发后DHT">{{ currentDetail.hcgdht }}<br><small>{{ currentDetail.hcgdhtCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="HCG激发后AD">{{ currentDetail.hcgad }}<br><small>{{ currentDetail.hcgadCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="GnRH激发LHmax">{{ currentDetail.lhMax }}<br><small>{{ currentDetail.lhMaxCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="GnRH激发FSHmax">{{ currentDetail.fshMax }}<br><small>{{ currentDetail.fshMaxCheckDate }}</small></el-descriptions-item>
          <el-descriptions-item label="GH峰值(ng/mL)">{{ currentDetail.gh }}<br><small>{{ currentDetail.ghCheckDate }}</small></el-descriptions-item>
        </el-descriptions>
        <!-- 影像检查 -->
        <h4 style="margin: 16px 0 8px">影像检查</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="性腺B超">{{ decodeDisplay(currentDetail.gonBUlt) }}</el-descriptions-item>
          <el-descriptions-item label="垂体MRI">{{ decodeDisplay(currentDetail.pituitaryMri) }}</el-descriptions-item>
          <el-descriptions-item label="甲状腺B超">{{ decodeDisplay(currentDetail.thyroidUlt) }}</el-descriptions-item>
          <el-descriptions-item label="骨密度">{{ decodeDisplay(currentDetail.bonMinDen) }}</el-descriptions-item>
        </el-descriptions>
        <!-- 常规实验室 -->
        <h4 style="margin: 16px 0 8px">常规实验室检查</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="血常规">{{ decodeDisplay(currentDetail.bloodRoutine) }}</el-descriptions-item>
          <el-descriptions-item label="尿常规">{{ decodeDisplay(currentDetail.urineRoutine) }}</el-descriptions-item>
          <el-descriptions-item label="乙肝三系">{{ currentDetail.hepatitisB }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="formatTreatment(currentDetail.diaTreaPlan)" style="margin-top: 16px">
          <h4>诊疗方案</h4>
          <p>{{ formatTreatment(currentDetail.diaTreaPlan) }}</p>
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
  patientId: { type: String, required: true },
  birthTime: { type: String, default: '' }
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

const planLabels = {
  '1': '未治疗', '2': 'rhGH治疗', '3': 'GnRHa联合生长激素治疗',
  '4': '停止GnRHa治疗', '5': '停止GnRHa联合生长激素治疗',
  '6': '停止生长激素治疗', '7': 'GnRHa治疗', '8': '芳香化酶抑制剂',
  '9': '中医药治疗', '10': '芳香化酶联合生长激素治疗',
  '11': '停止芳香化酶抑制剂', '12': '停止芳香化酶联合生长激素治疗'
}

const laghDrugLabels = {
  '11': '金赛增', '12': '益佩生', '13': '维臻高', '14': '诺泽优'
}

const rhghDrugLabels = {
  '21': '赛增粉剂', '22': '赛增水剂', '23': '诺泽粉剂', '24': '诺泽水剂',
  '25': '安苏萌粉剂', '26': '安苏萌水剂', '27': '海之元粉剂', '28': '海之元水剂', '29': '珍怡粉剂'
}

const rhGHTypeLabels = { '1': 'LAGH', '2': 'rhGH' }

function formatTreatment(diaTreaPlan) {
  if (!diaTreaPlan || diaTreaPlan === '无') return ''
  try {
    const plan = JSON.parse(diaTreaPlan)
    if (!plan || typeof plan !== 'object') return diaTreaPlan
    const parts = [planLabels[plan.diaPlan] || '未知方案']
    if (plan.diaPlan === '2') {
      if (plan.rhGHType) parts.push(rhGHTypeLabels[plan.rhGHType] || '')
      if (plan.rhGH && plan.rhGHType === '1') {
        parts.push(laghDrugLabels[plan.rhGH] || '')
        if (plan.PEGrhGHdose) parts.push(plan.PEGrhGHdose + ' mg/w')
      } else if (plan.rhGH && plan.rhGHType === '2') {
        parts.push(rhghDrugLabels[plan.rhGH] || '')
        if (plan.rhGHdose) parts.push(plan.rhGHdose + ' IU/d')
      }
    }
    if (plan.otherMedicine) parts.push('其他用药: ' + plan.otherMedicine)
    return parts.join('；')
  } catch { return diaTreaPlan }
}

function parseField(raw) {
  if (!raw) return { result: '0', description: '' }
  const idx = raw.indexOf('|')
  if (idx === -1) return { result: '0', description: raw }
  return { result: raw.substring(0, idx) || '0', description: raw.substring(idx + 1) }
}

function decodeDisplay(raw) {
  if (!raw) return ''
  const parsed = parseField(raw)
  const labels = { '0': '未查', '1': '正常', '2': '异常' }
  const base = labels[parsed.result]
  if (base === undefined) return raw
  return parsed.result === '2' && parsed.description ? base + '：' + parsed.description : base
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
