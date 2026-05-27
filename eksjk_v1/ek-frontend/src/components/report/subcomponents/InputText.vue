<template>
    <input
      :name="configIndex"
      :disabled="disabled"
      type="text"
      v-model="parentValue"
      :class="rootClass"
      @input="onChanged"
    >
</template>

<script>
  export default {
    name: "InputText",
    props: {
      present: {
        type: String,
        default: null
      },
      disabled: {
        type: Boolean,
        default: false
      },
      configIndex: Number,
    },
    data() {
      return {
        parentValue: this.getInputValue(),
        inputValue: this.getInputValue(),
      }
    },
    watch: {
      present() {
        this.inputValue = this.getInputValue()
        this.parentValue = this.getInputValue()
      }
    },
    computed: {
      rootClass() {
        return {
          'inputText': true,
          'inputText-disabled': this.disabled
        }
      }
    },
    methods: {
      onChanged(e){
        this.inputValue = e.srcElement.value
      },
      getInputValue() {
        return this.present
      },
      value() {
        return this.inputValue
      },
      plainText() {
        return this.inputValue
      },

    },
  }
</script>

<style scoped>
  .inputText {
    width: 10vw;
    border: 0;
    outline: none;
    border-bottom: 1px blueviolet solid;
  }

  .inputText-disabled{
    width: 10vw;
    border: 0;
    outline: none;
    border-bottom: 1px gray solid;
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
</style>