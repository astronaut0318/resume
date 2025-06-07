<template>
  <div class="document-version-preview">
    <el-page-header @back="goBack" title="版本预览">
      <template #content>
        <div class="flex items-center">
          <el-icon class="mr-2"><Document /></el-icon>
          <span class="text-large font-600 mr-3">版本预览</span>
          <el-tag v-if="version" size="small" type="info">版本 {{ version.version }}</el-tag>
        </div>
      </template>
      <template #extra>
        <div class="flex items-center">
          <el-button @click="downloadVersion" type="primary" plain>
            <el-icon><Download /></el-icon>
            下载此版本
          </el-button>
        </div>
      </template>
    </el-page-header>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>
    
    <div v-else-if="error" class="error-container">
      <el-empty description="加载版本失败">
        <template #description>
          <p>{{ error }}</p>
        </template>
        <el-button type="primary" @click="loadVersion">重试</el-button>
      </el-empty>
    </div>
    
    <div v-else class="editor-container">
      <div ref="editorContainer" class="document-editor-container"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Document, Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { previewDocumentVersion, downloadDocumentVersion } from '../api/document'

export default {
  name: 'DocumentVersionPreview',
  components: {
    Document,
    Download
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const editorContainer = ref(null)
    const loading = ref(true)
    const error = ref(null)
    const version = ref(null)
    let docEditor = null
    
    // 从路由参数获取版本ID
    const versionId = parseInt(route.params.versionId) || 0
    const userId = localStorage.getItem('userId')
    const userName = localStorage.getItem('username')
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 加载脚本
    const loadScript = (src) => {
      return new Promise((resolve, reject) => {
        if (document.querySelector(`script[src="${src}"]`)) {
          resolve()
          return
        }
        
        const script = document.createElement('script')
        script.src = src
        script.onload = resolve
        script.onerror = reject
        document.head.appendChild(script)
      })
    }
    
    // 加载版本
    const loadVersion = async () => {
      loading.value = true
      error.value = null
      
      try {
        // 获取OnlyOffice服务器地址
        const documentServerUrl = window.onlyofficeServerUrl || 'http://192.168.64.129:80'
        
        // 1. 加载OnlyOffice JS API
        await loadScript(`${documentServerUrl}/web-apps/apps/api/documents/api.js`)
        
        // 2. 获取版本预览配置
        const response = await previewDocumentVersion(versionId, userId, userName)
        console.log('API响应状态:', response.code)
        console.log('API响应数据:', response)
        
        if (response.code === 200) {
          version.value = response.data.version
          
          // 确保配置中包含docserviceApiUrl
          if (response.data && !response.data.docserviceApiUrl) {
            response.data.docserviceApiUrl = documentServerUrl
          }
          
          // 3. 初始化编辑器
          if (window.DocsAPI) {
            if (docEditor) {
              docEditor.destroyEditor()
            }
            
            // 添加事件处理
            const config = {
              ...response.data,
              events: {
                onAppReady: () => {
                  console.log('编辑器应用已就绪')
                },
                onDocumentReady: () => {
                  console.log('文档加载完成')
                },
                onError: (event) => {
                  console.error('编辑器错误:', event)
                  error.value = '文档加载失败，请稍后重试'
                  loading.value = false
                }
              }
            }
            
            docEditor = new window.DocsAPI.DocEditor(editorContainer.value, config)
          } else {
            error.value = 'OnlyOffice API 加载失败'
          }
        } else {
          error.value = response.message || '获取版本信息失败'
        }
      } catch (err) {
        console.error('加载版本失败:', err)
        error.value = '加载版本失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }
    
    // 下载版本
    const downloadVersion = async () => {
      try {
        const response = await downloadDocumentVersion(versionId)
        
        // 创建下载链接
        const blob = new Blob([response])
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        
        // 设置文件名
        const fileName = `document_version_${versionId}.${version.value?.fileExt || 'docx'}`
        link.download = fileName
        
        // 触发下载
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        ElMessage.success('文档版本下载成功')
      } catch (err) {
        console.error('下载版本失败:', err)
        ElMessage.error('下载版本失败')
      }
    }
    
    onMounted(() => {
      // 检查必要参数
      if (!versionId) {
        ElMessage.error('无效的版本ID')
        router.push('/')
        return
      }
      
      loadVersion()
    })
    
    onBeforeUnmount(() => {
      if (docEditor) {
        try {
          docEditor.destroyEditor()
        } catch (e) {
          console.error('销毁编辑器失败:', e)
        }
      }
    })
    
    return {
      loading,
      error,
      version,
      editorContainer,
      goBack,
      loadVersion,
      downloadVersion
    }
  }
}
</script>

<style scoped>
.document-version-preview {
  padding: 20px;
}

.loading-container,
.error-container {
  margin-top: 20px;
  height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f6f6f6;
}

.editor-container {
  margin-top: 20px;
}

.document-editor-container {
  width: 100%;
  height: 800px;
  border: 1px solid #e0e0e0;
}
</style> 