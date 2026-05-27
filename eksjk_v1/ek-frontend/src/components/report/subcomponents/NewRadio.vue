<template>
  <span v-if="disabled">
    <input
        @change="changeNewRadio"
        type="radio"
        :name="templateName + 's'"
        :checked="!disabled"
        :class="{'header-checkbox': asSectionHeader}">
    <span>{{ text }}</span>
  </span>

    <span v-else>
      <input
        @change="changeNewRadio"
        type="radio"
        :name="templateName + 's'"
        :checked="!disabled"
        :class="{'header-checkbox': asSectionHeader}">
      <span>{{ text }}</span>
    </span>
</template>

<script>
  export default {
    name: "NewRadio",
    props: {
      present: {
        type: Boolean,
        default: false
      },
      asSectionHeader: {
        type: Boolean,
        default: false
      },
      text: {
        type: String,
        default: ''
      },
      disabled: {
        type: Boolean,
        default: false
      },
      configIndex: Number,
      templateName: {
        type: String,
        required: true
      },
      label: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        radio: this.disabled,
      }
    },
    watch: {
      present(v) {
        this.radio = v
      }
    },
    methods: {
      value() {
        return this.radio
      },
      plainText() {
        return this.radio ? this.$el.innerText : ''
      },
      changeNewRadio(){
        this.$parent.changeNewRadio();
      },
    }
  }
</script>

<style scoped>
  .header-checkbox {
    position: absolute;
    left: 0px;
    top: 2px;
  }
</style>