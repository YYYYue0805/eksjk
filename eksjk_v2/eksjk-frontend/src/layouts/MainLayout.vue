<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <SideBar :collapsed="sidebarCollapsed" :menus="menus" />

    <!-- 右侧区域 -->
    <div class="main-layout__right">
      <!-- 顶部栏 -->
      <TopBar :collapsed="sidebarCollapsed" @toggle-collapse="toggleSidebar" />

      <!-- 主内容区 -->
      <div class="main-layout__content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 主布局组件
 * 经典三栏布局：左侧边栏 + 顶部栏 + 主内容区
 */
import { ref } from 'vue'
import SideBar from './SideBar.vue'
import TopBar from './TopBar.vue'
import { usePermissionStore } from '@/stores/permission'

const permissionStore = usePermissionStore()
const menus = permissionStore.menus

const sidebarCollapsed = ref(false)

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.main-layout__right {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.main-layout__content {
  flex: 1;
  overflow: auto;
  padding: var(--ek-content-padding);
  background-color: var(--ek-bg-color-page);
}
</style>
