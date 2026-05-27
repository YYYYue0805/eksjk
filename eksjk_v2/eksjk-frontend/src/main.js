/**
 * EKSJK V2 前端应用入口文件
 * 
 * <h3>技术栈配置：</h3>
 * <ul>
 *   <li><strong>Vue 3.x</strong> - 渐进式 JavaScript 框架，组合式 API</li>
 *   <li><strong>Vite 8.x</strong> - 现代化构建工具，快速开发体验</li>
 *   <li><strong>Element Plus</strong> - Vue 3 组件库，提供丰富的 UI 组件</li>
 *   <li><strong>Pinia</strong> - Vue 状态管理库，替代 Vuex</li>
 *   <li><strong>Vue Router 4.x</strong> - 官方路由管理器</li>
 *   <li><strong>Axios</strong> - HTTP 客户端，处理 API 请求</li>
 *   <li><strong>ECharts</strong> - 数据可视化图表库</li>
 * </ul>
 * 
 * <h3>主要功能模块：</h3>
 * <ul>
 *   <li>用户认证与权限管理</li>
 *   <li>患者病例管理（矮小症、性早熟、肥胖症等）</li>
 *   <li>随访管理与提醒</li>
 *   <li>学校儿童健康筛查</li>
 *   <li>数据统计与报表</li>
 *   <li>微信小程序集成</li>
 * </ul>
 * 
 * @author eksjk
 * @version 2.0.0
 * @since 2024
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// 全局样式
import 'element-plus/dist/index.css'
import './styles/index.scss'

const app = createApp(App)

// Pinia 状态管理（含持久化插件）
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// Vue Router
app.use(router)

// Element Plus（中文语言包）
app.use(ElementPlus, {
  locale: zhCn,
  size: 'default'
})

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')