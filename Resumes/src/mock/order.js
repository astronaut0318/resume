import Mock from 'mockjs'
const Random = Mock.Random
import { getTemplateById } from './templates'

// 订单列表
const orders = []

// 生成订单号
const generateOrderNo = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `${year}${month}${day}${random}${Date.now().toString().slice(-4)}`
}

// 创建订单
export function createOrderMock(options) {
  try {
    console.log('Mock: 创建订单请求', options)
    
    if (!options.body) {
      console.error('Mock: 创建订单失败 - 请求体为空')
      return {
        code: 400,
        message: '请求参数错误',
        data: null
      }
    }
    
    const { body } = options
    let requestBody
    
    try {
      // 尝试解析JSON
      requestBody = typeof body === 'string' ? JSON.parse(body) : body
      console.log('Mock: 解析后的请求体', requestBody)
    } catch (error) {
      console.error('Mock: 创建订单失败 - JSON解析错误', error, body)
      return {
        code: 400,
        message: '请求参数格式错误',
        data: null
      }
    }
    
    const { templateId, payType } = requestBody
    
    if (templateId === undefined || templateId === null) {
      console.error('Mock: 创建订单失败 - 缺少模板ID', requestBody)
      return {
        code: 400,
        message: '模板ID不能为空',
        data: null
      }
    }
    
    // 获取模板信息
    const template = getTemplateById(templateId)
    if (!template) {
      console.error(`Mock: 创建订单失败 - 模板不存在 ID: ${templateId}`)
      return {
        code: 400,
        message: '模板不存在',
        data: null
      }
    }
    
    const orderNo = generateOrderNo()
    const amount = template.price || 19.90
    
    // 创建新订单
    const newOrder = {
      id: orders.length + 1,
      orderNo,
      userId: 1, // 假设当前用户ID为1
      templateId,
      templateName: template.name,
      amount,
      status: 0, // 0-待支付
      payType: payType || null,
      payTime: null,
      createTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
      updateTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    
    orders.push(newOrder)
    
    console.log(`Mock: 创建订单成功 - 订单号: ${orderNo}`)
    return {
      code: 200,
      message: '创建成功',
      data: {
        orderNo,
        amount,
        payUrl: `https://pay.example.com/pay?orderNo=${orderNo}`
      }
    }
  } catch (error) {
    console.error('Mock: 创建订单失败 - 未知错误', error)
    return {
      code: 500,
      message: '服务器内部错误',
      data: null
    }
  }
}

// 获取订单列表
export function getOrderListMock(options) {
  const { url } = options
  const params = new URLSearchParams(url.split('?')[1])
  
  const status = params.get('status')
  const page = parseInt(params.get('page') || '1')
  const size = parseInt(params.get('size') || '10')
  
  let filteredOrders = [...orders]
  
  // 根据状态筛选
  if (status !== null && status !== undefined) {
    filteredOrders = filteredOrders.filter(order => order.status === parseInt(status))
  }
  
  // 分页
  const start = (page - 1) * size
  const end = start + size
  const pageOrders = filteredOrders.slice(start, end)
  
  return {
    code: 200,
    message: '操作成功',
    data: {
      total: filteredOrders.length,
      list: pageOrders
    }
  }
}

// 获取订单详情
export function getOrderDetailMock(options) {
  const { url } = options
  const orderNo = url.split('/').pop()
  
  const order = orders.find(o => o.orderNo === orderNo)
  
  if (!order) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  return {
    code: 200,
    message: '操作成功',
    data: order
  }
}

// 取消订单
export function cancelOrderMock(options) {
  const { url } = options
  const orderNo = url.split('/')[3]
  
  const orderIndex = orders.findIndex(o => o.orderNo === orderNo)
  
  if (orderIndex === -1) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  const order = orders[orderIndex]
  
  // 检查是否为会员订单
  if (order.templateName.includes('会员')) {
    return {
      code: 400,
      message: '会员订单不可取消',
      data: null
    }
  }
  
  // 检查订单状态
  if (order.status !== 0) {
    return {
      code: 400,
      message: '只能取消待支付的订单',
      data: null
    }
  }
  
  // 更新订单状态
  order.status = 2 // 已取消
  order.updateTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
  orders[orderIndex] = order
  
  return {
    code: 200,
    message: '取消成功',
    data: null
  }
}

// 支付订单（模拟支付成功）
export function payOrderMock(orderNo) {
  const orderIndex = orders.findIndex(o => o.orderNo === orderNo)
  
  if (orderIndex === -1) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  const order = orders[orderIndex]
  
  // 更新订单状态
  order.status = 1 // 已支付
  order.payTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
  order.updateTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
  orders[orderIndex] = order
  
  return {
    code: 200,
    message: '支付成功',
    data: null
  }
}

// 支付回调接口
export function payNotifyMock(options) {
  const { url, body } = options
  const payType = url.split('/').pop()
  
  try {
    // 解析支付平台回调数据
    const notifyData = JSON.parse(body)
    const { orderNo, status } = notifyData
    
    if (!orderNo) {
      return {
        code: 400,
        message: '订单号不能为空',
        data: null
      }
    }
    
    // 查找订单
    const orderIndex = orders.findIndex(o => o.orderNo === orderNo)
    if (orderIndex === -1) {
      return {
        code: 404,
        message: '订单不存在',
        data: null
      }
    }
    
    // 更新订单状态
    const order = orders[orderIndex]
    if (status === 'SUCCESS') {
      order.status = 1 // 已支付
      order.payTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
      order.updateTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
      order.payType = parseInt(payType) || 1
      orders[orderIndex] = order
      
      return {
        code: 200,
        message: '支付成功',
        data: null
      }
    } else {
      return {
        code: 400,
        message: '支付失败',
        data: null
      }
    }
  } catch (error) {
    console.error('支付回调处理失败:', error)
    return {
      code: 500,
      message: '支付回调处理失败',
      data: null
    }
  }
}

// 直接支付订单
export function payOrderDirectMock(options) {
  const { url } = options
  const orderNo = url.split('/')[3]
  
  const orderIndex = orders.findIndex(o => o.orderNo === orderNo)
  
  if (orderIndex === -1) {
    return {
      code: 404,
      message: '订单不存在',
      data: null
    }
  }
  
  const order = orders[orderIndex]
  
  // 检查订单状态
  if (order.status !== 0) {
    return {
      code: 400,
      message: '只能支付待支付的订单',
      data: null
    }
  }
  
  // 更新订单状态
  order.status = 1 // 已支付
  order.payTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
  order.updateTime = new Date().toISOString().replace('T', ' ').substring(0, 19)
  orders[orderIndex] = order
  
  return {
    code: 200,
    message: '支付成功',
    data: null
  }
} 