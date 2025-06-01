import request from '../utils/request'

// 获取用户列表
export const getUsersList = (params) => {
  return request.get('/users', { params })
}

// 更新用户状态（禁用/启用）
export const updateUserStatusApi = (userId, status) => {
  return request.put(`/users/${userId}/status`, { status })
}

// 更新用户角色
export const updateUserRoleApi = (userId, role) => {
  return request.put(`/users/${userId}/role`, { role })
}

// 创建模板分类
export const createTemplateCategoryApi = (data) => {
  return request.post('/template-categories', data)
} 