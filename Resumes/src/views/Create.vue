<template>
  <div class="create-container">
    <div class="editor-container">
      <!-- 左侧编辑区域 -->
      <div class="editor-panel">
        <h2 class="editor-title">{{ isEdit ? '编辑简历' : '创建简历' }}</h2>
        
        <!-- 表单区域 -->
        <el-form 
          ref="resumeForm" 
          :model="resumeData" 
          label-position="top"
          class="resume-form"
          v-loading="loading"
        >
          <!-- 基本信息 -->
          <el-collapse v-model="activeCollapse">
            <el-collapse-item title="基本信息" name="basicInfo">
              <el-form-item label="简历标题">
                <el-input v-model="resumeData.title" placeholder="请输入简历标题" />
              </el-form-item>
              
              <el-form-item label="姓名">
                <el-input v-model="resumeData.content.basicInfo.name" placeholder="请输入姓名" />
              </el-form-item>
              
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="电话">
                    <el-input v-model="resumeData.content.basicInfo.phone" placeholder="请输入电话" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="邮箱">
                    <el-input v-model="resumeData.content.basicInfo.email" placeholder="请输入邮箱" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="地址">
                <el-input v-model="resumeData.content.basicInfo.address" placeholder="请输入地址" />
              </el-form-item>
            </el-collapse-item>
            
            <!-- 教育经历 -->
            <el-collapse-item title="教育经历" name="education">
              <div v-for="(edu, index) in resumeData.content.education" :key="index" class="section-item">
                <div class="section-item-header">
                  <h4>教育经历 #{{ index + 1 }}</h4>
                  <el-button 
                    type="danger" 
                    circle 
                    size="small" 
                    @click="removeEducation(index)"
                    :icon="Delete"
                  />
                </div>
                
                <el-form-item label="学校">
                  <el-input v-model="edu.school" placeholder="请输入学校名称" />
                </el-form-item>
                
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="专业">
                      <el-input v-model="edu.major" placeholder="请输入专业" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="学位">
                      <el-select v-model="edu.degree" placeholder="请选择学位" style="width: 100%">
                        <el-option label="高中" value="高中" />
                        <el-option label="专科" value="专科" />
                        <el-option label="本科" value="本科" />
                        <el-option label="硕士" value="硕士" />
                        <el-option label="博士" value="博士" />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="开始时间">
                      <el-date-picker 
                        v-model="edu.startDate" 
                        type="date" 
                        placeholder="选择日期" 
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="结束时间">
                      <el-date-picker 
                        v-model="edu.endDate" 
                        type="date" 
                        placeholder="选择日期" 
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item label="描述">
                  <el-input 
                    v-model="edu.description" 
                    type="textarea" 
                    placeholder="请输入描述" 
                    :rows="3"
                  />
                </el-form-item>
                
                <el-divider v-if="index < resumeData.content.education.length - 1" />
              </div>
              
              <div class="add-section-btn">
                <el-button type="primary" plain @click="addEducation" :icon="Plus">添加教育经历</el-button>
              </div>
            </el-collapse-item>
            
            <!-- 工作经验 -->
            <el-collapse-item title="工作经验" name="workExperience">
              <div v-for="(work, index) in resumeData.content.workExperience" :key="index" class="section-item">
                <div class="section-item-header">
                  <h4>工作经验 #{{ index + 1 }}</h4>
                  <el-button 
                    type="danger" 
                    circle 
                    size="small" 
                    @click="removeWorkExperience(index)"
                    :icon="Delete"
                  />
                </div>
                
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="公司">
                      <el-input v-model="work.company" placeholder="请输入公司名称" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="职位">
                      <el-input v-model="work.position" placeholder="请输入职位" />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="开始时间">
                      <el-date-picker 
                        v-model="work.startDate" 
                        type="date" 
                        placeholder="选择日期" 
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="结束时间">
                      <el-date-picker 
                        v-model="work.endDate" 
                        type="date" 
                        placeholder="选择日期" 
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item label="工作描述">
                  <el-input 
                    v-model="work.description" 
                    type="textarea" 
                    placeholder="请输入工作描述" 
                    :rows="3"
                  />
                </el-form-item>
                
                <el-divider v-if="index < resumeData.content.workExperience.length - 1" />
              </div>
              
              <div class="add-section-btn">
                <el-button type="primary" plain @click="addWorkExperience" :icon="Plus">添加工作经验</el-button>
              </div>
            </el-collapse-item>
            
            <!-- 技能 -->
            <el-collapse-item title="技能" name="skills">
              <el-form-item label="技能标签">
                <el-tag
                  v-for="(skill, index) in resumeData.content.skills"
                  :key="index"
                  closable
                  @close="removeSkill(index)"
                  class="skill-tag"
                >
                  {{ skill }}
                </el-tag>
                
                <el-input
                  v-if="inputSkillVisible"
                  ref="inputSkillRef"
                  v-model="inputSkillValue"
                  class="skill-input"
                  size="small"
                  @keyup.enter="addSkill"
                  @blur="addSkill"
                />
                
                <el-button v-else class="button-new-tag" size="small" @click="showInputSkill">
                  + 添加技能
                </el-button>
              </el-form-item>
            </el-collapse-item>
          </el-collapse>
          
          <!-- 操作按钮 -->
          <div class="form-actions">
            <el-button @click="cancel">取消</el-button>
            <el-button type="primary" @click="saveResume" :loading="saving">保存简历</el-button>
          </div>
        </el-form>
      </div>
      
      <!-- 右侧预览区域 -->
      <div class="preview-panel">
        <div class="preview-header">
          <h2>简历预览</h2>
        </div>
        
        <div class="preview-content">
          <div class="resume-preview">
            <h1 class="preview-title">{{ resumeData.content.basicInfo.name || '姓名' }}</h1>
            
            <div class="preview-contact">
              <span v-if="resumeData.content.basicInfo.phone">{{ resumeData.content.basicInfo.phone }}</span>
              <span v-if="resumeData.content.basicInfo.email">{{ resumeData.content.basicInfo.email }}</span>
              <span v-if="resumeData.content.basicInfo.address">{{ resumeData.content.basicInfo.address }}</span>
            </div>
            
            <div v-if="resumeData.content.education.length > 0" class="preview-section">
              <h2>教育经历</h2>
              <div v-for="(edu, index) in resumeData.content.education" :key="index" class="preview-item">
                <div class="preview-item-header">
                  <div class="preview-item-title">{{ edu.school }}</div>
                  <div class="preview-item-date">{{ edu.startDate }} - {{ edu.endDate }}</div>
                </div>
                <div class="preview-item-subtitle">{{ edu.major }} | {{ edu.degree }}</div>
                <div v-if="edu.description" class="preview-item-desc">{{ edu.description }}</div>
              </div>
            </div>
            
            <div v-if="resumeData.content.workExperience.length > 0" class="preview-section">
              <h2>工作经验</h2>
              <div v-for="(work, index) in resumeData.content.workExperience" :key="index" class="preview-item">
                <div class="preview-item-header">
                  <div class="preview-item-title">{{ work.company }}</div>
                  <div class="preview-item-date">{{ work.startDate }} - {{ work.endDate }}</div>
                </div>
                <div class="preview-item-subtitle">{{ work.position }}</div>
                <div class="preview-item-desc">{{ work.description }}</div>
              </div>
            </div>
            
            <div v-if="resumeData.content.skills.length > 0" class="preview-section">
              <h2>技能</h2>
              <div class="preview-skills">
                <el-tag
                  v-for="(skill, index) in resumeData.content.skills"
                  :key="index"
                  class="preview-skill-tag"
                >
                  {{ skill }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useResumeStore } from '../stores/resume'
