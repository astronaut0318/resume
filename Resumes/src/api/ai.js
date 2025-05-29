import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器，添加token
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

// 简历分析 - 符合API-DOCS-PART6.md中的6.1节
export function analyzeResume(resumeId, resumeData = null) {
  // 如果传入了resumeData并且resumeId是临时ID（如-1），则使用传入的数据
  if (resumeData && (resumeId === -1 || resumeId === null)) {
    return api.post('/ai/resume/analyze', { resumeData })
  }
  // 否则使用正常的resumeId请求
  return api.post('/ai/resume/analyze', { resumeId })
}

// 内容优化建议 - 符合API-DOCS-PART6.md中的6.2节
export function optimizeResume(resumeId, section = null, customOptions = null, resumeData = null) {
  // 如果有自定义选项，则使用自定义请求
  if (customOptions) {
    const { customPrompt, textContent } = customOptions
    return api.post('/ai/resume/optimize', {
      resumeId,
      customPrompt,
      textContent
    })
  }
  
  // 如果传入了resumeData并且resumeId是临时ID，则使用传入的数据
  if (resumeData && (resumeId === -1 || resumeId === null)) {
    const data = { resumeData }
    if (section) {
      data.section = section
    }
    return api.post('/ai/resume/optimize', data)
  }
  
  // 否则使用标准请求
  const data = { resumeId }
  if (section) {
    data.section = section
  }
  return api.post('/ai/resume/optimize', data)
}

// 职位匹配分析 - 符合API-DOCS-PART6.md中的6.3节
export function matchPosition(resumeId, position, jobDescription, resumeData = null) {
  // 如果传入了resumeData并且resumeId是临时ID，则使用传入的数据
  if (resumeData && (resumeId === -1 || resumeId === null)) {
    return api.post('/ai/resume/match', {
      resumeData,
      position,
      jobDescription
    })
  }
  
  // 否则使用标准请求
  return api.post('/ai/resume/match', {
    resumeId,
    position,
    jobDescription
  })
}

// 自动生成简历内容 - 符合API-DOCS-PART6.md中的6.4节
export function generateResumeContent(data) {
  return api.post('/ai/resume/generate', data)
}

export default {
  analyzeResume,
  optimizeResume,
  matchPosition,
  generateResumeContent
} 