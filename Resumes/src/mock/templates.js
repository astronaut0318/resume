import Mock from 'mockjs'
const Random = Mock.Random

// 模板分类数据
const categories = [
  { id: 1, name: '简约风格', sort: 1, status: 1 },
  { id: 2, name: '商务风格', sort: 2, status: 1 },
  { id: 3, name: '创意风格', sort: 3, status: 1 },
  { id: 4, name: '专业风格', sort: 4, status: 1 }
]

// 生成16个模板数据
export const templates = Array.from({ length: 16 }, (_, index) => ({
  id: index + 1,
  categoryId: Math.floor(Math.random() * 4) + 1,
  name: Random.ctitle(4, 8) + '简历模板',
  thumbnail: `http://example.com/thumb${index + 1}.jpg`,
  price: Number(Random.float(9.9, 29.9, 1, 1)),
  isFree: 0,
  downloads: Random.integer(100, 10000),
  description: Random.cparagraph(1, 3),
  status: 1,
  createTime: Random.datetime(),
  updateTime: Random.datetime()
}))

// 根据ID获取模板
export const getTemplateById = (id) => {
  try {
    if (!id) {
      console.error('getTemplateById: ID参数为空')
      return null
    }
    
    const templateId = parseInt(id)
    if (isNaN(templateId)) {
      console.error(`getTemplateById: 无效的ID格式 - ${id}`)
      return null
    }
    
    const template = templates.find(t => t.id === templateId)
    if (!template) {
      console.error(`getTemplateById: 未找到ID为${templateId}的模板`)
    }
    return template
  } catch (error) {
    console.error('getTemplateById: 查询模板时出错', error)
    return null
  }
}

// 格式化数字显示
export const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toString()
}

// Mock API接口列表
const templatesApiList = [
  // 获取模板分类列表
  {
    url: '/api/templates/categories',
    method: 'get',
    response: () => {
      console.log('Mock: 获取模板分类列表')
      return {
        code: 200,
        message: '操作成功',
        data: categories
      }
    }
  },

  // 获取模板列表
  {
    url: '/api/templates',
    method: 'get',
    response: (config) => {
      console.log('Mock: 获取模板列表', config.query)
      const { categoryId, isFree, keyword, page = 1, size = 10 } = config.query
      
      let filteredTemplates = [...templates]
      
      if (categoryId) {
        filteredTemplates = filteredTemplates.filter(t => t.categoryId === parseInt(categoryId))
      }
      
      if (isFree !== undefined) {
        filteredTemplates = filteredTemplates.filter(t => t.isFree === parseInt(isFree))
      }
      
      if (keyword) {
        filteredTemplates = filteredTemplates.filter(t => 
          t.name.includes(keyword) || t.description.includes(keyword)
        )
      }

      return {
        code: 200,
        message: '操作成功',
        data: {
          total: filteredTemplates.length,
          list: filteredTemplates.slice((page - 1) * size, page * size)
        }
      }
    }
  },

  // 获取模板详情
  {
    url: /\/api\/templates\/(\d+)$/,
    method: 'get',
    response: (config) => {
      console.log('Mock: 获取模板详情')
      const id = parseInt(config.url.match(/\d+$/)?.[0] || 0)
      const template = templates.find(t => t.id === id)
      
      if (!template) {
        return {
          code: 404,
          message: '模板不存在',
          data: null
        }
      }

      return {
        code: 200,
        message: '操作成功',
        data: template
      }
    }
  },

  // 收藏模板
  {
    url: /\/api\/templates\/(\d+)\/collect$/,
    method: 'post',
    response: () => {
      console.log('Mock: 收藏模板')
      return {
        code: 200,
        message: '收藏成功',
        data: null
      }
    }
  },

  // 取消收藏
  {
    url: /\/api\/templates\/(\d+)\/collect$/,
    method: 'delete',
    response: () => {
      console.log('Mock: 取消收藏')
      return {
        code: 200,
        message: '取消成功',
        data: null
      }
    }
  },

  // 获取收藏列表
  {
    url: '/api/templates/collections',
    method: 'get',
    response: () => {
      console.log('Mock: 获取收藏列表')
      const collections = templates
        .slice(0, 5)
        .map(t => ({
          id: t.id,
          templateId: t.id,
          name: t.name,
          thumbnail: t.thumbnail,
          price: t.price,
          isFree: t.isFree,
          collectTime: Random.datetime()
        }))

      return {
        code: 200,
        message: '操作成功',
        data: {
          total: collections.length,
          list: collections
        }
      }
    }
  },

  // 分享模板
  {
    url: /\/api\/templates\/(\d+)\/share$/,
    method: 'get',
    response: (config) => {
      console.log('Mock: 分享模板')
      const id = parseInt(config.url.match(/\d+/)?.[0] || 0)
      const template = templates.find(t => t.id === id)
      
      if (!template) {
        return {
          code: 404,
          message: '模板不存在',
          data: null
        }
      }

      return {
        code: 200,
        message: '操作成功',
        data: {
          shareUrl: `https://example.com/share/template/${id}?code=${Random.string('lower', 12)}`,
          expireTime: Random.datetime('yyyy-MM-dd HH:mm:ss')
        }
      }
    }
  }
]

export default templatesApiList 