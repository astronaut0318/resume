import userAPI from './user';
import userVipAPI from './vip';
import userDetailsAPI from './details';

/**
 * 用户服务API模块
 */
export default {
  ...userAPI,
  ...userDetailsAPI,
  ...userVipAPI
}; 