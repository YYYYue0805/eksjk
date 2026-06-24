<template>
  <div class="sidebar" :class="{ 'sidebar--collapsed': collapsed }">
    <!-- Logo 区域 -->
    <div class="sidebar__logo" @click="$router.push('/dashboard')">
      <img src="@/assets/vue.svg" alt="Logo" class="sidebar__logo-img" />
      <span v-show="!collapsed" class="sidebar__logo-text">EKSJK</span>
    </div>

    <!-- 菜单 -->
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :unique-opened="true"
        :collapse-transition="false"
        class="sidebar__menu"
        @select="handleMenuSelect"
      >
        <template v-for="menu in menus" :key="menu.path">
          <!-- 有子菜单 -->
          <el-sub-menu v-if="menu.children && menu.children.length" :index="menu.path">
            <template #title>
              <el-icon><component :is="menu.icon" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.path"
              :index="child.path"
            >
              <template #title>{{ child.title }}</template>
            </el-menu-item>
          </el-sub-menu>

          <!-- 无子菜单 -->
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="menu.icon" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
/**
 * 侧边栏组件
 */
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  /** 是否折叠 */
  collapsed: {
    type: Boolean,
    default: false
  },
  /** 菜单列表 */
  menus: {
    type: Array,
    default: () => []
  }
})

const route = useRoute()
const router = useRouter()

/** 当前激活的菜单 */
const activeMenu = computed(() => {
  return route.meta?.activeMenu || route.path
})

/** 菜单选择事件：直接使用 router.push 替代 el-menu 的 router 属性 */
function handleMenuSelect(index) {
  if (index && index !== route.path) {
    router.push(index)
  }
}
</script>

<style scoped>
.sidebar {
  width: var(--ek-sidebar-width);
  height: 100vh;
  background-color: #001529;
  transition: width var(--ek-transition-duration) var(--ek-transition-timing);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar--collapsed {
  width: var(--ek-sidebar-collapsed-width);
}

.sidebar__logo {
  height: var(--ek-topbar-height);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar__logo-img {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.sidebar__logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-left: 10px;
  white-space: nowrap;
}

.sidebar__menu {
  border-right: none;
}

.sidebar :deep(.el-menu) {
  background-color: #001529;
  border-right: none;
}

.sidebar :deep(.el-menu-item),
.sidebar :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.65);
}

.sidebar :deep(.el-menu-item:hover),
.sidebar :deep(.el-sub-menu__title:hover) {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.08);
}

.sidebar :deep(.el-menu-item.is-active) {
  color: #fff;
  background-color: var(--ek-color-primary);
}

.sidebar :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #fff;
}
</style>
