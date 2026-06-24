<template>
  <div class="file-upload">
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :data="{ patientId: patientId, category: 'image' }"
      :on-success="handleUploadSuccess"
      :on-error="handleUploadError"
      :before-upload="beforeUpload"
      :show-file-list="false"
      list-type="picture-card"
      :disabled="disabled"
      multiple
      accept="image/*,.dcm,.dicom,.pdf"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="[previewUrl]"
      @close="previewVisible = false"
    />

    <!-- 文件列表 -->
    <div class="file-grid" v-if="files.length > 0">
      <div v-for="file in files" :key="file.path" class="file-item"
           @click="handlePreview(file)">
        <div class="file-thumb">
          <template v-if="isImage(file.type)">
            <img v-if="blobUrls[file.path]" :src="blobUrls[file.path]" class="thumb-img" />
            <div v-else class="thumb-loading">
              <el-icon :size="24" color="#c0c4cc"><Picture /></el-icon>
            </div>
          </template>
          <el-icon v-else-if="file.isDicom" :size="40" color="#409eff"><Monitor /></el-icon>
          <el-icon v-else :size="40" color="#909399"><Document /></el-icon>
        </div>
        <div class="file-name" :title="file.name">{{ file.name }}</div>
        <div class="file-note" @click.stop>
          <template v-if="editingNotePath === file.path">
            <el-input
              v-model="editingNoteText"
              size="small"
              placeholder="添加备注"
              @blur="handleNoteBlur(file)"
              @keyup.enter="handleNoteBlur(file)"
              maxlength="200"
            />
          </template>
          <template v-else>
            <span
              class="note-text"
              :class="{ 'note-placeholder': !file.note }"
              @click="handleNoteEdit(file)"
            >{{ file.note || '添加备注' }}</span>
          </template>
        </div>
        <div class="file-actions" v-if="!disabled">
          <el-button link type="primary" size="small" @click.stop="handleDownload(file)">下载</el-button>
          <el-popconfirm title="确定删除该文件吗？" @confirm="handleDelete(file)">
            <template #reference>
              <el-button link type="danger" size="small" @click.stop>删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </div>
    <el-empty v-else-if="!loading" description="暂无影像资料" :image-size="60" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Monitor, Document, Picture } from '@element-plus/icons-vue'
import { getFileList, deleteFile, getFileDownloadUrl, fetchFileBlobUrl, updateFileNote } from '@/api/file'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  patientId: { type: String, required: true },
  disabled: { type: Boolean, default: false }
})

const userStore = useUserStore()
const files = ref([])
const fileList = ref([])
const loading = ref(false)
const previewVisible = ref(false)
const previewUrl = ref('')

// 备注编辑状态
const editingNotePath = ref(null)
const editingNoteText = ref('')

// blob URL 缓存，key 为文件 path，避免重复请求
const blobUrls = ref({})

// 上传配置 - 使用 computed 动态获取 token，避免 onMounted 时 token 未就绪
const uploadUrl = '/api/files/upload'
const uploadHeaders = computed(() => {
  return userStore.token ? { 'satoken': userStore.token } : {}
})

onMounted(() => {
  if (props.patientId) loadFiles()
})

onUnmounted(() => {
  // 释放所有 blob URL，防止内存泄漏
  Object.values(blobUrls.value).forEach(url => URL.revokeObjectURL(url))
})

watch(() => props.patientId, (val) => {
  if (val) loadFiles()
})

async function loadFiles() {
  loading.value = true
  try {
    const res = await getFileList(props.patientId)
    files.value = res.data || []
    // 清除旧的 blob URL 缓存
    Object.values(blobUrls.value).forEach(url => URL.revokeObjectURL(url))
    blobUrls.value = {}
    // 异步加载图片 blob URL，逐个加载完成后响应式更新
    files.value.forEach(file => {
      if (isImage(file.type)) {
        fetchFileBlobUrl(file.path).then(url => {
          blobUrls.value = { ...blobUrls.value, [file.path]: url }
        }).catch(() => {})
      }
    })
  } catch (error) {
    console.error('加载文件列表失败', error)
  } finally {
    loading.value = false
  }
}

function beforeUpload(file) {
  const maxSize = 50 * 1024 * 1024 // 50MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过50MB')
    return false
  }
  return true
}

function handleUploadSuccess(response) {
  if (response.code === 200) {
    ElMessage.success('上传成功')
    loadFiles()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function handleUploadError(err) {
  console.error('上传错误:', err)
  ElMessage.error('上传失败，请检查文件大小或网络连接')
}

function handlePreview(file) {
  if (isImage(file.type)) {
    const blobUrl = blobUrls.value[file.path]
    if (blobUrl) {
      previewUrl.value = blobUrl
      previewVisible.value = true
    } else {
      // 未缓存时实时获取
      fetchFileBlobUrl(file.path).then(url => {
        blobUrls.value[file.path] = url
        previewUrl.value = url
        previewVisible.value = true
      }).catch(() => ElMessage.error('图片加载失败'))
    }
  } else if (file.isDicom) {
    ElMessage.info('DICOM影像查看器将在后续版本中集成')
  } else {
    handleDownload(file)
  }
}

function handleDownload(file) {
  const url = getFileDownloadUrl(file.path)
  window.open(url, '_blank')
}

async function handleDelete(file) {
  try {
    await deleteFile(file.path)
    ElMessage.success('删除成功')
    // 释放对应的 blob URL
    if (blobUrls.value[file.path]) {
      URL.revokeObjectURL(blobUrls.value[file.path])
      delete blobUrls.value[file.path]
    }
    loadFiles()
  } catch (error) {
    console.error('删除失败', error)
  }
}

function handleNoteEdit(file) {
  editingNotePath.value = file.path
  editingNoteText.value = file.note || ''
  nextTick(() => {
    const input = document.querySelector('.file-note .el-input__inner')
    if (input) input.focus()
  })
}

async function handleNoteBlur(file) {
  const newNote = editingNoteText.value.trim()
  try {
    await updateFileNote(file.path, newNote)
    file.note = newNote || null
  } catch (error) {
    console.error('保存备注失败', error)
    ElMessage.error('保存备注失败')
  } finally {
    editingNotePath.value = null
    editingNoteText.value = ''
  }
}

function isImage(type) {
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp'].includes(type?.toLowerCase())
}

function getThumbUrl(file) {
  return blobUrls.value[file.path] || ''
}
</script>

<style scoped>
.thumb-loading {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  margin-top: 16px;
}
.file-item {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 8px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}
.file-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}
.file-thumb {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}
.thumb-img {
  width: 100%;
  height: 100%;
}
.file-name {
  font-size: 12px;
  color: #606266;
  margin-top: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.file-actions {
  margin-top: 4px;
}
.file-note {
  margin-top: 2px;
  min-height: 22px;
}
.note-text {
  font-size: 11px;
  color: #606266;
  cursor: text;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
  display: inline-block;
}
.note-placeholder {
  color: #c0c4cc;
}
</style>
