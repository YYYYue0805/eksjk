<template>
  <view class="followup-page">
    <view class="card">
      <view class="form-title">快捷随访 — {{ patientName }}</view>

      <view class="form-item">
        <text class="form-label">随访日期</text>
        <picker mode="date" :value="form.measureDate" @change="e => form.measureDate = e.detail.value">
          <view class="form-input">{{ form.measureDate || '请选择日期' }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">身高 (cm)</text>
        <input class="form-input" type="digit" v-model="form.height" placeholder="请输入身高" />
      </view>

      <view class="form-item">
        <text class="form-label">体重 (kg)</text>
        <input class="form-input" type="digit" v-model="form.weight" placeholder="请输入体重" />
      </view>

      <view class="form-item">
        <text class="form-label">BMI（自动计算）</text>
        <view class="form-input readonly">{{ computedBmi || '—' }}</view>
      </view>

      <view class="form-item">
        <text class="form-label">骨龄</text>
        <input class="form-input" v-model="form.boneAge" placeholder="选填，如 10-6" />
      </view>

      <view class="form-item">
        <text class="form-label">备注</text>
        <textarea class="form-textarea" v-model="form.remark" placeholder="选填，记录其他信息" />
      </view>

      <view class="btn-primary mt-30" @click="handleSave" :class="{ disabled: saving }">
        {{ saving ? '保存中...' : '保存随访' }}
      </view>
    </view>

    <view class="tip text-gray text-center mt-20">
      <text>如需录入完整随访数据，请前往PC端操作</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { post } from '@/utils/request'

const patientId = ref('')
const patientName = ref('')
const saving = ref(false)

const today = new Date().toISOString().split('T')[0]
const form = reactive({
  measureDate: today,
  height: '',
  weight: '',
  boneAge: '',
  remark: ''
})

onLoad((options) => {
  patientId.value = options.patientId || ''
  patientName.value = decodeURIComponent(options.patientName || '')
})

const computedBmi = computed(() => {
  if (form.height && form.weight) {
    try {
      const h = parseFloat(form.height) / 100
      const w = parseFloat(form.weight)
      if (h > 0 && w > 0) {
        return (w / (h * h)).toFixed(1)
      }
    } catch (e) { /* 忽略 */ }
  }
  return ''
})

async function handleSave() {
  if (!form.height) {
    uni.showToast({ title: '请输入身高', icon: 'none' })
    return
  }
  if (!form.weight) {
    uni.showToast({ title: '请输入体重', icon: 'none' })
    return
  }

  saving.value = true
  try {
    await post('/api/doctor-app/followup', {
      patientId: patientId.value,
      measureDate: form.measureDate,
      height: form.height,
      weight: form.weight,
      boneAge: form.boneAge,
      remark: form.remark
    })
    uni.showToast({ title: '保存成功', icon: 'success' })
    uni.$emit('followUpCreated')
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    console.error('保存随访失败', e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.followup-page { padding-bottom: 30rpx; }
.form-title { font-size: 32rpx; font-weight: 600; margin-bottom: 30rpx; }

.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 28rpx; color: #666; margin-bottom: 10rpx; display: block; }
.form-input {
  height: 80rpx; line-height: 80rpx; padding: 0 24rpx;
  background: #f5f5f5; border-radius: 12rpx; font-size: 30rpx;
}
.form-input.readonly { color: #409EFF; font-weight: 500; }
.form-textarea {
  width: 100%; min-height: 160rpx; padding: 20rpx 24rpx;
  background: #f5f5f5; border-radius: 12rpx; font-size: 30rpx;
  box-sizing: border-box;
}

.tip { font-size: 24rpx; }
.disabled { opacity: 0.6; pointer-events: none; }
</style>
