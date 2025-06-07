<script setup>
import { onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../../stores/notification'
import { Bell } from '@element-plus/icons-vue'
import NotificationPopover from './NotificationPopover.vue'

const router = useRouter()
const notificationStore = useNotificationStore()

// 导航到通知中心
const toNotificationCenter = () => {
  router.push('/notifications')
}

// 获取未读通知数量和通知列表
const fetchNotificationData = async () => {
  try {
    await notificationStore.getUnreadCount()
    // 当通知数为空时初始化加载几条通知
    if (notificationStore.notifications.length === 0) {
      await notificationStore.getNotifications({ page: 1, size: 5 })
    }
  } catch (error) {
    console.error('获取通知数据失败', error)
  }
}

// 定时刷新通知数据
let refreshInterval = null

onMounted(() => {
  // 初始化获取通知数据
  fetchNotificationData()
  
  // 设置定时刷新，减少到15秒刷新一次，确保及时获取最新通知
  refreshInterval = setInterval(fetchNotificationData, 15000)
})

// 组件销毁时清除定时器
onBeforeUnmount(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>

<template>
  <div class="notification-icon">
    <NotificationPopover>
      <el-badge :value="notificationStore.unreadCount" :hidden="!notificationStore.hasUnread" :max="99">
        <el-icon class="notification-bell">
          <Bell />
        </el-icon>
      </el-badge>
    </NotificationPopover>
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