<template>
  <div class="treatment-plan-form">
    <el-divider />
    <div class="tab-section-title">诊疗方案</div>

    <!-- 治疗方案选择 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="治疗方案">
          <el-select v-model="localData.diaPlan" :disabled="disabled" style="width:100%" @change="onDiaPlanChange">
            <el-option v-for="opt in planOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- diaPlan=2: rhGH治疗 -->
    <template v-if="localData.diaPlan === '2'">
      <el-divider content-position="left">rhGH 治疗详情</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="生长激素类型">
            <el-select v-model="localData.rhGHType" :disabled="disabled" style="width:100%" @change="onRhGHTypeChange">
              <el-option v-for="opt in rhGHTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="localData.rhGHType" :span="10">
          <el-form-item label="药品">
            <el-select v-model="localData.rhGH" :disabled="disabled" style="width:100%">
              <el-option v-for="opt in currentDrugOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="isLagHType" :gutter="20">
        <el-col :span="8">
          <el-form-item label="剂量（mg/w）">
            <el-input v-model="localData.PEGrhGHdose" :disabled="disabled" placeholder="mg/w" @input="calcLongDose" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="剂量/体重（mg/kg.w）">
            <el-input v-model="localData.PEGrhGHdoseKG" :disabled="disabled" placeholder="自动计算" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="isRhGHType" :gutter="20">
        <el-col :span="8">
          <el-form-item label="剂量（IU/d）">
            <el-input v-model="localData.rhGHdose" :disabled="disabled" placeholder="IU/d" @input="calcShortDose" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="剂量/体重（IU/kg.d）">
            <el-input v-model="localData.rhGHdoseKG" :disabled="disabled" placeholder="自动计算" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="isLagHType" :gutter="20" style="margin-top:8px">
        <el-col :span="12">
          <el-form-item label="其他药物">
            <el-input v-model="localData.laghOtherMedicine" :disabled="disabled" placeholder="其他药物" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="其他">
            <el-input v-model="localData.laghOther" :disabled="disabled" placeholder="其他" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="isRhGHType" :gutter="20" style="margin-top:8px">
        <el-col :span="12">
          <el-form-item label="其他药物">
            <el-input v-model="localData.rhghOtherMedicine" :disabled="disabled" placeholder="其他药物" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="其他">
            <el-input v-model="localData.rhghOther" :disabled="disabled" placeholder="其他" />
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- diaPlan=7: GnRHa治疗 -->
    <template v-if="localData.diaPlan === '7'">
      <el-divider content-position="left">GnRHa 治疗详情</el-divider>
      <el-row :gutter="20">
        <el-col :span="14">
          <el-form-item label="GnRHa药品">
            <el-select v-model="localData.rhGH" :disabled="disabled" style="width:100%" filterable>
              <el-option v-for="opt in gnrhaDrugOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- diaPlan=4: 停止GnRHa治疗 -->
    <template v-if="['4','5'].includes(localData.diaPlan)">
      <el-divider content-position="left">停止 GnRHa 治疗详情</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="后续方案">
            <el-select v-model="localData.rhCustomizationDiaPlan" :disabled="disabled" style="width:100%">
              <el-option label="短效rhGH" value="1" />
              <el-option label="长效生长激素(PEG-rhGH)" value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <template v-if="localData.rhCustomizationDiaPlan === '1'">
          <el-col :span="8">
            <el-form-item label="停治短效rhGH(IU/d)">
              <el-input v-model="localData.rhCustomizationPrompt" :disabled="disabled" placeholder="IU/d" @input="calcStopShortDose" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="停治剂量/体重(mg/kg.w)">
              <el-input v-model="localData.rhCustomizationPromptKG" :disabled="disabled" placeholder="自动计算" />
            </el-form-item>
          </el-col>
        </template>
        <template v-if="localData.rhCustomizationDiaPlan === '2'">
          <el-col :span="8">
            <el-form-item label="停治长效rhGH(mg/kg.w)">
              <el-input v-model="localData.PEGrhCustomizationPrompt" :disabled="disabled" placeholder="mg/kg.w" @input="calcStopLongDose" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="停治剂量/体重(IU/kg.d)">
              <el-input v-model="localData.PEGrhCustomizationPromptKG" :disabled="disabled" placeholder="自动计算" />
            </el-form-item>
          </el-col>
        </template>
      </el-row>
    </template>

    <!-- diaPlan=8: 芳香化酶抑制剂 -->
    <template v-if="localData.diaPlan === '8'">
      <el-divider content-position="left">芳香化酶抑制剂详情</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="阿那曲唑">
            <el-select v-model="localData.rhGH" :disabled="disabled" style="width:100%">
              <el-option v-for="opt in anastrozoleOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <!-- diaPlan=3/10: 联合治疗动态表 -->
    <template v-if="['3','10'].includes(localData.diaPlan)">
      <el-divider content-position="left">
        {{ localData.diaPlan === '3' ? 'GnRHa联合生长激素方案' : '芳香化酶联合生长激素方案' }}
      </el-divider>
      <el-table :data="localData.planData" border stripe size="small" style="width:100%">
        <el-table-column :label="localData.diaPlan === '3' ? 'GnRHa药品' : '阿那曲唑'" min-width="180">
          <template #default="{ row }">
            <el-select v-model="row.rhGH" size="small" :disabled="disabled" style="width:100%" filterable>
              <el-option v-for="opt in (localData.diaPlan === '3' ? gnrhaDrugOptions : anastrozoleOptions)" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="生长激素类型" min-width="140">
          <template #default="{ row }">
            <el-select v-model="row.rhUnitedType" size="small" :disabled="disabled" style="width:100%" @change="onRowUnitedTypeChange(row)">
              <el-option v-for="opt in rhGHTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="药品" min-width="150">
          <template #default="{ row }">
            <el-select v-model="row.rhUnitedCustomization" size="small" :disabled="disabled" style="width:100%">
              <el-option v-for="opt in (row.rhUnitedType === '1' ? laghDrugOptions : row.rhUnitedType === '2' ? rhghDrugOptions : [])" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="用量" min-width="200">
          <template #default="{ row }">
            <template v-if="row.rhUnitedType === '2'">
              <el-input v-model="row.rhGHdose" size="small" :disabled="disabled" placeholder="IU/d" style="width:100px" @input="calcRowShortDose(row)" />
              <span style="margin-left:4px;font-size:12px;color:#909399">{{ row.rhGHdoseKG || 'IU/kg.d' }}</span>
            </template>
            <template v-else-if="row.rhUnitedType === '1'">
              <el-input v-model="row.PEGrhGHdose" size="small" :disabled="disabled" placeholder="mg/w" style="width:100px" @input="calcRowLongDose(row)" />
              <span style="margin-left:4px;font-size:12px;color:#909399">{{ row.PEGrhGHdoseKG || 'mg/kg.w' }}</span>
            </template>
          </template>
        </el-table-column>
        <el-table-column v-if="!disabled" label="操作" width="70">
          <template #default="{ $index }">
            <el-button type="danger" size="small" text @click="removeRow($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button v-if="!disabled" type="primary" size="small" style="margin-top:8px" @click="addRow">
        <el-icon><Plus /></el-icon>新增方案
      </el-button>
    </template>

    <!-- 其他药物 -->
    <el-row :gutter="20" style="margin-top:12px">
      <el-col :span="24">
        <el-form-item label="其他药物">
          <el-input v-model="localData.otherMedicine" type="textarea" :rows="2" :disabled="disabled" placeholder="请输入其他用药信息" />
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) },
  diseaseType: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  weight: { type: Number, default: 0 }
})

