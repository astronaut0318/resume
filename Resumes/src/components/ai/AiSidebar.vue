<template>
  <div class="ai-sidebar">
    <h3 class="ai-sidebar-title">AI辅助工具</h3>
    
    <!-- 简历分析 -->
    <el-card shadow="hover" class="ai-card">
      <template #header>
        <div class="card-header">
          <span>简历分析</span>
          <el-tooltip content="AI分析您的简历，给出评分和改进建议">
            <el-icon><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>
      <p class="card-desc">获取AI对您简历的全面评分和分析</p>
      <el-button type="primary" @click="analyzeCurrentResume" :loading="isAnalyzing" class="action-btn">开始分析</el-button>
    </el-card>
    
    <!-- 内容优化 -->
    <el-card shadow="hover" class="ai-card">
      <template #header>
        <div class="card-header">
          <span>内容优化</span>
          <el-tooltip content="获取简历内容的具体优化建议">
            <el-icon><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>
      <p class="card-desc">获取简历内容的优化建议，提升表达效果</p>
      <el-select v-model="sectionToOptimize" placeholder="选择优化内容" style="width: 100%; margin-bottom: 10px;">
        <el-option label="整个简历" value="" />
        <el-option label="基本信息" value="basicInfo" />
        <el-option label="教育经历" value="education" />
        <el-option label="工作经验" value="workExperience" />
        <el-option label="技能" value="skills" />
      </el-select>
      <el-button type="primary" @click="optimizeContent" :loading="isOptimizing" class="action-btn">获取优化建议</el-button>
    </el-card>
    
    <!-- 职位匹配 -->
    <el-card shadow="hover" class="ai-card">
      <template #header>
        <div class="card-header">
          <span>职位匹配分析</span>
          <el-tooltip content="分析简历与目标职位的匹配度">
            <el-icon><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>
      <p class="card-desc">检测您的简历与目标职位的匹配程度</p>
      <el-input v-model="targetPosition" placeholder="目标职位" style="margin-bottom: 10px;" />
      <el-input
        v-model="jobDescription"
        type="textarea"
        placeholder="职位描述"
        :rows="4"
        style="margin-bottom: 10px;"
      />
      <el-button type="primary" @click="matchWithPosition" :loading="isMatching" class="action-btn">开始匹配</el-button>
    </el-card>
    
    <!-- 内容生成 -->
    <el-card shadow="hover" class="ai-card">
      <template #header>
        <div class="card-header">
          <span>AI内容生成</span>
          <el-tooltip content="根据您的信息自动生成简历内容">
            <el-icon><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>
      <p class="card-desc">根据您的基本信息自动生成专业简历内容</p>
      <el-button type="primary" @click="openGeneratorDialog" :loading="isGenerating" class="action-btn">生成内容</el-button>
    </el-card>
  </div>
  
  <!-- 结果对话框 -->
  <el-dialog
    v-model="showResultDialog"
    :title="resultDialogTitle"
    width="60%"
    destroy-on-close
  >
    <div v-if="aiResult">
      <!-- 简历分析结果 -->
      <div v-if="resultType === 'analysis'" class="ai-result">
        <div class="score-section">
          <div class="score">{{ aiResult.score }}<span class="score-label">分</span></div>
          <div class="overall-comment">{{ aiResult.analysis.overall }}</div>
        </div>
        
        <el-divider content-position="left">详细分析</el-divider>
        
        <div class="analysis-item" v-for="item in analysisItems" :key="item.key">
          <div class="analysis-title">{{ getAnalysisTitle(item.key) }}:</div>
          <div class="analysis-content">{{ item.value }}</div>
        </div>
      </div>
      
      <!-- 内容优化结果 -->
      <div v-if="resultType === 'optimization'" class="ai-result">
        <el-alert
          title="AI优化建议仅供参考，您可以选择性应用"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        />
        
        <div v-for="(suggestion, index) in aiResult.suggestions" :key="index" class="suggestion-item">
          <div class="suggestion-header">
            <strong>{{ getSectionTitle(suggestion.section) }}</strong>
            <span v-if="suggestion.item !== undefined">项目 #{{ suggestion.item + 1 }}</span>
          </div>
          
          <div v-if="suggestion.original" class="suggestion-original">
            <div class="suggestion-label">原内容:</div>
            <div class="suggestion-text">{{ suggestion.original }}</div>
          </div>
          
          <div class="suggestion-new">
            <div class="suggestion-label">建议:</div>
            <div class="suggestion-text">{{ suggestion.suggestion }}</div>
          </div>
          
          <div class="suggestion-actions">
            <el-button size="small" @click="applySingleSuggestion(suggestion)">应用此建议</el-button>
          </div>
        </div>
      </div>
      
      <!-- 职位匹配结果 -->
      <div v-if="resultType === 'matching'" class="ai-result">
        <div class="match-header">
          <div class="match-score">匹配度: <span>{{ aiResult.matchScore }}%</span></div>
          <div class="match-analysis">{{ aiResult.analysis }}</div>
        </div>
        
        <el-divider content-position="left">优势</el-divider>
        <ul class="match-list">
          <li v-for="(item, index) in aiResult.advantages" :key="'adv-'+index">{{ item }}</li>
        </ul>
        
        <el-divider content-position="left">不足</el-divider>
        <ul class="match-list">
          <li v-for="(item, index) in aiResult.weaknesses" :key="'weak-'+index">{{ item }}</li>
        </ul>
        
        <el-divider content-position="left">改进建议</el-divider>
        <ul class="match-list">
          <li v-for="(item, index) in aiResult.suggestions" :key="'sug-'+index">{{ item }}</li>
        </ul>
      </div>
      
      <!-- 内容生成结果 -->
      <div v-if="resultType === 'generation'" class="ai-result">
        <el-alert
          title="以下是AI生成的内容，您可以选择应用到您的简历"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;"
        />
        
        <el-collapse v-model="activeCollapseNames">
          <el-collapse-item title="个人简介" name="profile">
            <div class="generated-content">{{ aiResult.profile }}</div>
            <div class="suggestion-actions">
              <el-button size="small" @click="applyGeneratedProfile">应用此内容</el-button>
            </div>
          </el-collapse-item>
          
          <el-collapse-item title="工作经验" name="workExperience">
            <div v-for="(exp, index) in aiResult.workExperience" :key="index" class="generated-item">
              <div class="generated-header">
                <strong>{{ exp.company }}</strong>
                <span>{{ exp.position }}</span>
              </div>
              
              <div class="generated-responsibilities">
                <div class="suggestion-label">工作职责:</div>
                <ul>
                  <li v-for="(item, idx) in exp.responsibilities" :key="idx">{{ item }}</li>
                </ul>
              </div>
              
              <div class="suggestion-actions">
                <el-button size="small" @click="applyGeneratedWorkExperience(exp, index)">应用此工作经验</el-button>
              </div>
            </div>
          </el-collapse-item>
          
          <el-collapse-item title="项目经验" name="projects">
            <div v-for="(proj, index) in aiResult.projects" :key="index" class="generated-item">
              <div class="generated-header">
                <strong>{{ proj.name }}</strong>
              </div>
              
              <div class="generated-description">
                <div class="suggestion-label">项目描述:</div>
                <div>{{ proj.description }}</div>
              </div>
              
              <div class="generated-highlights">
                <div class="suggestion-label">亮点:</div>
                <ul>
                  <li v-for="(item, idx) in proj.highlights" :key="idx">{{ item }}</li>
                </ul>
              </div>
              
              <div class="suggestion-actions">
                <el-button size="small" @click="applyGeneratedProject(proj, index)">应用此项目经验</el-button>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showResultDialog = false">关闭</el-button>
        <el-button type="primary" @click="applyAllSuggestions" v-if="canApplySuggestions">
          应用所有建议
        </el-button>
      </div>
    </template>
  </el-dialog>
  
  <!-- 内容生成对话框 -->
  <el-dialog
    v-model="showGeneratorDialog"
    title="AI内容生成"
    width="60%"
    destroy-on-close
  >
    <el-form :model="generatorForm" label-position="top">
      <el-form-item label="目标职位">
        <el-input v-model="generatorForm.position" placeholder="如：Java开发工程师" />
      </el-form-item>
      
      <el-form-item label="行业">
        <el-input v-model="generatorForm.industry" placeholder="如：互联网" />
      </el-form-item>
      
      <el-form-item label="工作年限">
        <el-input-number v-model="generatorForm.workYears" :min="0" :max="50" />
      </el-form-item>
      
      <el-form-item label="核心技能">
        <el-tag
          v-for="skill in generatorForm.keySkills"
          :key="skill"
          closable
          @close="removeSkill(skill)"
          style="margin-right: 10px; margin-bottom: 10px;"
        >
          {{ skill }}
        </el-tag>
        <el-input
          v-if="skillInputVisible"
          ref="skillInputRef"
          v-model="skillInput"
          @keyup.enter="addSkill"
          @blur="addSkill"
          style="width: 200px;"
        />
        <el-button v-else @click="showSkillInput" plain>+ 添加技能</el-button>
      </el-form-item>
      
      <el-form-item label="教育信息">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-input v-model="generatorForm.education.school" placeholder="学校" />
          </el-col>
          <el-col :span="12">
            <el-input v-model="generatorForm.education.major" placeholder="专业" />
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 10px;">
          <el-col :span="12">
            <el-select v-model="generatorForm.education.degree" placeholder="学位" style="width: 100%;">
              <el-option label="专科" value="专科" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
            </el-select>
          </el-col>
          <el-col :span="12">
            <el-date-picker
              v-model="generatorForm.education.graduationYear"
              type="year"
              placeholder="毕业年份"
              format="YYYY"
              value-format="YYYY"
              style="width: 100%;"
            />
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showGeneratorDialog = false">取消</el-button>
        <el-button type="primary" @click="generateContent" :loading="isGenerating">
          生成内容
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, nextTick, computed } from 'vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { analyzeResume, optimizeResume, matchPosition, generateResumeContent } from '../../api/ai'

