<template>
  <div class="templates-container">
    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="categories">
        <el-radio-group v-model="currentCategory" @change="handleCategoryChange">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button
            v-for="category in categories"
            :key="category.id"
            :label="category.id"
          >
            {{ category.name }}
          </el-radio-button>
        </el-radio-group>
      </div>

      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索模板"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 模板列表 -->
    <div class="templates-grid" v-loading="loading">
      <div
        v-for="template in templates"
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
                type="primary"
                circle
                @click.stop="(e) => handleShare(template, e)"
              >
                <el-icon><component :is="ShareIcon" /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
        layout="prev, pager, next"
      />
    </div>

    <!-- 分享对话框 -->
    <el-dialog
      v-model="showShareDialog"
      title="分享模板"
      width="360px"
      destroy-on-close
      center
    >
      <div class="share-dialog-content">
        <div class="share-tabs">
          <div
            class="share-tab"
            :class="{ active: shareType === 'wechat' }"
            @click="shareType = 'wechat'"
          >
            微信
          </div>
          <div
            class="share-tab"
            :class="{ active: shareType === 'qq' }"
            @click="shareType = 'qq'"
          >
            QQ
          </div>
        </div>
        
        <div class="qrcode-container">
          <qr-code
            :value="getShareUrl(shareType)"
            :size="200"
            level="H"
            render-as="svg"
          />
        </div>
        
        <div class="share-tips">
          {{ shareType === 'wechat' ? '请使用微信扫描二维码分享' : '请使用QQ扫描二维码分享' }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useTemplateStore } from '../stores/template'
import { ElMessage, ElMessageBox, ElDialog } from 'element-plus'
import { Share as ShareIcon } from '@element-plus/icons-vue'
import QrCode from 'qrcode.vue'
import {
  getTemplateCategories,
  getTemplates,
  shareTemplate
} from '../api/template'

const router = useRouter()
const userStore = useUserStore()
const templateStore = useTemplateStore()

// 数据
const categories = ref([])
const templates = ref([])
const total = ref(0)
const loading = ref(false)
const currentCategory = ref('')
const searchKeyword = ref('')
const page = ref(1)
const pageSize = ref(12)

// 添加分享相关的响应式变量
const showShareDialog = ref(false)
const currentShareTemplate = ref(null)
const shareType = ref('wechat')

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await getTemplateCategories()
    console.log('获取分类列表成功:', res)
    categories.value = res.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 获取模板列表
const fetchTemplates = async () => {
  loading.value = true
  templates.value = []
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      categoryId: currentCategory.value || undefined,
      keyword: searchKeyword.value || undefined
    }
    const res = await getTemplates(params)
    templates.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取模板列表失败:', error)
    ElMessage.error('获取模板列表失败')
  } finally {
    loading.value = false
  }
}

// 切换分类
const handleCategoryChange = (categoryId) => {
  currentCategory.value = categoryId
  page.value = 1
  fetchTemplates()
}

// 搜索
const handleSearch = () => {
  page.value = 1
  fetchTemplates()
}

// 页码变化
const handlePageChange = (newPage) => {
  page.value = newPage
  fetchTemplates()
}

// 修改分享处理函数
const handleShare = async (template, event) => {
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }

  try {
    // 如果用户不是VIP，显示提示但不中断程序
  if (!userStore.isVip) {
      ElMessage.warning('该功能仅对会员开放，请升级VIP')
    return
  }

    ElMessage.info('正在生成分享链接...')
    const res = await shareTemplate(template.id)
    if (res.code === 200 && res.data) {
      currentShareTemplate.value = {
        ...template,
        shareUrl: res.data.shareUrl
      }
      showShareDialog.value = true
    } else {
      throw new Error(res.message || '分享失败')
    }
  } catch (error) {
    console.error('分享模板失败:', error)
    ElMessage.error('分享功能暂时不可用，请稍后再试')
  }
}

// 生成分享链接
const getShareUrl = (type) => {
  if (!currentShareTemplate.value || !currentShareTemplate.value.shareUrl) {
    return 'https://example.com';  // 返回一个默认URL，防止错误
  }
  
  const url = encodeURIComponent(currentShareTemplate.value.shareUrl);
  const title = encodeURIComponent(`${currentShareTemplate.value.name} - 简历模板`);
  const description = encodeURIComponent('我发现了一个不错的简历模板，分享给你！');
  
  if (type === 'qq') {
    return `http://connect.qq.com/widget/shareqq/index.html?url=${url}&title=${title}&desc=${description}`;
  } else {
    // 微信分享直接返回原始链接，用于生成二维码
    return currentShareTemplate.value.shareUrl;
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

onMounted(() => {
  fetchCategories()
  fetchTemplates()
})
</script>

<style scoped>
.templates-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.filter-section {
  margin-bottom: 30px;
}

.categories {
  margin-bottom: 20px;
}

.search-section {
  display: flex;
  width: 100%;
  justify-content: flex-end;
}

.search-section .el-input {
  width: 300px;
}

@media (max-width: 768px) {
  .search-section .el-input {
    width: 100%;
  }
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.template-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
  cursor: pointer;
  background-color: #fff;
  display: flex;
  flex-direction: column;
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
  background: rgba(0, 0, 0, 0.5);
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
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.template-name {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.template-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
  font-size: 14px;
  margin-top: 10px;
}

.template-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 1200px) {
  .templates-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .templates-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .templates-grid {
    grid-template-columns: 1fr;
  }
  
  .template-image {
    padding-top: 120%;
  }
}

.share-dialog-content {
  padding: 20px 0;
}

.share-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.share-tab {
  padding: 8px 24px;
  cursor: pointer;
  position: relative;
  color: #666;
}

.share-tab.active {
  color: #409EFF;
}

.share-tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #409EFF;
}

.qrcode-container {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.share-tips {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 15px;
}
</style> 