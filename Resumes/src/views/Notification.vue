<script setup>
import { useNotificationStore } from '../stores/notification'
import NotificationList from '../components/notification/NotificationList.vue'

const notificationStore = useNotificationStore()

// 页面加载时初始化通知
notificationStore.initNotifications()
</script>

<template>
  <div class="notification-center">
    <div class="notification-header">
      <h2 class="center-title">
        通知中心
        <el-badge v-if="notificationStore.unreadCount > 0" :value="notificationStore.unreadCount" />
      </h2>
    </div>
    
    <div class="notification-container">
      <NotificationList />
    </div>
  </div>
</template>

<style scoped>
.notification-center {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 130px);
}

.notification-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #dcdfe6;
}

.center-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.notification-container {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .notification-center {
    padding: 15px;
  }
  
  .center-title {
    font-size: 20px;
  }
}
</style> 