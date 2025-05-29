import axios from 'axios'
import { 
  createResume as mockCreateResume, 
  getResumeList as mockGetResumeList,
  getResumeDetail as mockGetResumeDetail,
  updateResume as mockUpdateResume,
  deleteResume as mockDeleteResume,
  setDefaultResume as mockSetDefaultResume
} from '../mock/resume'

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
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(error.response.data)
    }
    return Promise.reject(error)
  }
)

// 创建简历
export const createResume = (data) => {
  // 使用mock数据
  // return Promise.resolve(mockCreateResume(data))
  // 实际接口
  return api.post('/resumes', data)
}

// 获取简历列表
export const getResumeList = (params) => {
  // 使用mock数据
  // return Promise.resolve(mockGetResumeList(params))
  // 实际接口
  return api.get('/resumes', { params })
}

// 获取简历详情
export const getResumeDetail = (resumeId) => {
  // 使用mock数据
  // return Promise.resolve(mockGetResumeDetail(resumeId))
  // 实际接口
  return api.get(`/resumes/${resumeId}`)
}

// 更新简历
export const updateResume = (resumeId, data) => {
  // 使用mock数据
  // return Promise.resolve(mockUpdateResume(resumeId, data))
  // 实际接口
  return api.put(`/resumes/${resumeId}`, data)
}

// 删除简历
export const deleteResume = (resumeId) => {
  // 使用mock数据
  // return Promise.resolve(mockDeleteResume(resumeId))
  // 实际接口
  return api.delete(`/resumes/${resumeId}`)
}

// 设置默认简历
export const setDefaultResume = (resumeId) => {
  // 使用mock数据
  // return Promise.resolve(mockSetDefaultResume(resumeId))
  // 实际接口
  return api.put(`/resumes/${resumeId}/default`)
} 