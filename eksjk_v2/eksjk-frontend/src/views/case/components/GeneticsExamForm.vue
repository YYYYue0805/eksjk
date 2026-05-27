<template>
  <div class="genetics-exam-form">
    <!-- 染色体核型 -->
    <div class="section-title">染色体核型</div>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="染色体核型">
          <el-input v-model="localData.karyotype" :disabled="disabled || !showKaryotype" placeholder="请输入染色体核型（如 46,XY）" />
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 基因突变检测表 -->
    <div class="section-title" v-if="showGeneTable">基因突变检测</div>
    <div v-if="showGeneTable">
      <el-table :data="genData" border stripe size="small">
        <el-table-column prop="geneName" label="基因名称">
          <template #default="{ row }">
            <el-input v-model="row.geneName" :disabled="disabled" placeholder="基因名称" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="mutationSite" label="突变位点">
          <template #default="{ row }">
            <el-input v-model="row.mutationSite" :disabled="disabled" placeholder="突变位点" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="mutationType" label="突变类型">
          <template #default="{ row }">
            <el-input v-model="row.mutationType" :disabled="disabled" placeholder="突变类型" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="sequencingMethod" label="测序方法">
          <template #default="{ row }">
            <el-input v-model="row.sequencingMethod" :disabled="disabled" placeholder="测序方法" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="inheritanceMode" label="遗传模式">
          <template #default="{ row }">
            <el-input v-model="row.inheritanceMode" :disabled="disabled" placeholder="遗传模式" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="pathogenicity" label="致病性评级">
          <template #default="{ row }">
            <el-select v-model="row.pathogenicity" :disabled="disabled" placeholder="评级" size="small" clearable>
              <el-option label="致病" value="致病" />
              <el-option label="可能致病" value="可能致病" />
              <el-option label="意义不明确" value="意义不明确" />
              <el-option label="可能良性" value="可能良性" />
              <el-option label="良性" value="良性" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column v-if="!disabled" label="操作" width="70" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" size="small" text @click="removeGene($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button v-if="!disabled" type="primary" size="small" style="margin-top: 8px" @click="addGene">
        <el-icon><Plus /></el-icon>新增基因
      </el-button>
    </div>

    <!-- 生物样本库 -->
    <div class="section-title" v-if="showBioBank">生物样本库</div>
    <el-row v-if="showBioBank" :gutter="16">
      <el-col :span="8">
        <el-form-item label="患者样本">
          <el-input v-model="localData.biologBank" :disabled="disabled" placeholder="样本编号" />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="父亲样本">
          <el-input v-model="localData.biologBankFa" :disabled="disabled" placeholder="样本编号" />
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item label="母亲样本">
          <el-input v-model="localData.biologBankMo" :disabled="disabled" placeholder="样本编号" />
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
  disabled: { type: Boolean, default: false },
  diseaseType: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])

// 哪些疾病类型显示哪些区块
const showKaryotype = computed(() => ['dsd', 'mas', 'sss'].includes(props.diseaseType))
const showGeneTable = computed(() => ['dsd', 'fss', 'sss'].includes(props.diseaseType))
const showBioBank = computed(() => ['dsd', 'fss', 'sga', 'sss'].includes(props.diseaseType))

const localData = reactive({
  karyotype: '',
  biologBank: '',
  biologBankFa: '',
  biologBankMo: '',
  genData: []
})

const genData = computed({
  get: () => localData.genData || [],
  set: (val) => { localData.genData = val }
})

function addGene() {
  localData.genData.push({
    geneName: '',
    mutationSite: '',
    mutationType: '',
    sequencingMethod: '',
    inheritanceMode: '',
    pathogenicity: ''
  })
  syncToParent()
}

function removeGene(index) {
  localData.genData.splice(index, 1)
  syncToParent()
}

watch(() => props.modelValue, (val) => {
  if (val && typeof val === 'object') {
    if (val.karyotype !== undefined) localData.karyotype = val.karyotype
    if (val.biologBank !== undefined) localData.biologBank = val.biologBank
    if (val.biologBankFa !== undefined) localData.biologBankFa = val.biologBankFa
    if (val.biologBankMo !== undefined) localData.biologBankMo = val.biologBankMo
    if (val.genData && Array.isArray(val.genData)) localData.genData = [...val.genData]
  }
}, { immediate: true, deep: true })

watch(localData, () => syncToParent(), { deep: true })

function syncToParent() {
  emit('update:modelValue', {
    karyotype: localData.karyotype,
    biologBank: localData.biologBank,
    biologBankFa: localData.biologBankFa,
    biologBankMo: localData.biologBankMo,
    genData: [...localData.genData]
  })
}
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
</style>
