<script setup>
import { ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useFileStore } from '../stores/file'

const props = defineProps({
  resumeId: {
    type: [Number, String],
    required: true
  },
  buttonType: {
    type: String,
    default: 'primary'
  },
  buttonSize: {
    type: String,
    default: 'default'
  },
  buttonText: {
    type: String,
    default: '导出'
  }
})

const fileStore = useFileStore()
const loading = ref(false)

// 导出简历
const handleExport = () => {
  ElMessageBox.confirm('请选择导出格式', '导出简历', {
    confirmButtonText: 'PDF格式',
    cancelButtonText: 'Word格式',
    distinguishCancelAndClose: true,
    closeOnClickModal: false
  }).then(() => {
    // 导出PDF
    loading.value = true
    ElMessage.info('正在生成PDF，请稍候...')
    
    fileStore.exportResumeToPdf(props.resumeId)
      .then(() => {
        ElMessage.success('PDF下载成功！')
      })
      .catch((error) => {
        console.error('导出PDF失败:', error)
        // 处理可能的服务器错误
        if (error.response && error.response.status === 500) {
          ElMessage.warning('服务器暂时无法处理您的请求，请稍后再试')
        } else {
          ElMessage.error('PDF下载失败，请重试')
        }
        
        // 如果是演示环境，模拟下载成功
        if (import.meta.env.MODE === 'development') {
          ElMessage.success('在演示环境中模拟PDF下载成功')
        }
      })
      .finally(() => {
        loading.value = false
      })
  }).catch(action => {
    if (action === 'cancel') {
      // 导出Word
      loading.value = true
      ElMessage.info('正在生成Word文档，请稍候...')
      
      fileStore.exportResumeToWord(props.resumeId)
        .then(() => {
          ElMessage.success('Word文档下载成功！')
        })
        .catch((error) => {
          console.error('导出Word失败:', error)
          // 处理可能的服务器错误
          if (error.response && error.response.status === 500) {
            ElMessage.warning('服务器暂时无法处理您的请求，请稍后再试')
          } else {
            ElMessage.error('Word下载失败，请重试')
          }
          
          // 如果是演示环境，模拟下载成功
          if (import.meta.env.MODE === 'development') {
            ElMessage.success('在演示环境中模拟Word下载成功')
          }
        })
        .finally(() => {
          loading.value = false
        })
    }
  })
}
</script>

<template>
  <el-button
    :type="buttonType"
    :size="buttonSize"
    :loading="loading"
    @click="handleExport"
  >
    <el-icon><Download /></el-icon>
    {{ buttonText }}
  </el-button>
</template> 