<template>
  <div class="image-palette" v-show="visible" @contextmenu.prevent>
    <div class="toolbar">
      <el-button type="text" :disabled="!!video" icon="el-icon-rank" @click="onPan">平移</el-button>
      <el-button type="text" :disabled="!!video" icon="el-icon-zoom-in" @click="onZoom">缩放</el-button>
      <el-button type="text" :disabled="!!video" icon="el-icon-refresh" @click="onRotate">旋转</el-button>
      <el-button type="text" :disabled="!!video" icon="el-icon-monitor" @click="onWwWc">调窗</el-button>
      <el-button type="text" :disabled="!!video" icon="el-icon-refresh-left" @click="onReset">重置</el-button>

      <i v-if="isMultiFrames && !video" :class="playButtonClass" @click="onPlay"></i>
      <el-slider
        v-if="isMultiFrames && !video"
        v-model="frameRate"
        :min="1"
        :max="30"
        style="width: 100px;"
        @change="onFrameRateChanged"
      ></el-slider>

      <div style="flex: 1;"></div>

      <el-button type="text" @click="onClose">关闭</el-button>
    </div>
    
    <ImageViewer
      v-if="visible"
      ref="imageViewer"
      :path="imagePath"
      :previewMode="false"
      style="flex: 1;"
      @loaded="onImageLoaded"
      @cornerstoneChanged="initTool"
    ></ImageViewer>
  </div>
</template>

<script>
import ImageViewer from './ImageViewer'
import cornerstoneTools from 'cornerstone-tools'

export default {
  name: 'ImagePalette',
  model: {
    prop: 'visible',
    event: 'change'
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    imagePath: {
      type: Object,
      default() {
        return {
          queryId: '',
          organ: 'lung',
          endpoint: ''
        }
      }
    }
  },
  data() {
    return {
      element: null,
      isMultiFrames: false,
      video: false,
      playing: false,
      frameRate: 10
    }
  },
  watch: {
    visible(v) {
      if (!v) return

      this.$nextTick(() => {
        this.onReset()
      })
    }
  },
  computed: {
    playButtonClass() {
      return {
        'play-buttton': true,
        'el-icon-video-play': !this.playing,
        'el-icon-video-pause': this.playing
      }
    }
  },
  methods: {
    onPan() {
      cornerstoneTools.setToolActiveForElement(
        this.element,
        'Pan',
        { mouseButtonMask: 1 }
      )
    },
    onZoom() {
      cornerstoneTools.setToolActiveForElement(
        this.element,
        'Zoom',
        { mouseButtonMask: 1 }
      )
    },
    onRotate() {
      cornerstoneTools.setToolActiveForElement(
        this.element,
        'Rotate',
        { mouseButtonMask: 1 }
      )
    },
    onWwWc() {
      cornerstoneTools.setToolActiveForElement(
        this.element,
        'Wwwc',
        { mouseButtonMask: 1 }
      )
    },
    onReset() {
      this.$refs.imageViewer.reset()
    },
    onClose() {
      if (this.playing) {
        this.onPlay()
      }
      this.video?.pause()
      
      this.$emit('change', false)
    },
    onImageLoaded(isMultiFrames, video=null) {
      this.isMultiFrames = isMultiFrames
      this.video = video
    },
    onPlay() {
      if (this.playing) {
        if (this.video) {
          this.video.pause()
        } else {
          cornerstoneTools.stopClip(this.element)
        }
      } else {
        if (this.video) {
          this.video.play()
        } else {
          cornerstoneTools.playClip(this.element, this.frameRate)
        }
      }

      this.playing = !this.playing
    },
    onFrameRateChanged() {
      if (this.playing)
        cornerstoneTools.playClip(this.element, this.frameRate)
    },
    initTool() {
      const element = this.$refs.imageViewer.getElement()
      // 平移工具
      cornerstoneTools.addToolForElement(element, cornerstoneTools.PanTool)
      // 缩放工具
      cornerstoneTools.addToolForElement(element, cornerstoneTools.ZoomTool, {
        // Optional configuration
        configuration: {
          invert: false,
          preventZoomOutsideImage: false,
          minScale: .2,
          maxScale: 10.0,
        }
      })
      // 旋转工具
      cornerstoneTools.addToolForElement(element, cornerstoneTools.RotateTool)
      // 调窗工具
      cornerstoneTools.addToolForElement(element, cornerstoneTools.WwwcTool)

      this.element = element
    }
  },
  components: {ImageViewer}
}
</script>

<style scoped>
.image-palette {
  position: fixed;
  width: 100vw;
  height: 100vh;
  z-index: 100;
  background-color: rgb(51, 51, 51);

  display: flex;
  flex-direction: column;

  left: 0;
  top: 0;
}

.toolbar {
  display: flex;
  justify-items: center;
  align-items: center;
  padding: 5px 5px;
}

.play-buttton {
  font-size: 28px;
  color: rgb(64, 158, 255);
  margin: 0 20px;
}
</style>