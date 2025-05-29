// 模拟用户列表数据
const userList = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    phone: '13800138000',
    role: 3,  // 3-管理员
    status: 1,
    createTime: '2023-01-01 12:00:00'
  },
  {
    id: 2,
    username: 'super',
    email: 'super@example.com',
    phone: '13800138001',
    role: 2,  // 2-终身会员
    status: 1,
    createTime: '2023-01-02 12:00:00'
  },
  {
    id: 3,
    username: 'zhangsan',
    email: 'zhangsan@example.com',
    phone: '13800138002',
    role: 0,  // 0-普通用户
    status: 1,
    createTime: '2023-01-03 12:00:00'
  },
  {
    id: 4,
    username: 'lisi',
    email: 'lisi@example.com',
    phone: '13800138003',
    role: 1,  // 1-VIP用户
    status: 1,
    createTime: '2023-01-04 12:00:00'
  },
  {
    id: 5,
    username: 'wangwu',
    email: 'wangwu@example.com',
    phone: '13800138004',
    role: 0,  // 0-普通用户
    status: 0,  // 0-禁用
    createTime: '2023-01-05 12:00:00'
  }
]

// 模拟模板分类数据
const templateCategories = [
  {
    id: 1,
    name: '简约风格',
    sort: 1
  },
  {
    id: 2,
    name: '创意风格',
    sort: 2
  },
  {
    id: 3,
    name: '商务风格',
    sort: 3
  }
]

// 获取用户列表
export const getUserList = (params = {}) => {
  const { username, email, role, status, page = 1, size = 10 } = params
  
  // 过滤用户列表
  let filteredList = [...userList]
  
  if (username) {
    filteredList = filteredList.filter(user => user.username.includes(username))
  }
  
  if (email) {
    filteredList = filteredList.filter(user => user.email.includes(email))
  }
  
  if (role !== undefined && role !== null && role !== '') {
    filteredList = filteredList.filter(user => user.role === parseInt(role))
  }
  
  if (status !== undefined && status !== null && status !== '') {
    filteredList = filteredList.filter(user => user.status === parseInt(status))
  }
  
  // 计算总数
  const total = filteredList.length
  
  // 分页
  const start = (page - 1) * size
  const end = start + size
  const pagedList = filteredList.slice(start, end)
  
  return {
    code: 200,
    message: '操作成功',
    data: {
      total,
      list: pagedList
    }
  }
}

// 更新用户状态
export const updateUserStatus = (userId, status) => {
  const userIndex = userList.findIndex(user => user.id === parseInt(userId))
  
  if (userIndex === -1) {
    return {
      code: 404,
      message: '用户不存在',
      data: null
    }
  }
  
  userList[userIndex].status = status
  
  return {
    code: 200,
    message: '操作成功',
    data: null
  }
}

// 更新用户角色
export const updateUserRole = (userId, role) => {
  const userIndex = userList.findIndex(user => user.id === parseInt(userId))
  
  if (userIndex === -1) {
    return {
      code: 404,
      message: '用户不存在',
      data: null
    }
  }
  
  userList[userIndex].role = role
  
  return {
    code: 200,
    message: '操作成功',
    data: null
  }
}

// 创建模板分类
export const createTemplateCategory = (data) => {
  const { name, sort } = data
  
  // 检查分类名称是否已存在
  if (templateCategories.some(category => category.name === name)) {
    return {
      code: 400,
      message: '分类名称已存在',
      data: null
    }
  }
  
  const newCategory = {
    id: templateCategories.length + 1,
    name,
    sort: sort || templateCategories.length + 1
  }
  
  templateCategories.push(newCategory)
  
  return {
    code: 200,
    message: '创建成功',
    data: {
      id: newCategory.id
    }
  }
} 