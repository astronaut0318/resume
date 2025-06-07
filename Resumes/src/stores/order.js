import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { createOrder, getOrderList, getOrderDetail, cancelOrder, payOrder } from '../api/order'
import { ElMessage } from 'element-plus'
import { useNotificationStore } from './notification'
import * as notificationApi from '../api/notification'

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
  const hasOrders = computed(() => {
    // 防止orderList.value为undefined
    return orderList.value && orderList.value.length > 0
  })
  
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
      if (res && res.code === 200) {
        // 处理不同的响应数据结构
        if (res.data && res.data.records) {
          // 处理分页数据格式 {records: [...], total: 100}
          orderList.value = res.data.records || []
          total.value = res.data.total || 0
        } else if (res.data && res.data.list) {
          // 处理 {list: [...], total: 100} 格式
          orderList.value = res.data.list || []
          total.value = res.data.total || 0
        } else if (Array.isArray(res.data)) {
          // 直接返回数组的格式
          orderList.value = res.data
          total.value = res.data.length
        } else {
          // 未知格式或空数据，设为空数组
          orderList.value = []
          total.value = 0
        }
      } else {
        // 服务返回错误或无数据
        orderList.value = []
        total.value = 0
      }
    } catch (error) {
      console.error('获取订单列表失败', error)
      ElMessage.error('获取订单列表失败')
      // 确保设置为空数组
      orderList.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }
  
  // 发送订单相关通知
  const sendOrderNotification = async (title, content, type = 2) => {
    try {
      const notificationStore = useNotificationStore()
      await notificationStore.createNotification({
        title,
        content,
        type
      })
    } catch (error) {
      console.error('发送通知失败', error)
    }
  }
  
  // 创建订单
  const createNewOrder = async (data) => {
    loading.value = true
    try {
      // 验证模板ID
      if (!data.templateId || isNaN(Number(data.templateId))) {
        ElMessage.error('无效的模板ID')
        return null
      }

      // 确保数据格式正确
      const orderData = {
        templateId: Number(data.templateId),
        payType: Number(data.payType || 1)
      }
      
      console.log('订单Store: 创建订单', orderData)
      
      // 添加重试逻辑
      try {
        const res = await createOrder(orderData)
        if (res.code === 200) {
          ElMessage.success('订单创建成功')
          
          // 发送订单创建通知
          await sendOrderNotification(
            '订单创建成功',
            `您的订单 ${res.data.orderNo} 已创建成功，请尽快完成支付。`,
            2 // 订单通知
          )
          
          return res.data
        } else {
          ElMessage.error(res.message || '订单创建失败')
          return null
        }
      } catch (apiError) {
        // API错误重试逻辑
        console.error('API请求失败，尝试使用备用逻辑', apiError)

        // 如果后端返回模板不存在错误，尝试先获取模板信息
        if (apiError.response?.data?.message === '模板不存在') {
          ElMessage.warning('系统无法找到该模板信息，请刷新页面或选择其他模板')
          return null
        }
        throw apiError // 继续抛出错误以便统一处理
      }
    } catch (error) {
      console.error('创建订单失败', error)
      // 增强错误处理
      let errorMsg = '创建订单失败'
      if (error.response) {
        if (error.response.status === 500) {
          errorMsg = '服务器内部错误，创建订单失败'
        } else if (error.response.status === 400) {
          errorMsg = '参数错误，请检查模板ID是否正确'
        } else if (error.response.data && error.response.data.message) {
          errorMsg = error.response.data.message
        }
      } else if (error.message) {
        errorMsg = error.message
      }
      
      ElMessage.error(errorMsg)
      return null
    } finally {
      loading.value = false
    }
  }
  
  // 支付订单
  const payCurrentOrder = async (orderNo) => {
    loading.value = true
    try {
      console.log('订单Store: 支付订单', orderNo)
      
      // 调用支付API
      const res = await payOrder(orderNo)
      if (res.code === 200) {
        ElMessage.success('订单支付成功')
        
        // 发送订单支付成功通知
        await sendOrderNotification(
          '订单支付成功',
          `您的订单 ${orderNo} 已支付成功，感谢您的支持！`,
          2 // 订单通知
        )
        
        // 刷新订单列表
        await fetchOrderList()
        return true
      } else {
        ElMessage.error(res.message || '订单支付失败')
        return false
      }
    } catch (error) {
      console.error('支付订单失败', error)
      
      // 增强错误处理
      let errorMsg = '支付订单失败'
      if (error.response) {
        if (error.response.status === 500) {
          errorMsg = '服务器内部错误，支付处理失败'
        } else if (error.response.status === 404) {
          errorMsg = '支付接口未找到，请检查订单号是否正确'
        } else if (error.response.status === 400) {
          errorMsg = '支付参数错误，请检查订单信息'
        } else if (error.response.data && error.response.data.message) {
          errorMsg = error.response.data.message
        }
      } else if (error.message) {
        errorMsg = error.message
      }
      
      ElMessage.error(errorMsg)
      return false
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
        
        // 发送订单取消通知
        await sendOrderNotification(
          '订单已取消',
          `您的订单 ${orderNo} 已成功取消。`,
          2 // 订单通知
        )
        
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
  const handlePaymentSuccess = async (orderData) => {
    // 根据订单类型发送不同内容的通知
    let notificationContent = ''
    if (orderData.orderType === 1) {
      notificationContent = `您购买的模板已支付成功，订单号: ${orderData.orderNo}，您现在可以使用该模板创建简历了。`
    } else if (orderData.orderType === 2) {
      notificationContent = `您的VIP会员订单已支付成功，订单号: ${orderData.orderNo}，感谢您的支持！现在您可以享受VIP会员的所有特权了。`
    } else {
      notificationContent = `您的订单 ${orderData.orderNo} 已支付成功，感谢您的支持！`
    }

    // 发送订单支付成功通知
    await sendOrderNotification(
      '支付成功通知',
      notificationContent,
      2 // 订单通知
    )
    
    // 刷新订单列表
    fetchOrderList()
  }
  
  // 设置当前订单状态筛选
  const setStatus = (status) => {
    currentStatus.value = status
    currentPage.value = 1 // 重置到第一页
    fetchOrderList()
  }
  
  // 设置当前页
  const setPage = (page) => {
    currentPage.value = page
    fetchOrderList()
  }

  return {
    // 状态
    orderList,
    currentOrder,
    loading,
    total,
    currentPage,
    pageSize,
    currentStatus,
    
    // 计算属性
    hasOrders,
    
    // 方法
    fetchOrderList,
    createNewOrder,
    payCurrentOrder,
    fetchOrderDetail,
    cancelCurrentOrder,
    handlePaymentSuccess,
    setStatus,
    setPage
  }
}) 