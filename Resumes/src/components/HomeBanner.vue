<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Setting, School, Cloudy } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)

const goToCreate = () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再使用此功能')
    router.push('/login')
    return
  }
  router.push('/create')
}
</script>

<template>
  <section class="hero-banner">
    <div class="banner-container">
      <div class="banner-content">
        <h1 class="banner-title">职场新机遇，从一份简历开始</h1>
        <p class="banner-subtitle">超适合求职的简历设计，1000+海量参考案例，超高求职通过率的用户反馈</p>
        <el-button type="primary" size="large" class="create-btn" @click="goToCreate">
<<<<<<< HEAD
          简历免费在线制作
=======
          简历在线制作
>>>>>>> upstream/master
        </el-button>
        
        <div class="feature-cards">
          <div class="feature-card">
            <div class="feature-icon">
              <span class="feature-number">3000+</span>
            </div>
            <p class="feature-desc">1000+模板范例，五百强HR共同建设</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><setting /></el-icon>
            </div>
            <p class="feature-desc">智能诊断纠错，一键排版美化</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><school /></el-icon>
            </div>
            <p class="feature-desc">简单高效便捷，数据安全可靠</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><cloudy /></el-icon>
            </div>
            <p class="feature-desc">实时保持，多端同步，随时查看</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero-banner {
  background-color: #f0f7ff;
  padding: 80px 0;
  overflow: hidden;
  min-height: 500px;
  display: flex;
  align-items: center;
}

.banner-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.banner-content {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.banner-title {
  font-size: 42px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.2;
}

.banner-subtitle {
  font-size: 18px;
  color: #666;
  margin-bottom: 35px;
  line-height: 1.6;
}

.create-btn {
  padding: 14px 36px;
  font-size: 18px;
  margin-bottom: 50px;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
  max-width: 1000px;
  margin: 0 auto;
}

.feature-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  margin-bottom: 15px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #1890ff;
  background-color: rgba(24, 144, 255, 0.1);
  border-radius: 12px;
}

.feature-number {
  font-weight: bold;
  font-size: 20px;
}

.feature-desc {
  font-size: 15px;
  color: #666;
  margin: 0;
  line-height: 1.4;
}

@media (max-width: 992px) {
  .feature-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  
  .banner-title {
    font-size: 36px;
  }
}

@media (max-width: 576px) {
  .hero-banner {
    padding: 60px 0;
  }
  
  .banner-title {
    font-size: 28px;
  }
  
  .banner-subtitle {
    font-size: 16px;
  }
  
  .feature-cards {
    grid-template-columns: 1fr;
  }
  
  .create-btn {
    padding: 12px 30px;
    font-size: 16px;
  }
}
</style> 