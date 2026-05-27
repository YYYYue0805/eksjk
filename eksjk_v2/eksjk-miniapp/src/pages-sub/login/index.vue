<template>
  <view class="login-page">
    <!-- 品牌区 -->
    <view class="brand-section">
      <text class="brand-icon">🏥</text>
      <text class="brand-title">儿科生长发育管理</text>
      <text class="brand-sub">记录宝宝每一步成长</text>
    </view>

    <!-- 登录按钮区 -->
    <view class="login-section">
      <button class="wx-login-btn" open-type="getPhoneNumber" @getphonenumber="onGetPhoneNumber">
        微信手机号快捷登录
      </button>

      <view class="divider">
        <view class="divider-line"></view>
        <text class="divider-text">或</text>
        <view class="divider-line"></view>
      </view>

      <button class="guest-btn" @click="guestLogin">
        跳过，先看看
      </button>
    </view>

    <!-- 协议 -->
    <view class="agreement">
      <text class="text-gray">登录即表示同意</text>
      <text class="text-primary">《用户协议》</text>
      <text class="text-gray">和</text>
      <text class="text-primary">《隐私政策》</text>
    </view>
  </view>
</template>

<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

/**
 * 微信手机号快捷登录
 */
async function onGetPhoneNumber(e) {
  if (e.detail.errMsg !== 'getPhoneNumber:ok') {
    uni.showToast({ title: '需要授权手机号才能登录', icon: 'none' })
    return
  }

  uni.showLoading({ title: '登录中...' })
  try {
    const loginData = await userStore.wxLogin()

    // 如果是新用户，需要提交手机号
    if (loginData.isNewUser) {
      // 手机号通过微信接口获取，这里简化处理
      await userStore.submitPhone(e.detail.code || '')
    }

    uni.hideLoading()
    uni.showToast({ title: '登录成功', icon: 'success' })

    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 500)
  } catch (err) {
    uni.hideLoading()
    console.error('登录失败', err)
  }
}

/**
 * 游客模式（仅微信登录，不获取手机号）
 */
async function guestLogin() {
  uni.showLoading({ title: '登录中...' })
  try {
    await userStore.wxLogin()
    uni.hideLoading()
    uni.switchTab({ url: '/pages/index/index' })
  } catch (err) {
    uni.hideLoading()
    console.error('登录失败', err)
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx;
  background: linear-gradient(180deg, #ECF5FF 0%, #FFFFFF 50%);
}

.brand-section {
  text-align: center;
  margin-bottom: 80rpx;
}
.brand-icon { font-size: 100rpx; display: block; }
.brand-title { font-size: 40rpx; font-weight: 700; display: block; margin-top: 20rpx; color: #303133; }
.brand-sub { font-size: 28rpx; color: #909399; display: block; margin-top: 12rpx; }

.login-section { width: 100%; }
.wx-login-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: #07C160;
  color: #fff;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 500;
  border: none;
}
.wx-login-btn::after { border: none; }

.divider {
  display: flex;
  align-items: center;
  margin: 40rpx 0;
}
.divider-line { flex: 1; height: 1rpx; background: #DCDFE6; }
.divider-text { padding: 0 24rpx; color: #C0C4CC; font-size: 24rpx; }

.guest-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: #fff;
  color: #606266;
  border-radius: 48rpx;
  font-size: 32rpx;
  border: 2rpx solid #DCDFE6;
}
.guest-btn::after { border: none; }

.agreement {
  position: fixed;
  bottom: 60rpx;
  font-size: 22rpx;
  text-align: center;
}
</style>
