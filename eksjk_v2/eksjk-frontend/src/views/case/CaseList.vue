<template>
  <div class="case-list">
    <PageHeader :title="pageTitle">
      <template #actions>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>新建病例
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>导出Excel
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索区 -->
    <SearchForm :model="queryForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="病例编号">
        <el-input v-model="queryForm.caseNum" placeholder="请输入病例编号" clearable
                  @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryForm.name" placeholder="请输入患者姓名" clearable
                  @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="queryForm.sex" placeholder="全部" clearable>
          <el-option label="男" value="1" />
          <el-option label="女" value="2" />
        </el-select>
      </el-form-item>
      <template #collapse>
        <el-form-item label="病历号">
          <el-input v-model="queryForm.medrecNum" placeholder="请输入病历号" clearable />
        </el-form-item>
        <el-form-item label="上传时间">
          <el-date-picker v-model="dateRange" type="daterange"
                          range-separator="至" start-placeholder="开始日期"
                          end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 数据表格 -->
    <DataTable :data="tableData" :loading="loading" :total="total"
               :page-num="queryForm.pageNum" :page-size="queryForm.pageSize"
               @page-change="handlePageChange" @row-dblclick="handleView">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="caseNum" label="病例编号" width="160" show-overflow-tooltip />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="sexName" label="性别" width="70" align="center" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="medrecNum" label="病历号" width="130" show-overflow-tooltip />
      <el-table-column prop="height" label="身高(cm)" width="90" align="center" />
      <el-table-column prop="weight" label="体重(kg)" width="90" align="center" />
      <el-table-column prop="bmi" label="BMI" width="80" align="center" />
      <el-table-column prop="chiCom" label="主诉" min-width="150" show-overflow-tooltip />
      <el-table-column label="ICD" width="200" show-overflow-tooltip>
        <template #default="{ row }">
          {{ row.icd ? (icdLabelMap[row.icd] ? `${row.icd} ${icdLabelMap[row.icd]}` : row.icd) : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="impPer" label="导入人员" width="100" />
      <el-table-column prop="cTime" label="上传时间" width="170">
        <template #default="{ row }">
          {{ formatDate(row.cTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">
            查看
          </el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-popconfirm title="确定删除该病例吗？" @confirm="handleDelete(row)">
            <template #reference>
              <el-button link type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </DataTable>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Download } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import { getPatientList, deletePatient, exportPatientExcel, diseaseTypes, getDisClassByType } from '@/api/patient'
import { icdLabelMap } from '@/data/icdData'

const route = useRoute()
const router = useRouter()

// 当前疾病类型
const diseaseType = computed(() => route.params.type)
const pageTitle = computed(() => {
  return diseaseTypes[diseaseType.value]?.name || '病例管理'
})

// 查询表单
const queryForm = reactive({
  disClass: '',
  caseNum: '',
  name: '',
  sex: '',
  medrecNum: '',
  startTime: '',
  endTime: '',
  pageNum: 1,
  pageSize: 20
})

const dateRange = ref(null)
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 监听日期范围变化
watch(dateRange, (val) => {
  if (val) {
    queryForm.startTime = val[0] + ' 00:00:00'
    queryForm.endTime = val[1] + ' 23:59:59'
  } else {
    queryForm.startTime = ''
    queryForm.endTime = ''
  }
})

// 监听路由参数变化（切换疾病类型）
watch(() => route.params.type, (newType) => {
  if (newType) {
    queryForm.disClass = getDisClassByType(newType)
    queryForm.pageNum = 1
    loadData()
  }
}, { immediate: true })

/**
 * 加载数据
 */
async function loadData() {
  loading.value = true
  try {
    const res = await getPatientList(queryForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载病例列表失败', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.pageNum = 1
  loadData()
}

function handleReset() {
  queryForm.caseNum = ''
  queryForm.name = ''
  queryForm.sex = ''
  queryForm.medrecNum = ''
  queryForm.startTime = ''
  queryForm.endTime = ''
  dateRange.value = null
  queryForm.pageNum = 1
  loadData()
}

function handlePageChange({ pageNum, pageSize }) {
  queryForm.pageNum = pageNum
  queryForm.pageSize = pageSize
  loadData()
}

function handleCreate() {
  router.push(`/case/${diseaseType.value}/create`)
}

function handleView(row) {
  router.push(`/case/${diseaseType.value}/${row.id}`)
}

function handleEdit(row) {
  router.push(`/case/${diseaseType.value}/${row.id}/edit`)
}

async function handleDelete(row) {
  try {
    await deletePatient(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败', error)
  }
}

async function handleExport() {
  try {
    const res = await exportPatientExcel(queryForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${pageTitle.value}_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败', error)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}
</script>

<style scoped>
.case-list {
  padding: 0;
}
</style>