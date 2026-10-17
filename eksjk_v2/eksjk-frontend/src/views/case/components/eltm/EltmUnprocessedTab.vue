<template>
  <div class="eltm-unprocessed-tab">
    <el-alert
      title="以下为 E路童萌 同步的外部原始数据（只读）。已映射到病例栏目的内容请在对应模块查看，此处保留完整原始记录供核对"
      type="info"
      :closable="false"
      show-icon
      class="source-tip"
    />

    <el-tabs v-model="activeGroup" type="border-card">
      <el-tab-pane label="外部表单" name="forms">
        <EltmFormsTab :patient-id="patientId" />
      </el-tab-pane>
      <el-tab-pane label="检查资料" name="pics" lazy>
        <EltmPicsTab :patient-id="patientId" />
      </el-tab-pane>
      <el-tab-pane label="评估报告" name="reports" lazy>
        <EltmReportsTab :patient-id="patientId" />
      </el-tab-pane>
      <el-tab-pane label="体征明细" name="growth" lazy>
        <EltmGrowthTab :patient-id="patientId" />
      </el-tab-pane>
      <el-tab-pane label="运动与睡眠" name="exerciseSleep" lazy>
        <EltmExerciseSleepTab :patient-id="patientId" />
      </el-tab-pane>
      <el-tab-pane label="项目与医院" name="assign" lazy>
        <EltmAssignTab :patient-id="patientId" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import EltmFormsTab from '@/views/case/components/eltm/EltmFormsTab.vue'
import EltmPicsTab from '@/views/case/components/eltm/EltmPicsTab.vue'
import EltmReportsTab from '@/views/case/components/eltm/EltmReportsTab.vue'
import EltmGrowthTab from '@/views/case/components/eltm/EltmGrowthTab.vue'
import EltmExerciseSleepTab from '@/views/case/components/eltm/EltmExerciseSleepTab.vue'
import EltmAssignTab from '@/views/case/components/eltm/EltmAssignTab.vue'

defineProps({
  patientId: { type: String, required: true }
})

const activeGroup = ref('forms')
</script>

<style scoped>
.source-tip { margin-bottom: 16px; }
.eltm-unprocessed-tab :deep(.el-tabs__content) { padding: 16px; }
</style>
