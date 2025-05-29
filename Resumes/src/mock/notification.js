import Mock from 'mockjs'

// 生成通知列表数据
const notifications = Mock.mock({
  'list|30': [{
    'id|+1': 1,
    'userId': '@integer(1, 100)',
    'title': '@ctitle(5, 20)',
    'content': '@cparagraph(1, 3)',
    'type|1': [1, 2, 3], // 1-系统通知，2-订单通知，3-其他
    'isRead|0-1': 0,
    'createTime': '@datetime("yyyy-MM-dd HH:mm:ss")'
  }]
}).list

// 预设一些典型通知
const presetNotifications = [
  {
    id: 101,
    userId: 1,
    title: '订单支付成功',
    content: '您的VIP会员订单已支付成功，感谢您的支持！现在您可以享受VIP会员的所有特权了。',
    type: 2,
    isRead: 0,
    createTime: '2023-07-15 10:30:45'
  },
  {
    id: 102,
    userId: 1,
    title: '系统维护通知',
    content: '尊敬的用户，系统将于2023年8月1日凌晨2:00-4:00进行系统维护，届时将暂停服务。给您带来的不便，敬请谅解。',
    type: 1,
    isRead: 0,
    createTime: '2023-07-20 09:15:30'
  },
  {
    id: 103,
    userId: 1,
    title: '新功能上线通知',
    content: '我们新上线了AI简历优化功能，可以智能分析您的简历并提供专业的优化建议。快去体验吧！',
    type: 1,
    isRead: 0,
    createTime: '2023-07-25 14:20:10'
  }
]

// 合并通知数据
const allNotifications = [...presetNotifications, ...notifications]

// 获取通知列表
export function getNotificationsMock(config) {
  const { url } = config
  const params = new URLSearchParams(url.split('?')[1])
  
  const isRead = params.get('isRead') !== null ? parseInt(params.get('isRead')) : null
  const type = params.get('type') !== null ? parseInt(params.get('type')) : null
  const page = parseInt(params.get('page') || '1')
  const size = parseInt(params.get('size') || '10')
  
  // 筛选
  let filteredList = [...allNotifications]
  if (isRead !== null) {
    filteredList = filteredList.filter(item => item.isRead === isRead)
  }
  if (type !== null) {
    filteredList = filteredList.filter(item => item.type === type)
  }
  
  // 排序（按时间倒序）
  filteredList.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  
  // 分页
  const total = filteredList.length
  const startIndex = (page - 1) * size
  const endIndex = startIndex + size
  const list = filteredList.slice(startIndex, endIndex)
  
  return {
    code: 200,
    message: '操作成功',
    data: {
      total,
      list
    }
  }
}

// 获取通知详情
export function getNotificationDetailMock(config) {
  const notificationId = parseInt(config.url.match(/\/api\/notifications\/(\d+)/)[1])
  const notification = allNotifications.find(item => item.id === notificationId)
  
  if (!notification) {
    return {
      code: 404,
      message: '通知不存在',
      data: null
    }
  }
  
  return {
    code: 200,
    message: '操作成功',
    data: notification
  }
}

// 标记通知为已读
export function markAsReadMock(config) {
  const notificationId = parseInt(config.url.match(/\/api\/notifications\/(\d+)\/read/)[1])
  const notification = allNotifications.find(item => item.id === notificationId)
  
  if (!notification) {
    return {
      code: 404,
      message: '通知不存在',
      data: null
    }
  }
  
  notification.isRead = 1
  
  return {
    code: 200,
    message: '标记成功',
    data: null
  }
}

// 标记所有通知为已读
export function markAllAsReadMock() {
  allNotifications.forEach(item => {
    item.isRead = 1
  })
  
  return {
    code: 200,
    message: '标记成功',
    data: null
  }
}

// 删除通知
export function deleteNotificationMock(config) {
  const notificationId = parseInt(config.url.match(/\/api\/notifications\/(\d+)/)[1])
  const index = allNotifications.findIndex(item => item.id === notificationId)
  
  if (index === -1) {
    return {
      code: 404,
      message: '通知不存在',
      data: null
    }
  }
  
  allNotifications.splice(index, 1)
  
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
}

// 获取未读通知数量
export function getUnreadCountMock() {
  const count = allNotifications.filter(item => item.isRead === 0).length
  
  return {
    code: 200,
    message: '操作成功',
    data: {
      count
    }
  }
}

// 发送邮件通知(仅管理员)
export function sendEmailNotificationMock(config) {
  const { userIds, subject, content, saveToSystem } = JSON.parse(config.body)
  
  // 模拟发送邮件
  const successCount = userIds ? userIds.length : 10
  
  // 如果需要保存到系统通知
  if (saveToSystem) {
    const newNotification = {
      id: allNotifications.length + 200,
      userId: 1, // 模拟发给当前用户
      title: subject,
      content,
      type: 1, // 系统通知
      isRead: 0,
      createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
    }
    
    allNotifications.unshift(newNotification)
  }
  
  return {
    code: 200,
    message: '发送成功',
    data: {
      successCount,
      failCount: 0
    }
  }
}

// 导出所有mock处理函数
export default [
  {
    url: /\/api\/notifications(\?.+)?$/,
    method: 'get',
    response: getNotificationsMock
  },
  {
    url: /\/api\/notifications\/\d+$/,
    method: 'get',
    response: getNotificationDetailMock
  },
  {
    url: /\/api\/notifications\/\d+\/read$/,
    method: 'put',
    response: markAsReadMock
  },
  {
    url: '/api/notifications/read-all',
    method: 'put',
    response: markAllAsReadMock
  },
  {
    url: /\/api\/notifications\/\d+$/,
    method: 'delete',
    response: deleteNotificationMock
  },
  {
    url: '/api/notifications/unread-count',
    method: 'get',
    response: getUnreadCountMock
  },
  {
    url: '/api/notifications/email',
    method: 'post',
    response: sendEmailNotificationMock
  }
] 