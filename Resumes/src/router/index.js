import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

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
      meta: { requiresAuth: true }
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
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/auth/Register.vue'),
      meta: { requiresGuest: true }
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
    },
    {
      path: '/files',
      name: 'FileManager',
      component: () => import('../views/user/FileManager.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notifications',
      name: 'Notifications',
      component: () => import('../views/Notification.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notifications/:id',
      name: 'NotificationDetail',
      component: () => import('../views/NotificationDetail.vue'),
      meta: { requiresAuth: true }
    },
    // 管理员路由
    {
      path: '/admin',
      name: 'AdminLayout',
      component: () => import('../views/admin/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          name: 'AdminDashboard',
          component: () => import('../views/admin/Dashboard.vue')
        },
        {
          path: 'users',
          name: 'AdminUsers',
          component: () => import('../views/admin/Users.vue')
        },
        {
          path: 'template-categories',
          name: 'AdminTemplateCategories',
          component: () => import('../views/admin/TemplateCategories.vue')
        }
      ]
    },
    // 404页面
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/NotFound.vue')
    }
  ]
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStore = useUserStore()
  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    ElMessage.error('您没有管理员权限')
    next('/')
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
