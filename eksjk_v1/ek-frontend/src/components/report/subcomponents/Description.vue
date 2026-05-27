<script>
  import Select from './Select'
  import TextField from './TextField'
  import Checkbox from './Checkbox'
  import Radio from './Radio'
  import NewRadio from  './NewRadio'
  import InputText from  './InputText'
  import Line from './line'
  import TwoInput from './TwoInput'


  export default {
    name: 'Description',
    props: {
      template: {
        type: String,
        required: true
      },
      selectOptionsArray: {
        type: Array,
        default: null
      },
      isSection: {
        type: Boolean,
        default: false
      },
      ownerType: {
        type: String,
        default: ''
      },
      requestOtherDescription: {
        type: Function,
        required: true
      },
      canBeDisabled: {
        type: Boolean,
        default: false
      },
      present: {
        type: Object,
        default: null
      },
      templateName: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        reserveText: null,
        disabled: this.isDisabled()
      }
    },
    watch: {
      present() {
        this.disabled = this.isDisabled()
      }
    },
    render(h) {
      // console.log('template=', this.template)

      const selectOptionsArray = this.parseSelectOptions(h, this.selectOptionsArray)
      const selects = this.getSelects(this.template, selectOptionsArray, this.ownerType != 'option')
      const textFields = this.getTextFields(this.template, this.ownerType != 'option')
      const checkboxes = this.getCheckBox(this.template)
      const radios = this.getRadio(this.template)
      const newRadio = this.getNewRadio(this.template)
      const inputText = this.getInputText(this.template)
      const line = this.getLine(this.template)
      const twoText = this.getTwoText(this.template, this.ownerType != 'option')

      let items = selects ? selects : []
      if (textFields) {
        items = items.concat(textFields)
      }
      if (checkboxes) {
        items = items.concat(checkboxes)
      }
      if (radios) {
        items = items.concat(radios)
      }
      if (newRadio) {
        items = items.concat(newRadio)
      }
      if (inputText) {
        items = items.concat(inputText)
      }
      if (line) {
        items = items.concat(line)
      }
      if (twoText) {
        items = items.concat(twoText)
      }

      items = items.sort(function (a, b) {
        return a.from - b.from
      })

      // 添加一个序号，保证生成配置的时候顺序正确
      let i = 0
      for (let item of items) {
        item.data.props.configIndex = i++;
      }

      let components = []
      let reserveText = null
      let start = 0
      for (let item of items) {
        let text = this.template.substring(start, item.from)
        if (text.length > 0) {
          let texts = this.parseText(h, text)
          if (this.ownerType == 'select' && !reserveText) {
            reserveText = texts[0]
            this.reserveText = reserveText
            texts.splice(0, 1)
          }

          components = components.concat(texts)
        }
        start += text.length + item.length

        components.push(h(item.component, item.data))
      }

      if (start < this.template.length) {
        components.push(this.template.substring(start))
      }

      return h(this.isSection ? 'p' : 'span', {
        class: {
          description: this.ownerType == '',
          disabled: this.disabled
        }
      }, components)
    },
    methods: {
      getTwoText(template, editable){
        let items = []
        // 匹配小括号
        let items1 = template.matchAll(/\((.*?)\)/g)
        if (items1) {
          items1 = [...items1]
          items1.forEach(item => {
            item.isNumber = true
          })
          items = items.concat(items1)
        }

        if (items.length == 0) return

        let textFields = []
        let i = 0
        for (const item of items) {
          textFields.push({
            from: item.index,
            length: item[0].length,
            component: TwoInput,
            data: {
              props: {
                type: item.isNumber ? 'number' : 'text',
                placeholder: item[1],
                editable,
                disabled: this.disabled,
                present: this.getPresent('TwoInput', i++)
              }
            }
          })
        }

        return textFields
      },
      getLine(template){
        let items = template.matchAll(/\|(.*?)\|/g)

        if (items.length == 0) return

        let textFields = []
        let i = 0
        for (const item of items) {
          let present = this.getPresent('Line', i++)
          textFields.push({
            from: item.index,
            length: item[0].length,
            component: Line,
            data: {
              props: {
                present,
              }
            }
          })
        }
        return textFields
      },
      getInputText(template){
        let items = []
        let items1 = template.matchAll(/\{(.*?)\}/g)
        if (items1) {
          items1 = [...items1]
          items1.forEach(item => {
            item.isNumber = true
          })
          items = items.concat(items1)
        }

        if (items.length == 0) return

        let textFields = []
        let i = 0
        for (const item of items) {
          let disabled = false
          let present = this.getPresent('InputText', i++)
          if(this.disabled){
            disabled = true;
            present = ''
          }
          textFields.push({
            from: item.index,
            length: item[0].length,
            component: InputText,
            data: {
              props: {
                present,
                disabled
              }
            }
          })
        }

        return textFields
      },
      getNewRadio(template){
        let matchAll = template.matchAll(/&(.*?)&/g)
        if (!matchAll) return

        let newRadio = []
        let i = 0
        for (let item of [...matchAll]) {
          let data = this.getNewRadioData(item.index == 0)
          data.props.present = this.getPresent('NewRadio', i++)
          data.props.text = item[1]
          data.props.templateName = this.templateName
          data.props.disabled = false
          if(this.disabled){
            data.props.disabled = true
          }
          newRadio.push({
            from: item.index,
            length: item[0].length,
            component: NewRadio,
            data
          })
        }
        return newRadio.length > 0 ? newRadio : null
      },
      getRadio(template) {
        let matchAll = template.matchAll(/%(.*?)%/g)
        if (!matchAll) return

        let radios = []
        let i = 0
        for (let item of [...matchAll]) {
          let data = this.getRadioData(item.index == 0)
          data.props.present = this.getPresent('Radio', i++)
          data.props.text = item[1]
          data.props.templateName = this.templateName
          data.props.disabled = false
          if(this.disabled){
            data.props.disabled = true
          }
          radios.push({
            from: item.index,
            length: item[0].length,
            component: Radio,
            data
          })
        }
        return radios.length > 0 ? radios : null
      },
      parseText(h, text) {
        let items = []

        // 匹配换行
        let items1 = text.matchAll(/\n/g)
        if (items1) {
          items1 = [...items1]
          items = items.concat(items1)
        }

        // 匹配缩进
        let items2 = text.matchAll(/--/g)
        if (items2) {
          items = items.concat([...items2])
        }

        if (items.length == 0) return [text]

        items = items.sort(function (a, b) {
          return a.from - b.from
        })

        let texts = []
        let index = 0
        let newlineCount = 0
        for (let item of items) {
          let subtext = text.substring(index, item.index)
          if (subtext.length > 0)
            texts.push(subtext)

          if (item[0] == '\n' && newlineCount < items1.length) {
            texts.push(h('br'))
            newlineCount++
          } else if (item[0] == '--') {
            texts.push(h('b', {
              domProps: {
                innerHTML: '&ensp;&ensp;'
              }
            }))
          }

          index = item.index + item[0].length
        }

        if (index < text.length) {
          texts.push(text.substring(index))
        }

        return texts
      },
      parseSelectOptions(h, optionsArray) {
        if (!optionsArray) return

        let selectOptionsArray = []
        let i = 0
        for (let item of optionsArray) {
          if (typeof item === 'string') {
            let option = item.replace(/[\r\n ]/g, '').split(',')
            if (option.length > 0) {
              selectOptionsArray.push(option)
            }
          } else if (Array.isArray(item)) {
            let options = []
            for (let subitem of item) {
              if (typeof subitem === 'string') {
                options.push(subitem)
              } else {
                let present = this.getPresent('Select', i)
                present = present ? present.value : null
                let option = this.requestOtherDescription(h, subitem.template, subitem.optionsArray, false, 'option')
                option.backup = this.requestOtherDescription(h, subitem.template, subitem.optionsArray, false, 'select', present)
                options.push(option)
              }
            }
            selectOptionsArray.push(options)
          }

          i++
        }

        return selectOptionsArray
      },
      getSelects(template, selectOptionsArray, editable) {
        let reg = /__/g
        const items = template.matchAll(reg)
        if (!items) return

        let selects = []
        let i = 0
        for (const item of items) {
          selects.push({
            from: item.index,
            length: item[0].length,
            component: Select,
            data: {
              props: {
                optionsArray: selectOptionsArray[i],
                editable,
                disabled: this.disabled,
                present: this.getPresent('Select', i)
              }
            }
          })

          i++
        }

        return selects
      },
      getTextFields(template, editable) {
        let items = []
        // 匹配中括号
        let items1 = template.matchAll(/\[(.*?)\]/g)
        if (items1) {
          items1 = [...items1]
          items1.forEach(item => {
            item.isNumber = true
          })
          items = items.concat(items1)
        }

        // 匹配小括号
        // const items2 = template.matchAll(/\((.*?)\)/g)
        // if (items2) {
        //   items = items.concat([...items2])
        // }

        if (items.length == 0) return

        let textFields = []
        let i = 0
        for (const item of items) {
          textFields.push({
            from: item.index,
            length: item[0].length,
            component: TextField,
            data: {
              props: {
                type: item.isNumber ? 'number' : 'text',
                placeholder: item[1],
                editable,
                disabled: this.disabled,
                present: this.getPresent('TextField', i++)
              }
            }
          })
        }

        return textFields
      },
      getCheckBox(template) {
        let matchAll = template.matchAll(/<(.*?)>/g)
        if (!matchAll) return

        let checkboxes = []
        let i = 0
        for (let item of [...matchAll]) {
          let data = this.getCheckBoxData(item.index == 0)
          data.props.present = this.getPresent('Checkbox', i++)
          data.props.text = item[1]
          checkboxes.push({
            from: item.index,
            length: item[0].length,
            component: Checkbox,
            data
          })
        }

        return checkboxes.length > 0 ? checkboxes : null
      },
      getCheckBoxData(asSectionHeader) {
        if (asSectionHeader) {
          return {
            props: {
              asSectionHeader,
            },
            nativeOn: {
              change: asSectionHeader ? this.onCheckboxChecked : null
            }
          }
        } else {
          return {
            props: {
              disabled: this.disabled
            }
          }
        }
      },
      onCheckboxChecked(e) {
        this.disabled = !e.srcElement.checked
      },
      getRadioData(asSectionHeader) {
        if (asSectionHeader) {
          return {
            props: {
              asSectionHeader,
            },
            nativeOn: {
              change: asSectionHeader ? this.onRadioChecked : null
            }
          }
        } else {
          return {
            props: {
              disabled: this.disabled
            }
          }
        }
      },
      onRadioChecked(e) {
        this.disabled = !e.srcElement.checked
      },
      changeRadio() {
        this.$parent.cRadio();
      },
      getNewRadioData(asSectionHeader){
        if (asSectionHeader) {
          return {
            props: {
              asSectionHeader,
            },
            nativeOn: {
              change: asSectionHeader ? this.onNewRadioChecked : null
            }
          }
        } else {
          return {
            props: {
              disabled: this.disabled
            }
          }
        }
      },
      onNewRadioChecked(e){
        this.disabled = !e.srcElement.checked
      },
      changeNewRadio() {
        this.$parent.newRadio();
      },
      getPresent(component, i) {
        if (this.present && this.present[component]) {
          return this.present[component][i]
        }
        return null
      },
      isDisabled() {
        return this.canBeDisabled && !this.present || (this.present && this.present.disabled)
      },
      value() {
        if (this.disabled) {
          return {
            disabled: this.disabled
          }
        }

        let v = {}
        let names = ['Select', 'TextField', 'Checkbox', 'Radio', 'NerRadio', 'InputText', 'TwoInput']
        for (let name of names) {
          let components = this.$children.filter(function (item) {
            return item.$options.name == name
          })

          // 先排序
          components.sort(function (a, b) {
            return a.configIndex - b.configIndex
          })

          let subvalue = components.map(function (item) {
            return item.value()
          })
          if (subvalue.length > 0) v[name] = subvalue
        }

        return v
      },
      plainText() {
        // 先排序
        let children = [].concat(this.$children)
        children.sort(function (a, b) {
          return a.configIndex - b.configIndex
        })

        let text = this.reserveText || ''
        let nodeCount = 0
        for (let i = 0; i < this._vnode.children.length; i++) {
          const child = this._vnode.children[i]
          if (child.tag == 'br')
            continue

          if (child.tag) {
            const node = children[nodeCount++]
            if (node.plainText)
              text += node.plainText()
          } else {
            text += child.text
          }
        }

        text = text.replace(/ {2,}/g, ' ')
        return text
      },
      isRadio() {
        let radio = false;
        this.$children.filter(function (item) {
          if(item.$options.name === 'Radio' || item.$options.name === 'Checkbox'){
            radio = true
          }
        })
        return radio
      },
      isNewRadio() {
        let radio = false;
        this.$children.filter(function (item) {
          if(item.$options.name === 'NewRadio' || item.$options.name === 'Checkbox'){
            radio = true
          }
        })
        return radio
      },
    }
  }
</script>

<style scoped>
  .description {
    margin: 10px 0;
    padding-left: 18px;
    position: relative;
    font-size: 14px;
  }

  .disabled {
    color: #909399;
  }
</style>
