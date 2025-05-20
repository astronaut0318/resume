/**
 * 认证模块mock数据
 * @param {Object} mock - mock实例
 */
const authMocks = (mock) => {
  // 模拟用户登录
  mock.onPost('/api/auth/login').reply((config) => {
    const requestData = JSON.parse(config.data || '{}');
    const { username, password } = requestData;
    
    console.log('Mock API - 用户登录', { username, password });
    
    // 模拟登录成功
    if (username && password) {
      return [
        200,
        {
          code: 200,
          message: "登录成功",
          data: {
            userId: 1,
            username: username,
            token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            refreshToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            expiresIn: 7200
          }
        }
      ];
    }
    
    // 登录失败
    return [
      200,
      {
        code: 400,
        message: "用户名或密码错误",
        data: null
      }
    ];
  });
  
  // 模拟刷新令牌
  mock.onPost('/api/auth/refresh').reply((config) => {
    const requestData = JSON.parse(config.data || '{}');
    const { refreshToken } = requestData;
    
    console.log('Mock API - 刷新令牌', { refreshToken });
    
    if (refreshToken) {
      return [
        200,
        {
          code: 200,
          message: "刷新成功",
          data: {
            token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..._new",
            refreshToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..._new",
            expiresIn: 7200
          }
        }
      ];
    }
    
    return [
      200,
      {
        code: 400,
        message: "无效的刷新令牌",
        data: null
      }
    ];
  });
  
  // 模拟退出登录
  mock.onPost('/api/auth/logout').reply(() => {
    console.log('Mock API - 退出登录');
    
    return [
      200,
      {
        code: 200,
        message: "退出成功",
        data: null
      }
    ];
  });
  
  // 模拟修改密码
  mock.onPut('/api/auth/password').reply((config) => {
    const requestData = JSON.parse(config.data || '{}');
    const { oldPassword, newPassword } = requestData;
    
    console.log('Mock API - 修改密码', { oldPassword, newPassword });
    
    if (oldPassword && newPassword) {
      return [
        200,
        {
          code: 200,
          message: "密码修改成功",
          data: null
        }
      ];
    }
    
    return [
      200,
      {
        code: 400,
        message: "密码格式不正确",
        data: null
      }
    ];
  });
};

export default authMocks; 