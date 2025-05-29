<script setup>
import { onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useFileStore } from '../../stores/file'
import FileUploader from '../../components/FileUploader.vue'

const fileStore = useFileStore()

// 初始化
onMounted(() => {
  fileStore.getFileList()
})

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

// 处理文件上传成功
const handleUploadSuccess = () => {
  fileStore.getFileList()
}

// 处理文件删除
const handleDeleteFile = async (fileId) => {
  try {
    await ElMessageBox.confirm('确定要删除该文件吗？此操作不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await fileStore.deleteFile(fileId)
  } catch (error) {
    // 用户取消删除，不做处理
  }
}

// 处理文件下载
const handleDownloadFile = (fileId, fileName) => {
  fileStore.downloadFile(fileId, fileName)
}

// 处理页码变化
const handlePageChange = (page) => {
  fileStore.setPage(page)
}

// 处理文件类型过滤
const handleTypeChange = (type) => {
  fileStore.setFileType(type)
}
</script>

<template>
  <div class="file-manager-container">
    <div class="file-manager-header">
      <h2>文件管理</h2>
      <div class="actions">
        <el-select 
          v-model="fileStore.fileType" 
          placeholder="文件类型" 
          clearable
          @change="handleTypeChange"
        >
          <el-option label="头像" value="avatar" />
          <el-option label="简历" value="resume" />
          <el-option label="证书" value="certificate" />
          <el-option label="其他" value="other" />
        </el-select>
        
        <FileUploader
          @success="handleUploadSuccess"
          buttonText="上传文件"
        />
      </div>
    </div>
    
    <el-table
      :data="fileStore.fileList"
      style="width: 100%"
      v-loading="fileStore.loading"
      empty-text="暂无文件"
    >
      <el-table-column prop="originalName" label="文件名" min-width="200" />
      <el-table-column prop="fileType" label="文件类型" width="180" />
      <el-table-column label="文件大小" width="120">
        <template #default="{ row }">
          {{ formatFileSize(row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="上传时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            @click="handleDownloadFile(row.id, row.originalName)"
          >
            下载
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDeleteFile(row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container" v-if="fileStore.total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
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