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
            <el-avatar 
              :size="80" 
              :src="avatarUrl"
              @error="handleAvatarError"
            >
              <img :src="defaultResources.avatar"/>
            </el-avatar>
            <div class="avatar-loading" v-if="profileLoading">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
            <div class="avatar-overlay" @click="triggerUpload" v-else>
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
            <el-tag v-if="userInfo?.role === 3" type="danger">管理员</el-tag>
            <el-tag v-else-if="userInfo?.role === 2" type="success">终身会员</el-tag>
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
          <div 
            class="menu-item" 
            :class="{ active: activeTab === 'notification' }" 
            @click="activeTab = 'notification'"
          >
            <el-icon><Bell /></el-icon>
            <span>通知设置</span>
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

        <!-- 通知设置 -->
        <div v-if="activeTab === 'notification'" class="tab-content">
          <h3 class="tab-title">通知设置</h3>
          
          <div class="notification-settings">
            <el-card class="notification-card">
              <template #header>
                <div class="card-header">
                  <span>测试通知功能</span>
                </div>
              </template>
              
              <p class="notification-desc">您可以通过点击下面的按钮来测试系统通知功能。</p>
              
              <div class="notification-actions">
                <el-button @click="sendTestNotification(1)" type="primary">
                  发送系统通知
                </el-button>
                <el-button @click="sendTestNotification(2)" type="success">
                  发送订单通知
                </el-button>
                <el-button @click="sendTestNotification(3)" type="warning">
                  发送其他通知
                </el-button>
              </div>
            </el-card>
            
            <el-card class="notification-card">
              <template #header>
                <div class="card-header">
                  <span>通知偏好设置</span>
                </div>
              </template>
              
              <div class="notification-preferences">
                <el-form label-position="left" label-width="120px">
                  <el-form-item label="系统通知">
                    <el-switch v-model="notificationPreferences.system" />
                  </el-form-item>
                  <el-form-item label="订单通知">
                    <el-switch v-model="notificationPreferences.order" />
                  </el-form-item>
                  <el-form-item label="邮件提醒">
                    <el-switch v-model="notificationPreferences.email" />
                  </el-form-item>
                </el-form>
                
                <div class="save-preferences">
                  <el-button type="primary" @click="saveNotificationPreferences">
                    保存设置
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useUserStore } from '../../stores/user'
import { useNotificationStore } from '../../stores/notification'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Lock, Star, Check, Camera, Bell, Loading } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { uploadFile } from '../../api/file'
import { getFileUrl, defaultResources } from '../../utils/config'
import { defaultAvatarBase64 } from '../../assets/defaultAvatar'

const userStore = useUserStore()
const notificationStore = useNotificationStore()
const router = useRouter()
const activeTab = ref('basic')
const fileInput = ref(null)
const profileLoading = ref(false)

// 获取用户信息
const userInfo = computed(() => userStore.userInfo)
const vipInfo = computed(() => userStore.vipInfo)

// 获取处理后的头像URL
const avatarUrl = computed(() => {
  if (!userInfo.value || !userInfo.value.avatar) {
    // 返回默认头像
    return defaultResources.avatar;
  }
  
  // 如果已经是完整URL则直接使用，否则构建URL
  if (userInfo.value.avatar.startsWith('http')) {
    return userInfo.value.avatar;
  } else {
    return getFileUrl(userInfo.value.avatar, 'thumbnail');
  }
})

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

