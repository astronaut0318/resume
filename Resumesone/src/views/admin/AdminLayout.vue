<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { Menu as IconMenu, User, Setting, Document, Grid } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 返回前台
const goToFrontend = () => {
  router.push('/')
}
</script>

<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="sidebar-header">
          <h2 v-if="!isCollapse">管理后台</h2>
          <el-icon v-else class="collapse-icon"><IconMenu /></el-icon>
          <el-icon class="toggle-icon" @click="toggleSidebar">
            <el-icon><IconMenu /></el-icon>
          </el-icon>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          :collapse="isCollapse"
          router
          background-color="#001529"
          text-color="#fff"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/admin">
            <el-icon><Grid /></el-icon>
            <template #title>控制台</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/template-categories">
            <el-icon><Document /></el-icon>
            <template #title>模板分类</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <el-header class="admin-header">
          <div class="header-left">
            <h3>在线简历管理后台</h3>
          </div>
          <div class="header-right">
            <el-button type="primary" link @click="goToFrontend">
              返回前台
            </el-button>
            <el-dropdown>
              <span class="user-dropdown">
                <el-avatar :size="32" :src="userStore.avatar" />
                <span>{{ userStore.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/account')">个人信息</el-dropdown-item>
                  <el-dropdown-item divided @click="userStore.logout()">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background-color: #001529;
  transition: width 0.3s;
  height: 100vh;
  overflow: hidden;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.collapse-icon {
  font-size: 20px;
}

.toggle-icon {
  cursor: pointer;
  font-size: 18px;
}

.sidebar-menu {
  border-right: none;
}

.admin-header {
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}
</style> 