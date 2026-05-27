/**
 * Vue Router 路由配置
 * 包含基础路由表和路由守卫
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'

/**
 * 基础路由表（静态路由）
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { title: '页面不存在', requiresAuth: false }
  },
  {
    path: '/',
    name: 'MainLayout',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: []
  }
  // 注意：catch-all 路由不在此处定义，而是在动态路由添加完成后再添加
  // 避免刷新页面时，动态路由尚未注册就被 catch-all 匹配到 /404
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由前置守卫
 */
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  const title = to.meta?.title
  if (title) {
    document.title = `${title} - EKSJK 儿科数据管理系统`
  }

  const userStore = useUserStore()
  const permissionStore = usePermissionStore()
  const isLoggedIn = userStore.isLoggedIn

  // 目标路由不需要认证，直接放行
  if (to.meta?.requiresAuth === false) {
    if (to.path === '/login' && isLoggedIn) {
      next({ path: '/dashboard' })
      return
    }
    next()
    return
  }

  // 未登录，跳转登录页
  if (!isLoggedIn) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 已登录，动态添加路由
  if (!permissionStore.routesAdded) {
    permissionStore.addDynamicRoutes(router)
    // 动态路由添加完成后，再添加 catch-all 路由（确保不会提前拦截动态路由）
    if (!router.hasRoute('CatchAll')) {
      router.addRoute({
        path: '/:pathMatch(.*)*',
        name: 'CatchAll',
        redirect: '/404'
      })
    }
    // 重新导航到目标路由（因为路由刚添加）
    // 如果目标是 / 根路径，重定向到 /dashboard
    const redirectTo = to.path === '/' ? { path: '/dashboard', replace: true } : { ...to, replace: true }
    next(redirectTo)
    return
  }

  // 已登录且路由已添加，如果访问根路径则重定向到 /dashboard
  if (to.path === '/') {
    next({ path: '/dashboard', replace: true })
    return
  }

  next()
})

export default router
