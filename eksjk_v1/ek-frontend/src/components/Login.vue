<template>
  <div class="login-wrapper">
    <div class="login-title">
      <p>儿科疾病临床数据库</p>
    </div>
    <div class="login-main">
      <div class="login-logo">
        <img src="../assets/img/login_logo.png" alt srcset />
      </div>
      <div class="login-user">
        <div class="user-title">用户登录/USER LOGIN</div>

        <div class="user-error">
          <p v-show="userError">用户名或密码错误!</p>
          <p v-show="needUsername">请输入用户名!</p>
        </div>
        
        <div class="user-name">
          <i class="icon el-icon-user-solid"></i>
          <input type="text" name="账号" placeholder="账号" v-model="username" />
        </div>

        <div class="user-error">
          <p v-show="needPassword">请输入密码!</p>
        </div>
        
        <div class="user-password" @keyup.enter="handleLogin">
          <i class="icon el-icon-lock"></i>
          <input
            type="password"
            name="密码"
            placeholder="密码"
            v-model="password"
          />
        </div>

        <div class="user-login">
          <input type="button" value="登录" @click="handleLogin" />
        </div>
        <div class="remember">
          <div class="user">
            <el-checkbox v-model="remember">记住用户名</el-checkbox>
          </div>
          <div class="password">
            <p @click="forgetPassword">忘记密码?</p>
          </div>
        </div>
      </div>
    </div>
    <div class="login-copyright">
      <div class="footer">
        <span>版权归属：</span>
        <a href="#">浙江求是数理医学研究院</a>
<!--        <span>, All Rights Reserved.</span>-->
      </div>
      <div class="footer">
        <p>平台开发：浙江求是数理医学研究院</p>
      </div>
    </div>
  </div>
</template>

<script>
import request from '../script/request'

export default {
  data() {
    return {
      unit:[],
      tempUnit:'',
      username: '',
      password: '',
      userError: false,
      needUsername: false,
      needPassword: false,
      remember: false,
    }
  },

  methods: {
    unitChange() {
      const queryId = ''
      request.getAllUnit({queryId}, data => {
        data.forEach((item, i) => {
          item.unitName = data[i].unit_name;
          item.id = data[i].id
          this.$set(this.unit, i, item);
        })
      }, error => {
        console.log(error)
      })
    },
    handleLogin() {
      this.userError = false
      this.needUsername = false
      this.needPassword = false
      if (this.username == '') {
        this.needUsername = true
        return
      }

      if (this.password == '') {
        this.needPassword = true
        return
      }

      this.needUsername = false
      this.needPassword = false
      this.userError = false

      request.login(
        this.username,
        this.password, 
      (data) => {
        this.unitChange()
        setTimeout(() => {
          this.loginSuccess(data)
        }, 200);
      }, error => {
          console.log(error)
          this.userError = true
      })
    },
    loginSuccess (data) {
      for (let i = 0; i < this.unit.length; i++) {
        if (this.unit[i].id == data.unit) {
          this.tempUnit = this.unit[i].unitName;
        }
      }
      const user = {
        id: data.id,
        name: data.name,
        username: this.username,
        institution: this.tempUnit,
        level: data.level,
        updateTime: data.date_update,
        timestamp: new Date().getTime(),
        remember: this.remember,
        isSuper: data.is_superuser
      }
      this.$store.commit('login', user)
      this.isCheckCode = ''

      // 有指定路由则跳转，默认跳转主页
      // let routeName = 'home'
      //有指定路由则跳转，默认跳转选择页面
      let routeName = 'diseaseSelect'
      if ('redirect' in this.$route.query) {
        routeName = this.$route.query.redirect
      }
      this.$router.push({ name: routeName })
      
      this.password = ''
    },
    forgetPassword() {
      this.$alert('请联系管理员重置密码', '提示', {
        confirmButtonText: '知道了'
      })
    }
  },
  activated () {
    if (this.$store.state.user) {
      this.remember = this.$store.state.user.remember
      if (this.remember) {
        this.username = this.$store.state.user.username
      } else {
        this.username = ''
      }
    } else {
      this.username = ''
    }

    this.resetValidate()
  }
}
</script>

<style lang="less" scoped>
@import "../assets/less/variable.less";

.login-wrapper {
  position: relative;
  width: 100vw;
  height: 100vh;
  min-height: 650px;
  background-color: @color-nav-bg;
  background-image: url(../assets/img/login_bg.png);
  background-position: right bottom;
  background-repeat: no-repeat;
  background-attachment: scroll;
  background-size: 4660px 462px;
  overflow-y: hidden;
  .login-title {
    p {
      position: fixed;
      left: 50vw;
      top: 72px;
      transform: translateX(-50%);
      font-family: "方正正准黑简体", Arial, sans-serif;
      font-weight: 400;
      font-style: normal;
      font-size: 38px;
      line-height: 38px;
      color: @color-text-ac01;
    }
  }
  .login-main {
    width: 45vw;
    height: 45vh;
    min-width: 800px;
    min-height: 380px;
    margin: 0 auto;
    transform: translateX(-50%);
    transform: translateY(50%);
    display: flex;
    .login-logo {
      width: 50%;
      height: 100%;
      background-color: @color-active-bg;
      display: flex;
      align-items: center;
      justify-content: center;
      img {
        width: 80%;
      }
    }
    .login-user {
      width: 50%;
      height: 100%;
      background-color: #ffffff;
      .user-title,
      .user-name,
      .user-password,
      .user-login,
      .user-error {
        width: 75%;
        height: 50px;
        margin: 0 auto 0;
        position: relative;
        display: flex;
        align-items: center;
        .icon {
          // width: 20px;
          // height: 20px;
          position: absolute;
          padding: 0 10px;
          // top: 6px;
          // left: 5px;
          font-size: 25px;
          color: @color-text-line;
        }
        input {
          width: 100%;
          height: 100% !important;
          padding-left: 40px;
          height: 25px;
          background-color: #f6fafb;
          border-radius: 2px;
          border: solid 1px @color-text-border;
        }
      }
      .verify-image {
        width: 75%;
        margin: 0 auto 0;

        img {
          width: 77px;
          height: 34px;
        }
      }

      .remember {
        width: 75%;
        height: 50px;
        display: flex;
        margin: 0 auto;
        align-items: center;
        justify-content: space-between;
        .user {
          display: flex;
          align-items: center;
        }
        input {
          cursor: pointer;
          margin-right: 2px;
        }
        p {
          cursor: pointer;
        }
      }
      .user-title {
        height: 30px;
        line-height: 30px;
        font-size: @font-normal;
        font-weight: bolder;
        margin-top: 50px;
        margin-bottom: 5px;
      }
      .user-error {
        height: 20px;
        margin-bottom: 5px;
        color: @color-sub01-bg;
      }
      .user-login {
        margin-top: 25px;
        input {
          background-color: @color-active-bg;
          padding: 0;
          color: @color-text-ac01;
          font-size: @font-nav-lv2;
          cursor: pointer;
          border: none;
        }
        input:hover {
          background-color: rgb(106, 183, 231);
        }
      }
    }
  }
  .login-copyright {
    position: absolute;
    left: 50vw;
    bottom: 20px;
    transform: translateX(-50%);
    font-size: 14px;
    color: #cccccc;
    text-align: center;
    line-height: 20px;

    a {
      text-decoration: none;
      color: #ccc;
    }
  }

  p {
    margin: 0;
    padding: 0;
  }
}
</style>
