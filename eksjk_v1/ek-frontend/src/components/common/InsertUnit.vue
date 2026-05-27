<template>
  <el-form ref="unitForm" :model="unitForm" :rules="rules" label-width="80px">
    <el-form-item label="单位名称" prop="unitName">
      <el-input
        v-model="unitForm.unitName"
        style="width: 80%"
        maxlength="128"
        show-word-limit
        placeholder="请输入单位名称"
      ></el-input>
    </el-form-item>
  </el-form>
</template>

<script>
  export default {
    name: "insertUnit",
    data() {
      return {
        rules: {
          unitName: [{required: true, message: ' ', trigger: 'blur'}],
        },
        unitForm: {
          unitName: '',
        },
        msg: '',
      }
    },
    methods: {
      chenkFrom() {
        this.$refs.unitForm.validate((valid) => {
          if (valid) {
            this.msg = true;
          } else {
            this.msg = false;
          }
        });
        return this.msg;
      },
      clear() {
        this.unitForm.unitName = ''
      }
    },
  }
</script>

<style scoped>

</style>