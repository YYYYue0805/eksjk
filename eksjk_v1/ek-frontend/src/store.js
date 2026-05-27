import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)

// 按照是否记住用户名设置信息
function setUser(user) {
  let _user = null
  if (user && user.remember && user.username) {
    _user = {
      username: user.username,
      remember: true
    }
    localStorage.setItem('user', JSON.stringify(_user))
  } else {
    localStorage.removeItem('user')
  }

  return _user
}

// 从本地存储中加载用户信息
function getUser() {
  let user = localStorage.getItem('user')
  try {
    user = JSON.parse(user)
  } catch {
    user = null
  }

  return user
}

export default new Vuex.Store({
  state: {
    user: getUser()
  },
  mutations: {
    login (state, user) {
      state.user = user
      localStorage.setItem('user', JSON.stringify(user))
    },
    logout (state) {
      state.user = setUser(state.user)
    }
  }
})
