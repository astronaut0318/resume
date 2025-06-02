import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as fileApi from '../api/file'
import { ElMessage } from 'element-plus'

export const useFileStore = defineStore('file', () => {
  // 状态
  const fileList = ref([])
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const fileType = ref('')
  
  // 计算属性
  const isEmpty = computed(() => fileList.value.length === 0)
  
  // 获取文件列表
  const getFileList = async (params = {}) => {
    loading.value = true
    try {
      const finalParams = {
        page: params.page || currentPage.value,
        size: params.size || pageSize.value
      }
      
      if (params.type || fileType.value) {
        finalParams.type = params.type || fileType.value
      }
      
      const response = await fileApi.getFileList(finalParams)
      const { code, data, message } = response.data
      
      if (code === 200) {
        fileList.value = data.list
        total.value = data.total
        return { success: true, data: data }
      } else {
        ElMessage.error(message || '获取文件列表失败')
        return { success: false, message }
      }
    } catch (error) {
      console.error('获取文件列表失败:', error)
      ElMessage.error('获取文件列表失败，请重试')
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }
  
  // 上传文件
  const uploadFile = async (file, type = '') => {
    loading.value = true
    try {
      const response = await fileApi.uploadFile(file, type)
      const { code, data, message } = response.data
      
      if (code === 200) {
        await getFileList() // 刷新文件列表
        return { success: true, data }
      } else {
        ElMessage.error(message || '上传文件失败')
        return { success: false, message }
      }
    } catch (error) {
      console.error('上传文件失败:', error)
      ElMessage.error('上传文件失败，请重试')
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }
  
  // 下载文件
  const downloadFile = async (fileId, fileName) => {
    loading.value = true
    try {
      // 显示下载中提示
      ElMessage.info(`正在准备下载 ${fileName}...`)
      
      // 调用API进行下载
      const response = await fileApi.downloadFile(fileId, fileName)
      
      ElMessage.success('下载成功')
      return { success: true }
    } catch (error) {
      console.error('下载文件失败:', error)
      // 区分不同类型的错误
      if (error.code === 'ECONNABORTED') {
        ElMessage.warning('下载请求超时，文件可能过大或网络不稳定')
      } else if (error.code === 'ERR_NETWORK') {
        ElMessage.warning('网络连接问题，请检查您的网络')
      } else if (error.response && error.response.status === 500) {
        ElMessage.warning('服务器处理请求时出错，请稍后再试')
      } else {
        ElMessage.error('下载文件失败，请重试')
      }
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }
  
  // 删除文件
  const deleteFile = async (fileId) => {
    loading.value = true
    try {
      const response = await fileApi.deleteFile(fileId)
      const { code, message } = response.data
      
      if (code === 200) {
        await getFileList() // 刷新文件列表
        ElMessage.success('删除成功')
        return { success: true }
      } else {
        ElMessage.error(message || '删除文件失败')
        return { success: false, message }
      }
    } catch (error) {
      console.error('删除文件失败:', error)
      ElMessage.error('删除文件失败，请重试')
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }
  
  // 导出简历为PDF
  const exportResumeToPdf = async (resumeId) => {
    loading.value = true
    try {
      const response = await fileApi.exportResumeToPdf(resumeId)
      // 处理文件下载
      const blob = new Blob([response.data])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `简历_${resumeId}.pdf`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      return { success: true }
    } catch (error) {
      console.error('导出PDF失败:', error)
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 导出简历为Word
  const exportResumeToWord = async (resumeId) => {
    loading.value = true
    try {
      const response = await fileApi.exportResumeToWord(resumeId)
      const blob = response.data // 关键：拿到真正的 Blob
      let filename = `简历_${resumeId}.docx`
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      return { success: true }
    } catch (error) {
      console.error('导出Word失败:', error)
      ElMessage.error('Word下载失败，请重试')
      return Promise.reject(error)
    } finally {
      loading.value = false
    }
  }
  
  // 设置页码
  const setPage = (page) => {
    currentPage.value = page
    getFileList()
  }
  
  // 设置每页条数
  const setPageSize = (size) => {
    pageSize.value = size
    currentPage.value = 1 // 重置到第一页
    getFileList()
  }
  
  // 设置文件类型过滤
  const setFileType = (type) => {
    fileType.value = type
    currentPage.value = 1 // 重置到第一页
    getFileList()
  }
  
  return {
    // 状态
    fileList,
    loading,
    total,
    currentPage,
    pageSize,
    fileType,
    
    // 计算属性
    isEmpty,
    
    // 方法
    getFileList,
    uploadFile,
    downloadFile,
    deleteFile,
    exportResumeToPdf,
    exportResumeToWord,
    setPage,
    setPageSize,
    setFileType
  }
}) 