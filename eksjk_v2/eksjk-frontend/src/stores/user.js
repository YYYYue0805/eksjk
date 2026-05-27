/**
 * 用户状态管理 Store
 * 管理 Token 存储、用户信息、持久化到 localStorage
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore(
  'user',
  () => {
    // ==================== 状态 ====================
    /** 认证 Token */
    const token = ref('')

    /** 用户信息 */
    const userInfo = ref(null)

    // ==================== 计算属性 ====================
    /** 是否已登录 */
    const isLoggedIn = computed(() => !!token.value)

    /** 用户角色 */
    const role = computed(() => userInfo.value?.roleCode || '')

    /** 用户姓名 */
    const realName = computed(() => userInfo.value?.realName || '')

    /** 所属医院 ID */
    const hospitalId = computed(() => userInfo.value?.hospitalId || null)

    /** 是否为超级管理员 */
    const isSuperAdmin = computed(() => role.value === 'super_admin')

    /** 是否为医院管理员 */
    const isHospitalAdmin = computed(() => role.value === 'hospital_admin')

    /** 是否为普通医生 */
    const isDoctor = computed(() => role.value === 'doctor')

    // ==================== 方法 ====================
    /**
     * 设置 Token
     * @param {string} newToken
     */
    function setToken(newToken) {
      token.value = newToken
    }

    /**
     * 设置用户信息
     * @param {Object} info
     */
    function setUserInfo(info) {
      userInfo.value = info
    }

    /**
     * 清除用户状态（退出登录）
     */
    function clearUser() {
      token.value = ''
      userInfo.value = null
    }

    /**
     * 判断密码是否需要修改（首次登录或超过 6 个月）
     */
    const needChangePassword = computed(() => {
      if (!userInfo.value) return false
      return userInfo.value.passwordNeedChange === true
    })

    /**
     * 判断密码是否即将过期（提醒修改）
     */
    const passwordExpireSoon = computed(() => {
      if (!userInfo.value) return false
      return userInfo.value.passwordExpireSoon === true
    })

    return {
      // 状态
      token,
      userInfo,
      // 计算属性
      isLoggedIn,
      role,
      realName,
      hospitalId,
      isSuperAdmin,
      isHospitalAdmin,
      isDoctor,
      needChangePassword,
      passwordExpireSoon,
      // 方法
      setToken,
      setUserInfo,
      clearUser
    }
  },
  {
    // Pinia 持久化配置
    persist: {
      key: 'eksjk-user',
      storage: localStorage,
      pick: ['token', 'userInfo']
    }
  }
)
