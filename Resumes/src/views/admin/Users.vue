<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getUsersList, updateUserStatusApi, updateUserRoleApi } from '../../api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

// 用户列表数据
const userList = ref([])
const total = ref(0)
const loading = ref(false)

// 搜索条件
const searchForm = reactive({
  username: '',
  email: '',
  role: '',
  status: ''
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10
})

// 角色选项
const roleOptions = [
  { label: '普通用户', value: 0 },
  { label: 'VIP用户', value: 1 },
  { label: '终身会员', value: 2 },
  { label: '管理员', value: 3 }
]

// 状态选项
const statusOptions = [
  { label: '禁用', value: 0 },
  { label: '正常', value: 1 }
]

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.page,
      size: pagination.size
    }
    
    const res = await getUsersList(params)
    if (res.code === 200) {
      userList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadUserList()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.page = 1
  loadUserList()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  loadUserList()
}

// 每页数量变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadUserList()
}

// 更新用户状态
const updateUserStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}用户 "${row.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await updateUserStatusApi(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${actionText}成功`)
      row.status = newStatus
    } else {
      ElMessage.error(res.message || `${actionText}失败`)
    }
  } catch (error) {
    console.error(`${actionText}用户失败:`, error)
  }
}

// 更新用户角色
const updateUserRole = async (row, role) => {
  if (row.role === role) return
  
  const roleName = roleOptions.find(option => option.value === role)?.label
  
  try {
    await ElMessageBox.confirm(
      `确定要将用户 "${row.username}" 的角色修改为 "${roleName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await updateUserRoleApi(row.id, role)
    if (res.code === 200) {
      ElMessage.success('角色修改成功')
      row.role = role
    } else {
      ElMessage.error(res.message || '角色修改失败')
    }
  } catch (error) {
    console.error('修改用户角色失败:', error)
  }
}

// 格式化角色
const formatRole = (row) => {
  const role = roleOptions.find(option => option.value === row.role)
  return role ? role.label : '未知'
}

// 格式化状态
const formatStatus = (row) => {
  const status = statusOptions.find(option => option.value === row.status)
  return status ? status.label : '未知'
}

onMounted(() => {
  loadUserList()
})
</script>

<template>
  <div class="admin-users">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
    </div>
    
    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 用户列表 -->
    <el-card class="list-card" shadow="never">
      <el-table :data="userList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        
        <el-table-column label="角色" width="150">
          <template #default="{ row }">
            <el-select v-model="row.role" @change="updateUserRole(row, row.role)">
              <el-option
                v-for="item in roleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ formatStatus(row) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="注册时间" />
        
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="updateUserStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.admin-users {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
}

.search-card {
  margin-bottom: 24px;
}

.list-card {
  margin-bottom: 24px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 