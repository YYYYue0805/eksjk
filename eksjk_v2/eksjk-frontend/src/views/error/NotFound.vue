<template>
  <div class="not-found">
    <div class="not-found__content">
      <h1 class="not-found__code">404</h1>
      <p class="not-found__message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.not-found__content {
  text-align: center;
}

.not-found__code {
  font-size: 120px;
  font-weight: 700;
  color: #dcdfe6;
  line-height: 1;
  margin-bottom: 16px;
}

.not-found__message {
  font-size: 18px;
  color: #606266;
  margin-bottom: 32px;
}
</style>
