import axios from 'axios'
import { register as mockRegister, 
         login as mockLogin, 
         getUserInfo as mockGetUserInfo,
         getUserDetails as mockGetUserDetails,
         updateUserInfo as mockUpdateUserInfo,
         updateUserDetails as mockUpdateUserDetails,
         getVipStatus as mockGetVipStatus,
         refreshToken as mockRefreshToken,
         logout as mockLogout } from '../mock/user'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      // token过期处理
      if (error.response.status === 401) {
        // 清除token并跳转到登录页
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(error.response.data)
    }
    return Promise.reject(error)
  }
)

// 用户注册
export const register = (data) => {
  // 使用mock数据
  return Promise.resolve(mockRegister(data))
  // 实际接口
  // return api.post('/users/register', data)
}

// 用户登录
export const login = (data) => {
  // 使用mock数据
  const response = mockLogin(data)
  if (response.code === 200) {
    localStorage.setItem('token', response.data.token)
    localStorage.setItem('refreshToken', response.data.refreshToken)
    localStorage.setItem('userId', response.data.userId)
  }
  return Promise.resolve(response)
  // 实际接口
  // return api.post('/auth/login', data)
}

// 获取用户信息
export const getUserInfo = (userId) => {
  // 使用mock数据
  return Promise.resolve(mockGetUserInfo(userId))
  // 实际接口
  // return api.get(`/users/${userId}`)
}

// 获取用户详细信息
export const getUserDetails = (userId) => {
  // 使用mock数据
  return Promise.resolve(mockGetUserDetails(userId))
  // 实际接口
  // return api.get(`/users/${userId}/details`)
}

// 更新用户信息
export const updateUserInfo = (userId, data) => {
  // 使用mock数据
  return Promise.resolve(mockUpdateUserInfo(userId, data))
  // 实际接口
  // return api.put(`/users/${userId}`, data)
}

// 更新用户详细信息
export const updateUserDetails = (userId, data) => {
  // 使用mock数据
  return Promise.resolve(mockUpdateUserDetails(userId, data))
  // 实际接口
  // return api.put(`/users/${userId}/details`, data)
}

// 获取VIP状态
export const getVipStatus = (userId) => {
  // 使用mock数据
  return Promise.resolve(mockGetVipStatus(userId))
  // 实际接口
  // return api.get(`/users/${userId}/vip`)
}

// 刷新token
export const refreshToken = (refreshToken) => {
  // 使用mock数据
  return Promise.resolve(mockRefreshToken(refreshToken))
  // 实际接口
  // return api.post('/auth/refresh', { refreshToken })
}

// 退出登录
export const logout = () => {
  // 使用mock数据
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('userId')
  return Promise.resolve(mockLogout())
  // 实际接口
  // return api.post('/auth/logout')
} 