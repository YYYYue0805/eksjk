<template>
  <div class="eltm-pics-tab" v-loading="loading">
    <el-alert title="数据来源：E路童萌，图片地址为外部系统链接，点击可查看原图" type="info"
              :closable="false" show-icon class="source-tip" />

    <div v-if="records.length > 0" class="pic-grid">
      <el-card v-for="pic in records" :key="pic.id" shadow="hover" class="pic-card">
        <el-image
          class="pic-image"
          :src="pic.imageUrl"
          :preview-src-list="[pic.imageUrl]"
          fit="cover"
          preview-teleported
          hide-on-click-modal
        >
          <template #error>
            <div class="image-error">图片无法加载</div>
          </template>
        </el-image>
        <div class="pic-body">
          <div class="pic-name" :title="pic.recordName">{{ pic.recordName || pic.typeContent || '未命名资料' }}</div>
          <div class="pic-meta">
            <span>{{ imageTypeText(pic.imageType) }}</span>
            <span v-if="pic.recordDate">{{ formatDate(pic.recordDate) }}</span>
          </div>
          <div v-if="pic.remark" class="pic-remark" :title="pic.remark">{{ pic.remark }}</div>
          <div class="pic-actions">
            <el-button link type="primary" size="small" :disabled="false" @click="openImage(pic.imageUrl)">打开原图</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-empty v-if="!loading && records.length === 0" description="暂无检查资料/病历图片" />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { getEltmPics } from '@/api/eltm'

const props = defineProps({
  patientId: { type: String, required: true }
})

const loading = ref(false)
const records = ref([])

const IMAGE_TYPES = {
  0: '检验',
  1: '检查',
  2: '影像',
  3: '病历',
  4: '处方',
  5: '其它',
  6: '出院小结'
}

function imageTypeText(type) {
  return IMAGE_TYPES[type] || '未分类'
}

function formatDate(value) {
  if (!value) return ''
  return new Date(value).toLocaleDateString('zh-CN')
}

function openImage(url) {
  if (url) window.open(url, '_blank')
}

async function loadData() {
  loading.value = true
  try {
    const res = await getEltmPics(props.patientId)
    records.value = res.data || []
  } catch (error) {
    console.error('加载病历图片失败', error)
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
.pic-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}
.pic-card :deep(.el-card__body) { padding: 0; }
.pic-image { width: 100%; height: 180px; display: block; background: #f5f7fa; }
.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 13px;
}
.pic-body { padding: 12px; }
.pic-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.pic-meta {
  display: flex;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
.pic-remark {
  margin-top: 6px;
  font-size: 12px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.pic-actions { margin-top: 8px; text-align: right; }
</style>
