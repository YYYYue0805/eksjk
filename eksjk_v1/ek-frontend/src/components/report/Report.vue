<script>
  import templates from '../../script/reportTemplates'
  import Description from './subcomponents/Description'

  function makeDescription(h, template, selectOptionsArray,
                           isSection = false, ownerType = '', present = null, templateName) {
    let reserveText = null
    if (ownerType == 'select') {
      let match = template.match(/[_{2,2}[\n]/)
      if (match) {
        reserveText = template.substring(0, match.index)
      }
    }

    let vnode = h(Description, {
      props: {
        template,
        selectOptionsArray,
        isSection,
        ownerType,
        requestOtherDescription: makeDescription,
        canBeDisabled: template.substr(0, 2) == '<>' || template.substr(0, 2) == '%%' || template.substr(0, 2) == '&&',
        present,
        templateName
      }
    })

    if (reserveText)
      vnode.reserveText = reserveText

    return vnode
  }

  export default {
    name: 'Report',
    props: {
      // 模板名称，取templates里面的key值
      templateName: {
        type: String,
        required: true
      },
      // 预设值
      present: {
        type: Array,
        default: null
      },
      // 是否静态内容
      isStatic: {
        type: Boolean,
        default: false
      }
    },
    render(h) {
      let sections = []
      let i = 0
      if (this.templateName) {
        for (let item of templates[this.templateName]) {
          let section = makeDescription(h, item.template, item.optionsArray, true, '', this.getPresent(i), this.templateName)
          sections.push(section)
          i++
        }
      }
      return h('div', {
        style: {
          'pointer-events': this.isStatic ? 'none' : 'auto'
        }
      }, sections)
    },
    watch: {
      templateName(newValue){
       if(newValue){
         this.cRadio()
         this.newRadio()
       }
     }
    },
    methods: {
      value() {
        let components = this.$children.filter(function (item) {
          return item.$options.name == 'Description'
        })

        return components.map(function (item) {
          return item.value()
        })
      },
      text() {
        let value = this.value()
        let descriptions = this.$children.filter(function (item) {
          return item.$options.name == 'Description'
        })

        let sections = []
        for (let i = 0; i < descriptions.length; ++i) {
          if (value[i].disabled) continue

          sections.push(descriptions[i].plainText())
        }

        const text = sections.join('\n')
        return text
      },
      cRadio() {
        let descriptions = this.$children.filter(function (item) {
          return item.$options.name == 'Description'
        })

        let sections = []
        for (let i = 0; i < descriptions.length; ++i) {
          if (descriptions[i].isRadio()) {
            sections.push(descriptions[i].disabled = true)
          }
        }
      },
      newRadio(){
        let descriptions = this.$children.filter(function (item) {
          return item.$options.name == 'Description'
        })

        let sections = []
        for (let i = 0; i < descriptions.length; ++i) {
          if (descriptions[i].isNewRadio()) {
            sections.push(descriptions[i].disabled = true)
          }
        }
      },
      getPresent(i) {
        if (!this.present) return null

        return this.present[i]
      }
    }
  }
</script>

<style scoped>
  * {
    outline: 0 none;
  }


</style>