import { useTemplateStore } from '../stores/template'
import { ElMessage, ElNotification } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const resumeStore = useResumeStore()
const templateStore = useTemplateStore()

// 状态
const loading = ref(false)
const saving = ref(false)
const activeCollapse = ref(['basicInfo', 'education', 'workExperience', 'skills'])
const resumeId = ref(null)
const templateId = ref(null)
const inputSkillVisible = ref(false)
const inputSkillValue = ref('')
const inputSkillRef = ref(null)

// 计算属性
const isEdit = computed(() => !!resumeId.value)

// 简历数据
const resumeData = reactive({
  title: '',
  templateId: null,
  content: {
    basicInfo: {
      name: '',
      phone: '',
      email: '',
      address: ''
    },
    education: [],
    workExperience: [],
    skills: []
  }
})

// 初始化
onMounted(async () => {
  loading.value = true
  try {
    // 获取路由参数
    const routeResumeId = route.query.resumeId
    const routeTemplateId = route.query.template
    
    if (routeResumeId) {
      // 编辑模式：加载简历数据
      resumeId.value = parseInt(routeResumeId)
      await loadResumeData(resumeId.value)
    } else if (routeTemplateId) {
      // 新建模式：使用模板
      templateId.value = parseInt(routeTemplateId)
      initFromTemplate(templateId.value)
    } else {
      // 新建模式：使用默认模板
      initEmptyResume()
    }
  } catch (error) {
    console.error('初始化失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
})

// 加载简历数据
const loadResumeData = async (id) => {
  try {
    const data = await resumeStore.fetchResumeDetail(id)
    if (data) {
      // 更新简历数据
      resumeData.title = data.title
      resumeData.templateId = data.templateId
      resumeData.content = { ...data.content }
    }
  } catch (error) {
    console.error('加载简历数据失败:', error)
    ElMessage.error('加载简历数据失败')
  }
}

// 从模板初始化
const initFromTemplate = (id) => {
  resumeData.title = '我的简历'
  resumeData.templateId = id
  
  // 添加默认的教育经历和工作经验
  addEducation()
  addWorkExperience()
}

// 初始化空简历
const initEmptyResume = () => {
  resumeData.title = '我的简历'
  resumeData.templateId = 1 // 默认模板ID
  
  // 添加默认的教育经历和工作经验
  addEducation()
  addWorkExperience()
}

// 添加教育经历
const addEducation = () => {
  resumeData.content.education.push({
    school: '',
    major: '',
    degree: '',
    startDate: '',
    endDate: '',
    description: ''
  })
}

// 移除教育经历
const removeEducation = (index) => {
  resumeData.content.education.splice(index, 1)
}

// 添加工作经验
const addWorkExperience = () => {
  resumeData.content.workExperience.push({
    company: '',
    position: '',
    startDate: '',
    endDate: '',
    description: ''
  })
}

// 移除工作经验
const removeWorkExperience = (index) => {
  resumeData.content.workExperience.splice(index, 1)
}

// 显示技能输入框
const showInputSkill = () => {
  inputSkillVisible.value = true
  nextTick(() => {
    inputSkillRef.value?.focus()
  })
}

// 添加技能
const addSkill = () => {
  if (inputSkillValue.value) {
    if (!resumeData.content.skills.includes(inputSkillValue.value)) {
      resumeData.content.skills.push(inputSkillValue.value)
    }
  }
  inputSkillVisible.value = false
  inputSkillValue.value = ''
}

// 移除技能
const removeSkill = (index) => {
  resumeData.content.skills.splice(index, 1)
}

// 保存简历
const saveResume = async () => {
  saving.value = true
  try {
    if (isEdit.value) {
      // 更新简历
      await resumeStore.updateResume(resumeId.value, {
        title: resumeData.title,
        templateId: resumeData.templateId,
        content: resumeData.content
      })
      ElNotification({
        title: '保存成功',
        message: '简历已成功更新，您可以继续编辑或稍后查看',
        type: 'success',
        duration: 3000
      })
    } else {
      // 创建简历
      await resumeStore.createResume({
        title: resumeData.title,
        templateId: resumeData.templateId,
        content: resumeData.content
      })
      ElNotification({
        title: '保存成功',
        message: '简历已成功保存，您可以继续编辑或稍后查看',
        type: 'success',
        duration: 3000
      })
    }
    
    // 更新状态，标记为编辑模式
    if (!isEdit.value) {
      isEdit.value = true
      resumeId.value = resumeStore.currentResume?.id || resumeId.value
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElNotification({
      title: '保存失败',
      message: '请检查您的网络连接或稍后重试',
      type: 'error',
      duration: 3000
    })
  } finally {
    saving.value = false
  }
}

// 取消
const cancel = () => {
  router.push('/resume')
}
</script>

<style scoped>
.create-container {
  padding: 20px;
  min-height: calc(100vh - 64px);
}

.editor-container {
  display: flex;
  gap: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.editor-panel {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow-y: auto;
  max-height: calc(100vh - 104px);
}

.preview-panel {
  width: 40%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.editor-title {
  margin-top: 0;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  color: #333;
}

.resume-form {
  margin-bottom: 20px;
}

.section-item {
  margin-bottom: 15px;
}

.section-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.section-item-header h4 {
  margin: 0;
  color: #333;
}

.add-section-btn {
  margin-top: 15px;
}

.skill-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.skill-input {
  width: 120px;
  margin-right: 8px;
  vertical-align: bottom;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.preview-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.preview-header h2 {
  margin: 0;
  color: #333;
}

.preview-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f9f9f9;
}

.resume-preview {
  background-color: #fff;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  min-height: 842px; /* A4 height */
}

.preview-title {
  text-align: center;
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.preview-contact {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 30px;
  color: #666;
}

.preview-section {
  margin-bottom: 25px;
}

.preview-section h2 {
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
  color: #333;
}

.preview-item {
  margin-bottom: 15px;
}

.preview-item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.preview-item-title {
  font-weight: bold;
  color: #333;
}

.preview-item-date {
  color: #666;
}

.preview-item-subtitle {
  margin-bottom: 5px;
  color: #333;
}

.preview-item-desc {
  color: #666;
  white-space: pre-line;
}

.preview-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-skill-tag {
  margin-right: 0;
}

@media (max-width: 1200px) {
  .editor-container {
    flex-direction: column;
  }
  
  .editor-panel, .preview-panel {
    width: 100%;
    max-height: none;
  }
  
  .preview-panel {
    height: 600px;
  }
}
</style> 