<template>
  <div class="unit-list">
    <PageHeader title="单位管理">
      <template #actions>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增机构
        </el-button>
      </template>
    </PageHeader>

    <!-- 搜索区 -->
    <SearchForm v-model="queryForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="名称/编码" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
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
      <el-table-column prop="unitName" label="机构名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="unitCode" label="机构编码" width="120" />
      <el-table-column prop="unitLevel" label="级别" width="100" />
      <el-table-column prop="contactName" label="联系人" width="100" />
      <el-table-column prop="contactPhone" label="联系电话" width="130" />
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'danger' : 'success'" size="small" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </DataTable>

    <!-- 新增/编辑抽屉 -->
    <FormDialog
      v-model="formVisible"
      :title="isEdit ? '编辑机构' : '新增机构'"
      :is-edit="isEdit"
      :submit-loading="submitLoading"
      @submit="handleSubmit"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="机构名称" prop="unitName">
          <el-input v-model="formData.unitName" placeholder="请输入机构名称" />
        </el-form-item>
        <el-form-item label="机构简称">
          <el-input v-model="formData.unitShortName" placeholder="请输入机构简称" />
        </el-form-item>
        <el-form-item label="机构编码">
          <el-input v-model="formData.unitCode" placeholder="请输入机构编码" />
        </el-form-item>

        <el-divider content-position="left">分类信息</el-divider>
        <el-form-item label="机构级别">
          <el-select v-model="formData.unitLevel" placeholder="请选择" clearable style="width: 100%">
            <el-option label="三级甲等" value="三级甲等" />
            <el-option label="三级乙等" value="三级乙等" />
            <el-option label="二级甲等" value="二级甲等" />
            <el-option label="二级乙等" value="二级乙等" />
            <el-option label="一级" value="一级" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="机构类型">
          <el-select v-model="formData.unitType" placeholder="请选择" clearable style="width: 100%">
            <el-option label="综合医院" value="综合医院" />
            <el-option label="专科医院" value="专科医院" />
            <el-option label="社区卫生服务中心" value="社区卫生服务中心" />
            <el-option label="妇幼保健院" value="妇幼保健院" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-divider content-position="left">联系信息</el-divider>
        <el-form-item label="联系人">
          <el-input v-model="formData.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系地址">
          <el-input v-model="formData.contactAddress" placeholder="请输入联系地址" />
        </el-form-item>

        <el-divider content-position="left">备注</el-divider>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { getUnitList, createUnit, updateUnit, updateUnitStatus, deleteUnit } from '@/api/unit'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// ==================== 查询 ====================
const queryForm = reactive({ keyword: '', status: null, pageNum: 1, pageSize: 10 })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// ==================== 表单 ====================
const formVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const editingId = ref(null)

const formData = reactive({
  unitName: '', unitShortName: '', unitCode: '', unitLevel: '', unitType: '',
  contactName: '', contactPhone: '', contactAddress: '', remark: ''
})

const formRules = {
  unitName: [{ required: true, message: '请输入机构名称', trigger: 'blur' }]
}

onMounted(() => { loadData() })

async function loadData() {
  loading.value = true
  try {
    const res = await getUnitList(queryForm)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {} finally { loading.value = false }
}

function handleSearch() { queryForm.pageNum = 1; loadData() }
function handleReset() { queryForm.pageNum = 1; loadData() }

function handleAdd() {
  isEdit.value = false; editingId.value = null
  Object.assign(formData, { unitName: '', unitShortName: '', unitCode: '', unitLevel: '', unitType: '', contactName: '', contactPhone: '', contactAddress: '', remark: '' })
  formVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true; editingId.value = row.id
  Object.assign(formData, { unitName: row.unitName, unitShortName: row.unitShortName, unitCode: row.unitCode, unitLevel: row.unitLevel, unitType: row.unitType, contactName: row.contactName, contactPhone: row.contactPhone, contactAddress: row.contactAddress, remark: row.remark })
  formVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) { await updateUnit(editingId.value, formData); ElMessage.success('编辑成功') }
    else { await createUnit(formData); ElMessage.success('新增成功') }
    formVisible.value = false; loadData()
  } catch (e) {} finally { submitLoading.value = false }
}

async function handleToggleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  const msg = row.status === 1 ? '禁用机构将同时禁用该机构下所有用户账号，是否继续？' : `确定要启用机构「${row.unitName}」吗？`
  try {
    await ElMessageBox.confirm(msg, `${action}机构`, { type: 'warning' })
    await updateUnitStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success(`${action}成功`); loadData()
  } catch (e) {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除机构「${row.unitName}」吗？`, '删除机构', { type: 'error', confirmButtonText: '确定删除' })
    await deleteUnit(row.id); ElMessage.success('删除成功'); loadData()
  } catch (e) {}
}
</script>