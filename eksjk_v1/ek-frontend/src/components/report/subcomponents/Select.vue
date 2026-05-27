<script>
export default {
  name: 'Select',
  data () {
    return {
      index: this.present ? this.present.index : -1,
    }
  },
  props: {
    optionsArray: {
      type: Array,
      default: null
    },
    editable: {
      type: Boolean,
      default: true
    },
    current: {
      type: Number,
      default: -1
    },
    disabled: {
      type: Boolean,
      default: false
    },
    present: {
      type: Object,
      default: null
    },
    configIndex: Number
  },
  watch: {
    // 监测选项组是否发生变化
    // 设计情况下选项是不会发生变化的，不知道是否是Vue的Bug，暂时这样处理
    optionsArray() {
      this.index = -1
    },
    present(v) {
      this.index = v ? v.index : -1
    }
  },
  computed: {
    currentOption() {
      if (this.index > -1 && this.index < this.optionsArray.length) {
        let option = this.optionsArray[this.index]
        return option.backup ? option.backup : option
      } else {
        return null
      }
    },
  },
  render(h) {
    if (!this.disabled && this.editable) {
      let content = null
      if (this.currentOption) {
        let text = this.currentOption
        if (typeof this.currentOption != 'string') {
          text = this.currentOption.reserveText
        }

        content = h('span', {
          class: {
            'select': true,
            'select-header': typeof this.currentOption != 'string'
          }
        }, [text])
      } else {
        content = h('span', {
          class: ['select', 'empty-select']
        })
      }

      let select = h('ElDropdown', {
        attrs: {
          placement: 'top',
          trigger: 'click',
          size: 'small'
        },
        on: {
          command: this.onItemClicked
        }
      }, [
        h('ElDropdownMenu', {
          slot: 'dropdown',
        }, this.optionsArray ? this.optionsArray.map(function (item, index) {
          return h('ElDropdownItem', {
            attrs: {
              key: index,
              command: index,
              divided: index > 0
            }
          }, [
            item
          ])
        }) : null),
        content
      ])

      let node = select
      if (this.currentOption && typeof this.currentOption != 'string') {
        node = h('span', [select, this.currentOption])
      }

      return node
    } else {
      return h('span', {
        class: {
          'select': true,
          'empty-select': true,
          'disabled-select': this.disabled
        }
      })
    }
  },
  methods: {
    onItemClicked(index) {
      this.index = index
    },
    getSubDescriptionValue() {
      for (let i of this.$children) {
        if (i.$options.name == 'Description') {
          return i.value()
        }
      }

      return ''
    },
    value() {
      let v = null
      if (this.currentOption) {
        let sub = typeof this.currentOption == 'string' ?
          this.currentOption :
          this.getSubDescriptionValue()

        v = {
          index: this.index,
          value: sub
        }
      }

      return v
    },
    plainText() {
      const getSubDescriptionText = function () {
        for (let i of this.$children) {
          if (i.$options.name == 'Description') {
            return i.plainText()
          }
        }

        return ''
      }.bind(this)

      let text = ''
      if (this.currentOption) {
        text += typeof this.currentOption == 'string' ?
          this.currentOption :
          getSubDescriptionText()
      }

      return text
    }
  }
}
</script>

<style scoped>
.select {
  cursor: pointer;
  color: #409eff;
  text-decoration: underline;
  font-size: 16px;
}

.select-header {
  color: #67C23A;
}

.empty-select {
  outline: 0 none;

  display: inline-block;
  min-width: 40px;
  min-height: 16px;
  border-bottom: 1px solid #1696e7;
}

.disabled-select {
  color: #909399;
  border-bottom: 1px solid #909399;

}
</style>