// 声明props和emit
const props = defineProps({
  resumeId: {
    type: Number,
    required: false,
    default: null
  },
  resumeData: {
    type: Object,
    required: false,
    default: () => ({})
  }
})

const emit = defineEmits(['apply-suggestion', 'apply-all-suggestions'])

// 状态变量
const isAnalyzing = ref(false)
const isOptimizing = ref(false)
const isMatching = ref(false)
const isGenerating = ref(false)
const showResultDialog = ref(false)
const showGeneratorDialog = ref(false)
const resultDialogTitle = ref('')
const resultType = ref('')
const aiResult = ref(null)
const canApplySuggestions = ref(false)
const activeCollapseNames = ref(['profile', 'workExperience', 'projects'])

// 计算属性，解决v-for和v-if混用问题
const analysisItems = computed(() => {
  if (!aiResult.value || !aiResult.value.analysis) return []
  return Object.entries(aiResult.value.analysis)
    .filter(([key]) => key !== 'overall')
    .map(([key, value]) => ({ key, value }))
})

// 表单数据
const sectionToOptimize = ref('')
const targetPosition = ref('')
const jobDescription = ref('')

// 内容生成表单
const generatorForm = reactive({
  position: '',
  industry: '',
  workYears: 1,
  keySkills: [],
  education: {
    school: '',
    major: '',
    degree: '本科',
    graduationYear: new Date().getFullYear()
  }
})

