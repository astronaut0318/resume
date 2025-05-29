import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useTemplateStore = defineStore('template', () => {
  // 从localStorage加载收藏数据
  const loadCollectedTemplates = () => {
    const saved = localStorage.getItem('collectedTemplates')
    return new Set(saved ? JSON.parse(saved) : [])
  }

  // 存储收藏的模板ID集合
  const collectedTemplates = ref(loadCollectedTemplates())

  // 监听收藏变化并保存到localStorage
  watch(collectedTemplates, (templates) => {
    localStorage.setItem('collectedTemplates', JSON.stringify(Array.from(templates)))
  }, { deep: true })

  // 检查模板是否已收藏
  const isCollected = (templateId) => {
    return collectedTemplates.value.has(templateId)
  }

  // 添加收藏
  const addCollection = (templateId) => {
    collectedTemplates.value.add(templateId)
  }

  // 取消收藏
  const removeCollection = (templateId) => {
    collectedTemplates.value.delete(templateId)
  }

  // 切换收藏状态
  const toggleCollection = (templateId) => {
    if (isCollected(templateId)) {
      removeCollection(templateId)
      return false
    } else {
      addCollection(templateId)
      return true
    }
  }

  return {
    collectedTemplates,
    isCollected,
    addCollection,
    removeCollection,
    toggleCollection
  }
}) 