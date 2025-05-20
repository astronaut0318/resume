import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import Prototypes from '../components/Prototypes.vue'
// 静态导入认证和模板组件
import Login from '../components/auth/Login.vue'
import Register from '../components/auth/Register.vue'
import TemplateMarket from '../components/templates/TemplateMarket.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/prototypes',
      name: 'prototypes',
      component: Prototypes
    },
    // 改为静态导入组件
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/templates',
      name: 'templates',
      component: TemplateMarket
    },
    // 添加一个临时的Dashboard路由
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Home  // 暂时使用Home组件作为Dashboard
    }
  ]
})

export default router