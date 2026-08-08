<template>
  <div class="genetics-exam-form">
    <!-- 4. 基因检测方法 -->
    <div class="section-title">基因检测方法</div>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="基因检测方法">
          <el-checkbox-group v-model="geneTestMethodChecked" :disabled="disabled">
            <el-checkbox value="先证者WES">先证者WES</el-checkbox>
            <el-checkbox value="Trio-WES">Trio-WES</el-checkbox>
            <el-checkbox value="CNV测序">CNV测序</el-checkbox>
            <el-checkbox value="CMA">CMA</el-checkbox>
            <el-checkbox value="WGS">WGS</el-checkbox>
            <el-checkbox value="其他">其他</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row v-if="geneTestMethodChecked.includes('其他')" :gutter="16">
      <el-col :span="12">
        <el-form-item label="其他检测方法">
          <el-input v-model="geneTestMethodOther" :disabled="disabled" placeholder="请输入其他检测方法" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 5. 基因检测结果 -->
    <div class="section-title">基因检测结果</div>
    <el-row :gutter="16">
      <el-col :span="24">
        <el-form-item label="基因检测结果">
          <el-radio-group v-model="localData.geneTestResult" :disabled="disabled">
            <el-radio value="阴性">阴性</el-radio>
            <el-radio value="阳性">阳性</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 6. 基因突变检测表 -->
    <div class="section-title" v-if="showGeneTable">基因突变检测</div>
    <div v-if="showGeneTable">
      <el-table :data="localData.genData" border stripe size="small">
        <el-table-column prop="geneName" label="基因名称" min-width="100">
          <template #default="{ row }"><el-input v-model="row.geneName" :disabled="disabled" placeholder="基因名称" size="small" /></template>
        </el-table-column>
        <el-table-column prop="rna" label="核酸变异" min-width="100">
          <template #default="{ row }"><el-input v-model="row.rna" :disabled="disabled" placeholder="核酸变异" size="small" /></template>
        </el-table-column>
        <el-table-column prop="mutationType" label="突变类型" width="130">
          <template #default="{ row }">
            <el-select v-model="row.mutationType" :disabled="disabled" placeholder="选择" size="small" clearable>
              <el-option label="杂合突变" value="1" />
              <el-option label="纯合突变" value="2" />
              <el-option label="半合子突变" value="3" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="other" label="其他" min-width="80">
          <template #default="{ row }"><el-input v-model="row.other" :disabled="disabled" placeholder="其他" size="small" /></template>
        </el-table-column>
        <el-table-column prop="pathogenicity" label="致病等级" width="150">
          <template #default="{ row }">
            <el-select v-model="row.pathogenicity" :disabled="disabled" placeholder="评级" size="small" clearable>
              <el-option label="P（致病）" value="1" />
              <el-option label="LP（可能致病）" value="2" />
              <el-option label="VUS（意义不明确）" value="3" />
              <el-option label="LB（可能良性）" value="4" />
              <el-option label="B（良性）" value="5" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="amino" label="氨基酸变异" min-width="100">
          <template #default="{ row }"><el-input v-model="row.amino" :disabled="disabled" placeholder="氨基酸变异" size="small" /></template>
        </el-table-column>
        <el-table-column prop="father" label="父亲" width="130">
          <template #default="{ row }">
            <el-select v-model="row.father" :disabled="disabled" placeholder="选择" size="small" clearable>
              <el-option label="野生型" value="1" />
              <el-option label="杂合突变" value="2" />
              <el-option label="纯合突变" value="3" />
              <el-option label="半合子突变" value="4" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="mother" label="母亲" width="130">
          <template #default="{ row }">
            <el-select v-model="row.mother" :disabled="disabled" placeholder="选择" size="small" clearable>
              <el-option label="野生型" value="1" />
              <el-option label="杂合突变" value="2" />
              <el-option label="纯合突变" value="3" />
              <el-option label="半合子突变" value="4" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column v-for="col in localData.genCustomColumns" :key="col" :label="col" min-width="120">
          <template #header>
            <span style="display:flex;align-items:center;gap:4px">
              <span>{{ col }}</span>
              <el-icon v-if="!disabled" class="remove-col-icon" @click="removeCustomColumn(col)"><Close /></el-icon>
            </span>
          </template>
          <template #default="{ row }">
            <el-input v-model="row.customFields[col]" :disabled="disabled" :placeholder="col" size="small" />
          </template>
        </el-table-column>
        <el-table-column v-if="!disabled" label="操作" width="70" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" size="small" text @click="removeGene($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!disabled" style="margin-top:8px;display:flex;gap:8px">
        <el-button type="primary" size="small" @click="addGene">
          <el-icon><Plus /></el-icon>新增基因
        </el-button>
        <el-button type="primary" size="small" plain @click="addCustomColumn">
          <el-icon><Plus /></el-icon>新增列
        </el-button>
      </div>
    </div>

    <!-- 5. MAS 遗传学检查及病理检查 -->
    <div class="section-title" v-if="showMasGenetics">遗传学检查及病理检查</div>
    <template v-if="showMasGenetics">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="GNAS基因测定检查">
            <el-select v-model="localData.gnas" :disabled="disabled" placeholder="请选择" clearable>
              <el-option label="是" value="1" />
              <el-option label="否" value="2" />
              <el-option label="不详" value="3" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="localData.gnas !== '2' && localData.gnas" :gutter="16">
        <el-col :span="12">
          <el-form-item label="标本采样类型或部位">
            <el-select v-model="localData.gnasSamLoc" :disabled="disabled" placeholder="请选择" clearable>
              <el-option label="外周血" value="1" />
              <el-option label="病变组织" value="2" />
              <el-option label="囊肿穿刺液" value="3" />
              <el-option label="其他" value="4" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="遗传学检测方法">
            <el-input v-model="localData.genTesMet" :disabled="disabled" placeholder="检测方法" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="检测结果">
            <el-input v-model="localData.detRes" :disabled="disabled" placeholder="检测结果" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="检测版本">
            <el-input v-model="localData.detVer" :disabled="disabled" placeholder="检测版本" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="突变位点">
            <el-input v-model="localData.mutSit" :disabled="disabled" placeholder="突变位点" />
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- 7. 基因结果 / 染色体结果 / 染色体核型（所有病种统一显示，对齐 ELTM 设计） -->
    <div class="section-title">基因结果</div>
    <div class="report-section">
      <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: 'E路童萌基因检测报告' }"
                 :on-success="onEltmGeneReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                 :show-file-list="false" :disabled="disabled" accept="image/*,.pdf" multiple>
        <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>基因结果上传</el-button>
      </el-upload>
      <div v-for="(file, idx) in geneResultFiles" :key="file.path" class="report-info">
        <el-icon><Document /></el-icon>
        <span class="report-name" :title="file.name">{{ file.name }}</span>
        <el-button link type="primary" size="small" @click="downloadFile(file)">下载</el-button>
        <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('gene', idx)">删除</el-button>
      </div>
    </div>
    <div class="section-title" style="margin-top:16px">染色体结果</div>
    <div class="report-section">
      <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: 'E路童萌染色体检查报告' }"
                 :on-success="onEltmChromReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                 :show-file-list="false" :disabled="disabled" accept="image/*,.pdf" multiple>
        <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>染色体检查报告</el-button>
      </el-upload>
      <div v-for="(file, idx) in chromResultFiles" :key="file.path" class="report-info">
        <el-icon><Document /></el-icon>
        <span class="report-name" :title="file.name">{{ file.name }}</span>
        <el-button link type="primary" size="small" @click="downloadFile(file)">下载</el-button>
        <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('chrom', idx)">删除</el-button>
      </div>
    </div>
    <el-form-item label="染色体核型">
      <el-checkbox-group v-model="chromChecked" :disabled="disabled">
        <el-checkbox label="正常核型">正常核型</el-checkbox>
        <el-checkbox label="21三体综合征">21三体综合征</el-checkbox>
        <el-checkbox label="特纳综合征">特纳综合征</el-checkbox>
        <el-checkbox label="克氏综合征">克氏综合征</el-checkbox>
        <el-checkbox label="染色体平衡易位">染色体平衡易位</el-checkbox>
        <el-checkbox label="染色体嵌合体">染色体嵌合体</el-checkbox>
        <el-checkbox label="其他异常核型">其他异常核型</el-checkbox>
      </el-checkbox-group>
      <el-input v-if="chromChecked.includes('其他异常核型')" v-model="localData.chromOther"
                :disabled="disabled" placeholder="请输入其他异常核型" style="width:300px;margin-top:8px" />
    </el-form-item>

    <!-- 8. 手术情况 -->
    <div class="section-title" v-if="showSurgeryPathology">手术情况</div>
    <el-row v-if="showSurgeryPathology" :gutter="16">
      <el-col :span="24">
        <el-form-item>
          <el-input v-model="localData.surgeryNote" type="textarea" :rows="3" :disabled="disabled" placeholder="请输入手术情况" maxlength="1500" show-word-limit />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 9. 病理结果 -->
    <div class="section-title" v-if="showSurgeryPathology">病理结果</div>
    <el-row v-if="showSurgeryPathology" :gutter="16">
      <el-col :span="24">
        <el-form-item>
          <el-input v-model="localData.pathologyResult" type="textarea" :rows="3" :disabled="disabled" placeholder="请输入病理结果" maxlength="1500" show-word-limit />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 10. 上传病理报告 -->
    <div v-if="showSurgeryPathology" class="report-section">
      <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: '病理报告' }"
                 :on-success="onPathologyReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                 :show-file-list="false" :disabled="disabled" accept="image/*,.pdf" multiple>
        <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>上传病理报告</el-button>
      </el-upload>
      <div v-for="(file, idx) in pathologyReportFiles" :key="file.path" class="report-info">
        <el-icon><Document /></el-icon>
        <span class="report-name" :title="file.name">{{ file.name }}</span>
        <el-button link type="primary" size="small" @click="downloadFile(file)">下载</el-button>
        <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('pathology', idx)">删除</el-button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive, computed, watch, ref, nextTick } from 'vue'
import { Plus, Document, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFileList, deleteFile, getFileDownloadUrl } from '@/api/file'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  disabled: { type: Boolean, default: false },
  diseaseType: { type: String, default: '' },
  patientId: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const uploadUrl = '/api/files/upload'
const uploadHeaders = computed(() => userStore.token ? { 'satoken': userStore.token } : {})

// 各疾病类型显示的区块
// 统一设计：所有病种遗传学检查对齐 ELTM 风格（基因结果/染色体结果上传 + 染色体核型多选）
const showGeneTable = computed(() => {
  if (localData.geneTestResult === '阳性') return true
  if (localData.genData && localData.genData.length > 0) return true
  return false
})
const showSurgeryPathology = computed(() => props.diseaseType === 'dsd')
const showMasGenetics = computed(() => props.diseaseType === 'mas')

const localData = reactive({
  karyotype: '',
  genData: [],
  genCustomColumns: [],
  biologBank: '',
  biologBankFa: '',
  biologBankMo: '',
  biologBankData: [],
  geneTestMethod: '',
  geneTestResult: '',
  surgeryNote: '',
  pathologyResult: '',
  gnas: '',
  gnasSamLoc: '',
  genTesMet: '',
  detRes: '',
  detVer: '',
  mutSit: '',
  geneMethod: '',
  geneRes: '',
  geneName: '',
  genePoint: '',
  geneType: '',
  geneMode: '',
  chrom: [],
  chromOther: ''
})

const chromChecked = computed({
  get: () => localData.chrom || [],
  set: (val) => { localData.chrom = val }
})

// 基因检测方法多选（逗号分隔存储）
const geneTestMethodChecked = computed({
  get: () => {
    const val = localData.geneTestMethod
    if (!val) return []
    const parts = val.split(',').filter(Boolean)
    return parts.map(p => p.startsWith('其他:') ? '其他' : p)
  },
  set: (vals) => {
    const result = vals.map(v => {
      if (v === '其他') {
        return geneTestMethodOther.value ? '其他:' + geneTestMethodOther.value : '其他:'
      }
      return v
    })
    localData.geneTestMethod = result.join(',')
    syncToParent()
  }
})

const geneTestMethodOther = computed({
  get: () => {
    const val = localData.geneTestMethod
    if (!val) return ''
    const customPart = val.split(',').find(p => p.startsWith('其他:'))
    return customPart ? customPart.substring(3) : ''
  },
  set: (val) => {
    const newVal = val ? '其他:' + val : '其他:'
    const parts = localData.geneTestMethod.split(',').filter(Boolean)
    const idx = parts.findIndex(p => p.startsWith('其他:'))
    if (idx >= 0) {
      parts[idx] = newVal
    } else {
      parts.push(newVal)
    }
    localData.geneTestMethod = parts.join(',')
    syncToParent()
  }
})

// 报告文件列表（支持多文件）
const geneResultFiles = ref([])
const chromResultFiles = ref([])
const pathologyReportFiles = ref([])

function beforeUpload(file) {
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) { ElMessage.error('文件大小不能超过50MB'); return false }
  return true
}

function onUploadError() { ElMessage.error('上传失败') }

function downloadFile(file) {
  if (file && file.path) window.open(getFileDownloadUrl(file.path), '_blank')
}

async function loadReportFiles() {
  if (!props.patientId) return
  try {
    const res = await getFileList(props.patientId)
    const files = res.data || []
    // 统一分类读取：兼容历史文件（旧的「基因检测报告」「染色体报告」分类）
    geneResultFiles.value = files.filter(f => f.category === 'E路童萌基因检测报告' || f.category === '基因检测报告')
    chromResultFiles.value = files.filter(f => f.category === 'E路童萌染色体检查报告' || f.category === '染色体报告')
    pathologyReportFiles.value = files.filter(f => f.category === '病理报告')
  } catch { /* ignore */ }
}

