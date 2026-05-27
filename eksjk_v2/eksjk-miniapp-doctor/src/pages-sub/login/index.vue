<template>
  <view class="login-page">
    <!-- 品牌区 -->
    <view class="brand-section">
      <text class="brand-icon">🩺</text>
      <text class="brand-title">儿科生长发育管理</text>
      <text class="brand-sub">医生版</text>
    </view>

    <!-- 登录方式切换 -->
    <view class="login-section">
      <!-- 微信快捷登录 -->
      <button class="wx-login-btn" @click="handleWxLogin" :loading="wxLoading">
        微信快捷登录
      </button>

      <view class="divider">
        <view class="divider-line"></view>
        <text class="divider-text">或</text>
        <view class="divider-line"></view>
      </view>

      <!-- 账号密码登录 -->
      <view class="account-form card">
        <view class="form-item">
          <text class="form-label">用户名</text>
          <input class="form-input" v-model="form.username" placeholder="请输入PC端登录用户名" />
        </view>
        <view class="form-item">
          <text class="form-label">密码</text>
          <input class="form-input" v-model="form.password" type="password" placeholder="请输入密码"
                 @confirm="handleAccountLogin" />
        </view>
        <button class="account-btn" @click="handleAccountLogin" :loading="accountLoading">
          账号密码登录
        </button>
      </view>
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
import { ref, reactive } from 'vue'
import { useDoctorStore } from '@/stores/user'

const doctorStore = useDoctorStore()
const wxLoading = ref(false)
const accountLoading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

/** 微信快捷登录 */
async function handleWxLogin() {
  wxLoading.value = true
  try {
    const result = await doctorStore.wxLogin()
    if (result.needBind) {
      // 未绑定，跳转绑定页
      uni.navigateTo({ url: `/pages-sub/login/bindAccount?openid=${result.openid}` })
    } else {
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 500)
    }
  } catch (err) {
    console.error('微信登录失败', err)
  } finally {
    wxLoading.value = false
  }
}

/** 账号密码登录 */
async function handleAccountLogin() {
  if (!form.username) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!form.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  accountLoading.value = true
  try {
    await doctorStore.accountLogin(form.username, form.password)
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 500)
  } catch (err) {
    console.error('账号登录失败', err)
  } finally {
    accountLoading.value = false
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

.brand-section { text-align: center; margin-bottom: 60rpx; }
.brand-icon { font-size: 100rpx; display: block; }
.brand-title { font-size: 40rpx; font-weight: 700; display: block; margin-top: 20rpx; color: #303133; }
.brand-sub { font-size: 28rpx; color: #409EFF; display: block; margin-top: 12rpx; font-weight: 500; }

.login-section { width: 100%; }
.wx-login-btn {
  width: 100%; height: 96rpx; line-height: 96rpx;
  background: #07C160; color: #fff; border-radius: 48rpx;
  font-size: 32rpx; font-weight: 500; border: none;
}
.wx-login-btn::after { border: none; }

.divider { display: flex; align-items: center; margin: 40rpx 0; }
.divider-line { flex: 1; height: 1rpx; background: #DCDFE6; }
.divider-text { padding: 0 24rpx; color: #C0C4CC; font-size: 24rpx; }

.account-form { padding: 30rpx; margin: 0; }
.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 28rpx; color: #666; margin-bottom: 10rpx; display: block; }
.form-input {
  height: 80rpx; line-height: 80rpx; padding: 0 24rpx;
  background: #f5f5f5; border-radius: 12rpx; font-size: 30rpx;
}
.account-btn {
  width: 100%; height: 88rpx; line-height: 88rpx;
  background: #409EFF; color: #fff; border-radius: 44rpx;
  font-size: 32rpx; font-weight: 500; border: none; margin-top: 10rpx;
}
.account-btn::after { border: none; }

.agreement {
  position: fixed; bottom: 60rpx;
  font-size: 22rpx; text-align: center;
}
</style>
