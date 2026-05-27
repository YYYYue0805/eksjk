<template>
  <div class="topbar">
    <!-- 左侧：折叠按钮 + 面包屑 -->
    <div class="topbar__left">
      <el-icon class="topbar__collapse-btn" @click="$emit('toggle-collapse')">
        <Fold v-if="!collapsed" />
        <Expand v-else />
      </el-icon>

      <el-breadcrumb separator="/" class="topbar__breadcrumb">
        <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
          <span v-if="item.path">{{ item.title }}</span>
          <span v-else class="topbar__breadcrumb-current">{{ item.title }}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧：用户信息 -->
    <div class="topbar__right">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="topbar__user">
          <el-avatar :size="32" class="topbar__avatar">
            {{ userInitial }}
          </el-avatar>
          <span class="topbar__username">{{ realName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人中心
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
/**
 * 顶部栏组件
 */
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { logout as logoutApi } from '@/api/auth'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle-collapse'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const realName = computed(() => userStore.realName || userStore.userInfo?.username || '用户')
const userInitial = computed(() => (realName.value || '').charAt(0).toUpperCase())

/** 面包屑导航 */
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  return matched.map(item => ({
    title: item.meta.title,
    path: item.path
  }))
})

/** 下拉菜单命令处理 */
async function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/profile?tab=password')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        try {
          await logoutApi()
        } catch (e) {
          // 忽略登出接口错误
        }
        userStore.clearUser()
        router.push('/login')
      } catch {
        // 取消操作
      }
      break
  }
}
</script>

<style scoped>
.topbar {
  height: var(--ek-topbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--ek-spacing-lg);
  background-color: var(--ek-bg-color);
  border-bottom: 1px solid var(--ek-border-color-light);
  box-shadow: var(--ek-shadow-sm);
}

.topbar__left {
  display: flex;
  align-items: center;
  gap: var(--ek-spacing-base);
}

.topbar__collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--ek-text-regular);
  transition: color 0.2s;
}

.topbar__collapse-btn:hover {
  color: var(--ek-color-primary);
}

.topbar__breadcrumb-current {
  color: var(--ek-text-primary);
  font-weight: 500;
}

.topbar__right {
  display: flex;
  align-items: center;
}

.topbar__user {
  display: flex;
  align-items: center;
  gap: var(--ek-spacing-sm);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--ek-radius-base);
  transition: background-color 0.2s;
}

.topbar__user:hover {
  background-color: var(--ek-bg-color-page);
}

.topbar__avatar {
  background-color: var(--ek-color-primary);
  color: #fff;
  font-size: 14px;
}

.topbar__username {
  font-size: 14px;
  color: var(--ek-text-primary);
}
</style>
