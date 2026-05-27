<template>
  <div class="questionnaire-form">
    <el-form :model="formData" :disabled="disabled" label-width="auto" label-position="top">
      <el-row :gutter="16">
        <el-col :span="getFieldSpan(field)" v-for="field in fields" :key="field.key">
          <el-form-item :label="field.label">
            <el-radio-group v-if="field.type === 'radio'" v-model="formData[field.key]">
              <el-radio v-for="opt in field.options" :key="opt.value" :value="opt.value">{{ opt.label }}</el-radio>
            </el-radio-group>
            <el-input v-else-if="field.type === 'textarea'" v-model="formData[field.key]"
                      type="textarea" :rows="2" :placeholder="field.placeholder || ''" />
            <el-input v-else v-model="formData[field.key]" :placeholder="field.placeholder || ''" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div class="form-actions" v-if="!disabled">
      <el-button type="primary" :loading="saving" @click="handleSave">保存问卷</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { saveQuestionnaire } from '@/api/school'

const props = defineProps({
  type: { type: String, required: true },
  data: { type: Object, default: () => ({}) },
  disabled: { type: Boolean, default: false },
  studentId: { type: String, required: true }
})

const emit = defineEmits(['saved'])
const saving = ref(false)
const formData = reactive({})

// Likert量表通用选项
const likertOptions = [
  { value: '1', label: '不符合' },
  { value: '2', label: '有点符合' },
  { value: '3', label: '完全符合' }
]

const frequencyOptions = [
  { value: '1', label: '从不' },
  { value: '2', label: '偶尔' },
  { value: '3', label: '经常' },
  { value: '4', label: '总是' }
]

