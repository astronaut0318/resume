/**
 * 全局配置文件
 */

// MinIO服务器配置
export const minioConfig = {
  baseUrl: 'http://localhost:9090', // MinIO服务器地址
  endpoints: {
    thumbnails: 'resume-thumbnails', // 缩略图存储桶
    templates: 'resume-templates' // 模板存储桶
  }
}

// 文件服务API配置
export const fileServiceConfig = {
  baseUrl: '/api/files' // 文件服务API基础路径
}

// 默认资源路径
export const defaultResources = {
  avatar: '/static/default-avatar.png', // 默认头像
  thumbnail: '/static/default-thumbnail.png' // 默认缩略图
}

// 获取文件完整URL
export const getFileUrl = (filePath, fileType) => {
  if (!filePath) return defaultResources.avatar;
  
  // 如果已经是完整URL，则直接返回
  if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
    return filePath;
  }
  
  // 根据文件类型选择不同的存储桶
  const bucket = fileType === 'thumbnail' ? minioConfig.endpoints.thumbnails : minioConfig.endpoints.templates;
  
  // 返回完整URL
  return `${minioConfig.baseUrl}/${bucket}/${filePath}`;
}

export default {
  minioConfig,
  fileServiceConfig,
  defaultResources,
  getFileUrl
} 