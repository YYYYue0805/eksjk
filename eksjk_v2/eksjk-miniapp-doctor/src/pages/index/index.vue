<template>
  <view class="dashboard-page">
    <!-- 顶部欢迎区 -->
    <view class="welcome-section card">
      <view class="flex-between">
        <view>
          <text class="welcome-title">🩺 {{ doctorStore.doctorInfo.realName || '医生' }}，您好</text>
          <text class="welcome-sub">今天也要加油哦</text>
        </view>
      </view>
    </view>

    <!-- 数据概览卡片 -->
    <view class="stats-grid">
      <view class="stat-card" @click="goPatientList">
        <text class="stat-num">{{ stats.totalPatients || 0 }}</text>
        <text class="stat-label">患者总数</text>
      </view>
      <view class="stat-card" @click="goPatientList">
        <text class="stat-num text-primary">{{ stats.monthNew || 0 }}</text>
        <text class="stat-label">本月新增</text>
      </view>
      <view class="stat-card">
        <text class="stat-num text-warning">{{ stats.pendingFollowUp || 0 }}</text>
        <text class="stat-label">待随访</text>
      </view>
      <view class="stat-card" @click="goReview">
        <text class="stat-num text-danger">{{ stats.pendingReview || 0 }}</text>
        <text class="stat-label">待审核</text>
      </view>
    </view>

    <!-- 快捷操作 -->
    <view class="section-title">快捷操作</view>
    <view class="quick-actions flex-row">
      <view class="action-item" @click="goPatientList">
        <text class="action-icon">🔍</text>
        <text class="action-text">搜索患者</text>
      </view>
      <view class="action-item" @click="goReview">
        <text class="action-icon">📋</text>
        <text class="action-text">数据审核</text>
      </view>
      <view class="action-item" @click="goStats">
        <text class="action-icon">📊</text>
        <text class="action-text">数据统计</text>
      </view>
      <view class="action-item" @click="goQrCode">
        <text class="action-icon">📱</text>
        <text class="action-text">我的二维码</text>
      </view>
    </view>

    <!-- 最近新增患者 -->
    <view class="section-title">最近新增</view>
    <view v-if="stats.recentPatients && stats.recentPatients.length > 0">
      <view class="patient-item card" v-for="item in stats.recentPatients" :key="item.id"
            @click="goPatientDetail(item.id)">
        <view class="flex-between">
          <view class="flex-row">
            <view class="patient-avatar" :class="item.sex === '1' ? 'boy' : 'girl'">
              {{ item.sex === '1' ? '👦' : '👧' }}
            </view>
            <view>
              <text class="patient-name">{{ item.name }}</text>
              <text class="patient-dis text-gray mt-10">{{ getDisClassName(item.disClass) }}</text>
            </view>
          </view>
          <text class="patient-time text-gray">{{ item.cTime }}</text>
        </view>
      </view>
    </view>
    <view v-else class="empty-state card">
      <text class="empty-text text-gray">暂无最近新增患者</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '@/utils/request'
import { useDoctorStore } from '@/stores/user'

const doctorStore = useDoctorStore()
const stats = ref({})
const loading = ref(false)

onMounted(() => {
  if (!doctorStore.isLoggedIn()) {
    uni.navigateTo({ url: '/pages-sub/login/index' })
    return
  }
  loadDashboard()
})

onShow(() => {
  if (doctorStore.isLoggedIn()) {
    loadDashboard()
  }
})

async function loadDashboard() {
  loading.value = true
  try {
    const res = await get('/api/doctor-app/dashboard')
    stats.value = res.data || {}
  } catch (e) {
    console.error('加载工作台数据失败', e)
  } finally {
    loading.value = false
  }
}

function getDisClassName(disClass) {
  const map = {
    '10000001': 'DSD', '10000002': 'FSS', '10000003': 'CPP',
    '10000004': 'MAS', '10000005': 'SGA', '10000006': 'SSS', '10000007': 'ELTM'
  }
  return map[disClass] || '其他'
}

function goPatientList() {
  uni.switchTab({ url: '/pages/patient/index' })
}

function goReview() {
  uni.navigateTo({ url: '/pages-sub/review/index' })
}

function goStats() {
  uni.switchTab({ url: '/pages/stats/index' })
}

function goQrCode() {
  uni.navigateTo({ url: '/pages-sub/mine/qrcode' })
}

function goPatientDetail(id) {
  uni.navigateTo({ url: `/pages-sub/patient/detail?id=${id}` })
}
</script>

<style scoped>
.dashboard-page { padding-bottom: 30rpx; }
.welcome-section { margin-top: 0; border-radius: 0 0 16rpx 16rpx; }
.welcome-title { font-size: 36rpx; font-weight: 600; display: block; }
.welcome-sub { font-size: 26rpx; color: #999; margin-top: 8rpx; display: block; }

.stats-grid {
  display: flex; flex-wrap: wrap; padding: 10rpx 10rpx 0;
}
.stat-card {
  width: calc(50% - 20rpx); margin: 10rpx;
  background: #fff; border-radius: 16rpx; padding: 30rpx;
  text-align: center; box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
}
.stat-num { font-size: 48rpx; font-weight: 700; display: block; }
.stat-label { font-size: 24rpx; color: #999; display: block; margin-top: 8rpx; }

.section-title { font-size: 30rpx; font-weight: 600; padding: 30rpx 30rpx 10rpx; }

.quick-actions { padding: 10rpx 10rpx; justify-content: space-around; flex-wrap: wrap; }
.action-item {
  display: flex; flex-direction: column; align-items: center;
  background: #fff; border-radius: 16rpx; padding: 24rpx 30rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04); width: calc(25% - 16rpx); margin: 0 8rpx;
}
.action-icon { font-size: 44rpx; }
.action-text { font-size: 22rpx; color: #666; margin-top: 8rpx; }

.patient-item { margin: 10rpx 20rpx; padding: 24rpx 30rpx; }
.patient-avatar {
  width: 72rpx; height: 72rpx; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 32rpx; margin-right: 20rpx;
}
.patient-avatar.boy { background: #E3F2FD; }
.patient-avatar.girl { background: #FCE4EC; }
.patient-name { font-size: 30rpx; font-weight: 500; display: block; }
.patient-dis { font-size: 24rpx; display: block; }
.patient-time { font-size: 22rpx; }

.empty-state { text-align: center; padding: 40rpx; }
.empty-text { font-size: 28rpx; }
</style>
