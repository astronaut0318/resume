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
} 