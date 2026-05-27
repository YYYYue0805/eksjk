<template>
  <div class="school-list">
    <PageHeader title="健康筛查">
      <template #actions>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>新增学生
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索区 -->
    <SearchForm :model="queryForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="编号">
        <el-input v-model="queryForm.num" placeholder="请输入编号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryForm.name" placeholder="请输入姓名" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="queryForm.sex" placeholder="全部" clearable>
          <el-option label="男" value="1" />
          <el-option label="女" value="2" />
        </el-select>
      </el-form-item>
      <template #collapse>
        <el-form-item label="班级">
          <el-input v-model="queryForm.sclass" placeholder="请输入班级" clearable />
        </el-form-item>
      </template>
    </SearchForm>

    <!-- 数据表格 -->
    <DataTable :data="tableData" :loading="loading" :total="total"
               :page-num="queryForm.pageNum" :page-size="queryForm.pageSize"
               @page-change="handlePageChange" @row-dblclick="handleView">
      <el-table-column prop="num" label="编号" width="130" show-overflow-tooltip />
      <el-table-column prop="sclass" label="班级" width="100" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="sexName" label="性别" width="70" align="center" />
      <el-table-column prop="height" label="身高(cm)" width="90" align="center" />
      <el-table-column prop="weight" label="体重(kg)" width="90" align="center" />
      <el-table-column prop="impPer" label="导入人员" width="100" />
      <el-table-column prop="cTime" label="导入时间" width="170">
        <template #default="{ row }">
          {{ formatDate(row.cTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该学生记录吗？" @confirm="handleDelete(row)">
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import { getStudentList, deleteStudent } from '@/api/school'

const router = useRouter()

const queryForm = reactive({
  num: '',
  name: '',
  sex: '',
  sclass: '',
  pageNum: 1,
  pageSize: 20
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getStudentList(queryForm)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载学生列表失败', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryForm.pageNum = 1
  loadData()
}

function handleReset() {
  queryForm.num = ''
  queryForm.name = ''
  queryForm.sex = ''
  queryForm.sclass = ''
  queryForm.pageNum = 1
  loadData()
}

function handlePageChange({ pageNum, pageSize }) {
  queryForm.pageNum = pageNum
  queryForm.pageSize = pageSize
  loadData()
}

function handleCreate() {
  router.push('/school/create')
}

function handleView(row) {
  router.push(`/school/${row.id}`)
}

function handleEdit(row) {
  router.push(`/school/${row.id}/edit`)
}

async function handleDelete(row) {
  try {
    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败', error)
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
.school-list {
  padding: 0;
}
</style>
