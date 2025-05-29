<template>
  <div class="create-container">
    <div class="editor-container" :class="{'sidebar-visible': sidebarVisible}">
      <!-- 左侧编辑区域 -->
      <div class="editor-panel">
        <h2 class="editor-title">{{ isEdit ? '编辑简历' : '创建简历' }}</h2>
        
        <!-- 文本样式工具栏 -->
        <div class="text-format-toolbar">
          <div class="toolbar-group">
            <span class="toolbar-label">字体样式：</span>
            <el-button-group>
              <el-button size="small" title="加粗" @click="applyTextFormat('bold', $event)" :class="{'format-active': currentStyles.bold}">
                <el-icon><i class="el-icon-bold">B</i></el-icon>
              </el-button>
              <el-button size="small" title="斜体" @click="applyTextFormat('italic', $event)" :class="{'format-active': currentStyles.italic}">
                <el-icon><i class="el-icon-italic">I</i></el-icon>
              </el-button>
              <el-button size="small" title="下划线" @click="applyTextFormat('underline', $event)" :class="{'format-active': currentStyles.underline}">
                <el-icon><i class="el-icon-underline">U</i></el-icon>
              </el-button>
            </el-button-group>
          </div>
          
          <div class="toolbar-group">
            <span class="toolbar-label">对齐方式：</span>
            <el-button-group>
              <el-button size="small" title="左对齐" @click="applyTextFormat('align-left', $event)" :class="{'format-active': currentStyles.align === 'left'}">
                <el-icon><i class="el-icon-align-left">L</i></el-icon>
              </el-button>
              <el-button size="small" title="居中" @click="applyTextFormat('align-center', $event)" :class="{'format-active': currentStyles.align === 'center'}">
                <el-icon><i class="el-icon-align-center">C</i></el-icon>
              </el-button>
              <el-button size="small" title="右对齐" @click="applyTextFormat('align-right', $event)" :class="{'format-active': currentStyles.align === 'right'}">
                <el-icon><i class="el-icon-align-right">R</i></el-icon>
              </el-button>
            </el-button-group>
          </div>
          
          <div class="toolbar-group">
            <span class="toolbar-label">字体颜色：</span>
            <div class="color-blocks">
              <div 
                v-for="(color, index) in predefinedColors" 
                :key="index" 
                class="color-block" 
                :style="{ backgroundColor: color }"
                :class="{ 'color-selected': textColor === color }"
                @click.stop="applyTextColor(color)"
                :title="colorNames[index]"
              ></div>
            </div>
          </div>
          
          <div class="toolbar-group">
            <span class="toolbar-label">字体大小：</span>
            <el-select 
              v-model="fontSize" 
              size="small" 
              @change="applyFontSize"
              style="width: 90px"
            >
              <el-option label="小" value="small"></el-option>
              <el-option label="默认" value="default"></el-option>
              <el-option label="大" value="large"></el-option>
              <el-option label="超大" value="x-large"></el-option>
            </el-select>
          </div>
          
          <!-- 新增AI快写功能 -->
          <div class="toolbar-group ai-write-container">
            <el-dropdown @command="handleAIWriteCommand" trigger="click">
              <el-button type="primary" size="small">
                AI快写 <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="补充内容">补充内容</el-dropdown-item>
                  <el-dropdown-item command="缩写内容">缩写内容</el-dropdown-item>
                  <el-dropdown-item command="调整文风">调整文风</el-dropdown-item>
                  <el-dropdown-item command="自定义指令" divided>自定义指令</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        
        <!-- 工具栏使用提示 -->
        <el-alert
          title="使用提示：点击右侧预览区域的文本内容，然后使用上方工具栏修改样式。所有预览中的文本内容均可编辑样式。"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        />
        
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
          <el-button type="primary" size="small" @click="toggleAISidebar">
            <template v-if="sidebarVisible">关闭AI助手</template>
            <template v-else>打开AI助手</template>
          </el-button>
        </div>
        
        <div class="preview-content">
          <div class="resume-preview">
            <h1 class="preview-title editable-content">{{ resumeData.content.basicInfo.name || '姓名' }}</h1>
            
            <div class="preview-contact">
              <span v-if="resumeData.content.basicInfo.phone" class="editable-content">{{ resumeData.content.basicInfo.phone }}</span>
              <span v-if="resumeData.content.basicInfo.email" class="editable-content">{{ resumeData.content.basicInfo.email }}</span>
              <span v-if="resumeData.content.basicInfo.address" class="editable-content">{{ resumeData.content.basicInfo.address }}</span>
            </div>
            
            <div v-if="resumeData.content.education.length > 0" class="preview-section">
              <h2 class="editable-content">教育经历</h2>
              <div v-for="(edu, index) in resumeData.content.education" :key="index" class="preview-item">
                <div class="preview-item-header">
                  <div class="preview-item-title editable-content">{{ edu.school }}</div>
                  <div class="preview-item-date editable-content">{{ edu.startDate }} - {{ edu.endDate }}</div>
                </div>
                <div class="preview-item-subtitle editable-content">{{ edu.major }} | {{ edu.degree }}</div>
                <div v-if="edu.description" class="preview-item-desc editable-content">{{ edu.description }}</div>
              </div>
            </div>
            
            <div v-if="resumeData.content.workExperience.length > 0" class="preview-section">
              <h2 class="editable-content">工作经验</h2>
              <div v-for="(work, index) in resumeData.content.workExperience" :key="index" class="preview-item">
                <div class="preview-item-header">
                  <div class="preview-item-title editable-content">{{ work.company }}</div>
                  <div class="preview-item-date editable-content">{{ work.startDate }} - {{ work.endDate }}</div>
                </div>
                <div class="preview-item-subtitle editable-content">{{ work.position }}</div>
                <div class="preview-item-desc editable-content">{{ work.description }}</div>
              </div>
            </div>
            
            <div v-if="resumeData.content.skills.length > 0" class="preview-section">
              <h2 class="editable-content">技能</h2>
              <div class="preview-skills">
                <el-tag
                  v-for="(skill, index) in resumeData.content.skills"
                  :key="index"
                  class="preview-skill-tag editable-content"
                >
                  {{ skill }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- AI 辅助侧边栏 -->
      <div class="ai-sidebar-container" v-show="sidebarVisible">
        <AiSidebar 
          :resumeId="resumeId" 
          :resumeData="resumeData"
          @apply-suggestion="handleApplySuggestion"
          @apply-all-suggestions="handleApplyAllSuggestions"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useResumeStore } from '../stores/resume'
