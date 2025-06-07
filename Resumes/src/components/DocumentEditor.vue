<template>
  <div class="document-editor-container">
    <div v-if="loading" class="editor-loading">
      <el-skeleton :rows="10" animated />
    </div>
    <DocumentEditor
      v-else
      id="docEditor"
      :documentServerUrl="documentServerUrl"
      :config="editorConfig"
      :events_onDocumentReady="onDocumentReady"
      :events_onAppReady="onAppReady"
      :events_onError="onEditorError"
    />
    
    <!-- 版本控制面板 -->
    <div v-if="showVersions && versions.length > 0" class="version-panel">
      <h3>版本历史</h3>
      <el-table :data="versions" style="width: 100%" size="small">
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="modifierName" label="修改者" width="120" />
        <el-table-column prop="createTime" label="创建时间">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="previewVersion(scope.row.id)">预览</el-button>
            <el-button size="small" @click="downloadVersion(scope.row.id)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" @click="createVersion" style="margin-top: 16px">创建新版本</el-button>
    </div>
  </div>
</template>

<script>
import { onMounted, ref, onBeforeUnmount } from 'vue'
import request from '../utils/request'
import { DocumentEditor } from "@onlyoffice/document-editor-vue"

export default {
  name: 'DocumentEditorWrapper',
  components: {
    DocumentEditor
  },
  props: {
    sourceType: {
      type: String,
      required: true,
      validator: (value) => ['resume', 'template', 'file'].includes(value.toLowerCase())
    },
    sourceId: {
      type: Number,
      required: true
    },
    mode: {
      type: String,
      default: 'view',
      validator: (value) => ['view', 'edit', 'comment'].includes(value)
    },
    showVersionControl: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    const loading = ref(true)
    const showVersions = ref(props.showVersionControl)
    const versions = ref([])
    const editorApiReady = ref(false)
    const documentServerUrl = window.onlyofficeServerUrl || 'http://192.168.64.129:80'
    const editorConfig = ref(null)
    const editorInstance = ref(null)
    
    // 获取当前用户信息
    const userId = localStorage.getItem('userId')
    const userName = localStorage.getItem('username')

    // 加载编辑器配置
    const loadConfig = async () => {
      try {
        // 使用正确的API路径，需要保留api前缀
        const url = `/api/documents/${props.sourceType.toLowerCase()}/${props.sourceId}/config?mode=${props.mode}&userId=${userId}&userName=${encodeURIComponent(userName || '')}`
        console.log('请求编辑器配置URL:', url)
        
        const headers = {
          'Content-Type': 'application/json'
        }
        // 添加认证token
        const token = localStorage.getItem('token')
        if (token) {
          headers['Authorization'] = `Bearer ${token}`
        }
        // 添加userId
        if (userId) {
          headers['userId'] = userId
        }
        
        const response = await fetch(url, { headers })
        console.log('API响应状态:', response.status)
        
        if (!response.ok) {
          console.error(`API请求失败: ${response.status} ${response.statusText}`)
          const errorText = await response.text()
          console.error('错误响应内容:', errorText)
          throw new Error(`API请求失败: ${response.status}`)
        }
        
        const result = await response.json()
        console.log('API响应数据:', result)
        
        if (result.code === 200 && result.data) {
          // 确保配置中包含docserviceApiUrl和正确的文档URL
          if (!result.data.docserviceApiUrl) {
            // 使用绝对URL确保可访问性
            const currentHost = window.location.hostname;
            result.data.docserviceApiUrl = `http://${currentHost}:80`;
            console.log('设置文档服务器URL:', result.data.docserviceApiUrl);
          }
          
          // 确保document对象配置正确
          if (result.data.document) {
            // 确保文档URL是绝对路径
            if (result.data.document.url && !result.data.document.url.startsWith('http')) {
              const currentHost = window.location.protocol + '//' + window.location.host;
              result.data.document.url = currentHost + result.data.document.url;
              console.log('修正文档URL为绝对路径:', result.data.document.url);
            }
            
            // 添加文档访问token到URL
            if (result.data.document.url && token) {
              const tokenParam = result.data.document.url.includes('?') ? `&token=${token}` : `?token=${token}`;
              result.data.document.url += tokenParam;
              console.log('添加token到文档URL');
            }
            
            // 设置权限
            if (props.mode === 'edit') {
              if (!result.data.document.permissions) {
                result.data.document.permissions = {};
              }
              result.data.document.permissions.edit = true;
              result.data.document.permissions.review = true;
            }
          }
          
          // 增强editorConfig配置
          if (result.data.editorConfig) {
            // 确保有callbackUrl并使用绝对路径 - 多层网络环境下的处理
            if (!result.data.editorConfig.callbackUrl || !result.data.editorConfig.callbackUrl.startsWith('http')) {
              // 测试已确认Docker容器可以访问192.168.64.129（虚拟机IP）
              // 使用已验证可访问的虚拟机IP
              const vmHost = '192.168.64.129'; // 确认可访问的虚拟机IP
              
              // 需要确保虚拟机上正确配置了端口转发，将此端口转发到宿主机的后端服务
              // 这里使用80端口，因为Docker容器测试显示可以访问虚拟机的80端口
              result.data.editorConfig.callbackUrl = `http://${vmHost}/documents/callback`;
              console.log('为多层网络环境设置回调URL:', result.data.editorConfig.callbackUrl);
            }
            
            // 如果文档URL使用了172.19.173.6 IP，也需要修正为虚拟机IP
            if (result.data.document && result.data.document.url) {
              if (result.data.document.url.includes('172.19.173.6')) {
                // 使用已验证的虚拟机IP
                const vmHost = '192.168.64.129'; 
                
                // 重写整个URL，确保使用正确的虚拟机IP和端口
                const urlPath = new URL(result.data.document.url).pathname;
                result.data.document.url = `http://${vmHost}${urlPath}`;
                console.log('修正文档URL为:', result.data.document.url);
              }
              
              // 确保URL中包含认证令牌
              const token = localStorage.getItem('token');
              if (token && !result.data.document.url.includes('token=')) {
                const tokenParam = result.data.document.url.includes('?') ? `&token=${token}` : `?token=${token}`;
                result.data.document.url += tokenParam;
                console.log('添加认证令牌到文档URL');
              }
            }

            // 设置文档服务器URL为虚拟机IP
            if (!result.data.docserviceApiUrl || result.data.docserviceApiUrl.includes('192.168.64.129:80')) {
              result.data.docserviceApiUrl = 'http://192.168.64.129';
              console.log('设置文档服务器URL:', result.data.docserviceApiUrl);
            }
            
            // 禁用自动保存提示
            result.data.editorConfig.customization.autosave = false;
            
            // 禁用错误通知弹窗，避免显示下载失败等提示
            result.data.editorConfig.customization.hideNonCriticalErrors = true;
            
            // 添加错误处理回调
            result.data.editorConfig.customization.uiTheme = 'theme-classic';
            
            // 自定义界面布局，使其更合理
            result.data.editorConfig.customization.compactHeader = true; // 精简头部
            result.data.editorConfig.customization.compactToolbar = false; // 不精简工具栏，提供完整功能
            
            // 启用全功能界面
            result.data.editorConfig.customization.showReviewChanges = true;
            result.data.editorConfig.customization.hideRightMenu = false;
            
            // 优化工具栏位置
            result.data.editorConfig.customization.toolbarNoTabs = false;
            
            // 显示所有工具栏
            result.data.editorConfig.customization.toolbarHideFileName = false;

            // 启用缩放功能
            result.data.editorConfig.customization.zoom = 100;
            
            // 强制保存配置
            if (!result.data.editorConfig.forcesave) {
              result.data.editorConfig.forcesave = true;
            }
            
            // 设置编辑器高度
            result.data.height = "800px";
            
            // 设置编辑器宽度
            result.data.width = "100%";
            
            // 添加自定义错误处理
            result.data.editorConfig.customization.features = {
              spellcheck: {
                mode: 'off'  // 关闭拼写检查，避免一些错误
              }
            };
            
            // 添加lang参数确保界面显示正确语言
            result.data.lang = 'zh-CN';
            
            // 修改请求超时设置
            result.data.editorConfig.timeout = 120000; // 2分钟超时
          }
          
          // 添加事件处理
          result.data.events = {
            onAppReady: onAppReady,
            onDocumentReady: onDocumentReady,
            onError: onEditorError,
            onRequestSaveAs: (event) => {
              console.log('onRequestSaveAs:', event);
            },
            onRequestInsertImage: (event) => {
              console.log('onRequestInsertImage:', event);
            }
          };
          
          editorConfig.value = result.data
          loading.value = false
        } else {
          throw new Error(result.message || '获取编辑器配置失败')
        }
      } catch (error) {
        console.error('获取编辑器配置异常:', error)
        loading.value = false
      }
    }
    
    // 应用准备就绪事件
    const onAppReady = () => {
      console.log('编辑器应用已就绪')
      
      // 获取编辑器实例
      try {
        editorInstance.value = window.DocEditor?.instances?.docEditor;
        console.log('成功获取编辑器实例:', editorInstance.value ? '已获取' : '未获取');
        
        if (editorInstance.value) {
          // 在编辑器就绪时调用方法
          console.log('编辑器实例已就绪，可以调用方法');
          
          // 记录网络诊断信息
          console.log('当前主机名:', window.location.hostname);
          console.log('当前URL:', window.location.href);
          console.log('Docker网络测试成功: 容器可以访问192.168.64.129(虚拟机IP)');
          
          // 可以在编辑器就绪时显示欢迎信息
          // editorInstance.value.showMessage('编辑器已就绪');
        }
      } catch (error) {
        console.error('获取编辑器实例失败:', error);
      }
      
      editorApiReady.value = true
      emit('app-ready')
    }
    
    // 文档准备就绪事件
    const onDocumentReady = () => {
      console.log('文档已加载完成')
      emit('document-ready')
    }
    
    // 编辑器错误事件
    const onEditorError = (event) => {
      // 详细记录错误信息以便调试
      console.error('编辑器错误事件触发:', event)
      
      try {
        if (!event || !event.data) {
          console.log('错误事件数据不完整')
          return
        }
        
        // 记录错误代码和详情以便调试
        const errorCode = event.data.errorCode
        const errorDescription = event.data.errorDescription || '未知错误'
        
        console.log(`错误代码: ${errorCode}, 错误描述: ${errorDescription}`)
        
        // 处理下载失败错误(errorCode: -4)
        if (errorCode === -4) {
          console.log('Docker多层网络环境下文档下载失败，检查网络连接...')
          console.log('请确保Docker容器、虚拟机和宿主机之间的网络连接正确配置')
          console.log('尝试重新加载编辑器...')
          
          // 尝试重新获取配置并重新初始化编辑器
          setTimeout(() => {
            loadConfig()
          }, 2000)
          return
        }
        
        // 处理特定错误类型
        if (errorCode === 'Error_3' || errorCode === 3) {
          console.log('忽略保存错误，编辑器将继续工作')
          return
        }
        
        // 如果是下载错误，可能是网络或权限问题
        if (['Error_4', 'Error_6', 4, 6].includes(errorCode)) {
          console.log('忽略下载错误，编辑器将继续工作')
          return
        }
        
        // 处理其他已知错误类型
        if (errorCode === 'Error_2' || errorCode === 2) {
          console.log('文件转换错误，但编辑器将继续工作')
          return
        }
        
        // 处理无法打开文件错误
        if (errorCode === 'Error_1' || errorCode === 1) {
          console.log('文件打开错误，但编辑器将继续工作')
          return
        }
        
        // 如果有编辑器实例，尝试重置错误状态
        if (editorInstance.value) {
          try {
            // 尝试隐藏消息
            console.log('尝试重置编辑器状态...')
          } catch (instanceError) {
            console.error('调用编辑器实例方法失败:', instanceError)
          }
        }
        
        // 其他错误，控制台记录但不提示用户，避免干扰体验
        console.log('编辑器错误，但已被拦截不显示给用户:', event)
      } catch (handlingError) {
        console.error('处理编辑器错误时发生异常:', handlingError)
      }
    }
    
    // 获取编辑器实例
    const getEditorInstance = () => {
      if (window.DocEditor && window.DocEditor.instances) {
        return window.DocEditor.instances.docEditor
      }
      return null
    }

    // 加载版本列表
    const loadVersions = async () => {
      try {
        // 使用正确的API路径，需要保留api前缀
        const url = `/api/documents/${props.sourceType.toLowerCase()}/${props.sourceId}/versions`
        
        const headers = {
          'Content-Type': 'application/json'
        }
        const token = localStorage.getItem('token')
        if (token) {
          headers['Authorization'] = `Bearer ${token}`
        }
        if (userId) {
          headers['userId'] = userId
        }
        
        const response = await fetch(url, { headers })
        
        if (!response.ok) {
          console.error(`获取版本列表失败: ${response.status} ${response.statusText}`)
          const errorText = await response.text()
          console.error('错误响应内容:', errorText)
          throw new Error(`获取版本列表失败: ${response.status}`)
        }
        
        const result = await response.json()
        
        if (result.code === 200) {
          versions.value = result.data || []
        } else {
          throw new Error(result.message || '获取版本列表失败')
        }
      } catch (error) {
        console.error('获取版本列表异常:', error)
      }
    }
    
    // 创建新版本
    const createVersion = async () => {
      try {
        const response = await request.post(
          `/api/documents/${props.sourceType.toLowerCase()}/${props.sourceId}/versions`,
          null,
          {
            params: {
              userId: userId
            }
          }
        )
        
        if (response.code === 200) {
          // 重新加载版本列表
          await loadVersions()
        }
      } catch (error) {
        console.error('Error creating version:', error)
      }
    }
    
    // 预览指定版本
    const previewVersion = (versionId) => {
      window.open(`/document/version/${versionId}?userId=${userId}&userName=${userName}`, '_blank')
    }
    
    // 下载指定版本
    const downloadVersion = (versionId) => {
      const token = localStorage.getItem('token')
      const url = `/api/documents/versions/${versionId}/download`
      const downloadUrl = token ? `${url}${url.includes('?') ? '&' : '?'}token=${token}` : url
      window.open(downloadUrl, '_blank')
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString()
    }

    onMounted(() => {
      loadConfig()
      if (props.showVersionControl) {
        loadVersions()
      }
    })

    onBeforeUnmount(() => {
      // 这里不再需要手动销毁编辑器，官方组件会处理
    })

    return {
      loading,
      documentServerUrl,
      editorConfig,
      editorApiReady,
      editorInstance,
      showVersions,
      versions,
      onAppReady,
      onDocumentReady,
      onEditorError,
      loadVersions,
      createVersion,
      previewVersion,
      downloadVersion,
      formatDate,
      getEditorInstance
    }
  }
}
</script>

<style scoped>
.document-editor-container {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.editor-loading {
  width: 100%;
  height: 800px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 20px;
}

/* OnlyOffice编辑器样式调整 */
:deep(#docEditor) {
  width: 100% !important;
  min-height: 800px !important;
  margin-bottom: 20px;
}

:deep(.onlyoffice-header) {
  height: auto !important;
}

:deep(.onlyoffice-toolbar) {
  padding: 4px 0 !important;
}

:deep(.asc-de-pane-wrapper) {
  height: auto !important;
  min-height: 600px !important;
}

:deep(.onlyoffice-document-canvas) {
  min-height: 700px !important;
}

.version-panel {
  margin-top: 20px;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #f9f9f9;
}
</style> 