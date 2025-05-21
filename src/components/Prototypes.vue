<template>
  <div class="prototypes-container">
    <el-page-header content="原型预览" @back="goBack" />
    
    <div class="header-section">
      <h1 class="main-title">智能简历工场 - HTML 原型预览</h1>
      <p class="subtitle">所有核心界面将在此平铺展示，模拟浏览器窗口。</p>
    </div>

    <el-divider />

    <div class="prototype-list">
      <div v-for="(item, index) in interfaces" :key="index" class="prototype-item">
        <h2 class="prototype-title">{{ item.title }}</h2>
        <div class="browser-frame">
          <div class="browser-header">
            <div class="browser-controls">
              <span class="control-red"></span>
              <span class="control-yellow"></span>
              <span class="control-green"></span>
            </div>
            <div class="browser-address-bar">{{ item.url }}</div>
          </div>
          <iframe class="browser-content" :srcdoc="getIframeSrcdoc(item)"></iframe>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const interfaces = ref([
  { title: "1. 欢迎/着陆页", file: "landing_page.html", url: "https://example.com/smart-resume-factory/" },
  { title: "2. 用户注册页", file: "registration_page.html", url: "https://example.com/smart-resume-factory/register" },
  { title: "3. 用户登录页", file: "login_page.html", url: "https://example.com/smart-resume-factory/login" },
  { title: "4. 仪表盘 (无简历)", file: "dashboard_no_resume.html", url: "https://example.com/smart-resume-factory/dashboard" },
  { title: "5. 仪表盘 (有简历)", file: "dashboard_with_resumes.html", url: "https://example.com/smart-resume-factory/dashboard" },
  { title: "6. 模板市场", file: "template_market_page.html", url: "https://example.com/smart-resume-factory/templates" },
  { title: "7. 简历编辑器", file: "resume_editor_page.html", url: "https://example.com/smart-resume-factory/editor/new" },
  { title: "8. 用户个人资料", file: "profile_page.html", url: "https://example.com/smart-resume-factory/profile" }
]);

// 使用白色背景+蓝色文字替代原来的暗色背景
const getIframeSrcdoc = (item) => {
  return `<html><body style="background-color: #f8fafc; display:flex; justify-content:center; align-items:center; height:100vh; color:#409EFF; font-size:1.2rem; text-align:center; padding: 20px;">原型界面 (${item.file}) 将在此处加载。<br/>请确保 ${item.file} 已创建在 design/prototypes/ 目录下。</body></html>`;
};

const goBack = () => {
  router.push('/');
};
</script>

<style scoped>
.prototypes-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.header-section {
  text-align: center;
  margin: 20px 0 30px;
}

.main-title {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.subtitle {
  color: #606266;
  font-size: 16px;
}

.prototype-list {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.prototype-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.prototype-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #409EFF;
}

.browser-frame {
  width: 90%;
  max-width: 1100px;
  margin: 0 auto;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.browser-header {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: #F2F6FC;
  border-bottom: 1px solid #EBEEF5;
}

.browser-controls {
  display: flex;
}

.browser-controls span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 6px;
  display: block;
}

.control-red { background-color: #F56C6C; }
.control-yellow { background-color: #E6A23C; }
.control-green { background-color: #67C23A; }

.browser-address-bar {
  flex-grow: 1;
  background-color: white;
  color: #909399;
  border-radius: 4px;
  padding: 4px 8px;
  margin-left: 20px;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border: 1px solid #DCDFE6;
}

.browser-content {
  width: 100%;
  height: 70vh;
  border: none;
}
</style> 