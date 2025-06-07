import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import * as userApi from '../api/user'
import config from '../config/config.js'

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
    console.log('VIP状态计算:', userInfo.value?.role);
    // 根据用户角色判断会员状态:
    // role=1 是月度会员
    // role=2 是终身会员
    // 都属于VIP用户
    return userInfo.value?.role === 1 || userInfo.value?.role === 2;
  })
  const isAdmin = computed(() => userInfo.value?.role === 3) // 角色为3表示管理员
  const avatar = computed(() => {
    // 如果avatar是完整的URL（以http开头），则直接返回
    if (userInfo.value?.avatar && (userInfo.value.avatar.startsWith('http://') || userInfo.value.avatar.startsWith('https://'))) {
      return userInfo.value.avatar;
    }
    
    // 如果avatar存在但不是完整URL，则拼接MinIO服务器地址
    if (userInfo.value?.avatar) {
      // 使用配置文件中的MinIO地址
      const minioEndpoint = config.minioEndpoint;
      const bucket = 'resume-avatars';
      // 如果avatar已经包含了bucket路径，则直接拼接
      if (userInfo.value.avatar.includes('/')) {
        return `${minioEndpoint}/${userInfo.value.avatar}`;
      }
      // 否则，拼接bucket和avatar
      return `${minioEndpoint}/${bucket}/${userInfo.value.avatar}`;
    }
    
    // 如果avatar不存在，则返回默认头像
    return '/default-avatar.png';  // 替换为实际的默认头像路径
  })
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
        
        // 清除localStorage中的相关数据
        localStorage.removeItem('vipStatus')
        localStorage.removeItem('userInfo')
        
        return res
      }
      return Promise.reject(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 初始化：如果有token则加载用户信息
  if (token.value) {
    loadUserInfo().then(() => {
      // 加载完成后检查是否需要从localStorage恢复状态
      restoreFromLocalStorage()
    })
  } else {
    // 即使没有token，也尝试从localStorage恢复状态
    // 这对于页面刷新后但token已过期的情况很有用
    restoreFromLocalStorage()
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
    isAdmin,
    avatar,
    username,
    
    // 方法
    login,
    register,
    loadUserInfo,
    updateInfo,
    updateDetails,
    updateVipStatus,
    restoreFromLocalStorage,
    logout
  }
}) 