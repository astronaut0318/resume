<template>
  <div class="document-editor-page">
    <div class="editor-header">
      <h1>文档编辑器示例</h1>
      <div class="editor-actions">
        <el-button type="primary" @click="showWelcomeMessage" :disabled="!editorReady">
          显示欢迎消息
        </el-button>
        <el-button type="info" @click="toggleVersions">
          {{ showVersionControl ? '隐藏版本控制' : '显示版本控制' }}
        </el-button>
      </div>
    </div>
    
    <DocumentEditor
      ref="documentEditor"
      :sourceType="sourceType"
      :sourceId="sourceId"
      :mode="mode"
      :showVersionControl="showVersionControl"
      @app-ready="onAppReady"
      @document-ready="onDocumentReady"
      @editor-instance="setEditorInstance"
    />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import DocumentEditor from '../components/DocumentEditor.vue'
import { useRoute } from 'vue-router'

export default {
  name: 'DocumentEditorExample',
  components: {
    DocumentEditor
  },
  setup() {
    const route = useRoute()
    const documentEditor = ref(null)
    const editorReady = ref(false)
    const editorInstance = ref(null)
    const showVersionControl = ref(false)
    
    // 从路由参数获取文档信息
    const sourceType = ref(route.params.sourceType || 'RESUME')
    const sourceId = ref(parseInt(route.params.sourceId) || 1)
    const mode = ref(route.query.mode || 'edit')
    
    const onAppReady = () => {
      console.log('OnlyOffice 应用已就绪')
    }
    
    const onDocumentReady = () => {
      console.log('文档已就绪')
      editorReady.value = true
    }
    
    const setEditorInstance = (instance) => {
      console.log('获取了编辑器实例')
      editorInstance.value = instance
    }
    
    const showWelcomeMessage = () => {
      if (documentEditor.value) {
        documentEditor.value.showMessage('欢迎使用OnlyOffice编辑器！')
      }
    }
    
    const toggleVersions = () => {
      showVersionControl.value = !showVersionControl.value
    }
    
    // 可以添加更多使用编辑器方法的函数
    const executeCustomMethod = (methodName, ...args) => {
      if (documentEditor.value) {
        const method = documentEditor.value.getEditorMethod(methodName)
        if (method) {
          return method(...args)
        }
      }
      return null
    }
    
    onMounted(() => {
      // 页面加载完成后的初始化逻辑
    })
    
    return {
      documentEditor,
      editorReady,
      sourceType,
      sourceId,
      mode,
      showVersionControl,
      onAppReady,
      onDocumentReady,
      setEditorInstance,
      showWelcomeMessage,
      toggleVersions,
      executeCustomMethod
    }
  }
}
</script>

<style scoped>
.document-editor-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.editor-actions {
  display: flex;
  gap: 10px;
}
</style> 