import request from '../utils/request'
import { ElMessage } from 'element-plus'
import router from '../router'

/**
 * 获取文档编辑器配置
 * @param {string} sourceType - 文档类型（resume, template, file）
 * @param {number} sourceId - 文档ID
 * @param {string} mode - 模式（view, edit, comment）
 * @param {number} userId - 用户ID
 * @param {string} userName - 用户名称
 * @returns {Promise} 文档配置信息
 */
export function getDocumentConfig(sourceType, sourceId, mode = 'view', userId, userName) {
  if (!userId) {
    userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.warning('用户未登录，请先登录')
      router.push('/login')
      return Promise.reject(new Error('用户未登录'))
    }
  }

  if (!userName) {
    userName = localStorage.getItem('username')
  }

  return request({
    url: `/api/documents/${sourceType.toLowerCase()}/${sourceId}/config`,
    method: 'get',
    params: {
      mode,
      userId,
      userName
    }
  })
}

/**
 * 创建文档版本
 * @param {string} sourceType - 文档类型（resume, template, file）
 * @param {number} sourceId - 文档ID
 * @param {number} userId - 用户ID
 * @returns {Promise} 版本ID
 */
export function createDocumentVersion(sourceType, sourceId, userId) {
  if (!userId) {
    userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.warning('用户未登录，请先登录')
      router.push('/login')
      return Promise.reject(new Error('用户未登录'))
    }
  }

  return request({
    url: `/api/documents/${sourceType.toLowerCase()}/${sourceId}/versions`,
    method: 'post',
    params: {
      userId
    }
  })
}

/**
 * 获取文档版本列表
 * @param {string} sourceType - 文档类型（resume, template, file）
 * @param {number} sourceId - 文档ID
 * @returns {Promise} 版本列表
 */
export function getDocumentVersions(sourceType, sourceId) {
  return request({
    url: `/api/documents/${sourceType.toLowerCase()}/${sourceId}/versions`,
    method: 'get'
  })
}

/**
 * 预览文档版本
 * @param {number} versionId - 版本ID
 * @param {number} userId - 用户ID
 * @param {string} userName - 用户名称
 * @returns {Promise} 文档配置信息
 */
export function previewDocumentVersion(versionId, userId, userName) {
  if (!userId) {
    userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.warning('用户未登录，请先登录')
      router.push('/login')
      return Promise.reject(new Error('用户未登录'))
    }
  }

  if (!userName) {
    userName = localStorage.getItem('username')
  }

  return request({
    url: `/api/documents/versions/${versionId}/preview`,
    method: 'get',
    params: {
      userId,
      userName
    }
  })
}

/**
 * 下载文档
 * @param {string} sourceType - 文档类型（resume, template, file）
 * @param {number} sourceId - 文档ID
 * @returns {Promise} blob数据流
 */
export function downloadDocument(sourceType, sourceId) {
  return request({
    url: `/api/documents/${sourceType.toLowerCase()}/${sourceId}/download`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 下载文档版本
 * @param {number} versionId - 版本ID
 * @returns {Promise} blob数据流
 */
export function downloadDocumentVersion(versionId) {
  return request({
    url: `/api/documents/versions/${versionId}/download`,
    method: 'get',
    responseType: 'blob'
  })
} 