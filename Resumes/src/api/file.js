import request from '../utils/request'

/**
 * 上传文件
 * @param {File} file - 文件对象
 * @param {string} type - 文件类型(可选，如avatar、resume等)
 * @returns {Promise}
 */
export function uploadFile(file, type = '') {
  const formData = new FormData()
  formData.append('file', file)
  
  if (type) {
    formData.append('type', type)
  }
  
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
 * @returns {Promise}
 */
export function getFileList(params = {}) {
  return request.get('/api/files', { params })
}

/**
 * 删除文件
 * @param {number|string} fileId - 文件ID
 * @returns {Promise}
 */
export function deleteFile(fileId) {
  return request.delete(`/api/files/${fileId}`)
} 