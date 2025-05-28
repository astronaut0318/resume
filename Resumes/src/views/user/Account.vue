<template>
  <div class="account-container">
    <div class="account-header">
      <h2>账户信息</h2>
      <p>您可以在这里查看和修改您的账户信息</p>
    </div>

    <div class="account-content">
      <div class="account-sidebar">
        <div class="user-avatar">
          <div class="avatar-wrapper">
            <el-avatar :size="80" :src="userInfo?.avatar">
              <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/>
            </el-avatar>
            <div class="avatar-overlay" @click="triggerUpload">
              <el-icon><Camera /></el-icon>
              <span>更换头像</span>
            </div>
          </div>
          <input 
            type="file" 
            ref="fileInput" 
            accept="image/*" 
            style="display: none"
            @change="handleAvatarChange"
          />
          <div class="user-name">{{ userInfo?.username }}</div>
          <div class="user-level">
            <el-tag v-if="userInfo?.role === 2" type="danger">管理员</el-tag>
            <el-tag v-else-if="userInfo?.role === 1" type="warning">VIP会员</el-tag>
            <el-tag v-else type="info">普通用户</el-tag>
          </div>
        </div>
        
        <div class="sidebar-menu">
          <div 
            class="menu-item" 
            :class="{ active: activeTab === 'basic' }" 
            @click="activeTab = 'basic'"
          >
            <el-icon><User /></el-icon>
            <span>基本信息</span>
          </div>
          <div 
            class="menu-item" 
            :class="{ active: activeTab === 'security' }" 
            @click="activeTab = 'security'"
          >
            <el-icon><Lock /></el-icon>
            <span>安全设置</span>
          </div>
          <div 
            class="menu-item" 
            :class="{ active: activeTab === 'vip' }" 
            @click="activeTab = 'vip'"
          >
            <el-icon><Star /></el-icon>
            <span>会员信息</span>
          </div>
        </div>
      </div>

      <div class="account-main">
        <!-- 基本信息 -->
        <div v-if="activeTab === 'basic'" class="tab-content">
          <h3 class="tab-title">基本信息</h3>
          
          <el-form 
            ref="formRef" 
            :model="form" 
            :rules="rules" 
            label-width="80px"
            class="account-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSubmit">保存修改</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 安全设置 -->
        <div v-if="activeTab === 'security'" class="tab-content">
          <h3 class="tab-title">安全设置</h3>
          
          <el-form 
            ref="passwordFormRef" 
            :model="passwordForm" 
            :rules="passwordRules" 
            label-width="100px"
            class="account-form"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                placeholder="请输入当前密码"
                show-password 
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码"
                show-password 
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入新密码"
                show-password 
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 会员信息 -->
        <div v-if="activeTab === 'vip'" class="tab-content">
          <h3 class="tab-title">会员信息</h3>
          
          <div class="vip-card">
            <div class="vip-info">
              <div class="vip-level">
                <div class="level-title">当前等级</div>
                <div class="level-value">
                  <el-tag v-if="userInfo?.role === 2" type="danger" size="large">终身会员</el-tag>
                  <el-tag v-else-if="userInfo?.role === 1" type="warning" size="large">月度会员</el-tag>
                  <el-tag v-else type="info" size="large">普通用户</el-tag>
                </div>
              </div>
              
              <div class="vip-expire" v-if="vipInfo?.isVip && userInfo?.role === 1">
                <div class="expire-title">到期时间</div>
                <div class="expire-value">{{ vipInfo?.endTime }}</div>
              </div>
              
              <div class="vip-expire" v-if="vipInfo?.isVip && userInfo?.role === 2">
                <div class="expire-title">会员状态</div>
                <div class="expire-value">永久有效</div>
              </div>
            </div>
            
            <div class="vip-actions" v-if="userInfo?.role !== 2">
              <el-button type="primary" @click="router.push('/vip')">
                {{ userInfo?.role === 1 ? '续费会员' : '开通会员' }}
              </el-button>
            </div>
            
            <div class="vip-actions" v-else>
              <el-tag type="success">您已是终身会员，无需续费</el-tag>
            </div>
          </div>
          
          <div class="vip-benefits">
            <h4>会员特权</h4>
            <ul>
              <li>
                <el-icon><Check /></el-icon>
                <span>使用所有付费模板</span>
              </li>
              <li>
                <el-icon><Check /></el-icon>
                <span>简历无限存储</span>
              </li>
              <li>
                <el-icon><Check /></el-icon>
                <span>AI简历优化</span>
              </li>
              <li>
                <el-icon><Check /></el-icon>
                <span>专家一对一指导</span>
              </li>
              <li>
                <el-icon><Check /></el-icon>
                <span>终身会员享受所有未来功能</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Star, Check, Camera } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const activeTab = ref('basic')
