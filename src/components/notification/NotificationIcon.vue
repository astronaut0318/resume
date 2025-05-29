<script setup>
import { onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../../stores/notification'
import { Bell } from '@element-plus/icons-vue'

const router = useRouter()
const notificationStore = useNotificationStore()

// 导航到通知中心
const toNotificationCenter = () => {
  router.push('/notifications')
}

// 获取未读通知数量
const fetchUnreadCount = async () => {
  try {
    await notificationStore.getUnreadCount()
  } catch (error) {
    console.error('获取未读通知数量失败', error)
  }
}

// 定时刷新未读通知数量
let refreshInterval = null

onMounted(() => {
  // 初始化获取未读通知数
  fetchUnreadCount()
  
  // 设置定时刷新
  refreshInterval = setInterval(fetchUnreadCount, 30000) // 每30秒刷新一次
})

// 组件销毁时清除定时器
onBeforeUnmount(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>

<template>
  <div class="notification-icon" @click="toNotificationCenter">
    <el-badge :value="notificationStore.unreadCount" :hidden="!notificationStore.hasUnread" :max="99">
      <el-icon class="notification-bell">
        <Bell />
      </el-icon>
    </el-badge>
  </div>
</template>

<style scoped>
.notification-icon {
  margin: 0 15px;
  position: relative;
  cursor: pointer;
}

.notification-bell {
  font-size: 20px;
  color: #606266;
  transition: color 0.3s;
}

.notification-icon:hover .notification-bell {
  color: #1890ff;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .notification-icon {
    margin: 0 10px;
  }
  
  .notification-bell {
    font-size: 18px;
  }
}
</style> 