// 技能输入相关
const skillInputVisible = ref(false)
const skillInput = ref('')
const skillInputRef = ref(null)

// 简历分析功能
const analyzeCurrentResume = async () => {
  try {
    isAnalyzing.value = true
    // 如果没有resumeId，发送当前简历内容作为临时数据
    const response = await analyzeResume(props.resumeId || -1, props.resumeData)
    
    if (response.data.code === 200) {
      aiResult.value = response.data.data
      resultType.value = 'analysis'
      resultDialogTitle.value = '简历分析结果'
      showResultDialog.value = true
      canApplySuggestions.value = false
    } else {
      ElMessage.error(response.data.message || '分析失败')
    }
  } catch (error) {
    console.error('简历分析失败:', error)
    ElMessage.error('分析失败，请稍后重试')
  } finally {
    isAnalyzing.value = false
  }
}

// 内容优化功能
const optimizeContent = async () => {
  try {
    isOptimizing.value = true
    const response = await optimizeResume(
      props.resumeId || -1, 
      sectionToOptimize.value || null, 
      null,
      props.resumeData
    )
    
    if (response.data.code === 200) {
      aiResult.value = response.data.data
      resultType.value = 'optimization'
      resultDialogTitle.value = '内容优化建议'
      showResultDialog.value = true
      canApplySuggestions.value = true
    } else {
      ElMessage.error(response.data.message || '获取优化建议失败')
    }
  } catch (error) {
    console.error('获取优化建议失败:', error)
    ElMessage.error('获取优化建议失败，请稍后重试')
  } finally {
    isOptimizing.value = false
  }
}

