<template>
  <div class="user-list">
    <PageHeader title="用户管理">
      <template #actions>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索区 -->
    <SearchForm v-model="queryForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="用户名/姓名" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="queryForm.roleCode" placeholder="全部" clearable style="width: 150px">
          <el-option label="超级管理员" value="super_admin" />
          <el-option label="医院管理员" value="hospital_admin" />
          <el-option label="普通医生" value="doctor" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="userStore.isSuperAdmin" label="所属医院">
        <el-select v-model="queryForm.hospitalId" placeholder="全部" clearable filterable style="width: 200px">
          <el-option v-for="item in unitOptions" :key="item.id" :label="item.name" :value="String(item.id)" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.isActive" placeholder="全部" clearable style="width: 120px">
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <!-- 数据表格 -->
    <DataTable
      :data="tableData"
      :loading="loading"
      :total="total"
      v-model:pageNum="queryForm.pageNum"
      v-model:pageSize="queryForm.pageSize"
      @page-change="loadData"
    >
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="100" />
      <el-table-column prop="roleName" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="getRoleTagType(row.roleCode)" size="small">{{ row.roleName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hospitalName" label="所属医院" min-width="150" show-overflow-tooltip />
      <el-table-column prop="department" label="科室" width="100" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.roleCode ? 'success' : 'danger'" size="small">
            {{ row.roleCode ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLogin" label="最后登录" width="170" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="warning" size="small" @click="handleResetPassword(row)">重置密码</el-button>
          <el-button link :type="row.isActive ? 'danger' : 'success'" size="small" @click="handleToggleStatus(row)">
            {{ row.isActive ? '禁用' : '启用' }}
          </el-button>
          <el-button v-if="userStore.isSuperAdmin" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 新增/编辑抽屉 -->
    <FormDialog
      v-model="formVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      :is-edit="isEdit"
      :submit-loading="submitLoading"
      @submit="handleSubmit"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="formData.sex" placeholder="请选择" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="roleCode">
          <el-select v-model="formData.roleCode" :disabled="userStore.isHospitalAdmin" placeholder="请选择角色" style="width: 100%">
            <el-option v-if="userStore.isSuperAdmin" label="超级管理员" value="super_admin" />
            <el-option v-if="userStore.isSuperAdmin" label="医院管理员" value="hospital_admin" />
            <el-option label="普通医生" value="doctor" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属医院">
          <el-select v-model="formData.hospitalId" :disabled="userStore.isHospitalAdmin" filterable placeholder="请选择医院" style="width: 100%">
            <el-option v-for="item in unitOptions" :key="item.id" :label="item.name" :value="String(item.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-input v-model="formData.department" placeholder="请输入科室" />
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="formData.professional" placeholder="请选择职称" clearable style="width: 100%">
            <el-option label="助理医师" value="10040001" />
            <el-option label="医师" value="10040002" />
            <el-option label="主治医师" value="10040003" />
            <el-option label="副主任医师" value="10040004" />
            <el-option label="主任医师" value="10040005" />
          </el-select>
        </el-form-item>
      </el-form>
    </FormDialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import DataTable from '@/components/DataTable.vue'
import FormDialog from '@/components/FormDialog.vue'
import { useUserStore } from '@/stores/user'
import { getUserList, createUser, updateUser, updateUserStatus, resetUserPassword, deleteUser } from '@/api/user'
import { getUnitOptions } from '@/api/unit'

const userStore = useUserStore()

// ==================== 查询 ====================
const queryForm = reactive({
  keyword: '',
  roleCode: '',
  hospitalId: '',
  isActive: null,
  pageNum: 1,
  pageSize: 10
})
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const unitOptions = ref([])

// ==================== 表单 ====================
const formVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const editingId = ref(null)

const formData = reactive({
  username: '',
  realName: '',
  sex: '',
  phone: '',
  email: '',
  roleCode: 'doctor',
  hospitalId: '',
  department: '',
  professional: ''
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// ==================== 生命周期 ====================
onMounted(() => {
  loadData()
  loadUnitOptions()
})

// ==================== 方法 ====================
async function loadData() {
  loading.value = true
  try {
    const res = await getUserList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

async function loadUnitOptions() {
  try {
    const res = await getUnitOptions()
    unitOptions.value = res.data || []
  } catch (e) {
    // 忽略
  }
}

function handleSearch() { queryForm.pageNum = 1; loadData() }
function handleReset() { queryForm.pageNum = 1; loadData() }

function getRoleTagType(roleCode) {
  const map = { super_admin: 'danger', hospital_admin: 'warning', doctor: '' }
  return map[roleCode] || 'info'
}

function handleAdd() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { username: '', realName: '', sex: '', phone: '', email: '', roleCode: 'doctor', hospitalId: '', department: '', professional: '' })
  if (userStore.isHospitalAdmin) {
    formData.roleCode = 'doctor'
    formData.hospitalId = userStore.hospitalId
  }
  formVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    username: row.username,
    realName: row.realName,
    sex: row.sex,
    phone: row.phone,
    email: row.email,
    roleCode: row.roleCode,
    hospitalId: row.hospitalId,
    department: row.department,
    professional: row.professional
  })
  formVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateUser(editingId.value, formData)
      ElMessage.success('编辑成功')
    } else {
      const res = await createUser(formData)
      ElMessage.success(`新增成功，初始密码：${res.data?.password || ''}`)
    }
    formVisible.value = false
    loadData()
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    submitLoading.value = false
  }
}

async function handleResetPassword(row) {
  try {
    await ElMessageBox.confirm(`确定要重置用户「${row.realName}」的密码吗？`, '重置密码', { type: 'warning' })
    const res = await resetUserPassword(row.id)
    ElMessageBox.alert(`新密码为：${res.data?.password || ''}，请妥善保管。`, '密码已重置', { type: 'success' })
  } catch (e) { /* 取消 */ }
}

async function handleToggleStatus(row) {
  const action = row.isActive ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}用户「${row.realName}」吗？`, `${action}用户`, { type: 'warning' })
    await updateUserStatus(row.id, !row.isActive)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (e) { /* 取消 */ }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.realName}」吗？此操作不可恢复。`, '删除用户', { type: 'error', confirmButtonText: '确定删除' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* 取消 */ }
}
</script>