import request from '../utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/api/orders',
    method: 'post',
    data
  })
}

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/api/orders',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(orderNo) {
  return request({
    url: `/api/orders/${orderNo}`,
    method: 'get'
  })
}

// 取消订单
export function cancelOrder(orderNo) {
  return request({
    url: `/api/orders/${orderNo}/cancel`,
    method: 'put'
  })
}

// 直接支付订单
export function payOrder(orderNo) {
  return request({
    url: `/api/orders/${orderNo}/pay`,
    method: 'post'
  })
}

// 支付回调
export function payNotify(payType, data) {
  return request({
    url: `/api/orders/notify/${payType}`,
    method: 'post',
    data
  })
} 