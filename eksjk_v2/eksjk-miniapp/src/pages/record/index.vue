<template>
  <view class="record-page">
    <!-- 选择宝宝 -->
    <view v-if="babyList.length > 0">
      <scroll-view scroll-x class="baby-scroll">
        <view class="baby-tab" v-for="baby in babyList" :key="baby.id"
              :class="{ active: selectedBaby?.id === baby.id }"
              @click="selectBaby(baby)">
          <text class="tab-emoji">{{ baby.sex === '1' ? '👦' : '👧' }}</text>
          <text class="tab-name">{{ baby.name }}</text>
        </view>
      </scroll-view>
    </view>

    <!-- 评测记录列表 -->
    <view v-if="records.length > 0" class="record-list">
      <view class="record-item card" v-for="(record, index) in records" :key="record.id">
        <view class="flex-between">
          <text class="record-date">{{ record.measureDate }}</text>
          <text class="record-index text-gray">#{{ records.length - index }}</text>
        </view>
        <view class="record-data mt-10">
          <view class="data-item">
            <text class="data-value">{{ record.height || '-' }}</text>
            <text class="data-label">身高(cm)</text>
          </view>
          <view class="data-item">
            <text class="data-value">{{ record.weight || '-' }}</text>
            <text class="data-label">体重(kg)</text>
          </view>
          <view class="data-item">
            <text class="data-value">{{ record.bmi || '-' }}</text>
            <text class="data-label">BMI</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else-if="!loading" class="empty-state card">
      <text class="empty-icon">📋</text>
      <text class="empty-text">暂无评测记录</text>
      <text class="empty-sub text-gray">去评测页面记录宝宝身高体重吧</text>
    </view>

    <!-- 加载中 -->
    <view v-if="loading" class="loading-state">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

const babyList = ref([])
const selectedBaby = ref(null)
const records = ref([])
const loading = ref(false)

onMounted(() => {
  loadBabyList()
})

async function loadBabyList() {
  try {
    const res = await get('/api/miniapp/babies')
    babyList.value = res.data || []
    if (babyList.value.length > 0) {
      selectBaby(babyList.value[0])
    }
  } catch (e) {
    console.error('加载宝宝列表失败', e)
  }
}

async function selectBaby(baby) {
  selectedBaby.value = baby
  await loadRecords(baby.id)
}

async function loadRecords(babyId) {
  loading.value = true
  try {
    const res = await get(`/api/miniapp/assess/history/${babyId}`)
    records.value = res.data || []
  } catch (e) {
    console.error('加载评测记录失败', e)
    records.value = []
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.record-page { padding-bottom: 30rpx; }

.baby-scroll { white-space: nowrap; padding: 20rpx; }
.baby-tab {
  display: inline-flex; flex-direction: column; align-items: center;
  padding: 20rpx 30rpx; margin-right: 16rpx;
  background: #fff; border-radius: 16rpx; border: 2rpx solid #eee;
}
.baby-tab.active { border-color: #409EFF; background: #ECF5FF; }
.tab-emoji { font-size: 40rpx; }
.tab-name { font-size: 24rpx; margin-top: 8rpx; }

.record-list { padding: 0 10rpx; }
.record-item { margin: 10rpx 10rpx; }
.record-date { font-size: 28rpx; font-weight: 600; }
.record-index { font-size: 24rpx; }

.record-data { display: flex; justify-content: space-around; }
.data-item { text-align: center; }
.data-value { font-size: 36rpx; font-weight: 600; color: #409EFF; display: block; }
.data-label { font-size: 22rpx; color: #999; margin-top: 6rpx; display: block; }

.empty-state { text-align: center; padding: 80rpx 30rpx; margin: 20rpx; }
.empty-icon { font-size: 80rpx; display: block; }
.empty-text { font-size: 30rpx; font-weight: 500; display: block; margin-top: 20rpx; }
.empty-sub { font-size: 26rpx; display: block; margin-top: 10rpx; }

.loading-state { text-align: center; padding: 40rpx; color: #999; }
</style>
