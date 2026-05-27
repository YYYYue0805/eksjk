<template>
  <div id="app">
    <div class="header" v-if="!login">
      <div class="header-title">儿科疾病临床数据库</div>
      
      <div>
        <a class="user-info" @click="onUserInfo" style="cursor:pointer">
          <span>{{userName}}</span>
        </a>
        <a href="#" class="el-icon-switch-button"
           style="padding-left: 1vw;padding-right: 1vw;font-size: 1.2vw;cursor: pointer;color: white;text-decoration: none" @click="onLogout">
        </a>
<!--        <el-button type="text" style="color: white;" @click="onLogout">退出</el-button>-->
      </div>
    </div>

    <main class="content">
      <keep-alive>
        <router-view></router-view>
      </keep-alive>
    </main>
  </div>
</template>

<script>
import request from './script/request'

export default {
  name: 'App',
  provide () {    //父组件中通过provide来提供变量，在子组件中通过inject来注入变量。
    return {
      reload: this.reload
    }
  },
  data() {
    return{
      isRouterAlive: true                    //控制视图是否显示的变量
    }
  },
  computed: {
    login () {
      return this.$route.name == 'login'
    },
    userInstitution () {
      return this.$store.state.user && this.$store.state.user.institution || ''
    },
    userName () {
      return this.$store.state.user && this.$store.state.user.name || ''
    }
  },
  methods: {
    onUserInfo () {
      this.$router.push({ name: 'user-profile' })
    },
    onLogout () {
      request.logout(() => {
        this.logout()
      }, () => {
        this.logout()
      })
    },
    logout () {
      this.$store.commit('logout')
      this.$router.push({ name: 'login' })
      //删除存储的疾病类型
      sessionStorage.removeItem('OrganSelect')
    },
    reload () {
      this.isRouterAlive = false
      this.$nextTick(function () {
        this.isRouterAlive = true
      })
    }
  },
  mounted () {
    request.setUnauthorizedCallback(this.logout)
  }
}
</script>

<style lang="less">
@import './assets/less/variable.less';

html, body, #app {
  margin: 0;
  padding: 0;
  font-size: 14px;
  color: #666;
  height: auto;
  /*overflow: hidden;*/
}

#app {
  display: flex;
  flex-direction: column;

  .content {
    position: relative;
    flex: 1;
  }
}

.header {
  min-height: 60px;
  max-height: 60px;
  width: auto;
  font-size: @font-header;
  color: #333333;
  background-color: #1daaf1;
  color: white;
  padding: 0 5px;

  display: flex;
  align-items: center;
  justify-content: space-between;

  .header-title {
    float: left;
    font-size: 18px;
  }

  .user-info {
    margin-right: 8px;
  }

  .user-info:hover {
    text-decoration: underline;
  }
}
</style>
