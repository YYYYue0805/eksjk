<template>
  <div class="page-header">
    <div class="page-header__left">
      <h2 class="page-header__title">{{ title }}</h2>
      <span v-if="subtitle" class="page-header__subtitle">{{ subtitle }}</span>
    </div>
    <div class="page-header__right">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
/**
 * 页面标题组件
 * 页面标题 + 副标题 + 右侧操作按钮区（通过 slot）
 */
defineProps({
  /** 页面标题 */
  title: {
    type: String,
    required: true
  },
  /** 副标题 */
  subtitle: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 0 var(--ek-spacing-base) 0;
}

.page-header__left {
  display: flex;
  align-items: baseline;
  gap: var(--ek-spacing-sm);
}

.page-header__title {
  font-size: 20px;
  font-weight: 600;
  color: var(--ek-text-primary);
  line-height: 1.4;
}

.page-header__subtitle {
  font-size: 14px;
  color: var(--ek-text-secondary);
}

.page-header__right {
  display: flex;
  align-items: center;
  gap: var(--ek-spacing-sm);
}
</style>
