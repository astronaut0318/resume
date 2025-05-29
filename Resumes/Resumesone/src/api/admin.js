import axios from 'axios'
import { getUserList, updateUserStatus, updateUserRole, createTemplateCategory } from '../mock/admin'

// 创建axios实例
const api = axios.create({
  baseURL: '/api/admin',
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
      // 处理401未授权错误
      if (error.response.status === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      // 处理403权限不足错误
      if (error.response.status === 403) {
        window.location.href = '/'
      }
      return Promise.reject(error.response.data)
    }
    return Promise.reject(error)
  }
)

// 获取用户列表
export const getUsersList = (params) => {
  // 使用mock数据
  return Promise.resolve(getUserList(params))
  // 实际接口
  // return api.get('/users', { params })
}

// 更新用户状态（禁用/启用）
export const updateUserStatusApi = (userId, status) => {
  // 使用mock数据
  return Promise.resolve(updateUserStatus(userId, status))
  // 实际接口
  // return api.put(`/users/${userId}/status`, { status })
}

// 更新用户角色
export const updateUserRoleApi = (userId, role) => {
  // 使用mock数据
  return Promise.resolve(updateUserRole(userId, role))
  // 实际接口
  // return api.put(`/users/${userId}/role`, { role })
}

// 创建模板分类
export const createTemplateCategoryApi = (data) => {
  // 使用mock数据
  return Promise.resolve(createTemplateCategory(data))
  // 实际接口
  // return api.post('/template-categories', data)
} 