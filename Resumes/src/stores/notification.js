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
      
      try {
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
      } catch (apiError) {
        console.error('API获取通知列表失败:', apiError)
        
        // 如果API调用失败(405错误)，从localStorage获取临时通知
        if (apiError.response && apiError.response.status === 405) {
          const tempNotificationsStr = localStorage.getItem('temp_notifications') || '[]'
          const tempNotifications = JSON.parse(tempNotificationsStr)
          
          // 简单实现分页
          const startIndex = (page - 1) * size
          const endIndex = startIndex + size
          const pageNotifications = tempNotifications.slice(startIndex, endIndex)
          
          if (page === 1) {
            notifications.value = pageNotifications
          } else {
            notifications.value = [...notifications.value, ...pageNotifications]
          }
          
          totalCount.value = tempNotifications.length
          currentPage.value = page
          
          return {
            list: pageNotifications,
            total: tempNotifications.length
          }
        }
        
        // 其他错误则继续抛出
        return Promise.reject(apiError)
      }
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
      } catch (apiError) {
        console.error('API标记通知为已读失败:', apiError)
        
        // 如果是API不可用错误，处理临时通知
        if (apiError.response && apiError.response.status === 405) {
          // 获取并更新localStorage中的临时通知
          const tempNotificationsStr = localStorage.getItem('temp_notifications') || '[]'
          const tempNotifications = JSON.parse(tempNotificationsStr)
          
          const tempIndex = tempNotifications.findIndex(item => item.id === id)
          if (tempIndex !== -1) {
            // 标记为已读
            tempNotifications[tempIndex].isRead = 1
            localStorage.setItem('temp_notifications', JSON.stringify(tempNotifications))
            
            // 更新本地通知状态
            const storeIndex = notifications.value.findIndex(item => item.id === id)
            if (storeIndex !== -1) {
              notifications.value[storeIndex].isRead = 1
            }
            
            // 更新未读计数
            const unreadCountStr = localStorage.getItem('temp_unread_count') || '0'
            const tempUnreadCount = Math.max(0, parseInt(unreadCountStr, 10) - 1)
            localStorage.setItem('temp_unread_count', tempUnreadCount.toString())
            unreadCount.value = tempUnreadCount
            
            return true
          }
        }
        
        // 其他错误则继续抛出
        return Promise.reject(apiError)
      }
    } catch (error) {
      console.error('标记通知为已读失败:', error)
      return Promise.reject(error)
    }
  }

  // 标记所有通知为已读
  const markAllAsRead = async () => {
    try {
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
      } catch (apiError) {
        console.error('API标记所有通知为已读失败:', apiError)
        
        // 如果是API不可用错误，处理临时通知
        if (apiError.response && apiError.response.status === 405) {
          // 获取并更新localStorage中的临时通知
          const tempNotificationsStr = localStorage.getItem('temp_notifications') || '[]'
          const tempNotifications = JSON.parse(tempNotificationsStr)
          
          // 所有通知标记为已读
          tempNotifications.forEach(item => {
            item.isRead = 1
          })
          localStorage.setItem('temp_notifications', JSON.stringify(tempNotifications))
          
          // 更新本地通知状态
          notifications.value.forEach(item => {
            item.isRead = 1
          })
          
          // 更新未读计数
          localStorage.setItem('temp_unread_count', '0')
          unreadCount.value = 0
          
          ElMessage.success('所有通知已标记为已读')
          return true
        }
        
        // 其他错误则继续抛出
        return Promise.reject(apiError)
      }
    } catch (error) {
      console.error('标记所有通知为已读失败:', error)
      return Promise.reject(error)
    }
  }

  // 删除通知
  const deleteNotification = async (id) => {
    try {
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
      } catch (apiError) {
        console.error('API删除通知失败:', apiError)
        
        // 如果是API不可用错误，处理临时通知
        if (apiError.response && apiError.response.status === 405) {
          // 获取并更新localStorage中的临时通知
          const tempNotificationsStr = localStorage.getItem('temp_notifications') || '[]'
          const tempNotifications = JSON.parse(tempNotificationsStr)
          
          const tempIndex = tempNotifications.findIndex(item => item.id === id)
          if (tempIndex !== -1) {
            // 如果是未读通知，更新未读计数
            if (tempNotifications[tempIndex].isRead === 0) {
              const unreadCountStr = localStorage.getItem('temp_unread_count') || '0'
              const tempUnreadCount = Math.max(0, parseInt(unreadCountStr, 10) - 1)
              localStorage.setItem('temp_unread_count', tempUnreadCount.toString())
              unreadCount.value = tempUnreadCount
            }
            
            // 从临时存储中删除
            tempNotifications.splice(tempIndex, 1)
            localStorage.setItem('temp_notifications', JSON.stringify(tempNotifications))
            
            // 更新本地通知列表
            const storeIndex = notifications.value.findIndex(item => item.id === id)
            if (storeIndex !== -1) {
              notifications.value.splice(storeIndex, 1)
              totalCount.value--
            }
            
            ElMessage.success('删除成功')
            return true
          }
        }
        
        // 其他错误则继续抛出
        return Promise.reject(apiError)
      }
    } catch (error) {
      console.error('删除通知失败:', error)
      return Promise.reject(error)
    }
  }

  // 获取未读通知数量
  const getUnreadCount = async () => {
    try {
      // 先尝试API请求
      const res = await notificationApi.getUnreadCount()
      if (res.code === 200) {
        unreadCount.value = res.data.count
        return res.data.count
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('获取未读通知数量失败:', error)
      
      // 如果API失败，尝试从localStorage获取临时未读计数
      if (error.response && error.response.status === 405) {
        const tempUnreadCountStr = localStorage.getItem('temp_unread_count') || '0'
        const tempUnreadCount = parseInt(tempUnreadCountStr, 10)
        unreadCount.value = tempUnreadCount
        return tempUnreadCount
      }
      
      return Promise.reject(error)
    }
  }

  // 创建新通知
  const createNotification = async (notificationData) => {
    try {
      const data = {
        title: notificationData.title,
        content: notificationData.content,
        type: notificationData.type || 2 // 默认为订单通知
      }
      
      // 如果提供了用户ID，则添加到请求数据中
      if (notificationData.userId) {
        data.userId = notificationData.userId
      }
      
      try {
        // 尝试调用后端API
        const res = await notificationApi.createNotification(data)
        
        if (res.code === 200) {
          // 创建成功后刷新通知列表和未读数量
          await refreshNotifications()
          return res.data
        }
        
        return Promise.reject(res)
      } catch (apiError) {
        console.error('通知API请求失败:', apiError)
        
        // 如果API不可用（返回405错误），使用本地存储模拟通知功能
        if (apiError.response && apiError.response.status === 405) {
          // 创建临时通知并存储在localStorage中
          const tempNotification = {
            id: Date.now(), // 使用时间戳作为临时ID
            title: data.title,
            content: data.content,
            type: data.type,
            isRead: 0,
            createTime: new Date().toISOString()
          }
          
          // 获取现有临时通知
          const tempNotificationsStr = localStorage.getItem('temp_notifications') || '[]'
          const tempNotifications = JSON.parse(tempNotificationsStr)
          
          // 添加新通知
          tempNotifications.unshift(tempNotification)
          
          // 最多保存20条临时通知
          if (tempNotifications.length > 20) {
            tempNotifications.length = 20
          }
          
          // 保存回localStorage
          localStorage.setItem('temp_notifications', JSON.stringify(tempNotifications))
          
          // 更新本地存储的未读计数
          const unreadCountStr = localStorage.getItem('temp_unread_count') || '0'
          const unreadCount = parseInt(unreadCountStr, 10) + 1
          localStorage.setItem('temp_unread_count', unreadCount.toString())
          
          // 使用BrowserEvent API发送事件以更新通知图标
          const event = new CustomEvent('notification_created', { 
            detail: { notification: tempNotification } 
          })
          window.dispatchEvent(event)
          
          console.log('已创建临时通知:', tempNotification)
          return tempNotification
        }
        
        // 其他错误则继续抛出
        return Promise.reject(apiError)
      }
    } catch (error) {
      console.error('创建通知失败:', error)
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
      // 即使失败，上面的方法也会尝试从localStorage获取临时通知
    }
    
    // 添加一个事件监听器以接收新通知
    window.addEventListener('notification_created', (event) => {
      if (event.detail && event.detail.notification) {
        // 如果当前是第一页，添加到通知列表顶部
        if (currentPage.value === 1) {
          notifications.value.unshift(event.detail.notification)
        }
        // 更新未读计数
        unreadCount.value += 1
      }
    })
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
      // 即使失败，上面的方法也会尝试从localStorage获取临时通知
    }
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
    createNotification,
    initNotifications,
    refreshNotifications
  }
}) 