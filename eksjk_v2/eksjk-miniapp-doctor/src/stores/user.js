/**
 * 医生用户状态管理 Store
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { post, get } from '@/utils/request'

export const useDoctorStore = defineStore('doctor', () => {
  const token = ref(uni.getStorageSync('doctor_token') || '')
  const doctorInfo = ref(uni.getStorageSync('doctor_info') ? JSON.parse(uni.getStorageSync('doctor_info')) : {})

  /**
   * 微信登录（获取 code 后换取 token）
   */
  async function wxLogin() {
    return new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: async (loginRes) => {
          try {
            const res = await post('/api/doctor-app/wx-login', { code: loginRes.code })
            const data = res.data
            if (data.needBind) {
              // 微信未绑定医生账号，需要跳转绑定页
              uni.setStorageSync('wx_openid_temp', data.openid)
              resolve({ needBind: true, openid: data.openid })
            } else {
              // 登录成功
              saveLoginState(data)
              resolve(data)
            }
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
   * 账号密码登录
   */
  async function accountLogin(username, password) {
    const res = await post('/api/doctor-app/account-login', { username, password })
    saveLoginState(res.data)
    return res.data
  }

  /**
   * 绑定微信账号（首次微信登录时，输入PC端账号密码绑定）
   */
  async function bindWxAccount(openid, username, password) {
    const res = await post('/api/doctor-app/bind-wx', { openid, username, password })
    saveLoginState(res.data)
    return res.data
  }

  /**
   * 保存登录状态
   */
  function saveLoginState(data) {
    token.value = data.token
    doctorInfo.value = data.doctorInfo || {}
    uni.setStorageSync('doctor_token', data.token)
    uni.setStorageSync('doctor_info', JSON.stringify(doctorInfo.value))
  }

  /**
   * 加载医生个人信息
   */
  async function loadProfile() {
    const res = await get('/api/doctor-app/profile')
    doctorInfo.value = res.data || {}
    uni.setStorageSync('doctor_info', JSON.stringify(doctorInfo.value))
    return doctorInfo.value
  }

  /**
   * 检查登录状态
   */
  function isLoggedIn() {
    return !!token.value
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    doctorInfo.value = {}
    uni.removeStorageSync('doctor_token')
    uni.removeStorageSync('doctor_info')
  }

  return {
    token,
    doctorInfo,
    wxLogin,
    accountLogin,
    bindWxAccount,
    saveLoginState,
    loadProfile,
    isLoggedIn,
    logout
  }
})
