<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { View, Download } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { getTemplates } from '../api/template'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const templateList = ref([])
const loading = ref(false)

// 获取模板列表
const fetchTemplates = async () => {
  loading.value = true
  try {
    const res = await getTemplates({ page: 1, size: 8 }) // 获取前8个模板
    console.log('获取首页模板列表成功:', res)
    templateList.value = res.data.list
  } catch (error) {
    console.error('获取模板列表失败:', error)
    ElMessage.error('获取模板列表失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = (template) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再使用此功能')
    router.push('/login')
    return
  }
  router.push({
    path: '/create',
    query: { templateId: template.id }
  })
}

// 格式化数字显示
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toString()
}

// 添加跳转到模板详情页的函数
const goToDetail = (template) => {
  router.push(`/templates/${template.id}`)
}

onMounted(() => {
  fetchTemplates()
})
</script>

<template>
  <section class="templates-section">
    <div class="section-container">
      <div class="section-header">
        <h2 class="section-title">在线简历模板</h2>
        <p class="section-subtitle">精选优质模板，一键套用，快速制作</p>
      </div>
      
      <div class="templates-grid" v-loading="loading">
        <div v-for="template in templateList" 
             :key="template.id" 
             class="template-card"
             @click="goToDetail(template)">
          <div class="template-image">
            <div class="template-image-placeholder" 
                 :style="{ 
                   backgroundImage: `url(${template.thumbnail})`,
                   backgroundSize: 'cover',
                   backgroundPosition: 'center',
                   backgroundRepeat: 'no-repeat'
                 }">
              <div class="template-content">
                <h3 class="template-title">{{ template.name }}</h3>
                <div class="template-stats">
                  <span class="stat-item">
                    <el-icon><download /></el-icon>
                    {{ formatNumber(template.downloads) }}
                  </span>
                </div>
              </div>
              <div class="template-hover">
                <el-button 
                  type="primary" 
                  class="edit-btn"
                  @click.stop="handleCreate(template)">
                  在线编辑
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.templates-section {
  padding: 60px 0;
  background-color: #fff;
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.section-subtitle {
  font-size: 16px;
  color: #666;
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.template-card {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  overflow: hidden;
  cursor: pointer;
  background-color: #fff;
  position: relative;
}

.template-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.template-card:hover::after {
  content: '点击查看详情';
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 3;
}

.template-image {
  position: relative;
  overflow: hidden;
}

.template-image-placeholder {
  height: 280px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  text-align: center;
}

.template-content {
  position: relative;
  z-index: 1;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  background-color: rgba(0, 0, 0, 0.3);
  padding: 20px;
  border-radius: 8px;
  backdrop-filter: blur(5px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 80%;
}

.template-title {
  font-size: 18px;
  margin: 0 0 15px;
  font-weight: bold;
  text-align: center;
}

.template-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-divider {
  margin: 0 8px;
  opacity: 0.6;
}

.template-hover {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  pointer-events: none;
}

.template-card:hover .template-hover {
  opacity: 1;
  pointer-events: auto;
}

.edit-btn {
  padding: 10px 30px;
  font-size: 16px;
  pointer-events: auto;
  position: relative;
  z-index: 2;
  transition: all 0.2s ease;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

@media (max-width: 992px) {
  .templates-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .templates-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .templates-grid {
    grid-template-columns: 1fr;
  }
}
</style> 