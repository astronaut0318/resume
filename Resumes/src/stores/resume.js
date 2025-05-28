import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as resumeApi from '../api/resume'

export const useResumeStore = defineStore('resume', () => {
  // 状态
  const resumeList = ref([])
  const currentResume = ref(null)
  const loading = ref(false)
  const total = ref(0)
  
  // 计算属性
  const defaultResume = computed(() => {
    return resumeList.value.find(resume => resume.isDefault === 1)
  })
  
  // 方法
  // 获取简历列表
  const fetchResumeList = async (params = {}) => {
    loading.value = true
    try {
      const res = await resumeApi.getResumeList(params)
      if (res.code === 200) {
        resumeList.value = res.data.list
        total.value = res.data.total
        return res.data
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('获取简历列表失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 获取简历详情
  const fetchResumeDetail = async (resumeId) => {
    loading.value = true
    try {
      const res = await resumeApi.getResumeDetail(resumeId)
      if (res.code === 200) {
        currentResume.value = res.data
        return res.data
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('获取简历详情失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 创建简历
  const createResume = async (data) => {
    loading.value = true
    try {
      const res = await resumeApi.createResume(data)
      if (res.code === 200) {
        // 刷新列表
        await fetchResumeList()
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('创建简历失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 更新简历
  const updateResume = async (resumeId, data) => {
    loading.value = true
    try {
      const res = await resumeApi.updateResume(resumeId, data)
      if (res.code === 200) {
        // 如果当前编辑的是这个简历，更新当前简历
        if (currentResume.value && currentResume.value.id === resumeId) {
          currentResume.value = { ...currentResume.value, ...data }
        }
        // 刷新列表
        await fetchResumeList()
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('更新简历失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 删除简历
  const deleteResume = async (resumeId) => {
    loading.value = true
    try {
      const res = await resumeApi.deleteResume(resumeId)
      if (res.code === 200) {
        // 如果当前编辑的是这个简历，清空当前简历
        if (currentResume.value && currentResume.value.id === resumeId) {
          currentResume.value = null
        }
        // 刷新列表
        await fetchResumeList()
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('删除简历失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 设置默认简历
  const setDefaultResume = async (resumeId) => {
    loading.value = true
    try {
      const res = await resumeApi.setDefaultResume(resumeId)
      if (res.code === 200) {
        // 刷新列表
        await fetchResumeList()
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      console.error('设置默认简历失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 初始化：加载简历列表
  fetchResumeList()
  
  return {
    // 状态
    resumeList,
    currentResume,
    loading,
    total,
    
    // 计算属性
    defaultResume,
    
    // 方法
    fetchResumeList,
    fetchResumeDetail,
    createResume,
    updateResume,
    deleteResume,
    setDefaultResume
  }
}) 