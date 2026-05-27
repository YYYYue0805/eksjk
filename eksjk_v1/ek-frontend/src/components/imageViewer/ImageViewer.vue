<template>
  <div class="image-viewer">
    <video v-if="videoPath" class="video-view" ref="video" loop :controls="!previewMode">
      <source :src="videoPath" type="video/mp4">
    </video>

    <div
      v-else
      class="image-canvas"
      ref="imageCanvas"
      @mousemove="onMousemove($event)"
      @mouseenter="isOverlayPixel = true"
      @mouseleave="isOverlayPixel = false"
      v-loading="loading"
    >
      <div v-if="error" class="overlay-box overlay-box-center">{{ error }}</div>

      <div v-if="!previewMode" class="overlay-box overlay-box-left-top">
        <div class="overlay">{{patient.name}}</div>
        <div class="overlay">{{patient.id}}</div>
        <div class="overlay">{{patient.birthDate}}</div>
        <div class="overlay">{{patient.age}}</div>
        <div class="overlay">{{patient.sex}}</div>
      </div>

      <div v-if="!previewMode" class="overlay-box overlay-box-left-bottom">
        <div class="overlay">Size: {{size.width}} x {{size.height}}</div>
      </div>

      <div v-if="!previewMode" class="overlay-box overlay-box-right-top">
        <div class="overlay">{{rightTop.studyDescription}}</div>
        <div class="overlay">{{rightTop.seriesDescription}}</div>
      </div>

      <div v-if="!previewMode" class="overlay-box overlay-box-right-bottom">
        <div class="overlay" :hidden="!isOverlayPixel">X: {{pixelCoords.x}}px, Y: {{pixelCoords.y}}px</div>
        <div class="overlay">Zoom: {{zoom}}</div>
        <div class="overlay">WW: {{window.width}}, WC: {{window.center}}</div>
      </div>
    </div>
  </div>
</template>

