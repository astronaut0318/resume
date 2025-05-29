import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as notificationApi from '../api/notification'
import { ElMessage } from 'element-plus'

export const useNotificationStore = defineStore('notification', () => {
  // 状态
  const notifications = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const totalCount = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)

  // 计算属性
  const hasUnread = computed(() => unreadCount.value > 0)
  const hasMore = computed(() => totalCount.value > notifications.value.length)

  // 方法
  // 获取通知列表
  const getNotifications = async (params = {}) => {
    try {
      loading.value = true
      const { page = currentPage.value, size = pageSize.value } = params
      
      const res = await notificationApi.getNotifications({
        page,
        size,
        ...params
      })
      
      if (res.code === 200) {
        if (page === 1) {
          notifications.value = res.data.list
        } else {
          notifications.value = [...notifications.value, ...res.data.list]
        }
        
        totalCount.value = res.data.total
        currentPage.value = page
        return res.data
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('获取通知列表失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }

  // 获取通知详情
  const getNotificationDetail = async (id) => {
    try {
      const res = await notificationApi.getNotificationDetail(id)
      if (res.code === 200) {
        // 如果通知是未读状态，则标记为已读并更新计数
        if (res.data.isRead === 0) {
          await markAsRead(id)
        }
        return res.data
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('获取通知详情失败:', error)
      return Promise.reject(error)
    }
  }

  // 标记通知为已读
  const markAsRead = async (id) => {
    try {
      const res = await notificationApi.markAsRead(id)
      if (res.code === 200) {
        // 更新本地通知状态
        const index = notifications.value.findIndex(item => item.id === id)
        if (index !== -1) {
          notifications.value[index].isRead = 1
        }
        // 更新未读计数
        getUnreadCount()
        return true
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('标记通知为已读失败:', error)
      return Promise.reject(error)
    }
  }

  // 标记所有通知为已读
  const markAllAsRead = async () => {
    try {
      const res = await notificationApi.markAllAsRead()
      if (res.code === 200) {
        // 更新本地通知状态
        notifications.value.forEach(item => {
          item.isRead = 1
        })
        // 更新未读计数
        unreadCount.value = 0
        ElMessage.success('所有通知已标记为已读')
        return true
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('标记所有通知为已读失败:', error)
      return Promise.reject(error)
    }
  }

  // 删除通知
  const deleteNotification = async (id) => {
    try {
      const res = await notificationApi.deleteNotification(id)
      if (res.code === 200) {
        // 更新本地通知列表
        const index = notifications.value.findIndex(item => item.id === id)
        if (index !== -1) {
          // 如果是未读通知，更新未读计数
          if (notifications.value[index].isRead === 0) {
            unreadCount.value = Math.max(0, unreadCount.value - 1)
          }
          notifications.value.splice(index, 1)
          totalCount.value--
        }
        ElMessage.success('删除成功')
        return true
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('删除通知失败:', error)
      return Promise.reject(error)
    }
  }

  // 获取未读通知数量
  const getUnreadCount = async () => {
    try {
      const res = await notificationApi.getUnreadCount()
      if (res.code === 200) {
        unreadCount.value = res.data.count
        return res.data.count
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('获取未读通知数量失败:', error)
      return Promise.reject(error)
    }
  }

  // 初始化加载
  const initNotifications = async () => {
    try {
      await Promise.all([
        getNotifications({ page: 1, size: pageSize.value }),
        getUnreadCount()
      ])
    } catch (error) {
      console.error('初始化通知失败:', error)
    }
  }

  // 刷新通知
  const refreshNotifications = async () => {
    try {
      await Promise.all([
        getNotifications({ page: 1, size: notifications.value.length || pageSize.value }),
        getUnreadCount()
      ])
    } catch (error) {
      console.error('刷新通知失败:', error)
    }
  }

  // 模拟接收新通知
  const receiveNewNotification = (notification) => {
    // 在列表前面添加新通知
    notifications.value.unshift(notification)
    totalCount.value++
    
    // 如果是未读通知，更新未读计数
    if (notification.isRead === 0) {
      unreadCount.value++
    }
    
    // 显示通知提醒
    ElMessage({
      message: notification.title,
      type: 'info',
      duration: 3000
    })
  }

  return {
    // 状态
    notifications,
    unreadCount,
    loading,
    totalCount,
    currentPage,
    pageSize,
    
    // 计算属性
    hasUnread,
    hasMore,
    
    // 方法
    getNotifications,
    getNotificationDetail,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    getUnreadCount,
    initNotifications,
    refreshNotifications,
    receiveNewNotification
  }
}) 