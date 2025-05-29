import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as userApi from '../api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref(null)
  const userDetails = ref(null)
  const vipInfo = ref(null)
  const token = ref(localStorage.getItem('token'))
  const userId = ref(localStorage.getItem('userId'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isVip = computed(() => {
    // 根据数据库表vip_members的字段调整判断逻辑
    return userInfo.value?.role > 0 || vipInfo.value?.status === 1
  })
  const avatar = computed(() => userInfo.value?.avatar || '')
  const username = computed(() => userInfo.value?.username || '')

  // 方法
  // 登录
  const login = async (data) => {
    try {
      const res = await userApi.login(data)
      if (res.code === 200) {
        token.value = res.data.token
        userId.value = res.data.userId
        // 持久化到localStorage，保证后续请求都能带上token
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userId', res.data.userId)
        await loadUserInfo()
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 注册
  const register = async (data) => {
    try {
      const res = await userApi.register(data)
      if (res.code === 200) {
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 加载用户信息
  const loadUserInfo = async () => {
    if (!userId.value) return
    try {
      // 获取用户基本信息
      const userRes = await userApi.getUserInfo(userId.value)
      if (userRes.code === 200) {
        userInfo.value = userRes.data
      }
      
      try {
        // 尝试获取详细信息，如果失败不影响整体流程
        const detailsRes = await userApi.getUserDetails(userId.value)
        if (detailsRes.code === 200) {
          userDetails.value = detailsRes.data
        }
      } catch (detailError) {
        console.warn('获取用户详情失败，可能是表不存在:', detailError)
        // 设置默认空值，防止前端引用报错
        userDetails.value = {}
      }
      
      try {
        // 尝试获取VIP信息，如果失败不影响整体流程
        const vipRes = await userApi.getVipStatus(userId.value)
        if (vipRes.code === 200) {
          vipInfo.value = vipRes.data
        }
      } catch (vipError) {
        console.warn('获取VIP信息失败，可能是表不存在:', vipError)
        // 设置默认空值，防止前端引用报错
        vipInfo.value = {}
      }
    } catch (error) {
      console.error('加载用户基本信息失败:', error)
    }
  }

  // 更新用户信息
  const updateInfo = async (data) => {
    try {
      const res = await userApi.updateUserInfo(userId.value, data)
      if (res.code === 200) {
        await loadUserInfo()
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 更新用户详细信息
  const updateDetails = async (data) => {
    try {
      const res = await userApi.updateUserDetails(userId.value, data)
      if (res.code === 200) {
        await loadUserInfo()
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 退出登录
  const logout = async () => {
    try {
      const res = await userApi.logout()
      if (res.code === 200) {
        userInfo.value = null
        userDetails.value = null
        vipInfo.value = null
        token.value = null
        userId.value = null
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 初始化：如果有token则加载用户信息
  if (token.value) {
    loadUserInfo()
  }

  return {
    // 状态
    userInfo,
    userDetails,
    vipInfo,
    token,
    userId,
    
    // 计算属性
    isLoggedIn,
    isVip,
    avatar,
    username,
    
    // 方法
    login,
    register,
    loadUserInfo,
    updateInfo,
    updateDetails,
    logout
  }
}) 