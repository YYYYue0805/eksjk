<template>
  <view class="assess-page">
    <!-- 选择宝宝 -->
    <view class="section-title">选择宝宝</view>
    <view v-if="babyList.length > 0">
      <scroll-view scroll-x class="baby-scroll">
        <view class="baby-tab" v-for="baby in babyList" :key="baby.id"
              :class="{ active: selectedBaby?.id === baby.id }"
              @click="selectedBaby = baby">
          <text class="tab-emoji">{{ baby.sex === '1' ? '👦' : '👧' }}</text>
          <text class="tab-name">{{ baby.name }}</text>
        </view>
      </scroll-view>
    </view>
    <view v-else class="empty-tip card">
      <text>请先添加宝宝</text>
      <view class="btn-primary btn-small mt-20" @click="goAddBaby">去添加</view>
    </view>

    <!-- 评测表单 -->
    <view v-if="selectedBaby" class="card mt-20">
      <view class="form-title">身高评测</view>

      <view class="form-item">
        <text class="form-label">测量日期</text>
        <picker mode="date" :value="form.measureDate" @change="onDateChange">
          <view class="form-input">{{ form.measureDate || '请选择日期' }}</view>
        </picker>
      </view>

      <view class="form-item">
        <text class="form-label">身高 (cm)</text>
        <input class="form-input" type="digit" v-model="form.height"
               placeholder="请输入身高" placeholder-class="placeholder" />
      </view>

      <view class="form-item">
        <text class="form-label">体重 (kg)</text>
        <input class="form-input" type="digit" v-model="form.weight"
               placeholder="请输入体重" placeholder-class="placeholder" />
      </view>

      <view class="btn-primary mt-30" @click="submitAssess" :class="{ disabled: submitting }">
        {{ submitting ? '评测中...' : '提交评测' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'

const babyList = ref([])
const selectedBaby = ref(null)
const submitting = ref(false)

const today = new Date().toISOString().split('T')[0]
const form = reactive({
  measureDate: today,
  height: '',
  weight: ''
})

onMounted(() => {
  loadBabyList()
})

async function loadBabyList() {
  try {
    const res = await get('/api/miniapp/babies')
    babyList.value = res.data || []
    if (babyList.value.length > 0) {
      selectedBaby.value = babyList.value[0]
    }
  } catch (e) {
    console.error('加载宝宝列表失败', e)
  }
}

function onDateChange(e) {
  form.measureDate = e.detail.value
}

async function submitAssess() {
  if (!selectedBaby.value) {
    uni.showToast({ title: '请先选择宝宝', icon: 'none' })
    return
  }
  if (!form.height) {
    uni.showToast({ title: '请输入身高', icon: 'none' })
    return
  }
  if (!form.weight) {
    uni.showToast({ title: '请输入体重', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const res = await post('/api/miniapp/assess', {
      babyId: selectedBaby.value.id,
      measureDate: form.measureDate,
      height: form.height,
      weight: form.weight
    })
    // 跳转到评测结果页
    const resultData = encodeURIComponent(JSON.stringify(res.data))
    uni.navigateTo({ url: `/pages-sub/assess/result?data=${resultData}` })
    // 重置表单
    form.height = ''
    form.weight = ''
  } catch (e) {
    console.error('评测失败', e)
  } finally {
    submitting.value = false
  }
}

function goAddBaby() {
  uni.navigateTo({ url: '/pages-sub/baby/edit?mode=create' })
}
</script>

<style scoped>
.assess-page { padding-bottom: 30rpx; }
.section-title { font-size: 30rpx; font-weight: 600; padding: 30rpx 30rpx 10rpx; }

.baby-scroll { white-space: nowrap; padding: 10rpx 20rpx; }
.baby-tab {
  display: inline-flex; flex-direction: column; align-items: center;
  padding: 20rpx 30rpx; margin-right: 16rpx;
  background: #fff; border-radius: 16rpx; border: 2rpx solid #eee;
}
.baby-tab.active { border-color: #409EFF; background: #ECF5FF; }
.tab-emoji { font-size: 40rpx; }
.tab-name { font-size: 24rpx; margin-top: 8rpx; }

.empty-tip { text-align: center; padding: 40rpx; color: #999; }
.btn-small { display: inline-block; padding: 0 40rpx; height: 64rpx; line-height: 64rpx; font-size: 28rpx; }

.form-title { font-size: 32rpx; font-weight: 600; margin-bottom: 30rpx; }
.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 28rpx; color: #666; margin-bottom: 10rpx; display: block; }
.form-input {
  height: 80rpx; line-height: 80rpx; padding: 0 24rpx;
  background: #f5f5f5; border-radius: 12rpx; font-size: 30rpx;
}
.placeholder { color: #ccc; }
.disabled { opacity: 0.6; pointer-events: none; }
</style>
