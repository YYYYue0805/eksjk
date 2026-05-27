import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Router from 'vue-router'
import ElementUI from 'element-ui'
import echarts from 'echarts'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/font/iconfont.css'
import { pcaa } from 'area-data-vue';
import "./assets/style/rest.css";
import 'area-linkage-vue/dist/index.css';
import AreaLinkageVue from 'area-linkage-vue';
import uploader from 'vue-simple-uploader'
Vue.use(ElementUI)
Vue.use(AreaLinkageVue)
Vue.use(uploader)

import initCornerstoneFamily from './script/cornerstone'
initCornerstoneFamily()

Vue.config.productionTip = false
Vue.prototype.$echarts = echarts
Vue.prototype.$pcaa = pcaa;
new Vue({
  render: h => h(App),
  router,
  store
}).$mount('#app')
const routerPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(error=> error)
}