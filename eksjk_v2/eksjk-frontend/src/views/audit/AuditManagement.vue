<template>
  <div class="audit-management">
    <PageHeader title="审核发放管理">
      <template #actions>
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </template>
    </PageHeader>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--review">
          <div class="stat-card__value">{{ stats.patientPendingReview }}</div>
          <div class="stat-card__label">待审核基线</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--release">
          <div class="stat-card__value">{{ stats.patientPendingRelease }}</div>
          <div class="stat-card__label">待发放基线</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--review">
          <div class="stat-card__value">{{ stats.followUpPendingReview }}</div>
          <div class="stat-card__label">待审核随访</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card stat-card--release">
          <div class="stat-card__value">{{ stats.followUpPendingRelease }}</div>
          <div class="stat-card__label">待发放随访</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="基线信息审核" name="patient" />
      <el-tab-pane label="随访信息审核" name="followup" />
    </el-tabs>

    <!-- 状态筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="handleSearch">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="pending_review">待审核</el-radio-button>
        <el-radio-button value="pending_release">待发放</el-radio-button>
        <el-radio-button value="released">已发放</el-radio-button>
        <el-radio-button value="rejected">已驳回</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 搜索区 -->
    <SearchForm :model="queryForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="病例编号">
        <el-input v-model="queryForm.caseNum" placeholder="请输入病例编号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryForm.name" placeholder="请输入患者姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
    </SearchForm>

    <!-- 基线信息审核表格 -->
    <DataTable v-if="activeTab === 'patient'" :data="tableData" :loading="loading"
               :total="total" :page-num="queryForm.pageNum" :page-size="queryForm.pageSize"
               @page-change="handlePageChange">
      <el-table-column prop="caseNum" label="病例编号" width="160" show-overflow-tooltip />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="sexName" label="性别" width="70" align="center" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="medrecNum" label="病历号" width="130" show-overflow-tooltip />
      <el-table-column label="审核状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="auditStatusTag(row.auditStatus)" size="small">
            {{ auditStatusLabel(row.auditStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditBy" label="审核人" width="100" />
      <el-table-column label="审核时间" width="160">
        <template #default="{ row }">{{ formatDate(row.auditTime) }}</template>
      </el-table-column>
      <el-table-column prop="releaseBy" label="发放人" width="100" />
      <el-table-column label="发放时间" width="160">
        <template #default="{ row }">{{ formatDate(row.releaseTime) }}</template>
      </el-table-column>
      <el-table-column prop="auditRemark" label="审核意见" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.auditStatus === 'pending_review'" type="success" size="small"
                     @click="handleApprove(row)">审核通过</el-button>
          <el-button v-if="row.auditStatus === 'pending_review'" type="danger" size="small"
                     @click="handleReject(row)">驳回</el-button>
          <el-button v-if="row.auditStatus === 'pending_release'" type="primary" size="small"
                     @click="handleRelease(row)">发放</el-button>
          <span v-if="row.auditStatus === 'released'" style="color: #67C23A">已生效</span>
          <span v-if="row.auditStatus === 'rejected'" style="color: #F56C6C">已驳回</span>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 随访信息审核表格 -->
    <DataTable v-if="activeTab === 'followup'" :data="tableData" :loading="loading"
               :total="total" :page-num="queryForm.pageNum" :page-size="queryForm.pageSize"
               @page-change="handlePageChange">
      <el-table-column prop="patientCaseNum" label="病例编号" width="160" show-overflow-tooltip />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="patientSex" label="性别" width="70" align="center" />
      <el-table-column label="随访日期" width="120">
        <template #default="{ row }">{{ formatDate(row.follTime) }}</template>
      </el-table-column>
      <el-table-column label="基线状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="auditStatusTag(row.patientAuditStatus)" size="small">
            {{ auditStatusLabel(row.patientAuditStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="随访状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="auditStatusTag(row.auditStatus)" size="small">
            {{ auditStatusLabel(row.auditStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditBy" label="审核人" width="100" />
      <el-table-column label="审核时间" width="160">
        <template #default="{ row }">{{ formatDate(row.auditTime) }}</template>
      </el-table-column>
      <el-table-column prop="releaseBy" label="发放人" width="100" />
      <el-table-column label="发放时间" width="160">
        <template #default="{ row }">{{ formatDate(row.releaseTime) }}</template>
      </el-table-column>
      <el-table-column prop="auditRemark" label="审核意见" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="canOperateFollowUp(row)">
            <el-button v-if="row.auditStatus === 'pending_review'" type="success" size="small"
                       @click="handleApprove(row)">审核通过</el-button>
            <el-button v-if="row.auditStatus === 'pending_review'" type="danger" size="small"
                       @click="handleReject(row)">驳回</el-button>
            <el-button v-if="row.auditStatus === 'pending_release'" type="primary" size="small"
                       @click="handleRelease(row)">发放</el-button>
            <span v-if="row.auditStatus === 'released'" style="color: #67C23A">已生效</span>
            <span v-if="row.auditStatus === 'rejected'" style="color: #F56C6C">已驳回</span>
          </template>
          <el-tooltip v-else content="请先审核通过该患者的基线信息" placement="top">
            <span style="color: #C0C4CC; font-size: 12px;">请先审核基线</span>
          </el-tooltip>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 审核对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px" :close-on-click-modal="false">
      <el-form :model="dialogForm" label-width="80px">
        <el-form-item label="审核意见">
          <el-input v-model="dialogForm.comment" type="textarea" :rows="3"
                    :placeholder="isReject ? '请填写驳回原因（必填）' : '审核意见（选填）'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button :type="isReject ? 'danger' : 'success'" @click="confirmAction" :loading="submitting">
          {{ isReject ? '确认驳回' : '确认通过' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 发放确认对话框 -->
    <el-dialog v-model="releaseDialogVisible" title="确认发放" width="400px" :close-on-click-modal="false">
      <p>确定要发放该记录吗？发放后数据将正式生效。</p>
      <template #footer>
        <el-button @click="releaseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRelease" :loading="submitting">确认发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import {
  getPatientAuditList, approvePatient, rejectPatient, releasePatient,
  getFollowUpAuditList, approveFollowUp, rejectFollowUp, releaseFollowUp,
  getAuditStats
} from '@/api/audit'

const activeTab = ref('patient')
const statusFilter = ref('')
const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)

const queryForm = reactive({
  caseNum: '',
  name: '',
  pageNum: 1,
  pageSize: 20
})

const stats = reactive({
  patientPendingReview: 0,
  patientPendingRelease: 0,
  followUpPendingReview: 0,
  followUpPendingRelease: 0
})

// 审核对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isReject = ref(false)
const currentRow = ref(null)
const dialogForm = reactive({ comment: '' })

// 发放确认对话框
const releaseDialogVisible = ref(false)

async function loadData() {
  loading.value = true
  try {
    const params = { ...queryForm }
    if (statusFilter.value) {
      params.auditStatus = statusFilter.value
    }
    const api = activeTab.value === 'patient' ? getPatientAuditList : getFollowUpAuditList
    const res = await api(params)
    if (activeTab.value === 'followup') {
      // 随访列表经过后端过滤后总数可能不准确，直接使用返回数据
      tableData.value = res.data?.records || []
      total.value = res.data?.total || 0
    } else {
      tableData.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    console.error('加载审核列表失败', error)
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  try {
    const res = await getAuditStats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

function handleSearch() {
  queryForm.pageNum = 1
  loadData()
}

function handleReset() {
  queryForm.caseNum = ''
  queryForm.name = ''
  queryForm.pageNum = 1
  statusFilter.value = ''
  loadData()
}

function handlePageChange({ pageNum, pageSize }) {
  queryForm.pageNum = pageNum
  queryForm.pageSize = pageSize
  loadData()
}

function handleTabChange() {
  queryForm.pageNum = 1
  queryForm.caseNum = ''
  queryForm.name = ''
  statusFilter.value = ''
  loadData()
}

// 审批操作
function handleApprove(row) {
  currentRow.value = row
  isReject.value = false
  dialogForm.comment = ''
  dialogTitle.value = '审核通过'
  dialogVisible.value = true
}

function handleReject(row) {
  currentRow.value = row
  isReject.value = true
  dialogForm.comment = ''
  dialogTitle.value = '驳回'
  dialogVisible.value = true
}

async function confirmAction() {
  if (isReject.value && !dialogForm.comment.trim()) {
    ElMessage.warning('驳回时必须填写原因')
    return
  }
  submitting.value = true
  try {
    const row = currentRow.value
    const data = { comment: dialogForm.comment || undefined }
    if (activeTab.value === 'patient') {
      if (isReject.value) {
        await rejectPatient(row.id, data)
        ElMessage.success('已驳回')
      } else {
        await approvePatient(row.id, data)
        ElMessage.success('审核通过')
      }
    } else {
      if (isReject.value) {
        await rejectFollowUp(row.id, data)
        ElMessage.success('已驳回')
      } else {
        await approveFollowUp(row.id, data)
        ElMessage.success('审核通过')
      }
    }
    dialogVisible.value = false
    loadData()
    loadStats()
  } catch (error) {
    console.error('操作失败', error)
  } finally {
    submitting.value = false
  }
}

// 发放操作
function handleRelease(row) {
  currentRow.value = row
  releaseDialogVisible.value = true
}

async function confirmRelease() {
  submitting.value = true
  try {
    const row = currentRow.value
    if (activeTab.value === 'patient') {
      await releasePatient(row.id)
    } else {
      await releaseFollowUp(row.id)
    }
    ElMessage.success('发放成功')
    releaseDialogVisible.value = false
    loadData()
    loadStats()
  } catch (error) {
    console.error('发放失败', error)
  } finally {
    submitting.value = false
  }
}

function canOperateFollowUp(row) {
  const s = row.patientAuditStatus
  return s === 'pending_release' || s === 'released'
}

function auditStatusLabel(status) {
  const map = {
    pending_review: '待审核',
    pending_release: '待发放',
    released: '已发放',
    rejected: '已驳回'
  }
  return map[status] || status || '未知'
}

function auditStatusTag(status) {
  const map = {
    pending_review: 'warning',
    pending_release: '',
    released: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style scoped>
.audit-management {
  padding: 0;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.stat-card--review {
  border-left: 4px solid #E6A23C;
}

.stat-card--release {
  border-left: 4px solid #409EFF;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.filter-bar {
  margin-bottom: 12px;
}
</style>
