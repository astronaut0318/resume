import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/', // 在开发环境中，会被代理到后端服务
  timeout: 15000 // 延长超时时间到15秒
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 可以在这里添加 token 等认证信息
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    // 自动加 userId
    const userId = localStorage.getItem('userId')
    if (userId) {
      config.headers['userId'] = userId
    }

    // 对GET请求添加时间戳，防止缓存
    if (config.method.toLowerCase() === 'get') {
      config.params = config.params || {}
      config.params._t = Date.now() // 添加时间戳参数
    }
    
    // 调试信息：记录请求信息
    console.log(`请求: ${config.method.toUpperCase()} ${config.url}`, config.data || config.params)
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 兼容文件流下载（blob）
    if (
      response.config.responseType === 'blob' ||
      response.data instanceof Blob
    ) {
      return response
    }
    
    const res = response.data
    
    // 记录原始响应便于调试
    console.log('API响应:', {
      url: response.config.url,
      method: response.config.method,
      status: response.status,
      data: res
    })
    
    // 返回后端的标准响应格式
    if (res && res.code !== undefined) {
      // 这是标准的后端响应格式 {code, message, data}
      if (res.code === 200) {
        return res // 成功直接返回响应对象
      }
      
      // 不显示通知相关接口的错误提示，避免干扰用户体验
      const isNotificationRequest = response.config.url?.includes('/notifications')
      if (!isNotificationRequest) {
        ElMessage.error(res.message || '请求失败')
      } else {
        console.warn('通知请求失败:', res.message)
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    // 非标准响应或直接返回数据的情况，包装成统一格式
    return {
      code: 200,
      message: '操作成功',
      data: res
    }
  },
  error => {
    console.error('响应错误:', error)
    console.log('响应详情:', error.response?.data || '无响应体')
    
    // 获取请求的URL信息，用于针对特定接口的错误处理
    const requestUrl = error.config?.url || ''
    const isOrderPayRequest = requestUrl.includes('/orders/') && requestUrl.includes('/pay')
    const isNotificationRequest = requestUrl.includes('/notifications')
    
    // 处理500错误，提供更友好的错误提示
    if (error.response && error.response.status === 500) {
      let errorMsg = '服务器内部错误'
      
      if (isOrderPayRequest) {
        errorMsg = '支付处理失败，请稍后再试或联系客服'
      } else if (requestUrl.includes('/orders')) {
        errorMsg = '订单处理异常，请稍后再试'
      } else {
        errorMsg = '服务器内部错误，请稍后再试或联系管理员'
      }
      
      if (!isNotificationRequest) {
        ElMessage.error(errorMsg)
      } else {
        console.warn('通知请求失败:', errorMsg)
      }
      
      return Promise.reject(error)
    }
    
    // 处理404错误
    if (error.response && error.response.status === 404) {
      let errorMsg = '请求的资源不存在'
      
      if (isOrderPayRequest) {
        errorMsg = '支付接口未找到，请确认订单号是否正确'
      }
      
      if (!isNotificationRequest) {
        ElMessage.error(errorMsg)
      } else {
        console.warn('通知请求失败:', errorMsg)
      }
      
      return Promise.reject(error)
    }
    
    // 如果为通知相关请求的错误，不显示错误消息
    if (!isNotificationRequest) {
      // 更详细的错误信息
      const errorMsg = error.response?.data?.message || error.message || '请求失败'
      ElMessage.error(errorMsg)
    } else {
      console.warn('通知请求失败:', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default service 