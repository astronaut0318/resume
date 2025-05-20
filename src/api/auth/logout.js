import API from '../config';
import { clearAuth } from './index';

/**
 * 退出登录
 * @returns {Promise} - 退出登录请求的Promise
 */
export const logout = () => {
  return API.post('/auth/logout').then(res => {
    // 无论请求是否成功，都清除本地认证信息
    clearAuth();
    return res;
  });
};

export default {
  logout
}; 