<script setup>
import { ref, onMounted, reactive } from 'vue'
import { createTemplateCategoryApi } from '../../api/admin'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 模板分类列表
const categoryList = ref([
  { id: 1, name: '简约风格', sort: 1 },
  { id: 2, name: '创意风格', sort: 2 },
  { id: 3, name: '商务风格', sort: 3 }
])
const loading = ref(false)
const dialogVisible = ref(false)

// 表单数据
const form = reactive({
  name: '',
  sort: 0
})

// 表单规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' },
    { type: 'number', message: '排序值必须为数字', trigger: 'blur' }
  ]
}

const formRef = ref(null)

// 加载模板分类列表
const loadCategoryList = () => {
  // 实际项目中，这里应该调用API获取分类列表
  // 这里使用模拟数据
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 打开新增对话框
const openAddDialog = () => {
  form.name = ''
  form.sort = categoryList.value.length + 1
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await createTemplateCategoryApi(form)
        if (res.code === 200) {
          ElMessage.success('创建成功')
          dialogVisible.value = false
          
          // 添加到列表中
          categoryList.value.push({
            id: res.data.id,
            name: form.name,
            sort: form.sort
          })
          
          // 按排序值排序
          categoryList.value.sort((a, b) => a.sort - b.sort)
        } else {
          ElMessage.error(res.message || '创建失败')
        }
      } catch (error) {
        console.error('创建模板分类失败:', error)
        ElMessage.error('创建失败，请稍后重试')
      }
    }
  })
}

onMounted(() => {
  loadCategoryList()
})
</script>

<template>
  <div class="template-categories">
    <div class="page-header">
      <h2 class="page-title">模板分类</h2>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">新增分类</el-button>
    </div>
    
    <!-- 分类列表 -->
    <el-card shadow="never" v-loading="loading">
      <el-table :data="categoryList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default>
            <el-button type="primary" link>编辑</el-button>
            <el-button type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增模板分类"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="1" :max="999" />
          <div class="form-tip">数字越小排序越靠前</div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.template-categories {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style> 