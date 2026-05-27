<template>
  <view class="bind-page">
    <view class="brand-section">
      <text class="brand-icon">🔗</text>
      <text class="brand-title">绑定医生账号</text>
      <text class="brand-sub">首次使用微信登录，请输入PC端账号密码完成绑定</text>
    </view>

    <view class="card">
      <view class="form-item">
        <text class="form-label">用户名</text>
        <input class="form-input" v-model="form.username" placeholder="请输入PC端登录用户名" />
      </view>
      <view class="form-item">
        <text class="form-label">密码</text>
        <input class="form-input" v-model="form.password" type="password" placeholder="请输入密码"
               @confirm="handleBind" />
      </view>
      <view class="btn-primary mt-30" @click="handleBind" :class="{ disabled: loading }">
        {{ loading ? '绑定中...' : '确认绑定' }}
      </view>
    </view>

    <view class="tip text-gray text-center mt-30">
      <text>绑定后，下次可直接使用微信快捷登录</text>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useDoctorStore } from '@/stores/user'

const doctorStore = useDoctorStore()
const loading = ref(false)
const openid = ref('')

const form = reactive({
  username: '',
  password: ''
})

onLoad((options) => {
  openid.value = options.openid || uni.getStorageSync('wx_openid_temp') || ''
  if (!openid.value) {
    uni.showToast({ title: '微信信息缺失，请重新登录', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1500)
  }
})

async function handleBind() {
  if (!form.username) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!form.password) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  loading.value = true
  try {
    await doctorStore.bindWxAccount(openid.value, form.username, form.password)
    uni.removeStorageSync('wx_openid_temp')
    uni.showToast({ title: '绑定成功', icon: 'success' })
    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 500)
  } catch (err) {
    console.error('绑定失败', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.bind-page {
  min-height: 100vh; padding: 60rpx 30rpx;
  background: linear-gradient(180deg, #ECF5FF 0%, #FFFFFF 30%);
}
.brand-section { text-align: center; margin-bottom: 40rpx; padding-top: 60rpx; }
.brand-icon { font-size: 80rpx; display: block; }
.brand-title { font-size: 36rpx; font-weight: 700; display: block; margin-top: 20rpx; }
.brand-sub { font-size: 26rpx; color: #909399; display: block; margin-top: 12rpx; }

.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 28rpx; color: #666; margin-bottom: 10rpx; display: block; }
.form-input {
  height: 80rpx; line-height: 80rpx; padding: 0 24rpx;
  background: #f5f5f5; border-radius: 12rpx; font-size: 30rpx;
}
.tip { font-size: 24rpx; }
.disabled { opacity: 0.6; pointer-events: none; }
</style>
