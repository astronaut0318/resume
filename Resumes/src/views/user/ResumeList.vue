<!-- 简历列表页面 -->
<template>
  <div class="resume-list-container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="nav-menu">
        <div 
          class="nav-item" 
          :class="{ active: activeTab === 'myResumes' }" 
          @click="activeTab = 'myResumes'"
        >
          <el-icon><Document /></el-icon>
          <span>我的简历</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: activeTab === 'collections' }" 
          @click="activeTab = 'collections'"
        >
          <el-icon><Star /></el-icon>
          <span>我的收藏</span>
        </div>
      </div>
    </div>

    <!-- 右侧内容区域 -->
    <div class="content-area">
      <!-- 我的简历 -->
      <div v-if="activeTab === 'myResumes'" class="my-resumes">
        <h2 class="section-title">我的简历</h2>
        <!-- 有简历时显示简历列表 -->
        <div v-if="resumeStore.resumeList.length > 0" class="resume-list" v-loading="resumeStore.loading">
          <div v-for="resume in resumeStore.resumeList" :key="resume.id" class="resume-card">
            <div class="resume-card-header">
              <h3 class="resume-title">{{ resume.title }}</h3>
              <div class="resume-badges">
                <el-tag v-if="resume.isDefault" type="success" size="small">默认</el-tag>
                <el-tag v-if="resume.status === 1" type="info" size="small">已完成</el-tag>
                <el-tag v-else type="warning" size="small">草稿</el-tag>
              </div>
            </div>
            <div class="resume-card-body">
              <p class="resume-update-time">更新时间: {{ resume.updateTime }}</p>
            </div>
            <div class="resume-card-footer">
              <el-button-group>
                <el-button type="primary" @click="editResume(resume.id)">编辑</el-button>
                <el-button @click="previewResume(resume.id)">预览</el-button>
                <el-dropdown trigger="click" @command="handleCommand($event, resume.id)">
                  <el-button>
                    更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item v-if="!resume.isDefault" command="setDefault">设为默认</el-dropdown-item>
                      <el-dropdown-item command="download">下载</el-dropdown-item>
                      <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-button-group>
            </div>
          </div>
          
          <div class="create-new-card" @click="createNewResume">
            <el-icon><Plus /></el-icon>
            <span>创建新简历</span>
          </div>
        </div>
        <!-- 没有简历时显示空状态 -->
        <div v-else class="empty-state" v-loading="resumeStore.loading">
          <el-empty description="暂无简历">
            <el-button type="primary" @click="createNewResume">创建简历</el-button>
          </el-empty>
        </div>
      </div>

      <!-- 收藏的模板区域 -->
      <div v-if="activeTab === 'collections'" class="collected-templates">
        <h2 class="section-title">收藏的模板</h2>
        <div v-if="collectedTemplates.length > 0" class="templates-grid">
          <div
            v-for="template in collectedTemplates"
            :key="template.id"
            class="template-card"
            @click="goToDetail(template)"
          >
            <div class="template-image">
              <img :src="template.thumbnail" :alt="template.name">
              <div class="template-overlay">
                <el-button
                  type="primary"
                  @click.stop="goToCreate(template)"
                >
                  在线制作
                </el-button>
              </div>
            </div>

            <div class="template-info">
              <h3 class="template-name">{{ template.name }}</h3>
              <div class="template-stats">
                <span>下载 {{ template.downloads }}</span>
                <div class="template-actions">
                  <el-button
                    type="danger"
                    icon="Delete"
                    circle
                    @click.stop="handleUncollect(template)"
                  />
                </div>
              </div>
              <div class="template-price">
                <span v-if="template.isFree" class="free">免费</span>
                <span v-else class="price">￥{{ template.price }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-empty description="暂无收藏" />
        </div>
      </div>
    </div>
    
    <!-- 预览简历对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="简历预览"
      width="800px"
      destroy-on-close
    >
      <div v-if="previewResumeData" class="resume-preview">
        <h2>{{ previewResumeData.title }}</h2>
        <div class="preview-section">
          <h3>基本信息</h3>
          <div class="preview-content">
            <p><strong>姓名：</strong>{{ previewResumeData.content.basicInfo.name }}</p>
            <p><strong>电话：</strong>{{ previewResumeData.content.basicInfo.phone }}</p>
            <p><strong>邮箱：</strong>{{ previewResumeData.content.basicInfo.email }}</p>
            <p v-if="previewResumeData.content.basicInfo.address"><strong>地址：</strong>{{ previewResumeData.content.basicInfo.address }}</p>
          </div>
        </div>
        
        <div class="preview-section">
          <h3>教育经历</h3>
          <div class="preview-content" v-for="(edu, index) in previewResumeData.content.education" :key="index">
            <p><strong>{{ edu.school }}</strong> - {{ edu.major }}</p>
            <p>{{ edu.degree }} | {{ edu.startDate }} - {{ edu.endDate }}</p>
            <p v-if="edu.description">{{ edu.description }}</p>
          </div>
        </div>
        
        <div class="preview-section">
          <h3>工作经验</h3>
          <div class="preview-content" v-for="(work, index) in previewResumeData.content.workExperience" :key="index">
            <p><strong>{{ work.company }}</strong> - {{ work.position }}</p>
            <p>{{ work.startDate }} - {{ work.endDate }}</p>
            <p>{{ work.description }}</p>
          </div>
        </div>
        
        <div class="preview-section">
          <h3>技能</h3>
          <div class="preview-content">
            <el-tag v-for="(skill, index) in previewResumeData.content.skills" :key="index" class="skill-tag">
              {{ skill }}
            </el-tag>
          </div>
        </div>
      </div>
      <div v-else class="preview-loading">
        <el-skeleton :rows="10" animated />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTemplateStore } from '../../stores/template'
