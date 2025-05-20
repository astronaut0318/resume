<template>
  <div class="template-market-container">
    <!-- 页面顶部导航栏 -->
    <div class="navbar">
      <!-- 左侧Logo区域 -->
      <div class="navbar-logo">
        <i class="fas fa-file-alt"></i>
        <span>智能简历工场</span>
      </div>

      <!-- 中间菜单区域 -->
      <div class="navbar-menu">
        <router-link to="/" class="menu-item">首页</router-link>
        <router-link to="/templates" class="menu-item active">模板市场</router-link>
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

    <!-- 页面内容 -->
    <div class="main-content">
      <h1 class="page-title">简历模板市场</h1>
      
      <!-- 筛选条件 -->
      <el-card class="filter-card" shadow="hover">
        <div class="filter-form">
          <!-- 分类筛选 -->
          <div class="filter-item">
            <span class="filter-label">模板分类</span>
            <el-select v-model="filters.categoryId" placeholder="所有分类" class="filter-select">
              <el-option label="所有分类" value=""></el-option>
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </div>
          
          <!-- 价格筛选 -->
          <div class="filter-item">
            <span class="filter-label">价格</span>
            <el-select v-model="filters.isFree" placeholder="全部" class="filter-select">
              <el-option label="全部" value=""></el-option>
              <el-option label="免费模板" value="1"></el-option>
              <el-option label="付费模板" value="0"></el-option>
            </el-select>
          </div>
          
          <!-- 搜索框 -->
          <div class="filter-item search-item">
            <span class="filter-label">关键词搜索</span>
            <el-input 
              v-model="filters.keyword" 
              placeholder="搜索模板..." 
              class="search-input"
            >
              <template #suffix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
        
        <div class="filter-actions">
          <el-button 
            @click="searchTemplates" 
            type="primary"
            :icon="Search"
          >
            查询
          </el-button>
        </div>
      </el-card>
      
      <!-- 模板列表 -->
      <div class="template-grid">
        <el-card v-for="template in templates" :key="template.id" class="template-card" shadow="hover">
          <img :src="template.thumbnail" :alt="template.name" class="template-img">
          <div class="template-info">
            <div class="template-header">
              <h3 class="template-title">{{ template.name }}</h3>
              <el-tag v-if="template.isFree" type="success" size="small" effect="light">免费</el-tag>
              <el-tag v-else type="info" size="small" effect="light">¥{{ template.price.toFixed(2) }}</el-tag>
            </div>
            <p class="template-desc">{{ template.description }}</p>
            <div class="template-footer">
              <el-text type="info" size="small">下载次数: {{ template.downloads }}</el-text>
              <el-button link type="primary" size="small">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="size"
          @current-change="handlePageChange"
        />
      </div>
    </div>
    
    <el-footer class="main-footer">
      <p>&copy; 2024 智能简历工场. 保留所有权利</p>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { Search } from '@element-plus/icons-vue';

// 导航激活项
const activeIndex = ref('templates');

// 模板分类
const categories = ref([
  { id: 1, name: '简约风格' },
  { id: 2, name: '商务风格' },
  { id: 3, name: '创意风格' }
]);

// 筛选条件
const filters = reactive({
  categoryId: '',
  isFree: '',
  keyword: ''
});

// 分页
const page = ref(1);
const size = ref(12);
const total = ref(0);
const totalPages = ref(1);

// 模板数据
const templates = ref([
  {
    id: 1,
    categoryId: 1,
    name: '简约风格简历模板',
    thumbnail: 'https://images.unsplash.com/photo-1586281380117-5a60ae2050cc',
    price: 0.00,
    isFree: 1,
    downloads: 1000,
    description: '简约大气的简历模板',
    status: 1
  },
  {
    id: 2,
    categoryId: 2,
    name: '商务风格简历模板',
    thumbnail: 'https://images.unsplash.com/photo-1610563166126-1526f247b699',
    price: 19.90,
    isFree: 0,
    downloads: 500,
    description: '专业商务风格简历模板',
    status: 1
  },
  {
    id: 3,
    categoryId: 3,
    name: '创意个性简历模板',
    thumbnail: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853',
    price: 29.90,
    isFree: 0,
    downloads: 300,
    description: '富有创意的个性化简历模板',
    status: 1
  },
  // 更多模板...
]);

// 获取模板列表
const fetchTemplates = async () => {
  try {
    // 这里调用API获取模板列表
    // 根据API文档: GET /api/templates
    console.log('Fetching templates with filters:', filters);
    console.log('Page:', page.value, 'Size:', size.value);
    
    // 实际项目中应使用真实API
    // 这里使用模拟数据
    total.value = 20;
    totalPages.value = Math.ceil(total.value / size.value);
    
    // 模拟API调用
    // const response = await fetch(`/api/templates?page=${page.value}&size=${size.value}&categoryId=${filters.categoryId}&isFree=${filters.isFree}&keyword=${filters.keyword}`);
    // const result = await response.json();
    // if (result.code === 200) {
    //   templates.value = result.data.list;
    //   total.value = result.data.total;
    //   totalPages.value = Math.ceil(total.value / size.value);
    // }
  } catch (error) {
    console.error('获取模板列表失败', error);
  }
};

// 搜索模板
const searchTemplates = () => {
  page.value = 1; // 重置到第一页
  fetchTemplates();
};

// 切换页码
const handlePageChange = (newPage) => {
  page.value = newPage;
  fetchTemplates();
};

onMounted(() => {
  // 获取分类列表
  // 实际项目中应调用API: GET /api/templates/categories
  
  // 获取模板列表
  fetchTemplates();
});
</script>

<style scoped>
.template-market-container {
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

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 30px;
}

.filter-card {
  margin-bottom: 30px;
  background-color: #f2f6fc;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.filter-item {
  display: flex;
  flex-direction: column;
}

.filter-label {
  font-size: 14px;
  margin-bottom: 8px;
  color: #606266;
}

.filter-select {
  width: 200px;
}

.search-item {
  flex-grow: 1;
  max-width: 300px;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.template-card {
  display: flex;
  flex-direction: column;
  transition: transform 0.3s;
}

.template-card:hover {
  transform: translateY(-5px);
}

.template-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.template-info {
  padding: 15px;
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.template-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.template-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
}

.template-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.main-footer {
  background-color: #f5f7fa;
  text-align: center;
  padding: 20px;
  color: #909399;
}

@media screen and (max-width: 768px) {
  .navbar {
    flex-direction: column;
    height: auto;
    padding: 10px;
  }
  
  .navbar-menu {
    margin: 10px 0;
  }
  
  .filter-form {
    flex-direction: column;
    gap: 15px;
  }
  
  .filter-select,
  .search-input {
    width: 100%;
  }
  
  .template-grid {
    grid-template-columns: repeat(auto-fill, minmax(100%, 1fr));
  }
}
</style> 