const emit = defineEmits(['update:modelValue'])

const defaultData = {
  diaPlan: '1',
  rhGHType: '',
  rhGH: '',
  rhGHdose: '',
  rhGHdoseKG: '',
  PEGrhGHdose: '',
  PEGrhGHdoseKG: '',
  GnRHa: '',
  GnRHadose: '',
  planData: [],
  otherMedicine: '',
  rhCustomizationDiaPlan: '',
  rhCustomizationPrompt: '',
  PEGrhCustomizationPrompt: '',
  rhCustomizationPromptKG: '',
  PEGrhCustomizationPromptKG: '',
  laghOtherMedicine: '',
  laghOther: '',
  rhghOtherMedicine: '',
  rhghOther: ''
}

const localData = reactive({ ...defaultData })

// 旧编码 → 新编码映射
const OLD_TO_NEW = {
  '3': { rhGHType: '1', rhGH: '11' },   // 金培 → 金赛增
  '4': { rhGHType: '1', rhGH: '12' },   // 怡培 → 益佩生
  '5': { rhGHType: '1', rhGH: '14' }    // 帕西 → 诺泽优
}

function migratePlanData(data) {
  if (!data || data._migrated) return
  if (data.diaPlan === '2' && !data.rhGHType && data.rhGH) {
    if (OLD_TO_NEW[data.rhGH]) {
      const m = OLD_TO_NEW[data.rhGH]
      data.rhGHType = m.rhGHType
      data.rhGH = m.rhGH
    } else if (data.rhGH === '1' || data.rhGH === '2') {
      data.rhGHType = '2'
      data.rhGH = ''
    }
  }
  if (Array.isArray(data.planData)) {
    data.planData.forEach(row => {
      if (!row.rhUnitedType && row.rhUnitedCustomization) {
        if (OLD_TO_NEW[row.rhUnitedCustomization]) {
          const m = OLD_TO_NEW[row.rhUnitedCustomization]
          row.rhUnitedType = m.rhGHType
          row.rhUnitedCustomization = m.rhGH
        } else if (['1', '2'].includes(row.rhUnitedCustomization)) {
          row.rhUnitedType = '2'
          row.rhUnitedCustomization = ''
        }
      }
    })
  }
  data._migrated = true
}

watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    Object.keys(defaultData).forEach(k => {
      if (val[k] !== undefined) localData[k] = val[k]
    })
    migratePlanData(localData)
  }
}, { immediate: true, deep: true })

watch(localData, (val) => {
  emit('update:modelValue', { ...val })
}, { deep: true })

// 治疗方案选项
const planOptions = computed(() => {
  const common = [
    { value: '1', label: '未治疗' },
    { value: '2', label: 'rhGH治疗' },
    { value: '7', label: 'GnRHa治疗' },
    { value: '3', label: 'GnRHa联合生长激素治疗' },
    { value: '8', label: '芳香化酶抑制剂' },
    { value: '11', label: '停止芳香化酶抑制剂' },
    { value: '10', label: '芳香化酶联合生长激素治疗' },
    { value: '12', label: '停止芳香化酶联合生长激素治疗' },
    { value: '4', label: '停止GnRHa治疗' },
    { value: '5', label: '停止GnRHa联合生长激素治疗' },
    { value: '6', label: '停止生长激素治疗' }
  ]
  if (['cpp', 'sga'].includes(props.diseaseType)) {
    common.push({ value: '9', label: '中医药治疗' })
  }
  return common
})

// rhGH 类型选项（一级）
const rhGHTypeOptions = [
  { value: '1', label: 'LAGH（长效生长激素）' },
  { value: '2', label: 'rhGH（重组人生长激素）' }
]

// LAGH 药品选项（二级）
const laghDrugOptions = [
  { value: '11', label: '金赛增（金培生长激素注射液）' },
  { value: '12', label: '益佩生（怡培生长激素注射液）' },
  { value: '13', label: '维臻高（隆培生长激素注射液）' },
  { value: '14', label: '诺泽优（帕西生长激素注射液）' }
]

// rhGH 药品选项（二级）
const rhghDrugOptions = [
  { value: '21', label: '赛增粉剂' },
  { value: '22', label: '赛增水剂' },
  { value: '23', label: '诺泽粉剂' },
  { value: '24', label: '诺泽水剂' },
  { value: '25', label: '安苏萌粉剂' },
  { value: '26', label: '安苏萌水剂' },
  { value: '27', label: '海之元粉剂' },
  { value: '28', label: '海之元水剂' },
  { value: '29', label: '珍怡粉剂' }
]

// GnRHa 详细药品选项（V1）
const gnrhaDrugOptions = [
  { value: '1', label: '达菲林针3.75mg，每28天1次' },
  { value: '7', label: '达菲林针3.75mg，每14天1次' },
  { value: '8', label: '达菲林针3.75mg，每21天1次' },
  { value: '9', label: '达菲林针3.75mg，每35天1次' },
  { value: '10', label: '达菲林针15mg，每84天1次' },
  { value: '11', label: '达必佳针3.75mg，每21天1次' },
  { value: '2', label: '达必佳针3.75mg，每28天1次' },
  { value: '3', label: '抑那通针3.75mg，每28天1次' },
  { value: '4', label: '抑那通针11.25mg，每12周1次' },
  { value: '5', label: '伯恩若康针3.75mg，每28天1次' },
  { value: '6', label: '贝依针3.75mg，每28天1次' }
]

