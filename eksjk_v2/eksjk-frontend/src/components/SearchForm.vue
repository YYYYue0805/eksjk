<template>
  <div class="search-form">
    <el-form
      ref="formRef"
      :model="modelValue"
      :inline="true"
      class="search-form__form"
      @submit.prevent="handleSearch"
    >
      <!-- 搜索条件插槽 -->
      <slot />

      <!-- 操作按钮 -->
      <el-form-item class="search-form__actions">
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button
          v-if="showExpand && hasMoreSlot"
          link
          type="primary"
          @click="expanded = !expanded"
        >
          {{ expanded ? '收起' : '更多筛选' }}
          <el-icon>
            <ArrowUp v-if="expanded" />
            <ArrowDown v-else />
          </el-icon>
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 展开区域 -->
    <div v-if="showExpand && expanded" class="search-form__expand">
      <el-form :model="modelValue" :inline="true">
        <slot name="more" />
      </el-form>
    </div>
  </div>
</template>

<script setup>
/**
 * 搜索表单组件
 * 支持通过 slot 定义搜索条件，支持条件折叠/展开
 */
import { ref, useSlots } from 'vue'

const props = defineProps({
  /** 表单数据（v-model） */
  modelValue: {
    type: Object,
    required: true
  },
  /** 是否显示展开/收起按钮 */
  showExpand: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['search', 'reset', 'update:modelValue'])

const formRef = ref(null)
const expanded = ref(false)
const slots = useSlots()

/** 是否有 more 插槽内容 */
const hasMoreSlot = !!slots.more

/**
 * 搜索
 */
function handleSearch() {
  emit('search')
}

/**
 * 重置
 */
function handleReset() {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  emit('reset')
}

// 暴露方法供父组件调用
defineExpose({
  resetFields: () => formRef.value?.resetFields()
})
</script>

<style scoped>
.search-form {
  background-color: var(--ek-bg-color);
  padding: var(--ek-spacing-base);
  border-radius: var(--ek-radius-base);
  margin-bottom: var(--ek-spacing-base);
}

.search-form__form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
}

.search-form__form :deep(.el-form-item) {
  margin-bottom: var(--ek-spacing-sm);
  margin-right: var(--ek-spacing-base);
}

.search-form__actions {
  margin-left: auto;
}

.search-form__expand {
  border-top: 1px solid var(--ek-border-color-lighter);
  padding-top: var(--ek-spacing-sm);
  margin-top: var(--ek-spacing-xs);
}
</style>