import { useTemplateStore } from '../stores/template'
import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'
import { Plus, Delete, ArrowDown } from '@element-plus/icons-vue'
import AiSidebar from '../components/ai/AiSidebar.vue'
// 导入AI服务API
import { optimizeResume } from '../api/ai'

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
const textColor = ref('#000000')
const fontSize = ref('default')
const selectedElement = ref(null)
const sidebarVisible = ref(false)  // AI侧边栏显示状态
const currentStyles = reactive({
  bold: false,
  italic: false,
  underline: false,
  align: 'left',
  color: '#000000',
  size: 'default'
})

// AI快写相关状态
const aiWriteProcessing = ref(false)
const aiWriteCustomPrompt = ref('')

// 预定义颜色
const predefinedColors = [
  '#1976D2', // 蓝色
  '#424242', // 深灰色
  '#4285F4', // 谷歌蓝
  '#F44336', // 红色
  '#4CAF50', // 绿色
  '#212121', // 黑色
  '#FF9800', // 橙色
  '#A1887F', // 棕灰色
  '#D32F2F', // 深红色
  '#795548', // 棕色
  '#00BCD4', // 青色
  '#3F51B5', // 靛蓝色
  '#FFEB3B', // 黄色
  '#9C27B0', // 紫色
  '#FF9900'  // 橙黄色
]

