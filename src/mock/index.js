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
export default setupMock; import { createOrderMock, getOrderListMock, getOrderDetailMock, cancelOrderMock, payNotifyMock, payOrderDirectMock } from './order'
import templatesApiList from './templates'
import fileApiList from './file'
import notificationApiList from './notification'

// 其他已有的mock导入...

// 创建完整的mock接口列表
const mockApis = [
  // 订单相关接口
  {
    url: '/api/orders',
    method: 'post',
    response: createOrderMock
  },
  {
    url: /\/api\/orders(\?.+)?$/,
    method: 'get',
    response: getOrderListMock
  },
  {
    url: /\/api\/orders\/[A-Za-z0-9]+$/,
    method: 'get',
    response: getOrderDetailMock
  },
  {
    url: /\/api\/orders\/[A-Za-z0-9]+\/cancel$/,
    method: 'put',
    response: cancelOrderMock
  },
  // 直接支付订单接口
  {
    url: /\/api\/orders\/[A-Za-z0-9]+\/pay$/,
    method: 'post',
    response: payOrderDirectMock
  },
  // 支付回调接口
  {
    url: /\/api\/orders\/notify\/\d+$/,
    method: 'post',
    response: payNotifyMock
  },
  // 添加模板相关接口
  ...templatesApiList,
  // 添加文件服务相关接口
  ...fileApiList,
  // 添加通知服务相关接口
  ...notificationApiList
]

export default mockApis 