// 问卷字段配置
const fieldConfigs = {
  cchkn: [
    { key: 'ntlbrgs', label: '能体谅到别人的感受', type: 'radio', options: likertOptions },
    { key: 'bad', label: '不安定、过分活跃、不能长久安静', type: 'radio', options: likertOptions },
    { key: 'jcdzt', label: '经常抱怨头痛、肚子痛或身体不舒服', type: 'radio', options: likertOptions },
    { key: 'lyfx', label: '很乐意与别的小孩分享东西', type: 'radio', options: likertOptions },
    { key: 'jcfpq', label: '经常发脾气或大吵大闹', type: 'radio', options: likertOptions },
    { key: 'bjgd', label: '比较孤独，喜欢自己一个人玩', type: 'radio', options: likertOptions },
    { key: 'bjsc', label: '一般来说，比较顺从', type: 'radio', options: likertOptions },
    { key: 'hdyy', label: '有很多担忧，经常表现出忧虑', type: 'radio', options: likertOptions },
    { key: 'lybz', label: '如果有人受伤，都很乐意提供帮助', type: 'radio', options: likertOptions },
    { key: 'jczlba', label: '经常的坐立不安或躁动', type: 'radio', options: likertOptions },
    { key: 'yhpy', label: '有一个或一个以上的好朋友', type: 'radio', options: likertOptions },
    { key: 'cjqfbr', label: '经常与别的小孩吵架或欺负其他小孩子', type: 'radio', options: likertOptions },
    { key: 'jcbgx', label: '经常不高兴、情绪低落或哭泣', type: 'radio', options: likertOptions },
    { key: 'sxpyxh', label: '一般来说，受别的小孩所喜欢', type: 'radio', options: likertOptions },
    { key: 'ryfx', label: '容易分心，注意力不集中', type: 'radio', options: likertOptions },
    { key: 'xhjjz', label: '在新环境下，会紧张或粘住大人', type: 'radio', options: likertOptions },
    { key: 'dljxyh', label: '爱对年纪小的儿童和善', type: 'radio', options: likertOptions },
    { key: 'jcshqp', label: '经常撒谎或欺骗', type: 'radio', options: likertOptions },
    { key: 'sbrzn', label: '受别的小孩捉弄或欺负', type: 'radio', options: likertOptions },
    { key: 'zybzbr', label: '经常自愿的帮助别人', type: 'radio', options: likertOptions },
    { key: 'zsqxqc', label: '做事前会想清楚', type: 'radio', options: likertOptions },
    { key: 'htdx', label: '会从事家里、学校或其他地方偷东西', type: 'radio', options: likertOptions },
    { key: 'hdrrq', label: '跟大人相处比跟小孩子相处融洽', type: 'radio', options: likertOptions },
    { key: 'ryjx', label: '对很多事情容易感到害怕', type: 'radio', options: likertOptions },
    { key: 'zylcj', label: '做事情能做到底，注意力持久', type: 'radio', options: likertOptions },
  ],
  cbq: [
    { key: 'zscm', label: '似乎总是匆匆忙忙地从一个地方到另一个地方', type: 'radio', options: frequencyOptions },
    { key: 'bzzsl', label: '当被阻止做想做的事时，会变得非常失落', type: 'radio', options: frequencyOptions },
    { key: 'htzz', label: '在书上画图或涂色时表现得非常专注', type: 'radio', options: frequencyOptions },
    { key: 'xhmxhd', label: '喜欢滑高的滑梯或其它冒险性活动', type: 'radio', options: frequencyOptions },
    { key: 'xcsba', label: '会因为很小的切伤或擦伤而非常不安', type: 'radio', options: frequencyOptions },
    { key: 'nwczb', label: '能为旅行或外出准备', type: 'radio', options: frequencyOptions },
    { key: 'mrjrxdf', label: '经常贸然进入新情境', type: 'radio', options: frequencyOptions },
    { key: 'jtjhmdx', label: '如果家庭计划没有兑现会非常失落', type: 'radio', options: frequencyOptions },
    { key: 'xhdtrcg', label: '喜欢别人对他唱歌', type: 'radio', options: frequencyOptions },
    { key: 'drhrbjs', label: '好像对任何人都不拘束', type: 'radio', options: frequencyOptions },
  ],
  mqzyfs: [
    { key: 'hzbgxbpp', label: '只要孩子不高兴，犯了错误，也不批评', type: 'radio', options: frequencyOptions },
    { key: 'hzysmgsm', label: '孩子要什么就给什么', type: 'radio', options: frequencyOptions },
    { key: 'hzsffcwsw', label: '孩子是否服从自己无所谓', type: 'radio', options: frequencyOptions },
    { key: 'hzgxbxyq', label: '只要孩子高兴，可以不惜一切', type: 'radio', options: frequencyOptions },
    { key: 'hzbfcdm', label: '孩子不服从家长时打骂孩子', type: 'radio', options: frequencyOptions },
    { key: 'glhzzs', label: '鼓励孩子做他会做的事', type: 'radio', options: frequencyOptions },
    { key: 'gjhzxqpytc', label: '根据孩子本人的兴趣培养他的特长', type: 'radio', options: frequencyOptions },
    { key: 'yxchtw', label: '在和孩子谈话时允许孩子插话提问', type: 'radio', options: frequencyOptions },
    { key: 'wmyyzpp', label: '当孩子做错时问明原因再批评', type: 'radio', options: frequencyOptions },
    { key: 'rhzmbwsm', label: '吩咐孩子做事时让孩子明白为什么', type: 'radio', options: frequencyOptions },
  ],
  qzhd: [
    { key: 'yhzyqks', label: '与孩子一起阅读、看图画书', type: 'radio', options: frequencyOptions },
    { key: 'jhzsdgl', label: '在生活中教孩子数的概念', type: 'radio', options: frequencyOptions },
    { key: 'tthh', label: '涂涂画画', type: 'radio', options: frequencyOptions },
    { key: 'yhzyqyx', label: '跟孩子一起玩开发智力的游戏', type: 'radio', options: frequencyOptions },
    { key: 'yqsz', label: '结合日常生活与孩子一起识字', type: 'radio', options: frequencyOptions },
    { key: 'yqcg', label: '一起听唱歌曲、诗歌、童谣', type: 'radio', options: frequencyOptions },
    { key: 'jgs', label: '讲故事', type: 'radio', options: frequencyOptions },
    { key: 'zsg', label: '做手工', type: 'radio', options: frequencyOptions },
    { key: 'zyd', label: '做运动', type: 'radio', options: frequencyOptions },
    { key: 'jhzzl', label: '教孩子生活自理技能', type: 'radio', options: frequencyOptions },
    { key: 'yhztlzw', label: '与孩子谈论周围发生的一些事', type: 'radio', options: frequencyOptions },
    { key: 'yhzrsdzr', label: '与孩子一起认识大自然的动植物', type: 'radio', options: frequencyOptions },
  ],
  pmbl: [
    { key: 'dycyn', label: '第一次接触电子屏幕的月龄', type: 'input', placeholder: '月龄' },
    { key: 'mtjcsj', label: '平均每天接触电子屏幕时间', type: 'input', placeholder: '分钟' },
    { key: 'ptgksj', label: '陪同观看的时间', type: 'input', placeholder: '分钟' },
    { key: 'ptgkjlsj', label: '与其交流电视内容的时间', type: 'input', placeholder: '分钟' },
  ],
  sthd: [
    { key: 'cjzqdpl', label: '中强度身体活动频率（每周几天）', type: 'input', placeholder: '天' },
    { key: 'dssjzqd', label: '中高强度身体活动时间（分钟/天）', type: 'input', placeholder: '分钟' },
    { key: 'cjdqdpl', label: '低强度身体活动频率（每周几天）', type: 'input', placeholder: '天' },
    { key: 'dssjdqd', label: '低强度身体活动时间（分钟/天）', type: 'input', placeholder: '分钟' },
    { key: 'jzpl', label: '静坐频率（每周几天）', type: 'input', placeholder: '天' },
    { key: 'jzsj', label: '每天静坐时间', type: 'input', placeholder: '分钟' },
    { key: 'pmjzpl', label: '屏幕前静坐频率（每周几天）', type: 'input', placeholder: '天' },
    { key: 'pmjzsj', label: '屏幕前静坐时间', type: 'input', placeholder: '分钟' },
  ],
  smxg: [
    { key: 'gdsjscsj', label: '在固定时间上床睡觉', type: 'radio', options: frequencyOptions },
    { key: 'esfzrs', label: '上床后20分钟内入睡', type: 'radio', options: frequencyOptions },
    { key: 'dzrs', label: '在自己床上独自入睡', type: 'radio', options: frequencyOptions },
    { key: 'hkjrs', label: '抗拒去睡觉', type: 'radio', options: frequencyOptions },
    { key: 'hzhpha', label: '害怕在黑暗中睡觉', type: 'radio', options: frequencyOptions },
    { key: 'hzhpyr', label: '害怕独自一个人睡觉', type: 'radio', options: frequencyOptions },
    { key: 'wsnc', label: '晚上会尿床', type: 'radio', options: frequencyOptions },
    { key: 'smsmh', label: '睡眠中说梦话', type: 'radio', options: frequencyOptions },
    { key: 'smbaw', label: '睡眠中不安稳，常动来动去', type: 'radio', options: frequencyOptions },
    { key: 'dhl', label: '睡眠中打鼾', type: 'radio', options: frequencyOptions },
    { key: 'hzmtsm', label: '每天的睡眠时间', type: 'input', placeholder: '小时' },
    { key: 'qckn', label: '早晨起床困难', type: 'radio', options: frequencyOptions },
    { key: 'kqlhpj', label: '看起来很疲倦', type: 'radio', options: frequencyOptions },
  ]
}

const fields = computed(() => fieldConfigs[props.type] || [])

/**
 * 根据字段类型动态计算列宽
 * radio 类型占整行(24)，input/textarea 类型占半行(12)
 */
function getFieldSpan(field) {
  if (field.type === 'radio') return 24
  if (field.type === 'textarea') return 24
  return 12
}

// 监听数据变化，初始化表单
watch(() => props.data, (newData) => {
  if (newData) {
    fields.value.forEach(field => {
      formData[field.key] = newData[field.key] || ''
    })
  }
}, { immediate: true, deep: true })

async function handleSave() {
  saving.value = true
  try {
    await saveQuestionnaire(props.studentId, props.type, formData)
    ElMessage.success('问卷保存成功')
    emit('saved', props.type)
  } catch (error) {
    console.error('保存问卷失败', error)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.questionnaire-form {
  padding: 16px 0;
}
.form-actions {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
