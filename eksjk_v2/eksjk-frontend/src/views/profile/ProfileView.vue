<template>
  <div class="profile-page">
    <PageHeader title="个人中心" />

    <div class="profile-page__content">
      <!-- 个人信息卡片 -->
      <el-card class="profile-page__card">
        <div class="profile-page__header">
          <el-avatar :size="64" class="profile-page__avatar">{{ userInitial }}</el-avatar>
          <div class="profile-page__info">
            <h3>{{ userStore.realName || userStore.userInfo?.username }}</h3>
            <el-tag size="small" :type="getRoleTagType()">{{ roleName }}</el-tag>
            <span class="text-secondary ml-sm">{{ userStore.userInfo?.hospitalName || '' }}</span>
          </div>
        </div>
      </el-card>

      <!-- 信息编辑表单 -->
      <el-card class="profile-page__form-card">
        <template #header>
          <span class="font-semibold">基本信息</span>
        </template>
        <el-form ref="formRef" :model="formData" label-width="90px">
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input :model-value="userStore.userInfo?.username" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别">
                <el-select v-model="formData.sex" placeholder="请选择" style="width: 100%">
                  <el-option label="男" value="男" />
                  <el-option label="女" value="女" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号">
                <el-input v-model="formData.phone" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="formData.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="角色">
                <el-input :model-value="roleName" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="科室">
                <el-input v-model="formData.department" placeholder="请输入科室" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职称">
                <el-select v-model="formData.professional" placeholder="请选择" clearable style="width: 100%">
                  <el-option label="助理医师" value="10040001" />
                  <el-option label="医师" value="10040002" />
                  <el-option label="主治医师" value="10040003" />
                  <el-option label="副主任医师" value="10040004" />
                  <el-option label="主任医师" value="10040005" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="工号">
                <el-input v-model="formData.jobNumber" placeholder="请输入工号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" :loading="saveLoading" @click="handleSave">保存修改</el-button>
            <el-button @click="handleChangePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="changePwdVisible" title="修改密码" width="420px">
      <el-form ref="changePwdFormRef" :model="changePwdForm" :rules="changePwdRules" label-width="90px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="changePwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="changePwdForm.newPassword" type="password" show-password placeholder="6-30位，需包含数字和字母" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="changePwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="changePwdLoading" @click="submitChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { useUserStore } from '@/stores/user'
import { getProfile, updateProfile } from '@/api/user'
import { changePassword } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const saveLoading = ref(false)

const formData = reactive({
  realName: '', sex: '', phone: '', email: '', department: '', professional: '', jobNumber: ''
})

const userInitial = computed(() => (userStore.realName || '').charAt(0).toUpperCase())
const roleName = computed(() => {
  const map = { super_admin: '超级管理员', hospital_admin: '医院管理员', doctor: '普通医生', parent: '家长' }
  return map[userStore.role] || '未知'
})

function getRoleTagType() {
  const map = { super_admin: 'danger', hospital_admin: 'warning', doctor: '' }
  return map[userStore.role] || 'info'
}

// 修改密码
const changePwdVisible = ref(false)
const changePwdLoading = ref(false)
const changePwdFormRef = ref(null)
const changePwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const changePwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度为 6-30 位', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码需包含数字和字母', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (r, v, cb) => v !== changePwdForm.newPassword ? cb(new Error('两次密码不一致')) : cb(), trigger: 'blur' }
  ]
}

onMounted(async () => {
  try {
    const res = await getProfile()
    const data = res.data
    Object.assign(formData, {
      realName: data.realName || '', sex: data.sex || '', phone: data.phone || '',
      email: data.email || '', department: data.department || '',
      professional: data.professional || '', jobNumber: data.jobNumber || ''
    })
  } catch (e) {}
})

async function handleSave() {
  saveLoading.value = true
  try {
    await updateProfile(formData)
    ElMessage.success('保存成功')
    // 更新 store 中的用户名
    userStore.setUserInfo({ ...userStore.userInfo, realName: formData.realName })
  } catch (e) {} finally { saveLoading.value = false }
}

function handleChangePassword() {
  Object.assign(changePwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  changePwdVisible.value = true
}

async function submitChangePassword() {
  const valid = await changePwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  changePwdLoading.value = true
  try {
    await changePassword(changePwdForm)
    changePwdVisible.value = false
    ElMessage.success('密码修改成功，请重新登录')
    userStore.clearUser()
    router.push('/login')
  } catch (e) {} finally { changePwdLoading.value = false }
}
</script>

<style scoped>
.profile-page__content {
  max-width: 900px;
}

.profile-page__card {
  margin-bottom: var(--ek-spacing-base);
}

.profile-page__header {
  display: flex;
  align-items: center;
  gap: var(--ek-spacing-base);
}

.profile-page__avatar {
  background-color: var(--ek-color-primary);
  color: #fff;
  font-size: 24px;
}

.profile-page__info h3 {
  font-size: 18px;
  margin-bottom: 4px;
}

.profile-page__form-card {
  margin-bottom: var(--ek-spacing-base);
}
</style>