function makeReportSuccessHandler() {
  return (response) => {
    if (response.code === 200) {
      ElMessage.success('上传成功')
      loadReportFiles()
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  }
}

const onPathologyReportSuccess = makeReportSuccessHandler()
const onEltmGeneReportSuccess = makeReportSuccessHandler()
const onEltmChromReportSuccess = makeReportSuccessHandler()

async function deleteReport(type, idx) {
  const map = {
    gene: geneResultFiles,
    chrom: chromResultFiles,
    pathology: pathologyReportFiles
  }
  const files = map[type]
  if (!files?.value?.[idx]) return
  try {
    await deleteFile(files.value[idx].path)
    files.value.splice(idx, 1)
    ElMessage.success('删除成功')
  } catch { ElMessage.error('删除失败') }
}

function addGene() {
  const customFields = {}
  localData.genCustomColumns.forEach(col => { customFields[col] = '' })
  localData.genData.push({ geneName: '', rna: '', mutationType: '', other: '', pathogenicity: '', amino: '', father: '', mother: '', customFields })
  syncToParent()
}

function removeGene(index) {
  localData.genData.splice(index, 1)
  syncToParent()
}

const FIXED_COLUMNS = ['基因名称', '核酸变异', '突变类型', '其他', '致病等级', '氨基酸变异', '父亲', '母亲']

function addCustomColumn() {
  ElMessageBox.prompt('请输入自定义列名', '新增列', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValidator: (val) => {
      if (!val || !val.trim()) return '列名不能为空'
      if (FIXED_COLUMNS.includes(val.trim())) return '列名与已有列重复'
      if (localData.genCustomColumns.includes(val.trim())) return '列名已存在'
      return true
    }
  }).then(({ value }) => {
    const colName = value.trim()
    localData.genCustomColumns.push(colName)
    localData.genData.forEach(row => {
      if (!row.customFields) row.customFields = {}
      row.customFields[colName] = ''
    })
    syncToParent()
  }).catch(() => { /* 取消 */ })
}

