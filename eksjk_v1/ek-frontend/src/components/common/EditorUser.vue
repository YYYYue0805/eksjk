<template>
  <el-form ref="editorForm" :model="editorForm" :rules="rules" label-width="80px">
    <el-form-item label="用户名" prop="userName">
      <el-input
        v-model="editorForm.userName"
        style="width: 80%"
        maxlength="64"
        show-word-limit
        placeholder="请输入用户名"
      ></el-input>
    </el-form-item>
    <el-form-item label="原密码" prop="password">
      <el-input
        v-model="editorForm.password"
        style="width: 80%"
        maxlength="32"
        show-word-limit
        show-password
        placeholder="请输入密码"
      ></el-input>
    </el-form-item>
    <el-form-item label="新密码" prop="password">
      <el-input
        v-model="editorForm.newPassword"
        style="width: 80%"
        maxlength="32"
        show-word-limit
        show-password
        placeholder="新密码必须包含数字和字母，至少六位"
      ></el-input>
    </el-form-item>
    <el-form-item label="真实姓名" prop="name">
      <el-input
        v-model="editorForm.name"
        style="width: 80%"
        maxlength="64"
        show-word-limit
        placeholder="请输入姓名"
      ></el-input>
    </el-form-item>
    <el-form-item label="性别" prop="sex">
      <el-select style="width: 80%" v-model="editorForm.sex" placeholder="请选择性别">
        <el-option label="男" value="男"></el-option>
        <el-option label="女" value="女"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input
        v-model="editorForm.email"
        style="width: 80%"
        maxlength="128"
        show-word-limit
        placeholder="请输入邮箱"
      ></el-input>
    </el-form-item>
    <el-form-item label="工作单位" prop="unit">
      <el-select style="width: 80%" v-model="editorForm.unit" @visible-change="unitChange" placeholder="请选择工作单位">
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
        v-model="editorForm.department"
        style="width: 80%"
        maxlength="128"
        show-word-limit
        placeholder="请输入科室"
      ></el-input>
    </el-form-item>
    <el-form-item label="职称" prop="professional">
      <el-select style="width: 80%" v-model="editorForm.professional" placeholder="请选择职称">
        <el-option label="助理医师" value="10040001"></el-option>
        <el-option label="医师" value="10040002"></el-option>
        <el-option label="主治医师" value="10040003"></el-option>
        <el-option label="副主任医师" value="10040004"></el-option>
        <el-option label="主任医师" value="10040005"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="用户等级" prop="level">
      <el-select style="width: 80%" v-model="editorForm.level" placeholder="请选择用户等级">
        <el-option label="普通用户" value="0"></el-option>
        <el-option label="管理员" value="1"></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script>
  import request from "../../script/request";

  export default {
    name: "EditorUser",
    data() {
      let isPass = (rule, password, callback) => {
        if (password) {
          const reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,30}$/
          if (reg.test(password)) {
            callback()
          } else {
            callback(new Error(' '))
          }
        } else {
          callback()
        }
      };
      let isSpace = (rule, userName, callback) => {
        const reg = /^[^ ]+$/
        if (reg.test(userName)) {
          callback()
        } else {
          callback(new Error('用户名不可包含空格符'))
        }
      };
      return {
        unit:[],
        editorForm: {
          queryId: '',
          userName: '',
          password: '',
          newPassword: '',
          name: '',
          sex: '',
          unit: '',
          level: '',
          email: '',
          department: '',
          professional: '',
          msg: '',
          checkPosition: '',
        },
        rules: {
          userName: [{required: true, message: ' ', trigger: 'blur'},
            {validator: isSpace, trigger: 'blur'}],
          password: [{required: false, message: ' ', trigger: 'blur'},
            {validator: isPass, trigger: 'blur'}],
          name: [{required: true, message: ' ', trigger: 'blur'}],
          unit: [{required: true, message: ' ', trigger: 'blur'}],
          level: [{required: true, message: ' ', trigger: 'change'}],
        }
      }
    },
    methods: {
      chenkFrom() {
        this.$refs.editorForm.validate((valid) => {
          if (valid) {
            this.msg = true;
          } else {
            this.msg = false;
          }
        });
        return this.msg;
      },
      getUserInfo(id) {
        this.editorForm.password = '';
        this.editorForm.newPassword = '';
        const queryUId = id;
        this.unitChange()
        request.userInfo({queryUId}, data => {
          this.editorForm.queryId = data.id;
          this.editorForm.name = data.name;
          this.editorForm.userName = data.username;
          this.editorForm.sex = data.sex;
          this.editorForm.email = data.email;
          for(let i=0;i<this.unit.length;i++){
            if(this.unit[i].id == data.unit){
              this.editorForm.unit = this.unit[i].id;
            }
          }
          this.editorForm.department = data.department;
          this.editorForm.professional = data.professional;
          this.editorForm.level = data.level.toString();
          let temp = data.check_position ? JSON.parse(data.check_position) : {};
          this.editorForm.checkPosition = temp['checkPosition'];
        }, error => {
          console.log(error);
        })
      },
      onUpdate() {
        this.$refs.editorForm.validate((valid) => {
          if (valid) {
            request.updateUser(this.editorForm, data => {
              console.log(data)
              this.$message("修改成功")
            }, error => {
              console.log(error)
              this.$message("修改失败")
            })
          } else {
            this.$message("请补全数据！")
          }
        });
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
    },
  }
</script>

<style scoped>

</style>