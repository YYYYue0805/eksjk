/**
 * 用户状态管理 Store
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { post, get } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(uni.getStorageSync('token') || '')
  const openid = ref(uni.getStorageSync('openid') || '')
  const isNewUser = ref(false)
  const profile = ref({})

  /**
   * 微信登录
   */
  async function wxLogin() {
    return new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: async (loginRes) => {
          try {
            const res = await post('/api/miniapp/login', { code: loginRes.code })
            token.value = res.data.token
            openid.value = res.data.openid
            isNewUser.value = res.data.isNewUser

            uni.setStorageSync('token', res.data.token)
            uni.setStorageSync('openid', res.data.openid)

            resolve(res.data)
          } catch (err) {
            reject(err)
          }
        },
        fail: (err) => {
          uni.showToast({ title: '微信登录失败', icon: 'none' })
          reject(err)
        }
      })
    })
  }

  /**
   * 提交手机号完成注册
   */
  async function submitPhone(phoneNum) {
    const res = await post('/api/miniapp/login', { code: '', phoneNum })
    isNewUser.value = false
    return res
  }

  /**
   * 加载个人信息
   */
  async function loadProfile() {
    const res = await get('/api/miniapp/profile')
    profile.value = res.data || {}
    return profile.value
  }

  /**
   * 检查登录状态
   */
  function isLoggedIn() {
    return !!token.value && !!openid.value
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    openid.value = ''
    profile.value = {}
    isNewUser.value = false
    uni.removeStorageSync('token')
    uni.removeStorageSync('openid')
  }

  return {
    token,
    openid,
    isNewUser,
    profile,
    wxLogin,
    submitPhone,
    loadProfile,
    isLoggedIn,
    logout
  }
})