// 职位匹配分析
const matchWithPosition = async () => {
  if (!targetPosition.value) {
    ElMessage.warning('请输入目标职位')
    return
  }
  
  try {
    isMatching.value = true
    const response = await matchPosition(
      props.resumeId || -1, 
      targetPosition.value, 
      jobDescription.value,
      props.resumeData
    )
    
    if (response.data.code === 200) {
      aiResult.value = response.data.data
      resultType.value = 'matching'
      resultDialogTitle.value = '职位匹配分析'
      showResultDialog.value = true
      canApplySuggestions.value = false
    } else {
      ElMessage.error(response.data.message || '职位匹配分析失败')
    }
  } catch (error) {
    console.error('职位匹配分析失败:', error)
    ElMessage.error('职位匹配分析失败，请稍后重试')
  } finally {
    isMatching.value = false
  }
}

// 显示内容生成对话框
const openGeneratorDialog = () => {
  showGeneratorDialog.value = true
}

// 技能输入相关方法
const showSkillInput = () => {
  skillInputVisible.value = true
  nextTick(() => {
    skillInputRef.value.focus()
  })
}

const addSkill = () => {
  if (skillInput.value && !generatorForm.keySkills.includes(skillInput.value)) {
    generatorForm.keySkills.push(skillInput.value)
  }
  skillInputVisible.value = false
  skillInput.value = ''
}

const removeSkill = (skill) => {
  generatorForm.keySkills = generatorForm.keySkills.filter(item => item !== skill)
}

// 生成内容
const generateContent = async () => {
  if (!generatorForm.position) {
    ElMessage.warning('请输入目标职位')
    return
  }
  
  if (generatorForm.keySkills.length === 0) {
    ElMessage.warning('请至少添加一项核心技能')
    return
  }
  
  if (!generatorForm.education.school || !generatorForm.education.major) {
    ElMessage.warning('请完善教育信息')
    return
  }
  
  try {
    isGenerating.value = true
    const response = await generateResumeContent(generatorForm)
    
    if (response.data.code === 200) {
      aiResult.value = response.data.data
      resultType.value = 'generation'
      resultDialogTitle.value = 'AI生成的简历内容'
      showGeneratorDialog.value = false
      showResultDialog.value = true
      canApplySuggestions.value = true
    } else {
      ElMessage.error(response.data.message || '生成内容失败')
    }
  } catch (error) {
    console.error('生成内容失败:', error)
    ElMessage.error('生成内容失败，请稍后重试')
  } finally {
    isGenerating.value = false
  }
}

