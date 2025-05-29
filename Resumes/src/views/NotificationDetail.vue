<script setup>
import { useRoute, useRouter } from 'vue-router'
import NotificationDetail from '../components/notification/NotificationDetail.vue'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 获取通知ID
const notificationId = route.params.id

// 删除后返回通知列表
const handleDeleted = () => {
  router.push('/notifications')
}
</script>

<template>
  <div class="notification-detail-page">
    <div class="page-header">
      <el-button plain @click="router.push('/notifications')">
        <el-icon><ArrowLeft /></el-icon>
        返回通知列表
      </el-button>
      <h2 class="page-title">通知详情</h2>
    </div>
    
    <div class="detail-container">
      <NotificationDetail :notification-id="notificationId" @deleted="handleDeleted" />
    </div>
  </div>
</template>

<style scoped>
.notification-detail-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 130px);
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 0 20px;
}

.detail-container {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .notification-detail-page {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .page-title {
    margin: 0;
  }
}
</style> 