function removeCustomColumn(colName) {
  localData.genCustomColumns = localData.genCustomColumns.filter(c => c !== colName)
  localData.genData.forEach(row => {
    if (row.customFields) delete row.customFields[colName]
  })
  syncToParent()
}

function normalizeGenData(data) {
  if (!data || !Array.isArray(data) || data.length === 0) return data
  const first = data[0]
  let result
  if (first.geneName !== undefined && first.sequencingMethod !== undefined && first.rna === undefined) {
    result = data.map(row => ({
      geneName: row.geneName || '',
      rna: row.mutationSite || '',
      mutationType: '',
      other: row.sequencingMethod || '',
      pathogenicity: row.pathogenicity || '',
      amino: '',
      father: '',
      mother: ''
    }))
  } else {
    result = data
  }
  // 确保每行都有 customFields
  result.forEach(row => {
    if (!row.customFields) row.customFields = {}
  })
  return result
}

// 防止父→子同步时触发子→父回写，避免级联更新导致页面卡顿
let syncingFromParent = false

watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    syncingFromParent = true
    if (val.karyotype !== undefined) localData.karyotype = val.karyotype
    if (val.biologBank !== undefined) localData.biologBank = val.biologBank
    if (val.biologBankFa !== undefined) localData.biologBankFa = val.biologBankFa
    if (val.biologBankMo !== undefined) localData.biologBankMo = val.biologBankMo
    if (val.genData && Array.isArray(val.genData)) localData.genData = normalizeGenData([...val.genData])
    if (val.genCustomColumns && Array.isArray(val.genCustomColumns)) localData.genCustomColumns = [...val.genCustomColumns]
    if (val.surgeryNote !== undefined) localData.surgeryNote = val.surgeryNote
    if (val.pathologyResult !== undefined) localData.pathologyResult = val.pathologyResult
    if (val.gnas !== undefined) localData.gnas = val.gnas
    if (val.gnasSamLoc !== undefined) localData.gnasSamLoc = val.gnasSamLoc
    if (val.genTesMet !== undefined) localData.genTesMet = val.genTesMet
    if (val.detRes !== undefined) localData.detRes = val.detRes
    if (val.detVer !== undefined) localData.detVer = val.detVer
    if (val.mutSit !== undefined) localData.mutSit = val.mutSit
    if (val.geneTestMethod !== undefined) localData.geneTestMethod = val.geneTestMethod
    if (val.geneTestResult !== undefined) localData.geneTestResult = val.geneTestResult
    if (val.geneMethod !== undefined) localData.geneMethod = val.geneMethod
    if (val.geneRes !== undefined) localData.geneRes = val.geneRes
    if (val.geneName !== undefined) localData.geneName = val.geneName
    if (val.genePoint !== undefined) localData.genePoint = val.genePoint
    if (val.geneType !== undefined) localData.geneType = val.geneType
    if (val.geneMode !== undefined) localData.geneMode = val.geneMode
    if (val.chrom !== undefined) {
      if (Array.isArray(val.chrom)) localData.chrom = [...val.chrom]
      else if (typeof val.chrom === 'string') { try { localData.chrom = JSON.parse(val.chrom) } catch {} }
    }
    if (val.chromOther !== undefined) localData.chromOther = val.chromOther
    nextTick(() => { syncingFromParent = false })
  }
}, { immediate: true, deep: true })

