<template>
  <view class="review-page">
    <view class="section-title">待审核数据</view>

    <view v-if="reviewList.length > 0">
      <view class="review-card card" v-for="item in reviewList" :key="item.id"
            @click="goDetail(item.id)">
        <view class="flex-between">
          <view>
            <text class="review-name">{{ item.parentName }} — {{ item.babyName }}</text>
            <text class="review-time text-gray mt-10" style="display:block;">提交时间: {{ item.submitTime }}</text>
          </view>
          <view class="tag tag-warning">待审核</view>
        </view>
        <view class="review-summary mt-20 flex-row flex-wrap">
          <view class="data-chip" v-if="item.height">
            <text class="chip-label">身高</text>
            <text class="chip-value">{{ item.height }} cm</text>
          </view>
          <view class="data-chip" v-if="item.weight">
            <text class="chip-label">体重</text>
            <text class="chip-value">{{ item.weight }} kg</text>
          </view>
          <view class="data-chip" v-if="item.sds">
            <text class="chip-label">SDS</text>
            <text class="chip-value">{{ item.sds }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state card">
      <text class="empty-icon">✅</text>
      <text class="empty-text">暂无待审核数据</text>
      <text class="empty-sub text-gray mt-10" style="display:block;">家长提交的评测数据将在这里显示</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

const reviewList = ref([])
const loading = ref(false)

onMounted(() => {
  loadPendingList()
})

async function loadPendingList() {
  loading.value = true
  try {
    const res = await get('/api/doctor-app/review/pending')
    reviewList.value = res.data || []
  } catch (e) {
    console.error('加载待审核列表失败', e)
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  uni.navigateTo({ url: `/pages-sub/review/detail?id=${id}` })
}
</script>

<style scoped>
.review-page { padding-bottom: 30rpx; }
.section-title { font-size: 30rpx; font-weight: 600; padding: 30rpx 30rpx 10rpx; }

.review-card { margin: 10rpx 20rpx; }
.review-name { font-size: 30rpx; font-weight: 500; }
.review-time { font-size: 24rpx; }
.review-summary { gap: 16rpx; }
.data-chip {
  background: #f5f5f5; border-radius: 12rpx; padding: 12rpx 20rpx;
  display: flex; flex-direction: column; align-items: center; min-width: 120rpx;
}
.chip-label { font-size: 22rpx; color: #999; }
.chip-value { font-size: 28rpx; font-weight: 600; margin-top: 4rpx; }

.empty-state { text-align: center; padding: 80rpx 30rpx; }
.empty-icon { font-size: 80rpx; display: block; }
.empty-text { font-size: 30rpx; font-weight: 500; display: block; margin-top: 20rpx; }
</style>
