/**
 * 用户模块mock数据
 * @param {Object} mock - mock实例
 */
const userMocks = (mock) => {
  // 默认注册信息
  const defaultRegisterData = {
    username: "zhangsan",
    password: "Pssword123",
    email: "zhangsan@example.com",
    phone: "13800138000"
  };
  
  // 模拟用户注册
  mock.onPost('/api/users/register').reply((config) => {
    const requestData = JSON.parse(config.data || '{}');
    const userData = {
      ...defaultRegisterData,
      ...requestData
    };
    
    console.log('Mock API - 用户注册', userData);
    
    // 模拟注册成功响应
    return [
      200,
      {
        code: 200,
        message: "注册成功",
        data: {
          userId: 1,
          username: userData.username
        }
      }
    ];
  });
  
  // 模拟获取用户信息
  mock.onGet(new RegExp('/api/users/\\d+')).reply((config) => {
    const userId = config.url.split('/').pop();
    
    console.log('Mock API - 获取用户信息', { userId });
    
    return [
      200,
      {
        code: 200,
        message: "操作成功",
        data: {
          id: parseInt(userId),
          username: "zhangsan",
          email: "zhangsan@example.com",
          phone: "13800138000",
          avatar: "http://example.com/avatar.jpg",
          role: 0,
          status: 1,
          createTime: "2023-01-01 12:00:00"
        }
      }
    ];
  });
  
  // 模拟获取用户详细信息
  mock.onGet(new RegExp('/api/users/\\d+/details')).reply((config) => {
    const userId = config.url.split('/')[3];
    
    console.log('Mock API - 获取用户详细信息', { userId });
    
    return [
      200,
      {
        code: 200,
        message: "操作成功",
        data: {
          id: 1,
          userId: parseInt(userId),
          realName: "张三",
          gender: 1,
          birthday: "1990-01-01",
          education: "本科",
          workYears: 5,
          address: "北京市朝阳区",
          profile: "个人简介...",
          createTime: "2023-01-01 12:00:00"
        }
      }
    ];
  });
  
  // 模拟VIP会员信息
  mock.onGet(new RegExp('/api/users/\\d+/vip')).reply((config) => {
    const userId = config.url.split('/')[3];
    
    console.log('Mock API - 获取VIP会员状态', { userId });
    
    return [
      200,
      {
        code: 200,
        message: "操作成功",
        data: {
          isVip: true,
          level: 2,
          startTime: "2023-01-01 00:00:00",
          endTime: "2023-12-31 23:59:59",
          remainingDays: 300
        }
      }
    ];
  });
};

export default userMocks; 