// 颜色名称
const colorNames = [
  '蓝色', '深灰色', '谷歌蓝', '红色', '绿色',
  '黑色', '橙色', '棕灰色', '深红色', '棕色',
  '青色', '靛蓝色', '黄色', '紫色', '橙黄色'
]

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
    
    // 添加点击事件监听，用于选择元素
    nextTick(() => {
      const previewContent = document.querySelector('.preview-content');
      if (previewContent) {
        previewContent.addEventListener('click', selectElement);
      }
    });
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
    let result
    
    if (isEdit.value) {
      // 更新已有简历
      result = await resumeStore.updateResume({
        id: resumeId.value,
        ...resumeData
      })
      ElMessage.success('简历更新成功')
    } else {
      // 创建新简历
      result = await resumeStore.createResume(resumeData)
      // 更新resumeId，以便AI功能可以使用
      resumeId.value = result.id
      // 更新路由，但不重新加载页面
      router.replace({ 
        path: '/create', 
        query: { resumeId: result.id } 
      })
      ElMessage.success('简历创建成功')
    }
    
    // 显示自动保存完成的通知
    ElNotification({
      title: '简历已保存',
      message: '您的简历已成功保存',
      type: 'success',
      duration: 2000
    })
    
    // 如果是新建模式，保存后切换到编辑模式
    if (!isEdit.value) {
      resumeId.value = result.id
    }
  } catch (error) {
    console.error('保存简历失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

// 取消
const cancel = () => {
  router.push('/resume')
}

// 应用文本格式
const applyTextFormat = (format, event) => {
  // 阻止事件冒泡和默认行为，避免失去选中状态
  if (event) {
    event.preventDefault();
    event.stopPropagation();
  }
  
  if (!selectedElement.value) {
    ElMessage.warning('请先选择要修改的文本内容');
    return;
  }
  
  const element = selectedElement.value;
  
  // 处理文本样式
  if (format === 'bold') {
    currentStyles.bold = !currentStyles.bold;
    applyTextStyleToElement(element, 'fontWeight', currentStyles.bold ? 'bold' : 'normal');
  } else if (format === 'italic') {
    currentStyles.italic = !currentStyles.italic;
    applyTextStyleToElement(element, 'fontStyle', currentStyles.italic ? 'italic' : 'normal');
  } else if (format === 'underline') {
    currentStyles.underline = !currentStyles.underline;
    applyTextStyleToElement(element, 'textDecoration', currentStyles.underline ? 'underline' : 'none');
  }
  
  // 处理对齐方式
  else if (format.startsWith('align-')) {
    const alignment = format.replace('align-', '');
    currentStyles.align = alignment;
    applyTextStyleToElement(element, 'textAlign', alignment);
  }
}

// 应用文本颜色
const applyTextColor = (color, event) => {
  // 阻止事件冒泡和默认行为
  if (event) {
    event.preventDefault();
    event.stopPropagation();
  }
  
  if (!selectedElement.value) {
    ElMessage.warning('请先选择要修改的文本内容');
    return;
  }
  
  // 更新当前颜色
  textColor.value = color;
  currentStyles.color = color;
  
  // 应用颜色到选中元素
  applyTextStyleToElement(selectedElement.value, 'color', color);
}

// 应用字体大小
const applyFontSize = (size) => {
  if (!selectedElement.value) {
    ElMessage.warning('请先选择要修改的文本内容');
    return;
  }
  
  const sizeMappings = {
    'small': '12px',
    'default': '14px',
    'large': '18px',
    'x-large': '24px'
  };
  
  currentStyles.size = size;
  applyTextStyleToElement(selectedElement.value, 'fontSize', sizeMappings[size]);
}

// 通用样式应用方法
const applyTextStyleToElement = (element, styleName, styleValue) => {
  // 如果是可编辑内容或其子元素，直接应用样式
  if (element.classList.contains('editable-content') || element.closest('.editable-content')) {
    element.style[styleName] = styleValue;
    return;
  }
  
  // 如果是预览内容区域中的元素，查找其最近的可编辑内容应用样式
  if (element.closest('.preview-content')) {
    const editableParent = element.closest('.editable-content');
    if (editableParent) {
      editableParent.style[styleName] = styleValue;
      // 更新选中元素为找到的可编辑元素
      selectedElement.value = editableParent;
      return;
    }
  }
  
  // 如果没有找到可编辑元素，尝试在当前元素的子元素中查找第一个可编辑元素
  const firstEditableChild = element.querySelector('.editable-content');
  if (firstEditableChild) {
    firstEditableChild.style[styleName] = styleValue;
    // 更新选中元素为找到的可编辑元素
    selectedElement.value = firstEditableChild;
    return;
  }
  
  // 最后尝试直接在当前元素上应用样式
  element.style[styleName] = styleValue;
}

// 选择元素
const selectElement = (event) => {
  // 阻止冒泡，避免点击工具栏时丢失选中状态
  if (event.target.closest('.text-format-toolbar')) {
    return;
  }
  
  let targetElement = event.target;
  
  // 优先选择可编辑内容
  // 如果点击的不是可编辑内容，但它在可编辑内容内部或它是可编辑内容的父元素
  if (!targetElement.classList.contains('editable-content')) {
    // 检查是否点击的是可编辑内容的内部元素
    const editableParent = targetElement.closest('.editable-content');
    if (editableParent) {
      targetElement = editableParent;
    } else {
      // 检查是否点击的是可编辑内容的父元素
      const editableChild = targetElement.querySelector('.editable-content');
      if (editableChild) {
        targetElement = editableChild;
      }
    }
  }
  
  // 更新当前选中元素
  selectedElement.value = targetElement;
  
  // 高亮显示选中的元素
  if (selectedElement.value) {
    // 移除之前的选中效果
    document.querySelectorAll('.resume-preview .element-selected').forEach(el => {
      el.classList.remove('element-selected');
    });
    
    // 添加新的选中效果
    selectedElement.value.classList.add('element-selected');
    
    // 更新当前样式状态
    const computedStyle = window.getComputedStyle(selectedElement.value);
    
    currentStyles.bold = computedStyle.fontWeight === 'bold' || parseInt(computedStyle.fontWeight) >= 700;
    currentStyles.italic = computedStyle.fontStyle === 'italic';
    currentStyles.underline = computedStyle.textDecoration.includes('underline');
    currentStyles.align = computedStyle.textAlign || 'left';
    currentStyles.color = textColor.value = computedStyle.color;
    
    // 更新字体大小
    const currentFontSize = computedStyle.fontSize;
    if (currentFontSize === '12px') fontSize.value = 'small';
    else if (currentFontSize === '14px') fontSize.value = 'default';
    else if (currentFontSize === '18px') fontSize.value = 'large';
    else if (currentFontSize === '24px') fontSize.value = 'x-large';
    else fontSize.value = 'default';
  }
}

// 切换AI侧边栏
const toggleAISidebar = () => {
  sidebarVisible.value = !sidebarVisible.value
}

// 处理AI建议应用
const handleApplySuggestion = (suggestion) => {
  // 根据建议类型应用相应内容
  if (suggestion.type === 'optimization') {
    // 应用优化建议
    const { section, item, suggestion: content } = suggestion.data
    
    if (section === 'basicInfo') {
      // 基本信息的优化
      resumeData.content.basicInfo = { ...resumeData.content.basicInfo, ...content }
    } else if (section === 'workExperience' && item !== undefined) {
      // 特定工作经验项的优化
      if (resumeData.content.workExperience[item]) {
        resumeData.content.workExperience[item].description = content
      }
    } else if (section === 'education' && item !== undefined) {
      // 特定教育经历项的优化
      if (resumeData.content.education[item]) {
        resumeData.content.education[item].description = content
      }
    } else if (section === 'skills') {
      // 技能优化可能是建议调整描述方式
      ElMessage.info(content)
    }
  } else if (suggestion.type === 'generation') {
    // 应用生成内容
    const { section, index, data } = suggestion
    
    if (section === 'profile') {
      // 个人简介，可以添加到基本信息中
      resumeData.content.basicInfo.summary = data
    } else if (section === 'workExperience') {
      // 生成的工作经验
      const workExp = {
        company: data.company,
        position: data.position,
        startDate: '',  // 需要用户填写
        endDate: '',    // 需要用户填写
        description: data.responsibilities.join('\n')
      }
      resumeData.content.workExperience.push(workExp)
    } else if (section === 'project') {
      // 生成的项目经验，我们可以添加到工作经验中
      const projectExp = {
        company: data.name,
        position: '项目经验',
        startDate: '',  // 需要用户填写
        endDate: '',    // 需要用户填写
        description: `${data.description}\n\n亮点:\n${data.highlights.map(h => `• ${h}`).join('\n')}`
      }
      resumeData.content.workExperience.push(projectExp)
    }
  }
}

// 处理应用所有AI建议
const handleApplyAllSuggestions = (data) => {
  if (data.type === 'optimization') {
    // 应用所有优化建议
    data.data.suggestions.forEach(suggestion => {
      handleApplySuggestion({ type: 'optimization', data: suggestion })
    })
  } else if (data.type === 'generation') {
    // 应用所有生成内容
    // 个人简介
    if (data.data.profile) {
      handleApplySuggestion({ 
        type: 'generation', 
        section: 'profile', 
        data: data.data.profile 
      })
    }
    
    // 工作经验
    if (data.data.workExperience) {
      data.data.workExperience.forEach((exp, index) => {
        handleApplySuggestion({ 
          type: 'generation', 
          section: 'workExperience', 
          index, 
          data: exp 
        })
      })
    }
    
    // 项目经验
    if (data.data.projects) {
      data.data.projects.forEach((proj, index) => {
        handleApplySuggestion({ 
          type: 'generation', 
          section: 'project', 
          index, 
          data: proj 
        })
      })
    }
  }
}

// 处理AI快写命令
const handleAIWriteCommand = async (command) => {
  if (!selectedElement.value) {
    ElMessage.warning('请先选择要修改的文本内容')
    return
  }
  
  const element = selectedElement.value
  const originalText = element.textContent.trim()
  
  if (!originalText) {
    ElMessage.warning('选中的内容为空，无法进行AI快写')
    return
  }
  
  if (command === '自定义指令') {
    // 打开自定义指令输入对话框
    ElMessageBox.prompt('请输入AI处理指令', 'AI快写', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：用更专业的语言重写',
    }).then(({ value }) => {
      if (value) {
        aiWriteCustomPrompt.value = value
        processAIWrite(originalText, value)
      }
    }).catch(() => {})
    return
  }
  
  // 根据命令处理不同的AI写作任务
  let prompt = ''
  switch (command) {
    case '补充内容':
      prompt = '扩展并丰富以下内容，使其更加详细和专业'
      break
    case '缩写内容':
      prompt = '精简以下内容，保留关键信息，使其更加简洁'
      break
    case '调整文风':
      prompt = '用更专业的语言和词汇重写以下内容，使其更符合正式简历的表达风格'
      break
    default:
      prompt = '优化以下内容'
  }
  
  processAIWrite(originalText, prompt)
}

// 处理AI写作请求
const processAIWrite = async (text, prompt) => {
  try {
    aiWriteProcessing.value = true
    
    // 创建一个临时标记，显示在元素上表示正在处理
    const selectedEl = selectedElement.value
    const originalHtml = selectedEl.innerHTML
    selectedEl.innerHTML = `<span style="opacity: 0.6">${originalHtml}</span><span class="ai-processing-indicator"> (AI处理中...)</span>`
    
    // 无需检查resumeId，直接使用临时ID或构造请求
    // 直接使用文本内容处理，而不依赖于已保存的简历
    const response = await optimizeResume(
      resumeId.value || -1, // 使用-1作为临时ID，表示未保存的简历
      null,
      {
        customPrompt: prompt,
        textContent: text
      },
      resumeData // 传递当前简历数据
    )
    
    if (response.data.code === 200 && response.data.data.suggestions && response.data.data.suggestions.length > 0) {
      // 提取优化后的内容
      const newContent = response.data.data.suggestions[0].suggestion
      
      // 显示对比确认框
      ElMessageBox.confirm(
        `<div class="ai-result-comparison">
          <div class="comparison-item">
            <div class="comparison-label">原内容:</div>
            <div class="comparison-text original">${text}</div>
          </div>
          <div class="comparison-item">
            <div class="comparison-label">AI生成内容:</div>
            <div class="comparison-text generated">${newContent}</div>
          </div>
        </div>`,
        'AI快写结果',
        {
          confirmButtonText: '应用',
          cancelButtonText: '取消',
          dangerouslyUseHTMLString: true,
          customClass: 'ai-write-dialog'
        }
      ).then(() => {
        // 应用新内容
        selectedEl.textContent = newContent
        ElMessage.success('已应用AI生成内容')
      }).catch(() => {
        // 恢复原始内容
        selectedEl.innerHTML = originalHtml
      })
    } else {
      // 恢复原始内容
      selectedEl.innerHTML = originalHtml
      ElMessage.error(response.data.message || 'AI处理失败')
    }
  } catch (error) {
    console.error('AI快写处理失败:', error)
    if (selectedElement.value) {
      selectedElement.value.innerHTML = text
    }
    ElMessage.error('AI处理失败，请稍后重试')
  } finally {
    aiWriteProcessing.value = false
  }
}
</script>

<style scoped>
.create-container {
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
}

.editor-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  height: calc(100vh - 130px);
  transition: grid-template-columns 0.3s ease;
}

