import Vue from 'vue'
import Router from 'vue-router'
import store from './store'
import StatisticPosi from "./components/StatisticsPosi"
import Login from './components/Login.vue'
import Home from './components/Home.vue'
import TianYuan from './components/TianYuan.vue'
import EditLog from './components/editLog.vue'
import Editor from './components/Editor.vue'
import StudentEditor from './components/StudentEditor.vue'
import Detail from './components/Detail.vue'
import UserProfile from './components/UserProfile.vue'
import User from "./components/User"
import diseaseSelect from "./components/diseaseSelect"
import Unit from "./components/Unit"

Vue.use(Router)

let router = new Router({
  routes: [
    { path: '/', name: '', redirect: '/home' },
    { path: '/login', name: 'login', component: Login },
    { path: '/home', name: 'home', component: Home, meta: { loginRequired: true } },
    { path: '/tianYuan', name: 'tianYuan', component: TianYuan,meta: { loginRequired: true }},
    { path: '/editLog', name: 'editLog', component: EditLog, meta: { loginRequired: true } },
    { path: '/editor', name: 'editor', component: Editor, meta: { loginRequired: true } },
    { path: '/studentEditor', name: 'studentEditor', component: StudentEditor, meta: { loginRequired: true } },
    { path: '/detail', name: 'detail', component: Detail, meta: { loginRequired: true }  },
    { path: '/userProfile', name: 'user-profile', component: UserProfile, meta: { loginRequired: true } },
    { path: '/user', name: 'user', component: User, meta: { loginRequired: true } },
    { path: '/diseaseSelect', name: 'diseaseSelect', component: diseaseSelect, meta: { loginRequired: true } },
    { path: '/statisticPosi', name: 'statisticPosi', component: StatisticPosi, meta: { loginRequired: true } },
    { path: '/unit', name: 'unit', component: Unit, meta: { loginRequired: true } },
  ]
})

// 注册全局钩子，判断是否需要跳转到登录页面
router.beforeEach((to, from, next) => {
  to.matched.some((route) => {
    if (route.meta.loginRequired) {
      if (store.state.user && store.state.user.id) {
        next()
      } else {
        next(`/login?redirect=${to.name}`)
      }
    } else {
      next()
    }
  })
})

export default router
