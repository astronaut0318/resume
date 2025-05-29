<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Tickets, Document, Money } from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据
const statistics = ref({
  userCount: 0,
  resumeCount: 0,
  templateCount: 0,
  orderCount: 0
})

// 加载统计数据
const loadStatistics = () => {
  // 模拟数据加载
  setTimeout(() => {
    statistics.value = {
      userCount: 1250,
      resumeCount: 3560,
      templateCount: 128,
      orderCount: 680
    }
  }, 500)
}

onMounted(() => {
  loadStatistics()
})

// 跳转到对应页面
const navigateTo = (path) => {
  router.push(path)
}
</script>

<template>
  <div class="admin-dashboard">
    <h2 class="page-title">控制台</h2>
    
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card" shadow="hover" @click="navigateTo('/admin/users')">
        <div class="stat-content">
          <div class="stat-icon user">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon resume">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.resumeCount }}</div>
            <div class="stat-label">简历总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover" @click="navigateTo('/admin/template-categories')">
        <div class="stat-content">
          <div class="stat-icon template">
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.templateCount }}</div>
            <div class="stat-label">模板总数</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon order">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.orderCount }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 快捷入口 -->
    <h3 class="section-title">快捷入口</h3>
    <div class="quick-links">
      <el-button type="primary" @click="navigateTo('/admin/users')">用户管理</el-button>
      <el-button type="primary" @click="navigateTo('/admin/template-categories')">模板分类</el-button>
    </div>
    
    <!-- 系统信息 -->
    <h3 class="section-title">系统信息</h3>
    <el-card class="system-info" shadow="hover">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
        <el-descriptions-item label="Node版本">v16.0.0</el-descriptions-item>
        <el-descriptions-item label="Vue版本">v3.2.0</el-descriptions-item>
        <el-descriptions-item label="Element Plus">v2.3.0</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.page-title {
  margin-top: 0;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 500;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.stat-icon.user {
  background-color: #1890ff;
}

.stat-icon.resume {
  background-color: #52c41a;
}

.stat-icon.template {
  background-color: #722ed1;
}

.stat-icon.order {
  background-color: #fa8c16;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.section-title {
  margin-top: 32px;
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 500;
}

.quick-links {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
}

.system-info {
  margin-bottom: 32px;
}
</style> 