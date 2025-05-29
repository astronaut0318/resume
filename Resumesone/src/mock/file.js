import Mock from 'mockjs'

// 上传文件
export const uploadFileMock = (req, res) => {
  return {
    code: 200,
    message: '上传成功',
    data: {
      fileId: Mock.Random.id(),
      fileName: Mock.Random.word(5, 10) + '.jpg',
      filePath: Mock.Random.image('200x200'),
      fileSize: Mock.Random.integer(1000, 1000000),
      fileType: 'image/jpeg'
    }
  }
}

// 下载文件
export const downloadFileMock = (req, res) => {
  // 实际文件下载在前端处理，这里仅返回成功状态
  return {
    code: 200,
    message: '下载成功',
    data: null
  }
}

// 导出简历为PDF
export const exportResumePdfMock = (req, res) => {
  return {
    code: 200,
    message: '导出成功',
    data: {
      downloadUrl: Mock.Random.url()
    }
  }
}

// 导出简历为Word
export const exportResumeWordMock = (req, res) => {
  return {
    code: 200,
    message: '导出成功',
    data: {
      downloadUrl: Mock.Random.url()
    }
  }
}

// 获取文件列表
export const getFileListMock = (req, res) => {
  // 解析查询参数
  const query = req.query || {}
  const page = parseInt(query.page) || 1
  const size = parseInt(query.size) || 10
  const type = query.type

  // 生成随机文件列表
  const list = []
  for (let i = 0; i < size; i++) {
    const fileTypes = ['image/jpeg', 'image/png', 'application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document']
    const fileType = type === 'avatar' ? 'image/jpeg' 
                  : type === 'resume' ? 'application/pdf'
                  : Mock.Random.pick(fileTypes)
    
    const fileExtension = fileType === 'image/jpeg' ? '.jpg'
                       : fileType === 'image/png' ? '.png'
                       : fileType === 'application/pdf' ? '.pdf'
                       : '.docx'
    
    list.push({
      id: Mock.Random.id(),
      originalName: Mock.Random.word(3, 8) + fileExtension,
      fileName: Mock.Random.guid() + fileExtension,
      filePath: fileType.startsWith('image/') 
                ? Mock.Random.image('200x200') 
                : 'http://example.com/files/' + Mock.Random.guid() + fileExtension,
      fileSize: Mock.Random.integer(10000, 5000000),
      fileType: fileType,
      createTime: Mock.Random.datetime()
    })
  }

  return {
    code: 200,
    message: '获取成功',
    data: {
      total: 35, // 总文件数
      list
    }
  }
}

// 删除文件
export const deleteFileMock = (req, res) => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  }
}

// 导出文件服务API列表
const fileApiList = [
  {
    url: '/api/files/upload',
    method: 'post',
    response: uploadFileMock
  },
  {
    url: /\/api\/files\/\d+\/download$/,
    method: 'get',
    response: downloadFileMock
  },
  {
    url: /\/api\/files\/resume\/\d+\/pdf$/,
    method: 'get',
    response: exportResumePdfMock
  },
  {
    url: /\/api\/files\/resume\/\d+\/word$/,
    method: 'get',
    response: exportResumeWordMock
  },
  {
    url: /\/api\/files(\?.+)?$/,
    method: 'get',
    response: getFileListMock
  },
  {
    url: /\/api\/files\/\d+$/,
    method: 'delete',
    response: deleteFileMock
  }
]

export default fileApiList 