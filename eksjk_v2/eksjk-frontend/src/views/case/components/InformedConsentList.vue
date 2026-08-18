<template>
  <div class="informed-consent-list">
    <div class="tab-pane-header">
      <el-tag type="info" size="small" effect="plain">来源：E路童萌</el-tag>
    </div>

    <!-- 文件列表 -->
    <div v-if="files.length > 0" class="consent-section">
      <div v-for="file in files" :key="file.id" class="consent-item">
        <el-icon><Document /></el-icon>
        <div class="consent-info">
          <div class="consent-name" :title="file.fileName">{{ file.fileName }}</div>
          <div class="consent-meta">
            <span>签署时间：{{ formatTime(file.signTime) }}</span>
            <span v-if="file.patientName">患者：{{ file.patientName }}</span>
            <span v-if="file.doctorName">医生：{{ file.doctorName }}</span>
          </div>
        </div>
        <span class="consent-source">E路童萌</span>
        <el-button link type="primary" size="small" @click="downloadFile(file)">查看/下载</el-button>
      </div>
    </div>
    <el-empty v-else-if="!loading" description="暂无知情同意书" :image-size="60" />

    <!-- 提示：不能手动上传 -->
    <el-alert v-if="files.length > 0" title="知情同意书由 E路童萌 微信端同步，仅支持查看，不可手动上传"
              type="info" :closable="false" show-icon style="margin-top:12px" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { getInformedConsentList } from '@/api/informedConsent'

const props = defineProps({
  patientId: { type: String, default: '' }
})

const files = ref([])
const loading = ref(false)

onMounted(() => { if (props.patientId) loadList() })

watch(() => props.patientId, (val) => { if (val) loadList() })

async function loadList() {
  loading.value = true
  try {
    const res = await getInformedConsentList(props.patientId)
    files.value = res.data || []
  } catch (error) {
    console.error('加载知情同意书失败', error)
  } finally {
    loading.value = false
  }
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

function downloadFile(file) {
  if (file && file.ossUrl) window.open(file.ossUrl, '_blank')
}
</script>

<style scoped>
.tab-pane-header { display: flex; justify-content: flex-end; margin-bottom: 12px; }
.consent-section { margin-bottom: 12px; }
.consent-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 8px;
}
.consent-item .el-icon { color: #409eff; font-size: 18px; }
.consent-info { flex: 1; overflow: hidden; }
.consent-name {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.consent-meta {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.consent-source {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}
</style>