<script>
  import * as cornerstone from 'cornerstone-core'
  import cornerstoneTools from 'cornerstone-tools'
  import MetaDataSet from '../../script/cornerstone/metaDataSet'

  export default {
    name: 'ImageViewer',
    props: {
      path: {
        type: Object,
        default() {
          return {
            // 查询ID
            queryId: '',
            // 器官
            organ: 'thyroid',
            // 图片路径末端，形如：
            // 超声图像/应变弹性成像/静态横切面或纵切面/1.dcm
            endpoint: ''
          }
        }
      },
      previewMode: {
        type: Boolean,
        default: true
      },
    },
    data() {
      return {
        element: null,
        image: null,
        loading: false,
        isOverlayPixel: false,
        patient: {
          name: '',
          id: '',
          birthDate: '',
          age: '',
          sex: ''
        },
        rightTop: {
          studyDescription: '',
          seriesDescription: '',
        },
        imageCount: 0,
        zoom: 1,
        pixelCoords: {x: 0, y: 0},
        window: {
          width: '-',
          center: '-'
        },
        mouseButton: -1,
        mousePosition: {x: 0, y: 0},
        size: {
          width: 0,
          height: 0
        },
        numFrames: 0,
        videoPath: '',
        error: ''
      }
    },
    watch: {
      'path.endpoint'() {
        this.showImage()
      },
      'path.queryId'() {
        // 切换病例的时候清空图像
        this.clear()
      }
    },
    methods: {
      showImage() {
        if (!this.path.queryId || !this.path.endpoint) return

        let endpoint = this.path.endpoint.replace(/\//g, '-')

        if (endpoint.indexOf('mp4') != -1) {
          this.videoPath = `${location.origin}/api/datamain/image?organ=` +
            `${this.path.organ}&queryId=${this.path.queryId}&type=mp4&path=${endpoint}`
          // videoPath变化后触发DOM渲染，之后$refs.video的值才能获取到
          // 所以需要将下面的代码延时到下一次DOM渲染完成后执行
          this.$nextTick(() => {
            this.$emit('loaded', true, this.$refs.video)
          })
          this.element = null
        } else {
          let schema = 'wadouri:'
          if (endpoint.indexOf('dcm') == -1) {
            schema = location.protocol
          }

          let imageId = schema + 'api/datamain/image?organ=' +
            `${this.path.organ}&queryId=${this.path.queryId}&path=${endpoint}`

          this.loadImage(imageId)
        }
      },
      showImageFromFile(file) {
        if (file.type.match(/image\//)) {
          let reader = new FileReader()
          reader.onload = (event) => {
            let src = event.target.result
            this.loadImage('localCommon:' + src)
          }
          reader.readAsDataURL(file)
        } else {
          /* global cornerstoneWADOImageLoader */
          const imageId = cornerstoneWADOImageLoader.wadouri.fileManager.add(file)
          this.loadImage(imageId)
        }
      },
      loadImage(imageId) {
        this.loading = true

        cornerstone.loadAndCacheImage(imageId).then((image) => {
          this.loading = false

          this.image = image
          this.updateImage(this.image)

          if (imageId.indexOf('wadouri') != -1) {
            // 判断是否多帧图像
            let numFrames = image.data ? image.data.intString('x00280008') : 0
            if (numFrames > 1) {
              this.numFrames = numFrames
              let imageIds = []
              for (let i = 0; i < numFrames; i++) {
                imageIds.push(`${imageId}?frame=${i}`)
              }
              let stack = {
                currentImageIdIndex: 0,
                imageIds: imageIds
              }

              // 添加堆叠工具
              cornerstoneTools.addStackStateManager(this.element, ['stack', 'playClip'])
              cornerstoneTools.addToolState(this.element, 'stack', stack)
            }

            if (!this.previewMode) {
              const metaDataSet = new MetaDataSet(this.image.data)
              this.patient.name = metaDataSet.PatientName
              this.patient.id = metaDataSet.PatientID
              this.patient.birthDate = metaDataSet.PatientBirthDate
              this.patient.age = metaDataSet.PatientAge
              this.patient.sex = metaDataSet.PatientSex
              this.rightTop.studyDescription = metaDataSet.StudyDescription
              this.rightTop.seriesDescription = metaDataSet.SeriesDescription
            }
          }

          if (!this.previewMode) {
            this.size.width = this.image.width
            this.size.height = this.image.height
          }

          this.$emit('loaded', this.numFrames > 1)
          this.error = ''
        }).catch(error => {
          this.loading = false
          console.log(error)
          // console.log(this.path.endpoint)
          if (error?.error?.indexOf('dicomParser') != -1) {
            this.error = '未支持的文件格式'
          } else {
            this.error = '图像/视频加载错误'
          }
        })
      },
      updateImage(image) {
        if (!this.element) {
          this.enableCornerstone()
        }

        const viewport = cornerstone.getDefaultViewportForImage(this.element, image)
        cornerstone.displayImage(this.element, image, viewport)
      },
      getElement() {
        return this.element
      },
      reset() {
        if (this.image)
          cornerstone.reset(this.element)
      },
      onImageRendered(e) {
        const eventData = e.detail
        cornerstone.setToPixelCoordinateSystem(eventData.enabledElement, eventData.canvasContext)
        const viewport = eventData.viewport
        this.zoom = viewport.scale.toFixed(2)
        this.window.width = Math.round(viewport.voi.windowWidth)
        this.window.center = Math.round(viewport.voi.windowCenter)
      },
      onMousemove(e) {
        if (!this.image || this.loading) {
          return
        }

        const pixelCoords = cornerstone.pageToPixel(this.element, e.pageX, e.pageY)
        this.pixelCoords = {
          x: Math.round(pixelCoords.x),
          y: Math.round(pixelCoords.y),
        }

        this.mousePosition = {
          x: e.pageX,
          y: e.pageY
        }
      },
      clear() {
        if (this.element) {
          cornerstone.disable(this.element)
          this.image = null
          cornerstone.enable(this.element)
        }

        this.videoPath = ''
        this.error = ''
        this.image = null
      },
      enableCornerstone () {
        if (this.videoPath) return

        this.element = this.$refs.imageCanvas
        cornerstone.enable(this.element)
        if (!this.previewMode) {
          this.element.addEventListener('cornerstoneimagerendered', this.onImageRendered)
        }

        // 监听resize，解决父元素从隐藏到显示时，图像没有显示的问题
        const resizeObserver = new ResizeObserver(() => {
          if (this.element)
            cornerstone.resize(this.element)
        })
        resizeObserver.observe(this.element)

        this.$emit('cornerstoneChanged', this.element)
      }
    },
    mounted() {
      this.showImage()
    },
  }
</script>

<style scoped>
  .image-viewer {
    overflow: hidden;
  }

  .video-view {
    width: 100%;
    height: 100%;
    background-color: rgb(80, 80, 80);
  }

  .image-canvas {
    width: 100%;
    height: 100%;
    color: white;
    position: relative;
  }

  .overlay-box {
    display: flex;
    flex-direction: column;

    position: absolute;
  }

  .overlay-box-center {
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    color: grey;
  }

  .overlay-box-left-top {
    left: 5px;
    top: 5px;

    text-align: left;
  }

  .overlay-box-left-bottom {
    left: 5px;
    bottom: 5px;

    text-align: left;
  }

  .overlay-box-right-top {
    right: 5px;
    top: 5px;

    text-align: right;
  }

  .overlay-box-right-bottom {
    right: 5px;
    bottom: 5px;

    text-align: right;
  }

  .overlay {
    color: white;
    text-shadow: 1px 1px 2px rgb(59, 118, 247);
  }
</style>