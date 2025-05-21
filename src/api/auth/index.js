import API from '../config';
import loginAPI from './login';
import refreshAPI from './refresh';
import logoutAPI from './logout';
import passwordAPI from './password';

// 认证服务工具函数
import { isAuthenticated, getCurrentUserId, getCurrentUsername } from './utils';

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} - 包含登录成功后的用户信息和令牌的Promise
 * 
 * API格式:
 * POST /api/auth/login
 * 
 * 成功响应:
 * {
 *   "code": 200,
 *   "message": "登录成功",
 *   "data": {
 *     "userId": 1,
 *     "username": "zhangsan",
 *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *     "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *     "expiresIn": 7200
 *   }
 * }
 */
export const login = (data) => {
  return API.post('/auth/login', data).then(res => {
    // 保存token到localStorage
    if (res.code === 200 && res.data) {
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('refreshToken', res.data.refreshToken);
      localStorage.setItem('userId', res.data.userId);
      localStorage.setItem('username', res.data.username);
      localStorage.setItem('expiresIn', res.data.expiresIn);
      
      // 设置token过期时间
      const expirationTime = new Date().getTime() + res.data.expiresIn * 1000;
      localStorage.setItem('tokenExpiration', expirationTime);
    }
    return res;
  });
};

/**
 * 刷新令牌
 * @param {string} refreshToken - 刷新令牌
 * @returns {Promise} - 包含新令牌的Promise
 * 
 * API格式:
 * POST /api/auth/refresh
 * 
 * 成功响应:
 * {
 *   "code": 200,
 *   "message": "刷新成功",
 *   "data": {
 *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *     "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *     "expiresIn": 7200
 *   }
 * }
 */
export const refreshToken = (refreshTokenValue = null) => {
  // 如果未提供refreshToken，则从localStorage获取
  const tokenToUse = refreshTokenValue || localStorage.getItem('refreshToken');
  
  if (!tokenToUse) {
    return Promise.reject({
      code: 401,
      message: '刷新令牌不存在，请重新登录'
    });
  }
  
  return API.post('/auth/refresh', { refreshToken: tokenToUse }).then(res => {
    // 更新localStorage中的token
    if (res.code === 200 && res.data) {
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('refreshToken', res.data.refreshToken);
      localStorage.setItem('expiresIn', res.data.expiresIn);
      
      // 更新token过期时间
      const expirationTime = new Date().getTime() + res.data.expiresIn * 1000;
      localStorage.setItem('tokenExpiration', expirationTime);
    }
    return res;
  });
};

/**
 * 退出登录
 * @returns {Promise}
 * 
 * API格式:
 * POST /api/auth/logout
 * 
 * 成功响应:
 * {
 *   "code": 200,
 *   "message": "退出成功",
 *   "data": null
 * }
 */
export const logout = () => {
  return API.post('/auth/logout').then(res => {
    // 无论请求是否成功，都清除本地存储的认证信息
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    localStorage.removeItem('expiresIn');
    localStorage.removeItem('tokenExpiration');
    
    return res;
  });
};

/**
 * 修改密码
 * @param {Object} data - 密码信息
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise}
 * 
 * API格式:
 * PUT /api/auth/password
 * 
 * 成功响应:
 * {
 *   "code": 200,
 *   "message": "密码修改成功",
 *   "data": null
 * }
 */
export const changePassword = (data) => {
  if (!data.oldPassword || !data.newPassword) {
    return Promise.reject({
      code: 400,
      message: '请求参数错误：旧密码和新密码不能为空'
    });
  }
  
  return API.put('/auth/password', data);
};

/**
 * 检查用户是否已登录
 * @returns {boolean} - 用户是否已登录
 */
export const isAuthenticated = () => {
  const token = localStorage.getItem('token');
  const expiration = localStorage.getItem('tokenExpiration');
  
  if (!token || !expiration) {
    return false;
  }
  
  // 检查token是否过期
  const now = new Date().getTime();
  if (now >= parseInt(expiration)) {
    return false;
  }
  
  return true;
};

/**
 * 获取当前登录用户ID
 * @returns {string|null} - 用户ID或null（如果未登录）
 */
export const getCurrentUserId = () => {
  return isAuthenticated() ? localStorage.getItem('userId') : null;
};

/**
 * 获取当前登录用户名
 * @returns {string|null} - 用户名或null（如果未登录）
 */
export const getCurrentUsername = () => {
  return isAuthenticated() ? localStorage.getItem('username') : null;
};

/**
 * 保存认证信息到本地存储
 * @param {Object} authData - 认证数据
 */
export const saveAuth = (authData) => {
  if (authData) {
    localStorage.setItem('token', authData.token);
    localStorage.setItem('refreshToken', authData.refreshToken);
    if (authData.userId) localStorage.setItem('userId', authData.userId);
    if (authData.username) localStorage.setItem('username', authData.username);
    localStorage.setItem('expiresIn', authData.expiresIn);
    
    // 设置token过期时间
    const expirationTime = new Date().getTime() + authData.expiresIn * 1000;
    localStorage.setItem('tokenExpiration', expirationTime);
  }
};

/**
 * 清除认证信息
 */
export const clearAuth = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('userId');
  localStorage.removeItem('username');
  localStorage.removeItem('expiresIn');
  localStorage.removeItem('tokenExpiration');
};

/**
 * 认证服务API模块
 */
const authAPI = {
  // 在运行时导入以避免循环引用
  get login() {
    return require('./login').default.login;
  },
  
  get refreshToken() {
    return require('./refresh').default.refreshToken;
  },
  
  get logout() {
    return require('./logout').default.logout;
  },
  
  get changePassword() {
    return require('./password').default.changePassword;
  },
  
  // 认证工具函数
  isAuthenticated,
  getCurrentUserId,
  getCurrentUsername,
  
  // 认证本地存储处理
  saveAuth,
  clearAuth
};

export default authAPI; 