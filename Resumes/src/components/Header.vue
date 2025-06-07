<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown, Trophy } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import NotificationIcon from './notification/NotificationIcon.vue'
import { defaultResources } from '../utils/config'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isVip = computed(() => userStore.isVip)
const isAdmin = computed(() => userStore.isAdmin)
const vipLevel = computed(() => userStore.userInfo?.role || 0)

// 调试VIP状态
console.log('[Header] 当前用户VIP状态检查:', { 
  isVip: isVip.value, 
  vipLevel: vipLevel.value, 
  userRole: userStore.userInfo?.role,
  vipInfo: userStore.vipInfo 
})

// 处理头像加载错误
const handleAvatarError = () => {
  console.error('导航栏头像加载失败:', userStore.avatar)
  // 这里不显示错误消息，避免用户体验受到影响
}

// 定期检查VIP状态
let vipCheckInterval = null

const navigateTo = (path) => {
  router.push(path)
}

const handleLogin = () => {
  console.log('点击了登录按钮')
  router.push('/login')
}

const handleRegister = () => {
  console.log('点击了注册按钮')
  router.push('/register')
}

const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    ElMessage.error('退出失败')
  }
}

// 定期检查并刷新用户VIP状态
const setupVipStatusCheck = () => {
  if (vipCheckInterval) {
    clearInterval(vipCheckInterval)
  }
  
  // 每60秒检查一次VIP状态
  vipCheckInterval = setInterval(() => {
    if (isLoggedIn.value) {
      // 尝试从localStorage恢复状态，确保实时更新
      userStore.restoreFromLocalStorage()
    }
  }, 60000)
}

// 监听登录状态变化
watch(isLoggedIn, (newVal) => {
  if (newVal) {
    // 用户登录后，启动VIP状态检查
    setupVipStatusCheck()
  } else {
    // 用户登出后，清除检查
    if (vipCheckInterval) {
      clearInterval(vipCheckInterval)
    }
  }
})

onMounted(() => {
  // 组件挂载时，如果用户已登录，启动VIP状态检查
  if (isLoggedIn.value) {
    setupVipStatusCheck()
  }
  
  // 确保页面加载时从localStorage恢复状态
  userStore.restoreFromLocalStorage()
})
</script>

<template>
  <header class="site-header">
    <div class="header-container">
      <div class="logo" @click="navigateTo('/')">
        <h1>在线简历</h1>
      </div>
      <nav class="main-nav">
        <ul>
          <li><router-link to="/">首页</router-link></li>
          <li><router-link to="/templates">模板中心</router-link></li>
          <li><router-link to="/create">在线制作</router-link></li>
          <li v-if="isAdmin"><router-link to="/admin" class="admin-link">管理后台</router-link></li>
        </ul>
      </nav>
      <div class="user-actions">
        <template v-if="!isLoggedIn">
          <el-button link @click="handleLogin">登录</el-button>
          <span class="divider">/</span>
          <el-button link @click="handleRegister">注册</el-button>
        </template>
        <template v-else>
          <NotificationIcon />
          
          <div class="user-menu">
            <el-dropdown>
              <span class="user-dropdown-link">
                <el-avatar :size="32" :src="userStore.avatar" @error="handleAvatarError">
                  <!-- Fallback默认头像 -->
                  <img :src="defaultResources.avatar" />
                </el-avatar>
                <span class="username">
                  {{ userStore.username }}
                  <span v-if="isVip" class="vip-badge" :class="{ 'lifetime': vipLevel === 2 }">
                    <el-icon><Trophy /></el-icon>
                    <span>{{ vipLevel === 2 ? '终身会员' : '会员' }}</span>
                  </span>
                  <span v-if="isAdmin" class="admin-badge">
                    <span>管理员</span>
                  </span>
                </span>
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="navigateTo('/resume')">我的简历</el-dropdown-item>
                  <el-dropdown-item @click="navigateTo('/orders')">我的订单</el-dropdown-item>
                  <el-dropdown-item @click="navigateTo('/files')">文件管理</el-dropdown-item>
                  <el-dropdown-item @click="navigateTo('/notifications')">通知中心</el-dropdown-item>
                  <el-dropdown-item @click="navigateTo('/account')">账户信息</el-dropdown-item>
                  <el-dropdown-item v-if="!isVip" @click="navigateTo('/vip')">
                    <span style="color: #f5a623">开通会员</span>
                  </el-dropdown-item>
                  <el-dropdown-item v-else-if="vipLevel === 1" @click="navigateTo('/vip')">
                    <span style="color: #f5a623">升级会员</span>
                  </el-dropdown-item>
                  <el-dropdown-item v-if="isAdmin" divided @click="navigateTo('/admin')">
                    <span style="color: #1890ff">管理后台</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.site-header {
  width: 100%;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 15px;
  }

  .main-nav {
    display: none;
  }

  .logo h1 {
    font-size: 20px;
  }
}

.logo {
  cursor: pointer;
}

.logo h1 {
  font-size: 22px;
  font-weight: bold;
  color: #1890ff;
  margin: 0;
}

.main-nav ul {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
}

.main-nav li {
  margin: 0 15px;
}

.main-nav a {
  color: #333;
  font-size: 16px;
  transition: color 0.3s;
  position: relative;
}

.main-nav a.router-link-active {
  color: #1890ff;
  font-weight: bold;
}

.main-nav a:hover {
  color: #1890ff;
}

.admin-link {
  color: #1890ff !important;
  font-weight: bold;
}

.badge {
  position: absolute;
  top: -10px;
  right: -24px;
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 0 4px;
  border-radius: 10px;
  font-weight: normal;
}

.user-actions {
  display: flex;
  align-items: center;
}

.divider {
  margin: 0 8px;
  color: #ccc;
}

.user-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #333;
}

.user-dropdown-link:hover {
  color: #1890ff;
}

.username {
  font-size: 14px;
  margin-right: 4px;
  display: flex;
  align-items: center;
}

.vip-badge {
  display: inline-flex;
  align-items: center;
  background-color: #f5a623;
  color: #fff;
  border-radius: 4px;
  padding: 0 4px;
  font-size: 12px;
  margin-left: 6px;
  line-height: 1.5;
}

.vip-badge.lifetime {
  background-color: #722ed1;
}

.vip-badge .el-icon {
  margin-right: 2px;
  font-size: 12px;
}

.admin-badge {
  display: inline-flex;
  align-items: center;
  background-color: #1890ff;
  color: #fff;
  border-radius: 4px;
  padding: 0 4px;
  font-size: 12px;
  margin-left: 6px;
  line-height: 1.5;
}
</style> 