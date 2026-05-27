<template>
  <div style="display: flex;margin-top: 2vh;margin-bottom: 2vh">
    <input type="file" size="small" ref="clearFile" @change="getFile($event)" multiple>
    <el-button type="primary" @click="submitAddFile" size="small">开始上传</el-button>
  </div>
</template>

<script>
  import request from '../../script/request'

  export default {
    name: "fileUpload",
    props: {
      caseId: {
        type: String,
        required: true
      },
      organ: {
        type: String,
        required: true
      },
      category: {
        type: String,
        required: true
      },
    },
    data() {
      return {
        addFileName: "",
        addArr:[],
      }
    },
    methods: {
      getFile(event) {
        let file = event.target.files[0]
        this.addFileName = file.name;
        this.addArr.push(file);
      },

      submitAddFile() {
        if (0 == this.addArr.length) {
          this.$message({
            type: 'info',
            message: '请选择要上传的文件'
          });
          return;
        }

        let formData = new FormData();
        formData.append('queryId', this.caseId);
        formData.append('organ', this.organ);
        formData.append('path', this.category+"-"+this.addFileName);
        formData.append('package', this.addArr[0]);

        request.upload(formData, progress => {
          this.uploadPercent = progress

        }, () => {
          this.$message({
            message: '上传成功',
            type: 'success'
          });
          this.$emit('update:fileName', this.addFileName)
        }, error => {
          console.log(error)
          this.$message({
            message: '上传失败',
            type: 'error'
          });
        })
      },

    }
  }
</script>

<style scoped>

</style>