import request from '../utils/request'

// 用户注册
export const register = (data) => {
  return request.post('/api/users/register', data)
}

// 用户登录
export const login = (data) => {
  return request.post('/api/auth/login', data).then(response => {
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('refreshToken', response.data.refreshToken)
      localStorage.setItem('userId', response.data.userId)
    }
    return response
  })
}

// 获取用户信息
export const getUserInfo = (userId) => {
  return request.get(`/api/users/${userId}`)
}

// 获取用户详细信息
export const getUserDetails = (userId) => {
  return request.get(`/api/users/${userId}/details`)
}

// 更新用户信息
export const updateUserInfo = (userId, data) => {
  return request.put(`/api/users/${userId}`, data)
}

// 更新用户详细信息
export const updateUserDetails = (userId, data) => {
  return request.put(`/api/users/${userId}/details`, data)
}

// 获取VIP状态
export const getVipStatus = (userId) => {
  return request.get(`/api/users/${userId}/vip`)
}

// 刷新token
export const refreshToken = (refreshToken) => {
  return request.post('/api/auth/refresh', { refreshToken })
}

// 退出登录
export const logout = () => {
  // 先请求后端，再清除本地 token
  return request.post('/api/auth/logout').finally(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userId')
  })
} 