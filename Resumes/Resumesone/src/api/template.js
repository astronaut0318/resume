import request from '@/utils/request'

// 获取模板分类列表
export function getTemplateCategories() {
  return request({
    url: '/api/templates/categories',
    method: 'get'
  })
}

// 获取模板列表
export function getTemplates(params) {
  return request({
    url: '/api/templates',
    method: 'get',
    params
  })
}

// 获取模板详情
export function getTemplateDetail(id) {
  return request({
    url: `/api/templates/${id}`,
    method: 'get'
  })
}

// 收藏模板
export function collectTemplate(id) {
  return request({
    url: `/api/templates/${id}/collect`,
    method: 'post'
  })
}

// 取消收藏
export function uncollectTemplate(id) {
  return request({
    url: `/api/templates/${id}/collect`,
    method: 'delete'
  })
}

// 获取收藏列表
export function getCollections(params) {
  return request({
    url: '/api/templates/collections',
    method: 'get',
    params
  })
}

// 分享模板
export function shareTemplate(id) {
  return request({
    url: `/api/templates/${id}/share`,
    method: 'get'
  })
} 