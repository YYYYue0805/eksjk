<template>
  <span class="report-checkbox">
    <input
      type="checkbox"
      v-model="checked"
      :disabled="disabled"
      :class="{'header-checkbox': asSectionHeader}">
    <span>{{ text }}</span>
  </span>
</template>

<script>
export default {
  name: 'Checkbox',
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
    configIndex: Number
  },
  data() {
    return {
      checked: this.present
    }
  },
  watch: {
    present(v) {
      this.checked = v
    }
  },
  methods: {
    value() {
      return this.checked
    },
    plainText() {
      return this.checked ? this.$el.innerText : ''
    }
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