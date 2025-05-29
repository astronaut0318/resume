<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../../stores/notification'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, View, Check, InfoFilled, ShoppingCart, Message } from '@element-plus/icons-vue'

const props = defineProps({
  showFilter: {
    type: Boolean,
    default: true
  },
  limit: {
    type: Number,
    default: 0 // 0 表示不限制
  }
})

const router = useRouter()
const notificationStore = useNotificationStore()

// 筛选条件
const filterType = ref(null)
const filterRead = ref(null)

// 当前显示的通知列表
const displayNotifications = computed(() => {
  let list = [...notificationStore.notifications]
  
  // 如果有限制，则只显示指定数量
  if (props.limit > 0) {
    list = list.slice(0, props.limit)
  }
  
  return list
})

// 加载通知列表
const loadNotifications = async (reset = false) => {
  if (reset) {
    notificationStore.currentPage = 1
  }
  
  const params = {}
  if (filterType.value !== null) {
    params.type = filterType.value
  }
  if (filterRead.value !== null) {
    params.isRead = filterRead.value
  }
  
  await notificationStore.getNotifications(params)
}

// 监听筛选条件变化
watch([filterType, filterRead], () => {
  loadNotifications(true)
}, { deep: true })

// 查看通知详情
const viewNotification = (id) => {
  router.push(`/notifications/${id}`)
}

// 标记为已读
const markAsRead = async (id) => {
  try {
    await notificationStore.markAsRead(id)
    ElMessage.success('已标记为已读')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除通知
const deleteNotification = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该通知？', '提示', {
      type: 'warning'
    })
    await notificationStore.deleteNotification(id)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 标记所有为已读
const markAllAsRead = async () => {
  try {
    await notificationStore.markAllAsRead()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 加载更多
const loadMore = async () => {
  if (notificationStore.hasMore && !notificationStore.loading) {
    await notificationStore.getNotifications({
      page: notificationStore.currentPage + 1,
      type: filterType.value !== null ? filterType.value : undefined,
      isRead: filterRead.value !== null ? filterRead.value : undefined
    })
  }
}

// 初始化加载
loadNotifications()
</script>

<template>
  <div class="notification-list">
    <!-- 筛选栏 -->
    <div v-if="showFilter" class="filter-bar">
      <div class="filter-group">
        <el-select v-model="filterType" placeholder="通知类型" clearable>
          <el-option label="系统通知" :value="1" />
          <el-option label="订单通知" :value="2" />
          <el-option label="其他通知" :value="3" />
        </el-select>
        
        <el-select v-model="filterRead" placeholder="阅读状态" clearable>
          <el-option label="未读" :value="0" />
          <el-option label="已读" :value="1" />
        </el-select>
      </div>
      
      <el-button 
        v-if="notificationStore.unreadCount > 0"
        type="primary" 
        plain 
        size="small" 
        @click="markAllAsRead"
      >
        全部标为已读
      </el-button>
    </div>
    
    <!-- 通知列表 -->
    <div class="list-content">
      <el-empty v-if="displayNotifications.length === 0" description="暂无通知" />
      
      <el-scrollbar v-else height="100%">
        <div class="notification-items">
          <div 
            v-for="item in displayNotifications" 
            :key="item.id" 
            class="notification-item"
            :class="{ 'unread': item.isRead === 0 }"
          >
            <div class="notification-icon">
              <!-- 根据通知类型显示不同图标 -->
              <el-icon v-if="item.type === 1" class="system-icon"><InfoFilled /></el-icon>
              <el-icon v-else-if="item.type === 2" class="order-icon"><ShoppingCart /></el-icon>
              <el-icon v-else class="other-icon"><Message /></el-icon>
            </div>
            
            <div class="notification-content" @click="viewNotification(item.id)">
              <div class="notification-title">
                {{ item.title }}
                <span v-if="item.isRead === 0" class="unread-dot"></span>
              </div>
              <div class="notification-brief">{{ item.content }}</div>
              <div class="notification-time">{{ item.createTime }}</div>
            </div>
            
            <div class="notification-actions">
              <el-tooltip content="查看详情" placement="top">
                <el-button circle size="small" @click="viewNotification(item.id)">
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              
              <el-tooltip v-if="item.isRead === 0" content="标记已读" placement="top">
                <el-button circle size="small" @click="markAsRead(item.id)">
                  <el-icon><Check /></el-icon>
                </el-button>
              </el-tooltip>
              
              <el-tooltip content="删除通知" placement="top">
                <el-button circle size="small" @click.stop="deleteNotification(item.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </div>
          
          <!-- 加载更多 -->
          <div v-if="notificationStore.hasMore && !limit" class="load-more">
            <el-button 
              type="primary" 
              plain 
              size="small" 
              :loading="notificationStore.loading"
              @click="loadMore"
            >
              加载更多
            </el-button>
          </div>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<style scoped>
.notification-list {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.filter-bar {
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-group {
  display: flex;
  gap: 10px;
}

.list-content {
  flex: 1;
  overflow: hidden;
}

.notification-items {
  padding: 0 15px;
}

.notification-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: flex-start;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #f0f9ff;
}

.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
}

.system-icon {
  color: #409eff;
}

.order-icon {
  color: #67c23a;
}

.other-icon {
  color: #909399;
}

.notification-content {
  flex: 1;
  cursor: pointer;
}

.notification-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #f56c6c;
  margin-left: 8px;
}

.notification-brief {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-actions {
  display: flex;
  gap: 5px;
  margin-left: 10px;
}

.load-more {
  padding: 15px 0;
  display: flex;
  justify-content: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .filter-group {
    width: 100%;
  }
  
  .notification-item {
    flex-direction: column;
  }
  
  .notification-icon {
    margin-bottom: 10px;
  }
  
  .notification-actions {
    margin-left: 0;
    margin-top: 10px;
  }
}
</style> 