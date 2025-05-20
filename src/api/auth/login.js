import API from '../config';
import { saveAuth } from './index';

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} - 包含登录成功后的用户信息和令牌的Promise
 */
export const login = (data) => {
  return API.post('/auth/login', data).then(res => {
    // 登录成功后保存认证信息
    if (res.code === 200 && res.data) {
      saveAuth(res.data);
    }
    return res;
  });
};

export default {
  login
}; 