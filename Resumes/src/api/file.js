import request from '../utils/request'
import { ElMessage } from 'element-plus'
import router from '../router'
import axios from 'axios'
import { minioConfig, getFileUrl } from '../utils/config'

/**
 * 检查用户是否登录
 * @returns {string|null} 用户ID或null（如果未登录）
 */
const checkUserLogin = () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('用户未登录，请先登录')
    // 导航到登录页
    router.push('/login')
    return null
  }
  return userId
}

/**
 * 上传文件
 * @param {File} file - 文件对象
 * @param {string} type - 文件类型(可选，后端会自动判断)
 * @param {string} bucket - 已废弃，设为null
 * @param {number} bizId - 业务ID(可选)
 * @param {number} userId - 用户ID(必填)
 * @returns {Promise}
 */
export function uploadFile(file, type = '', bucket = null, bizId = 0, userId) {
  // 检查用户是否登录
  if (!userId) {
    userId = checkUserLogin()
    if (!userId) return Promise.reject(new Error('用户未登录'))
  }

  const formData = new FormData()
  formData.append('file', file)
  
  // 添加类型参数（虽然后端已不依赖此参数，但保持API兼容）
  formData.append('type', type)
  
  // 不再发送bucket参数
  
  // 添加业务ID和用户ID
  formData.append('bizId', bizId)
  formData.append('userId', userId)
  
  console.log('Uploading file:', {
    fileName: file.name,
    fileType: file.type,
    fileSize: file.size,
    type: type,
    bizId: bizId,
    userId: userId
  })
  
  // 使用与后端匹配的路径 - FileController中的/upload路径
  return request.post('/api/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 下载文件
 * @param {number|string} fileId - 文件ID
 * @param {string} fileName - 文件名(用于下载时显示)
 * @returns {Promise}
 */
export function downloadFile(fileId, fileName) {
  // 检查用户是否登录
  if (!checkUserLogin()) {
    return Promise.reject(new Error('用户未登录'))
  }

  // 在开发环境中模拟下载而不发送实际请求
  if (import.meta.env.MODE === 'development') {
    return new Promise((resolve) => {
      // 创建模拟的文件数据
      const ext = fileName.split('.').pop().toLowerCase()
      let mimeType = 'application/octet-stream' // 默认MIME类型
      
      // 根据扩展名设置适当的MIME类型
      if (['jpg', 'jpeg', 'png', 'gif'].includes(ext)) {
        mimeType = `image/${ext === 'jpg' ? 'jpeg' : ext}`
      } else if (ext === 'pdf') {
        mimeType = 'application/pdf'
      } else if (['doc', 'docx'].includes(ext)) {
        mimeType = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      }
      
      // 创建一个空的blob作为模拟文件
      const blob = new Blob(['模拟文件内容 - ' + fileName], { type: mimeType })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      
      // 延迟一会儿再返回成功，模拟网络延迟
      setTimeout(() => {
        resolve({
          data: {
            code: 200,
            message: '下载成功',
            data: null
          }
        })
      }, 500)
    })
  }
  
  // 生产环境使用实际请求
  return request.get(`/api/files/${fileId}/download`, {
    responseType: 'blob'
  }).then(response => {
    // 创建blob链接并下载
    const blob = new Blob([response.data])
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    return {
      code: 200,
      message: '下载成功'
    }
  })
}

/**
 * 导出简历为PDF
 * @param {number|string} resumeId - 简历ID
 * @returns {Promise}
 */
export function exportResumeToPdf(resumeId) {
  // 检查用户是否登录
  if (!checkUserLogin()) {
    return Promise.reject(new Error('用户未登录'))
  }

  return request.get(`/api/files/resume/${resumeId}/pdf`, {
    responseType: 'blob'
  })
}

/**
 * 导出简历为Word
 * @param {number|string} resumeId - 简历ID
 * @returns {Promise<Blob>} 直接返回后端的blob数据
 */
export function exportResumeToWord(resumeId) {
  // 检查用户是否登录
  if (!checkUserLogin()) {
    return Promise.reject(new Error('用户未登录'))
  }

  return request.get(`/api/files/resume/${resumeId}/word`, {
    responseType: 'blob'
  })
    .then(blob => blob); // request.js 拦截器已返回 blob
}

/**
 * 获取文件列表
 * @param {Object} params - 查询参数
 * @param {string} [params.type] - 文件类型(可选)
 * @param {number} [params.page=1] - 页码
 * @param {number} [params.size=10] - 每页大小
 * @param {number} params.userId - 用户ID
 * @returns {Promise}
 */
export function getFileList(params = {}) {
  // 检查用户是否登录
  if (!params.userId) {
    const userId = checkUserLogin()
    if (!userId) {
      return Promise.reject(new Error('用户未登录'))
    }
    params.userId = userId
  }

  // 确保必须的参数存在
  const finalParams = {
    page: params.page || 1,
    size: params.size || 10,
    userId: params.userId,
    _timestamp: Date.now(), // 添加时间戳，防止缓存
    ...params
  }
  
  // 记录请求时间，便于调试
  const startTime = Date.now()
  console.log(`[${new Date().toLocaleTimeString()}] 请求文件列表，参数:`, finalParams)
  
  // 使用与后端匹配的路径 - FileController中的根路径
  return request.get('/api/files', { params: finalParams })
    .then(response => {
      // 记录响应时间
      const duration = Date.now() - startTime
      console.log(`[${new Date().toLocaleTimeString()}] 文件列表响应(${duration}ms):`, response)
      
      // 检查响应格式
      if (!response || typeof response !== 'object') {
        console.error('文件列表响应格式异常:', response)
        return Promise.reject(new Error('响应格式异常'))
      }
      
      // 标准响应处理
      if (response.code === 200) {
        if (!response.data) {
          console.warn('文件列表响应成功但数据为空')
          return {
            code: 200,
            message: response.message || '操作成功',
            data: [],
            success: true
          }
        }
        
        // 检查数据格式
        if (Array.isArray(response.data)) {
          console.log(`文件列表获取成功，共 ${response.data.length} 条记录`)
          // 检查返回的每条记录是否有fileUrl
          response.data.forEach((item, index) => {
            if (!item.fileUrl) {
              console.warn(`第 ${index + 1} 条记录缺少fileUrl字段:`, item)
              // 使用config.js中的getFileUrl函数生成文件URL
              if (item.filePath) {
                item.fileUrl = getFileUrl(item.filePath, item.fileType)
                console.log(`为文件 ${item.id} 补充fileUrl: ${item.fileUrl}`)
              }
            }
          })
        } else {
          console.warn('文件列表数据不是数组格式:', response.data)
        }
        
        return response
      } else {
        // 错误响应处理
        console.error(`文件列表请求失败: code=${response.code}, message=${response.message}`)
        return Promise.reject(new Error(response.message || '获取文件列表失败'))
      }
    })
    .catch(error => {
      console.error('获取文件列表出错:', error)
      return Promise.reject(error)
    })
}

/**
 * 删除文件
 * @param {number|string} fileId - 文件ID
 * @returns {Promise}
 */
export function deleteFile(fileId) {
  // 检查用户是否登录
  if (!checkUserLogin()) {
    return Promise.reject(new Error('用户未登录'))
  }

  // 确保fileId作为字符串处理
  const stringFileId = String(fileId);
  
  console.log(`[${new Date().toLocaleTimeString()}] 开始删除文件(字符串ID): ${stringFileId}`);
  
  // 直接使用axios发送DELETE请求，注意URL路径不要包含多余的/api前缀
  return axios({
    url: `/files/de/${stringFileId}`,
    method: 'delete',
    headers: {
      'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
    }
  })
  .then(response => {
    console.log('删除文件响应:', response);
    
    // 判断响应是否成功
    if (response.data && response.data.code === 200) {
      return {
        code: 200,
        message: response.data.message || '删除成功',
        success: true
      };
    } else {
      return Promise.reject(new Error(response.data?.message || '删除失败'));
    }
  })
  .catch(error => {
    console.error('删除文件请求失败:', error);
    throw new Error(error.response?.data?.message || error.message || '删除失败');
  });
} 