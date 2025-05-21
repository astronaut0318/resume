import axios from 'axios';
import { ElMessage } from 'element-plus';

// 创建axios实例
const API = axios.create({
  baseURL: '/api', // 基础URL，可以根据环境配置不同的值
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
});

// 错误码映射表
const ERROR_CODE_MAP = {
  400: '请求参数错误',
  401: '未授权，请重新登录',
  403: '权限不足',
  404: '资源不存在',
  500: '服务器内部错误'
};

// 请求拦截器
API.interceptors.request.use(
  config => {
    // 从localStorage获取token并添加到请求头
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
API.interceptors.response.use(
  response => {
    const res = response.data;
    
    // 根据API文档，code为200表示操作成功
    if (res.code === 200) {
      return res;
    }
    
    // 处理错误情况
    ElMessage.error(res.message || ERROR_CODE_MAP[res.code] || '未知错误');
    
    // 特殊处理token过期情况
    if (res.code === 401) {
      // 清除本地token信息
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
      
      // 跳转到登录页面
      setTimeout(() => {
        window.location.href = '/login';
      }, 1500);
    }
    
    return Promise.reject(res);
  },
  error => {
    // 网络错误等情况
    const { response } = error;
    let message = '网络错误，请稍后重试';
    
    if (response && response.status && ERROR_CODE_MAP[response.status]) {
      message = ERROR_CODE_MAP[response.status];
    }
    
    ElMessage.error(message);
    return Promise.reject(error);
  }
);

export default API; 