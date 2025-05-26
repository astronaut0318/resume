package com.ptu.document.service;

import com.ptu.document.dto.DocumentEditorConfig;
import com.ptu.document.entity.DocumentMetadataEntity;
import com.ptu.document.vo.DocumentVersionVO;

import java.io.InputStream;
import java.util.List;

/**
 * 文档服务接口
 */
public interface DocumentService {

    /**
     * 导出简历为Word
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return Word文件流
     */
    InputStream exportResumeAsWord(Long resumeId, Long userId);

    /**
     * 导出简历为PDF
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return PDF文件流
     */
    InputStream exportResumeAsPdf(Long resumeId, Long userId);


    /**
     * 获取文档元数据
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 文档元数据
     */
    DocumentMetadataEntity getDocumentMetadata(String sourceType, Long sourceId);

    /**
     * 保存文档元数据
     *
     * @param metadata 文档元数据
     * @return 是否成功
     */
    boolean saveDocumentMetadata(DocumentMetadataEntity metadata);

    /**
     * 获取文档编辑器配置
     *
     * @param sourceType 来源类型：template/resume/file
     * @param sourceId 来源ID
     * @param mode 模式：view/edit/comment
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 编辑器配置
     */
    DocumentEditorConfig getEditorConfig(String sourceType, Long sourceId, String mode, Long userId, String userName);

    /**
     * 处理回调
     *
     * @param body 回调请求体
     * @return 处理结果
     */
    boolean handleCallback(String body);

    /**
     * 创建文档版本
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param userId 用户ID
     * @return 版本ID
     */
    Long createDocumentVersion(String sourceType, Long sourceId, Long userId);

    /**
     * 获取文档版本列表
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 版本列表
     */
    List<DocumentVersionVO> getVersionList(String sourceType, Long sourceId);

    /**
     * 获取指定版本
     *
     * @param versionId 版本ID
     * @return 版本信息
     */
    DocumentVersionVO getVersion(Long versionId);

    /**
     * 预览指定版本
     *
     * @param versionId 版本ID
     * @param userId 用户ID
     * @param userName 用户名
     * @return 预览配置
     */
    DocumentEditorConfig previewVersion(Long versionId, Long userId, String userName);

    /**
     * 下载文档
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 文件流
     */
    InputStream downloadDocument(String sourceType, Long sourceId);

    /**
     * 下载指定版本
     *
     * @param versionId 版本ID
     * @return 文件流
     */
    InputStream downloadVersion(Long versionId);
    
    /**
     * 检查权限
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param userId 用户ID
     * @param mode 操作模式
     * @return 是否有权限
     */
    boolean checkPermission(String sourceType, Long sourceId, Long userId, String mode);
} 