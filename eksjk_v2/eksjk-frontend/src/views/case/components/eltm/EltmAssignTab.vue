<template>
  <div class="eltm-assign-tab" v-loading="loading">
    <el-alert title="数据来源：E路童萌，只读展示" type="info" :closable="false" show-icon class="source-tip" />

    <div class="section-title">所属项目</div>
    <el-table :data="projects" border stripe size="small" style="width: 100%">
      <el-table-column prop="projectName" label="项目名称" min-width="300" show-overflow-tooltip />
      <el-table-column prop="externalProjectId" label="外部项目ID" width="180" show-overflow-tooltip />
      <el-table-column prop="relationSource" label="关联来源" width="160" show-overflow-tooltip />
      <el-table-column prop="firstRelationTime" label="首次关联时间" width="170" />
    </el-table>
    <el-empty v-if="!loading && projects.length === 0" description="暂无项目数据" :image-size="50" />

    <div class="section-title with-gap">医院与医生绑定</div>
    <el-table :data="hospitalBinds" border stripe size="small" style="width: 100%">
      <el-table-column prop="hospitalName" label="医院" min-width="220" show-overflow-tooltip />
      <el-table-column prop="doctorName" label="医生" width="120" show-overflow-tooltip />
      <el-table-column label="地区" min-width="180">
        <template #default="{ row }">
          {{ [row.province, row.city, row.district].filter(Boolean).join(' / ') || '—' }}
        </template>
      </el-table-column>
      <el-table-column prop="bindSource" label="绑定来源" width="140" show-overflow-tooltip />
      <el-table-column prop="externalHospitalId" label="外部医院ID" width="160" show-overflow-tooltip />
    </el-table>
    <el-empty v-if="!loading && hospitalBinds.length === 0" description="暂无医院绑定数据" :image-size="50" />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { getEltmProjects } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const loading = ref(false)
const projects = ref([])
const hospitalBinds = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getEltmProjects(props.patientId)
    const data = res.data || {}
    projects.value = data.projects || []
    hospitalBinds.value = data.hospitalBinds || []
  } catch (error) {
    console.error('加载项目与医院绑定失败', error)
  } finally {
    loading.value = false
  }
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
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}
.with-gap { margin-top: 28px; }
</style>
