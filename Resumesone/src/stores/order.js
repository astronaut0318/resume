import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { createOrder, getOrderList, getOrderDetail, cancelOrder } from '../api/order'
import { ElMessage } from 'element-plus'
import { useNotificationStore } from './notification'

export const useOrderStore = defineStore('order', () => {
  // 状态
  const orderList = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const currentStatus = ref(null)

  // 计算属性
  const hasOrders = computed(() => orderList.value.length > 0)
  
  // 获取订单列表
  const fetchOrderList = async () => {
    loading.value = true
    try {
      const params = {
        page: currentPage.value,
        size: pageSize.value
      }
      
      if (currentStatus.value !== null) {
        params.status = currentStatus.value
      }
      
      const res = await getOrderList(params)
      if (res.code === 200) {
        orderList.value = res.data.list
        total.value = res.data.total
      }
    } catch (error) {
      console.error('获取订单列表失败', error)
      ElMessage.error('获取订单列表失败')
    } finally {
      loading.value = false
    }
  }
  
  // 创建订单
  const createNewOrder = async (data) => {
    loading.value = true
    try {
      // 确保数据格式正确
      const orderData = {
        templateId: Number(data.templateId),
        payType: Number(data.payType || 1)
      }
      
      console.log('订单Store: 创建订单', orderData)
      
      const res = await createOrder(orderData)
      if (res.code === 200) {
        ElMessage.success('订单创建成功')
        
        // 创建订单成功通知
        const notificationStore = useNotificationStore()
        notificationStore.receiveNewNotification({
          id: Date.now(),
          title: '订单创建成功',
          content: `您的订单 ${res.data.orderNo} 已创建成功，请尽快完成支付。`,
          type: 2, // 订单通知
          isRead: 0,
          createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
        })
        
        return res.data
      } else {
        ElMessage.error(res.message || '订单创建失败')
        return null
      }
    } catch (error) {
      console.error('创建订单失败', error)
      ElMessage.error(error.message || '创建订单失败')
      return null
    } finally {
      loading.value = false
    }
  }
  
  // 获取订单详情
  const fetchOrderDetail = async (orderNo) => {
    loading.value = true
    try {
      const res = await getOrderDetail(orderNo)
      if (res.code === 200) {
        currentOrder.value = res.data
        return res.data
      } else {
        ElMessage.error(res.message || '获取订单详情失败')
        return null
      }
    } catch (error) {
      console.error('获取订单详情失败', error)
      ElMessage.error('获取订单详情失败')
      return null
    } finally {
      loading.value = false
    }
  }
  
  // 取消订单
  const cancelCurrentOrder = async (orderNo) => {
    loading.value = true
    try {
      const res = await cancelOrder(orderNo)
      if (res.code === 200) {
        ElMessage.success('订单取消成功')
        
        // 订单取消通知
        const notificationStore = useNotificationStore()
        notificationStore.receiveNewNotification({
          id: Date.now(),
          title: '订单已取消',
          content: `您的订单 ${orderNo} 已成功取消。`,
          type: 2, // 订单通知
          isRead: 0,
          createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
        })
        
        // 刷新订单列表
        await fetchOrderList()
        return true
      } else {
        ElMessage.error(res.message || '订单取消失败')
        return false
      }
    } catch (error) {
      console.error('取消订单失败', error)
      ElMessage.error('取消订单失败')
      return false
    } finally {
      loading.value = false
    }
  }
  
  // 处理订单支付成功
  const handlePaymentSuccess = (orderData) => {
    // 发送支付成功通知
    const notificationStore = useNotificationStore()
    
    // 根据订单类型发送不同内容的通知
    let notificationContent = ''
    if (orderData.orderType === 1) {
      notificationContent = `您购买的模板已支付成功，订单号: ${orderData.orderNo}，您现在可以使用该模板创建简历了。`
    } else if (orderData.orderType === 2) {
      notificationContent = `您的VIP会员订单已支付成功，订单号: ${orderData.orderNo}，感谢您的支持！现在您可以享受VIP会员的所有特权了。`
    } else {
      notificationContent = `您的订单 ${orderData.orderNo} 已支付成功，感谢您的支持！`
    }
    
    notificationStore.receiveNewNotification({
      id: Date.now(),
      title: '订单支付成功',
      content: notificationContent,
      type: 2, // 订单通知
      isRead: 0,
      createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
    })
    
    // 刷新订单列表
    fetchOrderList()
  }
  
  // 设置筛选状态
  const setStatus = (status) => {
    currentStatus.value = status
    currentPage.value = 1 // 重置页码
    fetchOrderList()
  }
  
  // 设置页码
  const setPage = (page) => {
    currentPage.value = page
    fetchOrderList()
  }
  
  return {
    orderList,
    currentOrder,
    loading,
    total,
    currentPage,
    pageSize,
    currentStatus,
    hasOrders,
    fetchOrderList,
    createNewOrder,
    fetchOrderDetail,
    cancelCurrentOrder,
    handlePaymentSuccess,
    setStatus,
    setPage
  }
}) 