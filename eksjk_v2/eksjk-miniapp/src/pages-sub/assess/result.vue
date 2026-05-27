<template>
  <view class="result-page">
    <!-- 评测结果卡片 -->
    <view class="result-card card">
      <view class="result-header">
        <text class="result-icon">📏</text>
        <text class="result-title">评测完成</text>
      </view>

      <!-- 核心数据 -->
      <view class="core-data">
        <view class="data-item">
          <text class="data-value">{{ result.height || '-' }}</text>
          <text class="data-unit">cm</text>
          <text class="data-label">身高</text>
        </view>
        <view class="data-divider"></view>
        <view class="data-item">
          <text class="data-value">{{ result.weight || '-' }}</text>
          <text class="data-unit">kg</text>
          <text class="data-label">体重</text>
        </view>
        <view class="data-divider"></view>
        <view class="data-item">
          <text class="data-value">{{ result.bmi || '-' }}</text>
          <text class="data-unit"></text>
          <text class="data-label">BMI</text>
        </view>
      </view>
    </view>

    <!-- 评测结论 -->
    <view class="card">
      <view class="section-title">评测结论</view>
      <view class="conclusion-text">{{ result.conclusion || '暂无评测结论' }}</view>
    </view>

    <!-- 与上次对比 -->
    <view class="card" v-if="result.heightGrowth">
      <view class="section-title">与上次对比</view>
      <view class="compare-row flex-between">
        <text>身高增长</text>
        <text class="text-primary text-bold">{{ result.heightGrowth }} cm</text>
      </view>
      <view class="compare-row flex-between" v-if="result.daysSinceLast">
        <text>距上次评测</text>
        <text class="text-gray">{{ result.daysSinceLast }} 天</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="btn-area">
      <view class="btn-primary" @click="goBack">返回首页</view>
      <view class="btn-secondary mt-20" @click="goRecord">查看历史记录</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const result = ref({})

onLoad((options) => {
  if (options.data) {
    try {
      result.value = JSON.parse(decodeURIComponent(options.data))
    } catch (e) {
      console.error('解析评测结果失败', e)
    }
  }
})

function goBack() {
  uni.switchTab({ url: '/pages/index/index' })
}

function goRecord() {
  uni.switchTab({ url: '/pages/record/index' })
}
</script>

<style scoped>
.result-page { padding-bottom: 30rpx; }

.result-card { text-align: center; }
.result-header { margin-bottom: 30rpx; }
.result-icon { font-size: 60rpx; display: block; }
.result-title { font-size: 36rpx; font-weight: 600; display: block; margin-top: 10rpx; }

.core-data { display: flex; justify-content: space-around; align-items: center; padding: 20rpx 0; }
.data-item { text-align: center; flex: 1; }
.data-value { font-size: 48rpx; font-weight: 700; color: #409EFF; }
.data-unit { font-size: 24rpx; color: #999; margin-left: 4rpx; }
.data-label { font-size: 24rpx; color: #999; display: block; margin-top: 8rpx; }
.data-divider { width: 1rpx; height: 60rpx; background: #eee; }

.section-title { font-size: 30rpx; font-weight: 600; margin-bottom: 20rpx; }
.conclusion-text { font-size: 28rpx; color: #606266; line-height: 1.6; }

.compare-row { padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5; font-size: 28rpx; }
.compare-row:last-child { border-bottom: none; }

.btn-area { padding: 30rpx; }
.btn-secondary {
  text-align: center; height: 88rpx; line-height: 88rpx;
  border: 2rpx solid #409EFF; border-radius: 44rpx;
  color: #409EFF; font-size: 32rpx;
}
</style>
