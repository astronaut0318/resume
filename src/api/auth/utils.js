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

export default {
  isAuthenticated,
  getCurrentUserId,
  getCurrentUsername
}; 