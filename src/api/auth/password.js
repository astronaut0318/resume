import API from '../config';

/**
 * 修改密码
 * @param {Object} data - 密码信息
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 * @returns {Promise} - 修改密码请求的Promise
 */
export const changePassword = (data) => {
  return API.put('/auth/password', data);
};

export default {
  changePassword
}; 