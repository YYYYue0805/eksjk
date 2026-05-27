<template>
  <div class="image-upload"
       style="display: flex"
       v-loading="loading"
       element-loading-text="正在上传，请勿刷新或者关闭页面"
       element-loading-background="rgba(255, 255, 255, 0.5)"
  >
    <!--    <p>-->
    <!--      <span style="color: red !important;" v-if="required">*</span>-->
    <!--      <span>{{title}}</span>-->
    <!--    </p>-->

    <div
      class="upload-area"
      @click="onClicked"
      @mouseenter="hover = true"
      @mouseleave="hover = false"
      :style="{backgroundImage: `url(${bgImage})`}"
    >
      <!-- 文件选择 -->
      <div v-show="showTip">
        <div>
          <i class="el-icon-plus" style="font-size: 28px;"></i>
        </div>
        <div>{{ error ? error : '图像上传'}}</div>
        <input
          ref="fileInput"
          type="file"
          @change="onFileChange"
          style="display: none;"
          multiple
        >
      </div>

      <el-button
        icon="el-icon-delete"
        type="text"
        class="delete-button"
        @click.stop="onDelete"
        v-show="editable && !showTip && hover"
      >
      </el-button>

      <!-- 显示图像 -->
      <ImageViewer v-show="!showTip"
                   ref="imageViewer"
                   :path="imagePath"
                   style="width: 100%; height: 100%;"
      ></ImageViewer>
    </div>

    <ImagePalette v-model="visible" :imagePath="imagePath"></ImagePalette>
  </div>
</template>

<script>
  import ImageViewer from './ImageViewer'
  import ImagePalette from './ImagePalette'
  import request from '../../script/request'

  const BG_IMAGES = {
    thyroid: require('../../assets/img/thyroid_frame.png')
  }

  export default {
    name: 'ImageUpload',
    props: {
      caseId: {
        type: String,
        required: true
      },
      // 器官
      organ: {
        type: String,
        required: true
      },
      // 图片分类
      category: {
        type: String,
        required: true
      },
      // 图片名称
      fileName: {
        type: String,
      },

      editable: {
        type: Boolean,
        default: true
      },
      imageNum: {
        type: Number,
      },
    },
    data() {
      return {
        visible: false,
        loading: false,
        bgImage: BG_IMAGES[this.organ],
        hover: false,
        name: this.fileName,
        error: '',
        arr: [],
      }
    },
    watch: {
      fileName(v) {
        if (typeof v == 'string') {
          this.name = v
        } else {
          this.name = ''
        }
      }
    },
    computed: {
      imagePath() {
        return {
          queryId: this.caseId,
          organ: this.organ,
          category: this.category,
          endpoint: this.path,
        }
      },
      path() {
        if (!this.name) return ''

        let items = [this.category, this.name]
        return items.join('/')
      },
      showTip: {
        get() {
          return !this.name && this.editable
        },
        set() {
        }
      }
    },
    methods: {
      onClicked() {
        if (!this.editable) {
          if (this.name)
            this.visible = true
        } else if (this.editable) {
          if (this.name) {
            this.visible = true
          } else {
            this.visible = false
            this.$refs.fileInput.click()
          }
        } else if (!this.caseId) {
          this.$message('请先保存左边的病例信息')
        } else {
          this.$refs.fileInput.click()
        }
      },
      onFileChange(e) {
        let file = e.target.files[0]
        this.showTip = false
        this.$refs.imageViewer.clear();

        this.upload(file);
        this.$refs.fileInput.value = null
      },
      getPath(name) {
        return [
          ...this.category.split('/'),
          name
        ].join('-').replace(/\//g, '')
      },
      upload(file) {
        this.loading = true
        let date = new Date
        let dateArray = [
          date.getFullYear(), date.getMonth() + 1, date.getDate(),
          date.getMonth(), date.getMinutes(), date.getSeconds()
        ]

        let extention = '.dcm'
        let videoExtention = ''
        let match = file.type.match(/image\//)
        if (match) {
          extention = '.' + file.type.substring(match.index + match[0].length)
        }
        match = file.type.match(/video\//)
        if (match) {
          extention = '.' + file.type.substring(match.index + match[0].length)
          if (extention.indexOf('wmv') != -1) {
            extention = '.wmv'
          }
          if (extention.indexOf('avi') != -1) {
            extention = '.avi'
          }
          videoExtention = '.mp4'
        }

        match = file.type.match(/application\//)
        if (match) {
          extention = '.' + file.type.substring(match.index + match[0].length)
        }

        const name = dateArray.join('')

        let formData = new FormData()
        formData.append('queryId', this.caseId)
        formData.append('organ', this.organ)
        formData.append('path', this.getPath(name) + extention)
        formData.append('package', file)

        request.upload(formData, progress => {
          this.uploadPercent = progress

        }, () => {
          this.$message({
            message: '上传成功',
            type: 'success'
          });
          this.loading = false

          const realName = videoExtention ? name + videoExtention : name + extention
          this.$emit('update:fileName', realName)
          this.name = realName

        }, error => {
          // this.arr.push(0)
          console.log(error)
          this.loading = false
          const msg = '上传失败'
          this.$message(msg)
          this.error = msg
        })
      },
      onDelete() {
        if (!this.name) return

        const data = {
          queryId: this.caseId,
          organ: this.organ,
          path: this.getPath(this.name),
        }

        request.deleteImage(data, () => {
          this.$refs.imageViewer.clear()
          this.$emit('update:fileName', '')
          this.name = '';
          this.$emit('deleteImage', this.name)
          this.$message({
            message: '删除成功',
            type: 'success'
          })
        }, () => {
          this.$message('删除失败')
        })
      }
    },
    components: {
      ImageViewer,
      ImagePalette
    }
  }
</script>

<style scoped>
  .upload-area {
    width: 168px;
    height: 140px;

    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    color: grey;
    border: 1px solid #d2e0ec;
    background-size: 100%, 100%;

    position: relative;
  }

  .delete-button {
    position: absolute;
    top: 2px;
    right: 2px;
    z-index: 10;

    color: white;
    font-size: 24px;
    padding: 0;
  }
</style>
