<template>
  <view class="profile-edit-page">
    <view class="card">
      <view class="form-item">
        <text class="form-label">联系人姓名</text>
        <input class="form-input" v-model="form.contactsName" placeholder="请输入姓名" />
      </view>

      <view class="form-item">
        <text class="form-label">联系电话</text>
        <input class="form-input" type="number" v-model="form.contactsNum" placeholder="请输入联系电话" />
      </view>

      <view class="form-item">
        <text class="form-label">邮箱</text>
        <input class="form-input" v-model="form.email" placeholder="请输入邮箱" />
      </view>

      <view class="form-item">
        <text class="form-label">身份证号</text>
        <input class="form-input" v-model="form.idcard" placeholder="请输入身份证号" />
      </view>

      <view class="form-item">
        <text class="form-label">家庭住址</text>
        <input class="form-input" v-model="form.natPla" placeholder="请输入家庭住址" />
      </view>
    </view>

    <view class="btn-area">
      <view class="btn-primary" @click="handleSave" :class="{ disabled: saving }">
        {{ saving ? '保存中...' : '保存' }}
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, put } from '@/utils/request'

const saving = ref(false)
const form = reactive({
  contactsName: '',
  contactsNum: '',
  email: '',
  idcard: '',
  natPla: ''
})

onMounted(() => {
  loadProfile()
})

async function loadProfile() {
  try {
    const res = await get('/api/miniapp/profile')
    const data = res.data || {}
    Object.keys(form).forEach(key => {
      if (data[key] !== undefined && data[key] !== null) {
        form[key] = data[key]
      }
    })
  } catch (e) {
    console.error('加载个人信息失败', e)
  }
}

async function handleSave() {
  saving.value = true
  try {
    await put('/api/miniapp/profile', form)
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 500)
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.profile-edit-page { padding-bottom: 120rpx; }

.form-item { margin-bottom: 24rpx; }
.form-label { font-size: 28rpx; color: #666; margin-bottom: 10rpx; display: block; }
.form-input {
  height: 80rpx; line-height: 80rpx; padding: 0 24rpx;
  background: #f5f5f5; border-radius: 12rpx; font-size: 30rpx;
}

.btn-area { padding: 30rpx; position: fixed; bottom: 0; left: 0; right: 0; background: #fff; }
.disabled { opacity: 0.6; pointer-events: none; }
</style>
