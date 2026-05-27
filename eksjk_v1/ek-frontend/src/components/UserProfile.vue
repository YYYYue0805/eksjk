<template>
  <div class="user-profile" align="center">
    <el-row class="user-head">
      <el-col :span="20" align="left"><span class="user-title">个人信息</span></el-col>
      <el-col :span="4" align="right">
        <el-button size='mini' style="margin-top: 1vh;margin-right: 1vw" type="primary" @click='backHistory'>返回
        </el-button>
      </el-col>
    </el-row>
    <div class="content">
      <el-form label-width="80px" ref="userProfile" :model="userProfile" :rules="rules">
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="userProfile.userName"
            placeholder="请输入用户名"
            maxlength="64"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="userProfile.name"
            placeholder="请输入姓名"
            maxlength="64"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select
            v-model="userProfile.sex"
            label-position="right"
            placeholder="请选择性别"
            style="width: 100%"
          >
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="个人邮箱" prop="email">
          <el-input
            v-model="userProfile.email"
            placeholder="请输入个人邮箱"
            maxlength="64"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="工作单位" prop="unit">
          <el-select style="width: 100%" v-model="userProfile.unit" @visible-change="unitChange" placeholder="请选择工作单位">
            <el-option
              v-for="(item, index) in this.unit"
              :key="index"
              :value="item.id"
              :label="item.unitName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="professional">
          <el-select style="width: 100%" v-model="userProfile.professional" placeholder="请选择职称">
            <el-option label="助理医师" value="10040001"></el-option>
            <el-option label="医师" value="10040002"></el-option>
            <el-option label="主治医师" value="10040003"></el-option>
            <el-option label="副主任医师" value="10040004"></el-option>
            <el-option label="主任医师" value="10040005"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="原密码" v-if="this.isUpdate" prop="password">
          <el-input
            v-model="userProfile.password"
            show-password
            placeholder="请输入原密码"
            maxlength="32"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" v-if="this.isUpdate" prop="newPassword">
          <el-input
            v-model="userProfile.newPassword"
            show-password
            maxlength="32"
            placeholder="请输入新密码"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" v-if="!this.isUpdate" @click="isPass">修改密码</el-button>
          <el-button type="primary" v-if="this.isUpdate" @click="isPass">返回</el-button>
          <el-button type="primary" @click="onUpdate">确认修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
  import request from "../script/request"

  export default {
    name: 'UserProfile',
    data() {
      let isPass = (rule, password, callback) => {
        const reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,30}$/
        if (reg.test(password)) {
          callback()
        } else {
          callback(new Error(' '))
        }
      };
      return {
        unit:[],
        isUpdate: false,
        userProfile: {
          queryId: '',
          userName: '',
          name: '',
          sex: '',
          email: '',
          password: '',
          newPassword: '',
          unit: '',
          professional: '',
          level: '',
        },
        rules: {
          userName: [{required: true, message: ' ', trigger: 'blur'}],
          name: [{required: true, message: ' ', trigger: 'blur'}],
          password: [{required: true, message: ' ', trigger: 'blur'}],
          newPassword: [{required: true, message: ' ', trigger: 'blur'},
          {validator: isPass, trigger: 'blur'}],
          unit: [{required: true, message: ' ', trigger: 'blur'}],
        }
      }
    },
    activated() {
      this.unitChange()
      this.getUserInfo();
    },
    methods: {
      backHistory() {
        this.$router.push({name: 'home'})
      },
      getUserInfo() {
        this.userProfile.password = '';
        this.userProfile.newPassword= '';
        const queryUId = this.$store.state.user.id
        request.userInfo({queryUId}, data => {
          this.userProfile.queryId = data.id;
          this.userProfile.name = data.name
          this.userProfile.userName = data.username
          this.userProfile.sex = data.sex
          this.userProfile.email = data.email
          for(let i=0;i<this.unit.length;i++){
            if(this.unit[i].id == data.unit){
              this.userProfile.unit = this.unit[i].id;
            }
          }
          this.userProfile.professional = data.professional
          this.userProfile.level = data.level.toString()
        }, error => {
          console.log(error);
        })
      },
      onUpdate() {
        this.$refs.userProfile.validate((valid) => {
          if (valid) {
            request.updateUser(this.userProfile, data => {
              console.log(data)
              this.$message("修改成功")
              this.getUserInfo();
            }, error => {
              console.log(error)
              this.$message("修改失败")
            })
          } else {
            this.$message("请补全数据！")
          }
        });
      },
      isPass() {
        if (this.isUpdate) {
          this.isUpdate = false;
        } else {
          this.isUpdate = true;
        }
      },
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
    }
  }
</script>

<style scoped>
  .user-head {
    height: 5vh;
    background: #ffffff;
  }

  .user-title {
    margin-left: 2vw;
    font-size: 1.5vw;
    color: rgb(64, 158, 255);
  }

  .content {
    width: 25vw;
    padding-top: 10vh;
  }
</style>