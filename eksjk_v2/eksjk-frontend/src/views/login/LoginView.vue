<template>
  <div class="login-page">
    <!-- 左侧品牌展示区 -->
    <div class="login-page__brand">
      <div class="login-page__brand-content">
        <img src="@/assets/vue.svg" alt="Logo" class="login-page__brand-logo" />
        <h1 class="login-page__brand-title">EKSJK</h1>
        <h2 class="login-page__brand-subtitle">儿科生长发育数据管理系统</h2>
        <p class="login-page__brand-desc">专注于儿童内分泌疾病的临床数据管理、患者随访跟踪与生长发育监测</p>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-page__form-area">
      <div class="login-page__form-wrapper">
        <h3 class="login-page__form-title">欢迎登录</h3>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <div class="login-page__options">
            <el-checkbox v-model="rememberUsername">记住用户名</el-checkbox>
            <el-link type="primary" :underline="false" @click="handleForgotPassword">
              忘记密码?
            </el-link>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-page__submit-btn"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 强制改密对话框 -->
    <el-dialog
      v-model="forceChangePasswordVisible"
      title="修改密码"
      width="420px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <p class="mb-base text-warning">首次登录或使用默认密码，请先修改密码后再使用系统。</p>
      <el-form ref="changePwdFormRef" :model="changePwdForm" :rules="changePwdRules" label-width="90px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="changePwdForm.newPassword" type="password" show-password placeholder="6-30位，需包含数字和字母" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="changePwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" :loading="changePwdLoading" @click="handleForceChangePassword">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 改密提醒对话框 -->
    <el-dialog
      v-model="remindChangePasswordVisible"
      title="安全提醒"
      width="400px"
    >
      <p>您的密码已超过 6 个月未修改，为了账号安全，建议您尽快修改密码。</p>
      <template #footer>
        <el-button @click="handleRemindLater">稍后再说</el-button>
        <el-button type="primary" @click="handleRemindNow">立即修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login as loginApi, getUserInfo, changePassword } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

// ==================== 登录表单 ====================
const loginFormRef = ref(null)
const loading = ref(false)
const rememberUsername = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// ==================== 改密表单 ====================
const forceChangePasswordVisible = ref(false)
const remindChangePasswordVisible = ref(false)
const changePwdFormRef = ref(null)
const changePwdLoading = ref(false)
const changePwdForm = ref({ newPassword: '', confirmPassword: '' })

const changePwdRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度为 6-30 位', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码需包含数字和字母', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== changePwdForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 临时存储登录响应
let loginResponse = null

// ==================== 生命周期 ====================
onMounted(() => {
  // 恢复记住的用户名
  const savedUsername = localStorage.getItem('eksjk-remember-username')
  if (savedUsername) {
    loginForm.value.username = savedUsername
    rememberUsername.value = true
  }
})

// ==================== 方法 ====================
async function handleLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await loginApi(loginForm.value)
    loginResponse = res.data

    // 记住用户名
    if (rememberUsername.value) {
      localStorage.setItem('eksjk-remember-username', loginForm.value.username)
    } else {
      localStorage.removeItem('eksjk-remember-username')
    }

    // 存储 Token 和用户信息
    userStore.setToken(loginResponse.token)
    userStore.setUserInfo({
      id: loginResponse.userId,
      username: loginResponse.username,
      realName: loginResponse.realName,
      roleCode: loginResponse.roleCode,
      hospitalId: loginResponse.hospitalId,
      hospitalName: loginResponse.hospitalName,
      passwordNeedChange: loginResponse.passwordNeedChange,
      passwordExpireSoon: loginResponse.passwordExpireSoon
    })

    // 检查密码策略
    if (loginResponse.passwordNeedChange) {
      forceChangePasswordVisible.value = true
      return
    }

    if (loginResponse.passwordExpireSoon) {
      remindChangePasswordVisible.value = true
      return
    }

    // 正常跳转
    navigateAfterLogin()
  } catch (error) {
    // 错误已在 Axios 拦截器中处理
  } finally {
    loading.value = false
  }
}

function navigateAfterLogin() {
  // 重置路由状态，确保动态路由重新添加
  permissionStore.resetRoutes()

  const redirect = route.query.redirect || '/dashboard'
  router.push(redirect)
  ElMessage.success('登录成功')
}

async function handleForceChangePassword() {
  const valid = await changePwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  changePwdLoading.value = true
  try {
    await changePassword({
      oldPassword: loginForm.value.password,
      newPassword: changePwdForm.value.newPassword,
      confirmPassword: changePwdForm.value.confirmPassword
    })
    forceChangePasswordVisible.value = false
    ElMessage.success('密码修改成功，请使用新密码重新登录')
    // 清除 Token，需要重新登录
    userStore.clearUser()
    loginForm.value.password = ''
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    changePwdLoading.value = false
  }
}

function handleRemindNow() {
  remindChangePasswordVisible.value = false
  navigateAfterLogin()
  // 跳转后再导航到个人中心修改密码
  setTimeout(() => {
    router.push('/profile?tab=password')
  }, 500)
}

function handleRemindLater() {
  remindChangePasswordVisible.value = false
  navigateAfterLogin()
}

function handleForgotPassword() {
  ElMessage.info('请联系系统管理员重置密码')
}
</script>

<style scoped>
.login-page {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 左侧品牌区 */
.login-page__brand {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 40px;
}

.login-page__brand-content {
  text-align: center;
  max-width: 480px;
}

.login-page__brand-logo {
  width: 96px;
  height: 96px;
  margin-bottom: 32px;
  filter: brightness(0) invert(1);
}

.login-page__brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 12px;
  letter-spacing: 6px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.login-page__brand-subtitle {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 20px;
  opacity: 1;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.login-page__brand-desc {
  font-size: 16px;
  opacity: 1;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.9);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 右侧表单区 */
.login-page__form-area {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--ek-bg-color);
  padding: 40px;
}

.login-page__form-wrapper {
  width: 100%;
  max-width: 360px;
}

.login-page__form-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--ek-text-primary);
  margin-bottom: 32px;
  text-align: center;
}

.login-page__options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-page__submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-page__brand {
    display: none;
  }
  .login-page__form-area {
    width: 100%;
  }
}
</style>
