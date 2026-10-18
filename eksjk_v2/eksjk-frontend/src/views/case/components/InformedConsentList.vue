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
          <div class="consent-name" :title="displayName(file)">{{ displayName(file) }}</div>
          <div class="consent-meta">
            <span>签署时间：{{ formatTime(file.signTime || file.patientSignTime) || '—' }}</span>
            <span v-if="file.patientName">患者：{{ file.patientName }}</span>
            <span v-if="file.doctorName">医生：{{ file.doctorName }}</span>
            <span v-if="signStatusText(file)">状态：{{ signStatusText(file) }}</span>
          </div>
        </div>
        <span class="consent-source">E路童萌</span>
        <el-button link type="primary" size="small" :disabled="false" @click="openDetail(file)">详情</el-button>
        <el-button link type="primary" size="small" :disabled="!fileUrl(file)" @click="downloadFile(file)">
          查看/下载
        </el-button>
      </div>
    </div>
    <el-empty v-else-if="!loading" description="暂无知情同意书" :image-size="60" />

    <!-- 提示：不能手动上传 -->
    <el-alert v-if="files.length > 0" title="知情同意书由 E路童萌 微信端同步，仅支持查看，不可手动上传"
              type="info" :closable="false" show-icon style="margin-top:12px" />

    <el-drawer v-model="detailVisible" title="知情同意书详情" size="70%">
      <div v-if="currentFile" class="consent-detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="文件名称" :span="2">{{ displayName(currentFile) }}</el-descriptions-item>
          <el-descriptions-item label="患者">{{ currentFile.patientName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="医生">{{ currentFile.doctorName || '—' }}</el-descriptions-item>
          <el-descriptions-item label="患者签署时间">{{ formatTime(currentFile.patientSignTime) || '—' }}</el-descriptions-item>
          <el-descriptions-item label="医生签署时间">{{ formatTime(currentFile.doctorSignTime) || '—' }}</el-descriptions-item>
          <el-descriptions-item label="签署状态">{{ signStatusText(currentFile) || '—' }}</el-descriptions-item>
          <el-descriptions-item label="PDF 状态">{{ pdfStatusText(currentFile) }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="currentFile.content" class="detail-section">
          <div class="section-title">知情同意书正文</div>
          <pre class="consent-content">{{ stripHtml(currentFile.content) }}</pre>
        </div>
        <el-empty v-else description="正文尚未补拉，请稍后点击同步或联系管理员" :image-size="50" />

        <div v-if="signImages.length > 0" class="detail-section">
          <div class="section-title">签名图片</div>
          <div class="sign-images">
            <div v-for="item in signImages" :key="item.label" class="sign-item">
              <div class="sign-label">{{ item.label }}</div>
              <el-image
                class="sign-image"
                :src="item.url"
                :preview-src-list="[item.url]"
                fit="contain"
                preview-teleported
              />
            </div>
          </div>
        </div>

        <el-alert
          v-if="!fileUrl(currentFile)"
          title="该记录已同步签署信息，但 PDF 文件尚未生成或补拉"
          type="warning"
          :closable="false"
          show-icon
          style="margin-top:16px"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { Document } from '@element-plus/icons-vue'
import { getInformedConsentList } from '@/api/informedConsent'
import { fetchFileBlobUrl } from '@/api/file'

const props = defineProps({
  patientId: { type: String, default: '' }
})

const files = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const currentFile = ref(null)

const signImages = computed(() => {
  const file = currentFile.value
  if (!file) return []
  return [
    { label: '患者签名', url: imageUrl(file.patientSignImage) },
    { label: '监护人签名', url: imageUrl(file.guardianSignImage) },
    { label: '医生签名', url: imageUrl(file.doctorSignImage) }
  ].filter(item => item.url)
})

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

function displayName(file) {
  return file?.fileName || file?.pdfFileName || '知情同意书'
}

function fileUrl(file) {
  return file?.ossUrl || file?.pdfPath || ''
}

function imageUrl(value) {
  if (!value) return ''
  return /^(https?:|data:image\/)/i.test(value) ? value : ''
}

function signStatusText(file) {
  if (!file) return ''
  if (file.signTime || file.patientSignTime) return '已签署'
  return file.signStatus ? `状态码 ${file.signStatus}` : ''
}

function pdfStatusText(file) {
  if (!file) return '—'
  if (fileUrl(file)) return '已生成'
  const map = { '0': '待生成', '1': '已生成', '2': '生成失败', '3': '已跳过' }
  return map[file.pdfStatus] || '待生成'
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  if (Number.isNaN(d.getTime())) return ''
  return d.toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

function stripHtml(value) {
  if (!value) return ''
  return String(value)
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/(p|div|li|tr|h[1-6])>/gi, '\n')
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/g, ' ')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&amp;/g, '&')
    .replace(/\n{3,}/g, '\n\n')
    .trim()
}

function openDetail(file) {
  currentFile.value = file
  detailVisible.value = true
}

async function downloadFile(file) {
  const url = fileUrl(file)
  if (!url) return
  if (/^https?:\/\//i.test(url)) {
    window.open(url, '_blank')
    return
  }
  try {
    const blobUrl = await fetchFileBlobUrl(url)
    const link = document.createElement('a')
    link.href = blobUrl
    link.download = displayName(file) || '知情同意书'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    setTimeout(() => URL.revokeObjectURL(blobUrl), 1000)
  } catch (e) {
    window.open(url, '_blank')
  }
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
.consent-detail { padding-bottom: 24px; }
.detail-section { margin-top: 24px; }
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}
.consent-content {
  max-height: 420px;
  overflow: auto;
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}
.sign-images { display: flex; gap: 24px; flex-wrap: wrap; }
.sign-item { text-align: center; }
.sign-label { font-size: 12px; color: #909399; margin-bottom: 8px; }
.sign-image {
  width: 180px;
  height: 90px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fff;
}
</style>
