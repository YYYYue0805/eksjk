<template>
  <div class="baseline-exam-form">
    <!-- 性激素及相关 -->
    <div class="section-title">性激素及相关</div>
    <el-row :gutter="16">
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="LH (mIU/mL)"><el-input v-model="localData.lh" :disabled="disabled" placeholder="LH" /></el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="FSH (mIU/mL)"><el-input v-model="localData.fsh" :disabled="disabled" placeholder="FSH" /></el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="E2 (pg/mL)"><el-input v-model="localData.e2" :disabled="disabled" placeholder="E2" /></el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="T (ng/dL)"><el-input v-model="localData.t" :disabled="disabled" placeholder="T" /></el-form-item>
      </el-col>
      <el-col v-if="showBasicHormones" :span="8">
        <el-form-item label="PRL (ng/mL)"><el-input v-model="localData.prl" :disabled="disabled" placeholder="PRL" /></el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="DHT (ng/dL)"><el-input v-model="localData.dht" :disabled="disabled" placeholder="DHT" /></el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="FT (ng/dL)"><el-input v-model="localData.ft" :disabled="disabled" placeholder="FT" /></el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="SHBG (nmol/L)"><el-input v-model="localData.shbg" :disabled="disabled" placeholder="SHBG" /></el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="AMH (ng/mL)"><el-input v-model="localData.amh" :disabled="disabled" placeholder="AMH" /></el-form-item>
      </el-col>
      <el-col v-if="showDsdHormones" :span="8">
        <el-form-item label="INHB (pg/mL)"><el-input v-model="localData.inhb" :disabled="disabled" placeholder="INHB" /></el-form-item>
      </el-col>
    </el-row>

    <!-- 生长因子与代谢 -->
    <div class="section-title">生长因子与代谢</div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-form-item label="IGF-1 (ng/mL)"><el-input v-model="localData.igf1" :disabled="disabled" placeholder="IGF-1" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="IGFBP-3 (ug/mL)"><el-input v-model="localData.igfbp3" :disabled="disabled" placeholder="IGFBP-3" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="空腹血糖 (mmol/L)"><el-input v-model="localData.fasBloodGlu" :disabled="disabled" placeholder="空腹血糖" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="空腹胰岛素 (uIU/mL)"><el-input v-model="localData.fasInsulin" :disabled="disabled" placeholder="空腹胰岛素" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="糖化血红蛋白 (%)"><el-input v-model="localData.glyHem" :disabled="disabled" placeholder="糖化血红蛋白" /></el-form-item>
      </el-col>
    </el-row>

    <!-- 肾上腺激素 -->
    <div class="section-title" v-if="showAdrenal">肾上腺激素</div>
    <el-row v-if="showAdrenal" :gutter="16">
      <el-col :span="8">
        <el-form-item label="ACTH (pg/mL)"><el-input v-model="localData.acth" :disabled="disabled" placeholder="ACTH" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="皮质醇 (ug/dL)"><el-input v-model="localData.cortisol" :disabled="disabled" placeholder="皮质醇" /></el-form-item>
      </el-col>
      <el-col v-if="isDsd" :span="8">
        <el-form-item label="17-OHP (ng/mL)"><el-input v-model="localData.ohp" :disabled="disabled" placeholder="17-OHP" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="DHEA-S (ug/dL)"><el-input v-model="localData.dheas" :disabled="disabled" placeholder="DHEA-S" /></el-form-item>
      </el-col>
      <el-col v-if="isDsd" :span="8">
        <el-form-item label="雄烯二酮 (ng/mL)"><el-input v-model="localData.androstenedione" :disabled="disabled" placeholder="雄烯二酮" /></el-form-item>
      </el-col>
    </el-row>

    <!-- 激发试验 -->
    <div class="section-title" v-if="showProvocation">激发试验</div>
    <el-row v-if="showProvocation" :gutter="16">
      <!-- HCG 激发 (DSD) -->
      <template v-if="isDsd">
        <el-col :span="8">
          <el-form-item label="HCG 激发前"><el-input v-model="localData.hcg" :disabled="disabled" /></el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="HCG 激发后 T"><el-input v-model="localData.hcgt" :disabled="disabled" /></el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="HCG 激发后 DHT"><el-input v-model="localData.hcgdht" :disabled="disabled" /></el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="HCG 激发后 AD"><el-input v-model="localData.hcgad" :disabled="disabled" /></el-form-item>
        </el-col>
      </template>
      <!-- GnRH 激发 (DSD, CPP) -->
      <el-col :span="8">
        <el-form-item label="GnRH 激发 LHmax"><el-input v-model="localData.lhMax" :disabled="disabled" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="GnRH 激发 FSHmax"><el-input v-model="localData.fshMax" :disabled="disabled" /></el-form-item>
      </el-col>
    </el-row>

    <!-- 甲状腺功能 -->
    <div class="section-title" v-if="showThyroid">甲状腺功能</div>
    <el-row v-if="showThyroid" :gutter="16">
      <el-col :span="8">
        <el-form-item label="TSH (uIU/mL)"><el-input v-model="localData.tsh" :disabled="disabled" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="FT3 (pg/mL)"><el-input v-model="localData.ft3" :disabled="disabled" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="FT4 (ng/dL)"><el-input v-model="localData.ft4" :disabled="disabled" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="TPOAb (IU/mL)"><el-input v-model="localData.tpoab" :disabled="disabled" /></el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="TgAb (IU/mL)"><el-input v-model="localData.tgab" :disabled="disabled" /></el-form-item>
      </el-col>
    </el-row>

    <!-- 影像检查 -->
    <div class="section-title">影像检查</div>

    <!-- 性腺B超 -->
    <el-row v-if="showGonBUlt" :gutter="16">
      <el-col :span="24">
        <el-form-item label="性腺B超">
          <el-radio-group v-model="imagingState.gonBUlt.result" :disabled="disabled" @change="syncImagingToData('gonBUlt')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="imagingState.gonBUlt.result === '2'" :span="24">
        <el-form-item>
          <el-input v-model="imagingState.gonBUlt.description" type="textarea" :rows="3"
            :disabled="disabled" placeholder="请输入性腺B超异常描述" @input="syncImagingToData('gonBUlt')" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 卵巢囊肿 -->
    <el-row v-if="showOvarianCyst" :gutter="16">
      <el-col :span="24">
        <el-form-item label="卵巢囊肿">
          <el-radio-group v-model="imagingState.ovarianCyst.result" :disabled="disabled" @change="syncImagingToData('ovarianCyst')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 垂体MRI -->
    <el-row v-if="showPituitaryMri" :gutter="16">
      <el-col :span="24">
        <el-form-item label="垂体MRI">
          <el-radio-group v-model="imagingState.pituitaryMri.result" :disabled="disabled" @change="syncImagingToData('pituitaryMri')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="imagingState.pituitaryMri.result === '2'" :span="24">
        <el-form-item>
          <el-input v-model="imagingState.pituitaryMri.description" type="textarea" :rows="3"
            :disabled="disabled" placeholder="请输入垂体MRI异常描述" @input="syncImagingToData('pituitaryMri')" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 甲状腺B超 -->
    <el-row v-if="showThyroidUlt" :gutter="16">
      <el-col :span="24">
        <el-form-item label="甲状腺B超">
          <el-radio-group v-model="imagingState.thyroidUlt.result" :disabled="disabled" @change="syncImagingToData('thyroidUlt')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="imagingState.thyroidUlt.result === '2'" :span="24">
        <el-form-item>
          <el-input v-model="imagingState.thyroidUlt.description" type="textarea" :rows="3"
            :disabled="disabled" placeholder="请输入甲状腺B超异常描述" @input="syncImagingToData('thyroidUlt')" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 骨密度 -->
    <el-row v-if="showBonMinDen" :gutter="16">
      <el-col :span="24">
        <el-form-item label="骨密度">
          <el-radio-group v-model="imagingState.bonMinDen.result" :disabled="disabled" @change="syncImagingToData('bonMinDen')">
            <el-radio value="0">未查</el-radio>
            <el-radio value="1">正常</el-radio>
            <el-radio value="2">异常</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col v-if="imagingState.bonMinDen.result === '2'" :span="24">
        <el-form-item>
          <el-input v-model="imagingState.bonMinDen.description" type="textarea" :rows="3"
            :disabled="disabled" placeholder="请输入骨密度异常描述" @input="syncImagingToData('bonMinDen')" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- ==================== MAS 专属影像 ==================== -->
    <template v-if="isMas">
      <div class="section-sub-title">MAS 专项影像</div>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="肾上腺B超">
            <el-radio-group v-model="imagingState.adrenalUlt.result" :disabled="disabled" @change="syncImagingToData('adrenalUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.adrenalUlt.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.adrenalUlt.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入肾上腺B超异常描述" @input="syncImagingToData('adrenalUlt')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="肾脏B超">
            <el-radio-group v-model="imagingState.renalUlt.result" :disabled="disabled" @change="syncImagingToData('renalUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.renalUlt.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.renalUlt.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入肾脏B超异常描述" @input="syncImagingToData('renalUlt')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="骨骼X线">
            <el-radio-group v-model="imagingState.boneXRay.result" :disabled="disabled" @change="syncImagingToData('boneXRay')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.boneXRay.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.boneXRay.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入骨骼X线异常描述" @input="syncImagingToData('boneXRay')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="心脏B超">
            <el-radio-group v-model="imagingState.cardiacUlt.result" :disabled="disabled" @change="syncImagingToData('cardiacUlt')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.cardiacUlt.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.cardiacUlt.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入心脏B超异常描述" @input="syncImagingToData('cardiacUlt')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="MR部位">
            <el-select v-model="imagingState.mrPart.raw" :disabled="disabled" placeholder="请选择" @change="syncImagingToData('mrPart')">
              <el-option label="未查" value="" />
              <el-option label="头颅" value="头颅" />
              <el-option label="脊柱" value="脊柱" />
              <el-option label="四肢" value="四肢" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="MR结果">
            <el-radio-group v-model="imagingState.mrResult.result" :disabled="disabled" @change="syncImagingToData('mrResult')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.mrResult.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.mrResult.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入MR异常描述" @input="syncImagingToData('mrResult')" /></el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="CT部位">
            <el-select v-model="imagingState.ctPart.raw" :disabled="disabled" placeholder="请选择" @change="syncImagingToData('ctPart')">
              <el-option label="未查" value="" />
              <el-option label="头颅" value="头颅" />
              <el-option label="脊柱" value="脊柱" />
              <el-option label="四肢" value="四肢" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="CT结果">
            <el-radio-group v-model="imagingState.ctResult.result" :disabled="disabled" @change="syncImagingToData('ctResult')">
              <el-radio value="0">未查</el-radio>
              <el-radio value="1">正常</el-radio>
              <el-radio value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="imagingState.ctResult.result === '2'" :span="24">
          <el-form-item><el-input v-model="imagingState.ctResult.description" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入CT异常描述" @input="syncImagingToData('ctResult')" /></el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- 影像资料上传 -->
    <div class="section-title">影像资料上传</div>
    <FileUpload :patient-id="patientId" />
  </div>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'
