import API from '../config';

/**
 * 获取用户详细信息
 * @param {number} userId - 用户ID
 * @returns {Promise} - 包含用户详细信息的Promise
 */
export const getUserDetails = (userId) => {
  return API.get(`/users/${userId}/details`);
};

/**
 * 更新用户详细信息
 * @param {number} userId - 用户ID
 * @param {Object} data - 用户详细信息
 * @param {string} [data.realName] - 真实姓名
 * @param {number} [data.gender] - 性别(0:女, 1:男)
 * @param {string} [data.birthday] - 生日，格式为YYYY-MM-DD
 * @param {string} [data.education] - 学历
 * @param {number} [data.workYears] - 工作年限
 * @param {string} [data.address] - 地址
 * @param {string} [data.profile] - 个人简介
 * @returns {Promise}
 */
export const updateUserDetails = (userId, data) => {
  return API.put(`/users/${userId}/details`, data);
};

export default {
  getUserDetails,
  updateUserDetails
}; 