import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
  {
    path: '/',
      name: 'home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/templates',
    name: 'Templates',
    component: () => import('../views/Templates.vue')
  },
  {
    path: '/templates/:id',
    name: 'template-detail',
    component: () => import('../views/TemplateDetail.vue')
  },
  {
    path: '/create',
    name: 'Create',
      component: () => import('../views/Create.vue'),
      meta: {
        requiresAuth: true
      }
  },
  {
    path: '/ai',
    name: 'AI',
    component: () => import('../views/AI.vue')
  },
  {
    path: '/expert',
    name: 'Expert',
    component: () => import('../views/Expert.vue')
  },
  {
    path: '/vip',
    name: 'VipDetail',
    component: () => import('../views/VipDetail.vue')
  },
  {
    path: '/login',
      name: 'login',
      component: () => import('../views/auth/Login.vue'),
      meta: {
        requiresGuest: true
      }
  },
  {
    path: '/register',
      name: 'register',
      component: () => import('../views/auth/Register.vue'),
      meta: {
        requiresGuest: true
      }
  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('../views/user/ResumeList.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/user/Orders.vue')
  },
  {
    path: '/account',
    name: 'Account',
    component: () => import('../views/user/Account.vue')
  }
  ]
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // 已登录用户不能访问登录/注册页
  if (to.meta.requiresGuest && token) {
    next('/')
    return
  }
  
  next()
})

export default router
