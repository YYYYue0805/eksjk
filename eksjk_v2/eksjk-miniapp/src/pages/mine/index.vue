<template>
  <view class="mine-page">
    <!-- 用户信息卡片 -->
    <view class="user-card card">
      <view class="flex-row">
        <image class="avatar" :src="profile.myselfPicture || '/static/default-avatar.png'" mode="aspectFill" />
        <view class="user-info flex-1">
          <text class="user-name">{{ profile.contactsName || '未设置姓名' }}</text>
          <text class="user-phone text-gray">{{ profile.phoneNum || '未绑定手机号' }}</text>
        </view>
        <view class="edit-btn" @click="goEditProfile">
          <text>编辑</text>
        </view>
      </view>
    </view>

    <!-- 医生绑定 -->
    <view class="section-title">绑定医生</view>
    <view class="card" @click="handleDoctorAction">
      <view v-if="doctorInfo.bound" class="flex-between">
        <view>
          <text class="doctor-name">{{ doctorInfo.realName }}</text>
          <text class="doctor-dept text-gray mt-10">{{ doctorInfo.department }} · {{ doctorInfo.hospitalName }}</text>
        </view>
        <text class="text-danger" @click.stop="unbindDoctor">解绑</text>
      </view>
      <view v-else class="flex-center" style="padding: 20rpx 0;">
        <text class="text-primary">+ 绑定医生</text>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="section-title">更多</view>
    <view class="menu-list card">
      <view class="menu-item flex-between" @click="goEditProfile">
        <text>个人信息</text>
        <text class="text-gray">›</text>
      </view>
      <view class="menu-item flex-between" @click="clearCache">
        <text>清除缓存</text>
        <text class="text-gray">›</text>
      </view>
      <view class="menu-item flex-between">
        <text>关于系统</text>
        <text class="text-gray">v1.0.0</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-btn" @click="handleLogout">
      <text>退出登录</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const profile = ref({})
const doctorInfo = ref({ bound: false })

onMounted(() => {
  loadProfile()
  loadDoctor()
})

async function loadProfile() {
  try {
    const res = await get('/api/miniapp/profile')
    profile.value = res.data || {}
  } catch (e) {
    console.error('加载个人信息失败', e)
  }
}

async function loadDoctor() {
  try {
    const res = await get('/api/miniapp/doctor')
    doctorInfo.value = res.data || { bound: false }
  } catch (e) {
    console.error('加载医生信息失败', e)
  }
}

function goEditProfile() {
  uni.navigateTo({ url: '/pages-sub/profile/edit' })
}

function handleDoctorAction() {
  if (!doctorInfo.value.bound) {
    // 绑定医生：扫码或输入邀请码
    uni.showActionSheet({
      itemList: ['扫描医生二维码', '输入邀请码'],
      success: (res) => {
        if (res.tapIndex === 0) {
          uni.scanCode({
            success: async (scanRes) => {
              try {
                await post('/api/miniapp/doctor/bind', { doctorId: scanRes.result })
                uni.showToast({ title: '绑定成功', icon: 'success' })
                loadDoctor()
              } catch (e) {
                console.error('绑定失败', e)
              }
            }
          })
        } else {
          // 输入邀请码
          uni.showModal({
            title: '输入医生邀请码',
            editable: true,
            placeholderText: '请输入邀请码',
            success: async (modalRes) => {
              if (modalRes.confirm && modalRes.content) {
                try {
                  await post('/api/miniapp/doctor/bind', { doctorId: modalRes.content })
                  uni.showToast({ title: '绑定成功', icon: 'success' })
                  loadDoctor()
                } catch (e) {
                  console.error('绑定失败', e)
                }
              }
            }
          })
        }
      }
    })
  }
}

function unbindDoctor() {
  uni.showModal({
    title: '确认解绑',
    content: '解绑后将无法同步数据给医生，确定解绑吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await post('/api/miniapp/doctor/unbind')
          uni.showToast({ title: '已解绑', icon: 'success' })
          doctorInfo.value = { bound: false }
        } catch (e) {
          console.error('解绑失败', e)
        }
      }
    }
  })
}

function clearCache() {
  uni.showModal({
    title: '清除缓存',
    content: '确定清除本地缓存吗？',
    success: (res) => {
      if (res.confirm) {
        uni.clearStorageSync()
        uni.showToast({ title: '缓存已清除', icon: 'success' })
      }
    }
  })
}

function handleLogout() {
  uni.showModal({
    title: '退出登录',
    content: '确定退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages-sub/login/index' })
      }
    }
  })
}
</script>

<style scoped>
.mine-page { padding-bottom: 30rpx; }

.user-card { margin-top: 0; border-radius: 0 0 16rpx 16rpx; }
.avatar { width: 100rpx; height: 100rpx; border-radius: 50%; margin-right: 24rpx; background: #f0f0f0; }
.user-name { font-size: 34rpx; font-weight: 600; display: block; }
.user-phone { font-size: 26rpx; display: block; margin-top: 8rpx; }
.edit-btn {
  padding: 10rpx 24rpx; border: 2rpx solid #409EFF; border-radius: 30rpx;
  color: #409EFF; font-size: 24rpx;
}

.section-title { font-size: 30rpx; font-weight: 600; padding: 30rpx 30rpx 10rpx; }

.doctor-name { font-size: 30rpx; font-weight: 500; display: block; }
.doctor-dept { font-size: 24rpx; display: block; }

.menu-list { padding: 0; }
.menu-item {
  padding: 30rpx; border-bottom: 1rpx solid #f0f0f0; font-size: 30rpx;
}
.menu-item:last-child { border-bottom: none; }

.logout-btn {
  margin: 40rpx 30rpx; text-align: center; padding: 24rpx;
  background: #fff; border-radius: 16rpx; color: #F56C6C; font-size: 30rpx;
}
</style>