// 应用单个优化建议
const applySingleSuggestion = (suggestion) => {
  emit('apply-suggestion', { type: 'optimization', data: suggestion })
  ElMessage.success('应用成功')
}

// 应用所有优化建议
const applyAllSuggestions = () => {
  if (resultType.value === 'optimization') {
    ElMessageBox.confirm('确定要应用所有AI建议到您的简历吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      emit('apply-all-suggestions', { type: resultType.value, data: aiResult.value })
      showResultDialog.value = false
      ElMessage.success('应用成功')
    }).catch(() => {})
  } else if (resultType.value === 'generation') {
    ElMessageBox.confirm('确定要应用所有生成内容到您的简历吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      emit('apply-all-suggestions', { type: resultType.value, data: aiResult.value })
      showResultDialog.value = false
      ElMessage.success('应用成功')
    }).catch(() => {})
  }
}

// 应用生成的个人简介
const applyGeneratedProfile = () => {
  emit('apply-suggestion', { 
    type: 'generation', 
    section: 'profile', 
    data: aiResult.value.profile 
  })
  ElMessage.success('应用成功')
}

// 应用生成的工作经验
const applyGeneratedWorkExperience = (exp, index) => {
  emit('apply-suggestion', { 
    type: 'generation', 
    section: 'workExperience', 
    index,
    data: exp 
  })
  ElMessage.success('应用成功')
}

// 应用生成的项目经验
const applyGeneratedProject = (proj, index) => {
  emit('apply-suggestion', { 
    type: 'generation', 
    section: 'project', 
    index,
    data: proj 
  })
  ElMessage.success('应用成功')
}

// 辅助方法
const getAnalysisTitle = (key) => {
  const titles = {
    basicInfo: '基本信息',
    education: '教育经历',
    workExperience: '工作经验',
    skills: '技能'
  }
  return titles[key] || key
}

const getSectionTitle = (section) => {
  const titles = {
    basicInfo: '基本信息',
    education: '教育经历',
    workExperience: '工作经验',
    skills: '技能'
  }
  return titles[section] || section
}
</script>

<style scoped>
.ai-sidebar {
  height: 100%;
  padding: 20px;
  overflow-y: auto;
}

.ai-sidebar-title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.ai-card {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s;
}

.ai-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 15px;
}

.action-btn {
  width: 100%;
}

.ai-result {
  padding: 10px;
}

.score-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
}

.score {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
  margin-right: 20px;
}

.score-label {
  font-size: 16px;
  margin-left: 5px;
}

.overall-comment {
  font-size: 16px;
  color: #333;
  flex: 1;
}

.analysis-item {
  margin-bottom: 15px;
  background-color: #f9f9f9;
  padding: 12px;
  border-radius: 6px;
}

.analysis-title {
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.analysis-content {
  color: #606266;
  line-height: 1.5;
}

.suggestion-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.suggestion-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.suggestion-original {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f0f0f0;
  border-radius: 4px;
}

.suggestion-new {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #ecf5ff;
  border-radius: 4px;
}

.suggestion-label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.suggestion-text {
  color: #606266;
  line-height: 1.5;
}

.suggestion-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.match-header {
  background-color: #f5f7fa;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 8px;
}

.match-score {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.match-score span {
  color: #409EFF;
  font-size: 24px;
}

.match-analysis {
  color: #606266;
  line-height: 1.5;
}

.match-list {
  padding-left: 20px;
  margin-bottom: 20px;
}

.match-list li {
  margin-bottom: 8px;
  color: #606266;
}

.generated-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.generated-header {
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.generated-content, 
.generated-description, 
.generated-responsibilities, 
.generated-highlights {
  margin-bottom: 15px;
}

.generated-content {
  color: #606266;
  line-height: 1.6;
  background-color: #ecf5ff;
  padding: 12px;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .ai-sidebar {
    padding: 15px;
  }
  
  .score-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .score {
    margin-bottom: 10px;
  }
}
</style> 