watch(localData, () => {
  if (syncingFromParent) return
  syncToParent()
}, { deep: true })

function syncToParent() {
  emit('update:modelValue', {
    karyotype: localData.karyotype,
    biologBank: localData.biologBank,
    biologBankFa: localData.biologBankFa,
    biologBankMo: localData.biologBankMo,
    geneTestMethod: localData.geneTestMethod,
    geneTestResult: localData.geneTestResult,
    genData: [...localData.genData],
    genCustomColumns: [...localData.genCustomColumns],
    surgeryNote: localData.surgeryNote,
    pathologyResult: localData.pathologyResult,
    gnas: localData.gnas,
    gnasSamLoc: localData.gnasSamLoc,
    genTesMet: localData.genTesMet,
    detRes: localData.detRes,
    detVer: localData.detVer,
    mutSit: localData.mutSit,
    geneMethod: localData.geneMethod,
    geneRes: localData.geneRes,
    geneName: localData.geneName,
    genePoint: localData.genePoint,
    geneType: localData.geneType,
    geneMode: localData.geneMode,
    chrom: [...localData.chrom],
    chromOther: localData.chromOther
  })
}

watch(() => props.patientId, (val) => { if (val) loadReportFiles() }, { immediate: true })
</script>

<style scoped>
.genetics-exam-form {
  padding: 0;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 12px 0;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.report-section {
  margin-bottom: 12px;
}

.report-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.report-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #606266;
}

.remove-col-icon {
  cursor: pointer;
  font-size: 13px;
  color: #909399;
}
.remove-col-icon:hover {
  color: #f56c6c;
}
</style>