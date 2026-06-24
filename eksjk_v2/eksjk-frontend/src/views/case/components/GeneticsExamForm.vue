<template>
  <div class="genetics-exam-form">
    <!-- 1. 染色体核型 -->
    <div class="section-title" v-if="showKaryotype">染色体核型</div>
    <el-row v-if="showKaryotype" :gutter="16">
      <el-col :span="12">
        <el-form-item label="染色体核型">
          <el-select v-model="localData.karyotype" :disabled="disabled" placeholder="请选择或输入" clearable filterable allow-create>
            <el-option label="46,XY" value="46,XY" />
            <el-option label="46,XX" value="46,XX" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 2. 上传染色体报告 -->
    <div class="section-title" v-if="showChromReport">染色体报告</div>
    <div v-if="showChromReport" class="report-section">
      <template v-if="!chromReportFile">
        <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: '染色体报告' }"
                   :on-success="onChromReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                   :show-file-list="false" :disabled="disabled" accept="image/*,.pdf">
          <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>上传染色体报告</el-button>
        </el-upload>
      </template>
      <template v-else>
        <div class="report-info">
          <el-icon><Document /></el-icon>
          <span class="report-name" :title="chromReportFile.name">{{ chromReportFile.name }}</span>
          <el-button link type="primary" size="small" @click="downloadFile(chromReportFile)">下载</el-button>
          <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('chrom')">删除</el-button>
        </div>
      </template>
    </div>

    <!-- 3. 上传基因检测报告 -->
    <div class="section-title" v-if="showGeneReport">基因检测报告</div>
    <div v-if="showGeneReport" class="report-section">
      <template v-if="!geneReportFile">
        <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: '基因检测报告' }"
                   :on-success="onGeneReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                   :show-file-list="false" :disabled="disabled" accept="image/*,.pdf">
          <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>上传基因检测报告</el-button>
        </el-upload>
      </template>
      <template v-else>
        <div class="report-info">
          <el-icon><Document /></el-icon>
          <span class="report-name" :title="geneReportFile.name">{{ geneReportFile.name }}</span>
          <el-button link type="primary" size="small" @click="downloadFile(geneReportFile)">下载</el-button>
          <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('gene')">删除</el-button>
        </div>
      </template>
    </div>

    <!-- 4. 基因突变检测表 -->
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
        <el-table-column v-if="!disabled" label="操作" width="70" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" size="small" text @click="removeGene($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button v-if="!disabled" type="primary" size="small" style="margin-top:8px" @click="addGene">
        <el-icon><Plus /></el-icon>新增基因
      </el-button>
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

    <!-- 7. ELTM 遗传学检查 -->
    <div class="section-title" v-if="showEltmGenetics">基因结果</div>
    <template v-if="showEltmGenetics">
      <div class="report-section">
        <template v-if="!eltmGeneReportFile">
          <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: 'E路童萌基因检测报告' }"
                     :on-success="onEltmGeneReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                     :show-file-list="false" :disabled="disabled" accept="image/*,.pdf">
            <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>基因结果上传</el-button>
          </el-upload>
        </template>
        <template v-else>
          <div class="report-info">
            <el-icon><Document /></el-icon>
            <span class="report-name" :title="eltmGeneReportFile.name">{{ eltmGeneReportFile.name }}</span>
            <el-button link type="primary" size="small" @click="downloadFile(eltmGeneReportFile)">下载</el-button>
            <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('eltmGene')">删除</el-button>
          </div>
        </template>
      </div>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="基因检测方法">
            <el-input v-model="localData.geneMethod" :disabled="disabled" placeholder="基因检测方法" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="基因结果">
            <el-radio-group v-model="localData.geneRes" :disabled="disabled">
              <el-radio label="阴性">阴性</el-radio>
              <el-radio label="阳性">阳性</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="localData.geneRes === '阳性'" :gutter="16">
        <el-col :span="12">
          <el-form-item label="基因名称">
            <el-input v-model="localData.geneName" :disabled="disabled" placeholder="基因名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="突变位点">
            <el-input v-model="localData.genePoint" :disabled="disabled" placeholder="突变位点" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="localData.geneRes === '阳性'" :gutter="16">
        <el-col :span="12">
          <el-form-item label="突变类型">
            <el-input v-model="localData.geneType" :disabled="disabled" placeholder="突变类型" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="遗传模式">
            <el-input v-model="localData.geneMode" :disabled="disabled" placeholder="遗传模式" />
          </el-form-item>
        </el-col>
      </el-row>

      <div class="section-title" style="margin-top:16px">染色体结果</div>
      <div class="report-section">
        <template v-if="!eltmChromReportFile">
          <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: 'E路童萌染色体检查报告' }"
                     :on-success="onEltmChromReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                     :show-file-list="false" :disabled="disabled" accept="image/*,.pdf">
            <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>染色体检查报告</el-button>
          </el-upload>
        </template>
        <template v-else>
          <div class="report-info">
            <el-icon><Document /></el-icon>
            <span class="report-name" :title="eltmChromReportFile.name">{{ eltmChromReportFile.name }}</span>
            <el-button link type="primary" size="small" @click="downloadFile(eltmChromReportFile)">下载</el-button>
            <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('eltmChrom')">删除</el-button>
          </div>
        </template>
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
    </template>

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
      <template v-if="!pathologyReportFile">
        <el-upload :action="uploadUrl" :headers="uploadHeaders" :data="{ patientId, category: '病理报告' }"
                   :on-success="onPathologyReportSuccess" :on-error="onUploadError" :before-upload="beforeUpload"
                   :show-file-list="false" :disabled="disabled" accept="image/*,.pdf">
          <el-button :disabled="disabled" size="small"><el-icon><Plus /></el-icon>上传病理报告</el-button>
        </el-upload>
      </template>
      <template v-else>
        <div class="report-info">
          <el-icon><Document /></el-icon>
          <span class="report-name" :title="pathologyReportFile.name">{{ pathologyReportFile.name }}</span>
          <el-button link type="primary" size="small" @click="downloadFile(pathologyReportFile)">下载</el-button>
          <el-button v-if="!disabled" link type="danger" size="small" @click="deleteReport('pathology')">删除</el-button>
        </div>
      </template>
    </div>

  </div>
</template>

<script setup>
import { reactive, computed, watch, ref, nextTick } from 'vue'
import { Plus, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
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
const showKaryotype = computed(() => ['dsd', 'mas', 'sss', 'fss', 'sga', 'cpp'].includes(props.diseaseType))
const showChromReport = computed(() => ['dsd', 'fss', 'sss', 'sga', 'cpp'].includes(props.diseaseType))
const showGeneReport = computed(() => ['dsd', 'fss', 'sss', 'sga', 'cpp'].includes(props.diseaseType))
const showGeneTable = computed(() => ['dsd', 'fss', 'sss', 'sga', 'cpp'].includes(props.diseaseType))
const showSurgeryPathology = computed(() => props.diseaseType === 'dsd')
const showMasGenetics = computed(() => props.diseaseType === 'mas')
const showEltmGenetics = computed(() => props.diseaseType === 'eltm')

const localData = reactive({
  karyotype: '',
  genData: [],
  biologBank: '',
  biologBankFa: '',
  biologBankMo: '',
  biologBankData: [],
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

// 报告文件状态
const chromReportFile = ref(null)
const geneReportFile = ref(null)
const pathologyReportFile = ref(null)
const eltmGeneReportFile = ref(null)
const eltmChromReportFile = ref(null)

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
    chromReportFile.value = files.find(f => f.category === '染色体报告') || null
    geneReportFile.value = files.find(f => f.category === '基因检测报告') || null
    pathologyReportFile.value = files.find(f => f.category === '病理报告') || null
    eltmGeneReportFile.value = files.find(f => f.category === 'E路童萌基因检测报告') || null
    eltmChromReportFile.value = files.find(f => f.category === 'E路童萌染色体检查报告') || null
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

const onChromReportSuccess = makeReportSuccessHandler()
const onGeneReportSuccess = makeReportSuccessHandler()
const onPathologyReportSuccess = makeReportSuccessHandler()
const onEltmGeneReportSuccess = makeReportSuccessHandler()
const onEltmChromReportSuccess = makeReportSuccessHandler()

async function deleteReport(type) {
  const map = {
    chrom: chromReportFile,
    gene: geneReportFile,
    pathology: pathologyReportFile,
    eltmGene: eltmGeneReportFile,
    eltmChrom: eltmChromReportFile
  }
  const file = map[type]
  if (!file?.value?.path) return
  try {
    await deleteFile(file.value.path)
    file.value = null
    ElMessage.success('删除成功')
  } catch { ElMessage.error('删除失败') }
}

function addGene() {
  localData.genData.push({ geneName: '', rna: '', mutationType: '', other: '', pathogenicity: '', amino: '', father: '', mother: '' })
  syncToParent()
}

function removeGene(index) {
  localData.genData.splice(index, 1)
  syncToParent()
}

function normalizeGenData(data) {
  if (!data || !Array.isArray(data) || data.length === 0) return data
  const first = data[0]
  if (first.geneName !== undefined && first.sequencingMethod !== undefined && first.rna === undefined) {
    return data.map(row => ({
      geneName: row.geneName || '',
      rna: row.mutationSite || '',
      mutationType: '',
      other: row.sequencingMethod || '',
      pathogenicity: row.pathogenicity || '',
      amino: '',
      father: '',
      mother: ''
    }))
  }
  return data
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
    if (val.surgeryNote !== undefined) localData.surgeryNote = val.surgeryNote
    if (val.pathologyResult !== undefined) localData.pathologyResult = val.pathologyResult
    if (val.gnas !== undefined) localData.gnas = val.gnas
    if (val.gnasSamLoc !== undefined) localData.gnasSamLoc = val.gnasSamLoc
    if (val.genTesMet !== undefined) localData.genTesMet = val.genTesMet
    if (val.detRes !== undefined) localData.detRes = val.detRes
    if (val.detVer !== undefined) localData.detVer = val.detVer
    if (val.mutSit !== undefined) localData.mutSit = val.mutSit
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
    genData: [...localData.genData],
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
</style>