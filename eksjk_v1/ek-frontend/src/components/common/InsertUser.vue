<template>
  <el-form ref="userForm" :model="userForm" :rules="rules" label-width="80px">
    <el-form-item label="用户名" prop="userName">
      <el-input
        v-model="userForm.userName"
        style="width: 80%"
        maxlength="64"
        show-word-limit
        placeholder="请输入用户名"
      ></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input
        v-model="userForm.password"
        style="width: 80%"
        maxlength="32"
        show-word-limit
        show-password
        placeholder="请输入密码"
      ></el-input>
    </el-form-item>
    <el-form-item label="真实姓名" prop="name">
      <el-input
        v-model="userForm.name"
        style="width: 80%"
        maxlength="64"
        show-word-limit
        placeholder="请输入姓名"
      ></el-input>
    </el-form-item>
    <el-form-item label="性别" prop="sex">
      <el-select style="width: 80%" v-model="userForm.sex" placeholder="请选择性别">
        <el-option label="男" value="男"></el-option>
        <el-option label="女" value="女"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input
        v-model="userForm.email"
        style="width: 80%"
        maxlength="128"
        show-word-limit
        placeholder="请输入邮箱"
      ></el-input>
    </el-form-item>
    <el-form-item label="工作单位" prop="unit">
      <el-select style="width: 80%" v-model="userForm.unit" @visible-change="unitChange" placeholder="请选择工作单位">
        <el-option
          v-for="(item, index) in this.unit"
          :key="index"
          :value="item.id"
          :label="item.unitName"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="科室" prop="department">
      <el-input
        v-model="userForm.department"
        style="width: 80%"
        maxlength="128"
        show-word-limit
        placeholder="请输入科室"
      ></el-input>
    </el-form-item>
    <el-form-item label="职称" prop="professional">
      <el-select style="width: 80%" v-model="userForm.professional" placeholder="请选择职称">
        <el-option label="助理医师" value="10040001"></el-option>
        <el-option label="医师" value="10040002"></el-option>
        <el-option label="主治医师" value="10040003"></el-option>
        <el-option label="副主任医师" value="10040004"></el-option>
        <el-option label="主任医师" value="10040005"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="用户等级" prop="level">
      <el-select style="width: 80%" v-model="userForm.level" placeholder="请选择用户等级">
        <el-option label="普通用户" value="0"></el-option>
        <el-option label="管理员" value="1"></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script>
  import request from "../../script/request";
  export default {
    name: "InsertUser",
    data() {
      let isPass = (rule, password, callback) => {
        const reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,30}$/
        if (reg.test(password)) {
          callback()
        } else {
          callback(new Error('密码必须包含数字和字母，至少六位'))
        }
      };
      let isSpace = (rule, userName, callback) =>{
        const reg = /^[^ ]+$/
        if (reg.test(userName)) {
          callback()
        } else {
          callback(new Error('用户名不可包含空格符'))
        }
      };
      return {
        unit: [],
        userForm: {
          userName: '',
          password: '',
          name: '',
          sex: '',
          unit: '',
          department: '',
          level: '',
          email: '',
          professional: '',
          msg:'',
          checkPosition: '',
        },
        rules:{
          userName: [{required: true, message: ' ', trigger: 'blur'},
            {validator: isSpace,trigger: 'blur'}],
          password: [{required: true, message: ' ', trigger: 'blur'},
            {validator: isPass, trigger: 'blur'}],
          name: [{required: true, message: ' ', trigger: 'blur'}],
          unit: [{required: true, message: ' ', trigger: 'blur'}],
          department: [{required: true, message: ' ', trigger: 'blur'}],
          level: [{required: true, message: ' ', trigger: 'change'}],
          checkPosition: [{required: true, message: ' ', trigger: 'change'}],
        }
      }
    },
    methods: {
      chenkFrom(){
        this.$refs.userForm.validate((valid) => {
          if (valid) {
            this.msg = true;
          } else {
            this.msg = false;
          }
        });
        return this.msg;
      },
      clear() {
        this.userForm.userName = ''
        this.userForm.password = ''
        this.userForm.name = ''
        this.userForm.sex = ''
        this.userForm.unit = ''
        this.userForm.level = ''
        this.userForm.email = ''
        this.userForm.professional = ''
        this.userForm.department = ''
        this.userForm.checkPosition = ''
      },
      unitChange(){
        const queryId = ''
        request.getAllUnit({queryId},data =>{
          data.forEach((item,i) =>{
            item.unitName = data[i].unit_name;
            item.id = data[i].id
            this.$set(this.unit, i, item);
          })
        },error =>{
          console.log(error)
        })
      },
    },
  }
</script>

<style scoped>

</style>