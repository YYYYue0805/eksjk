<template>
  <view class="index-page">
    <!-- 顶部欢迎区 -->
    <view class="welcome-section card">
      <view class="flex-between">
        <view>
          <text class="welcome-title">👋 您好，家长</text>
          <text class="welcome-sub">记录宝宝每一步成长</text>
        </view>
      </view>
    </view>

    <!-- 宝宝列表 -->
    <view class="section-title">我的宝宝</view>
    <view v-if="babyList.length > 0">
      <view class="baby-card card" v-for="baby in babyList" :key="baby.id" @click="goBabyDetail(baby)">
        <view class="flex-row">
          <view class="baby-avatar" :class="baby.sex === '1' ? 'boy' : 'girl'">
            {{ baby.sex === '1' ? '👦' : '👧' }}
          </view>
          <view class="baby-info flex-1">
            <view class="flex-between">
              <text class="baby-name">{{ baby.name }}</text>
              <text class="baby-age text-gray">{{ baby.ageDesc }}</text>
            </view>
            <view class="baby-data mt-10">
              <text v-if="baby.height">身高 {{ baby.height }}cm</text>
              <text v-if="baby.weight" class="ml-20">体重 {{ baby.weight }}kg</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state card">
      <text class="empty-icon">👶</text>
      <text class="empty-text">还没有添加宝宝</text>
      <text class="empty-sub text-gray">添加宝宝，开始记录成长</text>
    </view>

    <!-- 添加宝宝按钮 -->
    <view class="add-btn" @click="goAddBaby">
      <text class="btn-primary">+ 添加宝宝</text>
    </view>

    <!-- 快捷操作 -->
    <view class="section-title">快捷操作</view>
    <view class="quick-actions flex-row">
      <view class="action-item" @click="goAssess">
        <text class="action-icon">📏</text>
        <text class="action-text">身高评测</text>
      </view>
      <view class="action-item" @click="goRecord">
        <text class="action-icon">📊</text>
        <text class="action-text">评测记录</text>
      </view>
      <view class="action-item" @click="goMine">
        <text class="action-icon">👤</text>
        <text class="action-text">个人中心</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow, onHide } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const babyList = ref([])
const loading = ref(false)

onMounted(() => {
  uni.$on('babyListChanged', loadBabyList)
})

onShow(() => {
  if (!userStore.isLoggedIn()) {
    uni.navigateTo({ url: '/pages-sub/login/index' })
    return
  }
  loadBabyList()
})

onHide(() => {
  // 页面隐藏时不需要特殊处理
})

async function loadBabyList() {
  loading.value = true
  try {
    const res = await get('/api/miniapp/babies')
    babyList.value = res.data || []
  } catch (e) {
    console.error('加载宝宝列表失败', e)
  } finally {
    loading.value = false
  }
}

function goBabyDetail(baby) {
  uni.navigateTo({ url: `/pages-sub/baby/edit?id=${baby.id}&mode=view` })
}

function goAddBaby() {
  uni.navigateTo({ url: '/pages-sub/baby/edit?mode=create' })
}

function goAssess() {
  uni.switchTab({ url: '/pages/assess/index' })
}

function goRecord() {
  uni.switchTab({ url: '/pages/record/index' })
}

function goMine() {
  uni.switchTab({ url: '/pages/mine/index' })
}
</script>

<style scoped>
.index-page { padding-bottom: 30rpx; }
.welcome-section { margin-top: 0; border-radius: 0 0 16rpx 16rpx; }
.welcome-title { font-size: 36rpx; font-weight: 600; display: block; }
.welcome-sub { font-size: 26rpx; color: #999; margin-top: 8rpx; display: block; }

.section-title { font-size: 30rpx; font-weight: 600; padding: 30rpx 30rpx 10rpx; }

.baby-card { margin: 10rpx 20rpx; }
.baby-avatar {
  width: 90rpx; height: 90rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 40rpx; margin-right: 24rpx;
}
.baby-avatar.boy { background: #E3F2FD; }
.baby-avatar.girl { background: #FCE4EC; }
.baby-name { font-size: 32rpx; font-weight: 600; }
.baby-age { font-size: 24rpx; }
.baby-data { font-size: 24rpx; color: #666; }
.baby-data text + text { margin-left: 20rpx; }

.empty-state { text-align: center; padding: 60rpx 30rpx; }
.empty-icon { font-size: 80rpx; display: block; }
.empty-text { font-size: 30rpx; font-weight: 500; display: block; margin-top: 20rpx; }
.empty-sub { font-size: 26rpx; display: block; margin-top: 10rpx; }

.add-btn { padding: 0 30rpx; margin-top: 20rpx; }

.quick-actions { padding: 10rpx 20rpx; justify-content: space-around; }
.action-item {
  display: flex; flex-direction: column; align-items: center;
  background: #fff; border-radius: 16rpx; padding: 30rpx 40rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
}
.action-icon { font-size: 48rpx; }
.action-text { font-size: 24rpx; color: #666; margin-top: 10rpx; }
</style>
