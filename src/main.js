import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 导入API实例
import API from './api/config'

// 开发环境直接导入mock
if (import.meta.env.DEV) {
  // 直接导入mock模块
  import('./mock/index.js')
    .then(({ default: setupMock }) => {
      setupMock(API);
      console.log('Mock API 服务已启用');
    })
    .catch(error => {
      console.error('Mock 服务加载失败:', error);
    });
}

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(ElementPlus)
app.mount('#app')