// 通知设置
const notificationPreferences = reactive({
  system: true,
  order: true,
  email: false
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

// 压缩图片函数
const compressImage = (file) => {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (event) => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement('canvas');
        const MAX_WIDTH = 300;
        const MAX_HEIGHT = 300;
        let width = img.width;
        let height = img.height;
        
        if (width > height) {
          if (width > MAX_WIDTH) {
            height *= MAX_WIDTH / width;
            width = MAX_WIDTH;
          }
        } else {
          if (height > MAX_HEIGHT) {
            width *= MAX_HEIGHT / height;
            height = MAX_HEIGHT;
          }
        }
        
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob((blob) => {
          const compressedFile = new File([blob], file.name, { 
            type: 'image/jpeg', 
            lastModified: Date.now() 
          });
          console.log('图片压缩前后对比:', {
            before: `${Math.round(file.size / 1024)}KB`,
            after: `${Math.round(compressedFile.size / 1024)}KB`,
            ratio: `${Math.round((file.size - compressedFile.size) / file.size * 100)}%`
          });
          resolve(compressedFile);
        }, 'image/jpeg', 0.7);
      };
      img.src = event.target.result;
    };
    reader.readAsDataURL(file);
  });
};

// 处理头像变更
const handleAvatarChange = async (e) => {
  if (!e.target.files || !e.target.files.length) return
  
  const file = e.target.files[0]
  
  // 文件类型检查
  const acceptTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!acceptTypes.includes(file.type)) {
    ElMessage.error('请上传 JPG、PNG、GIF 或 WebP 格式的图片')
    return
  }
  
  // 文件大小检查(2MB)
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  profileLoading.value = true
  
  try {
    // 压缩图片
    const compressedFile = await compressImage(file);
    
    // 使用文件上传API
    console.log('准备上传头像文件:', { fileName: compressedFile.name, fileType: compressedFile.type, fileSize: compressedFile.size, type: 'avatar' })
    const response = await uploadFile(compressedFile, 'avatar')
    console.log('头像上传响应:', response)
    const { code, data, message } = response.data
    
    if (code === 200) {
      // 更新用户头像，使用fileUrl而不是filePath
      console.log('头像上传成功，更新用户信息:', data)
      const updateRes = await userStore.updateInfo({
        avatar: data.fileUrl || data.filePath
      })
      
      if (updateRes.code === 200) {
        ElMessage.success('头像更新成功')
        // 强制刷新用户信息，确保头像更新显示
        await userStore.loadUserInfo()
      }
    } else {
      ElMessage.error(message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请重试')
  } finally {
    profileLoading.value = false
  }
}

// 保存通知设置
const saveNotificationPreferences = () => {
  ElMessage.success('通知设置已保存')
}

// 发送测试通知
const sendTestNotification = (type) => {
  const notificationTypes = {
    1: { title: '系统通知测试', content: '这是一条系统通知测试，您可以在通知中心查看所有通知。', type: 1 },
    2: { title: '订单通知测试', content: '您的订单 TEST123456 已创建，请尽快完成支付。', type: 2 },
    3: { title: '其他通知测试', content: '这是一条其他类型的通知测试，感谢您使用我们的服务！', type: 3 }
  }
  
  const notification = notificationTypes[type]
  if (notification) {
    notificationStore.createNotification({
      id: Date.now(),
      title: notification.title,
      content: notification.content,
      type: notification.type,
      isRead: 0,
      createTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
    })
    
    ElMessage.success('测试通知已发送')
  }
}

// 处理头像加载错误
const handleAvatarError = () => {
  console.error('头像加载失败:', userInfo.value?.avatar);
  ElMessage.warning({
    message: '头像加载失败，已显示默认头像',
    duration: 2000,
    showClose: true,
    grouping: true,
  });
  // 不更新全局状态，只是在UI层面使用默认头像
}

// 触发文件选择框
const triggerUpload = () => {
  fileInput.value.click()
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

.notification-settings {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.notification-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-desc {
  color: #606266;
  margin-bottom: 20px;
}

.notification-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.notification-preferences {
  padding: 10px 0;
}

.save-preferences {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.avatar-loading {
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
  color: #fff;
  border-radius: 50%;
}

.avatar-loading .el-icon {
  font-size: 24px;
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
  
  .notification-actions {
    flex-direction: column;
  }
}
</style> 