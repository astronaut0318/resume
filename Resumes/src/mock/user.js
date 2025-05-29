// 模拟用户数据
const users = [
  {
    id: 1,
    username: 'admin',
    password: '123456',
    email: 'admin@example.com',
    phone: '13800138000',
    avatar: 'https://picsum.photos/100/100?random=1',
    role: 2, // 0-普通用户，1-VIP用户，2-超级vip
    status: 1,
    createTime: '2023-01-01 12:00:00'
  },
  {
    id: 2,
    username: 'test',
    password: '123456',
    email: 'test@example.com',
    phone: '13800138001',
    avatar: 'https://picsum.photos/100/100?random=2',
    role: 0,
    status: 1,
    createTime: '2023-01-02 12:00:00'
  },
  {
    id: 3,
    username: 'test',
    password: '123456',
    email: 'test@example.com',
    phone: '13800138002',
    avatar: 'https://picsum.photos/100/100?random=3',
    role: 0,
    status: 1,
    createTime: '2023-01-03 12:00:00'
  }
]

// 模拟用户详细信息
const userDetails = [
  {
    id: 1,
    userId: 1,
    realName: '张三',
    gender: 1,
    birthday: '1990-01-01',
    education: '本科',
    workYears: 5,
    address: '北京市朝阳区',
    profile: '资深前端开发工程师，具有丰富的项目经验...',
    createTime: '2023-01-01 12:00:00'
  },
  {
    id: 2,
    userId: 2,
    realName: '张三',
    gender: 1,
    birthday: '1992-01-01',
    education: '本科',
    workYears: 3,
    address: '上海市浦东新区',
    profile: '资深前端开发工程师，具有丰富的项目经验...',
    createTime: '2023-01-02 12:00:00'
  }
]

// 模拟VIP信息
const vipInfo = [
  {
    userId: 1,
    isVip: true,
    level: 2,
    startTime: '2023-01-01 00:00:00',
    endTime: '2023-12-31 23:59:59',
    remainingDays: 300
  }
]

// 生成token的函数
const generateToken = (userId) => {
  return `mock_token_${userId}_${Date.now()}`
}

// 用户注册
export const register = (data) => {
  const { username, password, email, phone } = data
  
  // 检查用户名是否已存在
  if (users.find(u => u.username === username)) {
    return {
      code: 400,
      message: '用户名已存在',
      data: null
    }
  }

  // 创建新用户
  const newUser = {
    id: users.length + 1,
    username,
    password,
    email,
    phone,
    avatar: `https://picsum.photos/100/100?random=${users.length + 1}`,
    role: 0,
    status: 1,
    createTime: new Date().toISOString().replace('T', ' ').split('.')[0]
  }
  
  users.push(newUser)
  
  return {
    code: 200,
    message: '注册成功',
    data: {
      userId: newUser.id,
      username: newUser.username
    }
  }
}

// 用户登录
export const login = (data) => {
  const { username, password } = data
  
  const user = users.find(u => u.username === username && u.password === password)
  
  if (!user) {
    return {
      code: 400,
      message: '用户名或密码错误',
      data: null
    }
  }

  if (user.status !== 1) {
    return {
      code: 403,
      message: '账号已被禁用',
      data: null
    }
  }

  const token = generateToken(user.id)
  const refreshToken = generateToken(user.id) + '_refresh'
  
  return {
    code: 200,
    message: '登录成功',
    data: {
      userId: user.id,
      username: user.username,
      token,
      refreshToken,
      expiresIn: 7200
    }
  }
}

// 获取用户信息
export const getUserInfo = (userId) => {
  const user = users.find(u => u.id === parseInt(userId))
  
  if (!user) {
    return {
      code: 404,
      message: '用户不存在',
      data: null
    }
  }

  // 不返回密码
  const { password, ...userInfo } = user
  
  return {
    code: 200,
    message: '操作成功',
    data: userInfo
  }
}

// 获取用户详细信息
export const getUserDetails = (userId) => {
  const details = userDetails.find(d => d.userId === parseInt(userId))
  
  if (!details) {
    return {
      code: 404,
      message: '用户详细信息不存在',
      data: null
    }
  }
  
  return {
    code: 200,
    message: '操作成功',
    data: details
  }
}

// 更新用户信息
export const updateUserInfo = (userId, data) => {
  const userIndex = users.findIndex(u => u.id === parseInt(userId))
  
  if (userIndex === -1) {
    return {
      code: 404,
      message: '用户不存在',
      data: null
    }
  }

  users[userIndex] = {
    ...users[userIndex],
    ...data
  }
  
  return {
    code: 200,
    message: '更新成功',
    data: null
  }
}

// 更新用户详细信息
export const updateUserDetails = (userId, data) => {
  const detailIndex = userDetails.findIndex(d => d.userId === parseInt(userId))
  
  if (detailIndex === -1) {
    // 如果不存在则创建新的
    userDetails.push({
      id: userDetails.length + 1,
      userId: parseInt(userId),
      ...data,
      createTime: new Date().toISOString().replace('T', ' ').split('.')[0]
    })
  } else {
    // 更新现有的
    userDetails[detailIndex] = {
      ...userDetails[detailIndex],
      ...data
    }
  }
  
  return {
    code: 200,
    message: '更新成功',
    data: null
  }
}

// 查询VIP会员状态
export const getVipStatus = (userId) => {
  const vip = vipInfo.find(v => v.userId === parseInt(userId))
  
  if (!vip) {
    return {
      code: 200,
      message: '操作成功',
      data: {
        isVip: false,
        level: 0,
        startTime: null,
        endTime: null,
        remainingDays: 0
      }
    }
  }
  
  return {
    code: 200,
    message: '操作成功',
    data: vip
  }
}

// 刷新token
export const refreshToken = (refreshToken) => {
  // 从refreshToken中提取userId
  const userId = parseInt(refreshToken.split('_')[1])
  
  if (!userId) {
    return {
      code: 400,
      message: '无效的刷新令牌',
      data: null
    }
  }

  const newToken = generateToken(userId)
  const newRefreshToken = generateToken(userId) + '_refresh'
  
  return {
    code: 200,
    message: '刷新成功',
    data: {
      token: newToken,
      refreshToken: newRefreshToken,
      expiresIn: 7200
    }
  }
}

// 退出登录
export const logout = () => {
  return {
    code: 200,
    message: '退出成功',
    data: null
  }
} 