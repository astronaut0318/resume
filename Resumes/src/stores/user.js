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
  const isVip = computed(() => vipInfo.value?.isVip || false)
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
      const [userRes, detailsRes, vipRes] = await Promise.all([
        userApi.getUserInfo(userId.value),
        userApi.getUserDetails(userId.value),
        userApi.getVipStatus(userId.value)
      ])
      
      if (userRes.code === 200) {
        userInfo.value = userRes.data
      }
      if (detailsRes.code === 200) {
        userDetails.value = detailsRes.data
      }
      if (vipRes.code === 200) {
        vipInfo.value = vipRes.data
      }
    } catch (error) {
      console.error('加载用户信息失败:', error)
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