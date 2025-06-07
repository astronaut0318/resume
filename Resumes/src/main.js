import { createApp } from 'vue'
import pinia from './stores' // 导入统一的 pinia 实例
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'

// 全局配置
// OnlyOffice服务器配置
window.onlyofficeServerUrl = 'http://192.168.64.129:80' // OnlyOffice Document Server 地址

// 配置axios
axios.defaults.timeout = 30000 // 增加超时时间到30秒
axios.defaults.retry = 2 // 增加重试次数到2次
axios.defaults.retryDelay = 1500 // 增加重试延时到1.5秒
axios.defaults.maxContentLength = 10485760 // 设置最大响应内容长度为10MB

// API基础URL
axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

// 添加请求拦截器
axios.interceptors.request.use(
  config => {
    // 获取token并添加到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 设置请求阶段的超时处理
    if (config.url.includes('/download') || config.responseType === 'blob') {
      config.timeout = 60000 // 下载文件时使用更长的超时时间
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 添加响应拦截器，处理重试逻辑
axios.interceptors.response.use(undefined, function axiosRetryInterceptor(err) {
  const config = err.config
  // 如果配置不存在或重试次数未设置，则拒绝
  if (!config || !config.retry) return Promise.reject(err)
  
  // 设置变量追踪重试次数
  config.__retryCount = config.__retryCount || 0
  
  // 如果已经达到重试次数上限，则拒绝
  if (config.__retryCount >= config.retry) {
    return Promise.reject(err)
  }
  
  // 增加重试次数
  config.__retryCount += 1
  
  console.log(`请求重试: ${config.url}, 第 ${config.__retryCount} 次重试`)
  
  // 创建新的Promise以处理延时
  const backoff = new Promise(function(resolve) {
    setTimeout(function() {
      resolve()
    }, config.retryDelay || 1)
  })
  
  // 返回重试的Promise
  return backoff.then(function() {
    // 对于下载类请求特殊处理
    if (config.responseType === 'blob') {
      // 确保请求头和响应类型正确设置
      config.headers = { ...config.headers }
      config.responseType = 'blob'
    }
    return axios(config)
  })
})

const app = createApp(App)

// 注册所有 ElementPlus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia) // 使用统一的 pinia 实例
app.use(router)
app.use(ElementPlus)
app.mount('#app')
