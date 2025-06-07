package com.ptu.file.service;

import com.ptu.file.dto.OnlyOfficeSaveDTO;
import com.ptu.file.vo.FileUploadVO;
import com.ptu.file.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 文件服务接口
 * <p>
 * 提供文件的上传、下载、删除、分页查询、OnlyOffice保存等核心业务方法。
 * </p>
 */
public interface FileService {

    /**
     * 上传文件（模板、简历、头像）
     *
     * @param file    文件对象
     * @param fileType 文件类型（template/resume/avatar）
     * @param bizId   业务ID（如模板ID、简历ID、用户ID，头像时为用户ID）
     * @param userId  上传用户ID
     * @return 文件上传响应VO
     */
    FileUploadVO uploadFile(MultipartFile file, String fileType, Long bizId, Long userId);
    
    /**
     * 上传文件（模板、简历、头像）- 支持指定存储桶
     *
     * @param file    文件对象
     * @param fileType 文件类型（template/resume/avatar）
     * @param bizId   业务ID（如模板ID、简历ID、用户ID，头像时为用户ID）
     * @param userId  上传用户ID
     * @param bucket  指定存储桶（如果为null则使用默认存储桶）
     * @return 文件上传响应VO
     */
    FileUploadVO uploadFile(MultipartFile file, String fileType, Long bizId, Long userId, String bucket);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件二进制流（由Controller处理输出）
     */
    byte[] downloadFile(Long fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId);

    /**
     * 分页查询文件列表
     *
     * @param fileType 文件类型（template/resume/avatar）
     * @param bizId    业务ID（可选）
     * @param userId   用户ID
     * @param page     页码
     * @param size     每页大小
     * @return 文件VO列表
     */
    List<FileVO> listFiles(String fileType, Long bizId, Long userId, int page, int size);
    
    /**
     * 统计用户文件总数
     * 
     * @param userId 用户ID
     * @return 文件总数
     */
    long countUserFiles(Long userId);
    
    /**
     * 获取用户所有文件并手动构建URL
     * 用于文件列表查询结果为空但数据库有记录的情况
     * 
     * @param userId 用户ID
     * @return 文件列表（包含手动构建的URL）
     */
    List<FileVO> getAllUserFilesWithUrl(Long userId);

    /**
     * OnlyOffice文档保存回调处理
     *
     * @param saveDTO OnlyOffice保存回调参数
     * @return 是否保存成功
     */
    boolean handleOnlyOfficeSave(OnlyOfficeSaveDTO saveDTO);

    /**
     * 导出简历为Word
     * @param resumeId 简历ID
     * @return Word文件二进制流
     */
    byte[] exportResumeToWord(Long resumeId);
    
    /**
     * 获取文件URL
     * @param fileId 文件ID
     * @return 文件URL
     */
    String getFileUrlById(Long fileId);
} 