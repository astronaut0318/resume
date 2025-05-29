// 模拟简历数据
let resumeData = [
  {
    id: 1,
    userId: 1,
    title: "软件工程师简历",
    templateId: 1,
    content: {
      basicInfo: {
        name: "张三",
        phone: "13800138000",
        email: "zhangsan@example.com",
        address: "北京市海淀区",
        birthday: "1995-01-01",
        avatar: "https://randomuser.me/api/portraits/men/1.jpg"
      },
      education: [
        {
          school: "北京大学",
          major: "计算机科学",
          degree: "本科",
          startDate: "2016-09-01",
          endDate: "2020-07-01",
          description: "主修课程：数据结构、算法分析、操作系统、计算机网络、数据库系统"
        }
      ],
      workExperience: [
        {
          company: "某科技公司",
          position: "Java开发工程师",
          startDate: "2020-07-01",
          endDate: "2023-01-01",
          description: "负责后端系统开发与维护，参与电商平台核心模块的设计与实现，优化系统性能，提升用户体验。"
        }
      ],
      skills: [
        "Java", "Spring Boot", "MySQL", "Redis", "Git"
      ],
      projects: [
        {
          name: "电商平台",
          role: "后端开发",
          startDate: "2021-03-01",
          endDate: "2022-06-01",
          description: "负责订单系统和支付模块的开发，实现了高并发订单处理，提升了系统吞吐量30%。"
        }
      ],
      certificates: [
        {
          name: "Oracle认证Java工程师",
          issueDate: "2021-05-01",
          issuer: "Oracle"
        }
      ]
    },
    status: 1,
    isDefault: 1,
    createTime: "2023-01-01 12:00:00",
    updateTime: "2023-01-02 12:00:00"
  }
];

// 生成唯一ID
const generateId = () => {
  return Math.max(0, ...resumeData.map(item => item.id)) + 1;
};

// 创建简历
export const createResume = (data) => {
  const userId = localStorage.getItem('userId') || 1;
  const newResume = {
    id: generateId(),
    userId: parseInt(userId),
    ...data,
    status: 1,
    isDefault: resumeData.length === 0 ? 1 : 0,
    createTime: new Date().toISOString().replace('T', ' ').substr(0, 19),
    updateTime: new Date().toISOString().replace('T', ' ').substr(0, 19)
  };
  
  resumeData.push(newResume);
  
  return {
    code: 200,
    message: "创建成功",
    data: {
      resumeId: newResume.id
    }
  };
};

// 获取简历列表
export const getResumeList = (params = {}) => {
  const userId = localStorage.getItem('userId') || 1;
  const { page = 1, size = 10 } = params;
  
  // 筛选当前用户的简历
  const userResumes = resumeData.filter(resume => resume.userId === parseInt(userId));
  
  // 分页
  const start = (page - 1) * size;
  const end = start + size;
  const paginatedResumes = userResumes.slice(start, end);
  
  // 简化返回数据
  const list = paginatedResumes.map(resume => ({
    id: resume.id,
    title: resume.title,
    status: resume.status,
    isDefault: resume.isDefault,
    createTime: resume.createTime,
    updateTime: resume.updateTime
  }));
  
  return {
    code: 200,
    message: "操作成功",
    data: {
      total: userResumes.length,
      list
    }
  };
};

// 获取简历详情
export const getResumeDetail = (resumeId) => {
  const resume = resumeData.find(item => item.id === parseInt(resumeId));
  
  if (!resume) {
    return {
      code: 404,
      message: "简历不存在",
      data: null
    };
  }
  
  return {
    code: 200,
    message: "操作成功",
    data: resume
  };
};

// 更新简历
export const updateResume = (resumeId, data) => {
  const index = resumeData.findIndex(item => item.id === parseInt(resumeId));
  
  if (index === -1) {
    return {
      code: 404,
      message: "简历不存在",
      data: null
    };
  }
  
  // 更新数据
  resumeData[index] = {
    ...resumeData[index],
    ...data,
    updateTime: new Date().toISOString().replace('T', ' ').substr(0, 19)
  };
  
  return {
    code: 200,
    message: "更新成功",
    data: null
  };
};

// 删除简历
export const deleteResume = (resumeId) => {
  const index = resumeData.findIndex(item => item.id === parseInt(resumeId));
  
  if (index === -1) {
    return {
      code: 404,
      message: "简历不存在",
      data: null
    };
  }
  
  // 删除数据
  resumeData.splice(index, 1);
  
  return {
    code: 200,
    message: "删除成功",
    data: null
  };
};

// 设置默认简历
export const setDefaultResume = (resumeId) => {
  const userId = localStorage.getItem('userId') || 1;
  
  // 先将所有该用户的简历设为非默认
  resumeData.forEach(resume => {
    if (resume.userId === parseInt(userId)) {
      resume.isDefault = 0;
    }
  });
  
  // 设置指定简历为默认
  const index = resumeData.findIndex(item => item.id === parseInt(resumeId));
  
  if (index === -1) {
    return {
      code: 404,
      message: "简历不存在",
      data: null
    };
  }
  
  resumeData[index].isDefault = 1;
  
  return {
    code: 200,
    message: "设置成功",
    data: null
  };
}; 