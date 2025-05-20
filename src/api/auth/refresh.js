import API from '../config';
import { saveAuth } from './index';

/**
 * 刷新令牌
 * @param {Object} data - 刷新令牌数据
 * @param {string} data.refreshToken - 刷新令牌
 * @returns {Promise} - 包含新令牌的Promise
 */
export const refreshToken = (data) => {
  return API.post('/auth/refresh', data).then(res => {
    // 刷新成功后更新令牌
    if (res.code === 200 && res.data) {
      saveAuth({
        ...res.data,
        userId: localStorage.getItem('userId'),
        username: localStorage.getItem('username')
      });
    }
    return res;
  });
};

export default {
  refreshToken
}; 