<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadFile } from '../api/file'

const props = defineProps({
  // 文件类型
  fileType: {
    type: String,
    default: ''
  },
  // 文件大小限制(MB)
  maxSize: {
    type: Number,
    default: 5
  },
  // 允许的文件类型
  accept: {
    type: String,
    default: '*'
  },
  // 按钮文本
  buttonText: {
    type: String,
    default: '上传文件'
  },
  // 按钮类型
  buttonType: {
    type: String,
    default: 'primary'
  },
  // 按钮大小
  buttonSize: {
    type: String,
    default: 'default'
  }
})

const emit = defineEmits(['success', 'error'])
const uploading = ref(false)

// 上传文件
const handleUpload = async (file) => {
  // 文件大小验证
  if (file.size > props.maxSize * 1024 * 1024) {
    ElMessage.error(`文件大小不能超过${props.maxSize}MB`)
    return false
  }
  
  uploading.value = true
  try {
    const response = await uploadFile(file, props.fileType)
    const { code, message, data } = response.data
    
    if (code === 200) {
      ElMessage.success('上传成功')
      emit('success', data)
    } else {
      ElMessage.error(message || '上传失败')
      emit('error', response.data)
    }
    return true
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败，请重试')
    emit('error', error)
    return false
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <el-upload
    class="file-uploader"
    :accept="accept"
    :multiple="false"
    :auto-upload="false"
    :show-file-list="false"
    :http-request="handleUpload"
  >
    <el-button
      :type="buttonType"
      :size="buttonSize"
      :loading="uploading"
    >
      <el-icon><Upload /></el-icon>
      <span>{{ buttonText }}</span>
    </el-button>
    <template #tip>
      <div class="el-upload__tip">
        <slot name="tip">
          支持大小不超过{{ maxSize }}MB的文件
        </slot>
      </div>
    </template>
  </el-upload>
</template>

<style scoped>
.file-uploader {
  display: inline-block;
}
</style> 