/**
 * 全局配置文件
 * 包含环境相关的配置项
 */
export default {
  // MinIO服务器地址
  minioEndpoint: import.meta.env.VITE_MINIO_ENDPOINT || 'http://localhost:9090',
  
  // API基础地址
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '/',
  
  // 默认头像
  defaultAvatar: '/assets/default-avatar.png'
} 