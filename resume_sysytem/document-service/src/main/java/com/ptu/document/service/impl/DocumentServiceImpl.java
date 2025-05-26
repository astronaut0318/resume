package com.ptu.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptu.document.common.constants.DocumentConstant;
import com.ptu.document.common.utils.JwtUtils;
import com.ptu.document.config.MinioConfig;
import com.ptu.document.config.OnlyOfficeConfig;
import com.ptu.document.dto.CallbackData;
import com.ptu.document.dto.DocumentEditorConfig;
import com.ptu.document.entity.DocumentMetadataEntity;
import com.ptu.document.entity.DocumentVersionEntity;
import com.ptu.document.mapper.DocumentMetadataMapper;
import com.ptu.document.mapper.DocumentVersionMapper;
import com.ptu.document.service.DocumentService;
import com.ptu.document.service.MinioService;
import com.ptu.document.vo.DocumentVersionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 文档服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentMetadataMapper metadataMapper;
    private final DocumentVersionMapper versionMapper;
    private final MinioService minioService;
    private final MinioConfig minioConfig;
    private final OnlyOfficeConfig onlyOfficeConfig;
    private final ObjectMapper objectMapper;
    private final com.ptu.document.feign.ResumeFeignClient resumeFeignClient;

    /**
     * 导出简历为Word
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return Word文件流
     */
    @Override
    public InputStream exportResumeAsWord(Long resumeId, Long userId) {
        // 1. 通过Feign获取简历详情
        com.ptu.document.dto.ResumeDetailDTO resume = resumeFeignClient.getResumeDetail(resumeId, userId);
        if (resume == null || resume.getUserId() == null || !resume.getUserId().equals(userId)) {
            log.warn("用户{}无权导出简历{}或简历不存在", userId, resumeId);
            return null;
        }
        try {
            // 2. 使用POI生成Word文档
            org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument();
            // 标题
            org.apache.poi.xwpf.usermodel.XWPFParagraph title = doc.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun runTitle = title.createRun();
            runTitle.setText(resume.getTitle() != null ? resume.getTitle() : "简历");
            runTitle.setBold(true);
            runTitle.setFontSize(18);
            // 内容（简单示例，实际可根据content结构优化美化）
            if (resume.getContent() != null) {
                for (Map.Entry<String, Object> entry : resume.getContent().entrySet()) {
                    org.apache.poi.xwpf.usermodel.XWPFParagraph p = doc.createParagraph();
                    org.apache.poi.xwpf.usermodel.XWPFRun run = p.createRun();
                    run.setText(entry.getKey() + ": " + String.valueOf(entry.getValue()));
                }
            }
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            doc.write(out);
            return new java.io.ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            log.error("导出Word异常", e);
            return null;
        }
    }

    /**
     * 导出简历为PDF
     * @param resumeId 简历ID
     * @param userId 用户ID
     * @return PDF文件流
     */
    @Override
    public InputStream exportResumeAsPdf(Long resumeId, Long userId) {
        // 1. 先生成Word文档流
        InputStream wordStream = exportResumeAsWord(resumeId, userId);
        if (wordStream == null) return null;
        try {
            // 2. 使用PDFBox将Word转为PDF（简单文本方式，复杂格式建议用OnlyOffice服务）
            org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument(wordStream);
            java.io.ByteArrayOutputStream pdfOut = new java.io.ByteArrayOutputStream();
            org.apache.pdfbox.pdmodel.PDDocument pdfDoc = new org.apache.pdfbox.pdmodel.PDDocument();
            for (org.apache.poi.xwpf.usermodel.XWPFParagraph para : doc.getParagraphs()) {
                org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
                pdfDoc.addPage(page);
                org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(pdfDoc, page);
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText(para.getText());
                contentStream.endText();
                contentStream.close();
            }
            pdfDoc.save(pdfOut);
            pdfDoc.close();
            return new java.io.ByteArrayInputStream(pdfOut.toByteArray());
        } catch (Exception e) {
            log.error("导出PDF异常", e);
            return null;
        }
    }

    @Override
    public DocumentMetadataEntity getDocumentMetadata(String sourceType, Long sourceId) {
        return metadataMapper.getBySourceTypeAndSourceId(sourceType, sourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDocumentMetadata(DocumentMetadataEntity metadata) {
        // 查询是否已存在
        DocumentMetadataEntity existingMetadata = metadataMapper.getBySourceTypeAndSourceId(
                metadata.getSourceType(), metadata.getSourceId());
        
        if (existingMetadata != null) {
            // 更新
            metadata.setId(existingMetadata.getId());
            return metadataMapper.updateById(metadata) > 0;
        } else {
            // 新增
            return metadataMapper.insert(metadata) > 0;
        }
    }

    @Override
    public DocumentEditorConfig getEditorConfig(String sourceType, Long sourceId, String mode, Long userId, String userName) {
        // 1. 检查参数
        if (!StringUtils.hasText(sourceType) || sourceId == null || userId == null) {
            log.error("获取文档编辑器配置参数错误: sourceType={}, sourceId={}, userId={}", sourceType, sourceId, userId);
            return null;
        }
        
        // 2. 获取文档元数据
        DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
        if (metadata == null) {
            log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
            return null;
        }
        
        // 3. 检查权限
        if (!checkPermission(sourceType, sourceId, userId, mode)) {
            log.error("用户无权限访问文档: sourceType={}, sourceId={}, userId={}, mode={}", 
                    sourceType, sourceId, userId, mode);
            return null;
        }
        
        // 4. 生成文档访问URL
        String fileUrl = minioService.generatePresignedUrl(
                minioConfig.getDocumentBucket(), 
                metadata.getStoragePath(),
                30,
                TimeUnit.MINUTES);

        if (!StringUtils.hasText(fileUrl)) {
            log.error("生成文档访问URL失败: sourceType={}, sourceId={}", sourceType, sourceId);
            return null;
        }

        // 5. 创建编辑器配置
        DocumentEditorConfig config = new DocumentEditorConfig();

        // 设置文档类型
        config.setDocumentType(getDocumentType(metadata.getFileName()));

        // 设置文档信息
        DocumentEditorConfig.Document document = new DocumentEditorConfig.Document(
                metadata.getFileName(),
                fileUrl,
                getFileExtension(metadata.getFileName())
        );
        config.setDocument(document);

        // 设置编辑器配置
        DocumentEditorConfig.EditorConfig editorConfig = new DocumentEditorConfig.EditorConfig(
                mode,
                userId.toString(),
                userName != null ? userName : "用户" + userId
        );
        config.setEditorConfig(editorConfig);

        // 如果是编辑模式，设置回调URL
        if (DocumentConstant.MODE_EDIT.equals(mode)) {
            config.setCallbackUrl(onlyOfficeConfig.getCallbackUrl());
            editorConfig.setCallbackUrl(onlyOfficeConfig.getCallbackUrl());
        }

        // 6. 生成token
        String token = JwtUtils.generateDocumentToken(
                sourceType,
                sourceId,
                userId,
                mode,
                onlyOfficeConfig.getJwtSecret(),
                onlyOfficeConfig.getTokenTtl()
        );
        config.setToken(token);

        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleCallback(String body) {
        try {
            // 1. 解析回调数据
            CallbackData callbackData = objectMapper.readValue(body, CallbackData.class);
            if (callbackData == null) {
                log.error("解析回调数据失败: {}", body);
                return false;
            }

            // 2. 验证token
            if (!StringUtils.hasText(callbackData.getToken()) ||
                    !JwtUtils.validateToken(callbackData.getToken(), onlyOfficeConfig.getJwtSecret())) {
                log.error("回调Token无效");
                return false;
            }

            // 3. 解析token，获取sourceType和sourceId
            var claims = JwtUtils.parseToken(callbackData.getToken(), onlyOfficeConfig.getJwtSecret());
            String sourceType = claims.get("sourceType", String.class);
            Long sourceId = claims.get("sourceId", Long.class);
            Long userId = claims.get("userId", Long.class);

            // 4. 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return false;
            }

            // 5. 处理回调状态
            if (callbackData.getStatus() == DocumentConstant.CALLBACK_STATUS_SAVED) {
                // 5.1 状态为已保存，下载新文件并更新
                log.info("文档已保存: sourceType={}, sourceId={}", sourceType, sourceId);

                // 下载文件
                URL url = new URL(callbackData.getUrl());
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (InputStream inputStream = connection.getInputStream()) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    byte[] fileData = outputStream.toByteArray();

                    // 保存文件到MinIO
                    boolean saveResult = minioService.putObject(
                            minioConfig.getDocumentBucket(),
                            metadata.getStoragePath(),
                            fileData);

                    if (!saveResult) {
                        log.error("保存文件到MinIO失败: sourceType={}, sourceId={}", sourceType, sourceId);
                        return false;
                    }

                    // 更新文档元数据
                    metadata.setFileSize((long) fileData.length);
                    metadata.setUpdateTime(LocalDateTime.now());
                    saveDocumentMetadata(metadata);

                    // 如果版本控制已启用，创建新版本
                    if (onlyOfficeConfig.getDocument().isVersionsEnabled()) {
                        createDocumentVersion(sourceType, sourceId, userId);
                    }
                }

                return true;
            } else if (callbackData.getStatus() == DocumentConstant.CALLBACK_STATUS_FAILED) {
                // 5.2 状态为保存失败
                log.error("文档保存失败: sourceType={}, sourceId={}, error={}, errorDesc={}",
                        sourceType, sourceId, callbackData.getError(), callbackData.getErrorDescription());
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("处理回调异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDocumentVersion(String sourceType, Long sourceId, Long userId) {
        try {
            // 1. 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return null;
            }

            // 2. 获取当前最新版本号
            Integer latestVersion = versionMapper.getLatestVersion(sourceType, sourceId);
            int newVersion = (latestVersion == null) ? 1 : latestVersion + 1;

            // 3. 生成版本文件路径
            String versionStoragePath = sourceType + "/" + sourceId + "/versions/v" + newVersion +
                    getFileExtension(metadata.getFileName());

            // 4. 从主文件复制到版本文件
            byte[] fileData = minioService.getObjectBytes(minioConfig.getDocumentBucket(), metadata.getStoragePath());
            if (fileData == null) {
                log.error("获取文档内容失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return null;
            }

            boolean saveResult = minioService.putObject(
                    minioConfig.getVersionBucket(),
                    versionStoragePath,
                    fileData);

            if (!saveResult) {
                log.error("保存版本文件失败: sourceType={}, sourceId={}, version={}",
                        sourceType, sourceId, newVersion);
                return null;
            }

            // 5. 创建版本记录
            DocumentVersionEntity version = new DocumentVersionEntity();
            version.setSourceType(sourceType);
            version.setSourceId(sourceId);
            version.setVersion(newVersion);
            version.setStoragePath(versionStoragePath);
            version.setFileSize((long) fileData.length);
            version.setUserId(userId);

            versionMapper.insert(version);

            // 6. 检查并清理过多的版本
            if (onlyOfficeConfig.getDocument().getMaxVersions() > 0) {
                cleanOldVersions(sourceType, sourceId, onlyOfficeConfig.getDocument().getMaxVersions());
            }

            return version.getId();
        } catch (Exception e) {
            log.error("创建文档版本异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DocumentVersionVO> getVersionList(String sourceType, Long sourceId) {
        try {
            // 1. 获取版本列表
            List<DocumentVersionEntity> versions = versionMapper.getVersionList(sourceType, sourceId);
            if (versions == null || versions.isEmpty()) {
                return new ArrayList<>();
            }

            // 2. 转换为VO
            List<DocumentVersionVO> versionVOList = new ArrayList<>(versions.size());
            for (DocumentVersionEntity version : versions) {
                DocumentVersionVO vo = new DocumentVersionVO();
                vo.setId(version.getId());
                vo.setVersion(version.getVersion());
                vo.setFileSize(version.getFileSize());
                vo.setUserId(version.getUserId());
                vo.setCreateTime(version.getCreateTime());
                vo.setIsCurrent(false);

                // 生成预览URL
                String previewUrl = minioService.generatePresignedUrl(
                        minioConfig.getVersionBucket(),
                        version.getStoragePath(),
                        30,
                        TimeUnit.MINUTES);
                vo.setPreviewUrl(previewUrl);

                versionVOList.add(vo);
            }

            // 如果有版本，将最新版本标记为当前版本
            if (!versionVOList.isEmpty()) {
                versionVOList.get(0).setIsCurrent(true);
            }

            return versionVOList;
        } catch (Exception e) {
            log.error("获取文档版本列表异常: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public DocumentVersionVO getVersion(Long versionId) {
        try {
            // 1. 获取版本信息
            DocumentVersionEntity version = versionMapper.selectById(versionId);
            if (version == null) {
                return null;
            }

            // 2. 转换为VO
            DocumentVersionVO vo = new DocumentVersionVO();
            vo.setId(version.getId());
            vo.setVersion(version.getVersion());
            vo.setFileSize(version.getFileSize());
            vo.setUserId(version.getUserId());
            vo.setCreateTime(version.getCreateTime());

            // 生成预览URL
            String previewUrl = minioService.generatePresignedUrl(
                    minioConfig.getVersionBucket(),
                    version.getStoragePath(),
                    30,
                    TimeUnit.MINUTES);
            vo.setPreviewUrl(previewUrl);

            return vo;
        } catch (Exception e) {
            log.error("获取文档版本异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public DocumentEditorConfig previewVersion(Long versionId, Long userId, String userName) {
        try {
            // 1. 获取版本信息
            DocumentVersionEntity version = versionMapper.selectById(versionId);
            if (version == null) {
                log.error("版本不存在: versionId={}", versionId);
                return null;
            }

            // 2. 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(version.getSourceType(), version.getSourceId());
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}",
                        version.getSourceType(), version.getSourceId());
                return null;
            }

            // 3. 检查权限
            if (!checkPermission(version.getSourceType(), version.getSourceId(), userId, DocumentConstant.MODE_VIEW)) {
                log.error("用户无权限预览文档版本: versionId={}, userId={}", versionId, userId);
                return null;
            }

            // 4. 生成文档访问URL
            String fileUrl = minioService.generatePresignedUrl(
                    minioConfig.getVersionBucket(),
                    version.getStoragePath(),
                    30,
                    TimeUnit.MINUTES);

            if (!StringUtils.hasText(fileUrl)) {
                log.error("生成文档版本访问URL失败: versionId={}", versionId);
                return null;
            }

            // 5. 创建编辑器配置
            DocumentEditorConfig config = new DocumentEditorConfig();

            // 设置文档类型
            config.setDocumentType(getDocumentType(metadata.getFileName()));

            // 设置文档信息
            DocumentEditorConfig.Document document = new DocumentEditorConfig.Document(
                    metadata.getFileName() + " (版本 " + version.getVersion() + ")",
                    fileUrl,
                    getFileExtension(metadata.getFileName())
            );
            config.setDocument(document);

            // 设置编辑器配置
            DocumentEditorConfig.EditorConfig editorConfig = new DocumentEditorConfig.EditorConfig(
                    DocumentConstant.MODE_VIEW,
                    userId.toString(),
                    userName != null ? userName : "用户" + userId
            );
            config.setEditorConfig(editorConfig);

            // 6. 生成token
            String token = JwtUtils.generateDocumentToken(
                    version.getSourceType(),
                    version.getSourceId(),
                    userId,
                    DocumentConstant.MODE_VIEW,
                    onlyOfficeConfig.getJwtSecret(),
                    onlyOfficeConfig.getTokenTtl()
            );
            config.setToken(token);

            return config;
        } catch (Exception e) {
            log.error("预览文档版本异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public InputStream downloadDocument(String sourceType, Long sourceId) {
        try {
            // 1. 获取文档元数据
            DocumentMetadataEntity metadata = getDocumentMetadata(sourceType, sourceId);
            if (metadata == null) {
                log.error("获取文档元数据失败: sourceType={}, sourceId={}", sourceType, sourceId);
                return null;
            }

            // 2. 获取文档内容
            return minioService.getObject(minioConfig.getDocumentBucket(), metadata.getStoragePath());
        } catch (Exception e) {
            log.error("下载文档异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public InputStream downloadVersion(Long versionId) {
        try {
            // 1. 获取版本信息
            DocumentVersionEntity version = versionMapper.selectById(versionId);
            if (version == null) {
                log.error("版本不存在: versionId={}", versionId);
                return null;
            }
            
            // 2. 获取版本文件内容
            return minioService.getObject(minioConfig.getVersionBucket(), version.getStoragePath());
        } catch (Exception e) {
            log.error("下载文档版本异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean checkPermission(String sourceType, Long sourceId, Long userId, String mode) {
        // 根据不同的sourceType实现不同的权限检查逻辑
        // 此处简单实现，实际项目中需要根据业务需求进行权限控制
        if (DocumentConstant.SOURCE_TYPE_TEMPLATE.equals(sourceType)) {
            // 模板文件：管理员可编辑，其他用户只能查看
            return true; // 此处简化，实际应从用户服务获取用户角色判断
        } else if (DocumentConstant.SOURCE_TYPE_RESUME.equals(sourceType)) {
            // 简历文件：只有所有者可编辑，其他用户不可访问
            return true; // 此处简化，实际应从简历服务获取简历所有者判断
        } else if (DocumentConstant.SOURCE_TYPE_FILE.equals(sourceType)) {
            // 普通文件：只有所有者可编辑，其他用户不可访问
            return true; // 此处简化，实际应从文件服务获取文件所有者判断
        }
        
        return false;
    }
    
    /**
     * 获取文档类型
     *
     * @param fileName 文件名
     * @return 文档类型
     */
    private String getDocumentType(String fileName) {
        if (fileName == null) {
            return DocumentConstant.DOC_TYPE_WORD;
        }
        
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls")) {
            return DocumentConstant.DOC_TYPE_EXCEL;
        } else if (lowerFileName.endsWith(".pptx") || lowerFileName.endsWith(".ppt")) {
            return DocumentConstant.DOC_TYPE_PPTX;
        } else {
            return DocumentConstant.DOC_TYPE_WORD;
        }
    }
    
    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    /**
     * 清理旧版本
     *
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param maxVersions 最大版本数
     */
    private void cleanOldVersions(String sourceType, Long sourceId, int maxVersions) {
        try {
            // 1. 获取所有版本
            List<DocumentVersionEntity> versions = versionMapper.getVersionList(sourceType, sourceId);
            if (versions == null || versions.size() <= maxVersions) {
                return;
            }
            
            // 2. 删除多余的版本
            for (int i = maxVersions; i < versions.size(); i++) {
                DocumentVersionEntity version = versions.get(i);
                
                // 删除版本文件
                minioService.removeObject(minioConfig.getVersionBucket(), version.getStoragePath());
                
                // 删除版本记录
                versionMapper.deleteById(version.getId());
            }
        } catch (Exception e) {
            log.error("清理旧版本异常: {}", e.getMessage(), e);
        }
    }
} 