import { useResumeStore } from '../../stores/resume'
<<<<<<< HEAD
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Star, Plus, ArrowDown } from '@element-plus/icons-vue'
import { uncollectTemplate } from '../../api/template'
=======
import { useFileStore } from '../../stores/file'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Star, Plus, ArrowDown } from '@element-plus/icons-vue'
import { uncollectTemplate } from '../../api/template'
import ResumeExporter from '../../components/ResumeExporter.vue'
>>>>>>> upstream/master

const router = useRouter()
const templateStore = useTemplateStore()
const resumeStore = useResumeStore()
<<<<<<< HEAD
=======
const fileStore = useFileStore()
>>>>>>> upstream/master
const activeTab = ref('myResumes')

// 预览相关
const previewDialogVisible = ref(false)
const previewResumeData = ref(null)

// 获取收藏的模板列表
const collectedTemplates = computed(() => {
  return Array.from(templateStore.collectedTemplates).map(id => ({
    id,
    name: `模板 ${id}`,
    thumbnail: `http://example.com/thumb${id}.jpg`,
    downloads: Math.floor(Math.random() * 10000),
    isFree: Math.random() > 0.5,
    price: Math.random() > 0.5 ? 0 : Number((Math.random() * 20 + 9.9).toFixed(1))
  }))
})

// 取消收藏
const handleUncollect = async (template) => {
  try {
    await uncollectTemplate(template.id)
    templateStore.removeCollection(template.id)
    ElMessage.success('取消收藏成功')
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('操作失败')
  }
}

// 跳转到在线制作
const goToCreate = (template) => {
  router.push(`/create?template=${template.id}`)
}

// 跳转到详情页
const goToDetail = (template) => {
  router.push(`/templates/${template.id}`)
}

// 创建新简历
const createNewResume = () => {
  router.push('/create')
}

// 编辑简历
const editResume = (resumeId) => {
  router.push(`/create?resumeId=${resumeId}`)
}

// 预览简历
const previewResume = async (resumeId) => {
  try {
    const resumeData = await resumeStore.fetchResumeDetail(resumeId)
    previewResumeData.value = resumeData
    previewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取简历数据失败')
  }
}

// 处理更多操作
const handleCommand = (command, resumeId) => {
  switch (command) {
    case 'setDefault':
      setDefaultResume(resumeId)
      break
    case 'download':
      downloadResume(resumeId)
      break
    case 'delete':
      confirmDeleteResume(resumeId)
      break
  }
}

// 设置默认简历
const setDefaultResume = async (resumeId) => {
  try {
    await resumeStore.setDefaultResume(resumeId)
    ElMessage.success('设置默认简历成功')
  } catch (error) {
    ElMessage.error('设置失败')
  }
}

