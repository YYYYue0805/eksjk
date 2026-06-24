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
          <el-form-item label="生长激素">
            <el-select v-model="localData.rhGH" :disabled="disabled" style="width:100%">
              <el-option v-for="opt in rhGHOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <template v-if="isShortActing">
          <el-col :span="8">
            <el-form-item label="剂量(IU/d)">
              <el-input v-model="localData.rhGHdose" :disabled="disabled" placeholder="IU/d" @input="calcShortDose" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="剂量/体重(IU/kg.d)">
              <el-input v-model="localData.rhGHdoseKG" :disabled="disabled" placeholder="自动计算" />
            </el-form-item>
          </el-col>
        </template>
        <template v-if="isLongActing">
          <el-col :span="8">
            <el-form-item label="剂量(mg/w)">
              <el-input v-model="localData.PEGrhGHdose" :disabled="disabled" placeholder="mg/w" @input="calcLongDose" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="剂量/体重(mg/kg.w)">
              <el-input v-model="localData.PEGrhGHdoseKG" :disabled="disabled" placeholder="自动计算" />
            </el-form-item>
          </el-col>
        </template>
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
    <template v-if="localData.diaPlan === '4'">
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
        <el-table-column label="生长激素" min-width="150">
          <template #default="{ row }">
            <el-select v-model="row.rhUnitedCustomization" size="small" :disabled="disabled" style="width:100%">
              <el-option v-for="opt in rhGHOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="用量" min-width="200">
          <template #default="{ row }">
            <template v-if="['1','2'].includes(row.rhUnitedCustomization)">
              <el-input v-model="row.rhGHdose" size="small" :disabled="disabled" placeholder="IU/kg.d" style="width:100px" @input="calcRowShortDose(row)" />
              <span style="margin-left:4px;font-size:12px;color:#909399">{{ row.rhGHdoseKG || 'mg/kg.w' }}</span>
            </template>
            <template v-else-if="['3','4','5'].includes(row.rhUnitedCustomization)">
              <el-input v-model="row.PEGrhGHdoseKG" size="small" :disabled="disabled" placeholder="mg/w" style="width:100px" @input="calcRowLongDose(row)" />
              <span style="margin-left:4px;font-size:12px;color:#909399">{{ row.PEGrhGHdose || 'mg/kg.w' }}</span>
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
  PEGrhCustomizationPromptKG: ''
}

const localData = reactive({ ...defaultData })

watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    Object.keys(defaultData).forEach(k => {
      if (val[k] !== undefined) localData[k] = val[k]
    })
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

// rhGH 品牌选项（V1 临床标签）
const rhGHOptions = [
  { value: '1', label: '短效rhGH(粉剂)' },
  { value: '2', label: '短效rhGH(水剂)' },
  { value: '3', label: '金培生长激素注射液' },
  { value: '4', label: '怡培生长激素注射液' },
  { value: '5', label: '帕西生长激素注射液' }
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

// 短效/长效判断
const isShortActing = computed(() => ['1', '2'].includes(localData.rhGH))
const isLongActing = computed(() => ['3', '4', '5'].includes(localData.rhGH))

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
  if (props.weight > 0 && row.PEGrhGHdoseKG) {
    row.PEGrhGHdose = (Number(row.PEGrhGHdoseKG) / props.weight).toFixed(4)
  } else {
    row.PEGrhGHdose = ''
  }
}

// diaPlan 切换时重置 planData
function onDiaPlanChange() {
  localData.planData = [{ rhGH: '', rhUnitedCustomization: '', rhGHdose: '', rhGHdoseKG: '', PEGrhGHdose: '', PEGrhGHdoseKG: '' }]
}

function addRow() {
  localData.planData.push({ rhGH: '', rhUnitedCustomization: '', rhGHdose: '', rhGHdoseKG: '', PEGrhGHdose: '', PEGrhGHdoseKG: '' })
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
