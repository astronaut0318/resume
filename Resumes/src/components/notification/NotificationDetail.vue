<script setup>
import { ref, watch } from 'vue'
import { useNotificationStore } from '../../stores/notification'
import { ElMessage } from 'element-plus'

const props = defineProps({
  notificationId: {
    type: [Number, String],
    required: true
  }
})

const notificationStore = useNotificationStore()
const notification = ref(null)
const loading = ref(false)

// 获取通知详情
const fetchNotificationDetail = async () => {
  try {
    loading.value = true
    notification.value = await notificationStore.getNotificationDetail(Number(props.notificationId))
  } catch (error) {
    ElMessage.error('获取通知详情失败')
  } finally {
    loading.value = false
  }
}

// 监听ID变化，重新获取详情
watch(() => props.notificationId, () => {
  fetchNotificationDetail()
}, { immediate: true })

// 删除通知
const handleDelete = async () => {
  try {
    await notificationStore.deleteNotification(props.notificationId)
    ElMessage.success('删除成功')
    // 触发删除事件
    emit('deleted')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 定义事件
const emit = defineEmits(['deleted'])
</script>

<template>
  <div class="notification-detail">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="skeleton-wrapper">
          <el-skeleton-item variant="p" style="width: 50%" />
          <el-skeleton-item variant="text" style="margin-top: 15px; width: 100%" />
          <el-skeleton-item variant="text" style="width: 100%" />
          <el-skeleton-item variant="text" style="width: 60%" />
        </div>
      </template>
      
      <template #default>
        <template v-if="notification">
          <div class="detail-header">
            <h3 class="detail-title">{{ notification.title }}</h3>
            <div class="detail-meta">
              <span class="detail-time">{{ notification.createTime }}</span>
              <el-tag 
                size="small" 
                :type="notification.type === 1 ? 'info' : notification.type === 2 ? 'success' : 'warning'"
              >
                {{ notification.type === 1 ? '系统通知' : notification.type === 2 ? '订单通知' : '其他通知' }}
              </el-tag>
            </div>
          </div>
          
          <div class="detail-content">
            {{ notification.content }}
          </div>
          
          <div class="detail-actions">
            <el-button type="danger" plain size="small" @click="handleDelete">删除通知</el-button>
          </div>
        </template>
        <el-empty v-else description="通知不存在或已被删除" />
      </template>
    </el-skeleton>
  </div>
</template>

<style scoped>
.notification-detail {
  padding: 20px;
}

.skeleton-wrapper {
  padding: 20px;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px 0;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-time {
  font-size: 14px;
  color: #909399;
}

.detail-content {
  font-size: 15px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 30px;
  white-space: pre-wrap;
}

.detail-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .notification-detail {
    padding: 15px;
  }
  
  .detail-title {
    font-size: 16px;
  }
  
  .detail-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style> 