// 下载简历
const downloadResume = (resumeId) => {
<<<<<<< HEAD
  ElMessage.info('下载功能开发中...')
=======
  // 使用ElMessageBox来实现格式选择
  ElMessageBox.confirm('请选择导出格式', '导出简历', {
    confirmButtonText: 'PDF格式',
    cancelButtonText: 'Word格式',
    distinguishCancelAndClose: true,
    closeOnClickModal: false
  }).then(() => {
    // 导出PDF
    ElMessage.info('正在生成PDF，请稍候...')
    fileStore.exportResumeToPdf(resumeId)
      .then(() => {
        ElMessage.success('PDF下载成功！')
      })
      .catch((error) => {
        console.error('导出PDF失败:', error)
        if (error.response && error.response.status === 500) {
          ElMessage.warning('服务器暂时无法处理您的请求，请稍后再试')
        } else {
          ElMessage.error('PDF下载失败，请重试')
        }
        
        // 如果是演示环境，模拟下载成功
        if (import.meta.env.MODE === 'development') {
          ElMessage.success('在演示环境中模拟PDF下载成功')
        }
      })
  }).catch(action => {
    if (action === 'cancel') {
      // 导出Word
      ElMessage.info('正在生成Word文档，请稍候...')
      fileStore.exportResumeToWord(resumeId)
        .then(() => {
          ElMessage.success('Word文档下载成功！')
        })
        .catch((error) => {
          console.error('导出Word失败:', error)
          if (error.response && error.response.status === 500) {
            ElMessage.warning('服务器暂时无法处理您的请求，请稍后再试')
          } else {
            ElMessage.error('Word下载失败，请重试')
          }
          
          // 如果是演示环境，模拟下载成功
          if (import.meta.env.MODE === 'development') {
            ElMessage.success('在演示环境中模拟Word下载成功')
          }
        })
    }
  })
>>>>>>> upstream/master
}

// 确认删除简历
const confirmDeleteResume = (resumeId) => {
  ElMessageBox.confirm(
    '确定要删除这份简历吗？删除后无法恢复。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    deleteResume(resumeId)
  }).catch(() => {})
}

// 删除简历
const deleteResume = async (resumeId) => {
  try {
    await resumeStore.deleteResume(resumeId)
    ElMessage.success('删除成功')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 初始化
onMounted(() => {
  resumeStore.fetchResumeList()
})
</script>

<style scoped>
.resume-list-container {
  display: flex;
  min-height: calc(100vh - 64px);
}

.sidebar {
  width: 200px;
  background-color: #f5f7fa;
  border-right: 1px solid #e6e6e6;
  padding: 20px 0;
}

.nav-menu {
  display: flex;
  flex-direction: column;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.nav-item:hover {
  background-color: #ecf5ff;
  color: #1890ff;
}

.nav-item.active {
  background-color: #ecf5ff;
  color: #1890ff;
  border-right: 2px solid #1890ff;
}

.nav-item .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.section-title {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.collected-templates, .my-resumes {
  width: 100%;
}

.empty-state {
  padding: 40px 0;
  display: flex;
  justify-content: center;
}

/* 简历列表样式 */
.resume-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.resume-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  transition: transform 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 200px;
}

.resume-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.resume-card-header {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resume-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resume-badges {
  display: flex;
  gap: 5px;
}

.resume-card-body {
  padding: 15px;
  flex: 1;
}

.resume-update-time {
  color: #999;
  font-size: 14px;
  margin: 0;
}

.resume-card-footer {
  padding: 15px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.create-new-card {
  border-radius: 8px;
  border: 2px dashed #dcdfe6;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  color: #909399;
}

.create-new-card:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.create-new-card .el-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

/* 模板列表样式 */
.templates-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.template-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
  cursor: pointer;
  background-color: #fff;
}

.template-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.template-image {
  position: relative;
  padding-top: 141.4%; /* A4 纸比例 */
  overflow: hidden;
  background-color: #f5f7fa;
}

.template-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.template-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.template-card:hover .template-overlay {
  opacity: 1;
}

.template-info {
  padding: 12px;
}

.template-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.template-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #999;
}

.template-price {
  font-size: 16px;
  font-weight: 500;
}

.free {
  color: #67c23a;
}

.price {
  color: #f56c6c;
}

/* 简历预览样式 */
.resume-preview {
  padding: 20px;
}

.resume-preview h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.preview-section {
  margin-bottom: 20px;
}

.preview-section h3 {
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  color: #333;
}

.preview-content {
  padding: 10px 0;
}

.skill-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

@media (max-width: 1200px) {
  .templates-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .resume-list-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e6e6e6;
    padding: 10px 0;
  }
  
  .nav-menu {
    flex-direction: row;
    justify-content: center;
  }
  
  .nav-item {
    padding: 8px 16px;
  }
  
  .nav-item.active {
    border-right: none;
    border-bottom: 2px solid #1890ff;
  }
  
  .templates-grid {
    grid-template-columns: 1fr;
  }
}
</style> 