/* 添加AI侧边栏显示时的样式 */
.editor-container.sidebar-visible {
  grid-template-columns: 0.8fr 0.8fr 0.4fr;
}

.editor-panel, .preview-panel {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow-y: auto;
}

/* AI侧边栏容器 */
.ai-sidebar-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  transition: width 0.3s ease;
}

.editor-title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.preview-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

/* 文本格式工具栏样式 */
.text-format-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  padding: 10px 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
  align-items: center;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 5px;
}

.toolbar-label {
  font-size: 13px;
  color: #606266;
}

/* 颜色块样式 */
.color-blocks {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  max-width: 240px;
}

.color-block {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  transition: all 0.2s ease;
}

.color-block:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.color-selected {
  box-shadow: 0 0 0 2px #409eff;
}

.el-icon-bold, .el-icon-italic, .el-icon-underline,
.el-icon-align-left, .el-icon-align-center, .el-icon-align-right {
  font-style: normal;
  font-weight: bold;
}

.el-icon-italic {
  font-style: italic;
}

.el-icon-underline {
  text-decoration: underline;
}

/* 已激活的样式按钮 */
.format-active {
  color: #409eff;
  background-color: #ecf5ff;
}

/* 选中元素样式 */
.element-selected {
  outline: 2px dashed #409eff;
  outline-offset: 2px;
}

/* 可编辑内容样式 */
.editable-content {
  cursor: pointer;
  transition: background-color 0.2s ease;
  border-radius: 2px;
}

.editable-content:hover {
  background-color: rgba(64, 158, 255, 0.1);
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

.ai-write-container {
  margin-left: auto;
}
</style>

<style>
/* 全局样式，放在非scoped区域 */
.ai-write-dialog .el-message-box__content {
  max-height: 400px;
  overflow-y: auto;
}

.ai-result-comparison {
  text-align: left;
}

.comparison-item {
  margin-bottom: 15px;
}

.comparison-label {
  font-weight: bold;
  margin-bottom: 5px;
}

.comparison-text {
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
}

.comparison-text.original {
  background-color: #f5f7fa;
  color: #606266;
}

.comparison-text.generated {
  background-color: #ecf5ff;
  color: #409EFF;
}

.ai-processing-indicator {
  color: #E6A23C;
  font-style: italic;
}
</style> 