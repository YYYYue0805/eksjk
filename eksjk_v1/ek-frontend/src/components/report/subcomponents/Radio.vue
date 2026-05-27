<template>
    <span v-if="disabled">
      <input
        @change="changeRadio"
        type="radio"
        :name="templateName"
        :checked="!disabled"
        :class="{'header-checkbox': asSectionHeader}">
      <span>{{ text }}</span>
    </span>

    <span v-else>
      <input
        @change="changeRadio"
        type="radio"
        :name="templateName"
        :checked="!disabled"
        :class="{'header-checkbox': asSectionHeader}">
      <span>{{ text }}</span>
    </span>
</template>

<script>
  export default {
    name: "Radio",
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
      changeRadio(){
        this.$parent.changeRadio();
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