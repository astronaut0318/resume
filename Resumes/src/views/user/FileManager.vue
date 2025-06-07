<script setup>
import { onMounted, ref, watch, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useFileStore } from '../../stores/file'
import FileUploader from '../../components/FileUploader.vue'
import { RefreshRight, Upload } from '@element-plus/icons-vue'
import axios from 'axios'

const fileStore = useFileStore()
const fileType = ref('resume')  // 固定为简历类型
const showFileUploader = ref(false)
const currentFileType = ref('resume')

// 添加fileTypeText计算属性
const fileTypeText = computed(() => {
  switch (currentFileType.value) {
    case 'image':
      return '图片';
    case 'template':
    default:
      return '简历';
  }
});

// 初始化
onMounted(() => {
  console.log('FileManager组件已挂载，正在获取文件列表...')
  loadFileList()
})

// 加载文件列表 - 获取所有文件，不再按类型筛选
const loadFileList = () => {
  const params = {
    page: fileStore.currentPage,
    size: 10, // 每页显示10条记录
    _timestamp: Date.now() // 防止缓存
  }
  // 移除type参数，查询所有类型的文件
  console.log('正在加载文件列表，不应用type过滤，参数:', params)
  fileStore.getFileList(params)
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  }
}

// 格式化文件类型
const formatFileType = (type) => {
  switch (type) {
    case 'thumbnail':
      return '图片';
    case 'template':
      return '简历';
    default:
      return type || '未知';
  }
}

// 处理文件上传成功
const handleUploadSuccess = (data) => {
  console.log('文件上传成功，正在刷新列表...', data)
  
  // 显示成功消息
  ElMessage.success('上传成功')
  
  // 立即更新文件列表状态（如果上传响应包含文件信息）
  if (data) {
    try {
      // 处理上传响应 - FileUploadVO格式: {fileId, fileName, filePath, fileSize, fileType}
      // 转换为文件列表项格式: {id, originalName, fileName, filePath, fileSize, fileType, createTime}
      const newFile = {
        id: data.fileId, // FileUploadVO中是fileId
        originalName: data.fileName, // 使用fileName作为原始名
        fileName: data.fileName,
        filePath: data.filePath || '',
        fileSize: data.fileSize || 0,
        fileType: data.fileType || 'resume', // 使用响应中的文件类型，默认为resume
        createTime: new Date().toLocaleString() // 上传响应没有时间，使用当前时间
      }
      
      console.log('添加新上传的文件到列表:', newFile)
      
      // 添加到列表前端 - 这是本地状态更新，不依赖后端刷新
      if (Array.isArray(fileStore.fileList)) {
        // 确保新文件在前面显示
        fileStore.fileList.unshift(newFile)
        
        // 更新总数
        fileStore.total = fileStore.total + 1
        
        // 如果列表为空，则解决"暂无文件"问题
        if (fileStore.fileList.length === 1) {
          setTimeout(() => {
            // 触发视图更新
            fileStore.fileList = [...fileStore.fileList]
          }, 0)
        }
      }
      
      // 先本地更新，同时在后台从服务器获取完整列表
      setTimeout(() => {
        loadFileList() // 延迟加载最新列表，确保服务器已处理完上传
      }, 2000)
    } catch (err) {
      console.error('处理上传文件信息失败:', err)
      
      // 如果处理出错，尝试刷新列表
      setTimeout(() => {
        loadFileList()
      }, 1000)
    }
  } else {
    // 上传响应为空，刷新列表
    setTimeout(() => {
      loadFileList()
    }, 1000)
  }
}

// 使用超链接形式进行删除的处理函数
const handleAlternativeDelete = async (fileId) => {
  try {
    // 用户确认删除
    await ElMessageBox.confirm('确定要删除该文件吗？此操作不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 显示加载提示
    const loadingMsg = ElMessage({
      message: '正在删除文件...',
      type: 'info',
      duration: 0
    });
    
    try {
      console.log(`尝试使用标准DELETE方法删除文件ID: ${fileId}`);
      
      // 使用axios直接发送DELETE请求，确保URL路径正确
      const response = await axios.delete(`/files/de/${fileId}`);
      
      console.log('删除文件响应:', response);
      loadingMsg.close();
      
      // 处理响应
      if (response.data && response.data.code === 200) {
        // 删除成功
        ElMessage.success('文件删除成功');
        
        // 从列表中移除该文件
        const index = fileStore.fileList.findIndex(file => file.id === fileId);
        if (index !== -1) {
          fileStore.fileList.splice(index, 1);
          fileStore.total = Math.max(0, fileStore.total - 1);
        }
      } else {
        // 删除失败
        ElMessage.error(response.data?.message || '删除失败');
      }
      
      // 刷新列表
      setTimeout(() => loadFileList(), 500);
      
    } catch (error) {
      loadingMsg.close();
      console.error('删除文件失败:', error);
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message || '未知错误'));
      
      // 刷新列表
      loadFileList();
    }
  } catch (error) {
    // 用户取消操作
    console.log('用户取消删除操作');
  }
};

