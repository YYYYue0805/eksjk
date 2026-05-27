<template>
  <el-drawer
    v-model="visible"
    :title="title"
    :size="size"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    :destroy-on-close="true"
    @open="handleOpen"
    @close="handleClose"
  >
    <template #header>
      <span class="form-dialog__title">{{ title }}</span>
    </template>

    <!-- 表单内容插槽 -->
    <div class="form-dialog__body">
      <slot />
    </div>

    <!-- 底部按钮 -->
    <template #footer>
      <div class="form-dialog__footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ submitText }}
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
/**
 * 表单抽屉组件
 * 封装 Element Plus el-drawer，支持新增/编辑两种模式
 */
import { computed } from 'vue'

const props = defineProps({
  /** 是否显示（v-model） */
  modelValue: {
    type: Boolean,
    default: false
  },
  /** 标题 */
  title: {
    type: String,
    default: ''
  },
  /** 抽屉宽度 */
  size: {
    type: [String, Number],
    default: '600px'
  },
  /** 是否为编辑模式 */
  isEdit: {
    type: Boolean,
    default: false
  },
  /** 提交按钮文字 */
  submitText: {
    type: String,
    default: ''
  },
  /** 提交按钮 loading */
  submitLoading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'submit', 'cancel', 'open', 'close'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

/** 计算提交按钮文字 */
const computedSubmitText = computed(() => {
  if (props.submitText) return props.submitText
  return props.isEdit ? '保存' : '新增'
})

/**
 * 打开回调
 */
function handleOpen() {
  emit('open')
}

/**
 * 关闭回调
 */
function handleClose() {
  emit('close')
}

/**
 * 取消
 */
function handleCancel() {
  visible.value = false
  emit('cancel')
}

/**
 * 提交
 */
function handleSubmit() {
  emit('submit')
}
</script>

<style scoped>
.form-dialog__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ek-text-primary);
}

.form-dialog__body {
  padding: 0 var(--ek-spacing-lg);
}

.form-dialog__footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--ek-spacing-sm);
}
</style>
