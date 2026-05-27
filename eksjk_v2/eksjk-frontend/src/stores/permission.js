/**
 * 权限状态管理 Store
 * 根据用户角色动态生成菜单和路由
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useUserStore } from './user'

/**
 * 完整菜单结构定义
 */
const allMenus = [
  {
    path: '/dashboard',
    title: '工作台',
    icon: 'DataBoard',
    roles: ['super_admin', 'hospital_admin', 'doctor']
  },
  {
    path: '/case',
    title: '病例管理',
    icon: 'Document',
    roles: ['super_admin', 'hospital_admin', 'doctor'],
    children: [
      { path: '/case/dsd', title: '性发育异常 (DSD)', roles: ['super_admin', 'hospital_admin', 'doctor'] },
      { path: '/case/fss', title: '遗传性骨病 (FSS)', roles: ['super_admin', 'hospital_admin', 'doctor'] },
      { path: '/case/cpp', title: '中枢性性早熟 (CPP)', roles: ['super_admin', 'hospital_admin', 'doctor'] },
      { path: '/case/mas', title: 'McCune-Albright (MAS)', roles: ['super_admin', 'hospital_admin', 'doctor'] },
      { path: '/case/sga', title: '小于胎龄儿 (SGA)', roles: ['super_admin', 'hospital_admin', 'doctor'] },
      { path: '/case/sss', title: '家族性矮小 (SSS)', roles: ['super_admin', 'hospital_admin', 'doctor'] },
      { path: '/case/eltm', title: 'E路童萌 (ELTM)', roles: ['super_admin', 'hospital_admin', 'doctor'] }
    ]
  },
  {
    path: '/school',
    title: '健康筛查',
    icon: 'School',
    roles: ['super_admin', 'hospital_admin', 'doctor']
  },
  {
    path: '/statistics',
    title: '统计分析',
    icon: 'TrendCharts',
    roles: ['super_admin', 'hospital_admin', 'doctor']
  },
  {
    path: '/notice',
    title: '通知公告',
    icon: 'Bell',
    roles: ['super_admin', 'hospital_admin', 'doctor']
  },
  {
    path: '/user',
    title: '用户管理',
    icon: 'UserFilled',
    roles: ['super_admin', 'hospital_admin']
  },
  {
    path: '/system',
    title: '系统管理',
    icon: 'Setting',
    roles: ['super_admin'],
    children: [
      { path: '/system/unit', title: '单位管理', roles: ['super_admin'] },
      { path: '/system/log', title: '操作日志', roles: ['super_admin'] }
    ]
  }
]

/**
 * 动态路由配置（懒加载）
 */
const asyncRoutes = [
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/DashboardView.vue'),
    meta: { title: '工作台', requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserList',
    component: () => import('@/views/user/UserList.vue'),
    meta: { title: '用户管理', requiresAuth: true, roles: ['super_admin', 'hospital_admin'] }
  },
  {
    path: '/system/unit',
    name: 'UnitList',
    component: () => import('@/views/unit/UnitList.vue'),
    meta: { title: '单位管理', requiresAuth: true, roles: ['super_admin'] }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/ProfileView.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  // 病例管理 - 新建（必须在 :id 路由之前，避免 create 被当作 :id 匹配）
  {
    path: '/case/:type/create',
    name: 'CaseCreate',
    component: () => import('@/views/case/CaseDetail.vue'),
    meta: { title: '新建病例', requiresAuth: true }
  },
  // 病例管理 - 编辑（必须在 :id 路由之前）
  {
    path: '/case/:type/:id/edit',
    name: 'CaseEdit',
    component: () => import('@/views/case/CaseDetail.vue'),
    meta: { title: '编辑病例', requiresAuth: true }
  },
  // 病例管理 - 详情（查看）
  {
    path: '/case/:type/:id',
    name: 'CaseView',
    component: () => import('@/views/case/CaseDetail.vue'),
    meta: { title: '病例详情', requiresAuth: true }
  },
  // 病例管理 - 列表（放在最后，作为兜底）
  {
    path: '/case/:type',
    name: 'CaseList',
    component: () => import('@/views/case/CaseList.vue'),
    meta: { title: '病例管理', requiresAuth: true }
  },
  // 健康筛查 - 新建
  {
    path: '/school/create',
    name: 'SchoolCreate',
    component: () => import('@/views/school/SchoolDetail.vue'),
    meta: { title: '新增学生', requiresAuth: true }
  },
  // 健康筛查 - 编辑
  {
    path: '/school/:id/edit',
    name: 'SchoolEdit',
    component: () => import('@/views/school/SchoolDetail.vue'),
    meta: { title: '编辑学生', requiresAuth: true }
  },
  // 健康筛查 - 详情
  {
    path: '/school/:id',
    name: 'SchoolView',
    component: () => import('@/views/school/SchoolDetail.vue'),
    meta: { title: '学生详情', requiresAuth: true }
  },
  // 健康筛查 - 列表
  {
    path: '/school',
    name: 'SchoolList',
    component: () => import('@/views/school/SchoolList.vue'),
    meta: { title: '健康筛查', requiresAuth: true }
  },
  // 统计分析
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/statistics/StatisticsView.vue'),
    meta: { title: '统计分析', requiresAuth: true }
  },
  // 通知公告
  {
    path: '/notice',
    name: 'Notice',
    component: () => import('@/views/notice/NoticeView.vue'),
    meta: { title: '通知公告', requiresAuth: true }
  },
  // 操作日志
  {
    path: '/system/log',
    name: 'SystemLog',
    component: () => import('@/views/system/SystemLog.vue'),
    meta: { title: '操作日志', requiresAuth: true, roles: ['super_admin'] }
  }
]

export const usePermissionStore = defineStore('permission', () => {
  const routesAdded = ref(false)

  /** 根据角色过滤菜单 */
  const menus = computed(() => {
    const userStore = useUserStore()
    const role = userStore.role
    if (!role) return []
    return filterMenusByRole(allMenus, role)
  })

  /**
   * 递归过滤菜单
   */
  function filterMenusByRole(menuList, role) {
    return menuList
      .filter(menu => menu.roles.includes(role))
      .map(menu => {
        const filtered = { ...menu }
        if (menu.children) {
          filtered.children = filterMenusByRole(menu.children, role)
        }
        return filtered
      })
  }

  /**
   * 动态添加路由
   */
  function addDynamicRoutes(router) {
    if (routesAdded.value) return

    const userStore = useUserStore()
    const role = userStore.role

    asyncRoutes.forEach(route => {
      // 检查路由角色权限
      if (route.meta?.roles && !route.meta.roles.includes(role)) {
        return
      }
      router.addRoute('MainLayout', route)
    })

    routesAdded.value = true
  }

  /**
   * 重置路由状态（登出时调用）
   */
  function resetRoutes() {
    routesAdded.value = false
  }

  return {
    menus,
    routesAdded,
    addDynamicRoutes,
    resetRoutes
  }
})
