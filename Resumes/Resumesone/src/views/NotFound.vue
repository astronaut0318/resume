<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="not-found">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <div class="error-message">
        <h2>页面不存在</h2>
        <p>抱歉，您访问的页面不存在或已被删除</p>
      </div>
      <div class="actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
}

.not-found-content {
  text-align: center;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #1890ff;
  margin: 0;
  line-height: 1;
}

.error-message {
  margin: 24px 0 32px;
}

.error-message h2 {
  font-size: 24px;
  margin: 0 0 16px;
}

.error-message p {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style> 