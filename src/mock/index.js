import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

// 导入各模块的mock数据
import userMocks from './user';
import authMocks from './auth';

/**
 * 创建mock实例
 * @param {Object} instance - axios实例
 * @param {Object} options - 配置选项
 * @returns {Object} mock实例
 */
const setupMock = (instance = axios, options = {}) => {
  // 创建mock适配器实例
  const mock = new MockAdapter(instance, {
    delayResponse: 500, // 延迟响应时间
    onNoMatch: 'passthrough', // 不匹配的请求将被传递给真实API
    ...options
  });

  // 初始化各模块mock
  userMocks(mock);
  authMocks(mock);

  return mock;
};

// 导出为默认导出
export default setupMock; 