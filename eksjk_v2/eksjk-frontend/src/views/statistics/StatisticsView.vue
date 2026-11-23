<template>
  <div class="statistics">
    <PageHeader title="统计分析" />

    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <!-- 按医院×月份新增 -->
        <el-tab-pane label="按医院×月份新增" name="hospital">
          <div class="statistics__toolbar">
            <el-date-picker
              v-model="hospitalRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
            <el-button type="primary" :loading="hospitalLoading" @click="loadHospitalMonthly">查询</el-button>
          </div>
          <el-table
            :data="hospitalMatrix.rows"
            v-loading="hospitalLoading"
            border
            size="small"
            show-summary
            :summary-method="hospitalSummaries"
          >
            <el-table-column prop="hospitalName" label="医院" min-width="160" fixed />
            <el-table-column
              v-for="m in hospitalMatrix.months"
              :key="m"
              :label="m"
              width="100"
              align="center"
            >
              <template #default="{ row }">{{ row.counts?.[m] || 0 }}</template>
            </el-table-column>
            <el-table-column prop="total" label="合计" width="90" align="center" />
          </el-table>
        </el-tab-pane>

        <!-- 按病种新增 -->
        <el-tab-pane label="按病种新增" name="disease">
          <div class="statistics__toolbar">
            <el-date-picker
              v-model="diseaseRange"
              type="daterange"
              value-format="YYYY-MM-DD"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
            <el-button type="primary" :loading="diseaseLoading" @click="loadDiseaseNewly">查询</el-button>
          </div>
          <el-table :data="diseaseRows" v-loading="diseaseLoading" border size="small">
            <el-table-column prop="diseaseName" label="病种" min-width="160" />
            <el-table-column prop="count" label="新增病例数" width="140" align="center" sortable />
          </el-table>
        </el-tab-pane>

        <!-- 自定义检索 -->
        <el-tab-pane label="自定义检索" name="custom">
          <el-form :model="customForm" label-width="90px" class="statistics__form">
            <el-row :gutter="12">
              <el-col :span="6">
                <el-form-item label="病种">
                  <el-select v-model="customForm.disClass" placeholder="全部" clearable>
                    <el-option v-for="d in diseaseOptions" :key="d.value" :label="d.label" :value="d.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="性别">
                  <el-select v-model="customForm.sex" placeholder="全部" clearable>
                    <el-option label="男" value="1" />
                    <el-option label="女" value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="创建时间">
                  <el-date-picker
                    v-model="customForm.range"
                    type="daterange"
                    value-format="YYYY-MM-DD"
                    range-separator="至"
                    start-placeholder="开始"
                    end-placeholder="结束"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="6">
                <el-form-item label="年龄(岁)">
                  <el-input v-model="customForm.minAge" placeholder="最小" style="width: 48%" />
                  <el-input v-model="customForm.maxAge" placeholder="最大" style="width: 48%; margin-left: 4%" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="身高(cm)">
                  <el-input v-model="customForm.minHeight" placeholder="最小" style="width: 48%" />
                  <el-input v-model="customForm.maxHeight" placeholder="最大" style="width: 48%; margin-left: 4%" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="骨龄">
                  <el-input v-model="customForm.minBoneAge" placeholder="最小" style="width: 48%" />
                  <el-input v-model="customForm.maxBoneAge" placeholder="最大" style="width: 48%; margin-left: 4%" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="达到终身高">
                  <el-select v-model="customForm.finalHeight" placeholder="不限" clearable>
                    <el-option label="是" :value="true" />
                    <el-option label="否" :value="false" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <div class="statistics__form-actions">
              <el-button type="primary" :loading="customLoading" @click="doCustomSearch">检索</el-button>
              <el-button @click="resetCustom">重置</el-button>
              <span v-if="customTotal !== null" class="statistics__total text-secondary">共匹配 {{ customTotal }} 例</span>
            </div>
          </el-form>

          <el-table :data="customList" v-loading="customLoading" border size="small" max-height="420">
            <el-table-column prop="name" label="姓名" min-width="100" />
            <el-table-column prop="sex" label="性别" width="70" align="center" />
            <el-table-column prop="age" label="年龄" width="70" align="center" />
            <el-table-column prop="height" label="身高" width="80" align="center" />
            <el-table-column prop="boneAge" label="骨龄" width="80" align="center" />
            <el-table-column prop="diseaseName" label="病种" min-width="130" />
            <el-table-column prop="hospitalName" label="医院" min-width="140" />
            <el-table-column label="达到终身高" width="100" align="center">
              <template #default="{ row }">{{ row.finalHeight ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="150" />
            <template #empty><el-empty description="输入条件后点击检索" :image-size="60" /></template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { getHospitalMonthly, getDiseaseNewly, customSearch } from '@/api/statistics'

