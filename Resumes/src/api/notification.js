import request from '../utils/request'

/**
 * 获取通知列表
 * @param {Object} params - 查询参数
 * @param {Number} params.isRead - 是否已读(可选，0-未读，1-已读)
 * @param {Number} params.type - 通知类型(可选，1-系统通知，2-订单通知，3-其他)
 * @param {Number} params.page - 页码(默认1)
 * @param {Number} params.size - 每页大小(默认10)
 */
export function getNotifications(params) {
  return request({
    url: '/api/notifications',
    method: 'get',
    params
  })
}

/**
 * 获取通知详情
 * @param {Number} notificationId - 通知ID
 */
export function getNotificationDetail(notificationId) {
  return request({
    url: `/api/notifications/${notificationId}`,
    method: 'get'
  })
}

/**
 * 标记通知为已读
 * @param {Number} notificationId - 通知ID
 */
export function markAsRead(notificationId) {
  return request({
    url: `/api/notifications/${notificationId}/read`,
    method: 'put'
  })
}

/**
 * 标记所有通知为已读
 */
export function markAllAsRead() {
  return request({
    url: '/api/notifications/read-all',
    method: 'put'
  })
}

/**
 * 删除通知
 * @param {Number} notificationId - 通知ID
 */
export function deleteNotification(notificationId) {
  return request({
    url: `/api/notifications/${notificationId}`,
    method: 'delete'
  })
}

/**
 * 获取未读通知数量
 */
export function getUnreadCount() {
  return request({
    url: '/api/notifications/unread-count',
    method: 'get'
  })
}

/**
 * 发送邮件通知(仅管理员)
 * @param {Object} data - 发送数据
 * @param {Array} data.userIds - 接收用户ID列表，为空则发送给所有用户
 * @param {String} data.subject - 邮件主题
 * @param {String} data.content - 邮件内容
 * @param {Boolean} data.saveToSystem - 是否同时保存到系统通知
 */
export function sendEmailNotification(data) {
  return request({
    url: '/api/notifications/email',
    method: 'post',
    data
  })
} 