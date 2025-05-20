import API from '../config';

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.email - 邮箱
 * @param {string} data.phone - 手机号
 * @returns {Promise} - 包含用户ID和用户名的Promise
 */
export const register = (data) => {
  return API.post('/users/register', data);
};

/**
 * 获取用户信息
 * @param {number} userId - 用户ID
 * @returns {Promise} - 包含用户信息的Promise
 */
export const getUserInfo = (userId) => {
  return API.get(`/users/${userId}`);
};

/**
 * 更新用户信息
 * @param {number} userId - 用户ID
 * @param {Object} data - 用户信息
 * @param {string} [data.email] - 邮箱
 * @param {string} [data.phone] - 手机号
 * @param {string} [data.avatar] - 头像URL
 * @returns {Promise}
 */
export const updateUserInfo = (userId, data) => {
  return API.put(`/users/${userId}`, data);
};

export default {
  register,
  getUserInfo,
  updateUserInfo
}; 