const activeTab = ref('hospital')

// 病种选项
const diseaseOptions = [
  { value: '10000001', label: '性发育异常(DSD)' },
  { value: '10000002', label: '遗传性骨病(FSS)' },
  { value: '10000003', label: '中枢性性早熟(CPP)' },
  { value: '10000004', label: 'McCune-Albright(MAS)' },
  { value: '10000005', label: '小于胎龄儿(SGA)' },
  { value: '10000006', label: '家族性矮小(SSS)' },
  { value: '10000007', label: 'E路童萌(ELTM)' }
]

function rangeParams(range) {
  if (!range || range.length !== 2) return {}
  return { startDate: range[0], endDate: range[1] }
}

// ===== 医院×月份 =====
const hospitalRange = ref([])
const hospitalLoading = ref(false)
const hospitalMatrix = ref({ months: [], rows: [], monthTotals: {}, grandTotal: 0 })

async function loadHospitalMonthly() {
  hospitalLoading.value = true
  try {
    const res = await getHospitalMonthly(rangeParams(hospitalRange.value))
    hospitalMatrix.value = res.data || { months: [], rows: [], monthTotals: {}, grandTotal: 0 }
  } catch (error) {
    console.error('获取医院×月份统计失败:', error)
  } finally {
    hospitalLoading.value = false
  }
}

function hospitalSummaries({ columns }) {
  const matrix = hospitalMatrix.value
  return columns.map((col, idx) => {
    if (idx === 0) return '合计'
    if (col.property === 'total') return matrix.grandTotal ?? 0
    return matrix.monthTotals?.[col.label] ?? 0
  })
}

// ===== 按病种 =====
const diseaseRange = ref([])
const diseaseLoading = ref(false)
const diseaseRows = ref([])

async function loadDiseaseNewly() {
  diseaseLoading.value = true
  try {
    const res = await getDiseaseNewly(rangeParams(diseaseRange.value))
    diseaseRows.value = res.data || []
  } catch (error) {
    console.error('获取病种新增统计失败:', error)
  } finally {
    diseaseLoading.value = false
  }
}

// ===== 自定义检索 =====
const customLoading = ref(false)
const customTotal = ref(null)
const customList = ref([])
const emptyCustomForm = () => ({
  disClass: '',
  sex: '',
  range: [],
  minAge: '',
  maxAge: '',
  minHeight: '',
  maxHeight: '',
  minBoneAge: '',
  maxBoneAge: '',
  finalHeight: null
})
const customForm = ref(emptyCustomForm())

async function doCustomSearch() {
  customLoading.value = true
  try {
    const f = customForm.value
    const payload = {
      disClass: f.disClass || undefined,
      sex: f.sex || undefined,
      startDate: f.range?.[0],
      endDate: f.range?.[1],
      minAge: f.minAge || undefined,
      maxAge: f.maxAge || undefined,
      minHeight: f.minHeight || undefined,
      maxHeight: f.maxHeight || undefined,
      minBoneAge: f.minBoneAge || undefined,
      maxBoneAge: f.maxBoneAge || undefined,
      finalHeight: f.finalHeight === null || f.finalHeight === '' ? undefined : f.finalHeight
    }
    const res = await customSearch(payload)
    customTotal.value = res.data?.total ?? 0
    customList.value = res.data?.list || []
  } catch (error) {
    console.error('自定义检索失败:', error)
  } finally {
    customLoading.value = false
  }
}

function resetCustom() {
  customForm.value = emptyCustomForm()
  customTotal.value = null
  customList.value = []
}

onMounted(() => {
  loadHospitalMonthly()
  loadDiseaseNewly()
})
</script>

<style scoped>
.statistics__toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}
.statistics__form {
  margin-bottom: 8px;
}
.statistics__form-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.statistics__total {
  font-size: 13px;
}
</style>
