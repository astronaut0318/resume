import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as fileApi from '../api/file'
import { ElMessage } from 'element-plus'
import router from '../router'

export const useFileStore = defineStore('file', () => {
  // 状态
  const fileList = ref([])
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const fileType = ref('resume')
  
  // 计算属性
  const isEmpty = computed(() => fileList.value.length === 0)
  
  // 获取文件列表
  const getFileList = async (params = {}) => {
    loading.value = true
    try {
      // 从localStorage获取userId
      const userId = localStorage.getItem('userId')
      if (!userId) {
        ElMessage.warning('用户未登录，请先登录')
        // 可以重定向到登录页面
        router.push('/login')
        loading.value = false
        return { success: false, message: '用户未登录' }
      }
      console.log('使用用户ID获取文件列表:', userId)
      
      const finalParams = {
        page: params.page || currentPage.value,
        size: params.size || pageSize.value,
        userId: userId,
        _timestamp: Date.now(), // 添加时间戳，防止缓存
        ...params
      }
      
      // 如果没有明确指定type，则不传递type参数，获取所有类型的文件
      if (!params.type) {
        delete finalParams.type;
      }
      
      console.log('请求文件列表最终参数:', finalParams)
      
      // 添加重试逻辑
      let retryCount = 0;
      const maxRetries = 2;
      let success = false;
      let response;
      
      while (!success && retryCount <= maxRetries) {
        try {
          response = await fileApi.getFileList(finalParams);
          success = true;
        } catch (err) {
          retryCount++;
          if (retryCount <= maxRetries) {
            console.log(`文件列表请求失败，正在进行第 ${retryCount} 次重试...`);
            await new Promise(resolve => setTimeout(resolve, 1000)); // 延迟1秒重试
          } else {
            throw err; // 重试次数用完，抛出最后的错误
          }
        }
      }
      
      console.log('获取文件列表原始响应:', response)
      
      // 正确获取数据部分 - 针对已知的后端格式
      if (response && response.code === 200 && response.data) {
        console.log('文件列表数据:', response.data)
        
        // 后端直接返回数组形式的列表（当前格式）
        if (Array.isArray(response.data)) {
          const allFiles = response.data
          // 保存总记录数
          total.value = allFiles.length
          
          // 手动处理分页 - 即使后端没有分页，前端也进行分页处理
          const start = (currentPage.value - 1) * pageSize.value
          const end = start + pageSize.value
          // 从完整数组中截取当前页应显示的数据
          fileList.value = allFiles.slice(start, end)
          
          console.log(`文件列表已更新，总共${total.value}条记录，当前页显示${fileList.value.length}条（第${currentPage.value}页，每页${pageSize.value}条）`)
        } 
        // 处理分页格式或其他格式
        else {
          const data = response.data
          if (data.records) {
            fileList.value = data.records
            total.value = data.total || 0
            currentPage.value = data.current || 1
            pageSize.value = data.size || 10
            console.log('文件列表已更新为分页数据，共', data.total, '个文件')
          } else if (Array.isArray(data.list)) {
            fileList.value = data.list
            total.value = data.total || data.list.length
            console.log('文件列表已更新为list数据，共', data.list.length, '个文件')
          } else {
            // 未知格式，直接使用整个data
            console.warn('未识别的文件列表格式:', data)
            fileList.value = []
            total.value = 0
          }
        }
        return { success: true, data: response.data }
      } else {
        console.error('获取文件列表响应错误:', response)
        ElMessage.error(response?.message || '获取文件列表失败')
        fileList.value = []
        total.value = 0
        return { success: false, message: response?.message }
      }
    } catch (error) {
      console.error('获取文件列表异常:', error)
      ElMessage.error('获取文件列表失败，请重试')
      fileList.value = []
      total.value = 0
      return { success: false, error }
    } finally {
      loading.value = false
    }
  }
  
  // 上传文件
  const uploadFile = async (file) => {
    loading.value = true
    try {
      // 从localStorage获取用户ID
      const userId = localStorage.getItem('userId')
      if (!userId) {
        ElMessage.warning('用户未登录，请先登录')
        // 可以重定向到登录页面
        router.push('/login')
        loading.value = false
        return { success: false, message: '用户未登录' }
      }
      console.log('使用用户ID进行文件上传:', userId)
      
      const response = await fileApi.uploadFile(file, 'resume', '', 0, userId)
      console.log('文件上传Store响应:', response)
      
      const { code, data, message } = response.data || {}
      
      if (code === 200 && data) {
        console.log('上传成功，文件数据:', data)
        // 上传成功后立即刷新文件列表
        await getFileList()
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
      // 确保fileId作为字符串处理
      const stringFileId = String(fileId);
      console.log(`[${new Date().toLocaleTimeString()}] store层开始删除文件(字符串ID): ${stringFileId}`);
      
      // 调用API删除文件
      const response = await fileApi.deleteFile(stringFileId)
      console.log('文件删除响应:', response)
      
      // 检查响应是否明确表示成功
      if (response.success === true || response.code === 200) {
        console.log(`[${new Date().toLocaleTimeString()}] 文件删除API调用成功`);
        
        // 成功时手动维护本地状态
        try {
          // 从文件列表中移除该文件 - 注意要比较字符串形式
          const index = fileList.value.findIndex(file => String(file.id) === stringFileId);
          if (index !== -1) {
            console.log(`从本地状态移除文件: ${stringFileId}`);
            fileList.value.splice(index, 1);
            total.value--;
          }
        } catch (err) {
          console.error('本地状态更新失败:', err);
        }
        
        return { 
          success: true, 
          message: response.message || '删除成功',
          fileId: stringFileId
        }
      } else {
        console.warn('文件删除API响应异常:', response);
        return { 
          success: false, 
          message: response.message || '删除操作未成功完成',
          fileId: stringFileId
        }
      }
    } catch (error) {
      console.error('删除文件操作失败:', error);
      
      // 提供更详细的错误信息日志
      if (error.stack) {
        console.error('错误堆栈:', error.stack);
      }
      
      return { 
        success: false, 
        message: error.message || '删除文件失败',
        error,
        fileId
      }
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
  }
  
  // 设置每页条数
  const setPageSize = (size) => {
    pageSize.value = size
    currentPage.value = 1
  }
  
  // 设置文件类型过滤
  const setFileType = (type) => {
    fileType.value = type
    currentPage.value = 1
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