import usersAPI from './users';
import authAPI from './auth';

/**
 * 智能简历工场API模块集合
 */
export default {
  // 用户服务API
  users: usersAPI,
  
  // 认证服务API
  auth: authAPI,
  
  // 后续可添加其他服务API
  // resumes: resumesAPI,
  // templates: templatesAPI,
}; 