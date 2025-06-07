package com.ptu.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ptu.file.dto.OnlyOfficeSaveDTO;
import com.ptu.file.entity.FileEntity;
import com.ptu.file.mapper.FileMapper;
import com.ptu.file.service.FileService;
import com.ptu.file.vo.FileUploadVO;
import com.ptu.file.vo.FileVO;
import com.ptu.common.util.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 初始化所有需要的MinIO存储桶
     */
    @javax.annotation.PostConstruct
    public void initBuckets() {
        log.info("开始初始化MinIO存储桶");
        try {
            // 只创建两个必要的桶
            String[] buckets = new String[]{
                "resume-thumbnails", // 图片文件桶
                "resume-templates"   // 简历文件桶
            };
            
            for (String bucket : buckets) {
                try {
                    minioUtils.createBucket(bucket);
                    log.info("存储桶已就绪: {}", bucket);
                } catch (Exception e) {
                    log.error("创建存储桶失败: {}", bucket, e);
                }
            }
            log.info("MinIO存储桶初始化完成");
        } catch (Exception e) {
            log.error("MinIO存储桶初始化失败", e);
        }
    }

    // ... 其他方法 ...

    @Override
    public byte[] exportResumeToWord(Long resumeId) {
        // 1. 查询简历Word文件的元数据（假设fileType=resume，bizId=resumeId）
        FileEntity file = fileMapper.selectOne(
            new QueryWrapper<FileEntity>()
                .eq("file_type", "template")
                .eq("biz_id", resumeId)
        );
        if (file == null) {
            throw new RuntimeException("未找到对应简历Word文件");
        }

        // 2. 从MinIO获取文件流
        String bucket = "resume-templates"; // 或你实际的bucket名
        String objectName = file.getFilePath(); // 通常是filePath或fileName
        try (InputStream in = minioUtils.getObject(bucket, objectName);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public FileUploadVO uploadFile(MultipartFile file, String fileType, Long bizId, Long userId) {
        // 调用重载方法，传递null作为bucket参数
        return uploadFile(file, fileType, bizId, userId, null);
    }

    @Override
    public FileUploadVO uploadFile(MultipartFile file, String fileType, Long bizId, Long userId, String specifiedBucket) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件不能为空");
            }
            
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            
            // 确定文件类型和存储桶
            String bucket;
            String actualFileType;
            
            // 检查文件是否为图片
            boolean isImageFile = isImageFile(file);
            if (isImageFile) {
                // 图片文件统一使用thumbnail类型，存储在resume-thumbnails桶
                bucket = "resume-thumbnails";
                actualFileType = "thumbnail";
                log.info("检测到图片文件: {}, 将存储到resume-thumbnails桶", originalFilename);
            } else {
                // 非图片文件统一视为简历文件，存储在resume-templates桶
                bucket = "resume-templates";
                actualFileType = "template";
                log.info("检测到非图片文件: {}, 将存储到resume-templates桶", originalFilename);
            }
            
            // 使用原始文件名作为存储名称
            String objectName = originalFilename;
            
            // 检查同名文件是否已存在，如果存在则添加时间戳前缀避免覆盖
            boolean fileExists = false;
            try {
                fileExists = minioUtils.objectExists(bucket, objectName);
            } catch (Exception e) {
                log.warn("检查文件是否存在时出错，将继续上传: {}", e.getMessage());
            }
            
            if (fileExists) {
                log.warn("文件[{}]已存在，添加时间戳前缀避免覆盖", objectName);
                String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                objectName = timestamp + "_" + originalFilename;
            }
            
            log.info("开始上传文件: 原始名称={}, 存储名称={}, 类型={}, 存储桶={}, 用户ID={}, 业务ID={}",
                    originalFilename, objectName, actualFileType, bucket, userId, bizId);
            
            // 上传到MinIO
            minioUtils.uploadFile(bucket, objectName, file, file.getContentType());
            log.info("文件已成功上传到MinIO: bucket={}, objectName={}", bucket, objectName);
            
            // 创建文件记录
            FileEntity entity = new FileEntity();
            entity.setFileName(objectName);      // 存储的文件名（可能加了时间戳）
            entity.setOriginalName(originalFilename); // 原始文件名
            entity.setFilePath(objectName);
            entity.setFileSize(file.getSize());
            entity.setFileType(actualFileType);
            entity.setBizId(bizId != null ? bizId : 0L);
            entity.setUserId(userId);
            entity.setCreateTime(new Date());
            
            // 获取文件扩展名
            String fileExt = "";
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                fileExt = originalFilename.substring(lastDotIndex + 1).toLowerCase();
                entity.setFileExt(fileExt);
            }
            
            // 保存到数据库
            try {
                log.info("准备将文件记录保存到数据库: {}", entity);
                int result = fileMapper.insert(entity);
                if (result > 0) {
                    log.info("文件记录已保存到数据库, id: {}", entity.getId());
                } else {
                    log.error("文件记录保存失败，影响行数为0");
                    throw new RuntimeException("文件记录保存失败");
                }
            } catch (Exception e) {
                log.error("保存文件记录到数据库失败", e);
                // 尝试从MinIO删除已上传的文件
                try {
                    minioUtils.removeObject(bucket, objectName);
                    log.info("已从MinIO删除文件（因数据库保存失败）: {}", objectName);
                } catch (Exception ex) {
                    log.error("删除MinIO中的文件失败", ex);
                }
                throw new RuntimeException("保存文件记录失败: " + e.getMessage());
            }
            
            // 返回结果
            FileUploadVO vo = new FileUploadVO();
            vo.setFileId(entity.getId());
            vo.setFileName(entity.getOriginalName()); // 返回原始文件名，方便前端显示
            vo.setFilePath(entity.getFilePath());
            vo.setFileSize(entity.getFileSize());
            vo.setFileType(actualFileType);
            
            // 生成文件URL并设置到返回对象中
            String fileUrl = getFileUrl(bucket, objectName);
            vo.setFileUrl(fileUrl);
            
            log.info("文件上传完成: fileId={}, fileUrl={}", vo.getFileId(), vo.getFileUrl());
            
            return vo;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] downloadFile(String fileId) {
        try {
            // 1. 查询文件信息
            FileEntity file = fileMapper.selectById(fileId);
            if (file == null) {
                throw new RuntimeException("文件不存在");
            }
            
            // 2. 确定bucket - 使用简化的逻辑
            String bucket = getBucketByFileType(file.getFileType());
            
            // 3. 从MinIO下载文件
            InputStream inputStream = minioUtils.getObject(bucket, file.getFilePath());
            
            // 4. 转为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("文件下载失败: {}", e.getMessage());
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            log.error("文件删除失败: fileId为null或空");
            return false;
        }
        
        log.info("开始删除文件, fileId: {}", fileId);
        
        try {
            // 使用原生SQL查询，完全避免ORM框架类型转换问题
            FileEntity file = fileMapper.selectByRawId(fileId);
            
            if (file == null) {
                log.warn("文件不存在, fileId: {}", fileId);
                // 数据库记录不存在，但实际上可以认为删除目标已达成
                return true;
            }
            
            log.info("待删除文件信息: id={}, fileName={}, filePath={}, fileType={}, userId={}", 
                     file.getId(), file.getFileName(), file.getFilePath(), file.getFileType(), file.getUserId());
            
            // 2. 确定bucket - 使用简化的逻辑
            String bucket = getBucketByFileType(file.getFileType());
            log.info("文件存储在桶: {}", bucket);
            
            // 3. 先从MinIO删除文件
            boolean minioDeleteSuccess = false;
            try {
                log.info("准备从MinIO删除文件, bucket: {}, objectName: {}", bucket, file.getFilePath());
                
                // 检查文件是否存在于MinIO
                boolean fileExists = minioUtils.objectExists(bucket, file.getFilePath());
                if (!fileExists) {
                    log.warn("MinIO中文件不存在，无需删除: bucket={}, objectName={}", bucket, file.getFilePath());
                    minioDeleteSuccess = true; // 文件不存在，视为删除成功
                } else {
                    // 文件存在，执行删除
                    minioUtils.removeObject(bucket, file.getFilePath());
                    log.info("MinIO文件删除成功");
                    minioDeleteSuccess = true;
                }
            } catch (Exception e) {
                log.error("MinIO文件删除失败: {}", e.getMessage(), e);
                // MinIO删除失败，返回false，不删除数据库记录
                return false;
            }
            
            // 4. MinIO删除成功后，再删除数据库记录 - 使用原生SQL删除
            if (minioDeleteSuccess) {
                log.info("准备删除数据库记录, fileId: {}", fileId);
                // 使用原生SQL删除
                int result = fileMapper.deleteByRawId(fileId);
                boolean dbDeleteSuccess = result > 0;
                log.info("数据库记录删除结果: {}, 影响行数: {}", dbDeleteSuccess ? "成功" : "失败", result);
                
                return dbDeleteSuccess;
            } else {
                log.error("MinIO文件删除失败，中止数据库记录删除");
                return false;
            }
        } catch (Exception e) {
            log.error("文件删除过程中发生异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<FileVO> listFiles(String fileType, Long bizId, Long userId, int page, int size) {
        log.info("查询文件列表: fileType={}, bizId={}, userId={}, page={}, size={}", fileType, bizId, userId, page, size);
        
        try {
            // 创建查询条件
            QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
            
            // 添加查询条件
            if (fileType != null && !fileType.isEmpty()) {
                queryWrapper.eq("file_type", fileType);
            }
            
            if (bizId != null) {
                queryWrapper.eq("biz_id", bizId);
            }
            
            if (userId != null) {
                queryWrapper.eq("user_id", userId);
            }
            
            // 按创建时间降序排序
            queryWrapper.orderByDesc("create_time");
            
            // 执行分页查询
            Page<FileEntity> pageParam = new Page<>(page, size);
            Page<FileEntity> pageResult = fileMapper.selectPage(pageParam, queryWrapper);
            
            // 添加详细日志，显示查询结果统计
            log.info("文件列表查询结果: 总记录数={}, 当前页数={}, 每页大小={}, 当前页记录数={}",
                    pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize(),
                    pageResult.getRecords().size());
            
            // 如果结果为空，检查数据库中是否有任何记录
            if (pageResult.getRecords().isEmpty()) {
                log.warn("未找到任何匹配的文件记录，检查是否存在任何文件记录...");
                Long totalCount = fileMapper.selectCount(null);
                log.info("数据库中总文件记录数: {}", totalCount);
                
                if (totalCount > 0) {
                    // 数据库有记录但当前查询为空，检查是否是筛选条件问题
                    log.warn("数据库中存在文件记录，但当前查询条件下未找到匹配项，可能是查询条件限制导致");
                    // 尝试查询当前用户的记录总数
                    if (userId != null) {
                        QueryWrapper<FileEntity> userQuery = new QueryWrapper<>();
                        userQuery.eq("user_id", userId);
                        Long userFileCount = fileMapper.selectCount(userQuery);
                        log.info("用户ID={}的文件记录总数: {}", userId, userFileCount);
                    }
                } else {
                    log.warn("数据库中不存在任何文件记录，可能是尚未上传文件或存在数据同步问题");
                }
            } else {
                // 记录前几条记录的详细信息，帮助调试
                int logLimit = Math.min(pageResult.getRecords().size(), 3);
                for (int i = 0; i < logLimit; i++) {
                    FileEntity entity = pageResult.getRecords().get(i);
                    log.info("文件记录[{}]: id={}, 文件名={}, 类型={}, 用户ID={}, 大小={}",
                            i, entity.getId(), entity.getFileName(), entity.getFileType(),
                            entity.getUserId(), entity.getFileSize());
                }
            }
            
            // 转换为VO对象
            List<FileVO> resultList = new ArrayList<>();
            for (FileEntity entity : pageResult.getRecords()) {
                FileVO vo = new FileVO();
                vo.setId(entity.getId());
                vo.setOriginalName(entity.getOriginalName());
                vo.setFileName(entity.getFileName());
                vo.setFilePath(entity.getFilePath());
                vo.setFileSize(entity.getFileSize());
                vo.setFileType(entity.getFileType());
                vo.setCreateTime(entity.getCreateTime());
                
                // 添加文件URL
                String bucket = getBucketByFileType(entity.getFileType());
                String fileUrl = getFileUrl(bucket, entity.getFilePath());
                vo.setFileUrl(fileUrl);
                
                resultList.add(vo);
            }
            
            // 记录返回结果信息
            log.info("文件列表查询返回: 结果数量={}", resultList.size());
            
            return resultList;
        } catch (Exception e) {
            log.error("查询文件列表失败", e);
            // 返回空列表，避免抛出异常导致前端400错误
            return new ArrayList<>();
        }
    }

    @Override
    public boolean handleOnlyOfficeSave(OnlyOfficeSaveDTO saveDTO) {
        // 简单实现，实际可能需要更复杂的处理
        try {
            log.info("OnlyOffice保存回调处理: {}", saveDTO);
            return true;
        } catch (Exception e) {
            log.error("OnlyOffice保存处理失败", e);
            return false;
        }
    }
    
    /**
     * 根据文件类型获取对应的MinIO bucket
     * @param fileType 文件类型
     * @return bucket名称
     */
    private String getBucketByFileType(String fileType) {
        if ("thumbnail".equals(fileType)) {
            return "resume-thumbnails";
        } else {
            // 默认都作为template类型处理
                return "resume-templates";
        }
    }

    /**
     * 获取文件的完整访问URL
     * @param bucket 存储桶
     * @param objectName 对象名称
     * @return 完整的文件访问URL
     */
    private String getFileUrl(String bucket, String objectName) {
        try {
            // 生成一个有效期为7天的URL
            return minioUtils.getFileUrl(bucket, objectName, 60 * 24 * 7);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return null;
        }
    }

    /**
     * 获取文件URL
     * @param fileId 文件ID
     * @return 文件URL
     */
    @Override
    public String getFileUrlById(Long fileId) {
        try {
            FileEntity file = fileMapper.selectById(fileId);
            if (file == null) {
                return null;
            }
            String bucket = getBucketByFileType(file.getFileType());
            return getFileUrl(bucket, file.getFilePath());
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return null;
        }
    }

    /**
     * 检查文件是否为图片
     * @param file 文件
     * @return 是否为图片
     */
    private boolean isImageFile(MultipartFile file) {
        if (file == null) {
            return false;
        }
        
        // 1. 检查Content-Type
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        
        // 2. 检查文件扩展名
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }
        
        String extension = "";
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = filename.substring(lastDotIndex + 1).toLowerCase();
        }
        
        // 常见图片扩展名
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif") || 
               extension.equals("bmp") || extension.equals("webp") ||
               extension.equals("svg") || extension.equals("tiff");
    }

    /**
     * 生成唯一文件名，避免文件覆盖
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFilename) {
        // 提取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }
        
        // 使用UUID和时间戳生成唯一文件名
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
        
        return timestamp + "_" + uuid + extension;
    }

    /**
     * 统计用户文件总数
     * @param userId 用户ID
     * @return 文件总数
     */
    @Override
    public long countUserFiles(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        try {
            QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            return fileMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("统计用户文件数失败: userId={}", userId, e);
            return 0;
        }
    }
    
    /**
     * 获取用户所有文件并手动构建URL
     * 用于文件列表查询结果为空但数据库有记录的情况
     * @param userId 用户ID
     * @return 文件列表（包含手动构建的URL）
     */
    @Override
    public List<FileVO> getAllUserFilesWithUrl(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        try {
            log.info("开始手动获取用户文件列表: userId={}", userId);
            
            // 1. 直接查询数据库获取所有文件记录
            QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.orderByDesc("create_time");
            List<FileEntity> entities = fileMapper.selectList(queryWrapper);
            
            log.info("从数据库查询到文件记录: count={}", entities.size());
            
            // 2. 转换为VO并构建URL
            List<FileVO> result = new ArrayList<>();
            for (FileEntity entity : entities) {
                FileVO vo = new FileVO();
                vo.setId(entity.getId());
                vo.setOriginalName(entity.getOriginalName());
                vo.setFileName(entity.getFileName());
                vo.setFilePath(entity.getFilePath());
                vo.setFileSize(entity.getFileSize());
                vo.setFileType(entity.getFileType());
                vo.setCreateTime(entity.getCreateTime());
                
                // 手动构建URL - 先尝试使用正常方法
                String bucket = getBucketByFileType(entity.getFileType());
                String fileUrl = null;
                
                try {
                    fileUrl = getFileUrl(bucket, entity.getFilePath());
                } catch (Exception e) {
                    log.warn("使用常规方法获取文件URL失败: fileId={}, bucket={}, filePath={}", 
                             entity.getId(), bucket, entity.getFilePath(), e);
                }
                
                // 如果常规方法失败，尝试手动构建URL
                if (fileUrl == null || fileUrl.isEmpty()) {
                    try {
                        // 根据MinIO配置手动构建URL
                        String baseUrl = minioUtils.getBaseUrl();
                        fileUrl = baseUrl + "/" + bucket + "/" + entity.getFilePath();
                        log.info("手动构建文件URL: {}", fileUrl);
                    } catch (Exception e) {
                        log.error("手动构建文件URL失败", e);
                    }
                }
                
                vo.setFileUrl(fileUrl);
                result.add(vo);
            }
            
            log.info("手动构建文件列表完成: count={}", result.size());
            return result;
        } catch (Exception e) {
            log.error("手动获取用户文件列表失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    // ... 其他方法 ...
}
