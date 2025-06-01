import request from '../utils/request'

// 创建简历
export const createResume = (data) => {
  return request.post('/api/resumes', data)
}

// 获取简历列表
export const getResumeList = (params) => {
  return request.get('/api/resumes', { params })
}

// 获取简历详情
export const getResumeDetail = (resumeId) => {
  return request.get(`/api/resumes/${resumeId}`)
}

// 更新简历
export const updateResume = (data) => {
  const resumeId = data.id
  const updateData = { ...data }
  delete updateData.id
  return request.put(`/api/resumes/${resumeId}`, updateData)
}

// 删除简历
export const deleteResume = (resumeId) => {
  return request.delete(`/api/resumes/${resumeId}`)
}

// 设置默认简历
export const setDefaultResume = (resumeId) => {
  return request.put(`/api/resumes/${resumeId}/default`)
} 