// 处理文件删除
const handleDeleteFile = async (fileId) => {
  try {
    // 用户确认删除操作
    await ElMessageBox.confirm('确定要删除该文件吗？此操作不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 显示删除中的提示
    const loadingMsg = ElMessage({
      message: '正在删除文件...',
      type: 'info',
      duration: 0
    });
    
    try {
      console.log('开始删除文件:', fileId);
      
      // 添加网络请求调试信息
      const deleteStartTime = Date.now();
      console.log(`[${new Date().toLocaleTimeString()}] 发起删除请求: ID=${fileId}`);
      
      const result = await fileStore.deleteFile(fileId)
      
      // 日志记录删除请求完成时间
      console.log(`[${new Date().toLocaleTimeString()}] 删除请求完成: 耗时=${Date.now() - deleteStartTime}ms`);
      
      // 关闭加载提示
      loadingMsg.close();
      
      // 检查删除结果
      if (result.success) {
        console.log('文件删除API成功:', result);
        ElMessage.success(result.message || '删除成功');
        
        // 删除成功直接更新本地状态
        const index = fileStore.fileList.findIndex(file => file.id === fileId)
        if (index !== -1) {
          console.log(`从本地列表移除文件 ID: ${fileId}`);
          fileStore.fileList.splice(index, 1)
          fileStore.total = Math.max(0, fileStore.total - 1)
        }
      } else {
        console.warn('文件删除失败，错误信息:', result.message);
        ElMessage.error(result.message || '删除失败');
      }
      
      // 无论成功失败都刷新列表以确保与后端同步
      console.log('刷新文件列表');
      setTimeout(() => loadFileList(), 500);
      
    } catch (deleteError) {
      // 关闭加载提示
      loadingMsg.close();
      console.error('文件删除操作异常:', deleteError);
      
      // 打印更详细的错误信息
      if (deleteError.stack) {
        console.error('错误堆栈:', deleteError.stack);
      }
      
      // 显示更友好的错误提示
      ElMessage.error(deleteError.message || '删除操作失败，请稍后再试');
      
      // 刷新文件列表
      loadFileList();
    }
  } catch (error) {
    // 用户取消删除，不做处理
    console.log('用户取消删除操作');
  }
}

// 处理文件下载
const handleDownloadFile = (fileId, fileName) => {
  fileStore.downloadFile(fileId, fileName)
}

// 处理页码变化
const handlePageChange = (page) => {
  fileStore.setPage(page)
  loadFileList() // 重新加载当前页
}

// 处理文件类型变化
const handleFileTypeChange = (type) => {
  currentFileType.value = type
  showFileUploader.value = true
}
</script>

<template>
  <div class="file-manager-container">
    <div class="file-manager-header">
      <h2>文件管理</h2>
      <div class="actions">
        <el-dropdown trigger="click" @command="handleFileTypeChange">
          <el-button type="primary" class="upload-btn">
            <el-icon><Upload /></el-icon>
            上传文件
            <el-icon><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="image">上传图片</el-dropdown-item>
              <el-dropdown-item command="template">上传简历</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <!-- 文件上传组件，根据选择的文件类型显示 -->
        <FileUploader
          v-if="showFileUploader"
          :fileType="currentFileType"
          @success="handleUploadSuccess"
          :buttonText="`上传${fileTypeText}`"
          @error="() => showFileUploader = false"
        />
        
        <el-button type="primary" @click="loadFileList" :loading="fileStore.loading" plain>
          <el-icon><RefreshRight /></el-icon> 刷新列表
        </el-button>
      </div>
    </div>
    
    <el-table
      :data="fileStore.fileList"
      style="width: 100%"
      v-loading="fileStore.loading"
      empty-text="暂无文件"
    >
      <el-table-column prop="originalName" label="文件名" min-width="180" show-overflow-tooltip />
      <el-table-column label="类型" width="80">
        <template #default="{ row }">
          {{ formatFileType(row.fileType) }}
        </template>
      </el-table-column>
      <el-table-column label="文件大小" width="100">
        <template #default="{ row }">
          {{ formatFileSize(row.fileSize || 0) }}
        </template>
      </el-table-column>
      <el-table-column label="上传时间" width="180">
        <template #default="{ row }">
          {{ row.createTime ? new Date(row.createTime).toLocaleString() : '' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="handleDownloadFile(row.id, row.originalName)"
          >
            下载
          </el-button>
          <!-- 删除按钮 -->
          <a
            href="#"
            class="el-button el-button--small el-button--danger"
            style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;"
            @click.prevent="handleAlternativeDelete(row.id)"
          >
            删除
          </a>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container" v-if="fileStore.total > 0">
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="fileStore.total"
        :current-page="fileStore.currentPage"
        :page-size="fileStore.pageSize"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
.file-manager-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.file-manager-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 