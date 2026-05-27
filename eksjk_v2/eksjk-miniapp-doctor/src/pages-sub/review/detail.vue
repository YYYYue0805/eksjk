<template>
  <view class="review-detail-page">
    <view class="card">
      <view class="section-title">评测数据</view>
      <view class="info-row">
        <text class="info-label">家长</text>
        <text class="info-value">{{ detail.parentName || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">宝宝</text>
        <text class="info-value">{{ detail.babyName || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">提交时间</text>
        <text class="info-value">{{ detail.submitTime || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">身高</text>
        <text class="info-value">{{ detail.height ? detail.height + ' cm' : '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">体重</text>
        <text class="info-value">{{ detail.weight ? detail.weight + ' kg' : '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">SDS</text>
        <text class="info-value">{{ detail.sds || '-' }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">百分位</text>
        <text class="info-value">{{ detail.percentile || '-' }}</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-area">
      <view class="btn-success" @click="handleApprove" :class="{ disabled: submitting }">
        ✅ 审核通过
      </view>
      <view class="btn-danger mt-20" @click="handleReject" :class="{ disabled: submitting }">
        ❌ 审核拒绝
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get, post } from '@/utils/request'

const recordId = ref('')
const detail = ref({})
const submitting = ref(false)

onLoad((options) => {
  recordId.value = options.id || ''
  if (recordId.value) {
    loadDetail()
  }
})

async function loadDetail() {
  try {
    // 简化实现：实际应从后端获取审核详情
    detail.value = { parentName: '—', babyName: '—', submitTime: '—' }
  } catch (e) {
    console.error('加载审核详情失败', e)
  }
}

async function handleApprove() {
  uni.showModal({
    title: '确认通过',
    content: '审核通过后数据将同步到病例系统，确定吗？',
    success: async (res) => {
      if (res.confirm) {
        submitting.value = true
        try {
          await post(`/api/doctor-app/review/${recordId.value}`, { approved: true })
          uni.showToast({ title: '审核通过', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 500)
        } catch (e) {
          console.error('审核失败', e)
        } finally {
          submitting.value = false
        }
      }
    }
  })
}

async function handleReject() {
  uni.showModal({
    title: '拒绝原因',
    editable: true,
    placeholderText: '请输入拒绝原因',
    success: async (res) => {
      if (res.confirm && res.content) {
        submitting.value = true
        try {
          await post(`/api/doctor-app/review/${recordId.value}`, {
            approved: false,
            rejectReason: res.content
          })
          uni.showToast({ title: '已拒绝', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 500)
        } catch (e) {
          console.error('审核失败', e)
        } finally {
          submitting.value = false
        }
      }
    }
  })
}
</script>

<style scoped>
.review-detail-page { padding-bottom: 30rpx; }
.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 20rpx; }
.info-row {
  display: flex; justify-content: space-between; padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.info-row:last-child { border-bottom: none; }
.info-label { font-size: 28rpx; color: #999; }
.info-value { font-size: 28rpx; color: #333; }

.action-area { padding: 30rpx; }
.disabled { opacity: 0.6; pointer-events: none; }
</style>
