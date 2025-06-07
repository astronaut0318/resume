<template>
  <div class="document-editor-page">
    <el-page-header @back="goBack" :title="pageTitle">
      <template #content>
        <div class="flex items-center">
          <el-icon class="mr-2"><Document /></el-icon>
          <span class="text-large font-600 mr-3">{{ pageTitle }}</span>
        </div>
      </template>
      <template #extra>
        <div class="flex items-center">
          <el-button @click="downloadDocument" type="primary" plain>
            <el-icon><Download /></el-icon>
            下载文档
          </el-button>
        </div>
      </template>
    </el-page-header>
    
    <div class="editor-container">
      <document-editor
        :source-type="sourceType"
        :source-id="sourceId"
        :mode="mode"
        :show-version-control="showVersionControl"
      />
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import DocumentEditor from '../components/DocumentEditor.vue'
import { downloadDocument as apiDownloadDocument } from '../api/document'

export default {
  name: 'DocumentEditorPage',
  components: {
    DocumentEditor,
    Document,
    Download
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    // 从路由参数获取文档信息
    const sourceType = ref(route.params.sourceType?.toUpperCase() || 'FILE')
    const sourceId = ref(parseInt(route.params.sourceId) || 0)
    const mode = ref(route.query.mode || 'view')
    const showVersionControl = ref(route.query.showVersions === 'true')
    
    // 页面标题
    const pageTitle = computed(() => {
      const modeText = mode.value === 'view' ? '查看' : (mode.value === 'edit' ? '编辑' : '评论')
      const typeText = sourceType.value === 'RESUME' ? '简历' : 
                      (sourceType.value === 'TEMPLATE' ? '模板' : '文档')
      return `${modeText}${typeText}`
    })
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 下载文档
    const downloadDocument = async () => {
      try {
        const response = await apiDownloadDocument(sourceType.value, sourceId.value)
        
        // 创建下载链接
        const blob = new Blob([response])
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        
        // 设置文件名
        const fileName = `document_${sourceId.value}.${getFileExtension(sourceType.value)}`
        link.download = fileName
        
        // 触发下载
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        ElMessage.success('文档下载成功')
      } catch (error) {
        console.error('下载文档失败:', error)
        ElMessage.error('下载文档失败')
      }
    }
    
    // 根据文档类型获取默认文件扩展名
    const getFileExtension = (type) => {
      switch (type) {
        case 'RESUME':
        case 'TEMPLATE':
          return 'docx'
        default:
          return 'docx'
      }
    }
    
    onMounted(() => {
      // 检查必要参数
      if (!sourceId.value) {
        ElMessage.error('无效的文档ID')
        router.push('/')
      }
    })
    
    return {
      sourceType,
      sourceId,
      mode,
      showVersionControl,
      pageTitle,
      goBack,
      downloadDocument
    }
  }
}
</script>

<style scoped>
.document-editor-page {
  padding: 20px;
}

.editor-container {
  margin-top: 20px;
}
</style> 