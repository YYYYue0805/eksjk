<template>
  <div :class="rootClass">
    <span>【</span>
    <strong
      class="text-field-content"
      :contenteditable="!isStatic ? 'plaintext-only' : 'false'"
      :placeholder="placeholder"
      v-html="present"
      @input="onChanged"
    ></strong>
    <!-- <span v-else>{{inputValue}}</span> -->
    <span>】</span>
  </div>
</template>

<script>
  export default {
    name: "TwoInput",
    props: {
      placeholder: {
        type: String,
        default: ''
      },
      type: {
        type: String,
        default: 'number'
      },
      editable: {
        type: Boolean,
        default: true
      },
      disabled: {
        type: Boolean,
        default: false
      },
      present: {
        type: [String, Number],
        default: null
      },
      configIndex: Number
    },
    data() {
      return {
        inputValue: this.getInputValue(),
        isStatic: this.editable
      }
    },
    watch: {
      present() {
        this.inputValue = this.getInputValue()
      }
    },
    computed: {
      rootClass() {
        return {
          'text-field': true,
          'text-field-disabled': this.disabled
        }
      }
    },
    methods: {
      getInputValue() {
        if (this.type == 'number') {
          return this.present && this.present != 0 ? this.present : ''
        } else {
          return this.present ? this.present : ''
        }
      },
      onChanged(e) {
        let content = e.srcElement.innerHTML
        if (this.type == 'number' && /[^0-9.]/.test(content)) {
          const result = content.match(/^\d*\.{0,1}\d{0,}$/)
          if (!result) {
            e.srcElement.innerHTML = ''
          } else {
            e.srcElement.innerHTML = result[0]

            setTimeout(() => {
              const selection = getSelection()
              selection.selectAllChildren(e.srcElement)
              selection.collapseToEnd()
            }, 10)
          }
        } else {
          if (content.substr(content.length - 1, 1) === '.') {
            if (content.split('.').length > 2) {
              e.srcElement.innerHTML = content.substr(0, content.length - 1)
            } else {
              e.srcElement.innerHTML = content
            }

            setTimeout(() => {
              const selection = getSelection()
              selection.selectAllChildren(e.srcElement)
              selection.collapseToEnd()
            }, 10)
          } else {
            const result = content.search(/^[0-9]+(.[0-9]{0,2})?$/)
            if (result != 0) {
              e.srcElement.innerHTML = content.substr(0, content.length - 1)
            } else {
              e.srcElement.innerHTML = content
            }
            setTimeout(() => {
              const selection = getSelection()
              selection.selectAllChildren(e.srcElement)
              selection.collapseToEnd()
            }, 10)
          }
        }

        this.inputValue = content
      },
      value() {
        if (this.type == 'number') {
          let v = parseFloat(this.inputValue)
          return isNaN(v) ? null : v
        } else {
          return this.inputValue
        }
      },
      plainText() {
        return this.type == 'number' ? this.inputValue.toString() : this.inputValue
      }
    },
    mounted() {
      // 判断是否禁用鼠标响应，是的话，切换到不可编辑状态
      let cs = getComputedStyle(this.$el, null)
      this.isStatic = !(cs.pointerEvents == 'auto')
    }
  }
</script>

<style scoped>
  .text-field {
    display: inline-block;
    color: #409eff;
    padding: 0;
    margin: 0;
  }

  .text-field-content {
    display: inline-block;
    min-width: 40px;
    height: 20px;
    line-height: 20px;
    vertical-align: middle;
    text-align: center;

    border: none;
    outline: 0 none;
  }

  /* 以下属性实现placeholder功能 */
  .text-field-content:empty:before {
    content: attr(placeholder);
    color: #bbb;
  }

  .text-field-disabled {
    color: #909399;
  }
</style>