// 阿那曲唑选项
const anastrozoleOptions = [
  { value: '1', label: '阿那曲唑0.5/片' },
  { value: '2', label: '阿那曲唑1/片' },
  { value: '3', label: '阿那曲唑1.5/片' },
  { value: '4', label: '阿那曲唑2/片' }
]

// LAGH / rhGH 类型判断
const isLagHType = computed(() => localData.rhGHType === '1')
const isRhGHType = computed(() => localData.rhGHType === '2')

// 根据 rhGHType 返回对应的药品选项
const currentDrugOptions = computed(() => {
  if (localData.rhGHType === '1') return laghDrugOptions
  if (localData.rhGHType === '2') return rhghDrugOptions
  return []
})

// 类型切换时清空药品选择
function onRhGHTypeChange() {
  localData.rhGH = ''
}

function onRowUnitedTypeChange(row) {
  row.rhUnitedCustomization = ''
}

// 自动计算：短效 rhGHdose (IU/d) → rhGHdoseKG (IU/kg.d)
function calcShortDose() {
  if (props.weight > 0 && localData.rhGHdose) {
    localData.rhGHdoseKG = (Number(localData.rhGHdose) / props.weight).toFixed(4)
  } else {
    localData.rhGHdoseKG = ''
  }
}

// 自动计算：长效 PEGrhGHdose (mg/w) → PEGrhGHdoseKG (mg/kg.w)
function calcLongDose() {
  if (props.weight > 0 && localData.PEGrhGHdose) {
    localData.PEGrhGHdoseKG = (Number(localData.PEGrhGHdose) / props.weight).toFixed(4)
  } else {
    localData.PEGrhGHdoseKG = ''
  }
}

// 自动计算：停止短效 rhCustomizationPrompt (IU/d) → rhCustomizationPromptKG (mg/kg.w)
function calcStopShortDose() {
  if (props.weight > 0 && localData.rhCustomizationPrompt) {
    localData.rhCustomizationPromptKG = (Number(localData.rhCustomizationPrompt) / props.weight).toFixed(4)
  } else {
    localData.rhCustomizationPromptKG = ''
  }
}

// 自动计算：停止长效 PEGrhCustomizationPrompt (mg/kg.w) → PEGrhCustomizationPromptKG (IU/kg.d)
function calcStopLongDose() {
  if (props.weight > 0 && localData.PEGrhCustomizationPrompt) {
    localData.PEGrhCustomizationPromptKG = (Number(localData.PEGrhCustomizationPrompt) / props.weight).toFixed(4)
  } else {
    localData.PEGrhCustomizationPromptKG = ''
  }
}

// planData 行内计算
function calcRowShortDose(row) {
  if (props.weight > 0 && row.rhGHdose) {
    row.rhGHdoseKG = (Number(row.rhGHdose) / props.weight).toFixed(4)
  } else {
    row.rhGHdoseKG = ''
  }
}

function calcRowLongDose(row) {
  if (props.weight > 0 && row.PEGrhGHdose) {
    row.PEGrhGHdoseKG = (Number(row.PEGrhGHdose) / props.weight).toFixed(4)
  } else {
    row.PEGrhGHdoseKG = ''
  }
}

// diaPlan 切换时重置 planData
function onDiaPlanChange() {
  localData.planData = [{ rhGH: '', rhUnitedType: '', rhUnitedCustomization: '', rhGHdose: '', rhGHdoseKG: '', PEGrhGHdose: '', PEGrhGHdoseKG: '' }]
}

function addRow() {
  localData.planData.push({ rhGH: '', rhUnitedType: '', rhUnitedCustomization: '', rhGHdose: '', rhGHdoseKG: '', PEGrhGHdose: '', PEGrhGHdoseKG: '' })
}

function removeRow(index) {
  localData.planData.splice(index, 1)
}
</script>

<style scoped>
.treatment-plan-form {
  margin-top: 8px;
}

.tab-section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #409EFF;
}
</style>
