import API from '../config';

/**
 * 查询用户VIP会员状态
 * @param {number} userId - 用户ID
 * @returns {Promise} - 包含VIP会员状态信息的Promise
 */
export const getVipStatus = (userId) => {
  return API.get(`/users/${userId}/vip`);
};

export default {
  getVipStatus
}; 