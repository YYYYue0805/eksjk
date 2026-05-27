<template>
    <el-form ref="editorForm" :model="editorForm" :rules="rules" label-width="80px">
    <el-form-item label="单位名称" prop="unitName">
      <el-input
        v-model="editorForm.unitName"
        style="width: 80%"
        maxlength="128"
        show-word-limit
        placeholder="请输入单位名称"
      ></el-input>
    </el-form-item>
      <el-input v-model="editorForm.queryId" style="display: none"></el-input>
  </el-form>
</template>

<script>
  import request from "../../script/request";
  export default {
    name: "EditorUnit",
    data(){
      return{
        rules:{
          unitName: [{required: true, message: ' ', trigger: 'blur'}],
        },
        editorForm:{
          unitName: '',
          queryId: '',
        },
        msg:'',
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
      getUnitInfo(id) {
        const queryId = id;
        request.getUnitInfo({queryId}, data => {
          this.editorForm.unitName = data.unit_name;
          this.editorForm.queryId = data.id;
        }, error => {
          console.log(error);
        })
      },
      onUpdate() {
        this.$refs.editorForm.validate((valid) => {
          if (valid) {
            request.insertUnit(this.editorForm, data => {
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
    },
  }
</script>

<style scoped>

</style>