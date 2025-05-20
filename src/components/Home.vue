<template>
  <div class="home-container">
    <!-- 页面顶部导航栏 -->
    <div class="navbar">
      <!-- 左侧Logo区域 -->
      <div class="navbar-logo">
        <i class="fas fa-file-alt"></i>
        <span>智能简历在线编辑</span>
      </div>

      <!-- 中间菜单区域 -->
      <div class="navbar-menu">
        <router-link to="/" class="menu-item active">首页</router-link>
        <router-link to="/templates" class="menu-item">模板市场</router-link>
        <router-link to="/pricing" class="menu-item">价格方案</router-link>
        <router-link to="/about" class="menu-item">关于我们</router-link>
      </div>

      <!-- 右侧用户区域 -->
      <div class="navbar-user">
        <router-link to="/login" class="btn-login">登录</router-link>
        <span class="divider">|</span>
        <router-link to="/register" class="btn-register">注册</router-link>
      </div>
    </div>

    <!-- 页面主体 -->
    <div class="page-content">
      <!-- Hero区域 -->
      <div class="hero-section">
        <el-row :gutter="20">
          <el-col :span="16" :offset="4">
            <h1 class="hero-title">专业简历，一键生成</h1>
            <p class="hero-subtitle">智能简历工场提供各种专业模板，AI驱动的内容优化，帮助您脱颖而出</p>
            <div class="hero-actions">
              <router-link to="/register">
                <el-button type="primary" size="large">免费开始使用</el-button>
              </router-link>
              <router-link to="/prototypes">
                <el-button size="large">查看原型页面</el-button>
              </router-link>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 功能特性区域 -->
      <div class="features-section">
        <el-row>
          <el-col :span="24">
            <h2 class="section-title">为什么选择我们？</h2>
          </el-col>
        </el-row>
        <el-row :gutter="30">
          <el-col :xs="24" :sm="24" :md="8">
            <el-card class="feature-card">
              <template #header>
                <div class="feature-icon">
                  <i class="fas fa-paint-brush"></i>
                </div>
              </template>
              <h3>精美简历模板</h3>
              <p>多种专业设计的模板可供选择，涵盖各行各业需求</p>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :sm="24" :md="8">
            <el-card class="feature-card">
              <template #header>
                <div class="feature-icon">
                  <i class="fas fa-robot"></i>
                </div>
              </template>
              <h3>AI内容优化</h3>
              <p>智能分析简历内容，提供专业建议，提升简历质量</p>
            </el-card>
          </el-col>
          
          <el-col :xs="24" :sm="24" :md="8">
            <el-card class="feature-card">
              <template #header>
                <div class="feature-icon">
                  <i class="fas fa-cloud"></i>
                </div>
              </template>
              <h3>云端存储管理</h3>
              <p>随时在线编辑、保存和导出您的简历，方便快捷</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 页脚 -->
    <el-footer class="main-footer">
      <p>&copy; 2024 智能简历工场. 保留所有权利</p>
    </el-footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const activeIndex = ref('home');
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 导航栏样式 */
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  background-color: #409EFF;
  color: white;
  padding: 0 20px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
}

.navbar-logo i {
  margin-right: 10px;
}

.navbar-menu {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.menu-item {
  color: white;
  text-decoration: none;
  font-size: 16px;
  padding: 0 8px;
  height: 60px;
  line-height: 60px;
}

.menu-item:hover, .menu-item.active {
  background-color: rgba(255, 255, 255, 0.1);
}

.navbar-user {
  display: flex;
  align-items: center;
}

.btn-login, .btn-register {
  color: white;
  text-decoration: none;
  font-size: 14px;
}

.divider {
  margin: 0 8px;
  color: rgba(255, 255, 255, 0.7);
}

.page-content {
  flex: 1;
}

.hero-section {
  background-color: #ecf5ff;
  padding: 80px 0;
  text-align: center;
}

.hero-title {
  font-size: 42px;
  color: #303133;
  margin-bottom: 20px;
  font-weight: 600;
}

.hero-subtitle {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.features-section {
  padding: 60px 20px;
}

.section-title {
  text-align: center;
  font-size: 32px;
  margin-bottom: 40px;
  color: #303133;
}

.feature-card {
  margin-bottom: 20px;
  height: 100%;
}

.feature-icon {
  font-size: 40px;
  color: #409EFF;
  margin-bottom: 10px;
  text-align: center;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #303133;
  font-weight: 600;
}

.feature-card p {
  color: #606266;
  line-height: 1.6;
}

.main-footer {
  background-color: #f5f7fa;
  text-align: center;
  padding: 20px;
  color: #909399;
}

/* 媒体查询 */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    height: auto;
    padding: 10px;
  }
  
  .navbar-menu {
    margin: 10px 0;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .hero-actions .el-button {
    margin-bottom: 10px;
  }
}
</style> 