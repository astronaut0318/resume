<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

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
          <li><router-link to="/ai">AI简历<span class="badge">免费</span></router-link></li>
          <li><router-link to="/expert">简历行家</router-link></li>
        </ul>
      </nav>
      <div class="user-actions">
        <template v-if="!isLoggedIn">
          <el-button link @click="handleLogin">登录</el-button>
          <span class="divider">/</span>
          <el-button link @click="handleRegister">注册</el-button>
        </template>
        <template v-else>
          <div class="user-menu">
            <el-dropdown>
              <span class="user-dropdown-link">
                <el-avatar :size="32" :src="userStore.avatar" />
                <span class="username">{{ userStore.username }}</span>
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="navigateTo('/resume')">我的简历</el-dropdown-item>
                  <el-dropdown-item @click="navigateTo('/orders')">我的订单</el-dropdown-item>
                  <el-dropdown-item @click="navigateTo('/account')">账户信息</el-dropdown-item>
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
}
</style> 