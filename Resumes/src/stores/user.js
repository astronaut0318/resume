import { defineStore } from 'pinia'
<<<<<<< HEAD
import { ref, computed } from 'vue'
=======
import { ref, computed, watch } from 'vue'
>>>>>>> upstream/master
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
<<<<<<< HEAD
  const isVip = computed(() => {
    // 根据数据库表vip_members的字段调整判断逻辑
    return userInfo.value?.role > 0 || vipInfo.value?.status === 1
  })
  const avatar = computed(() => userInfo.value?.avatar || '')
  const username = computed(() => userInfo.value?.username || '')

=======
  const isVip = computed(() => vipInfo.value?.isVip || false)
  const isAdmin = computed(() => userInfo.value?.role === 3) // 角色为3表示管理员
  const avatar = computed(() => userInfo.value?.avatar || '')
  const username = computed(() => userInfo.value?.username || '')

  // 监听VIP状态变化，同步到localStorage
  watch(vipInfo, (newVal) => {
    if (newVal) {
      localStorage.setItem('vipStatus', JSON.stringify(newVal))
    }
  }, { deep: true })

  // 监听用户信息变化，同步到localStorage
  watch(userInfo, (newVal) => {
    if (newVal) {
      localStorage.setItem('userInfo', JSON.stringify(newVal))
    }
  }, { deep: true })

>>>>>>> upstream/master
  // 方法
  // 登录
  const login = async (data) => {
    try {
      const res = await userApi.login(data)
      if (res.code === 200) {
        token.value = res.data.token
        userId.value = res.data.userId
<<<<<<< HEAD
        // 持久化到localStorage，保证后续请求都能带上token
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userId', res.data.userId)
=======
>>>>>>> upstream/master
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
<<<<<<< HEAD
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
=======
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
>>>>>>> upstream/master
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

<<<<<<< HEAD
=======
  // 直接更新VIP状态 (用于支付成功后立即更新状态)
  const updateVipStatus = (vipData) => {
    vipInfo.value = {
      ...vipInfo.value,
      ...vipData
    }
    // 不需要await，因为我们已经直接更新了状态
    // 实际环境中可能需要调用API
  }

  // 检查是否需要从localStorage恢复状态
  const restoreFromLocalStorage = () => {
    try {
      // 尝试从localStorage恢复VIP状态
      const savedVipStatus = localStorage.getItem('vipStatus')
      if (savedVipStatus && (!vipInfo.value || !vipInfo.value.isVip)) {
        vipInfo.value = JSON.parse(savedVipStatus)
      }

      // 尝试从localStorage恢复用户信息
      const savedUserInfo = localStorage.getItem('userInfo')
      if (savedUserInfo && !userInfo.value) {
        userInfo.value = JSON.parse(savedUserInfo)
      }
    } catch (error) {
      console.error('从localStorage恢复状态失败:', error)
    }
  }

>>>>>>> upstream/master
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
<<<<<<< HEAD
=======
        
        // 清除localStorage中的相关数据
        localStorage.removeItem('vipStatus')
        localStorage.removeItem('userInfo')
        
>>>>>>> upstream/master
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 初始化：如果有token则加载用户信息
  if (token.value) {
<<<<<<< HEAD
    loadUserInfo()
=======
    loadUserInfo().then(() => {
      // 加载完成后检查是否需要从localStorage恢复状态
      restoreFromLocalStorage()
    })
  } else {
    // 即使没有token，也尝试从localStorage恢复状态
    // 这对于页面刷新后但token已过期的情况很有用
    restoreFromLocalStorage()
>>>>>>> upstream/master
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
<<<<<<< HEAD
=======
    isAdmin,
>>>>>>> upstream/master
    avatar,
    username,
    
    // 方法
    login,
    register,
    loadUserInfo,
    updateInfo,
    updateDetails,
<<<<<<< HEAD
=======
    updateVipStatus,
    restoreFromLocalStorage,
>>>>>>> upstream/master
    logout
  }
}) 