const fileInput = ref(null)

// 获取用户信息
const userInfo = computed(() => userStore.userInfo)
const vipInfo = computed(() => userStore.vipInfo)

// 表单相关
const formRef = ref(null)
const form = ref({
  username: '',
  email: '',
  phone: ''
})

// 修改密码表单
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 密码表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 初始化表单数据
const initFormData = () => {
  if (userInfo.value) {
    form.value.username = userInfo.value.username || ''
    form.value.email = userInfo.value.email || ''
    form.value.phone = userInfo.value.phone || ''
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 更新用户基本信息
    const basicInfo = {
      username: form.value.username,
      email: form.value.email,
      phone: form.value.phone
    }
    
    await userStore.updateInfo(basicInfo)
    ElMessage.success('信息更新成功')
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败')
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
    initFormData()
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    
    // 这里应该调用API修改密码
    ElMessage.success('密码修改成功')
    resetPasswordForm()
  } catch (error) {
    console.error('密码修改失败:', error)
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

// 触发文件选择框
const triggerUpload = () => {
  fileInput.value.click()
}

// 处理头像变更
const handleAvatarChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  // 验证文件大小（限制为2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  try {
    // 创建临时URL以预览图片
    const avatarUrl = URL.createObjectURL(file)
    
    // 这里应该调用API上传头像
    // 模拟上传成功
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 更新用户信息
    await userStore.updateInfo({
      avatar: avatarUrl
    })
    
    ElMessage.success('头像更新成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  } finally {
    // 清空文件输入，以便重复选择同一文件时也能触发change事件
    e.target.value = ''
  }
}

// 初始化
onMounted(() => {
  // 加载用户信息
  userStore.loadUserInfo()
  
  // 初始化表单数据
  initFormData()
})
</script>

<style scoped>
.account-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.account-header {
  margin-bottom: 24px;
}

.account-header h2 {
  font-size: 24px;
  font-weight: 500;
  margin: 0 0 8px;
  color: #333;
}

.account-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.account-content {
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.account-sidebar {
  width: 240px;
  border-right: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.user-avatar {
  padding: 24px;
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-wrapper {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
  font-size: 12px;
}

.avatar-overlay .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.user-name {
  margin-top: 12px;
  font-size: 16px;
  font-weight: 500;
}

.user-level {
  margin-top: 8px;
}

.sidebar-menu {
  padding: 16px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.menu-item:hover {
  background-color: #f5f7fa;
  color: #1890ff;
}

.menu-item.active {
  background-color: #ecf5ff;
  color: #1890ff;
  border-right: 2px solid #1890ff;
}

.menu-item .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.account-main {
  flex: 1;
  padding: 24px;
}

.tab-title {
  font-size: 18px;
  font-weight: 500;
  margin: 0 0 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.account-form {
  max-width: 500px;
}

.vip-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 24px;
}

.vip-info {
  display: flex;
  gap: 40px;
}

.level-title, .expire-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.level-value, .expire-value {
  font-size: 16px;
  font-weight: 500;
}

.vip-benefits {
  margin-top: 32px;
}

.vip-benefits h4 {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 16px;
}

.vip-benefits ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.vip-benefits li {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  color: #666;
}

.vip-benefits li .el-icon {
  color: #52c41a;
  margin-right: 8px;
}

@media (max-width: 768px) {
  .account-content {
    flex-direction: column;
  }
  
  .account-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .sidebar-menu {
    display: flex;
    padding: 0;
  }
  
  .menu-item {
    flex: 1;
    justify-content: center;
    padding: 12px;
  }
  
  .menu-item.active {
    border-right: none;
    border-bottom: 2px solid #1890ff;
  }
  
  .menu-item .el-icon {
    margin-right: 4px;
  }
  
  .vip-info {
    flex-direction: column;
    gap: 16px;
  }
  
  .vip-card {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}
</style> 