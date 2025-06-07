<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../../stores/notification'
import { ElMessage } from 'element-plus'
import { InfoFilled, ShoppingCart, Message } from '@element-plus/icons-vue'

const props = defineProps({
  limit: {
    type: Number,
    default: 5
  }
})

const router = useRouter()
const notificationStore = useNotificationStore()
const visible = ref(false)
const loading = ref(false)

// 计算属性：获取最近的几条通知
const recentNotifications = computed(() => {
  return notificationStore.notifications.slice(0, props.limit)
})

// 获取通知列表
const fetchNotifications = async () => {
  try {
    loading.value = true
    // 每次显示弹窗时，强制从后端重新获取最新通知
    await notificationStore.getNotifications({ page: 1, size: props.limit })
    // 同时刷新未读数量
    await notificationStore.getUnreadCount()
  } catch (error) {
    console.error('获取通知列表失败', error)
  } finally {
    loading.value = false
  }
}

// 查看通知详情
const viewNotification = (id) => {
  visible.value = false
  router.push(`/notifications/${id}`)
}

// 标记通知为已读
const markAsRead = async (id) => {
  try {
    await notificationStore.markAsRead(id)
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 标记所有为已读
const markAllAsRead = async () => {
  try {
    await notificationStore.markAllAsRead()
    ElMessage.success('已将所有通知标记为已读')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 查看所有通知
const viewAllNotifications = () => {
  visible.value = false
  router.push('/notifications')
}

// 弹出框显示时获取最新通知
const handleShow = () => {
  fetchNotifications()
}

onMounted(() => {
  // 初始化获取未读通知数
  notificationStore.getUnreadCount()
})
</script>

<template>
  <el-popover
    v-model:visible="visible"
    placement="bottom-end"
    :width="320"
    trigger="click"
    @show="handleShow"
  >
    <template #reference>
      <slot></slot>
    </template>
    
    <div class="notification-popover">
      <div class="popover-header">
        <h3 class="popover-title">
          通知
          <el-badge v-if="notificationStore.unreadCount > 0" :value="notificationStore.unreadCount" :max="99" />
        </h3>
        
        <el-button 
          v-if="notificationStore.unreadCount > 0"
          type="primary" 
          link 
          @click="markAllAsRead"
        >
          全部已读
        </el-button>
      </div>
      
      <div class="popover-content">
        <el-empty v-if="recentNotifications.length === 0" description="暂无通知" />
        
        <div v-else class="notification-list">
          <div 
            v-for="item in recentNotifications" 
            :key="item.id" 
            class="notification-item"
            :class="{ 'unread': item.isRead === 0 }"
            @click="viewNotification(item.id)"
          >
            <div class="notification-icon">
              <!-- 根据通知类型显示不同图标 -->
              <el-icon v-if="item.type === 1" class="system-icon"><InfoFilled /></el-icon>
              <el-icon v-else-if="item.type === 2" class="order-icon"><ShoppingCart /></el-icon>
              <el-icon v-else class="other-icon"><Message /></el-icon>
            </div>
            
            <div class="notification-content">
              <div class="notification-title">
                {{ item.title }}
                <span v-if="item.isRead === 0" class="unread-dot"></span>
              </div>
              <div class="notification-brief">{{ item.content }}</div>
              <div class="notification-time">{{ item.createTime }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="popover-footer">
        <el-button type="primary" link @click="viewAllNotifications">查看全部</el-button>
      </div>
    </div>
  </el-popover>
</template>

<style scoped>
.notification-popover {
  max-height: 400px;
  display: flex;
  flex-direction: column;
}

.popover-header {
  padding: 10px 15px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.popover-title {
  font-size: 16px;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.popover-content {
  flex: 1;
  overflow-y: auto;
  max-height: 300px;
  padding: 10px 0;
}

.notification-list {
  padding: 0;
}

.notification-item {
  padding: 12px 15px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #f0f9ff;
}

.notification-icon {
  margin-right: 12px;
  margin-top: 3px;
}

.system-icon {
  color: #409eff;
}

.order-icon {
  color: #67c23a;
}

.other-icon {
  color: #e6a23c;
}

.notification-content {
  flex: 1;
  overflow: hidden;
}

.notification-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 5px;
  color: #303133;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #f56c6c;
  display: inline-block;
}

.notification-brief {
  font-size: 13px;
  color: #606266;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.popover-footer {
  padding: 10px 15px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}
</style> 