import FileUpload from '@/views/case/components/FileUpload.vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  diseaseData: { type: Object, default: () => ({}) },
  disabled: { type: Boolean, default: false },
  diseaseType: { type: String, default: '' },
  patientId: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'update:diseaseData'])

// 疾病类型判断
const isDsd = computed(() => props.diseaseType === 'dsd')
const isCpp = computed(() => props.diseaseType === 'cpp')
const isFss = computed(() => props.diseaseType === 'fss')
const isSga = computed(() => props.diseaseType === 'sga')
const isSss = computed(() => props.diseaseType === 'sss')
const isMas = computed(() => props.diseaseType === 'mas')
const isEltm = computed(() => props.diseaseType === 'eltm')

// 区块条件显示
const showBasicHormones = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showDsdHormones = computed(() => isDsd.value)
const showAdrenal = computed(() => ['dsd', 'fss', 'sga', 'sss', 'eltm'].includes(props.diseaseType))
const showProvocation = computed(() => ['dsd', 'cpp'].includes(props.diseaseType))
const showThyroid = computed(() => ['mas', 'eltm'].includes(props.diseaseType))
const showGonBUlt = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))
const showPituitaryMri = computed(() => ['dsd', 'cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))
const showThyroidUlt = computed(() => ['cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))
const showBonMinDen = computed(() => ['fss', 'sga', 'sss'].includes(props.diseaseType))
const showOvarianCyst = computed(() => ['cpp', 'fss', 'sga', 'sss'].includes(props.diseaseType))

// 编码/解码：合并存储 "状态|描述"
function parseField(raw) {
  if (!raw) return { result: '0', description: '' }
  const idx = raw.indexOf('|')
  if (idx === -1) return { result: '0', description: raw } // 兼容旧纯文本数据
  return {
    result: raw.substring(0, idx) || '0',
    description: raw.substring(idx + 1)
  }
}
function encodeField(result, desc) {
  if (!result || result === '0') return '0|'
  if (result === '1') return '1|'
  return `2|${desc || ''}`
}

// 影像检查状态（result + description 分离）
function makeImagingState() {
  return {
    gonBUlt: { result: '0', description: '' },
    ovarianCyst: { result: '0', description: '' },
    pituitaryMri: { result: '0', description: '' },
    thyroidUlt: { result: '0', description: '' },
    bonMinDen: { result: '0', description: '' },
    adrenalUlt: { result: '0', description: '' },
    renalUlt: { result: '0', description: '' },
    boneXRay: { result: '0', description: '' },
    cardiacUlt: { result: '0', description: '' },
    mrPart: { raw: '' },
    mrResult: { result: '0', description: '' },
    ctPart: { raw: '' },
    ctResult: { result: '0', description: '' }
  }
}
const imagingState = reactive(makeImagingState())

// 从 localData（4个主表字段）加载到 imagingState
function loadImagingFromLocalData() {
  ;['gonBUlt', 'pituitaryMri', 'thyroidUlt', 'bonMinDen'].forEach(field => {
    const parsed = parseField(localData[field])
    imagingState[field].result = parsed.result
    imagingState[field].description = parsed.description
  })
}
// 从 diseaseData（MAS字段）加载到 imagingState
function loadImagingFromDiseaseData() {
  const masFields = ['adrenalUlt', 'renalUlt', 'boneXRay', 'cardiacUlt', 'mrResult', 'ctResult']
  masFields.forEach(field => {
    const raw = props.diseaseData[field]
    if (raw !== undefined && raw !== null) {
      const parsed = parseField(raw)
      imagingState[field].result = parsed.result
      imagingState[field].description = parsed.description
    }
  })
  // MR/CT 部位（plain text, no encoding）
  if (props.diseaseData.mrPart) imagingState.mrPart.raw = props.diseaseData.mrPart
  if (props.diseaseData.ctPart) imagingState.ctPart.raw = props.diseaseData.ctPart
}

// 同步影像状态到对应存储
function syncImagingToData(field) {
  // 4个主表字段 → localData → modelValue
  if (['gonBUlt', 'pituitaryMri', 'thyroidUlt', 'bonMinDen'].includes(field)) {
    localData[field] = encodeField(imagingState[field].result, imagingState[field].description)
    return
  }
  // MR/CT 部位
  if (field === 'mrPart' || field === 'ctPart') {
    emit('update:diseaseData', { ...props.diseaseData, [field]: imagingState[field].raw })
    return
  }
  // MAS 影像字段 → diseaseData
  const encoded = encodeField(imagingState[field].result, imagingState[field].description)
  emit('update:diseaseData', { ...props.diseaseData, [field]: encoded })
}

const localData = reactive({
  lh: '', fsh: '', e2: '', t: '', prl: '', dht: '', ft: '', shbg: '', amh: '', inhb: '',
  igf1: '', igfbp3: '', fasBloodGlu: '', fasInsulin: '', glyHem: '',
  acth: '', cortisol: '', ohp: '', dheas: '', androstenedione: '',
  hcg: '', hcgt: '', hcgdht: '', hcgad: '', lhMax: '', fshMax: '',
  tsh: '', ft3: '', ft4: '', tpoab: '', tgab: '',
  gonBUlt: '', pituitaryMri: '', thyroidUlt: '', bonMinDen: ''
})

watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    Object.keys(localData).forEach(key => {
      if (val[key] !== undefined && val[key] !== null) {
        localData[key] = val[key]
      }
    })
    loadImagingFromLocalData()
  }
}, { immediate: true, deep: true })

// 监听 diseaseData 变化（MAS影像字段加载）
watch(() => props.diseaseData, (val) => {
  if (val && typeof val === 'object') {
    loadImagingFromDiseaseData()
  }
}, { immediate: true, deep: true })

watch(localData, () => {
  emit('update:modelValue', { ...localData })
}, { deep: true })
</script>

<style scoped>
.baseline-exam-form {
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
</style>
